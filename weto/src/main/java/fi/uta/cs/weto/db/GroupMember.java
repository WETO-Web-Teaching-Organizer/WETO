package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import static fi.uta.cs.weto.db.DbGroupMember.selectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class GroupMember extends BeanGroupMember
{
  public static ArrayList<GroupMember> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<GroupMember> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((GroupMember) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupMember> selectByUserId(Connection conn,
          Integer userId)
          throws SQLException
  {
    ArrayList<GroupMember> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "userid=" + userId);
    while(iter.hasNext())
    {
      result.add((GroupMember) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupMember> selectByTaskIdAndUserId(Connection conn,
          Integer taskId, Integer userId)
          throws NoSuchItemException, SQLException
  {
    ArrayList<GroupMember> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + "and userid=" + userId);
    while(iter.hasNext())
    {
      result.add((GroupMember) iter.next());
    }
    return result;
  }

  public static ArrayList<GroupMember> selectByTaskIdAndGroupId(Connection conn,
          Integer taskId, Integer groupId) throws SQLException,
          InvalidValueException, NoSuchItemException
  {

    ArrayList<GroupMember> result = new ArrayList<GroupMember>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " and groupid=" + groupId);
    while(iter.hasNext())
    {
      result.add((GroupMember) iter.next());
    }
    return result;
  }

  public static GroupMember select1ByTaskIdAndGroupIdAndUserId(Connection conn,
          Integer taskId, Integer groupId, Integer userId) throws SQLException,
                                                                  InvalidValueException,
                                                                  NoSuchItemException
  {

    GroupMember result = null;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " and groupid=" + groupId + " and userid="
            + userId);
    if(iter.hasNext())
    {
      result = (GroupMember) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
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
          ObjectNotValidException, NoSuchItemException
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

}
