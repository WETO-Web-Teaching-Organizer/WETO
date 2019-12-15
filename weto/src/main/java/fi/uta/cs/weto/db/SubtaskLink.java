package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class SubtaskLink extends BeanSubtaskLink
{
  public static boolean hasSubTasks(Connection conn, Integer containerId)
          throws SQLException
  {
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "containerid=" + containerId);
    boolean result = iter.hasNext();
    iter.close();
    return result;
  }

  public static int lastSubtaskRank(Connection conn, Integer containerId)
          throws SQLException
  {
    int result = 0;
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT max(rank) FROM subtasklink WHERE containerid="
              + containerId);
      if(rs.next())
      {
        result = rs.getInt(1);
        if(rs.wasNull())
        {
          result = -1;
        }
      }
    }
    return result;
  }

  /**
   * Retrieve list of subtasks for a given container task.
   *
   * @param conn
   * @param containerId container task identifier
   * @return list of subtasks
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<SubtaskLink> selectByContainerId(Connection conn,
          Integer containerId)
          throws SQLException
  {
    ArrayList<SubtaskLink> result = new ArrayList<>();
    Iterator<?> iter = SubtaskLink.selectionIterator(conn, "containerid="
            + containerId);
    while(iter.hasNext())
    {
      result.add((SubtaskLink) iter.next());
    }
    return result;
  }

  /**
   * Retrieves subtask link from database.
   *
   * @param conn open database connection
   * @param subtaskId task identifier for subtask
   * @return subtask link
   * @throws fi.uta.cs.sqldatamodel.NoSuchItemException
   * @throws SQLException if the JDBC operation fails.
   */
  public static SubtaskLink select1BySubtaskId(Connection conn,
          Integer subtaskId) throws NoSuchItemException, SQLException
  {
    SubtaskLink result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "subtaskId=" + subtaskId);
    if(iter.hasNext())
    {
      result = (SubtaskLink) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  /**
   * Delete subtask link.
   *
   * @param conn open database connection
   * @param subtaskId task identifier for subtask
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   * @throws fi.uta.cs.sqldatamodel.ObjectNotValidException
   */
  public static void deleteBySubtaskId(Connection conn, Integer subtaskId)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 ObjectNotValidException
  {
    SubtaskLink link = select1BySubtaskId(conn, subtaskId);
    link.delete(conn);
  }

  @Override
  public void insert(Connection conn) throws SQLException,
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

}
