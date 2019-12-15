package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import static fi.uta.cs.weto.db.DbGrade.selectionIterator;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Grade extends BeanGrade
{
  public static Grade select1ById(Connection conn, Integer gradeId)
          throws NoSuchItemException, SQLException
  {
    Grade result = null;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + gradeId);
    if(iter.hasNext())
    {
      result = (Grade) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  /**
   * Retrieve all grades for given task sorted by receiver.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @return collection of grades
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Grade> selectByTaskId(Connection conn, Integer taskId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectAnonymousByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND reviewerid IS NULL");
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectAnonymousNonaggregateByTaskId(
          Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND reviewerid IS NULL AND status!=" + GradeStatus.AGGREGATE
            .getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectAnonymousByTaskIdAndReceiverId(
          Connection conn, Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND reviewerid IS NULL AND receiverid=" + receiverId);
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectAggregateByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND status=" + GradeStatus.AGGREGATE.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectByTaskIdAndReviewerId(Connection conn,
          Integer taskId, Integer reviewerId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter;
    if(reviewerId != null)
    {
      iter = selectionIterator(conn, "taskid=" + taskId + " AND reviewerid="
              + reviewerId);
    }
    else
    {
      iter = selectionIterator(conn, "taskid=" + taskId
              + " AND reviewerid is null");
    }
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectByCourseTaskIdAndReviewerId(
          Connection conn, Integer courseTaskId, Integer reviewerId)
          throws SQLException, InvalidValueException
  {
    ArrayList<Grade> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT taskid, grade.id, reviewerid, receiverid, mark,"
              + " grade.status, grade.timestamp FROM grade, task WHERE"
              + " reviewerid=" + reviewerId
              + " AND grade.taskid=task.id AND "
              + " task.roottaskid=" + courseTaskId);
      while(rs.next())
      {
        Grade g = new Grade();
        g.setFromResultSet(rs, 0);
        result.add(g);
      }
    }
    return result;
  }

  public static ArrayList<Grade> selectByCourseTaskIdAndReceiverId(
          Connection conn, Integer courseTaskId, Integer receiverId)
          throws SQLException, InvalidValueException
  {
    ArrayList<Grade> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT taskid, grade.id, reviewerid, receiverid, mark,"
              + " grade.status, grade.timestamp FROM grade, task WHERE"
              + " receiverid=" + receiverId
              + " AND grade.taskid=task.id AND "
              + " task.roottaskid=" + courseTaskId);
      while(rs.next())
      {
        Grade g = new Grade();
        g.setFromResultSet(rs, 0);
        result.add(g);
      }
    }
    return result;
  }

  public static ArrayList<Grade> selectValidAggregateByCourseTaskIdAndReceiverId(
          Connection conn, Integer courseTaskId, Integer receiverId)
          throws SQLException, InvalidValueException
  {
    ArrayList<Grade> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT taskid, grade.id, reviewerid, receiverid, mark,"
              + " grade.status, grade.timestamp FROM grade, task WHERE"
              + " grade.receiverid=" + receiverId + " AND grade.status="
              + GradeStatus.AGGREGATE.getValue()
              + " AND grade.mark IS NOT null AND grade.taskid=task.id AND "
              + " task.roottaskid=" + courseTaskId);
      while(rs.next())
      {
        Grade g = new Grade();
        g.setFromResultSet(rs, 0);
        result.add(g);
      }
    }
    return result;
  }

  /**
   * Retrieve all grades for given task sorted by receiver.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @param receiverId user id
   * @return collection of grades
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Grade> selectByTaskIdAndReceiverId(Connection conn,
          Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND receiverid=" + receiverId);
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectAggregateByTaskIdAndReceiverId(
          Connection conn, Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND receiverid=" + receiverId + " AND status="
            + GradeStatus.AGGREGATE.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  /**
   * Retrieve all grades that are valid for a given task sorted by receiver.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @param receiverId user id
   * @return collection of grades
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Grade> selectValidByTaskIdAndReceiverId(
          Connection conn,
          Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND receiverid=" + receiverId + " AND status="
            + GradeStatus.VALID.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  /**
   * Retrieve all grades that are valid for a given task.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @return collection of grades
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Grade> selectValidByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND status=" + GradeStatus.VALID.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectVisibleByTaskIdAndReviewerId(
          Connection conn, Integer taskId, Integer reviewerId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND reviewerid=" + reviewerId + " AND status!="
            + GradeStatus.HIDDEN.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectVisibleNonaggregateByTaskIdAndReceiverId(
          Connection conn, Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND receiverid=" + receiverId
            + " AND status!=" + GradeStatus.HIDDEN.getValue()
            + " AND status!=" + GradeStatus.AGGREGATE.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectPeerVisibleByTaskIdAndReceiverId(
          Connection conn, Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND receiverid=" + receiverId
            + " AND reviewerid IS NOT null AND status!=" + GradeStatus.HIDDEN
            .getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static ArrayList<Grade> selectPeerVisibleByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Grade> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND reviewerid IS NOT null AND status!="
            + GradeStatus.HIDDEN.getValue());
    while(iter.hasNext())
    {
      result.add((Grade) iter.next());
    }
    return result;
  }

  public static boolean hasValidAggregateGrade(Connection conn, Integer taskId,
          Integer receiverId) throws SQLException
  {
    int validCount = 0;
    try(PreparedStatement ps = conn.prepareStatement(
            "SELECT count(*) FROM grade WHERE taskid=" + taskId
            + " AND receiverid=" + receiverId + " AND status="
            + GradeStatus.AGGREGATE.getValue() + " AND mark is not null"))
    {
      ResultSet rs = ps.executeQuery();
      rs.next();
      validCount = rs.getInt(1);
      rs.close();
    }
    return (validCount > 0);
  }

  public Grade() throws InvalidValueException
  {
    super();
    setStatus(GradeStatus.UNSPECIFIED.getValue());
  }

  @Override
  public void insert(Connection conn)
          throws ObjectNotValidException, SQLException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.insert(conn);
  }

  public void insert(Connection conn, int timeStamp)
          throws ObjectNotValidException, SQLException
  {
    try
    {
      setTimeStamp(timeStamp);
    }
    catch(InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.insert(conn);
  }

  @Override
  public void update(Connection con) throws SQLException,
                                            ObjectNotValidException,
                                            NoSuchItemException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.update(con);
  }

  public String getTimeStampString() throws WetoTimeStampException
  {
    String time = new WetoTimeStamp(getTimeStamp()).toString();
    int secondsBoundary = time.lastIndexOf(':');
    return time.substring(0, secondsBoundary);
  }

}
