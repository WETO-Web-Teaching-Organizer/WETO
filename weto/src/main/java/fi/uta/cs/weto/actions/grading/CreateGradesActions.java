package fi.uta.cs.weto.actions.grading;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.GradingModel;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CreateGradesActions
{
  public static class Calculate extends WetoTeacherAction
  {
    private boolean submitted;

    public Calculate()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), Tab.GRADING.getBit(), 0);
      submitted = false;
    }

    @Override
    public String action() throws Exception
    {
      if(!submitted)
      {
        return INPUT;
      }
      else
      {
        Connection conn = getCourseConnection();
        Integer courseTaskId = getNavigator().getCourseTaskId();
        Task task = Task.select1ById(conn, courseTaskId);
        GradingModel.calculateGrades(conn, task, UserTaskView
                .selectByTaskIdAndClusterType(conn, courseTaskId,
                        ClusterType.STUDENTS.getValue()));
        addActionMessage(getText("grading.message.calculateSuccess"));
        return SUCCESS;
      }

    }

    public void setSubmitted(boolean submitted)
    {
      this.submitted = submitted;
    }

  }

  public static class Generate extends WetoTeacherAction
  {
    private Connection conn;
    private Integer userId;
    private Integer taskId;

    private boolean submitted;

    private boolean overwriteExisting;
    private boolean gradeSubmissionsOnly;
    private boolean gradePeerReviewersOnly;
    private Float defaultMark;

    public Generate()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), Tab.GRADING.getBit(), 0);
      submitted = false;
    }

    @Override
    public String action() throws Exception
    {
      String result = SUCCESS;
      conn = getCourseConnection();
      taskId = getTaskId();
      userId = getCourseUserId();
      if(!submitted)
      {
        result = INPUT;
      }
      else
      {
        ArrayDeque<Integer> taskQueue = new ArrayDeque<>();
        taskQueue.addLast(taskId);
        while(!taskQueue.isEmpty())
        {
          Integer queueTaskId = taskQueue.removeFirst();
          ArrayList<Integer> subtaskIds = Task.selectSubtaskIds(conn,
                  queueTaskId);
          if(subtaskIds.isEmpty())
          {
            if(!gradePeerReviewersOnly)
            {
              AutoGrading ag = null;
              ArrayList<Tag> quizScores = new ArrayList<>();
              QuizModel quiz = null;
              try
              {
                ag = AutoGrading.select1ByTaskId(conn, queueTaskId);
              }
              catch(NoSuchItemException e)
              {
                quiz = new QuizModel(conn, queueTaskId, null);
                quiz.readQuestions();
                quiz.gradeMultipleChoiceQuestions(null);
                quizScores = Tag.selectByTaggedIdAndType(conn, queueTaskId,
                        TagType.QUIZ_SCORE.getValue());
              }
              if((ag != null) || !quizScores.isEmpty())
              {
                generateAutoGrades(queueTaskId, quizScores, quiz);
              }
              else
              {
                generateGrades(queueTaskId);
              }
            }
            else
            {
              generatePeerReviewGrades(queueTaskId);
            }
          }
          else
          {
            taskQueue.addAll(subtaskIds);
          }
        }
      }
      return result;
    }

    private String generateAutoGrades(int leafTaskId, ArrayList<Tag> quizScores,
            QuizModel quiz)
            throws Exception
    {
      final boolean quizMode = !quizScores.isEmpty();
      ArrayList<Object[]> latePenaltySchedule = GradingModel
              .fetchPenaltySchedule(conn, leafTaskId);
      ArrayList<UserTaskView> students = UserTaskView
              .selectByTaskIdAndClusterType(conn, leafTaskId,
                      ClusterType.STUDENTS.getValue());
      HashSet<Integer> uids = new HashSet<>();
      for(UserTaskView student : students)
      {
        uids.add(student.getUserId());
      }
      Map<Integer, ArrayList<Submission>> submissionMap = new HashMap<>();
      Map<Integer, Map.Entry<StringBuilder, Float>> quizScoreMap
                                                            = new HashMap<>();
      if(quizMode)
      {
        quizScoreMap = QuizModel.getQuizScores(conn, taskId, null, quiz,
                quizScores);
      }
      else
      {
        submissionMap = SubmissionModel.getSubmissions(conn, leafTaskId, userId,
                uids, null, null, true, null, this);
      }
      ArrayList<Grade> grades = Grade.selectAnonymousNonaggregateByTaskId(conn,
              leafTaskId);
      HashMap<Integer, Grade> studentGrades = new HashMap<>();
      ArrayList<Grade> insertGrades = new ArrayList();
      ArrayList<Grade> updateGrades = new ArrayList();
      ArrayList<Grade> deleteGrades = new ArrayList();
      for(Grade grade : grades)
      {
        Integer receiverId = grade.getReceiverId();
        Grade previous = studentGrades.get(receiverId);
        ArrayList<Submission> studentSubmissions = submissionMap.get(
                receiverId);
        if((previous == null) && ((quizScoreMap.get(receiverId) != null)
                || ((studentSubmissions != null) && !studentSubmissions
                .isEmpty())))
        {
          studentGrades.put(grade.getReceiverId(), grade);
        }
        else
        {
          deleteGrades.add(grade);
        }
      }
      for(UserTaskView student : students)
      {
        Integer studentId = student.getUserId();
        ArrayList<Submission> studentSubmissions = submissionMap.get(studentId);
        Float newMark = null;
        Map.Entry<StringBuilder, Float> quizScore = quizScoreMap.get(studentId);
        if(quizScore != null)
        {
          newMark = quizScore.getValue();
        }
        boolean isValid = (newMark != null);
        if((studentSubmissions != null) && !studentSubmissions.isEmpty())
        {
          Submission latest = studentSubmissions.get(0);
          Integer submissionTimeStamp = latest.getTimeStamp();
          if(latest.getAutoGradeMark() != null)
          {
            newMark = new Float(latest.getAutoGradeMark());
            if(!latePenaltySchedule.isEmpty())
            {
              float penalty = 0;
              for(Object[] lp : latePenaltySchedule)
              {
                WetoTimeStamp wts = (WetoTimeStamp) lp[0];
                if(wts.getTimeStamp() <= submissionTimeStamp)
                {
                  penalty = (Float) lp[1];
                }
              }
              newMark -= penalty;
            }
          }
          isValid = SubmissionStatus.ACCEPTED.getValue().equals(latest
                  .getStatus());
        }
        if(isValid)
        {
          Integer gradeStatus = GradeStatus.NULL.getValue();
          if(isValid)
          {
            gradeStatus = GradeStatus.VALID.getValue();
          }
          Grade grade = studentGrades.get(studentId);
          if(grade != null)
          {
            Float oldMark = grade.getMark();
            if(overwriteExisting || (oldMark == null || ((newMark != null)
                    && (oldMark < newMark))))
            {
              grade.setMark(newMark);
              grade.setStatus(gradeStatus);
              updateGrades.add(grade);
            }
          }
          else
          {
            grade = new Grade();
            grade.setReceiverId(student.getUserId());
            grade.setReviewerId(null);
            grade.setTaskId(leafTaskId);
            grade.setMark(newMark);
            grade.setStatus(gradeStatus);
            insertGrades.add(grade);
          }
        }
        else if(overwriteExisting)
        {
          Grade grade = studentGrades.get(studentId);
          if(grade != null)
          {
            deleteGrades.add(grade);
          }
        }
      }
      GradingModel.batchDeleteGrades(conn, deleteGrades);
      GradingModel.batchInsertGrades(conn, insertGrades);
      GradingModel.batchUpdateGrades(conn, updateGrades);
      if(quizMode)
      {
        ArrayList<Grade> newGrades = Grade.selectAnonymousNonaggregateByTaskId(
                conn, leafTaskId);
        HashMap<Integer, Grade> newGradeMap = new HashMap<>();
        for(Grade newGrade : newGrades)
        {
          newGradeMap.put(newGrade.getReceiverId(), newGrade);
        }
        for(Grade grade : grades)
        {
          ArrayList<Tag> reviews = Tag.selectByTaggedIdAndType(conn, grade
                  .getId(), TagType.REVIEW.getValue());
          if(!reviews.isEmpty())
          {
            Integer studentId = grade.getReceiverId();
            Grade newGrade = newGradeMap.get(studentId);
            Map.Entry<StringBuilder, Float> quizScore = quizScoreMap.get(
                    studentId);
            int i = 0;
            if((newGrade != null) && (quizScore != null))
            {
              Tag oldReview = reviews.get(0);
              oldReview.setTaggedId(newGrade.getId());
              oldReview.setAuthorId(getCourseUserId());
              oldReview.setText(quizScore.getKey().toString().trim());
              oldReview.update(conn);
              quizScoreMap.remove(studentId);
              i = 1;
            }
            for(; i < reviews.size(); ++i)
            {
              reviews.get(i).delete(conn);
            }
          }
        }
        for(Map.Entry<Integer, Map.Entry<StringBuilder, Float>> quizScore
                    : quizScoreMap.entrySet())
        {
          StringBuilder gradeComment = quizScore.getValue().getKey();
          Grade newGrade = newGradeMap.get(quizScore.getKey());
          if((gradeComment.length() > 0) && (newGrade != null))
          {
            Tag review = new Tag();
            review.setTaggedId(newGrade.getId());
            review.setType(TagType.REVIEW.getValue());
            review.setAuthorId(getCourseUserId());
            review.setText(gradeComment.toString().trim());
            review.insert(conn);
          }
        }
      }
      return SUCCESS;
    }

    private String generatePeerReviewGrades(int leafTaskId) throws Exception
    {
      HashSet<Integer> peerReviewerIdSet = new HashSet<>(UserTaskView
              .selectPeerReviewerIdsByTaskId(conn, leafTaskId));
      ArrayList<Grade> grades = Grade.selectAnonymousNonaggregateByTaskId(conn,
              leafTaskId);
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, leafTaskId);
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      HashMap<Integer, Grade> studentGrades = new HashMap<>();
      ArrayList<Grade> insertGrades = new ArrayList();
      ArrayList<Grade> updateGrades = new ArrayList();
      ArrayList<Grade> deleteGrades = new ArrayList();
      for(Grade grade : grades)
      {
        Integer receiverId = grade.getReceiverId();
        Grade previousGrade = studentGrades.get(receiverId);
        if((previousGrade == null) && peerReviewerIdSet.contains(receiverId))
        {
          studentGrades.put(grade.getReceiverId(), grade);
        }
        else
        {
          deleteGrades.add(grade);
        }
      }
      HashSet<Integer> processedIds = new HashSet<>();
      for(Integer peerReviewerId : peerReviewerIdSet)
      {
        if(!processedIds.contains(peerReviewerId))
        {
          Grade grade = studentGrades.get(peerReviewerId);
          if(grade != null)
          {
            Float oldMark = grade.getMark();
            if(overwriteExisting || (oldMark == null))
            {
              grade.setMark(defaultMark);
              grade.setStatus(GradeStatus.VALID.getValue());
            }
            else
            {
              grade.setMark(oldMark + defaultMark);
            }
            updateGrades.add(grade);
          }
          else
          {
            grade = new Grade();
            grade.setReceiverId(peerReviewerId);
            grade.setReviewerId(null);
            grade.setTaskId(leafTaskId);
            grade.setMark(defaultMark);
            grade.setStatus(GradeStatus.VALID.getValue());
            insertGrades.add(grade);
          }
          processedIds.addAll(SubmissionModel.getSubmissionGroupMemberIds(conn,
                  leafTaskId, peerReviewerId, ignoreGroups));
        }
      }
      GradingModel.batchDeleteGrades(conn, deleteGrades);
      GradingModel.batchInsertGrades(conn, insertGrades);
      GradingModel.batchUpdateGrades(conn, updateGrades);
      return SUCCESS;
    }

    private String generateGrades(int leafTaskId) throws Exception
    {
      ArrayList<Object[]> latePenaltySchedule = GradingModel
              .fetchPenaltySchedule(conn, leafTaskId);
      // Process all students.
      ArrayList<UserTaskView> students = UserTaskView
              .selectByTaskIdAndClusterType(conn, leafTaskId,
                      ClusterType.STUDENTS.getValue());
      for(UserTaskView student : students)
      {
        ArrayList<Grade> grades = Grade.selectByTaskIdAndReceiverId(conn,
                leafTaskId, student.getUserId());
        Grade grade = null;
        Float mark = null;
        if(gradeSubmissionsOnly)
        {
          try
          {
            Submission latest = Submission
                    .select1LatestSubmissionByUserIdAndTaskId(conn, student
                            .getUserId(), leafTaskId);
            if((defaultMark != null) && latest.getStatus().equals(
                    SubmissionStatus.ACCEPTED.getValue()))
            {
              float penalty = 0;
              if(!latePenaltySchedule.isEmpty())
              {
                int submissionTimeStamp = latest.getTimeStamp();
                for(Object[] lp : latePenaltySchedule)
                {
                  WetoTimeStamp wts = (WetoTimeStamp) lp[0];
                  if(wts.getTimeStamp() <= submissionTimeStamp)
                  {
                    penalty = (Float) lp[1];
                  }
                }
              }
              mark = defaultMark - penalty;
            }
          }
          catch(NoSuchItemException e)
          {
            continue;
          }
        }
        else
        {
          mark = defaultMark;
        }
        for(Grade currGrade : grades)
        {
          if(userId.equals(currGrade.getReviewerId()))
          {
            grade = currGrade;
            break;
          }
        }
        // Create grade if it does not exist
        if(grade == null)
        {
          grade = new Grade();
          grade.setMark(mark);
          grade.setReceiverId(student.getUserId());
          grade.setReviewerId(userId);
          grade.setTaskId(leafTaskId);
          if(mark != null)
          {
            grade.setStatus(GradeStatus.VALID.getValue());
          }
          grade.insert(conn);
        }
        // Update mark if it is not given.
        else if((mark != null) && (grade.getMark() == null))
        {
          grade.setMark(mark);
          grade.setStatus(GradeStatus.VALID.getValue());
          grade.update(conn);
        }
      }
      return SUCCESS;
    }

    public void setSubmitted(boolean submitted)
    {
      this.submitted = submitted;
    }

    public void setOverwriteExisting(boolean overwriteExisting)
    {
      this.overwriteExisting = overwriteExisting;
    }

    public void setGradeSubmissionsOnly(boolean gradeSubmissionsOnly)
    {
      this.gradeSubmissionsOnly = gradeSubmissionsOnly;
    }

    public void setGradePeerReviewersOnly(boolean gradePeerReviewersOnly)
    {
      this.gradePeerReviewersOnly = gradePeerReviewersOnly;
    }

    public void setDefaultMark(String defaultMark)
    {
      try
      {
        this.defaultMark = Float.valueOf(defaultMark);
      }
      catch(NumberFormatException e)
      {
        this.defaultMark = null;
      }
    }

  }
}
