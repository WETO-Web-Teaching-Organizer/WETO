package fi.uta.cs.weto.actions.students;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ViewStudent extends WetoCourseAction
{
  public class DataRow
  {
    private final Integer taskId;
    private final String taskName;
    private final String[] latestSubmission;
    private final ArrayList<String[]> givenGrades;
    private final ArrayList<String[]> receivedGrades;
    private final ArrayList<String[]> permissions;

    public DataRow(Integer taskId, String taskName, String[] latestSubmission,
            ArrayList<String[]> givenGrades, ArrayList<String[]> receivedGrades,
            ArrayList<String[]> permissions)
    {
      this.taskId = taskId;
      this.taskName = taskName;
      this.latestSubmission = latestSubmission;
      this.givenGrades = givenGrades;
      this.receivedGrades = receivedGrades;
      this.permissions = permissions;
    }

    public Integer getTaskId()
    {
      return taskId;
    }

    public String getTaskName()
    {
      return taskName;
    }

    public String[] getLatestSubmission()
    {
      return latestSubmission;
    }

    public ArrayList<String[]> getGivenGrades()
    {
      return givenGrades;
    }

    public ArrayList<String[]> getReceivedGrades()
    {
      return receivedGrades;
    }

    public ArrayList<String[]> getPermissions()
    {
      return permissions;
    }

  }

  private Integer studentId;
  private StudentView student;
  private ArrayList<DataRow> dataRows;
  private boolean canWithdraw;
  private String withdrawLimit;

  public ViewStudent()
  {
    super(Tab.GRADING.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Integer courseTaskId = getCourseTaskId();
    // This step verifies that the student really is a student in the current course.
    UserTaskView.select1ByTaskIdAndUserIdAndClusterType(conn, courseTaskId,
            studentId, ClusterType.STUDENTS.getValue());
    Integer userId = getCourseUserId();
    if(!haveViewRights(Tab.GRADING.getBit() | Tab.SUBMISSIONS.getBit(), userId
            .equals(studentId), true))
    {
      throw new WetoActionException(getText("general.error.accessDenied"));
    }
    final boolean isTeacher = getNavigator().isTeacher();
    WetoTimeStamp[] withdrawLimits = PermissionModel.getTimeStampLimits(conn,
            userId, courseTaskId, PermissionType.WITHDRAW, isTeacher);
    int withdrawValue = PermissionModel.checkTimeStampLimits(withdrawLimits);
    // Is the current user allowed to withdraw the viewed student?
    canWithdraw = (withdrawValue == PermissionModel.CURRENT) && (isTeacher
            || userId.equals(studentId));
    withdrawLimit = (withdrawLimits[1] != null) ? withdrawLimits[1].toString()
                            : getText("general.header.forever").toLowerCase();
    try
    {
      student = StudentView.select1ByUserId(conn, studentId);
    }
    catch(NoSuchItemException e)
    {
      student = null;
    }
    ArrayList<SubtaskView> courseTaskList = new ArrayList<>();
    Task courseTask = Task.select1ById(conn, courseTaskId);
    SubtaskView courseTaskView = new SubtaskView();
    courseTaskView.setId(courseTaskId);
    courseTaskView.setRootTaskId(courseTaskId);
    courseTaskView.setContainerId(null);
    courseTaskView.setName(courseTask.getName());
    courseTaskView.setComponentBits(courseTask.getComponentBits());
    courseTaskView.setStatus(courseTask.getStatus());
    courseTaskView.setRank(0);
    courseTaskView.setText(courseTask.getText());
    courseTaskView.setShowTextInParent(courseTask.getShowTextInParent());
    courseTaskList.add(courseTaskView);
    courseTaskList.addAll(SubtaskView.selectByCourseTaskId(conn, courseTaskId));
    HashMap<Integer, ArrayList<Integer>> taskSubmissionGroupMap
                                         = new HashMap<>();
    HashSet<Integer> allSubmissionMembers = new HashSet<>();
    HashMap<Integer, ArrayList<SubtaskView>> subtaskListMap = new HashMap<>();
    for(SubtaskView subtask : courseTaskList)
    {
      Integer subtaskId = subtask.getId();
      ArrayList<SubtaskView> subtasks = new ArrayList<>();
      subtasks.add(subtask);
      subtaskListMap.put(subtaskId, subtasks);
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, subtaskId);
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      ArrayList<Integer> submissionMemberIds = SubmissionModel
              .getSubmissionGroupMemberIds(conn, subtaskId, studentId,
                      ignoreGroups);
      allSubmissionMembers.addAll(submissionMemberIds);
      taskSubmissionGroupMap.put(subtaskId, submissionMemberIds);
    }
    for(SubtaskView subtask : courseTaskList)
    {
      Integer fatherId = subtask.getContainerId();
      Integer subtaskId = subtask.getId();
      try
      {
        if((fatherId != null) && !subtaskId.equals(fatherId))
        {
          subtaskListMap.get(fatherId).add(subtask);
        }
      }
      catch(Exception e)
      {
        throw e;
      }
    }
    Integer taskId = getTaskId();
    ArrayList<Submission> courseSubmissions = new ArrayList<>();
    ArrayList<Grade> givenCourseGrades = new ArrayList<>();
    ArrayList<Grade> receivedCourseGrades = new ArrayList<>();
    for(Integer memberId : allSubmissionMembers)
    {
      courseSubmissions.addAll(Submission.selectByCourseTaskIdAndUserId(conn,
              courseTaskId, memberId));
      givenCourseGrades.addAll(Grade.selectByCourseTaskIdAndReviewerId(conn,
              courseTaskId, memberId));
      receivedCourseGrades.addAll(Grade.selectByCourseTaskIdAndReceiverId(conn,
              courseTaskId, memberId));
    }
    HashMap<Integer, Submission> submissionMap = new HashMap<>();
    for(Submission submission : courseSubmissions)
    {
      Integer submissionTaskId = submission.getTaskId();
      ArrayList<Integer> submissionMemberIds = taskSubmissionGroupMap.get(
              submissionTaskId);
      if(submissionMemberIds.contains(submission.getUserId()))
      {
        Submission taskSubmission = submissionMap.get(submissionTaskId);
        if((taskSubmission == null) || (taskSubmission.getTimeStamp()
                < submission.getTimeStamp()))
        {
          submissionMap.put(submissionTaskId, submission);
        }
      }
    }
    HashMap<Integer, ArrayList<Grade>> givenGradeListMap = new HashMap<>();
    for(Grade grade : givenCourseGrades)
    {
      Integer gradeTaskId = grade.getTaskId();
      ArrayList<Integer> submissionMemberIds = taskSubmissionGroupMap.get(
              gradeTaskId);
      if(submissionMemberIds.contains(grade.getReviewerId()))
      {
        ArrayList<Grade> taskGrades = givenGradeListMap.get(gradeTaskId);
        if(taskGrades == null)
        {
          taskGrades = new ArrayList<>();
          givenGradeListMap.put(gradeTaskId, taskGrades);
        }
        taskGrades.add(grade);
      }
    }
    HashMap<Integer, ArrayList<Grade>> receivedGradeListMap = new HashMap<>();
    for(Grade grade : receivedCourseGrades)
    {
      Integer gradeTaskId = grade.getTaskId();
      ArrayList<Integer> submissionMemberIds = taskSubmissionGroupMap.get(
              gradeTaskId);
      if(submissionMemberIds.contains(grade.getReceiverId()))
      {
        ArrayList<Grade> taskGrades = receivedGradeListMap.get(gradeTaskId);
        if(taskGrades == null)
        {
          taskGrades = new ArrayList<>();
          receivedGradeListMap.put(gradeTaskId, taskGrades);
        }
        taskGrades.add(grade);
      }
    }
    ArrayList<Permission> coursePermissions = Permission
            .selectByCourseTaskIdAndUserId(conn, courseTaskId, studentId);
    HashMap<Integer, ArrayList<Permission>> permissionListMap = new HashMap<>();
    for(Permission permission : coursePermissions)
    {
      Integer permissionTaskId = permission.getTaskId();
      ArrayList<Permission> taskPermissions = permissionListMap.get(
              permissionTaskId);
      if(taskPermissions == null)
      {
        taskPermissions = new ArrayList<>();
        permissionListMap.put(permissionTaskId, taskPermissions);
      }
      taskPermissions.add(permission);
    }
    dataRows = new ArrayList<>();
    ArrayDeque<Integer> dfsStack = new ArrayDeque<>();
    dfsStack.add(taskId);
    while(!dfsStack.isEmpty())
    {
      Integer dfsTaskId = dfsStack.removeLast();
      ArrayList<SubtaskView> subtaskList = subtaskListMap.get(dfsTaskId);
      SubtaskView dfsTask = subtaskList.get(0);
      if(dfsTask.getHasSubmissions() || dfsTask.getHasGrades())
      {
        String[] latestSubmission = null;
        Submission latest = submissionMap.get(dfsTaskId);
        if(latest != null)
        {
          SubmissionStatus status = SubmissionStatus.getStatus(latest
                  .getStatus());
          latestSubmission = new String[]
          {
            latest.getId().toString(),
            (status != null) ? status.getValue().toString() : "0",
            latest.getTimeStampString()
          };
        }
        ArrayList<String[]> givenGrades = new ArrayList<>();
        ArrayList<Grade> givenGradeList = givenGradeListMap.get(dfsTaskId);
        if(givenGradeList != null)
        {
          for(Grade grade : givenGradeList)
          {
            givenGrades.add(new String[]
            {
              grade.getId().toString(),
              grade.getStatus().toString(),
              (grade.getMark() != null) ? grade.getMark().toString() : "-",
              grade.getTimeStampString()
            });
          }
        }
        ArrayList<String[]> receivedGrades = new ArrayList<>();
        WetoTimeStamp[] resultsPeriod = PermissionModel.getTimeStampLimits(conn,
                userId, dfsTaskId, PermissionType.RESULTS);
        if(PermissionModel.checkTimeStampLimits(resultsPeriod)
                == PermissionModel.CURRENT)
        {
          ArrayList<Grade> receivedGradeList = receivedGradeListMap.get(
                  dfsTaskId);
          if(receivedGradeList != null)
          {
            for(Grade grade : receivedGradeList)
            {
              receivedGrades.add(new String[]
              {
                grade.getId().toString(),
                grade.getStatus().toString(),
                (grade.getMark() != null) ? grade.getMark().toString() : "-",
                grade.getTimeStampString()
              });
            }
          }
        }
        ArrayList<String[]> permissions = new ArrayList<>();
        ArrayList<Permission> taskPermissions = permissionListMap.get(dfsTaskId);
        if(taskPermissions != null)
        {
          for(Permission permission : taskPermissions)
          {
            permissions.add(new String[]
            {
              permission.getId().toString(),
              getText(PermissionType.getType(permission.getType()).getProperty()),
              permission.getStartTimeStampString(),
              permission.getEndTimeStampString()
            });
          }
        }
        dataRows.add(new DataRow(dfsTaskId, dfsTask.getName(), latestSubmission,
                givenGrades, receivedGrades, permissions));
      }
      for(int i = subtaskList.size() - 1; i > 0; i--)
      {
        dfsStack.add(subtaskList.get(i).getId());
      }
    }
    return SUCCESS;
  }

  public void setStudentId(Integer studentId)
  {
    this.studentId = studentId;
  }

  public StudentView getStudent()
  {
    return student;
  }

  public boolean isCanWithdraw()
  {
    return canWithdraw;
  }

  public String getWithdrawLimit()
  {
    return withdrawLimit;
  }

  public ArrayList<DataRow> getDataRows()
  {
    return dataRows;
  }

  public Integer getAnchorId()
  {
    return studentId;
  }

}
