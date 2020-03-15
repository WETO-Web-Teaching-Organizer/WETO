package fi.uta.cs.weto.actions.grading;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.GradingModel;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.PermissionRefType;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PeerReviewActions
{
  public static class Setup extends WetoTeacherAction
  {
    // Subtasks of the root task (root task itself is at index 0)
    private ArrayList<ReviewTask> subtasks;
    boolean hasPreviousGrades;
    // Beans storing a reviewee and his/her reviewers
    private ArrayList<ArrayList<ReviewerGroup>> allReviewersForSubmitters;
    // The same information as in reviewersForSubmitters but in concise form:
    // only user ids are included (this is enough for saving them to database)
    private String[] reviewInfoStrings = new String[0];

    private PeerReviewForm form = new PeerReviewForm();

    private String step;

    private HashSet<Integer> studentIdSet;
    private HashSet<Integer> reviewTaskIdSet;

    private Connection conn;
    private Integer taskId;

    public Setup()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), Tab.GRADING.getBit(),
              Tab.GRADING.getBit());
    }

    @Override
    public String action() throws Exception
    {
      String result = ERROR;
      conn = getCourseConnection();
      taskId = getTaskId();
      subtasks = new ArrayList<>();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      ReviewTask rt = new ReviewTask();
      if(form.getMinScore() == null)
      {
        form.setMinScore(scoring.getProperty("minScore", "0"));
      }
      if(form.getMaxScore() == null)
      {
        form.setMaxScore(scoring.getProperty("maxScore", "0"));
      }
      reviewTaskIdSet = new HashSet<>();
      if(!form.isOmitRootTaskGrades())
      {
        reviewTaskIdSet.add(taskId);
      }
      if(form.getCreateSubtaskGrades() != null)
      {
        for(Integer reviewTaskId : form.getCreateSubtaskGrades())
        {
          reviewTaskIdSet.add(reviewTaskId);
        }
      }
      rt.setId(taskId);
      rt.setName(getTask().getName());
      subtasks = new ArrayList<>();
      subtasks.add(rt);
      for(SubtaskView subtask : SubtaskView.selectByContainerId(conn, taskId))
      {
        rt = new ReviewTask();
        rt.setId(subtask.getId());
        rt.setName(subtask.getName());
        subtasks.add(rt);
      }
      if((step == null) || step.equals(getText("peerReview.header.backToSetup")))
      {
        result = settings();
      }
      else if(step.equals(getText("peerReview.header.preview")))
      {
        studentIdSet = getStudentIdSet();
        allReviewersForSubmitters = new ArrayList<>();
        for(ReviewTask reviewTask : subtasks)
        {
          if(reviewTaskIdSet.contains(reviewTask.getId()))
          {
            drawReviewers(reviewTask);
          }
        }
        result = "preview";
      }
      else if(step.equals(getText("peerReview.header.save")))
      {
        studentIdSet = getStudentIdSet();
        result = saveGroups();
        scoring.setProperty("minScore", form.getMinScore().toString());
        scoring.setProperty("maxScore", form.getMaxScore().toString());
        scoring.update(conn);
      }
      return result;
    }

    private String settings() throws Exception
    {
      hasPreviousGrades = false;
      if(!Grade.selectPeerVisibleByTaskId(conn, taskId).isEmpty())
      {
        hasPreviousGrades = true;
      }
      for(ReviewTask task : subtasks)
      {
        if(!Grade.selectPeerVisibleByTaskId(conn, task.getId()).isEmpty())
        {
          hasPreviousGrades = true;
        }
      }
      return "settings";
    }

    private HashSet<Integer> getStudentIdSet() throws SQLException
    {
      HashSet<Integer> studentIdSet = new HashSet<>();
      for(StudentView student : StudentView.selectByTaskId(conn, taskId))
      {
        studentIdSet.add(student.getUserId());
      }
      return studentIdSet;
    }

    private void drawReviewers(ReviewTask rt) throws Exception
    {
      Integer reviewTaskId = rt.getId();
      ArrayList<StudentView> submitters = StudentView.selectSubmittersByTaskId(
              conn, reviewTaskId);
      Map<Integer, ArrayList<Integer>> groupMap = new HashMap<>();
      ArrayList<UserGroup> peerReviewGroups = UserGroup
              .selectInheritedByTaskIdAndType(conn, reviewTaskId,
                      GroupType.REVIEW.getValue());
      Integer peerGroupTaskId = null;
      if(!peerReviewGroups.isEmpty())
      {
        peerGroupTaskId = peerReviewGroups.get(0).getTaskId();
      }
      for(StudentView student : submitters)
      {
        Integer submitterId = student.getUserId();
        boolean includeSubmitter = true;
        if(!form.isOverwritePreviousGrades())
        {
          ArrayList<Grade> grades = Grade
                  .selectPeerVisibleByTaskIdAndReceiverId(conn, reviewTaskId,
                          submitterId);
          for(Grade grade : grades)
          {
            if(studentIdSet.contains(grade.getReviewerId()))
            {
              includeSubmitter = false;
              break;
            }
          }
        }
        if(includeSubmitter)
        {
          Integer groupId = null;
          if(peerGroupTaskId != null)
          {
            try
            {
              GroupView gv = GroupView.select1ByTaskIdAndUserIdAndType(conn,
                      peerGroupTaskId, submitterId, GroupType.REVIEW.getValue());
              groupId = gv.getId();
            }
            catch(NoSuchItemException e)
            {
            }
          }
          if(!groupMap.containsKey(groupId))
          {
            groupMap.put(groupId, new ArrayList<Integer>());
          }
          groupMap.get(groupId).add(submitterId);
        }
      }
      int reviewerCount = form.getReviewerCount();
      if((reviewerCount < 1) && !form.isReviewOwnSubmission())
      {
        throw new WetoActionException(getText("peerReview.error.zeroReviews"),
                INPUT);
      }
      Set<Integer> groups = groupMap.keySet();
      Random rnd = new Random();
      final int sizeLimit = reviewerCount * (reviewerCount + 1) / 2;
      ArrayList<ReviewerGroup> reviewersForSubmitters = new ArrayList<>();
      for(Integer groupId : groups)
      {
        ArrayList<Integer> members = groupMap.get(groupId);
        if(members.size() <= reviewerCount)
        {
          throw new WetoActionException(getText("peerReview.error.smallGroup",
                  new String[]
                  {
                    String.valueOf(reviewerCount),
                    (groupId != null) ? groupId.toString() : "null",
                    String.valueOf(members.size())
                  }), INPUT);
        }
        // Create a random permutation of the members in the current group
        for(int i = 0; i < members.size(); ++i)
        {
          // Select a random item within 0...size-1-i and switch it to size-1-i
          int rand = rnd.nextInt(members.size() - i);
          Integer tmp = members.get(members.size() - 1 - i);
          members.set(members.size() - 1 - i, members.get(rand));
          members.set(rand, tmp);
        }
        // Now go through all members and set who reviews who
        for(int i = 0; i < members.size(); ++i)
        {
          Integer reviewee = members.get(i);
          // Create ReviewerGroup-object, with UserAccount-object for reviewee
          ReviewerGroup rg = new ReviewerGroup(reviewTaskId, rt.getName(),
                  UserAccount.select1ById(conn, reviewee));
          // Add reviewee as self-reviewer if also own work is reviewed
          if(form.isReviewOwnSubmission())
          {
            rg.addReviewer(UserAccount.select1ById(conn, reviewee));
          }
          // If there are enough members, then the reviewers are allocated non-
          // uniformly: the member with index i in the randomized member array
          // will be reviewed by members that are at indices i+1, i+3, i+6, and
          // so on. These are based on the arithmetic sum: i + 1, i + 1+2,
          // i + 1+2+3, ... The index wraps to 0 if it goes to members.size().
          if(members.size() > sizeLimit)
          {
            int sum = 0;
            for(int j = 1; j <= reviewerCount; ++j)
            {
              sum += j; // Update the arithmetic sum
              Integer reviewerId = members.get((i + sum) % members.size());
              rg.addReviewer(UserAccount.select1ById(conn, reviewerId));
            }
          }
          else // Otherwise use the simple uniform rule that the member i will be
          {    // reviewed by the members i+1, i+2, ..., i + reviewerCount.
            for(int j = 1; j <= reviewerCount; ++j)
            {
              Integer reviewerId = members.get((i + j) % members.size());
              rg.addReviewer(UserAccount.select1ById(conn, reviewerId));
            }
          }
          reviewersForSubmitters.add(rg);
        }
      }
      allReviewersForSubmitters.add(reviewersForSubmitters);
    }

    private String saveGroups() throws Exception
    {
      Map<Integer, Map<Integer, ArrayList<Integer>>> taskReviewerMaps
                                                             = new HashMap<>();
      for(String reviewInfo : reviewInfoStrings)
      {
        ArrayList<Integer> reviewers;
        String[] parts = reviewInfo.split(";", 2);
        String[] ids = parts[1].split("=", 2);
        Integer reviewTaskId = Integer.valueOf(parts[0]);
        Map<Integer, ArrayList<Integer>> taskReviewerMap = taskReviewerMaps.get(
                reviewTaskId);
        if(taskReviewerMap == null)
        {
          taskReviewerMap = new HashMap<>();
          taskReviewerMaps.put(reviewTaskId, taskReviewerMap);
        }
        Integer revieweeId = Integer.valueOf(ids[0]);
        Integer reviewerId = Integer.valueOf(ids[1]);
        if((reviewers = taskReviewerMap.get(revieweeId)) == null)
        {
          reviewers = new ArrayList<>();
          taskReviewerMap.put(revieweeId, reviewers);
        }
        reviewers.add(reviewerId);
      }
      for(Integer reviewTaskId : taskReviewerMaps.keySet())
      {
        Map<Integer, ArrayList<Integer>> taskReviewerMap = taskReviewerMaps.get(
                reviewTaskId);
        for(Integer reviewee : taskReviewerMap.keySet())
        {
          // Delete the old grade(s) if this was requested by the teacher
          if(form.isOverwritePreviousGrades())
          {
            ArrayList<Grade> grades = Grade
                    .selectPeerVisibleByTaskIdAndReceiverId(conn, reviewTaskId,
                            reviewee);
            for(Grade grade : grades)
            {
              if(studentIdSet.contains(grade.getReviewerId()))
              {
                GradingModel.deleteGrade(conn, grade);
              }
            }
          }
          // Now create new grades and review tags for the specified reviewee
          // and reviewers
          for(Integer reviewer : taskReviewerMap.get(reviewee))
          {
            // Create the grade
            Grade grade = new Grade();
            grade.setReviewerId(reviewer);
            grade.setReceiverId(reviewee);
            grade.setTaskId(reviewTaskId);
            grade.setStatus(GradeStatus.UNSPECIFIED.getValue());
            grade.insert(conn);
            // Create a tag for for this grade and set the reviewer as author
            Tag tag = new Tag();
            tag.setAuthorId(reviewer);
            tag.setTaggedId(grade.getId());
            tag.setType(TagType.REVIEW.getValue());
            tag.setRank(0);
            tag.insert(conn);
          }
        }
      }
      // Create time permissions (for peer reviewing)?
      if(form.isCreatePermissions() && !form.getPermissionDate().isEmpty())
      {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm");
        Calendar gc = new GregorianCalendar();
        gc.setTime(dateFormatter.parse(form.getPermissionDate()));
        WetoTimeStamp limit = new WetoTimeStamp(gc);
        WetoTimeStamp now = new WetoTimeStamp();
        // Enable grading from now until the specified time limit
        Permission gradingPermission = new Permission();
        gradingPermission.setType(PermissionType.GRADING.getValue());
        gradingPermission.setTaskId(taskId);
        gradingPermission.setUserRefType(PermissionRefType.USER.getValue());
        gradingPermission.setStartDate(now.getTimeStamp());
        gradingPermission.setEndDate(limit.getTimeStamp());
        gradingPermission.insert(conn);
        // Enable viewing results after the specified time limit has passed
        Permission resultsPermission = new Permission();
        resultsPermission.setType(PermissionType.RESULTS.getValue());
        resultsPermission.setTaskId(taskId);
        resultsPermission.setUserRefType(PermissionRefType.USER.getValue());
        resultsPermission.setStartDate(limit.getTimeStamp());
        resultsPermission.insert(conn);
      }
      addActionMessage(getText("peerReview.message.success"));
      return SUCCESS;
    }

    public ArrayList<ReviewTask> getSubtasks()
    {
      return subtasks;
    }

    public boolean isHasPreviousGrades()
    {
      return hasPreviousGrades;
    }

    public PeerReviewForm getForm()
    {
      return form;
    }

    public void setStep(String step)
    {
      this.step = step;
    }

    public ArrayList<ArrayList<ReviewerGroup>> getAllReviewersForSubmitters()
    {
      return allReviewersForSubmitters;
    }

    public void setReviewInfoStrings(String[] reviewInfoStrings)
    {
      this.reviewInfoStrings = reviewInfoStrings;
    }

  }

  public static class ReviewerGroup
  {
    // The id and name of the reviewed task
    Integer taskId;
    String taskName;
    // The user whose submission(s) are reviewed
    UserAccount reviewee;
    // Reviewer ids
    ArrayList<UserAccount> reviewers;

    public ReviewerGroup(Integer taskId, String taskName, UserAccount reviewee)
    {
      this.taskId = taskId;
      this.taskName = taskName;
      this.reviewee = reviewee;
      reviewers = new ArrayList<>();
    }

    public String toString()
    {
      return "ReviewerGroup";
    }

    public void addReviewer(UserAccount user)
    {
      reviewers.add(user);
    }

    public Integer getTaskId()
    {
      return taskId;
    }

    public void setTaskId(Integer taskId)
    {
      this.taskId = taskId;
    }

    public String getTaskName()
    {
      return taskName;
    }

    public void setTaskName(String taskName)
    {
      this.taskName = taskName;
    }

    public UserAccount getReviewee()
    {
      return reviewee;
    }

    public void setReviewee(UserAccount reviewee)
    {
      this.reviewee = reviewee;
    }

    public ArrayList<UserAccount> getReviewers()
    {
      return reviewers;
    }

    public void setReviewers(ArrayList<UserAccount> reviewers)
    {
      this.reviewers = reviewers;
    }

  }

  public static class ReviewTask
  {
    private Integer id;
    private String name;
    private Integer submitterCount;
    private Float minScore;
    private Float maxScore;

    public Integer getId()
    {
      return id;
    }

    public void setId(Integer id)
    {
      this.id = id;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public Integer getSubmitterCount()
    {
      return submitterCount;
    }

    public void setSubmitterCount(Integer submitterCount)
    {
      this.submitterCount = submitterCount;
    }

    public Float getMinScore()
    {
      return minScore;
    }

    public void setMinScore(Float minScore)
    {
      this.minScore = minScore;
    }

    public Float getMaxScore()
    {
      return maxScore;
    }

    public void setMaxScore(Float maxScore)
    {
      this.maxScore = maxScore;
    }

  }

  public static class PeerReviewForm
  {
    // The number of reviewers for one submission; default value is 1
    private int reviewerCount = 1;
    // Run the peer-review for rootTask also
    private boolean omitRootTaskGrades;
    // Should the users review their own submissions also
    private boolean reviewOwnSubmission;
    // Overwrite previous grades for root task and subtask if any grades exist
    private boolean overwritePreviousGrades;
    // Create grading and results permissions
    private boolean createPermissions;
    // Threshold date for permissions (grading ends and results begins)
    private String permissionDate = "";
    // List of subtasks that should be included in peer reviewing
    private HashSet<Integer> createSubtaskGrades;
    // Grading minimum and maximum scores (same as via grading properties).
    private Float minScore;
    private Float maxScore;

    public Integer getReviewerCount()
    {
      return reviewerCount;
    }

    public void setReviewerCount(Integer reviewerCount)
    {
      this.reviewerCount = reviewerCount;
    }

    public boolean isOmitRootTaskGrades()
    {
      return omitRootTaskGrades;
    }

    public void setOmitRootTaskGrades(boolean omitRootTaskGrades)
    {
      this.omitRootTaskGrades = omitRootTaskGrades;
    }

    public boolean isReviewOwnSubmission()
    {
      return reviewOwnSubmission;
    }

    public void setReviewOwnSubmission(boolean reviewOwnSubmission)
    {
      this.reviewOwnSubmission = reviewOwnSubmission;
    }

    public boolean isOverwritePreviousGrades()
    {
      return overwritePreviousGrades;
    }

    public void setOverwritePreviousGrades(boolean overwritePreviousGrades)
    {
      this.overwritePreviousGrades = overwritePreviousGrades;
    }

    public boolean isCreatePermissions()
    {
      return createPermissions;
    }

    public void setCreatePermissions(boolean createPermissions)
    {
      this.createPermissions = createPermissions;
    }

    public String getPermissionDate()
    {
      return permissionDate;
    }

    public void setPermissionDate(String permissionDate)
    {
      this.permissionDate = permissionDate;
    }

    public HashSet<Integer> getCreateSubtaskGrades()
    {
      return createSubtaskGrades;
    }

    public void setCreateSubtaskGrades(Integer[] createSubtaskGrades)
    {
      this.createSubtaskGrades = new HashSet<>();
      for(Integer subtaskId : createSubtaskGrades)
      {
        this.createSubtaskGrades.add(subtaskId);
      }
    }

    public Float getMinScore()
    {
      return minScore;
    }

    public void setMinScore(String minScore)
    {
      if(minScore != null && !minScore.isEmpty())
      {
        this.minScore = Float.parseFloat(minScore);
      }
      else
      {
        this.minScore = 0.0F;
      }
    }

    public Float getMaxScore()
    {
      return maxScore;
    }

    public void setMaxScore(String maxScore)
    {
      if(maxScore != null && !maxScore.isEmpty())
      {
        this.maxScore = Float.parseFloat(maxScore);
      }
      else
      {
        this.maxScore = 0.0F;
      }
    }

  }

}
