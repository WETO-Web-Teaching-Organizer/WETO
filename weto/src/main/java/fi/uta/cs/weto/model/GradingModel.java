package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserTaskView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GradingModel
{
  public static final Float MANDATORY_FAILED = Float.MIN_VALUE;

  public static ArrayList<Grade> getStudentGrades(Connection conn,
          Integer taskId, Integer studentId, boolean ignoreGroups)
          throws SQLException, InvalidValueException
  {
    ArrayList<Grade> grades = new ArrayList<>();
    if(ignoreGroups)
    {
      grades.addAll(Grade.selectVisibleNonaggregateByTaskIdAndReceiverId(conn,
              taskId, studentId));
    }
    else
    {
      for(Integer submitterId : SubmissionModel
              .getSubmissionGroupMemberIds(conn, taskId, studentId, false))
      {
        grades.addAll(Grade.selectVisibleNonaggregateByTaskIdAndReceiverId(conn,
                taskId, submitterId));
      }
    }
    return grades;
  }

  public static void deleteGrade(Connection conn, Grade grade)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 InvalidValueException, ObjectNotValidException,
                 WetoTimeStampException, IOException
  {
    for(Tag review : Tag.selectByTaggedIdAndType(conn, grade.getId(),
            TagType.REVIEW.getValue()))
    {
      review.delete(conn);
    }
    for(Tag message : Tag.selectByTaggedIdAndStatusAndType(conn, grade
            .getTaskId(), grade.getId(),
            TagType.GRADE_DISCUSSION.REVIEW.getValue()))
    {
      message.delete(conn);
    }
    grade.delete(conn);
  }

  public static void batchInsertGrades(Connection conn, List<Grade> grades)
          throws SQLException, WetoTimeStampException
  {
    if(!grades.isEmpty())
    {
      Integer timeStamp = new WetoTimeStamp().getTimeStamp();
      String prepareString
                     = "insert into Grade (taskId, reviewerId, receiverId,"
              + " mark, timeStamp, status) values (?, ?, ?, ?, ?, ?)";
      try(PreparedStatement ps = conn.prepareStatement(prepareString))
      {
        for(Grade grade : grades)
        {
          ps.setObject(1, grade.getTaskIdData().jdbcGetValue());
          ps.setObject(2, grade.getReviewerIdData().jdbcGetValue());
          ps.setObject(3, grade.getReceiverIdData().jdbcGetValue());
          ps.setObject(4, grade.getMarkData().jdbcGetValue());
          ps.setObject(5, timeStamp);
          ps.setObject(6, grade.getStatusData().jdbcGetValue());
          ps.addBatch();
        }
        ps.executeBatch();
      }
    }
  }

  public static void batchUpdateGrades(Connection conn, List<Grade> grades)
          throws SQLException, WetoTimeStampException
  {
    if(!grades.isEmpty())
    {
      Integer timeStamp = new WetoTimeStamp().getTimeStamp();
      String prepareString = "update Grade set taskId = ?, reviewerId = ?,"
              + " receiverId = ?, mark = ?, timeStamp = ?,"
              + " status = ? where id = ?";
      try(PreparedStatement ps = conn.prepareStatement(prepareString))
      {
        for(Grade grade : grades)
        {
          ps.setObject(1, grade.getTaskIdData().jdbcGetValue());
          ps.setObject(2, grade.getReviewerIdData().jdbcGetValue());
          ps.setObject(3, grade.getReceiverIdData().jdbcGetValue());
          ps.setObject(4, grade.getMarkData().jdbcGetValue());
          ps.setObject(5, timeStamp);
          ps.setObject(6, grade.getStatusData().jdbcGetValue());
          ps.setObject(7, grade.getIdData().jdbcGetValue());
          ps.addBatch();
        }
        ps.executeBatch();
      }
    }
  }

  public static void batchDeleteGrades(Connection conn, List<Grade> grades)
          throws SQLException
  {
    if(!grades.isEmpty())
    {
      String prepareString = "delete from Grade where id = ?";
      try(PreparedStatement ps = conn.prepareStatement(prepareString))
      {
        for(Grade grade : grades)
        {
          ps.setObject(1, grade.getIdData().jdbcGetValue());
          ps.addBatch();
        }
        ps.executeBatch();
      }
      prepareString = "delete from Tag where type = ? and taggedid = ?";
      try(PreparedStatement ps = conn.prepareStatement(prepareString))
      {
        boolean notEmpty = false;
        for(Grade grade : grades)
        {
          if(!GradeStatus.AGGREGATE.getValue().equals(grade.getStatus()))
          {
            notEmpty = true;
            ps.setObject(1, TagType.REVIEW.getValue());
            ps.setObject(2, grade.getIdData().jdbcGetValue());
            ps.addBatch();
          }
        }
        if(notEmpty)
        {
          ps.executeBatch();
        }
      }
    }
  }

  public static void calculateGrades(Connection conn, Task task,
          ArrayList<UserTaskView> students)
          throws InvalidValueException, SQLException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException,
                 WetoTimeStampException,
                 IOException
  {
    ArrayList<Grade> insertGrades = new ArrayList<>();
    ArrayList<Grade> updateGrades = new ArrayList<>();
    ArrayList<Grade> deleteGrades = new ArrayList<>();
    generateAggregateGrades(conn, task, students, insertGrades, updateGrades,
            deleteGrades, new Boolean[1]);
    batchDeleteGrades(conn, deleteGrades);
    batchInsertGrades(conn, insertGrades);
    batchUpdateGrades(conn, updateGrades);
    PropertyModel.updateNavigationTreeUpdate(conn, task.getRootTaskId());
  }

  private static Map<Integer, Float> generateAggregateGrades(Connection conn,
          Task task, ArrayList<UserTaskView> students,
          ArrayList<Grade> insertGrades, ArrayList<Grade> updateGrades,
          ArrayList<Grade> deleteGrades, Boolean[] isMandatory)
          throws InvalidValueException, SQLException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException, IOException
  {
    Integer taskId = task.getId();
    Map<Integer, Float> grades;
    HashMap<Integer, Map<Integer, Float>> subtasksGrades = new HashMap<>();
    HashSet<Integer> mandatorySubtasks = new HashSet<>();
    ArrayList<Task> allSubtasks = Task.selectSubtasks(conn, task.getId());
    boolean isGradable = task.getHasGrades();
    for(Task subtask : allSubtasks)
    {
      if(isGradable && subtask.getHasGrades())
      {
        isMandatory[0] = false;
        Integer subtaskId = subtask.getId();
        subtasksGrades.put(subtaskId, generateAggregateGrades(conn, subtask,
                students, insertGrades, updateGrades, deleteGrades, isMandatory));
        if(isMandatory[0])
        {
          mandatorySubtasks.add(subtaskId);
        }
      }
      else
      {
        generateAggregateGrades(conn, subtask, students, insertGrades,
                updateGrades, deleteGrades, isMandatory);
      }
    }
    if(isGradable)
    {
      if(subtasksGrades.isEmpty())
      {
        grades = generateLeafAggregateGrades(conn, taskId, null, insertGrades,
                updateGrades, deleteGrades, isMandatory);
      }
      else
      {
        grades = generateNodeAggregateGrades(conn, taskId, null, students,
                subtasksGrades, mandatorySubtasks, insertGrades, updateGrades,
                deleteGrades, isMandatory);
      }
    }
    else
    {
      grades = new HashMap<>();
      isMandatory[0] = false;
    }
    return grades;
  }

  private static Map<Integer, Float> generateLeafAggregateGrades(
          Connection conn, Integer taskId, Integer studentId,
          ArrayList<Grade> insertGrades, ArrayList<Grade> updateGrades,
          ArrayList<Grade> deleteGrades, Boolean[] isMandatory)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException, IOException
  {
    Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
    final boolean calculateAverage = Boolean.parseBoolean(scoring.getProperty(
            "calculateAverage"));
    final String roundType = scoring.getProperty("roundType", "None");
    isMandatory[0] = Boolean.parseBoolean(scoring.getProperty("mandatoryTask"));
    final boolean ignoreGroups = Boolean.parseBoolean(scoring.getProperty(
            "ignoreGroups"));
    HashSet<Integer> peerReviewerIdSet = null;
    if(Boolean.parseBoolean(scoring.getProperty("requirePeerReview")))
    {
      peerReviewerIdSet = new HashSet<>();
      for(Integer memberId : UserTaskView.selectPeerReviewerIdsByTaskId(conn,
              taskId))
      {
        peerReviewerIdSet.addAll(SubmissionModel.getSubmissionGroupMemberIds(
                conn, taskId, memberId, ignoreGroups));
      }
    }
    // If studentId is not null, then only a specific student is processed.
    ArrayList<Grade> gradeList = (studentId != null) ? getStudentGrades(conn,
            taskId, studentId, ignoreGroups) : Grade
            .selectByTaskId(conn, taskId);
    // Values as arrays to allow indirect references.
    Map<Integer, Integer[]> countMap = new HashMap<>();
    Map<Integer, Float[]> markMap = new HashMap<>();
    for(Grade grade : gradeList)
    {
      Integer receiverId = grade.getReceiverId();
      Float mark = grade.getMark();
      Integer gradeStatus = grade.getStatus();
      if(((peerReviewerIdSet == null) || peerReviewerIdSet.contains(receiverId))
              && GradeStatus.VALID.getValue().equals(gradeStatus) && (mark
              != null))
      {
        Integer[] count = countMap.get(receiverId);
        Float[] score = markMap.get(receiverId);
        if(count == null)
        {
          count = new Integer[1];
          count[0] = 1;
          score = new Float[1];
          score[0] = null;
          for(Integer memberId : SubmissionModel.getSubmissionGroupMemberIds(
                  conn, taskId, receiverId, ignoreGroups))
          {
            countMap.put(memberId, count);
            markMap.put(memberId, score);
          }
        }
        else
        {
          count[0] += 1;
        }
        score[0] = (score[0] == null) ? mark : score[0] + mark;
      }
    }
    ArrayList<GradeTableEntry> gradeTable = decodeGradeTable(scoring
            .getGradeTable());
    TreeMap<Float, GradeTableEntry> gradingMap = new TreeMap<>();
    GradeTableEntry lowerBound = null;
    GradeTableEntry upperBound = null;
    for(GradeTableEntry entry : gradeTable)
    {
      if(entry.getType() == GradeTableEntry.LOWER_BOUND)
      {
        lowerBound = entry;
      }
      else if(entry.getType() == GradeTableEntry.UPPER_BOUND)
      {
        upperBound = entry;
      }
      else
      {
        gradingMap.put(entry.getScore(), entry);
      }
    }
    Map<Integer, Grade> aggregateGradeMap = new HashMap<>();
    ArrayList<Grade> aggregateGradeList;
    if(studentId != null)
    {
      aggregateGradeList = new ArrayList<>();
      for(Integer memberId : SubmissionModel.getSubmissionGroupMemberIds(conn,
              taskId, studentId, ignoreGroups))
      {
        aggregateGradeList.addAll(Grade.selectAggregateByTaskIdAndReceiverId(
                conn, taskId, memberId));
      }
    }
    else
    {
      aggregateGradeList = Grade.selectAggregateByTaskId(conn, taskId);
    }
    for(Grade grade : aggregateGradeList)
    {
      Integer receiverId = grade.getReceiverId();
      if(!aggregateGradeMap.containsKey(receiverId))
      {
        aggregateGradeMap.put(receiverId, grade);
      }
      else
      {
        deleteGrades.add(grade);
      }
    }
    HashMap<Integer, Float> resultMap = new HashMap<>();
    for(Map.Entry<Integer, Float[]> entry : markMap.entrySet())
    {
      Integer receiverId = entry.getKey();
      Float mark = entry.getValue()[0];
      if(mark != null)
      {
        if(calculateAverage)
        {
          mark /= countMap.get(receiverId)[0];
        }
        mark = gradeTableMark(gradingMap, lowerBound, upperBound, roundType,
                mark);
      }
      resultMap.put(receiverId, mark);
      if(mark != null)
      {
        Grade aggregateGrade = aggregateGradeMap.get(receiverId);
        if(aggregateGrade == null)
        {
          aggregateGrade = new Grade();
          aggregateGrade.setStatus(GradeStatus.AGGREGATE.getValue());
          aggregateGrade.setReviewerId(null);
          aggregateGrade.setReceiverId(receiverId);
          aggregateGrade.setMark(mark);
          aggregateGrade.setTaskId(taskId);
          insertGrades.add(aggregateGrade);
        }
        else
        {
          aggregateGrade.setMark(mark);
          updateGrades.add(aggregateGrade);
          aggregateGradeMap.remove(receiverId);
        }
      }
    }
    for(Map.Entry<Integer, Grade> entry : aggregateGradeMap.entrySet())
    {
      deleteGrades.add(entry.getValue());
    }
    return resultMap;
  }

  private static Map<Integer, Float> generateNodeAggregateGrades(
          Connection conn, Integer taskId, Integer studentId,
          ArrayList<UserTaskView> students,
          HashMap<Integer, Map<Integer, Float>> subtasksGrades,
          HashSet<Integer> mandatorySubtasks, ArrayList<Grade> insertGrades,
          ArrayList<Grade> updateGrades, ArrayList<Grade> deleteGrades,
          Boolean[] isMandatory)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException, IOException
  {
    Map<Integer, Float> markMap = new HashMap<>();
    Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
    final String roundType = scoring.getProperty("roundType", "None");
    ArrayList<GradeTableEntry> gradeTable = decodeGradeTable(scoring
            .getGradeTable());
    TreeMap<Float, GradeTableEntry> gradeMap = new TreeMap<>();
    GradeTableEntry lowerBound = null;
    GradeTableEntry upperBound = null;
    for(GradeTableEntry entry : gradeTable)
    {
      if(entry.getType() == GradeTableEntry.LOWER_BOUND)
      {
        lowerBound = entry;
      }
      else if(entry.getType() == GradeTableEntry.UPPER_BOUND)
      {
        upperBound = entry;
      }
      else
      {
        gradeMap.put(entry.getScore(), entry);
      }
    }
    Integer failedMax;
    try
    {
      failedMax = Integer.parseInt(scoring.getProperty("maxFailedSubtasks"));
    }
    catch(NumberFormatException e)
    {
      failedMax = null;
    }
    AggregateFunctionType aft = AggregateFunctionType.getType(Integer.parseInt(
            scoring.getProperty("aggregateFunction", "0")));
    isMandatory[0] = Boolean.parseBoolean(scoring.getProperty("mandatoryTask"));
    Map< Integer, Grade> aggregateGradeMap = new HashMap<>();
    ArrayList<Grade> gradeList = (studentId == null) ? Grade
            .selectByTaskId(conn, taskId) : Grade.selectByTaskIdAndReceiverId(
            conn, taskId, studentId);
    for(Grade grade : gradeList)
    {
      Integer receiverId = grade.getReceiverId();
      if(!aggregateGradeMap.containsKey(receiverId))
      {
        grade.setStatus(GradeStatus.AGGREGATE.getValue());
        grade.setReviewerId(null);
        aggregateGradeMap.put(receiverId, grade);
      }
      else
      {
        deleteGrades.add(grade);
      }
    }
    for(UserTaskView student : students)
    {
      Integer receiverId = student.getUserId();
      Float score = aft.getAggregateScore(receiverId, subtasksGrades, failedMax,
              mandatorySubtasks);
      Float mark = score;
      if(score != null)
      {
        if(!GradingModel.MANDATORY_FAILED.equals(score))
        {
          mark = gradeTableMark(gradeMap, lowerBound, upperBound, roundType,
                  mark);
        }
        markMap.put(receiverId, mark);
        Float aggregateMark = GradingModel.MANDATORY_FAILED.equals(score) ? null
                                      : mark;
        Grade aggregateGrade = aggregateGradeMap.get(receiverId);
        if(aggregateGrade == null)
        {
          aggregateGrade = new Grade();
          aggregateGrade.setStatus(GradeStatus.AGGREGATE.getValue());
          aggregateGrade.setReviewerId(null);
          aggregateGrade.setReceiverId(receiverId);
          aggregateGrade.setMark(aggregateMark);
          aggregateGrade.setTaskId(taskId);
          insertGrades.add(aggregateGrade);
        }
        else
        {
          aggregateGrade.setMark(aggregateMark);
          updateGrades.add(aggregateGrade);
          aggregateGradeMap.remove(receiverId);
        }
      }
    }
    for(Map.Entry<Integer, Grade> entry : aggregateGradeMap.entrySet())
    {
      deleteGrades.add(entry.getValue());
    }
    return markMap;
  }

  private static Float roundMark(Float mark, String roundType)
  {
    if(mark != null)
    {
      if("Floor".equals(roundType))
      {
        mark = (float) Math.floor(mark);
      }
      else if("Floor".equals(roundType))
      {
        mark = (float) Math.ceil(mark);
      }
      else if("Floor".equals(roundType))
      {
        mark = (float) Math.round(mark);
      }
    }
    return mark;
  }

  private static Float gradeTableMark(TreeMap<Float, GradeTableEntry> gtm,
          GradeTableEntry lowerBound, GradeTableEntry upperBound,
          String roundType, Float score)
  {
    Float mark = score;
    if(score != null)
    {
      if((lowerBound != null) && (score < lowerBound.getScore()))
      {
        mark = lowerBound.getMark();
      }
      else if((upperBound != null) && (upperBound.getScore() < score))
      {
        mark = upperBound.getMark();
      }
      else if(!gtm.isEmpty())
      {
        Map.Entry<Float, GradeTableEntry> mapEntry = gtm.floorEntry(score);
        if(mapEntry != null)
        {
          mark = mapEntry.getValue().getMark();
        }
        else
        {
          mark = null;
        }
      }
    }
    return roundMark(mark, roundType);
  }

  public static void recalculateStudentGrades(Connection conn,
          Integer updateTaskId, Integer studentId)
          throws InvalidValueException, SQLException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException,
                 WetoTimeStampException,
                 IOException
  {
    ArrayList<Grade> insertGrades = new ArrayList<>();
    ArrayList<Grade> updateGrades = new ArrayList<>();
    ArrayList<Grade> deleteGrades = new ArrayList<>();
    Boolean[] isMandatory = new Boolean[1];
    ArrayList<UserTaskView> students = new ArrayList<>();
    students.add(UserTaskView.select1ByTaskIdAndUserId(conn, updateTaskId,
            studentId));
    generateLeafAggregateGrades(conn, updateTaskId, studentId, insertGrades,
            updateGrades, deleteGrades, isMandatory);
    batchDeleteGrades(conn, deleteGrades);
    deleteGrades.clear();
    batchInsertGrades(conn, insertGrades);
    insertGrades.clear();
    batchUpdateGrades(conn, updateGrades);
    updateGrades.clear();
    HashMap<Integer, Map<Integer, Float>> subtasksGrades = new HashMap<>();
    HashSet<Integer> mandatorySubtasks = new HashSet<>();
    while(true)
    {
      SubtaskLink fatherLink;
      try
      {
        fatherLink = SubtaskLink.select1BySubtaskId(conn, updateTaskId);
      }
      catch(NoSuchItemException e)
      { // Ok: reached the top level task
        break;
      }
      updateTaskId = fatherLink.getContainerId();
      Task updateTask = Task.select1ById(conn, updateTaskId);
      if(!updateTask.getHasGrades())
      { // Do not proceeed to a father task that has no grades
        break;
      }
      ArrayList<Task> allSubtasks = Task.selectSubtasks(conn, updateTaskId);
      for(Task subtask : allSubtasks)
      {
        Integer subtaskId = subtask.getId();
        if(subtask.getHasGrades())
        {
          Scoring scoring = Scoring.select1ByTaskId(conn, subtaskId);
          if(Boolean.parseBoolean(scoring.getProperty("mandatoryTask")))
          {
            mandatorySubtasks.add(subtaskId);
          }
          HashMap<Integer, Float> tmpGradeMap = new HashMap<>();
          ArrayList<Grade> aggregateGrades = Grade
                  .selectAggregateByTaskIdAndReceiverId(conn, subtaskId,
                          studentId);
          if(!aggregateGrades.isEmpty())
          {
            Float aggregateMark = aggregateGrades.get(0).getMark();
            if(aggregateMark == null)
            {
              aggregateMark = GradingModel.MANDATORY_FAILED;
            }
            tmpGradeMap.put(studentId, aggregateMark);
            if(aggregateGrades.size() > 1)
            {
              deleteGrades.addAll(aggregateGrades.subList(1, aggregateGrades
                      .size()));
            }
          }
          subtasksGrades.put(subtaskId, tmpGradeMap);
        }
      }
      generateNodeAggregateGrades(conn, updateTaskId, studentId, students,
              subtasksGrades, mandatorySubtasks, insertGrades, updateGrades,
              deleteGrades, isMandatory);
      batchDeleteGrades(conn, deleteGrades);
      deleteGrades.clear();
      batchInsertGrades(conn, insertGrades);
      insertGrades.clear();
      batchUpdateGrades(conn, updateGrades);
      updateGrades.clear();
      mandatorySubtasks.clear();
      subtasksGrades.clear();
    }
    PropertyModel.updateNavigationTreeUpdate(conn, updateTaskId);
  }

  public static ArrayList<Task> getGradableSubtasks(Connection conn, Task task)
          throws SQLException, InvalidValueException, NoSuchItemException
  {
    ArrayList<Task> gradableSubtasks = new ArrayList<>();
    ArrayList<Task> allSubtasks = Task.selectSubtasks(conn, task.getId());
    for(Task subtask : allSubtasks)
    {
      if(subtask.getHasGrades())
      {
        gradableSubtasks.add(subtask);
      }
    }
    return gradableSubtasks;
  }

  public static ArrayList<ArrayList<Float>> retrieveAggregateGrades(
          Connection conn, ArrayList<Task> tasks,
          ArrayList<StudentView> students)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    ArrayList<ArrayList<Float>> grades = new ArrayList<>();
    if(!students.isEmpty())
    {
      for(int i = students.size(); i > 0; --i)
      {
        grades.add(new ArrayList<Float>());
      }
      for(Task task : tasks)
      {
        Integer taskId = task.getId();
        ArrayList<Grade> taskGrades;
        if(students.size() > 1)
        {
          taskGrades = Grade.selectAggregateByTaskId(conn, taskId);
        }
        else // Fetch grades for only one student
        {
          taskGrades = Grade.selectAggregateByTaskIdAndReceiverId(conn, taskId,
                  students.get(0).getUserId());
        }
        // Store student's mark for the current task
        Map<Integer, Float> marks = new HashMap<>();
        for(Grade grade : taskGrades)
        {
          marks.put(grade.getReceiverId(), grade.getMark());
        }
        int i = 0;
        for(StudentView student : students)
        {
          grades.get(i).add(marks.get(student.getUserId()));
          i += 1;
        }
      }
    }
    return grades;
  }

  public static ArrayList<ArrayList<Float>> retrieveAllGrades(Connection conn,
          ArrayList<Task> tasks, ArrayList<StudentView> students)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    ArrayList<ArrayList<Float>> grades = new ArrayList<>();
    if(!students.isEmpty())
    {
      for(int i = students.size(); i > 0; --i)
      {
        grades.add(new ArrayList<Float>());
      }
      Map<Integer, ArrayList<Float>> studentsGradesMap = new HashMap<>();
      for(Task task : tasks)
      {
        Integer taskId = task.getId();
        ArrayList<Grade> taskGrades;
        if(students.size() > 1)
        {
          taskGrades = Grade.selectByTaskId(conn, taskId);
        }
        else // Fetch grades for only one student
        {
          taskGrades = Grade.selectByTaskIdAndReceiverId(conn, taskId,
                  students.get(0).getUserId());
        }
        Collections.sort(taskGrades, new Comparator<Grade>()
        {
          @Override
          public int compare(Grade a, Grade b)
          {
            if(GradeStatus.AGGREGATE.getValue().equals(a.getStatus())
                    && !GradeStatus.AGGREGATE.getValue()
                    .equals(b.getStatus()))
            {
              return -1;
            }
            else if(GradeStatus.AGGREGATE.getValue().equals(b.getStatus())
                    && !GradeStatus.AGGREGATE.getValue().equals(a
                            .getStatus()))
            {
              return 1;
            }
            else
            {
              return b.getTimeStamp().compareTo(a.getTimeStamp());
            }
          }

        });
        // Store student's mark for the current task
        studentsGradesMap.clear();
        for(Grade grade : taskGrades)
        {
          Integer receiverId = grade.getReceiverId();
          ArrayList<Float> studentGrades = studentsGradesMap.get(receiverId);
          if(studentGrades == null)
          {
            studentGrades = new ArrayList<>();
            studentsGradesMap.put(receiverId, studentGrades);
            if(!GradeStatus.AGGREGATE.getValue().equals(grade.getStatus()))
            {
              studentGrades.add(null);
            }
          }
          studentGrades.add(grade.getMark());
        }
        int i = 0;
        for(StudentView student : students)
        {
          ArrayList<Float> studentGrades = studentsGradesMap.get(student
                  .getUserId());
          if(studentGrades != null)
          {
            grades.get(i).addAll(studentGrades);
          }
          i += 1;
        }
      }
    }
    return grades;
  }

  public static class GradeTableEntry implements Comparable<GradeTableEntry>
  {
    public static final int LOWER_BOUND = 0;
    public static final int UPPER_BOUND = 1;
    public static final int EXACT_BOUND = 2;
    private static final String[] SCOREPREFIX = new String[]
    {
      "<", ">", ""
    };

    private Float score;
    private Float mark;
    private int type;

    public GradeTableEntry(Float score, Float mark, int type)
    {
      this.score = score;
      this.mark = mark;
      this.type = type;
    }

    public String toString()
    {
      String markString = (mark != null) ? mark.toString() : "";
      return SCOREPREFIX[type] + score.toString() + "=" + markString + ";";
    }

    public Float getScore()
    {
      return score;
    }

    public void setScore(Float score)
    {
      this.score = score;
    }

    public Float getMark()
    {
      return mark;
    }

    public void setMark(Float mark)
    {
      this.mark = mark;
    }

    public int getType()
    {
      return type;
    }

    public void setType(int type)
    {
      this.type = type;
    }

    @Override
    public int compareTo(GradeTableEntry t)
    {
      if(type == t.type)
      {
        if(score < t.score)
        {
          return -1;
        }
        else if(score > t.score)
        {
          return 1;
        }
        return 0;
      }
      return type - t.type;
    }

  }

  public static GradeTableEntry decodeGradeTableScore(String scoreString)
  {
    int lowMarker = scoreString.indexOf("<");
    int highMarker = scoreString.indexOf(">");
    float score = 0;
    int type = GradeTableEntry.EXACT_BOUND;
    if(lowMarker >= 0)
    {
      score = Float.parseFloat(scoreString.substring(lowMarker + 1));
      type = GradeTableEntry.LOWER_BOUND;
    }
    else if(highMarker >= 0)
    {
      score = Float.parseFloat(scoreString.substring(highMarker + 1));
      type = GradeTableEntry.UPPER_BOUND;
    }
    else
    {
      score = Float.parseFloat(scoreString);
    }
    return new GradeTableEntry(score, null, type);
  }

  public static ArrayList<GradeTableEntry> decodeGradeTable(
          String gradeTableString)
  {
    ArrayList<GradeTableEntry> gradeTable = new ArrayList<>();
    if(gradeTableString != null && !gradeTableString.trim().isEmpty())
    {
      String[] scoreMarkStrings = gradeTableString.split(";", 0);
      for(String scoreMarkString : scoreMarkStrings)
      {
        String[] scoreMarkParts = scoreMarkString.split("=", 2);
        GradeTableEntry entry = decodeGradeTableScore(scoreMarkParts[0]);
        try
        {
          entry.setMark(Float.parseFloat(scoreMarkParts[1]));
        }
        catch(Exception e)
        { // Mark may be missing: means unspecified grade
        }
        gradeTable.add(entry);
      }
    }
    return gradeTable;
  }

  public static ArrayList<GradeTableEntry> sortGradeTable(
          ArrayList<GradeTableEntry> oldGradeTable)
  {
    ArrayList<GradeTableEntry> newGradeTable = new ArrayList<>();
    TreeMap<Float, GradeTableEntry> lowerBounds = new TreeMap<>();
    TreeMap<Float, GradeTableEntry> exactBounds = new TreeMap<>();
    TreeMap<Float, GradeTableEntry> upperBounds = new TreeMap<>();
    for(GradeTableEntry entry : oldGradeTable)
    {
      if(entry.getType() == GradeTableEntry.LOWER_BOUND)
      {
        lowerBounds.put(entry.getScore(), entry);
      }
      else if(entry.getType() == GradeTableEntry.EXACT_BOUND)
      {
        exactBounds.put(entry.getScore(), entry);
      }
      else
      {
        upperBounds.put(entry.getScore(), entry);
      }
    }
    GradeTableEntry lowerEntry = null;
    if(!lowerBounds.isEmpty())
    {
      lowerEntry = lowerBounds.get(lowerBounds.lastKey());
      newGradeTable.add(lowerEntry);
    }
    GradeTableEntry upperEntry = null;
    if(!upperBounds.isEmpty())
    {
      upperEntry = upperBounds.get(upperBounds.firstKey());
      if((lowerEntry != null) && (lowerEntry.getScore() > upperEntry.getScore()))
      {
        upperEntry.setScore(lowerEntry.getScore());
      }
    }
    if(!exactBounds.isEmpty())
    {
      Float lowerLimit = (lowerEntry != null) ? lowerEntry.getScore()
                                 : exactBounds.firstKey();
      Float upperLimit = (upperEntry != null) ? upperEntry.getScore()
                                 : exactBounds.lastKey();
      for(GradeTableEntry entry : exactBounds.values())
      {
        if((lowerLimit <= entry.getScore()) && (entry.getScore() <= upperLimit))
        {
          newGradeTable.add(entry);
        }
      }
    }
    if(upperEntry != null)
    {
      newGradeTable.add(upperEntry);
    }
    return newGradeTable;
  }

  public static String gradeTableToString(ArrayList<GradeTableEntry> gradeTable)
  {
    String result = null;
    if(!gradeTable.isEmpty())
    {
      StringBuilder sb = new StringBuilder();
      for(GradingModel.GradeTableEntry entry : gradeTable)
      {
        sb.append(entry.toString());
      }
      result = sb.toString();
    }
    return result;
  }

  public static ArrayList<Object[]> fetchPenaltySchedule(Connection conn,
          int startTaskId)
          throws SQLException, InvalidValueException,
                 ObjectNotValidException, IOException, WetoTimeStampException
  {
    ArrayList<Object[]> latePenaltySchedule = new ArrayList<>();
    String latePenalties = null;
    int currTaskId = startTaskId;
    while(latePenalties == null)
    {
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, currTaskId);
        latePenalties = scoring.getProperty("latePenalties");
      }
      catch(NoSuchItemException e)
      {
      }
      if(latePenalties == null)
      {
        try
        {
          currTaskId = SubtaskLink.select1BySubtaskId(conn, currTaskId)
                  .getContainerId();
        }
        catch(NoSuchItemException e2)
        {
          break;
        }
      }
    }
    if(latePenalties != null)
    {
      try(BufferedReader br
                                 = new BufferedReader(new StringReader(
                      latePenalties)))
      {
        String line;
        while((line = br.readLine()) != null && !line.trim().isEmpty())
        {
          String[] parts = line.split("=", 2);
          String[] dateAndTime = parts[0].split(" ", 2);
          String[] date = dateAndTime[0].split("\\.", 3);
          String[] time = dateAndTime[1].split(":", 2);
          WetoTimeStamp wts = new WetoTimeStamp(Integer.parseInt(date[2]),
                  Integer.parseInt(date[1]), Integer.parseInt(date[0]), Integer
                  .parseInt(time[0]), Integer.parseInt(time[1]), 0);
          latePenaltySchedule.add(new Object[]
          {
            wts,
            Float.valueOf(parts[1])
          });
        }
      }
      Collections.sort(latePenaltySchedule, new Comparator<Object[]>()
      {
        @Override
        public int compare(Object[] o1, Object[] o2)
        {
          WetoTimeStamp wts1 = (WetoTimeStamp) o1[0];
          WetoTimeStamp wts2 = (WetoTimeStamp) o2[0];
          return (wts1.getTimeStamp() - wts2.getTimeStamp());
        }

      });
    }
    return latePenaltySchedule;
  }

}
