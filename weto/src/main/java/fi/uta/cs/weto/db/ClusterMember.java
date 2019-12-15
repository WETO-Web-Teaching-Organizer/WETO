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

public class ClusterMember extends BeanClusterMember
{
  public static ArrayList<ClusterMember> selectByClusterId(Connection conn,
          Integer clusterId)
          throws SQLException
  {
    ArrayList<ClusterMember> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "clusterid=" + clusterId);
    while(iter.hasNext())
    {
      result.add((ClusterMember) iter.next());
    }
    return result;
  }

  public static ArrayList<ClusterMember> selectByUserId(Connection conn,
          Integer userId)
          throws SQLException
  {
    ArrayList<ClusterMember> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "userid=" + userId);
    while(iter.hasNext())
    {
      result.add((ClusterMember) iter.next());
    }
    return result;
  }

  public static ClusterMember select1ByClusterIdAndUserId(Connection conn,
          Integer clusterId, Integer userId)
          throws NoSuchItemException, SQLException
  {
    ClusterMember result = null;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "clusterid=" + clusterId + " AND userid=" + userId);
    if(iter.hasNext())
    {
      result = (ClusterMember) iter.next();
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
