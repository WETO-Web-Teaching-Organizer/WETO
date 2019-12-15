package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Log extends BeanLog
{
  public Log()
  {
    super();
  }

  public Log(Integer courseTaskId, Integer taskId, Integer userId, Integer event,
          Integer par1,
          Integer par2, String address) throws InvalidValueException
  {
    super();
    setCourseTaskId(courseTaskId);
    setTaskId(taskId);
    setUserId(userId);
    setEvent(event);
    setPar1(par1);
    setPar2(par2);
    setAddress(address);
  }

  public static ArrayList<Log> selectByConstraints(Connection conn,
          Integer courseTaskId, Integer userId, Integer taskId, Integer eventId,
          Integer from, Integer to, Integer limit)
          throws SQLException
  {
    ArrayList<Log> result = new ArrayList<>();
    StringBuilder constraints
            = new StringBuilder("coursetaskid=" + courseTaskId);
    if(userId != null)
    {
      constraints.append(" AND userid=" + userId);
    }
    if(taskId != null)
    {
      constraints.append(" AND taskid=" + taskId);
    }
    if(eventId != null)
    {
      constraints.append(" AND event=" + eventId);
    }
    if(from != null)
    {
      constraints.append(" AND timestamp>=" + from);
    }
    if(to != null)
    {
      constraints.append(" AND timestamp<=" + to);
    }
    constraints.append(" ORDER BY timestamp DESC LIMIT " + limit);
    Iterator<?> iter = selectionIterator(conn, constraints.toString());
    while(iter.hasNext())
    {
      result.add((Log) iter.next());
    }
    return result;
  }

  public static void deleteByTaskId(Connection conn, Integer taskId) throws
          SQLException
  {
    String prepareString = "delete from log where taskid = ?";
    try(PreparedStatement ps = conn.prepareStatement(prepareString))
    {
      ps.setObject(1, taskId);
      ps.executeUpdate();
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

  public String getTimeStampString() throws WetoTimeStampException
  {
    String time = new WetoTimeStamp(getTimeStamp()).toString();
    int secondsBoundary = time.lastIndexOf(':');
    return time.substring(0, secondsBoundary);
  }

}
