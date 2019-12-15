package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskDocument extends BeanTaskDocument
{
  public static ArrayList<TaskDocument> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<TaskDocument> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskId=" + taskId);
    while(iter.hasNext())
    {
      result.add((TaskDocument) iter.next());
    }
    return result;
  }

  public static ArrayList<TaskDocument> selectByTaskIdAndStatus(Connection conn,
          Integer taskId, Integer status)
          throws SQLException
  {
    ArrayList<TaskDocument> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskId=" + taskId
            + " AND status=" + status);
    while(iter.hasNext())
    {
      result.add((TaskDocument) iter.next());
    }
    return result;
  }

  public static ArrayList<TaskDocument> selectByTaskIdAndUserId(Connection conn,
          Integer taskId, Integer userId)
          throws SQLException
  {
    ArrayList<TaskDocument> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskId=" + taskId
            + " AND userid=" + userId);
    while(iter.hasNext())
    {
      result.add((TaskDocument) iter.next());
    }
    return result;
  }

  public static TaskDocument select1ByTaskIdAndDocumentId(Connection conn,
          Integer taskId, Integer taskDocumentId)
          throws NoSuchItemException, SQLException
  {
    TaskDocument result = null;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "documentId=" + taskDocumentId + "AND taskID=" + taskId);
    if(iter.hasNext())
    {
      result = (TaskDocument) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

}
