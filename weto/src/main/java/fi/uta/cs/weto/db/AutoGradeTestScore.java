package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class AutoGradeTestScore extends BeanAutoGradeTestScore
{
  public static ArrayList<AutoGradeTestScore> selectBySubmissionId(
          Connection conn, Integer submissionId)
          throws SQLException
  {
    ArrayList<AutoGradeTestScore> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "submissionid=" + submissionId
            + " ORDER BY phase, testno");
    while(iter.hasNext())
    {
      result.add((AutoGradeTestScore) iter.next());
    }
    return result;
  }

  public static void deleteBySubmissionId(Connection conn,
          Integer submissionId)
          throws TooManyItemsException, NoSuchItemException, SQLException,
                 ObjectNotValidException
  {
    Iterator<?> iter = selectionIterator(conn, "submissionid=" + submissionId);
    while(iter.hasNext())
    {
      ((AutoGradeTestScore) iter.next()).delete(conn);
    }
  }

  public static ArrayList<AutoGradeTestScore> selectByTaskId(
          Connection conn, Integer taskId)
          throws SQLException
  {
    ArrayList<AutoGradeTestScore> result = new ArrayList<>();
    String stmt = "SELECT a.* FROM autogradetestscore a, submission s WHERE "
            + "a.submissionid=s.id AND s.taskid=" + taskId
            + " ORDER BY a.timestamp";
    PreparedStatement ps = conn.prepareStatement(stmt);
    Iterator<?> iter = new SqlSelectionIterator(ps, AutoGradeTestScore.class);
    while(iter.hasNext())
    {
      result.add((AutoGradeTestScore) iter.next());
    }
    return result;
  }

  public boolean isPrivateScore()
  {
    int phase = getPhase();
    return ((phase == AutoGradeJobQueue.IMMEDIATE_PRIVATE)
            || (phase == AutoGradeJobQueue.FINAL_PRIVATE));
  }

  @Override
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.insert(con);
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

  @Override
  public String getFeedback()
  {
    return WetoUtilities.escapeHtml(super.getFeedback());
  }

}
