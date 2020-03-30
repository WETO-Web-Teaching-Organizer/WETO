package fi.uta.cs.weto.model;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.actions.Login;
import fi.uta.cs.weto.db.RightsCluster;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.util.DbTransactionContext;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public abstract class WetoCourseAction extends ActionSupport
{
  public static final String ACCESS_DENIED = "courseAccessDenied";
  public static final String SUBTASK_ACCESS_DENIED = "subtaskAccessDenied";
  public static final String REGISTRATION = "registration";

  private static final int permanentTeacherTabBits;
  private static final int permanentStudentTabBits;
  private static final int rootTabBits;
  private static final int allRightsBits;

  static
  {
    permanentTeacherTabBits = Tab.MAIN.getBit() | Tab.STUDENTS.getBit()
            | Tab.PERMISSIONS.getBit() | Tab.GROUPS.getBit()
            | Tab.TASK_DOCUMENTS.getBit() | Tab.LOG.getBit();
    permanentStudentTabBits = Tab.MAIN.getBit();
    rootTabBits = Tab.LOG.getBit();
    int rights = 0;
    for(Tab t : Tab.values())
    {
      rights |= t.getBit();
    }
    for(Component c : Component.values())
    {
      rights |= c.getBit();
    }
    allRightsBits = rights;
  }

  private Connection masterConnection;
  private Connection courseConnection;
  private DbTransactionContext dbSession;
  private HttpServletRequest request;
  private Navigator navigator;
  private Integer masterUserId;
  private Integer courseUserId;
  private Integer clusterType;
  private boolean committed;

  private Task task;
  private LinkedList<String[]> taskPath;
  private RightsCluster cluster;
  private final int reqOwnerViewBits;
  private final int reqOwnerUpdateBits;
  private final int reqOwnerCreateBits;
  private final int reqOwnerDeleteBits;
  private Integer courseTaskId;
  private boolean isCourseTask;
  private boolean isLeafTask;
  private boolean isHidden;
  private String courseName;
  private String pageTitle;
  private ArrayList<Tab> tabs;
  private boolean hasToolMenu;
  private String courseCssFilename;

  private Integer taskId;
  private Integer tabId;
  private Integer dbId;

  public abstract String action() throws Exception;

  public WetoCourseAction(int reqOwnerViewBits, int reqOwnerUpdateBits,
          int reqOwnerCreateBits, int reqOwnerDeleteBits)
  {
    this.reqOwnerViewBits = reqOwnerViewBits;
    this.reqOwnerUpdateBits = reqOwnerUpdateBits;
    this.reqOwnerCreateBits = reqOwnerCreateBits;
    this.reqOwnerDeleteBits = reqOwnerDeleteBits;
  }

  void doPrepare() throws Exception
  {
    committed = false;
    request = ServletActionContext.getRequest();
    dbSession = DbTransactionContext.getInstance(request);
    masterConnection = dbSession.getConnection("master");
    try
    {
      // Try to retrieve master and course user ids from the navigator.
      navigator = Navigator.getOrCreateMasterInstance(request, masterConnection);
      masterUserId = navigator.getMasterUserId();
      courseUserId = navigator.getCourseUserId();
    }
    catch(FailedLoginException e)
    {
      navigator = null;
      masterUserId = null;
      courseUserId = null;
    }
    Login.checkLogin(this, request, navigator, masterUserId);
    courseConnection = dbSession.getConnection(navigator.getDatabase(dbId));
    // A course action requires task id, tab id and database id. In addition
    // the master user id and navigator must exist.
    if((taskId != null) && (tabId != null) && (dbId != null) && (masterUserId
            != null) && (navigator != null))
    {
      // Retrieve the course information
      Integer sessionCourseTaskId = navigator.getCourseTaskId();
      task = Task.select1ById(courseConnection, taskId);
      courseTaskId = task.getRootTaskId();
      isCourseTask = taskId.equals(courseTaskId);
      isLeafTask = !SubtaskLink.hasSubTasks(courseConnection, taskId);
      // Create task path and incidentally check if the task is hidden
      taskPath = new LinkedList<>();
      Task courseTask = task;
      Integer tmpTaskId = courseTask.getId();
      isHidden = false;
      while(true)
      {
        isHidden = isHidden || courseTask.getIsHidden();
        String[] pathItem = new String[2];
        pathItem[0] = tmpTaskId.toString();
        pathItem[1] = courseTask.getName();
        taskPath.addFirst(pathItem);
        try
        {
          SubtaskLink link = SubtaskLink.select1BySubtaskId(courseConnection,
                  tmpTaskId);
          tmpTaskId = link.getContainerId();
          courseTask = Task.select1ById(courseConnection, tmpTaskId);
        }
        catch(NoSuchItemException e)
        {
          break;
        }
      }
      courseName = courseTask.getName();
      pageTitle = Jsoup.clean(task.getName(), "", Whitelist.none(),
              new Document.OutputSettings().prettyPrint(false));
      courseCssFilename = TaskModel.createCourseCssFilename(dbId, courseTaskId);
      File cssFile = TaskModel.createCourseCssFilePath(courseCssFilename);
      if(!cssFile.exists())
      {
        ArrayList<Tag> cssTags = Tag.selectByTaggedIdAndType(courseConnection,
                taskId, TagType.CSS_STYLE.getValue());
        String cssText = cssTags.isEmpty() ? "" : cssTags.get(0).getText();
        try(ByteArrayInputStream text = new ByteArrayInputStream(cssText
                .getBytes()))
        {
          WetoUtilities.streamToFile(text, cssFile);
        }
      }
      try
      {
        // Check whether the last page visited by the user was in the same
        // course. If not, recreate the navigator.
        if(!dbId.equals(navigator.getDbId()) || !courseTaskId.equals(
                sessionCourseTaskId))
        {
          masterUserId = navigator.getRealMasterUserId();
          // Check if the user is a course member. If not, a
          // NoSuchItemException will be thrown.
          UserIdReplication uidr = UserIdReplication
                  .select1ByMasterDbUserId(courseConnection, masterUserId);
          courseUserId = uidr.getCourseDbUserId();
          try
          {
            navigator = Navigator
                    .createCourseInstance(request, masterConnection,
                            masterUserId, courseConnection, courseUserId,
                            courseTaskId, navigator.getDatabases(), dbId);
          }
          catch(SQLException | InvalidValueException e)
          {
            throw new WetoActionException(getText("accessDenied.systemError"));
          }
        }
        clusterType = navigator.getClusterType();
        cluster = navigator.getCluster();
        // The step below checks whether the user already belongs to the course.
        UserTaskView.select1ByTaskIdAndUserId(courseConnection, courseTaskId,
                courseUserId);
        if(navigator.getNavigationTree() == null)
        {
          navigator.refreshNavigationTree(courseConnection);
        }
        navigator.validateNavigationTree(courseConnection);
        // Check user access rights. This is based on owner rights. More fine
        // grained access should be checked by calling have*Rights -methods.
        if(((reqOwnerViewBits & cluster.getOwnerViewBits()) != reqOwnerViewBits)
                || ((reqOwnerUpdateBits & cluster.getOwnerUpdateBits())
                != reqOwnerUpdateBits)
                || ((reqOwnerCreateBits & cluster.getOwnerCreateBits())
                != reqOwnerCreateBits)
                || ((reqOwnerDeleteBits & cluster.getOwnerDeleteBits())
                != reqOwnerDeleteBits))
        {
          String redirectString;
          if(tabId.equals(Tab.MAIN.getValue()) && "viewTask".equals(
                  ActionContext.getContext().getName()))
          {
            if(taskId.equals(courseTaskId))
            {
              redirectString = WetoMasterAction.ACCESS_DENIED;
            }
            else
            {
              redirectString = SUBTASK_ACCESS_DENIED;
            }
          }
          else
          {
            redirectString = ACCESS_DENIED;
          }
          throw new WetoActionException(getText("general.error.accessDenied"),
                  redirectString);
        }
        int tabBits = task.getComponentBits();
        // If the user is a student, check if the task is currently
        // visible (not hidden and within course time limits).
        if(ClusterType.STUDENTS.getValue().equals(clusterType))
        {
          WetoTimeStamp[] viewPeriod = PermissionModel.getTimeStampLimits(
                  courseConnection, navigator.getUserIP(), courseUserId, taskId,
                  PermissionType.VIEW);
          if(isHidden || (PermissionModel.checkTimeStampLimits(viewPeriod)
                  != PermissionModel.CURRENT))
          {
            if(taskId.equals(courseTaskId))
            {
              throw new WetoActionException(
                      getText("accessDenied.courseNotOpen"),
                      "masterAccessDenied");
            }
            else
            {
              throw new WetoActionException(
                      getText("general.error.accessDenied"),
                      SUBTASK_ACCESS_DENIED);
            }
          }
          tabBits = tabBits | permanentStudentTabBits;
        }
        else
        {
          tabBits = tabBits | permanentTeacherTabBits;
        }
        // Populate task tabs and also verify tab viewing permissions.
        int generalOwnerBits = cluster.getOwnerViewBits();
        tabBits &= generalOwnerBits;
        if(!isCourseTask)
        {
          tabBits &= (~rootTabBits);
        }
        tabs = new ArrayList<>();
        boolean tabVisible = false;
        for(Tab tab : Tab.values())
        {
          if((tab.getBit() & tabBits) != 0)
          {
            if(!task.getIsQuiz() || !ClusterType.STUDENTS.getValue().equals(
                    clusterType) || !tab.equals(Tab.SUBMISSIONS))
            {
              tabs.add(tab);
              if(tab.getValue() == tabId)
              {
                tabVisible = true;
                hasToolMenu = tab.isHasToolMenu();
              }
            }
          }
        }
        if(!tabVisible)
        {
          String redirectString;
          if(tabId.equals(Tab.MAIN.getValue()))
          {
            redirectString = WetoMasterAction.ACCESS_DENIED;
          }
          else
          {
            redirectString = ACCESS_DENIED;
          }
          throw new WetoActionException("", redirectString);
        }
      }
      catch(NoSuchItemException e)
      {
        // A user that is not a course member is forwarded
        // to course registration.
        throw new WetoActionException("", REGISTRATION);
      }
    }
    else
    {
      throw new WetoActionException(getText("accessDenied.systemError"));
    }
  }

  @Override
  public final String execute() throws Exception
  {
    try
    {
      try
      {
        doPrepare();
        String result = action();
        commit();
        return result;
      }
      catch(WetoActionException e)
      {
        String msg = e.getMessage();
        if(msg != null && !msg.isEmpty())
        {
          addActionError(msg);
        }
        return e.getResult();
      }
      catch(Exception e)
      {
        addActionError(e.getMessage());
        return ERROR;
      }
    }
    catch(Exception e)
    {
      addActionError(getText("general.error.system"));
      return ERROR;
    }
    finally
    {
      close();
    }
  }

  final void commit()
  {
    if((dbSession != null) && !navigator.isStudentRole() && !committed)
    {
      dbSession.commitAll();
      committed = true;
    }
  }

  final void close()
  {
    if(dbSession != null && !committed)
    {
      dbSession.cancelAll();
      committed = true;
    }
  }

  public static final int getAllRightsBits()
  {
    return allRightsBits;
  }

  public final boolean haveViewRights(int reqComponentBits, boolean owner,
          boolean general)
  {
    int rights = (owner) ? cluster.getOwnerViewBits() : 0;
    rights |= (general) ? cluster.getGeneralViewBits() : 0;
    return ((rights & reqComponentBits) == reqComponentBits);
  }

  public final boolean haveUpdateRights(int reqComponentBits, boolean owner,
          boolean general)
  {
    int rights = (owner) ? cluster.getOwnerUpdateBits() : 0;
    rights |= (general) ? cluster.getGeneralUpdateBits() : 0;
    return ((rights & reqComponentBits) == reqComponentBits);
  }

  public final boolean haveCreateRights(int reqComponentBits, boolean owner,
          boolean general)
  {
    int rights = (owner) ? cluster.getOwnerCreateBits() : 0;
    rights |= (general) ? cluster.getGeneralCreateBits() : 0;
    return ((rights & reqComponentBits) == reqComponentBits);
  }

  public final boolean haveDeleteRights(int reqComponentBits, boolean owner,
          boolean general)
  {
    int rights = (owner) ? cluster.getOwnerDeleteBits() : 0;
    rights |= (general) ? cluster.getGeneralDeleteBits() : 0;
    return ((rights & reqComponentBits) == reqComponentBits);
  }

  public void refreshNavigationTree()
          throws SQLException, NoSuchItemException, WetoTimeStampException,
                 InvalidValueException
  {
    getNavigator().refreshNavigationTree(courseConnection);
  }

  public final Connection getMasterConnection()
  {
    return masterConnection;
  }

  public final Connection getCourseConnection()
  {
    return courseConnection;
  }

  public DbTransactionContext getDbSession()
  {
    return dbSession;
  }

  public final HttpServletRequest getRequest()
  {
    return request;
  }

  public final Navigator getNavigator()
  {
    return navigator;
  }

  public final Integer getMasterUserId()
  {
    return masterUserId;
  }

  public final Integer getCourseUserId()
  {
    return courseUserId;
  }

  public final Integer getClusterType()
  {
    return clusterType;
  }

  public final Task getTask()
  {
    return task;
  }

  public final LinkedList<String[]> getTaskPath()
  {
    return taskPath;
  }

  public String getCourseName()
  {
    return courseName;
  }

  public String getNavigationTree()
  {
    return navigator.getNavigationTree();
  }

  public final Integer getCourseTaskId()
  {
    return courseTaskId;
  }

  public final Integer getTaskId()
  {
    return taskId;
  }

  public final boolean isCourseTask()
  {
    return isCourseTask;
  }

  public final boolean isLeafTask()
  {
    return isLeafTask;
  }

  public boolean isIsHidden()
  {
    return isHidden;
  }

  public final void setTaskId(Integer taskId)
  {
    this.taskId = taskId;
  }

  public final Integer getTabId()
  {
    return tabId;
  }

  public final void setTabId(Integer tabId)
  {
    this.tabId = tabId;
  }

  public final Integer getDbId()
  {
    return dbId;
  }

  public final void setDbId(Integer dbId)
  {
    this.dbId = dbId;
  }

  public ArrayList<Tab> getTabs()
  {
    return tabs;
  }

  public void setCommitted(boolean committed)
  {
    this.committed = committed;
  }

  public String getPageTitle()
  {
    return pageTitle;
  }

  public boolean isHasToolMenu()
  {
    return hasToolMenu;
  }

  public String getCourseCssFilename()
  {
    return courseCssFilename;
  }

  public boolean isEditTask()
  {
    return false;
  }

  public Integer getMainTabId()
  {
    return Tab.MAIN.getValue();
  }

  public Integer getStudentsTabId()
  {
    return Tab.STUDENTS.getValue();
  }

  public Integer getPermissionsTabId()
  {
    return Tab.PERMISSIONS.getValue();
  }

  public Integer getGradingTabId()
  {
    return Tab.GRADING.getValue();
  }

  public Integer getSubmissionsTabId()
  {
    return Tab.SUBMISSIONS.getValue();
  }

  public Integer getForumTabId()
  {
    return Tab.FORUM.getValue();
  }

  public Integer getGroupsTabId()
  {
    return Tab.GROUPS.getValue();
  }

  public Integer getTaskDocumentsTabId()
  {
    return Tab.TASK_DOCUMENTS.getValue();
  }

  public void validateCourseSubtaskId(Integer subtaskId)
          throws WetoActionException
  {
    if(!navigator.isVisibleCourseSubtask(subtaskId))
    {
      throw new WetoActionException(getText("general.error.accessDenied"),
              SUBTASK_ACCESS_DENIED);
    }
  }

}
