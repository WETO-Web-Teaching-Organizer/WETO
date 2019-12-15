package fi.uta.cs.weto.actions.main;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.TaskModel;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

public class TaskActions
{
  public static class PrepareUpdate extends SettingsBean
  {
    private ArrayList<SubtaskView> subtasks;
    private QuizModel quiz;

    public PrepareUpdate()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      // Retrieve task and populate input data for editing.
      Task task = getTask();
      setTaskName(task.getName());
      setTaskText(task.getText());
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      subtasks = SubtaskView.selectByContainerId(conn, taskId);
      quiz = new QuizModel(conn, taskId, null);
      quiz.readQuestions();
      return INPUT;
    }

    @Override
    public boolean isEditTask()
    {
      return true;
    }

    public ArrayList<SubtaskView> getSubtasks()
    {
      return subtasks;
    }

    public QuizModel getQuiz()
    {
      return quiz;
    }

  }

  public static class Update extends SettingsBean
  {
    public Update()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      // Retrieve task from the database, populate it with
      // the values entered into form and update it.
      Task task = getTask();
      String html = getTaskText();
      boolean hasInline = false;
      int i = 0;
      ArrayList<String> badElements = new ArrayList<>();
      StringBuilder sb = new StringBuilder();
      Matcher m = TaskModel.inlineElementPattern.matcher(html);
      while(m.find())
      {
        Integer inlineTaskId = Integer.parseInt(m.group(2));
        if(taskId.equals(inlineTaskId))
        {
          hasInline = true;
          sb.append(html.substring(i, m.end()));
        }
        else
        {
          sb.append(html.substring(i, m.start()));
          badElements.add(m.group());
        }
        i = m.end();
      }
      if(i < html.length())
      {
        sb.append(html.substring(i));
      }
      if(!hasInline)
      {
        task.setNoInline(true);
        task.update(conn, false);
      }
      task.setText(sb.toString());
      String taskName = getTaskName();
      if((taskName == null) || taskName.isEmpty())
      {
        taskName = "-";
      }
      boolean sameName = task.getName().equals(taskName);
      task.setName(taskName);
      task.setNoInline(!hasInline);
      task.update(conn);
      if(!sameName)
      {
        refreshNavigationTree();
      }
      // If we are updating course task, replicate changes to master db
      try
      {
        Connection masterConn = getMasterConnection();
        CourseImplementation ci = CourseImplementation
                .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                        taskId);
        Task masterTask = Task.select1ById(masterConn, ci.getMasterTaskId());
        masterTask.setText(getTaskText());
        masterTask.setName(taskName);
        masterTask.setNoInline(false);
        masterTask.update(masterConn);
      }
      catch(NoSuchItemException e)
      {
        // No replication required: this was not a course task.
      }
      addActionMessage(getText("editTask.message.updateSuccess"));
      for(String badElement : badElements)
      {
        addActionError(getText("editTask.error.badElement", new String[]
        {
          badElement
        }));
      }
      return SUCCESS;
    }

  }

  public static class PrepareRevert extends SettingsBean
  {
    private ArrayList<SubtaskView> subtasks;

    public PrepareRevert()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      // Retrieve task and populate input data for editing.
      Task task = getTask();
      setTaskName(task.getName());
      setTaskText(task.getText());
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      subtasks = SubtaskView.selectByContainerId(conn, taskId);
      return INPUT;
    }

    public ArrayList<SubtaskView> getSubtasks()
    {
      return subtasks;
    }

  }

  public static class Revert extends WetoTeacherAction
  {
    public Revert()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      // Retrieve task from the database, populate it with
      // the values entered into form and update it.
      Task task = getTask();
      String tmpText = task.getText();
      task.setText(task.getOldText());
      task.setOldText(tmpText);
      task.update(conn);
      // If we are updating course task, replicate changes to master db
      try
      {
        Connection masterConn = getMasterConnection();
        CourseImplementation ci = CourseImplementation
                .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                        taskId);
        Task masterTask = Task.select1ById(masterConn, ci.getMasterTaskId());
        masterTask.setText(task.getText());
        masterTask.setOldText(task.getOldText());
        masterTask.update(masterConn);
      }
      catch(NoSuchItemException e)
      {
        // No replication required: this was not a course task.
      }
      addActionMessage(getText("editTask.message.updateSuccess"));
      return SUCCESS;
    }

  }

  public static class PrepareCreate extends SettingsBean
  {
    public PrepareCreate()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      //Set default task name
      setTaskName(getText("editTask.default.name"));
      return INPUT;
    }

  }

  public static class Create extends SettingsBean
  {
    public Create()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      // Create one or more subtasks.
      int i = (isMultipleTasks() == true) ? getMinRange() : 0;
      ArrayList<SubtaskView> subtasks = SubtaskView.selectByContainerId(conn,
              taskId);
      int subRank = 0;
      if(subtasks.size() > 0 && subtasks.get(subtasks.size() - 1).getRank()
              != null)
      {
        subRank = subtasks.get(subtasks.size() - 1).getRank() + 1;
      }
      do
      {
        // Create a new task and populate it with the
        // values entered in the form.
        Task viewedTask = getTask();
        // Create new task and populate it with the
        // values entered in the form.
        Task task = new Task();
        task.setHasGrades(isHasGrades());
        task.setHasSubmissions(isHasSubmissions());
        task.setShowTextInParent(isShowTextInParent());
        task.setIsHidden(isHidden());
        task.setIsPublic(isPublicTask());
        task.setIsQuiz(isQuiz());
        task.setHasForum(isHasForum());
        task.setRootTaskId(viewedTask.getRootTaskId());

        String taskName = getTaskName();
        if(isMultipleTasks())
        {
          task.setName(taskName + i);
        }
        else
        {
          if(taskName.isEmpty())
          {
            taskName = "-";
          }
          task.setName(getTaskName());
        }
        task.insert(conn);

        // If task has grades, create scoring rules for it.
        if(task.getHasGrades() == true)
        {
          Scoring scoring = new Scoring();
          if(getMinScore() != null)
          {
            scoring.setProperty("minScore", getMinScore().toString());
          }
          if(getMaxScore() != null)
          {
            scoring.setProperty("maxScore", getMaxScore().toString());
          }
          scoring.setTaskId(task.getId());
          scoring.insert(conn);
        }

        // Create subtask link.
        SubtaskLink subtask = new SubtaskLink();
        subtask.setContainerId(taskId);
        subtask.setSubtaskId(task.getId());
        subtask.setRank(subRank++);
        subtask.insert(conn);
        if(isHasSubmissions() && !getFilePatterns().isEmpty())
        {
          SubmissionProperties sp = new SubmissionProperties();
          sp.setTaskId(task.getId());
          Properties properties = new Properties();
          if((getFilePatterns() != null) && !getFilePatterns().isEmpty())
          {
            properties.setProperty("filePatterns", getFilePatterns());
          }
          if((getPatternDescriptions() != null) && !getPatternDescriptions()
                  .isEmpty())
          {
            properties.setProperty("patternDescriptions",
                    getPatternDescriptions());
          }
          properties.setProperty("allowZipping", isAllowZipping() ? "true"
                                                         : "false");
          if((getOldSubmissionLimit() != null) && (getOldSubmissionLimit() >= 0))
          {
            properties.setProperty("oldSubmissionLimit", getOldSubmissionLimit()
                    .toString());
          }
          StringWriter sw = new StringWriter();
          properties.store(sw, null);
          sp.setProperties(sw.toString());
          sp.insert(conn);
        }
      }
      while(isMultipleTasks() && (++i <= getMaxRange()));
      refreshNavigationTree();
      addActionMessage(getText("editTask.message.createSuccess"));
      return SUCCESS;
    }

  }

  public static class PrepareDeleteSubmissions extends WetoTeacherAction
  {
    public PrepareDeleteSubmissions()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, Tab.SUBMISSIONS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      return INPUT;
    }

  }

  public static class DeleteSubmissions extends WetoTeacherAction
  {
    private static final Logger logger = Logger.getLogger(
            DeleteSubmissions.class);

    public DeleteSubmissions()
    {
      super(Tab.MAIN.getBit(), 0, 0, Tab.MAIN.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      logger.debug("User " + this.getMasterUserId()
              + " is deleting submissions under task " + taskId);
      ArrayDeque<Integer> dfsStack = new ArrayDeque<>();
      dfsStack.add(taskId);
      HashSet<Integer> processedSet = new HashSet<>();
      while(!dfsStack.isEmpty())
      {
        Integer currTaskId = dfsStack.getLast();
        if(!processedSet.contains(currTaskId))
        {
          ArrayList<Integer> subtaskIds = Task
                  .selectSubtaskIds(conn, currTaskId);
          dfsStack.addAll(subtaskIds);
          processedSet.add(currTaskId);
        }
        else
        {
          SubmissionModel.deleteStudentTaskSubmissions(conn, currTaskId);
          dfsStack.removeLast();
        }
      }
      addActionMessage(getText("deleteSubmissions.message.deleteSuccess"));
      return SUCCESS;
    }

  }

  public static class PrepareDelete extends WetoTeacherAction
  {
    public PrepareDelete()
    {
      super(Tab.MAIN.getBit(), 0, 0, Tab.MAIN.getBit());
    }

    @Override
    public String action() throws Exception
    {
      if(isCourseTask())
      {
        throw new WetoActionException(getText("deleteTask.error.courseTask"));
      }
      return INPUT;
    }

  }

  public static class Delete extends WetoTeacherAction
  {
    private static final Logger logger = Logger.getLogger(Delete.class);

    public Delete()
    {
      super(Tab.MAIN.getBit(), 0, 0, Tab.MAIN.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      // Check that task is not a root level course task.
      if(isCourseTask())
      {
        throw new WetoActionException(getText("deleteTask.error.courseTask"));
      }
      logger.debug("User " + this.getMasterUserId() + " is deleting task "
              + taskId);
      ArrayDeque<Integer> dfsStack = new ArrayDeque<>();
      dfsStack.add(taskId);
      HashSet<Integer> processedSet = new HashSet<>();
      Integer containerId = null;
      while(!dfsStack.isEmpty())
      {
        Integer currTaskId = dfsStack.getLast();
        if(!processedSet.contains(currTaskId))
        {
          ArrayList<Integer> subtaskIds = Task
                  .selectSubtaskIds(conn, currTaskId);
          dfsStack.addAll(subtaskIds);
          processedSet.add(currTaskId);
        }
        else
        {
          containerId = TaskModel.deleteCourseDbTask(conn, null, currTaskId);
          dfsStack.removeLast();
        }
      }
      if(containerId == null)
      {
        throw new WetoActionException(getText("deleteTask.error.noContainer"));
      }
      setTaskId(containerId);
      refreshNavigationTree();
      addActionMessage(getText("deleteTask.message.deleteSuccess"));
      return SUCCESS;
    }

  }

  public static class EditCss extends WetoTeacherAction
  {
    private String cssText;

    public EditCss()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      if(!isCourseTask())
      {
        throw new WetoActionException(getText(
                "taskSettings.error.editSubtaskCss"));
      }
      String cssFilename = getCourseCssFilename();
      String cssPath = ServletActionContext.getServletContext().getRealPath(
              "/css/" + cssFilename);
      File cssFile = new File(cssPath);
      if(!cssFile.canRead())
      {
        throw new WetoActionException(getText(
                "taskSettings.error.invalidCssFile"));
      }
      try(ByteArrayOutputStream text = new ByteArrayOutputStream())
      {
        WetoUtilities.fileToStream(cssFile, text);
        cssText = text.toString("UTF-8");
      }
      return SUCCESS;
    }

    public String getCssText()
    {
      return cssText;
    }

  }

  public static class SaveCss extends WetoTeacherAction
  {
    private String cssText;

    public SaveCss()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      if(!isCourseTask())
      {
        throw new WetoActionException(getText(
                "taskSettings.error.editSubtaskCss"));
      }
      Integer courseTaskId = getCourseTaskId();
      Connection courseConn = getCourseConnection();
      ArrayList<Tag> cssTags = Tag.selectByTaggedIdAndType(courseConn,
              courseTaskId, TagType.CSS_STYLE.getValue());
      if(cssTags.isEmpty())
      {
        Tag cssTag = new Tag();
        cssTag.setType(TagType.CSS_STYLE.getValue());
        cssTag.setTaggedId(courseTaskId);
        cssTag.setAuthorId(getCourseUserId());
        cssTag.setText(cssText);
        cssTag.insert(courseConn);
      }
      else
      {
        Tag cssTag = cssTags.get(0);
        cssTag.setText(cssText);
        cssTag.update(courseConn);
      }
      String cssFilename = getCourseCssFilename();
      String cssPath = ServletActionContext.getServletContext().getRealPath(
              "/css/" + cssFilename);
      File cssFile = new File(cssPath);
      try(ByteArrayInputStream text = new ByteArrayInputStream(cssText
              .getBytes()))
      {
        WetoUtilities.streamToFile(text, cssFile);
      }
      addActionMessage(getText("taskSettings.message.cssSaved"));
      return SUCCESS;
    }

    public void setCssText(String cssText)
    {
      this.cssText = cssText;
    }

  }

  public static class ViewSettings extends SettingsBean
  {
    public ViewSettings()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer taskId = getTaskId();
      if(isCourseTask())
      {
        Connection masterConn = getMasterConnection();
        CourseImplementation ci = CourseImplementation
                .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                        getCourseTaskId());
        // Retrieve settings and populate input data for editing.
        setAcceptAllStudents(ci.getAcceptAllStudents());
        setTeachers(UserAccount.selectTeachers(masterConn));
        setCourseTeachers(UserTaskView.selectByTaskIdAndClusterType(courseConn,
                getCourseTaskId(), ClusterType.TEACHERS.getValue()));
      }
      // Retrieve task and populate input data for editing.
      Task task = getTask();
      setHasGrades(task.getHasGrades());
      setHasSubmissions(task.getHasSubmissions());
      setHasForum(task.getHasForum());
      setHasGroups(task.getHasGroups());
      setShowTextInParent(task.getShowTextInParent());
      setHidden(task.getIsHidden());
      setPublicTask(task.getIsPublic());
      setQuiz(task.getIsQuiz());
      return INPUT;
    }

  }

  public static class SaveSettings extends SettingsBean
  {
    public SaveSettings()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      // Retrieve task from the database, populate it with
      // the values entered into form and update it.
      Task task = getTask();
      boolean sameHasSubmissions = (task.getHasSubmissions()
              == isHasSubmissions());
      boolean sameHasGrades = (task.getHasGrades() == isHasGrades());
      boolean sameHasForum = (task.getHasForum() == isHasForum());
      boolean sameHasGroups = (task.getHasGroups() == isHasGroups());
      boolean sameIsQuiz = (task.getIsQuiz() == isQuiz());
      boolean sameShowTextInParent = (task.getShowTextInParent()
              == isShowTextInParent());
      boolean sameIsHidden = (task.getIsHidden() == isHidden());
      boolean sameIsPublicTask = (task.getIsPublic() == isPublicTask());
      boolean refreshNavigationTree = false;
      HashSet<Integer> quizSubtaskIds = new HashSet<>();
      try
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, taskId);
        Task parent = Task.select1ById(conn, link.getContainerId());
        quizSubtaskIds.add(parent.getId());
      }
      catch(NoSuchItemException e)
      {
      }
      ArrayDeque<Task> dfsStack = new ArrayDeque();
      dfsStack.add(task);
      boolean changesOnly = false;
      while(!dfsStack.isEmpty())
      {
        Task dfsTask = dfsStack.removeLast();
        boolean hiddenChanged = false;
        if(!(changesOnly && sameHasSubmissions))
        {
          dfsTask.setHasSubmissions(isHasSubmissions());
        }
        if(!(changesOnly && sameHasGrades))
        {
          dfsTask.setHasGrades(isHasGrades());
        }
        if(!(changesOnly && sameHasForum))
        {
          dfsTask.setHasForum(isHasForum());
        }
        if(!(changesOnly && sameHasGroups))
        {
          dfsTask.setHasGroups(isHasGroups());
        }
        if(!(changesOnly && sameIsQuiz))
        {
          dfsTask.setIsQuiz(isQuiz());
        }
        if(!(changesOnly && sameShowTextInParent))
        {
          dfsTask.setShowTextInParent(isShowTextInParent());
        }
        if(!(changesOnly && sameIsHidden))
        {
          hiddenChanged = (dfsTask.getIsHidden() != isHidden());
          dfsTask.setIsHidden(isHidden());
        }
        if(!(changesOnly && sameIsPublicTask))
        {
          dfsTask.setIsPublic(isPublicTask());
        }
        if(dfsTask.getIsQuiz() || isApplySettingsToSubtasks())
        {
          for(Task subtask : Task.selectSubtasks(conn, dfsTask.getId()))
          {
            if(dfsTask.getIsQuiz())
            {
              // Ensure that subtask-quizzes are hidden if this task is a quiz.
              if(subtask.getIsQuiz() && !subtask.getIsHidden())
              {
                subtask.setIsHidden(true);
                subtask.update(conn);
              }
              // Record information that the subtask has a quiz parent
              quizSubtaskIds.add(subtask.getId());
            }
            if(isApplySettingsToSubtasks())
            {
              dfsStack.add(subtask);
            }
          }
        }
        // Prevent this task from becoming unhidden if it is a subtask-quiz.
        if(!dfsTask.getIsHidden() && quizSubtaskIds.contains(dfsTask.getId()))
        {
          dfsTask.setIsHidden(true);
          addActionError(getText("quiz.error.subQuizMustBeHidden"));
        }
        dfsTask.update(conn);
        if(hiddenChanged)
        {
          refreshNavigationTree = true;
        }
        // If the task has grades, ensure that it has a scoring entry.
        if(dfsTask.getHasGrades() == true)
        {
          try
          {
            Scoring.select1ByTaskId(conn, dfsTask.getId());
          }
          catch(NoSuchItemException e)
          { // Scoring rules are added only if they did not exist before.
            Scoring scoring = new Scoring();
            scoring.setTaskId(dfsTask.getId());
            scoring.insert(conn);
          }
        }
        changesOnly = (isApplySettingsToSubtasks() && isChangesOnly());
      }
      if(refreshNavigationTree)
      {
        refreshNavigationTree();
      }
      // If we are updating a course task, replicate changes to master db
      if(isCourseTask())
      {
        Connection masterConn = getMasterConnection();
        CourseImplementation ci = CourseImplementation
                .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                        taskId);
        Task masterTask = Task.select1ById(masterConn, ci.getMasterTaskId());
        masterTask.setHasGrades(isHasGrades());
        masterTask.setHasSubmissions(isHasSubmissions());
        masterTask.setShowTextInParent(isShowTextInParent());
        masterTask.setIsHidden(isHidden());
        masterTask.setIsPublic(isPublicTask());
        masterTask.setIsQuiz(isQuiz());
        masterTask.setHasForum(isHasForum());
        masterTask.update(masterConn);
      }
      addActionMessage(getText("taskSettings.message.taskSuccess"));
      return SUCCESS;
    }

  }

  public static class SaveCourseSettings extends SettingsBean
  {
    public SaveCourseSettings()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Integer taskId = getTaskId();
      Connection masterConn = getMasterConnection();
      CourseImplementation ci = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                      taskId);
      ci.setAcceptAllStudents(isAcceptAllStudents());
      ci.update(masterConn);
      addActionMessage(getText("taskSettings.message.courseSuccess"));
      return SUCCESS;
    }

  }

  private static abstract class SettingsBean extends WetoTeacherAction
  {
    private ArrayList<UserAccount> teachers = new ArrayList<>();
    private ArrayList<UserTaskView> courseTeachers = new ArrayList<>();

    private String taskName = "";
    private boolean hasGrades;
    private boolean hasSubmissions;
    private boolean hasForum;
    private String taskText = "";
    private boolean showTextInParent;
    private boolean hidden;
    private boolean publicTask;
    private boolean quiz;
    private int minScore;
    private int maxScore;
    private String filePatterns = "";
    private String patternDescriptions = "";
    private boolean allowZipping;
    private Integer oldSubmissionLimit = null;
    private boolean multipleTasks;
    private int minRange;
    private int maxRange;
    private boolean hasGroups;
    private boolean acceptAllStudents;

    private boolean applySettingsToSubtasks;
    private boolean changesOnly;

    public SettingsBean(int reqOwnerViewBits, int reqOwnerUpdateBits,
            int reqOwnerCreateBits, int reqOwnerDeleteBits)
    {
      super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits,
              reqOwnerDeleteBits);
    }

    public ArrayList<UserAccount> getTeachers()
    {
      return teachers;
    }

    void setTeachers(ArrayList<UserAccount> teachers)
    {
      this.teachers = teachers;
    }

    public ArrayList<UserTaskView> getCourseTeachers()
    {
      return courseTeachers;
    }

    void setCourseTeachers(ArrayList<UserTaskView> courseTeachers)
    {
      this.courseTeachers = courseTeachers;
    }

    public String getTaskName()
    {
      return taskName;
    }

    public void setTaskName(String taskName)
    {
      this.taskName = taskName;
    }

    public boolean isHasGrades()
    {
      return hasGrades;
    }

    public void setHasGrades(Boolean hasGrades)
    {
      this.hasGrades = hasGrades;
    }

    public boolean isHasSubmissions()
    {
      return hasSubmissions;
    }

    public void setHasSubmissions(boolean hasSubmissions)
    {
      this.hasSubmissions = hasSubmissions;
    }

    public boolean isHasForum()
    {
      return hasForum;
    }

    public void setHasForum(boolean hasForum)
    {
      this.hasForum = hasForum;
    }

    public String getTaskText()
    {
      return taskText;
    }

    public void setTaskText(String taskText)
    {
      this.taskText = (taskText != null) ? taskText.replaceAll("\r\n", "\n")
                              : null;
    }

    public boolean isShowTextInParent()
    {
      return showTextInParent;
    }

    public void setShowTextInParent(boolean showTextInParent)
    {
      this.showTextInParent = showTextInParent;
    }

    public boolean isHidden()
    {
      return hidden;
    }

    public void setHidden(boolean hidden)
    {
      this.hidden = hidden;
    }

    public boolean isPublicTask()
    {
      return publicTask;
    }

    public void setPublicTask(boolean publicTask)
    {
      this.publicTask = publicTask;
    }

    public boolean isApplySettingsToSubtasks()
    {
      return applySettingsToSubtasks;
    }

    public void setApplySettingsToSubtasks(boolean applySettingsToSubtasks)
    {
      this.applySettingsToSubtasks = applySettingsToSubtasks;
    }

    public boolean isChangesOnly()
    {
      return changesOnly;
    }

    public void setChangesOnly(boolean changesOnly)
    {
      this.changesOnly = changesOnly;
    }

    public boolean isQuiz()
    {
      return quiz;
    }

    public void setQuiz(boolean quiz)
    {
      this.quiz = quiz;
    }

    public Integer getMinScore()
    {
      return minScore;
    }

    public void setMinScore(Integer minScore)
    {
      this.minScore = minScore;
    }

    public Integer getMaxScore()
    {
      return maxScore;
    }

    public void setMaxScore(Integer maxScore)
    {
      this.maxScore = maxScore;
    }

    public String getFilePatterns()
    {
      return filePatterns;
    }

    public void setFilePatterns(String filePatterns)
    {
      this.filePatterns = filePatterns;
    }

    public String getPatternDescriptions()
    {
      return patternDescriptions;
    }

    public void setPatternDescriptions(String patternDescriptions)
    {
      this.patternDescriptions = patternDescriptions;
    }

    public boolean isAllowZipping()
    {
      return allowZipping;
    }

    public void setAllowZipping(boolean allowZipping)
    {
      this.allowZipping = allowZipping;
    }

    public Integer getOldSubmissionLimit()
    {
      return oldSubmissionLimit;
    }

    public void setOldSubmissionLimit(Integer oldSubmissionLimit)
    {
      this.oldSubmissionLimit = oldSubmissionLimit;
    }

    public boolean isMultipleTasks()
    {
      return multipleTasks;
    }

    public void setMultipleTasks(boolean multipleTasks)
    {
      this.multipleTasks = multipleTasks;
    }

    public Integer getMinRange()
    {
      return minRange;
    }

    public void setMinRange(Integer minRange)
    {
      this.minRange = minRange;
    }

    public Integer getMaxRange()
    {
      return maxRange;
    }

    public void setMaxRange(Integer maxRange)
    {
      this.maxRange = maxRange;
    }

    public boolean isHasGroups()
    {
      return hasGroups;
    }

    public void setHasGroups(boolean hasGroups)
    {
      this.hasGroups = hasGroups;
    }

    public boolean isAcceptAllStudents()
    {
      return acceptAllStudents;
    }

    public void setAcceptAllStudents(boolean acceptAllStudents)
    {
      this.acceptAllStudents = acceptAllStudents;
    }
  }

}
