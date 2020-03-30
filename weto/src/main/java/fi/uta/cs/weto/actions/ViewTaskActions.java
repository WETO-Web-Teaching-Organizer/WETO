package fi.uta.cs.weto.actions;

import static com.opensymphony.xwork2.Action.ERROR;
import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.ContentElementBean;
import fi.uta.cs.weto.model.ContentElementType;
import fi.uta.cs.weto.model.HtmlBean;
import fi.uta.cs.weto.model.Navigator;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.PropertyModel;
import fi.uta.cs.weto.model.QuestionBean;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.TaskModel;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.DbTransactionContext;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class ViewTaskActions
{
  public static class SelectView extends ActionSupport
  {
    private Integer taskId;
    private Integer tabId;
    private Integer dbId;
    private boolean publicView;

    private Integer answererId;

    @Override
    public final String execute() throws Exception
    {
      if((taskId == null) || (dbId == null))
      {
        addActionError(getText("general.error.invalidAddress"));
        return ERROR;
      }
      Tab tab = (tabId == null) ? null : Tab.getTab(tabId);
      if((tab == null) || (tab == Tab.MAIN))
      {
        DbTransactionContext dbSession = null;
        try
        {
          final HttpServletRequest request = ServletActionContext.getRequest();
          dbSession = DbTransactionContext.getInstance(request);
          final Connection masterConnection = dbSession.getConnection("master");
          HashMap<Integer, String> databases = null;
          try
          {
            // Try to retrieve an existing navigator.
            Navigator navigator = Navigator.getOrCreateMasterInstance(request,
                    masterConnection);
            databases = navigator.getDatabases();
            publicView = publicView || navigator.isIsPublicView();
          }
          catch(FailedLoginException e)
          {
            publicView = true;
            databases = DatabasePool.selectNamesWithMaster(masterConnection);
          }
          if(publicView)
          {
            // Check if the action concerns a (potentially) public task.
            try
            {
              Integer taskId = Integer.valueOf(request.getParameterMap().get(
                      "taskId")[0]);
              Integer dbId = Integer.valueOf(
                      request.getParameterMap().get("dbId")[0]);
              Connection courseConn = dbSession.getConnection(databases
                      .get(dbId));
              Task task = Task.select1ById(courseConn, taskId);
              if(task.getIsPublic())
              {
                return "PUBLICMAIN";
              }
            }
            catch(Exception e)
            {
            }
          }
          return "COURSEMAIN";
        }
        catch(Exception e)
        {
          addActionError(e.getMessage());
          return ERROR;
        }
        finally
        {
          if(dbSession != null)
          {
            if(publicView)
            {
              dbSession.cancelAll();
            }
            else
            {
              dbSession.commitAll();
            }
          }
        }
      }
      else
      {
        return tab.name();
      }
    }

    public Integer getTaskId()
    {
      return taskId;
    }

    public void setTaskId(Integer taskId)
    {
      this.taskId = taskId;
    }

    public Integer getTabId()
    {
      return tabId;
    }

    public void setTabId(Integer tabId)
    {
      this.tabId = tabId;
    }

    public Integer getDbId()
    {
      return dbId;
    }

    public void setDbId(Integer dbId)
    {
      this.dbId = dbId;
    }

    public void setPublicView(boolean publicView)
    {
      this.publicView = publicView;
    }

    public void setAnswererId(Integer answererId)
    {
      this.answererId = answererId;
    }

    public Integer getAnswererId()
    {
      return answererId;
    }

  }

  private final static int loggedTabBits;

  static
  {
    loggedTabBits = Tab.MAIN.getBit() | Tab.GRADING.getBit() | Tab.FORUM
            .getBit();
  }

  public static class CourseView extends WetoCourseAction
  {
    private final List<ContentElementBean> elements = new ArrayList<>();
    private final ArrayList<SubtaskView> subtasks = new ArrayList<>();
    private boolean subtasksQuizzes;
    private final List<String> studentAnswers = new ArrayList<>();
    private String[] submissionPeriod;
    private boolean quizOpen;
    private boolean pendingStudents;
    private Integer answererId;

    public CourseView()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      String result = "MAIN";
      ArrayList<SubtaskView> allSubtasks = SubtaskView.selectByContainerId(
              conn,
              taskId);
      subtasksQuizzes = false;
      final boolean isTeacher = getNavigator().isTeacher();
      Task task = getTask();
      final boolean isQuiz = task.getIsQuiz();
      final String userIP = getNavigator().getUserIP();
      if(answererId == null)
      {
        answererId = userId;
      }
      else
      {
        // An answererId was specified. Verify that the current user has the
        // right to view the quiz answers of the user with id answererId.
        if(!haveViewRights(Tab.SUBMISSIONS.getBit(), userId.equals(answererId),
                true))
        {
          throw new WetoActionException(getText("general.error.accessDenied"));
        }
        result = result.concat("BODY");
      }
      for(SubtaskView subtask : allSubtasks)
      {
        Integer subtaskId = subtask.getId();
        if(isTeacher || (!subtask.getIsHidden() && PermissionModel
                .viewPermissionActive(conn, userIP, subtaskId, userId, false)))
        {
          subtasks.add(subtask);
          if(isQuiz && subtask.getIsQuiz())
          {
            subtasksQuizzes = true;
            QuizModel quiz = new QuizModel(conn, subtaskId, null);
            quiz.readQuestions();
            QuestionBean q;
            ArrayList<Tag> answers = Tag.selectByTaggedIdAndAuthorIdAndType(
                    conn, subtaskId, answererId, TagType.QUIZ_ANSWER
                            .getValue());
            if(!answers.isEmpty())
            {
              Tag answer = answers.get(0);
              q = quiz.selectQuestion(answer.getRank());
              QuizModel.populateAnswers(q, answer);
              QuizModel.populateResults(conn, q, answer, this);
            }
            else
            {
              q = quiz.selectRandomQuestion();
              // The question selection is saved only for students who are
              // viewing the quiz with their own user id.
              if(!isTeacher && answererId.equals(userId))
              {
                Tag answer = new Tag();
                answer.setAuthorId(userId);
                answer.setTaggedId(subtaskId);
                answer.setType(TagType.QUIZ_ANSWER.getValue());
                answer.setRank(q.getQuestionId());
                answer.insert(conn);
              }
            }
            elements.add(q);
          }
        }
      }
      if(isQuiz)
      {
        if(!subtasksQuizzes)
        {
          QuizModel quiz = new QuizModel(conn, taskId, null);
          quiz.readQuestions();
          for(QuestionBean q : quiz.getQuestions())
          {
            try
            {
              Tag answer = Tag
                      .select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
                              taskId, q.getQuestionId(), answererId,
                              TagType.QUIZ_ANSWER
                                      .getValue());
              QuizModel.populateAnswers(q, answer);
              QuizModel.populateResults(conn, q, answer, this);

            }
            catch(NoSuchItemException e)
            {
            }
            elements.add(q);
          }
        }
        WetoTimeStamp[] timeLimits = PermissionModel.getTimeStampLimits(conn,
                userIP, userId, taskId, PermissionType.SUBMISSION, isTeacher);
        submissionPeriod = WetoTimeStamp.limitsToStrings(timeLimits);
        quizOpen = (PermissionModel.checkTimeStampLimits(timeLimits)
                == PermissionModel.CURRENT) && answererId.equals(userId);
      }
      else
      {
        String html = task.getText();
        if(((html == null) || html.isEmpty()))
        {
          if(subtasks.isEmpty())
          {
            html = getText("general.header.emptyPage");
          }
          else
          {
            html = "";
          }
        }
        else if(!task.getNoInline())
        {
          Matcher m = TaskModel.inlineElementPattern.matcher(html);
          boolean hasInline = false;
          int i = 0;
          while(m.find())
          {
            hasInline = true;
            String precedingHtml = html.substring(i, m.start()).trim();
            if(!precedingHtml.isEmpty())
            {
              elements.add(new HtmlBean(ContentElementType.HTML.getValue(),
                      precedingHtml));
            }
            i = m.end();
            Integer elRefId = Integer.parseInt(m.group(3));
            QuizModel quiz = new QuizModel(conn, taskId, null);
            quiz.readQuestion(elRefId);
            for(QuestionBean q : quiz.getQuestions())
            {
              try
              {
                Tag answer = Tag
                        .select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
                                taskId,
                                q.getQuestionId(), answererId,
                                TagType.QUIZ_ANSWER.getValue());
                QuizModel.populateAnswers(q, answer);
                QuizModel.populateResults(conn, q, answer, this);
              }
              catch(NoSuchItemException e)
              {
              }
              elements.add(q);
            }
          }
          if(i < html.length())
          {
            String endHtml = html.substring(i).trim();
            if(!endHtml.isEmpty())
            {
              elements.add(new HtmlBean(ContentElementType.HTML.getValue(),
                      endHtml));
            }
          }
          if(!hasInline)
          {
            task.setNoInline(true);
            task.update(conn, false);
          }
          else
          {
            WetoTimeStamp[] timeLimits = PermissionModel
                    .getTimeStampLimits(conn, userIP, userId, taskId,
                            PermissionType.SUBMISSION, isTeacher);
            submissionPeriod = WetoTimeStamp.limitsToStrings(timeLimits);
            quizOpen = (PermissionModel.checkTimeStampLimits(timeLimits)
                    == PermissionModel.CURRENT) && answererId.equals(userId);
          }
        }
        else
        {
          elements.add(new HtmlBean(ContentElementType.HTML.getValue(), html));
        }
      }
      if(isTeacher)
      {
        ArrayList<Property> pending = PropertyModel.getPendingStudents(conn,
                getNavigator().getCourseTaskId());
        pendingStudents = !pending.isEmpty();
      }
      // Add the page view event to the log, if this tab is under logging.
      if(((Tab.MAIN.getBit() & loggedTabBits) != 0) && !getNavigator()
              .isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, Tab.MAIN.getViewEvent()
                .getValue(), null, null, getRequest().getRemoteAddr()).insert(
                conn);
      }
      return result;
    }

    public List<ContentElementBean> getElements()
    {
      return elements;
    }

    public ArrayList<SubtaskView> getSubtasks()
    {
      return subtasks;
    }

    public boolean isSubtasksQuizzes()
    {
      return subtasksQuizzes;
    }

    public List<String> getStudentAnswers()
    {
      return studentAnswers;
    }

    public String[] getSubmissionPeriod()
    {
      return submissionPeriod;
    }

    public boolean isQuizOpen()
    {
      return quizOpen;
    }

    public boolean isPendingStudents()
    {
      return pendingStudents;
    }

    public void setAnswererId(Integer answererId)
    {
      this.answererId = answererId;
    }

  }

  public static class PublicView extends ActionSupport
  {
    public static final String ACCESS_DENIED = "courseAccessDenied";
    public static final String SUBTASK_ACCESS_DENIED = "subtaskAccessDenied";

    private DbTransactionContext dbSession;
    private Connection courseConnection;
    private Navigator navigator;

    private Task task;
    private LinkedList<String[]> taskPath;
    private Integer courseTaskId;
    private boolean isCourseTask;
    private boolean isLeafTask;
    private String courseName;
    private String pageTitle;
    private ArrayList<Tab> tabs;
    private final boolean hasToolMenu = false;
    private String courseCssFilename;

    private Integer taskId;
    private Integer tabId;
    private Integer dbId;

    private final List<ContentElementBean> elements = new ArrayList<>();
    private final ArrayList<SubtaskView> subtasks = new ArrayList<>();
    private boolean subtasksQuizzes;
    private final List<String> studentAnswers = new ArrayList<>();
    private String[] submissionPeriod;
    private final boolean quizOpen = false;

    private String action() throws Exception
    {
      final Connection conn = courseConnection;
      ArrayList<SubtaskView> allSubtasks = SubtaskView.selectByContainerId(conn,
              taskId);
      subtasksQuizzes = false;
      final boolean isQuiz = task.getIsQuiz();
      final String userIP = getNavigator().getUserIP();
      for(SubtaskView subtask : allSubtasks)
      {
        Integer subtaskId = subtask.getId();
        if(subtask.getIsPublic() && PermissionModel.viewPermissionActive(conn,
                userIP, subtaskId, null, false))
        {
          subtasks.add(subtask);
          if(isQuiz && subtask.getIsQuiz())
          {
            subtasksQuizzes = true;
            QuizModel quiz = new QuizModel(conn, subtaskId, null);
            quiz.readQuestions();
            elements.add(quiz.selectRandomQuestion());
          }
        }
      }
      if(isQuiz)
      {
        if(!subtasksQuizzes)
        {
          QuizModel quiz = new QuizModel(conn, taskId, null);
          quiz.readQuestions();
          for(QuestionBean q : quiz.getQuestions())
          {
            elements.add(q);
          }
        }
        WetoTimeStamp[] timeLimits = PermissionModel.getTimeStampLimits(conn,
                userIP, null, taskId, PermissionType.SUBMISSION, false);
        submissionPeriod = WetoTimeStamp.limitsToStrings(timeLimits);
      }
      else
      {
        String html = task.getText();
        if(((html == null) || html.isEmpty()))
        {
          if(subtasks.isEmpty())
          {
            html = getText("general.header.emptyPage");
          }
          else
          {
            html = "";
          }
        }
        else if(!task.getNoInline())
        {
          Matcher m = TaskModel.inlineElementPattern.matcher(html);
          boolean hasInline = false;
          int i = 0;
          while(m.find())
          {
            hasInline = true;
            String precedingHtml = html.substring(i, m.start()).trim();
            if(!precedingHtml.isEmpty())
            {
              elements.add(new HtmlBean(ContentElementType.HTML.getValue(),
                      precedingHtml));
            }
            i = m.end();
            Integer elRefId = Integer.parseInt(m.group(3));
            QuizModel quiz = new QuizModel(conn, taskId, null);
            quiz.readQuestion(elRefId);
            for(QuestionBean q : quiz.getQuestions())
            {
              elements.add(q);
            }
          }
          if(i < html.length())
          {
            String endHtml = html.substring(i).trim();
            if(!endHtml.isEmpty())
            {
              elements.add(new HtmlBean(ContentElementType.HTML.getValue(),
                      endHtml));
            }
          }
          if(!hasInline)
          {
            task.setNoInline(true);
            task.update(conn, false);
          }
          else
          {
            WetoTimeStamp[] timeLimits = PermissionModel
                    .getTimeStampLimits(conn, userIP, null, taskId,
                            PermissionType.SUBMISSION, false);
            submissionPeriod = WetoTimeStamp.limitsToStrings(timeLimits);
          }
        }
        else
        {
          elements.add(new HtmlBean(ContentElementType.HTML.getValue(), html));
        }
      }
      return Tab.MAIN.name();
    }

    private void doPrepare() throws Exception
    {
      final HttpServletRequest request = ServletActionContext.getRequest();
      dbSession = DbTransactionContext.getInstance(request);
      final Connection masterConnection = dbSession.getConnection("master");
      HashMap<Integer, String> databases = null;
      try
      {
        // Try to retrieve a course database id from the navigator.
        navigator = Navigator.getOrCreateMasterInstance(request,
                masterConnection);
        databases = navigator.getDatabases();
      }
      catch(NoSuchElementException e)
      {
        navigator = null;
        databases = DatabasePool.selectNamesWithMaster(masterConnection);
      }
      courseConnection = dbSession.getConnection(databases.get(dbId));
      task = Task.select1ById(courseConnection, taskId);
      courseTaskId = task.getRootTaskId();

      // A new navigator must be created either if it does not exist, the last
      // last page visited by the user belongs to a different course, or the
      // navigator is non-public.
      if((navigator == null) || !dbId.equals(navigator.getDbId())
              || !courseTaskId.equals(navigator.getCourseTaskId()) || !navigator
              .isIsPublicView())
      {
        navigator = Navigator.createPublicInstance(request, courseConnection,
                courseTaskId, databases, dbId, navigator);
      }
      // Create the course navigation tree, if necessary.
      if(navigator.getNavigationTree() == null)
      {
        navigator.refreshNavigationTree(courseConnection);
      }
      navigator.validateNavigationTree(courseConnection);
      // A course action requires task id, tab id and database id. Also a
      // navigator must now exist.
      if((taskId != null) && (tabId != null) && (dbId != null) && (navigator
              != null))
      {
        // Retrieve the course information
        isCourseTask = taskId.equals(courseTaskId);
        isLeafTask = !SubtaskLink.hasSubTasks(courseConnection, taskId);
        // Create task path and incidentally check if the task is public
        taskPath = new LinkedList<>();
        Task courseTask = task;
        Integer tmpTaskId = courseTask.getId();
        boolean isHidden = false;
        while(true)
        {
          isHidden = isHidden || courseTask.getIsHidden() || !courseTask
                  .getIsPublic();
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
        WetoTimeStamp[] viewPeriod = PermissionModel.getTimeStampLimits(
                courseConnection, navigator.getUserIP(), null, taskId,
                PermissionType.VIEW);
        if(isHidden || (PermissionModel.checkTimeStampLimits(viewPeriod)
                != PermissionModel.CURRENT))
        {
          if(taskId.equals(courseTaskId))
          {
            throw new WetoActionException(getText("accessDenied.courseNotOpen"),
                    "masterAccessDenied");
          }
          else
          {
            throw new WetoActionException(getText("general.error.accessDenied"),
                    SUBTASK_ACCESS_DENIED);
          }
        }
        courseName = courseTask.getName();
        pageTitle = Jsoup.clean(task.getName(), "", Whitelist.none(),
                new Document.OutputSettings().prettyPrint(false));
        courseCssFilename = TaskModel
                .createCourseCssFilename(dbId, courseTaskId);
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
        tabs = new ArrayList<>();
        tabs.add(Tab.MAIN);
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
        if(dbSession != null)
        {
          dbSession.cancelAll();
        }
      }
    }

    public final Navigator getNavigator()
    {
      return navigator;
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

    public List<ContentElementBean> getElements()
    {
      return elements;
    }

    public ArrayList<SubtaskView> getSubtasks()
    {
      return subtasks;
    }

    public boolean isSubtasksQuizzes()
    {
      return subtasksQuizzes;
    }

    public List<String> getStudentAnswers()
    {
      return studentAnswers;
    }

    public String[] getSubmissionPeriod()
    {
      return submissionPeriod;
    }

    public boolean isQuizOpen()
    {
      return quizOpen;
    }

    public boolean isPendingStudents()
    {
      return false;
    }

    public void setAnswererId(Integer answererId)
    {
    }

  }
}
