package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;

public class PermissionIdReplication extends BeanPermissionIdReplication
{
  public static PermissionIdReplication select1ByMasterDbPermissionId(
          Connection conn, Integer masterDbPermissionId)
          throws NoSuchItemException, SQLException
  {
    PermissionIdReplication result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "masterdbpermissionid=" + masterDbPermissionId);
    if(iter.hasNext())
    {
      result = (PermissionIdReplication) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static PermissionIdReplication select1ByCourseDbPermissionId(
          Connection conn, Integer courseDbPermissionId)
          throws NoSuchItemException, SQLException
  {
    PermissionIdReplication result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "coursedbpermissionid=" + courseDbPermissionId);
    if(iter.hasNext())
    {
      result = (PermissionIdReplication) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
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
    super.update(conn);
  }

}
