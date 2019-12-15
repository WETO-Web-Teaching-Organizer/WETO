package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class AutoGradeJobQueue extends BeanAutoGradeJobQueue
{
  // Convention: negative status codes mean the job has ended, positive
  // codes mean the job is active.
  public final static int ERROR_IN_TEST = -4;
  public final static int END_TEST_RUN = -3;
  public final static int END_NOT_ACCEPTED = -2;
  public final static int END_ACCEPTED = -1;
  public final static int TEST_RUN = 0;
  public final static int IMMEDIATE_PUBLIC = 1;
  public final static int IMMEDIATE_PRIVATE = 2;
  public final static int FINAL_PUBLIC = 3;
  public final static int FINAL_PRIVATE = 4;

  public final static int SUBMISSION_JOB = 0;
  public final static int INLINE_JOB = 1;

  public AutoGradeJobQueue() throws InvalidValueException
  {
    super();
    this.setTestRunning(false);
    this.setJobComment("");
    this.setJobType(SUBMISSION_JOB);
  }

  public static ArrayList<AutoGradeJobQueue> selectBySubmissionId(
          Connection conn, Integer submissionId) throws SQLException
  {
    ArrayList<AutoGradeJobQueue> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "jobtype=" + SUBMISSION_JOB + "AND refid=" + submissionId);
    while(iter.hasNext())
    {
      result.add((AutoGradeJobQueue) iter.next());
    }
    return result;
  }

  public static ArrayList<AutoGradeJobQueue> selectByTaskId(
          Connection conn, Integer taskId) throws SQLException
  {
    ArrayList<AutoGradeJobQueue> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((AutoGradeJobQueue) iter.next());
    }
    return result;
  }

  private static final String jobIdsQuery
                                      = "SELECT refid FROM autogradejobqueue WHERE jobtype="
          + SUBMISSION_JOB + " AND queuephase < " + FINAL_PUBLIC;

  public static int getQueuePos(Connection conn, Integer submissionId)
          throws SQLException
  {
    int queuePos = -1;
    int i = 0;
    PreparedStatement ps = conn.prepareStatement(jobIdsQuery);
    ResultSet rs = ps.executeQuery();
    while(rs.next())
    {
      if(submissionId.equals(rs.getInt(1)))
      {
        queuePos = i;
        break;
      }
      i += 1;
    }
    rs.close();
    ps.close();
    return queuePos;
  }

  private static final String quizJobIdsQuery
                                      = "SELECT refid FROM autogradejobqueue WHERE jobtype="
          + INLINE_JOB + " AND queuephase < " + FINAL_PUBLIC;

  public static int getQuizQueuePos(Connection conn, Integer quizAnswerId)
          throws SQLException
  {
    int queuePos = -1;
    int i = 0;
    PreparedStatement ps = conn.prepareStatement(quizJobIdsQuery);
    ResultSet rs = ps.executeQuery();
    while(rs.next())
    {
      if(quizAnswerId.equals(rs.getInt(1)))
      {
        queuePos = i;
        break;
      }
      i += 1;
    }
    rs.close();
    ps.close();
    return queuePos;
  }

  public static void deleteByTaskId(Connection conn, Integer taskId)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 ObjectNotValidException
  {
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      AutoGradeJobQueue job = (AutoGradeJobQueue) iter.next();
      job.delete(conn);
    }
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

  @Override
  public void update(Connection conn)
          throws SQLException, ObjectNotValidException, NoSuchItemException
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

}
