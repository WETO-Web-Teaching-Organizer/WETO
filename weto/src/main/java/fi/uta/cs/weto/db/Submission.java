package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Submission extends BeanSubmission
{
  public static Submission select1ById(Connection conn, Integer submissionId)
          throws InvalidValueException, NoSuchItemException, SQLException
  {
    Submission result = new Submission();
    result.setId(submissionId);
    result.select(conn);
    return result;
  }

  public static ArrayList<Submission> selectByCourseTaskIdAndUserId(
          Connection conn, Integer courseTaskId, Integer userId)
          throws SQLException, InvalidValueException
  {
    ArrayList<Submission> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT submission.id, userid, submission.timestamp,"
              + " autogrademark, submission.status, taskid, message, error,"
              + " filecount FROM submission, task WHERE userid=" + userId
              + " AND submission.taskid=task.id AND "
              + " task.roottaskid=" + courseTaskId);
      while(rs.next())
      {
        Submission s = new Submission();
        s.setFromResultSet(rs, 0);
        result.add(s);
      }
    }
    return result;
  }

  public static ArrayList<Submission> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Submission> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " ORDER BY timestamp DESC");
    while(iter.hasNext())
    {
      result.add((Submission) iter.next());
    }
    return result;
  }

  public static ArrayList<Submission> selectByTaskIdAndStatus(Connection conn,
          Integer taskId, Integer status)
          throws SQLException
  {
    ArrayList<Submission> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND status=" + status + " ORDER BY timestamp DESC");
    while(iter.hasNext())
    {
      result.add((Submission) iter.next());
    }
    return result;
  }

  public static ArrayList<Submission> selectByUserIdAndTaskId(Connection conn,
          Integer userId, Integer taskId)
          throws SQLException
  {
    ArrayList<Submission> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "userid=" + userId
            + " AND taskid=" + taskId + " ORDER BY timestamp DESC");
    while(iter.hasNext())
    {
      result.add((Submission) iter.next());
    }
    return result;
  }

  public static ArrayList<Submission> selectByUserIdAndTaskIdAndStatus(
          Connection conn, Integer userId, Integer taskId, Integer status)
          throws SQLException
  {
    ArrayList<Submission> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "userid=" + userId
            + " AND taskid=" + taskId + " AND status=" + status
            + " ORDER BY timestamp DESC");
    while(iter.hasNext())
    {
      result.add((Submission) iter.next());
    }
    return result;
  }

  /**
   * Returns the latest submission made by a user for a specific task
   *
   * @param conn Connection to database
   * @param userId user id
   * @param taskId Task id
   * @return Latest submission by user
   */
  public static Submission select1LatestSubmissionByUserIdAndTaskId(
          Connection conn, Integer userId, Integer taskId)
          throws NoSuchItemException, SQLException
  {
    Submission result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "userid=" + userId + " AND taskid=" + taskId
            + " ORDER BY timestamp DESC LIMIT 1");
    if(iter.hasNext())
    {
      result = (Submission) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
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
  public void update(Connection conn) throws SQLException,
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
    super.update(conn);
  }

  public void update(Connection conn, boolean updateTimeStamp) throws
          SQLException, ObjectNotValidException, NoSuchItemException
  {
    super.update(conn);
  }

  public String getTimeStampString() throws WetoTimeStampException
  {
    String time = new WetoTimeStamp(getTimeStamp()).toString();
    int secondsBoundary = time.lastIndexOf(':');
    return time.substring(0, secondsBoundary);
  }

  @Override
  public String getMessage()
  {
    return WetoUtilities.escapeHtml(super.getMessage());
  }

}
