package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class RightsCluster extends BeanRightsCluster
{
  public static RightsCluster select1ById(Connection conn, Integer clusterId)
          throws NoSuchItemException, SQLException
  {
    RightsCluster result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + clusterId);
    if(iter.hasNext())
    {
      result = (RightsCluster) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static ArrayList<RightsCluster> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<RightsCluster> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((RightsCluster) iter.next());
    }
    return result;
  }

  public static RightsCluster select1ByTaskIdAndType(Connection conn,
          Integer taskId, Integer clusterType)
          throws NoSuchItemException, SQLException
  {
    RightsCluster result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " AND type=" + clusterType);
    if(iter.hasNext())
    {
      result = (RightsCluster) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  @Override
  public void insert(Connection con)
          throws SQLException, ObjectNotValidException
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
  public void update(Connection con)
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
    super.update(con);
  }

}
