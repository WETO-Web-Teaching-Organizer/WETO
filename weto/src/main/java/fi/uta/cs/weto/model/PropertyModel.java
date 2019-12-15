package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.db.Property;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class PropertyModel
{
  public static Property getPendingStudent(Connection conn, Integer courseTaskId,
          Integer masterUserId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    return Property.select1ByTypeAndRefIdAndKey(conn,
            PropertyType.PENDING_STUDENTS.getValue(), courseTaskId, masterUserId);
  }

  public static ArrayList<Property> getPendingStudents(Connection conn,
          Integer courseTaskId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    return Property.selectByTypeAndRefId(conn, PropertyType.PENDING_STUDENTS
            .getValue(), courseTaskId);
  }

  public static void insertPendingStudent(Connection conn, Integer courseTaskId,
          Integer masterUserId, String groupId)
          throws InvalidValueException, ObjectNotValidException, SQLException
  {
    Property pending = new Property();
    pending.setType(PropertyType.PENDING_STUDENTS.getValue());
    pending.setRefId(courseTaskId);
    pending.setKey(masterUserId);
    pending.setValue(groupId);
    pending.insert(conn);
  }

  public static Property getNavigationTreeUpdate(Connection conn,
          Integer courseTaskId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    return Property.select1ByTypeAndRefIdAndKey(conn,
            PropertyType.UPDATE_NAVIGATION_TREE.getValue(), courseTaskId, 0);
  }

  public static void updateNavigationTreeUpdate(Connection conn,
          Integer courseTaskId)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 WetoTimeStampException, NoSuchItemException
  {
    Property pending;
    boolean doUpdate = false;
    try
    {
      pending = getNavigationTreeUpdate(conn, courseTaskId);
      doUpdate = true;
    }
    catch(NoSuchItemException e)
    {
      pending = new Property();
      pending.setType(PropertyType.UPDATE_NAVIGATION_TREE.getValue());
      pending.setRefId(courseTaskId);
      pending.setKey(0);
    }
    pending.setValue(new WetoTimeStamp().getTimeStamp().toString());
    if(doUpdate)
    {
      pending.update(conn);
    }
    else
    {
      pending.insert(conn);
    }
  }

}
