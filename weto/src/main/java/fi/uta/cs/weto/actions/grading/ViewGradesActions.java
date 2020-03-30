package fi.uta.cs.weto.actions.grading;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.CSVReview;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.GradingModel;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.InstructionBean;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ViewGradesActions
{
  public static class SelectView extends WetoCourseAction
  {
    public SelectView()
    {
      super(Tab.GRADING.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      if(isLeafTask())
      {
        if(getNavigator().isTeacher())
        {
          return "teacherLeafGrades";
        }
        else
        {
          // Add the view grading event to the log.
          if(!getNavigator().isStudentRole())
          {
            new Log(getCourseTaskId(), getTaskId(), getCourseUserId(),
                    LogEvent.VIEW_GRADING.getValue(), null, null, getRequest()
                    .getRemoteAddr()).insert(getCourseConnection());
          }
          return "studentLeafGrades";
        }
      }
      else
      {
        return "nodeGrades";
      }
    }

  }

  public static class NodeGrades extends WetoCourseAction
  {
    private String[] gradingPeriod;
    private boolean gradingPeriodActive;
    private String[] resultsPeriod;
    private boolean resultsPeriodActive;
    private ArrayList<StudentView> students;
    private ArrayList<String[]> gradeTable;
    private ArrayList<ArrayList<Float>> grades;
    private ArrayList<Task> tasks;
    private int studentsCount;
    private Map<Integer, String> groupMembers;
    private Map<Integer, String> groupList;
    private Integer filterGroupId = -1;
    private Integer validGradeCount = 0;

    public NodeGrades()
    {
      super(Tab.GRADING.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      Integer taskId = getTaskId();
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] gradingLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.GRADING);
      gradingPeriodActive = (PermissionModel.checkTimeStampLimits(gradingLimits)
              == PermissionModel.CURRENT);
      gradingPeriod = WetoTimeStamp.limitsToStrings(gradingLimits);
      WetoTimeStamp[] resultsLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.RESULTS);
      resultsPeriodActive = (PermissionModel.checkTimeStampLimits(resultsLimits)
              == PermissionModel.CURRENT);
      resultsPeriod = WetoTimeStamp.limitsToStrings(resultsLimits);
      tasks = new ArrayList<>();
      tasks.add(getTask());
      for(Task gradeTask : GradingModel.getGradableSubtasks(conn, tasks.get(0)))
      {
        if(!gradeTask.getIsHidden())
        {
          Integer subtaskId = gradeTask.getId();
          WetoTimeStamp[] viewLimits = PermissionModel.getTimeStampLimits(conn,
                  userIP, userId, subtaskId, PermissionType.VIEW);
          resultsLimits = PermissionModel.getTimeStampLimits(conn, userIP,
                  userId, subtaskId, PermissionType.RESULTS);
          if((PermissionModel.checkTimeStampLimits(viewLimits)
                  == PermissionModel.CURRENT) && (PermissionModel
                          .checkTimeStampLimits(resultsLimits)
                  == PermissionModel.CURRENT))
          {
            tasks.add(gradeTask);
          }
        }
      }
      if(haveViewRights(Tab.GRADING.getBit(), false, true))
      {
        ArrayList<StudentView> allStudents = StudentView.selectByTaskId(conn,
                taskId);
        ArrayList<UserGroup> groups = UserGroup
                .selectInheritedByTaskIdAndNotType(conn, taskId,
                        GroupType.SUBMISSION.getValue());
        groupList = new HashMap<>();
        groupList.put(-1, "-");
        for(UserGroup userGroup : groups)
        {
          groupList.put(userGroup.getId(), userGroup.getName());
        }
        if((filterGroupId != null) && (filterGroupId != -1))
        {
          Integer groupTaskId = groups.isEmpty() ? taskId : groups.get(0)
                  .getTaskId();
          ArrayList<GroupMember> filterMembers = GroupMember
                  .selectByTaskIdAndGroupId(conn, groupTaskId, filterGroupId);
          HashSet<Integer> groupMemberSet = new HashSet<>();
          for(GroupMember member : filterMembers)
          {
            groupMemberSet.add(member.getUserId());
          }
          students = new ArrayList<>();
          for(StudentView member : allStudents)
          {
            if(groupMemberSet.contains(member.getUserId()))
            {
              students.add(member);
            }
          }
        }
        else
        {
          students = allStudents;
        }
      }
      else
      {
        students = new ArrayList<>();
        if(haveViewRights(Tab.GRADING.getBit(), true, false))
        {
          try
          {
            students.add(StudentView.select1ByUserId(conn, userId));
          }
          catch(NoSuchItemException e)
          {
          }
        }
      }
      grades = GradingModel.retrieveAggregateGrades(conn, tasks, students);
      Map<Integer, String> tmpMembers = new HashMap<>();
      ArrayList<GroupView> members = GroupView
              .selectInheritedByTaskIdAndNotType(conn, taskId,
                      GroupType.SUBMISSION.getValue());
      for(GroupView gv : members)
      {
        tmpMembers.put(gv.getUserId(), gv.getName().split(";")[0]);
      }
      groupMembers = new HashMap<>();
      int i = 0;
      for(StudentView student : students)
      {
        if((grades.get(i).size() > 0) && (grades.get(i).get(0) != null))
        {
          validGradeCount += 1;
        }
        i += 1;
        String groupName = tmpMembers.get(student.getUserId());
        if(groupName == null)
        {
          // Student does not belong to a group. This is marked by "-".
          groupMembers.put(student.getUserId(), "-");
        }
        else
        {
          groupMembers.put(student.getUserId(), groupName);
        }
      }
      gradeTable = new ArrayList<>();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      String gradeTableString = scoring.getGradeTable();
      if(gradeTableString != null && !gradeTableString.isEmpty())
      {
        String[] scoreMarkStrings = gradeTableString.split(";", 0);
        for(String scoreMarkString : scoreMarkStrings)
        {
          gradeTable.add(scoreMarkString.split("=", 2));
        }
      }
      studentsCount = students.size();
      return SUCCESS;
    }

    public String[] getGradingPeriod()
    {
      return gradingPeriod;
    }

    public boolean isGradingPeriodActive()
    {
      return gradingPeriodActive;
    }

    public String[] getResultsPeriod()
    {
      return resultsPeriod;
    }

    public boolean isResultsPeriodActive()
    {
      return resultsPeriodActive;
    }

    public ArrayList<StudentView> getStudents()
    {
      return students;
    }

    public ArrayList<String[]> getGradeTable()
    {
      return gradeTable;
    }

    public ArrayList<ArrayList<Float>> getGrades()
    {
      return grades;
    }

    public ArrayList<Task> getTasks()
    {
      return tasks;
    }

    public int getStudentsCount()
    {
      return studentsCount;
    }

    public Map<Integer, String> getGroupMembers()
    {
      return groupMembers;
    }

    public Map<Integer, String> getGroupList()
    {
      return groupList;
    }

    public Integer getFilterGroupId()
    {
      return filterGroupId;
    }

    public void setFilterGroupId(Integer filterGroupId)
    {
      this.filterGroupId = filterGroupId;
    }

    public Integer getValidGradeCount()
    {
      return validGradeCount;
    }

  }

  public static class LeafGrades extends WetoCourseAction
  {
    private String[] gradingPeriod;
    private String[] resultsPeriod;
    private String[] challengePeriod;
    private boolean gradingPeriodActive;
    private boolean resultsPeriodActive;
    private boolean challengePeriodActive;
    private ArrayList<Grade> givenGrades;
    private ArrayList<Grade> receivedGrades;

    /* new */
    private Map<Integer, CSVReview> givenReviewsMap;
    private Map<Integer, ArrayList<Document>> givenReviewDocuments;
    private ArrayList<Document> mySubmittedDocuments;
    private Map<Integer, CSVReview> receivedReviewsMap;
    private ArrayList<InstructionBean> reviewInstructions;
    private Scoring scoring;
    /* /new */

    private ArrayList<String[]> gradeTable;
    private Map<Integer, ArrayList<Grade>> studentsGradesMap;
    private ArrayList<UserTaskView> receiversList;
    private Map<Integer, UserTaskView> visibleMembersMap;
    private boolean studentView;
    private int fillerMax;
    private Integer[] fillerTable;
    private Map<Integer, String> groupMembers;
    private boolean createRights;
    private Map<Integer, String> groupList;
    private Integer filterGroupId = -1;
    private String[] submitterGroups;
    private Integer validGradeCount = 0;

    public LeafGrades()
    {
      super(Tab.GRADING.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      Integer taskId = getTaskId();
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] gradingLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.GRADING);
      gradingPeriodActive = (PermissionModel.checkTimeStampLimits(gradingLimits)
              == PermissionModel.CURRENT);
      gradingPeriod = WetoTimeStamp.limitsToStrings(gradingLimits);
      WetoTimeStamp[] resultsLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.RESULTS);
      resultsPeriodActive = (PermissionModel.checkTimeStampLimits(resultsLimits)
              == PermissionModel.CURRENT);
      resultsPeriod = WetoTimeStamp.limitsToStrings(resultsLimits);
      WetoTimeStamp[] challengeLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, taskId, PermissionType.GRADE_CHALLENGE);
      challengePeriodActive = (PermissionModel.checkTimeStampLimits(
              challengeLimits) == PermissionModel.CURRENT);
      challengePeriod = WetoTimeStamp.limitsToStrings(challengeLimits);
      Map<Integer, UserTaskView> allMembersMap = new HashMap<>();
      ArrayList<UserTaskView> allMembersList = UserTaskView.selectByTaskId(
              conn, taskId);
      for(UserTaskView member : allMembersList)
      {
        allMembersMap.put(member.getUserId(), member);
      }
      studentView = true;
      studentsGradesMap = new HashMap<>();
      receiversList = new ArrayList<>();
      createRights = false;
      scoring = Scoring.select1ByTaskId(conn, taskId);
      boolean ignoreGroups = Boolean.parseBoolean(scoring.getProperty(
              "ignoreGroups"));
      int timeStamp = new WetoTimeStamp().getTimeStamp();
      ArrayList<Integer> gradeMemberIds = null;
      // Can view all users' grades?
      if(haveViewRights(Tab.GRADING.getBit(), false, true))
      {
        studentView = false;
        if(haveCreateRights(Tab.GRADING.getBit(), false, true))
        {
          createRights = true;
        }
        ArrayList<Grade> taskGrades = Grade.selectByTaskId(conn, taskId);
        Collections.sort(taskGrades, new Comparator<Grade>()
        {
          @Override
          public int compare(Grade a, Grade b)
          {
            if(GradeStatus.AGGREGATE.getValue().equals(a.getStatus())
                    && !GradeStatus.AGGREGATE.getValue().equals(b.getStatus()))
            {
              return -1;
            }
            else if(GradeStatus.AGGREGATE.getValue().equals(b.getStatus())
                    && !GradeStatus.AGGREGATE.getValue().equals(a.getStatus()))
            {
              return 1;
            }
            else
            {
              return b.getTimeStamp().compareTo(a.getTimeStamp());
            }
          }

        });
        for(Grade grade : taskGrades)
        {
          Integer receiverId = grade.getReceiverId();
          ArrayList<Grade> studentGrades = studentsGradesMap.get(receiverId);
          if(studentGrades == null)
          {
            studentGrades = new ArrayList<>();
            studentsGradesMap.put(receiverId, studentGrades);
            if(!GradeStatus.AGGREGATE.getValue().equals(grade.getStatus()))
            {
              Grade aggregateGrade = new Grade();
              aggregateGrade.setStatus(GradeStatus.AGGREGATE.getValue());
              aggregateGrade.setMark(null);
              aggregateGrade.setTaskId(taskId);
              aggregateGrade.setReceiverId(receiverId);
              aggregateGrade.setReviewerId(null);
              aggregateGrade.setTimeStamp(timeStamp);
              studentGrades.add(aggregateGrade);
            }
          }
          studentGrades.add(grade);
        }
        ArrayList<UserGroup> groups = UserGroup
                .selectInheritedByTaskIdAndNotType(conn, taskId,
                        GroupType.SUBMISSION.getValue());
        groupList = new HashMap<>();
        groupList.put(-1, "-");
        for(UserGroup userGroup : groups)
        {
          groupList.put(userGroup.getId(), userGroup.getName());
        }
        if((filterGroupId != null) && (filterGroupId != -1))
        {
          Integer groupTaskId = groups.isEmpty() ? taskId : groups.get(0)
                  .getTaskId();
          ArrayList<GroupMember> filterMembers = GroupMember
                  .selectByTaskIdAndGroupId(conn, groupTaskId, filterGroupId);
          HashSet<Integer> groupMemberSet = new HashSet<>();
          for(GroupMember member : filterMembers)
          {
            groupMemberSet.add(member.getUserId());
          }
          for(UserTaskView member : allMembersList)
          {
            if(groupMemberSet.contains(member.getUserId())
                    && ClusterType.STUDENTS.getValue().equals(member
                            .getClusterType()))
            {
              receiversList.add(member);
            }
          }
        }
        else
        {
          for(UserTaskView member : allMembersList)
          {
            if(ClusterType.STUDENTS.getValue()
                    .equals(member.getClusterType()))
            {
              receiversList.add(member);
            }
          }
        }
        visibleMembersMap = allMembersMap;
      }
      // Can view own grades?
      else if(haveViewRights(Tab.GRADING.getBit(), true, false))
      {
        gradeMemberIds = SubmissionModel
                .getSubmissionGroupMemberIds(conn, taskId, userId, ignoreGroups);
        if(resultsPeriodActive)
        {
          receivedGrades = new ArrayList<>();
          ArrayList<Grade> aggregateGrades = Grade
                  .selectAggregateByTaskIdAndReceiverId(conn, taskId, userId);
          if(aggregateGrades.isEmpty())
          {
            Grade aggregateGrade = new Grade();
            aggregateGrade.setStatus(GradeStatus.AGGREGATE.getValue());
            aggregateGrade.setMark(null);
            aggregateGrade.setTaskId(taskId);
            aggregateGrade.setReceiverId(userId);
            aggregateGrade.setReviewerId(null);
            aggregateGrade.setTimeStamp(timeStamp);
            receivedGrades.add(aggregateGrade);
          }
          else
          {
            receivedGrades.add(aggregateGrades.get(0));
          }
          for(Integer memberId : gradeMemberIds)
          {
            receivedGrades.addAll(Grade
                    .selectVisibleNonaggregateByTaskIdAndReceiverId(conn, taskId,
                            memberId));
          }
          studentsGradesMap.put(userId, receivedGrades);
        }
        givenGrades = new ArrayList<>();
        for(Integer memberId : gradeMemberIds)
        {
          givenGrades.addAll(Grade.selectVisibleByTaskIdAndReviewerId(conn,
                  taskId, memberId));
        }
        for(Grade grade : givenGrades)
        {
          if(!grade.getReceiverId().equals(userId))
          {
            ArrayList<Grade> studentGrades = studentsGradesMap.get(grade
                    .getReceiverId());
            if(studentGrades == null)
            {
              studentGrades = new ArrayList<>();
              studentsGradesMap.put(grade.getReceiverId(), studentGrades);
            }
            studentGrades.add(grade);
          }
        }
        visibleMembersMap = new HashMap<>();
        receiversList.add(allMembersMap.get(userId));
        for(UserTaskView member : allMembersList)
        {
          if(studentsGradesMap.get(member.getUserId()) != null
                  && !member.getUserId().equals(userId))
          {
            receiversList.add(member);
          }
          if(member.getUserId().equals(userId) || !ClusterType.STUDENTS
                  .getValue().equals(member.getClusterType()))
          {
            visibleMembersMap.put(member.getUserId(), member);
          }
        }
      }
      else
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ACCESS_DENIED);
      }
      fillerMax = 0;
      for(UserTaskView receiver : receiversList)
      {
        ArrayList<Grade> grades = studentsGradesMap.get(receiver.getUserId());
        if(grades != null)
        {
          if(grades.size() > fillerMax + 1)
          {
            fillerMax = grades.size() - 1;
          }
          if(!grades.isEmpty() && (grades.get(0).getMark() != null))
          {
            validGradeCount += 1;
          }
        }
      }
      ArrayList<GroupView> submitterGroupList = GroupView
              .selectInheritedByTaskIdAndType(conn, taskId, GroupType.SUBMISSION
                      .getValue());
      HashMap<Integer, GroupView> submitterMap = new HashMap<>();
      for(GroupView gv : submitterGroupList)
      {
        submitterMap.put(gv.getUserId(), gv);
      }
      groupMembers = new HashMap<>();
      fillerTable = new Integer[receiversList.size()];
      submitterGroups = new String[receiversList.size()];
      int i = 0;
      for(UserTaskView receiver : receiversList)
      {
        ArrayList<Grade> grades = studentsGradesMap.get(receiver.getUserId());
        int size = ((grades != null) && !grades.isEmpty()) ? grades.size() - 1
                           : 0;
        fillerTable[i] = fillerMax - size;
        groupMembers.put(receiver.getUserId(), "-");
        GroupView submitterGroup = submitterMap.get(receiver.getUserId());
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
      ArrayList<GroupView> members = GroupView
              .selectInheritedByTaskIdAndNotType(conn, taskId,
                      GroupType.SUBMISSION.getValue());
      for(GroupView gv : members)
      {
        groupMembers.put(gv.getUserId(), gv.getName().split(";")[0]);
      }
      gradeTable = new ArrayList<>();
      String gradeTableString = scoring.getGradeTable();
      if(gradeTableString != null && !gradeTableString.isEmpty())
      {
        String[] scoreMarkStrings = gradeTableString.split(";", 0);
        for(String scoreMarkString : scoreMarkStrings)
        {
          gradeTable.add(scoreMarkString.split("=", 2));
        }
      }
      // Make map for received review texts
      receivedReviewsMap = new HashMap<>();
      if(receivedGrades != null && resultsPeriodActive)
      {
        for(Grade grade : receivedGrades)
        {
          CSVReview review = CSVReview.readAllForGrade(conn, grade);
          if(review != null)
          {
            receivedReviewsMap.put(grade.getId(), review);
          }
        }
      }
      // Do the same for given reviews
      givenReviewsMap = new HashMap<>();
      if(givenGrades != null)
      {
        for(Grade grade : givenGrades)
        {
          CSVReview review = CSVReview.read1ForGradeAndSubmissionGroup(conn,
                  grade, gradeMemberIds);
          if(review != null)
          {
            // Add <gradeid, Review> -object to map
            givenReviewsMap.put(grade.getId(), review);
          }
        }
      }
      // Get task related documents for receivers and self
      Task task = getTask();
      mySubmittedDocuments = new ArrayList<>();
      givenReviewDocuments = new HashMap<>();
      if(task.getHasSubmissions() && (gradeMemberIds != null))
      {
        for(Integer memberId : gradeMemberIds)
        {
          try
          {
            Submission mySubmission = Submission
                    .select1LatestSubmissionByUserIdAndTaskId(conn, memberId,
                            taskId);
            mySubmittedDocuments = Document.selectBySubmissionId(conn,
                    mySubmission.getId());
            break;
          }
          catch(NoSuchItemException e)
          {
          }
        }
        // Get documents for each review by self
        if(givenGrades != null)
        {
          for(Grade grade : givenGrades)
          {
            int gradeId = grade.getId();
            int receiverId = grade.getReceiverId();
            try
            {
              Submission receivedSubmission = Submission
                      .select1LatestSubmissionByUserIdAndTaskId(conn, receiverId,
                              taskId);
              ArrayList<Document> receivedDocs = Document
                      .selectBySubmissionId(conn, receivedSubmission.getId());
              givenReviewDocuments.put(gradeId, receivedDocs);
            }
            // Don't react to noSuchItemException, since sometimes there aren't submissions available (yet)
            catch(NoSuchItemException e)
            {
            }
          }
        }
      }
      // Get review instructions
      if(getTask().getHasReviewInstructions())
      {
        reviewInstructions = ReviewInstructionsActions
                .getInstructionsBeans(conn, taskId);
      }
      return SUCCESS;
    }

    public Scoring getScoring()
    {
      return scoring;
    }

    public Boolean getHasReviewInstructions()
    {
      return getTask().getHasReviewInstructions();
    }

    public ArrayList<InstructionBean> getReviewInstructions()
    {
      return reviewInstructions;
    }

    public String[] getGradingPeriod()
    {
      return gradingPeriod;
    }

    public String[] getResultsPeriod()
    {
      return resultsPeriod;
    }

    public String[] getChallengePeriod()
    {
      return challengePeriod;
    }

    public boolean isGradingPeriodActive()
    {
      return gradingPeriodActive;
    }

    public boolean isResultsPeriodActive()
    {
      return resultsPeriodActive;
    }

    public boolean isChallengePeriodActive()
    {
      return challengePeriodActive;
    }

    public ArrayList<Grade> getGivenGrades()
    {
      return givenGrades;
    }

    public ArrayList<Grade> getReceivedGrades()
    {
      return receivedGrades;
    }

    public ArrayList<String[]> getGradeTable()
    {
      return gradeTable;
    }

    public Map<Integer, ArrayList<Grade>> getStudentsGradesMap()
    {
      return studentsGradesMap;
    }

    public ArrayList<UserTaskView> getReceiversList()
    {
      return receiversList;
    }

    public Map<Integer, UserTaskView> getVisibleMembersMap()
    {
      return visibleMembersMap;
    }

    public boolean isStudentView()
    {
      return studentView;
    }

    public int getFillerMax()
    {
      return fillerMax;
    }

    public Integer[] getFillerTable()
    {
      return fillerTable;
    }

    public Map<Integer, String> getGroupMembers()
    {
      return groupMembers;
    }

    public boolean isCreateRights()
    {
      return createRights;
    }

    public Map<Integer, String> getGroupList()
    {
      return groupList;
    }

    public Integer getFilterGroupId()
    {
      return filterGroupId;
    }

    public void setFilterGroupId(Integer filterGroupId)
    {
      this.filterGroupId = filterGroupId;
    }

    public String[] getSubmitterGroups()
    {
      return submitterGroups;
    }

    public Map<Integer, ArrayList<Document>> getGivenReviewDocuments()
    {
      return givenReviewDocuments;
    }

    public void setGivenReviewDocuments(
            Map<Integer, ArrayList<Document>> givenReviewDocuments)
    {
      this.givenReviewDocuments = givenReviewDocuments;
    }

    public Map<Integer, CSVReview> getGivenReviewsMap()
    {
      return givenReviewsMap;
    }

    public void setGivenReviewsMap(Map<Integer, CSVReview> givenReviewsMap)
    {
      this.givenReviewsMap = givenReviewsMap;
    }

    public ArrayList<Document> getMySubmittedDocuments()
    {
      return mySubmittedDocuments;
    }

    public void setMySubmittedDocuments(ArrayList<Document> mySubmittedDocuments)
    {
      this.mySubmittedDocuments = mySubmittedDocuments;
    }

    public Map<Integer, CSVReview> getReceivedReviewsMap()
    {
      return receivedReviewsMap;
    }

    public void setReceivedReviewsMap(Map<Integer, CSVReview> receivedReviewsMap)
    {
      this.receivedReviewsMap = receivedReviewsMap;
    }

    public Integer getValidGradeCount()
    {
      return validGradeCount;
    }

  }
}
