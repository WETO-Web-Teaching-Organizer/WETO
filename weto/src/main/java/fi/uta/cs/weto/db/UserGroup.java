package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class UserGroup extends BeanUserGroup
{
  public static ArrayList<UserGroup> selectByTaskIdAndType(Connection conn,
          Integer taskId, Integer type)
          throws SQLException
  {
    ArrayList<UserGroup> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId + " and type="
            + type);
    while(iter.hasNext())
    {
      result.add((UserGroup) iter.next());
    }
    return result;
  }

  public static ArrayList<UserGroup> selectByTaskIdAndNotType(Connection conn,
          Integer taskId, Integer notType)
          throws SQLException
  {
    ArrayList<UserGroup> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " and type!=" + notType);
    while(iter.hasNext())
    {
      result.add((UserGroup) iter.next());
    }
    return result;
  }

  public static ArrayList<UserGroup> selectByTaskId(Connection conn,
          Integer taskId) throws SQLException
  {
    ArrayList<UserGroup> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " ORDER BY type, name");
    while(iter.hasNext())
    {
      result.add((UserGroup) iter.next());
    }
    return result;
  }

  public static ArrayList<UserGroup> selectInheritedByTaskId(Connection conn,
          Integer taskId) throws SQLException
  {
    ArrayList<UserGroup> result = UserGroup.selectByTaskId(conn, taskId);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = UserGroup.selectByTaskId(conn, searchTaskId);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

  public static ArrayList<UserGroup> selectInheritedByTaskIdAndType(
          Connection conn, Integer taskId, Integer type)
          throws SQLException
  {
    ArrayList<UserGroup> result = UserGroup.selectByTaskIdAndType(conn, taskId,
            type);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = UserGroup.selectByTaskIdAndType(conn, searchTaskId, type);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

  public static ArrayList<UserGroup> selectInheritedByTaskIdAndNotType(
          Connection conn, Integer taskId, Integer type)
          throws SQLException
  {
    ArrayList<UserGroup> result = UserGroup.selectByTaskIdAndNotType(conn,
            taskId, type);
    Integer searchTaskId = taskId;
    try
    {
      while(result.isEmpty())
      {
        SubtaskLink link = SubtaskLink.select1BySubtaskId(conn, searchTaskId);
        searchTaskId = link.getContainerId();
        result = UserGroup.selectByTaskIdAndNotType(conn, searchTaskId, type);
      }
    }
    catch(NoSuchItemException e)
    {
    }
    return result;
  }

  public static UserGroup select1ByTaskIdAndName(Connection conn, Integer taskId,
          String name)
          throws NoSuchItemException, InvalidValueException, SQLException
  {
    UserGroup result = null;
    if(name != null)
    {
      String prepareString = "SELECT id, name, type, taskid, timestamp "
              + "FROM usergroup WHERE taskid=? AND lower(name) LIKE ?";
      PreparedStatement ps = conn.prepareStatement(prepareString);
      ps.setInt(1, taskId);
      ps.setString(2, name.trim().toLowerCase());
      ResultSet rs = ps.executeQuery();
      if(rs.next())
      {
        result = new UserGroup();
        result.setId(rs.getInt(1));
        result.setName(rs.getString(2));
        result.setType(rs.getInt(3));
        result.setTaskId(rs.getInt(4));
        result.setTimeStamp(rs.getInt(5));
      }
      rs.close();
      ps.close();
    }
    if(result == null)
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static UserGroup select1ById(Connection conn, Integer groupId)
          throws SQLException, InvalidValueException, NoSuchItemException
  {
    UserGroup result = new UserGroup();
    result.setId(groupId);
    result.select(conn);
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
