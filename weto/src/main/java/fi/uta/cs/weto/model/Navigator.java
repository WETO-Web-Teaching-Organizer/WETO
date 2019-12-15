package fi.uta.cs.weto.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.actions.Login;
import fi.uta.cs.weto.db.Admin;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.RightsCluster;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;

public class Navigator
{
  private final Integer courseTaskId;
  private final boolean isAdmin;
  private final Integer realMasterUserId;
  private Integer activeMasterUserId;
  private final Integer realCourseUserId;
  private Integer activeCourseUserId;
  private final UserAccount realUser;
  private UserAccount activeUser;
  private final RightsCluster realCluster;
  private final RightsCluster studentCluster;
  private RightsCluster activeCluster;
  private final HashMap<Integer, String> databases;
  private final HashSet<Integer> courseSubtaskIds;
  private final Integer dbId;
  private final boolean isPublicView;
  private String navigationTree;
  private Integer navigationTreeTimeStamp;

  // Navtree JSON variable names
  private final String NAME = "name";
  private final String CHILDREN = "children";
  private final String ID = "id";
  private final String GRADE = "grade";
  private final String ISROOT = "isRoot";
  private final String ISHIDDEN = "isHidden";
  // Navtree JSON defaults
  private final String DEFAULTROOTNAME = "root";

  private Navigator(Connection masterConn, UserAccount masterUser,
          HashMap<Integer, String> databases, Integer masterDbId)
          throws InvalidValueException, SQLException, NoSuchItemException
  {
    Integer masterUserId = masterUser.getId();
    if((masterUser.getLastName() == null) || masterUser.getLastName().isEmpty())
    {
      masterUser.setLastName("?");
    }
    if((masterUser.getFirstName() == null) || masterUser.getFirstName()
            .isEmpty())
    {
      masterUser.setFirstName("?");
    }
    this.realUser = this.activeUser = masterUser;
    this.isAdmin = Admin.userIsAdmin(masterConn, masterUserId);
    this.realMasterUserId = this.activeMasterUserId = masterUserId;
    this.databases = databases;
    this.dbId = masterDbId;
    this.courseTaskId = null;
    this.realCourseUserId = this.activeCourseUserId = null;
    this.realCluster = this.activeCluster = null;
    this.studentCluster = null;
    this.isPublicView = false;
    this.courseSubtaskIds = new HashSet<>();
  }

  private Navigator(Connection masterConn, Integer masterUserId,
          Connection courseConn, Integer courseUserId, Integer courseTaskId,
          HashMap<Integer, String> databases, Integer dbId)
          throws InvalidValueException, SQLException, NoSuchItemException
  {
    UserAccount masterUser = UserAccount.select1ById(masterConn, masterUserId);
    if((masterUser.getLastName() == null) || masterUser.getLastName().isEmpty())
    {
      masterUser.setLastName("?");
    }
    if((masterUser.getFirstName() == null) || masterUser.getFirstName()
            .isEmpty())
    {
      masterUser.setFirstName("?");
    }
    this.realUser = this.activeUser = masterUser;
    this.isAdmin = Admin.userIsAdmin(masterConn, masterUserId);
    this.courseTaskId = courseTaskId;
    this.realMasterUserId = this.activeMasterUserId = masterUserId;
    this.realCourseUserId = this.activeCourseUserId = courseUserId;
    UserTaskView courseMember = UserTaskView
            .select1ByTaskIdAndUserId(courseConn, courseTaskId, courseUserId);
    this.realCluster = this.activeCluster = RightsCluster
            .select1ByTaskIdAndType(courseConn, courseTaskId, courseMember
                    .getClusterType());
    this.studentCluster = RightsCluster.select1ByTaskIdAndType(courseConn,
            courseTaskId, ClusterType.STUDENTS.getValue());
    this.databases = databases;
    this.dbId = dbId;
    this.isPublicView = false;
    this.courseSubtaskIds = new HashSet<>();
  }

  private Navigator(Navigator oldNavigator, Integer courseTaskId,
          HashMap<Integer, String> databases, Integer dbId)
          throws InvalidValueException, SQLException, NoSuchItemException
  {
    UserAccount masterUser = new UserAccount();
    masterUser.setFirstName((oldNavigator != null) ? oldNavigator.getUser()
            .getFirstName() : "Guest");
    masterUser.setLastName((oldNavigator != null) ? oldNavigator.getUser()
            .getLastName() : "Viewer");
    this.realUser = this.activeUser = masterUser;
    this.isAdmin = false;
    this.courseTaskId = courseTaskId;
    this.realMasterUserId = this.activeMasterUserId = (oldNavigator != null)
                                                              ? oldNavigator.realMasterUserId
                                                      : null;
    this.realCourseUserId = this.activeCourseUserId = null;
    this.realCluster = this.activeCluster = null;
    this.studentCluster = null;
    this.databases = databases;
    this.dbId = dbId;
    this.isPublicView = true;
    this.courseSubtaskIds = new HashSet<>();
  }

  /**
   * Fetches the Navigator instance from the session associated with the given
   * HttpServletRequest. If the instance does not exist, new one is created.
   *
   * @param request HTTP request from the client
   * @return Navigator instance
   */
  public static Navigator getInstance(HttpServletRequest request) throws
          FailedLoginException
  {
    Navigator instance = (Navigator) request.getSession().getAttribute(
            "navigator");
    if(instance == null)
    {
      throw new FailedLoginException();
    }
    return instance;
  }

  public static Navigator getOrCreateMasterInstance(HttpServletRequest request,
          Connection masterConn)
          throws FailedLoginException, WetoActionException,
                 InvalidValueException, SQLException, ObjectNotValidException,
                 NoSuchItemException, WetoTimeStampException
  {
    Navigator instance = (Navigator) request.getSession().getAttribute(
            "navigator");
    if(instance == null)
    {
      HashMap<Integer, String> databases = DatabasePool.selectNamesWithMaster(
              masterConn);
      UserAccount masterUser = Login.retrieveShibbolethAttributes(request,
              masterConn, databases);
      instance = new Navigator(masterConn, masterUser, databases,
              DatabasePool.MASTER_ID);
      request.getSession().setAttribute("navigator", instance);
    }
    return instance;
  }

  static Navigator createMasterInstance(HttpServletRequest request,
          Connection masterConn, Integer masterUserId,
          HashMap<Integer, String> databases, Integer masterDbId)
          throws SQLException, NoSuchItemException, InvalidValueException,
                 WetoTimeStampException
  {
    UserAccount masterUser = UserAccount.select1ById(masterConn, masterUserId);
    Navigator instance = new Navigator(masterConn, masterUser, databases,
            masterDbId);
    request.getSession().setAttribute("navigator", instance);
    return instance;
  }

  static Navigator createCourseInstance(HttpServletRequest request,
          Connection masterConn, Integer masterUserId, Connection courseConn,
          Integer courseUserId, Integer courseTaskId,
          HashMap<Integer, String> databases, Integer dbId)
          throws SQLException, NoSuchItemException, InvalidValueException,
                 WetoTimeStampException
  {
    Navigator instance = new Navigator(masterConn, masterUserId, courseConn,
            courseUserId, courseTaskId, databases, dbId);
    instance.buildNavigationTree(courseConn, instance.DEFAULTROOTNAME);
    request.getSession().setAttribute("navigator", instance);
    return instance;
  }

  public static Navigator createPublicInstance(HttpServletRequest request,
          Connection courseConn, Integer courseTaskId,
          HashMap<Integer, String> databases, Integer dbId,
          Navigator oldNavigator)
          throws SQLException, NoSuchItemException, InvalidValueException,
                 WetoTimeStampException
  {
    Navigator instance = new Navigator(oldNavigator, courseTaskId, databases,
            dbId);
    instance.buildNavigationTree(courseConn, instance.DEFAULTROOTNAME);
    request.getSession().setAttribute("navigator", instance);
    return instance;
  }

  public void refreshNavigationTree(Connection courseConn)
          throws SQLException, NoSuchItemException, WetoTimeStampException,
                 InvalidValueException
  {
    buildNavigationTree(courseConn, DEFAULTROOTNAME);
    navigationTreeTimeStamp = new WetoTimeStamp().getTimeStamp();
  }

  public void validateNavigationTree(Connection courseConn)
          throws SQLException, NoSuchItemException, WetoTimeStampException,
                 InvalidValueException, ObjectNotValidException
  {
    Integer timeStamp = WetoTimeStamp.STAMP_MIN;
    try
    {
      Property lastUpdate = PropertyModel.getNavigationTreeUpdate(courseConn,
              courseTaskId);
      timeStamp = Integer.parseInt(lastUpdate.getValue());
    }
    catch(NoSuchItemException e)
    {
    }
    if((navigationTreeTimeStamp == null)
            || (navigationTreeTimeStamp < timeStamp))
    {
      refreshNavigationTree(courseConn);
    }
  }

  private void buildNavigationTree(Connection courseConn, String rootName)
          throws SQLException, NoSuchItemException, WetoTimeStampException,
                 InvalidValueException
  {
    courseSubtaskIds.add(courseTaskId);
    ArrayList<SubtaskView> courseTaskList = SubtaskView.selectByCourseTaskId(
            courseConn, courseTaskId);
    HashMap<Integer, ArrayList<SubtaskView>> subtaskListMap = new HashMap<>();
    for(SubtaskView subtask : courseTaskList)
    {
      Integer fatherId = subtask.getContainerId();
      Integer subtaskId = subtask.getId();
      ArrayList<SubtaskView> subtasks = subtaskListMap.get(fatherId);
      if(subtasks == null)
      {
        subtasks = new ArrayList<>();
        subtaskListMap.put(fatherId, subtasks);
      }
      if((fatherId != null) && !subtaskId.equals(fatherId))
      {
        subtasks.add(subtask);
      }
      courseSubtaskIds.add(subtaskId);
    }
    ArrayList<Grade> validAggregateGrades = Grade
            .selectValidAggregateByCourseTaskIdAndReceiverId(courseConn,
                    courseTaskId, activeCourseUserId);
    HashMap<Integer, Float> validGrades = new HashMap<>();
    for(Grade grade : validAggregateGrades)
    {
      validGrades.put(grade.getTaskId(), grade.getMark());
    }
    HashSet<Integer> viewableSubtaskSet = isTeacher() ? null : PermissionModel
            .getViewableCourseSubtasks(courseConn, courseTaskId, false,
                    subtaskListMap, activeCourseUserId);
    // Create root
    JsonObject root = new JsonObject();
    rootName = WetoUtilities.escapeHtml(rootName);
    root.addProperty(NAME, rootName);
    root.addProperty(ID, courseTaskId);
    Float mark = validGrades.get(courseTaskId);
    if(mark != null)
    {
      root.addProperty(GRADE, mark);
    }
    root.addProperty(ISROOT, true);
    root.addProperty(ISHIDDEN, Task.select1ById(courseConn, courseTaskId)
            .getIsHidden());

    // Recurse
    JsonArray descendants = getNavigationDescendants(courseConn, subtaskListMap,
            viewableSubtaskSet, validGrades, root, courseTaskId, isTeacher());
    root.add(CHILDREN, descendants);

    // Save result
    navigationTree = root.toString();
  }

  private JsonArray getNavigationDescendants(Connection courseConn,
          final HashMap<Integer, ArrayList<SubtaskView>> subtaskListMap,
          final HashSet<Integer> viewableSubtaskSet,
          final HashMap<Integer, Float> validGrades, JsonObject parent,
          final int taskId, final boolean isTeacher)
          throws SQLException, WetoTimeStampException
  {
    ArrayList<SubtaskView> subtasks = subtaskListMap.get(taskId);
    JsonArray childrenCollector = new JsonArray();
    // At the bottom of the recursion
    if((subtasks == null) || subtasks.isEmpty())
    {
      parent.addProperty(CHILDREN, "");
    }
    else
    {
      // Add children of current parent
      for(SubtaskView subtask : subtasks)
      {
        int subtaskId = subtask.getId();
        boolean isHidden = subtask.getIsHidden();
        // Add only visible tasks to the navigation tree.
        if(isTeacher || (!isHidden && viewableSubtaskSet.contains(
                subtaskId) && (!isPublicView || subtask.getIsPublic())))
        {
          // Build representation of child: id, name etc
          JsonObject child = new JsonObject();
          child.addProperty(NAME, WetoUtilities.escapeHtml(subtask.getName()));
          child.addProperty(ID, subtaskId);
          Float mark = validGrades.get(subtaskId);
          if(mark != null)
          {
            child.addProperty(GRADE, mark);
          }
          child.addProperty(ISROOT, false);
          child.addProperty(ISHIDDEN, isHidden);

          // Recurse through the next level
          JsonArray grandChildren = getNavigationDescendants(courseConn,
                  subtaskListMap, viewableSubtaskSet, validGrades, child,
                  subtaskId, isTeacher);
          child.add(CHILDREN, grandChildren);
          childrenCollector.add(child);
        }
      }
    }
    return childrenCollector;
  }

  /**
   * Removes navigator from the session.
   *
   * @param request HTTP request
   */
  static void reset(HttpServletRequest request)
  {
    request.getSession().setAttribute("navigator", null);
  }

  public boolean switchRole(HttpServletRequest request)
  {
    if(ClusterType.TEACHERS.getValue().equals(realCluster.getType()))
    {
      if(ClusterType.TEACHERS.getValue().equals(activeCluster.getType()))
      {
        activeCluster = studentCluster;
      }
      else
      {
        activeMasterUserId = realMasterUserId;
        activeCourseUserId = realCourseUserId;
        activeUser = realUser;
        activeCluster = realCluster;
      }
      navigationTree = null;
      return true;
    }
    return false;
  }

  public boolean takeStudentRole(HttpServletRequest request,
          Integer masterStudentId, Integer courseStudentId,
          UserAccount studentUser) throws InvalidValueException
  {
    if(ClusterType.TEACHERS.getValue().equals(realCluster.getType()))
    {
      activeMasterUserId = masterStudentId;
      activeCourseUserId = courseStudentId;
      if(studentUser.getLastName() == null || studentUser.getLastName()
              .isEmpty())
      {
        studentUser.setLastName("?");
      }
      if(studentUser.getFirstName() == null || studentUser.getFirstName()
              .isEmpty())
      {
        studentUser.setFirstName("?");
      }
      activeUser = studentUser;
      activeCluster = studentCluster;
      navigationTree = null;
      return true;
    }
    return false;
  }

  public Integer getMasterUserId()
  {
    return activeMasterUserId;
  }

  public Integer getRealMasterUserId()
  {
    return realMasterUserId;
  }

  public Integer getCourseUserId()
  {
    return activeCourseUserId;
  }

  public Integer getRealCourseUserId()
  {
    return realCourseUserId;
  }

  public Integer getCourseTaskId()
  {
    return courseTaskId;
  }

  public UserAccount getUser()
  {
    return activeUser;
  }

  public UserAccount getRealUser()
  {
    return realUser;
  }

  public boolean isAdmin()
  {
    return isAdmin;
  }

  public boolean isTeacher()
  {
    return (activeCluster != null) && ClusterType.TEACHERS.getValue().equals(
            activeCluster.getType());
  }

  public boolean isAssistant()
  {
    return (activeCluster != null) && ClusterType.ASSISTANTS.getValue().equals(
            activeCluster.getType());
  }

  public boolean isStudent()
  {
    return ClusterType.STUDENTS.getValue().equals(activeCluster.getType());
  }

  public boolean isReallyTeacher()
  {
    return ClusterType.TEACHERS.getValue().equals(realCluster.getType());
  }

  public boolean isStudentRole()
  {
    return (realUser != activeUser);
  }

  public HashMap<Integer, String> getDatabases()
  {
    return databases;
  }

  public String getDatabase(int dbId)
  {
    return databases.get(dbId);
  }

  public Integer getDbId()
  {
    return dbId;
  }

  public boolean isIsPublicView()
  {
    return isPublicView;
  }

  public RightsCluster getCluster()
  {
    return activeCluster;
  }

  public Integer getClusterType()
  {
    return (activeCluster != null) ? activeCluster.getType() : null;
  }

  public Integer getRealClusterType()
  {
    return realCluster.getType();
  }

  public String getNavigationTree()
  {
    return navigationTree;
  }

  public boolean isVisibleCourseSubtask(Integer subtaskId)
  {
    return courseSubtaskIds.contains(subtaskId);
  }

}
