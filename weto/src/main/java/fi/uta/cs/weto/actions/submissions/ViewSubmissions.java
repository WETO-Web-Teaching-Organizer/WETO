package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.FileSubmissionBean;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionError;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ViewSubmissions extends WetoCourseAction
{
  private static final SubmissionStatus[] submissionStates = SubmissionStatus
          .values();
  private static final SubmissionError[] submissionErrors = SubmissionError
          .values();
  private static final Integer notSubmittedState
                                       = SubmissionStatus.NOT_SUBMITTED
                  .getValue();
  private static final Integer acceptedState = SubmissionStatus.ACCEPTED
          .getValue();
  private static final Integer notAcceptedState = SubmissionStatus.NOT_ACCEPTED
          .getValue();

  private int mostSubmissions;
  private ArrayList<UserTaskView> students = new ArrayList<>();
  private boolean createRights;
  private boolean updateRights;
  private boolean deleteRights;
  private boolean submissionPeriodActive;
  private String[] submissionPeriod;
  private String[] generalSubmissionPeriod;
  private Map<Integer, ArrayList<Submission>> userSubmissions;
  private Integer submissionsTotal;
  private Integer submissionsShowing;
  private Integer studentsShowing;
  private Integer acceptedStudents;
  private boolean allowedFilePatternsDefined;
  private String from;
  private String to;
  private boolean showAllStudents;
  private final Map<Integer, String> groupMember = new HashMap<>();
  private String[] submitterGroups;
  private Integer submissionQuota;

  UserTaskView student;
  ArrayList<FileSubmissionBean> fileSubmissionBeans;
  private String patternDescriptions;
  private boolean allowZipping;
  private boolean allowInlineFiles;
  private boolean allowTestRun;
  private ArrayList<ArrayList<Grade>> grades;
  private HashMap<Integer, UserTaskView> reviewerMap;

  public ViewSubmissions()
  {
    super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Integer taskId = getTaskId();
    Integer userId = getCourseUserId();
    // check if allowed file types have been defined, and notify user if not
    allowedFilePatternsDefined = false;
    submissionQuota = null;
    Properties properties = new Properties();
    try
    {
      SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
              taskId);
      properties.load(new StringReader(sp.getProperties()));
      String filePatterns = properties.getProperty("filePatterns");
      allowedFilePatternsDefined = (filePatterns != null) && !filePatterns
              .isEmpty();
      String oldSubmissionLimit = properties.getProperty("oldSubmissionLimit");
      try
      {
        submissionQuota = 1 + Integer.valueOf(oldSubmissionLimit);
      }
      catch(NumberFormatException e)
      {
      }
    }
    catch(NoSuchItemException e)
    {
    }
    if(getNavigator().isTeacher())
    {
      retrieveTeacherSubmissions(conn, taskId, userId);
    }
    else
    {
      retrieveStudentSubmissions(conn, taskId, userId, properties);
    }
    return SUCCESS;
  }

  void retrieveTeacherSubmissions(Connection conn, Integer taskId,
          Integer userId)
          throws SQLException, WetoTimeStampException
  {
    final String userIP = getNavigator().getUserIP();
    WetoTimeStamp[] timeLimits = PermissionModel
            .getTimeStampLimits(conn, userIP, userId, taskId,
                    PermissionType.SUBMISSION, getNavigator().isTeacher());
    submissionPeriodActive = (PermissionModel.checkTimeStampLimits(timeLimits)
            == PermissionModel.CURRENT);
    submissionPeriod = WetoTimeStamp.limitsToStrings(timeLimits);
    WetoTimeStamp[] generalTimeLimits = PermissionModel.getTimeStampLimits(conn,
            userIP, null, taskId, PermissionType.SUBMISSION);
    generalSubmissionPeriod = WetoTimeStamp.limitsToStrings(generalTimeLimits);
    createRights = haveCreateRights(Tab.SUBMISSIONS.getBit(), false, true);
    updateRights = haveUpdateRights(Tab.SUBMISSIONS.getBit(), false, true);
    deleteRights = haveDeleteRights(Tab.SUBMISSIONS.getBit(), false, true);
    students = UserTaskView.selectByTaskIdAndClusterType(conn, taskId,
            ClusterType.STUDENTS.getValue());
    HashSet<Integer> uids = new HashSet<>();
    ArrayList<GroupView> submitterGroupList = GroupView
            .selectInheritedByTaskIdAndType(conn, taskId, GroupType.SUBMISSION
                    .getValue());
    HashMap<Integer, GroupView> submitterMap = new HashMap<>();
    for(GroupView gv : submitterGroupList)
    {
      submitterMap.put(gv.getUserId(), gv);
    }
    submitterGroups = new String[students.size()];
    int i = 0;
    for(UserTaskView student : students)
    {
      uids.add(student.getUserId());
      groupMember.put(student.getUserId(), "-");
      GroupView submitterGroup = submitterMap.get(student.getUserId());
      if(submitterGroup != null)
      {
        submitterGroups[i] = submitterGroup.getName();
      }
      else
      {
        submitterGroups[i] = "";
      }
      i += 1;
    }
    ArrayList<GroupView> groupMembers = GroupView
            .selectInheritedByTaskIdAndNotType(conn, taskId,
                    GroupType.SUBMISSION.getValue());
    for(GroupView gv : groupMembers)
    {
      groupMember.put(gv.getUserId(), gv.getName().split(";")[0]);
    }
    Integer fromTimeStamp = null;
    DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    if(from != null && !from.isEmpty())
    {
      try
      {
        Calendar cal = new GregorianCalendar();
        cal.setTime(formatter.parse(from));
        fromTimeStamp = new WetoTimeStamp(cal).getTimeStamp();
      }
      catch(ParseException e)
      {
        addActionError(e.getMessage());
      }
    }
    Integer toTimeStamp = null;
    if(to != null && !to.isEmpty())
    {
      try
      {
        Calendar cal = new GregorianCalendar();
        cal.setTime(formatter.parse(to));
        // The to-date is interpreted mean "until the end of the given day"
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        toTimeStamp = new WetoTimeStamp(cal).getTimeStamp();
      }
      catch(ParseException e)
      {
        addActionError(e.getMessage());
      }
    }
    int[] stats = new int[3];
    userSubmissions = SubmissionModel.getSubmissions(conn, taskId, userId, uids,
            fromTimeStamp, toTimeStamp, false, stats, this);
    submissionsTotal = stats[0];
    mostSubmissions = stats[1];
    acceptedStudents = stats[2];
    submissionsShowing = 0;
    studentsShowing = 0;
    for(UserTaskView student : students)
    {
      ArrayList<Submission> submissions = userSubmissions.get(student
              .getUserId());
      int size = (submissions != null) ? submissions.size() : 0;
      if(size > 0)
      {
        studentsShowing++;
        submissionsShowing += size;
      }
    }
  }

  void retrieveStudentSubmissions(Connection conn, Integer taskId,
          Integer userId, Properties properties)
          throws SQLException, WetoTimeStampException, NoSuchItemException,
                 IOException, InvalidValueException, ObjectNotValidException
  {
    final String userIP = getNavigator().getUserIP();
    WetoTimeStamp[] timeLimits = PermissionModel
            .getTimeStampLimits(conn, userIP, userId, taskId,
                    PermissionType.SUBMISSION, false);
    submissionPeriodActive = (PermissionModel.checkTimeStampLimits(timeLimits)
            == PermissionModel.CURRENT);
    submissionPeriod = WetoTimeStamp.limitsToStrings(timeLimits);
    WetoTimeStamp[] generalTimeLimits = PermissionModel.getTimeStampLimits(conn,
            userIP, null, taskId, PermissionType.SUBMISSION);
    generalSubmissionPeriod = WetoTimeStamp.limitsToStrings(generalTimeLimits);
    createRights = haveCreateRights(Tab.SUBMISSIONS.getBit(), true, false);
    updateRights = haveUpdateRights(Tab.SUBMISSIONS.getBit(), true, false);
    deleteRights = haveDeleteRights(Tab.SUBMISSIONS.getBit(), true, false);
    student = UserTaskView.select1ByTaskIdAndUserId(conn, taskId, userId);
    boolean ignoreGroups = false;
    try
    {
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
    }
    catch(NoSuchItemException e)
    {
    }
    ArrayList<Submission> submissions = SubmissionModel.getStudentSubmissions(
            conn, taskId, userId, ignoreGroups);
    mostSubmissions = submissionsTotal = submissions.size();
    Task task = getTask();
    if(!task.getIsQuiz())
    {
      patternDescriptions = properties.getProperty("patternDescriptions");
      if((patternDescriptions == null) || patternDescriptions.isEmpty())
      {
        patternDescriptions = properties.getProperty("filePatterns");
      }
      allowZipping = Boolean.valueOf(properties.getProperty("allowZipping"));
      allowInlineFiles = Boolean.valueOf(properties.getProperty(
              "allowInlineFiles"));
      allowTestRun = Boolean.valueOf(properties.getProperty("allowTestRun"));
      fileSubmissionBeans = new ArrayList<>();
      for(Submission submission : submissions)
      {
        fileSubmissionBeans.add(SubmissionModel.getFileSubmissionBean(conn,
                submission, task, true));
      }
    }
  }

  public SubmissionStatus[] getSubmissionStates()
  {
    return submissionStates;
  }

  public SubmissionError[] getSubmissionErrors()
  {
    return submissionErrors;
  }

  public Integer getNotSubmittedState()
  {
    return notSubmittedState;
  }

  public Integer getAcceptedState()
  {
    return acceptedState;
  }

  public Integer getNotAcceptedState()
  {
    return notAcceptedState;
  }

  public int getMostSubmissions()
  {
    return mostSubmissions;
  }

  public Integer getSubmissionQuota()
  {
    return submissionQuota;
  }

  public ArrayList<UserTaskView> getStudents()
  {
    return students;
  }

  public boolean isCreateRights()
  {
    return createRights;
  }

  public boolean isUpdateRights()
  {
    return updateRights;
  }

  public boolean isDeleteRights()
  {
    return deleteRights;
  }

  public boolean isSubmissionPeriodActive()
  {
    return submissionPeriodActive;
  }

  public String[] getSubmissionPeriod()
  {
    return submissionPeriod;
  }

  public String[] getGeneralSubmissionPeriod()
  {
    return generalSubmissionPeriod;
  }

  public Map<Integer, ArrayList<Submission>> getUserSubmissions()
  {
    return userSubmissions;
  }

  public Integer getSubmissionsTotal()
  {
    return submissionsTotal;
  }

  public Integer getSubmissionsShowing()
  {
    return submissionsShowing;
  }

  public Integer getStudentsShowing()
  {
    return studentsShowing;
  }

  public Integer getAcceptedStudents()
  {
    return acceptedStudents;
  }

  public boolean isAllowedFilePatternsDefined()
  {
    return allowedFilePatternsDefined;
  }

  public String getFrom()
  {
    return from;
  }

  public void setFrom(String from)
  {
    this.from = from;
  }

  public String getTo()
  {
    return to;
  }

  public void setTo(String to)
  {
    this.to = to;
  }

  public boolean isShowAllStudents()
  {
    return showAllStudents;
  }

  public void setShowAllStudents(boolean showAllStudents)
  {
    this.showAllStudents = showAllStudents;
  }

  public Map<Integer, String> getGroupMember()
  {
    return groupMember;
  }

  public String[] getSubmitterGroups()
  {
    return submitterGroups;
  }

  public UserTaskView getStudent()
  {
    return student;
  }

  public ArrayList<FileSubmissionBean> getFileSubmissionBeans()
  {
    return fileSubmissionBeans;
  }

  public String getPatternDescriptions()
  {
    return patternDescriptions;
  }

  public boolean isAllowZipping()
  {
    return allowZipping;
  }

  public boolean isAllowInlineFiles()
  {
    return allowInlineFiles;
  }

  public boolean isAllowTestRun()
  {
    return allowTestRun;
  }

  public ArrayList<ArrayList<Grade>> getGrades()
  {
    return grades;
  }

  public HashMap<Integer, UserTaskView> getReviewerMap()
  {
    return reviewerMap;
  }

  public static class JSONSubmission
  {
    public JSONSubmission(Integer id, Integer status, String message,
            Integer error, Integer autoGradeMark, String timeStampString,
            Integer timeStamp, Integer queuePos)
    {
      this.id = id;
      this.status = status;
      this.message = message;
      this.error = error;
      this.autoGradeMark = autoGradeMark;
      this.timeStampString = timeStampString;
      this.timeStamp = timeStamp;
      this.queuePos = queuePos;
    }

    private final Integer id;
    private final Integer status;
    private final String message;
    private final Integer error;
    private final Integer autoGradeMark;
    private final String timeStampString;
    private final Integer timeStamp;
    private final Integer queuePos;

    public Integer getId()
    {
      return id;
    }

    public Integer getStatus()
    {
      return status;
    }

    public String getMessage()
    {
      return message;
    }

    public Integer getError()
    {
      return error;
    }

    public Integer getAutoGradeMark()
    {
      return autoGradeMark;
    }

    public String getTimeStampString()
    {
      return timeStampString;
    }

    public Integer getTimeStamp()
    {
      return timeStamp;
    }

    public Integer getQueuePos()
    {
      return queuePos;
    }

  }

  public static class GetJSONSubmission extends WetoCourseAction
  {
    private JSONSubmission jsonSubmission;
    private Integer submissionId;

    public JSONSubmission getSubmission()
    {
      return jsonSubmission;
    }

    public void setSubmissionId(int submissionId)
    {
      this.submissionId = submissionId;
    }

    public GetJSONSubmission()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      Submission submission = Submission.select1ById(conn, submissionId);
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to view/edit the submission.
      // If the user is a student but not the submitter, check submission group.
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, getTaskId());
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
              .isStudent(), ignoreGroups);
      // Verify that the logged user has rights to view the submission.
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ERROR);
      }
      int status = submission.getStatus();
      String timeStampString = submission.getTimeStampString();
      Integer timeStamp = submission.getTimeStamp();
      // Message, error or autoGradeMark might possibly be null
      String message = submission.getMessage();
      if(message == null)
      {
        message = "";
      }
      Integer error = submission.getError();
      Integer autoGradeMark = submission.getAutoGradeMark();
      Integer queuePos = null;
      if(status == SubmissionStatus.PROCESSING.getValue())
      {
        queuePos = AutoGradeJobQueue.getQueuePos(conn, submissionId);
      }
      jsonSubmission = new JSONSubmission(submissionId, status, message, error,
              autoGradeMark, timeStampString, timeStamp, queuePos);
      return SUCCESS;
    }

  }

  public static class GetJSONSubmissions extends WetoCourseAction
  {
    private List<JSONSubmission> jsonSubmissions;
    private Map<String, String> submissionStates;
    private Map<String, String> submissionErrors;
    private String filePatterns;
    private String patternDescriptions;
    private Boolean allowZipping;
    private Boolean allowInlineFiles;
    private Boolean allowTestRun;
    private Boolean hasAutoGrading;
    private Integer oldSubmissionLimit;
    private String submissionQuotaMessage;

    public Map<String, String> getSubmissionStates()
    {
      return submissionStates;
    }

    public Map<String, String> getSubmissionErrors()
    {
      return submissionErrors;
    }

    public List<JSONSubmission> getSubmissions()
    {
      return jsonSubmissions;
    }

    public String getFilePatterns()
    {
      return filePatterns;
    }

    public String getPatternDescriptions()
    {
      return patternDescriptions;
    }

    public Boolean getAllowZipping()
    {
      return allowZipping;
    }

    public Boolean getAllowInlineFiles()
    {
      return allowInlineFiles;
    }

    public Boolean getAllowTestRun()
    {
      return allowTestRun;
    }

    public Boolean getHasAutoGrading()
    {
      return hasAutoGrading;
    }

    public Integer getOldSubmissionLimit()
    {
      return oldSubmissionLimit;
    }

    public String getSubmissionQuotaMessage()
    {
      return submissionQuotaMessage;
    }

    public GetJSONSubmissions()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      jsonSubmissions = new ArrayList<>();
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      ArrayList<Submission> submissions = SubmissionModel.getStudentSubmissions(
              conn, taskId, userId, ignoreGroups);
      for(Submission submission : submissions)
      {
        Integer submissionId = submission.getId();
        int status = submission.getStatus();
        String timeStampString = submission.getTimeStampString();
        Integer timeStamp = submission.getTimeStamp();
        // Message, error or autoGradeMark might possibly be null
        String message = submission.getMessage();
        Integer error = submission.getError();
        Integer autoGradeMark = submission.getAutoGradeMark();
        Integer queuePos = null;
        if(status == SubmissionStatus.PROCESSING.getValue())
        {
          queuePos = AutoGradeJobQueue.getQueuePos(conn, submissionId);
        }
        JSONSubmission newJSONSubmission = new JSONSubmission(submissionId,
                status, message, error, autoGradeMark, timeStampString,
                timeStamp, queuePos);
        jsonSubmissions.add(newJSONSubmission);
      }
      // Get submission options
      try
      {
        // Get status and state names
        submissionStates = new HashMap<>();
        for(SubmissionStatus substatus : SubmissionStatus.values())
        {
          submissionStates.put(substatus.getValue().toString(), getText(
                  substatus
                          .getProperty()));
        }
        submissionErrors = new HashMap<>();
        for(SubmissionError suberror : SubmissionError.values())
        {
          submissionErrors.put(suberror.getValue().toString(), getText(suberror
                  .getProperty()));
        }
        // Properties
        SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
                taskId);
        Properties properties = new Properties();
        properties.load(new StringReader(sp.getProperties()));
        filePatterns = properties.getProperty("filePatterns");
        patternDescriptions = properties.getProperty("patternDescriptions");
        allowZipping = Boolean.valueOf(properties.getProperty("allowZipping"));
        allowInlineFiles = Boolean.valueOf(properties.getProperty(
                "allowInlineFiles"));
        allowTestRun = Boolean.valueOf(properties.getProperty("allowTestRun"));
        hasAutoGrading = getTask().getIsAutoGraded();
        String oldSubmissionLimitString = properties.getProperty(
                "oldSubmissionLimit");
        try
        {
          oldSubmissionLimit = Integer.valueOf(oldSubmissionLimitString);
          if(oldSubmissionLimit == 0)
          {
            submissionQuotaMessage = "";
          }
          else if(submissions.size() <= oldSubmissionLimit)
          {
            submissionQuotaMessage = getText(
                    "submissions.header.submissionQuota",
                    new String[]
                    {
                      Integer.toString(oldSubmissionLimit + 1)
                    });
          }
          else
          {
            submissionQuotaMessage = getText(
                    "submissions.header.submissionQuota",
                    new String[]
                    {
                      Integer.toString(oldSubmissionLimit + 1)
                    }) + " " + getText("submissions.header.submissionQuotaFull",
                            new String[]
                            {
                              Integer.toString(submissions.size())
                            });
          }
        }
        catch(NumberFormatException e)
        {
          oldSubmissionLimit = null;
          submissionQuotaMessage = getText(
                  "submissions.header.submissionQuotaUnlimited");
        }
      }
      catch(NoSuchItemException e)
      {
      }
      return SUCCESS;
    }

  }
}
