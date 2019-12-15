package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class GroupView extends BeanGroupView
{
  public static GroupView select1ByTaskIdAndUserIdAndType(Connection conn,
          Integer taskId, Integer userId, Integer type)
          throws NoSuchItemException, SQLException
  {
    GroupView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " AND userid=" + userId + " AND type=" + type);
    if(iter.hasNext())
    {
      result = (GroupView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static GroupView select1InheritedByTaskIdAndUserIdAndType(
          Connection conn,
          Integer taskId, Integer userId, Integer type)
          throws NoSuchItemException, SQLException
  {
    Integer searchTaskId = taskId;
    GroupView result = null;
    while(true)
    {
      try
      {
        result = GroupView.select1ByTaskIdAndUserIdAndType(conn, searchTaskId,
                userId, type);
        break;
      }
      catch(NoSuchItemException e)
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
      }
    }
    return result;
  }

  public static ArrayList<GroupView> selectByTaskIdAndUserId(Connection conn,
          Integer taskId, Integer userId)
          throws SQLException
  {
    ArrayList<GroupView> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " AND userid=" + userId);
    while(iter.hasNext())
    {
      result.add((GroupView) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupView> selectInheritedByTaskIdAndUserId(
          Connection conn, Integer taskId, Integer userId) throws SQLException
  {
    ArrayList<GroupView> result = GroupView
            .selectByTaskIdAndUserId(conn, taskId, userId);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = GroupView.selectByTaskIdAndUserId(conn, searchTaskId, userId);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

  public static ArrayList<GroupView> selectByTaskId(Connection conn,
          Integer taskId) throws SQLException
  {
    ArrayList<GroupView> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((GroupView) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupView> selectByGroupId(Connection conn,
          Integer groupId) throws SQLException
  {
    ArrayList<GroupView> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + groupId);
    while(iter.hasNext())
    {
      result.add((GroupView) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupView> selectInheritedByTaskId(Connection conn,
          Integer taskId) throws SQLException
  {
    ArrayList<GroupView> result = GroupView.selectByTaskId(conn, taskId);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = GroupView.selectByTaskId(conn, searchTaskId);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

  public static ArrayList<GroupView> selectByTaskIdAndType(Connection conn,
          Integer taskId, Integer type)
          throws SQLException
  {
    ArrayList<GroupView> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " and type=" + type);
    while(iter.hasNext())
    {
      result.add((GroupView) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupView> selectInheritedByTaskIdAndType(
          Connection conn, Integer taskId, Integer type)
          throws SQLException
  {
    ArrayList<GroupView> result = GroupView.selectByTaskIdAndType(conn, taskId,
            type);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = GroupView.selectByTaskIdAndType(conn, searchTaskId, type);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

  public static ArrayList<GroupView> selectByTaskIdAndNotType(Connection conn,
          Integer taskId, Integer notType)
          throws SQLException
  {
    ArrayList<GroupView> result = new ArrayList<>();
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " and type!=" + notType);
    while(iter.hasNext())
    {
      result.add((GroupView) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupView> selectInheritedByTaskIdAndNotType(
          Connection conn, Integer taskId, Integer notType)
          throws SQLException
  {
    ArrayList<GroupView> result = GroupView.selectByTaskIdAndNotType(conn,
            taskId, notType);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = GroupView.selectByTaskIdAndNotType(conn, searchTaskId, notType);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

}
