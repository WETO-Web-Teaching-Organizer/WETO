package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class GradeView extends BeanGradeView
{
  /**
   * Retrieve gradeview entry with given grade identifier.
   *
   * @param conn open database connection
   * @param gradeId grade identifier
   * @return gradeview entry
   * @throws NoSuchItemException
   * @throws SQLException if the JDBC operation fails.
   */
  public static GradeView select1ByGradeId(Connection conn, Integer gradeId)
          throws NoSuchItemException, SQLException
  {
    GradeView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "gradeid=" + gradeId);
    if(iter.hasNext())
    {
      result = (GradeView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static ArrayList<GradeView> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<GradeView> result = new ArrayList<>();

    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((GradeView) iter.next());
    }
    return result;
  }

  /**
   * Retrieve gradeview entries associated to the given task and grade reviewer.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @param reviewerId reviewer user id
   * @return collection of gradeview entries
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<GradeView> selectByTaskIdAndReviewerId(
          Connection conn, Integer taskId, Integer reviewerId)
          throws SQLException
  {
    ArrayList<GradeView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND reviewerid="
            + reviewerId);
    while(iter.hasNext())
    {
      result.add((GradeView) iter.next());
    }
    return result;
  }

  /**
   * Retrieve gradeview entries associated to the given task and grade receiver.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @param receiverId receiver user id
   * @return collection of gradeview entries
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<GradeView> selectByTaskIdAndReceiverId(
          Connection conn, Integer taskId, Integer receiverId)
          throws SQLException
  {
    ArrayList<GradeView> result = new ArrayList<>();
    Iterator<?> iter = GradeView.selectionIterator(conn, "taskId=" + taskId
            + " AND receiverid="
            + receiverId);
    while(iter.hasNext())
    {
      result.add((GradeView) iter.next());
    }
    return result;
  }

  /* Overrides for using student anynomization
  @Override
  public java.lang.String getReviewerFirstName()
  {
    return WetoUtilities.getRandomFirstName(getReviewerId());
  }

  @Override
  public java.lang.String getReviewerLastName()
  {
    return WetoUtilities.getRandomLastName(getReviewerId());
  }

  @Override
  public java.lang.String getReceiverFirstName()
  {
    return WetoUtilities.getRandomFirstName(getReceiverId());
  }

  @Override
  public java.lang.String getReceiverLastName()
  {
    return WetoUtilities.getRandomLastName(getReceiverId());
  }
   */
}
