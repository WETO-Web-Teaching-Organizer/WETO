package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;

public class ClusterIdReplication extends BeanClusterIdReplication
{
  public static ClusterIdReplication select1ByMasterDbClusterId(Connection conn,
          Integer masterDbClusterId)
          throws NoSuchItemException, SQLException
  {
    ClusterIdReplication result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "masterdbclusterid=" + masterDbClusterId);
    if(iter.hasNext())
    {
      result = (ClusterIdReplication) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static ClusterIdReplication select1ByCourseDbClusterId(Connection conn,
          Integer courseDbClusterId)
          throws NoSuchItemException, SQLException
  {
    ClusterIdReplication result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "coursedbclusterid=" + courseDbClusterId);
    if(iter.hasNext())
    {
      result = (ClusterIdReplication) iter.next();
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
