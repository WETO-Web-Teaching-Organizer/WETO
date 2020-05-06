package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.PermissionRefType;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class Permission extends BeanPermission
{
  static final int USER_REF_TYPE = PermissionRefType.USER.getValue();

  /**
   * Retrieve permission with given id.
   *
   * @param conn open database connection
   * @param permissionId permission identifier
   * @return permission
   * @throws InvalidValueException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   * @throws SQLException if the JDBC operation fails.
   */
  public static Permission select1ById(Connection conn, Integer permissionId)
          throws InvalidValueException, NoSuchItemException, SQLException
  {
    Permission permission = new Permission();
    permission.setId(permissionId);
    permission.select(conn);
    return permission;
  }

  /**
   * Retrieve permissions for a given task and user
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @param userId user identifier
   * @return permissions
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Permission> selectByTaskIdAndUserId(Connection conn,
          Integer taskId, Integer userId)
          throws SQLException
  {
    ArrayList<Permission> result = new ArrayList<>();
    Iterator<?> iter = Permission.selectionIterator(conn, "taskid=" + taskId
            + " AND userrefid=" + userId + " AND userreftype=" + USER_REF_TYPE);
    while(iter.hasNext())
    {
      Permission permission = (Permission) iter.next();
      result.add(permission);
    }
    return result;
  }
  //Select all permissions which are still active
  public static ArrayList<Permission> selectActive(Connection conn) throws SQLException, WetoTimeStampException {
    ArrayList<Permission> result = new ArrayList<>();

    int nowStamp = new WetoTimeStamp(new GregorianCalendar()).getTimeStamp();
    Iterator<?> iter = selectionIterator(conn,
            "(startdate IS NULL OR startdate <= " + nowStamp
                    + ") AND (enddate IS NULL OR enddate >= " + nowStamp + ")");
    while (iter.hasNext()) {
      result.add((Permission) iter.next());
    }
    return result;

  }
  public static ArrayList<Permission> selectByCourseTaskIdAndUserId(
          Connection conn, Integer courseTaskId, Integer userId)
          throws SQLException, InvalidValueException
  {
    ArrayList<Permission> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT permission.id, userrefid, userreftype, taskid, type,"
              + " startdate, enddate, detail, permission.timestamp FROM"
              + " permission, task WHERE userrefid=" + userId
              + " AND userreftype=" + USER_REF_TYPE
              + " AND permission.taskid=task.id AND "
              + " task.roottaskid=" + courseTaskId);
      while(rs.next())
      {
        Permission p = new Permission();
        p.setFromResultSet(rs, 0);
        result.add(p);
      }
    }
    return result;
  }

  /**
   * Retrieve permissions for a given task.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @return permissions
   * @throws SQLException if the JDBC operation fails.
   */

  public boolean isActive() throws WetoTimeStampException {
    int nowStamp = new WetoTimeStamp(new GregorianCalendar()).getTimeStamp();

    if (nowStamp >= this.getStartDate() && nowStamp <= this.getEndDate())  {
      return true;
    } else {
      return false;
    }

  }

  public static ArrayList<Permission> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Permission> result = new ArrayList<>();
    Iterator<?> iter = Permission.selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      Permission permission = (Permission) iter.next();
      result.add(permission);
    }
    return result;
  }

  public static ArrayList<Permission> selectByCourseTaskIdAndUserIdAndType(
          Connection conn, Integer courseTaskId, Integer userId,
          Integer permissionType)
          throws SQLException, InvalidValueException
  {
    ArrayList<Permission> result = new ArrayList<>();
    if(userId != null)
    {
      try(Statement stat = conn.createStatement())
      {
        ResultSet rs = stat.executeQuery(
                "SELECT permission.id, userrefid, userreftype, taskid, type,"
                + " startdate, enddate, detail, permission.timestamp FROM"
                + " permission, task WHERE userrefid=" + userId
                + " AND userreftype=" + USER_REF_TYPE
                + " AND type=" + permissionType
                + " AND permission.taskid=task.id AND "
                + " task.roottaskid=" + courseTaskId);
        while(rs.next())
        {
          Permission p = new Permission();
          p.setFromResultSet(rs, 0);
          result.add(p);
        }
      }
    }
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT permission.id, userrefid, userreftype, taskid, type,"
              + " startdate, enddate, detail, permission.timestamp FROM"
              + " permission, task WHERE userrefid is null"
              + " AND type=" + permissionType
              + " AND permission.taskid=task.id AND "
              + " task.roottaskid=" + courseTaskId);
      while(rs.next())
      {
        Permission p = new Permission();
        p.setFromResultSet(rs, 0);
        result.add(p);
      }
    }
    return result;
  }

  /**
   * Return permissions for user need to be checked for user for given
   * permission type.
   *
   * @param conn open database connection
   * @param taskId task identifier
   * @param userId user identifier
   * @param permissionType permission type
   * @return permissions in order that they need to be checked
   * @throws fi.uta.cs.sqldatamodel.NoSuchItemException
   * @throws SQLException if the JDBC operation fails.
   */
  public static Permission select1ByTaskIdAndUserIdAndType(Connection conn,
          Integer taskId, Integer userId, Integer permissionType)
          throws NoSuchItemException, SQLException
  {
    Permission result;
    SqlSelectionIterator iter;
    if(userId == null)
    {
      iter = (SqlSelectionIterator) selectionIterator(conn, "taskid=" + taskId
              + " AND userrefid is null AND type=" + permissionType);
    }
    else
    {
      iter = (SqlSelectionIterator) selectionIterator(conn, "taskid=" + taskId
              + " AND userrefid=" + userId + " AND userreftype=" + USER_REF_TYPE
              + " AND type=" + permissionType);
    }
    if(iter.hasNext())
    {
      result = (Permission) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  /**
   * Inserts this object to the database table Permission.
   *
   * @param conn Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
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

  public String getStartDateString()
  {
    String result;
    try
    {
      WetoTimeStamp t = new WetoTimeStamp(getStartDate());
      result = t.getDateString();
    }
    catch(Exception e)
    {
      result = null;
    }
    return result;
  }

  public String getStartTime()
  {
    String result;
    try
    {
      WetoTimeStamp t = new WetoTimeStamp(getStartDate());
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);
      ps.format("%02d:%02d", t.getHour(), t.getMinute());
      result = baos.toString();
    }
    catch(Exception e)
    {
      result = null;
    }
    return result;
  }

  public String getStartTimeStampString() throws WetoTimeStampException
  {
    String result = null;
    if(getStartDate() != null)
    {
      String time = new WetoTimeStamp(getStartDate()).toString();
      int secondsBoundary = time.lastIndexOf(':');
      result = time.substring(0, secondsBoundary);
    }
    return result;
  }

  public String getEndDateString()
  {
    String result;
    try
    {
      WetoTimeStamp t = new WetoTimeStamp(getEndDate());
      result = t.getDateString();
    }
    catch(Exception e)
    {
      result = null;
    }
    return result;
  }

  public String getEndTime()
  {
    String result;
    try
    {
      WetoTimeStamp t = new WetoTimeStamp(getEndDate());
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);
      ps.format("%02d:%02d", t.getHour(), t.getMinute());
      result = baos.toString();
    }
    catch(Exception e)
    {
      result = null;
    }
    return result;
  }

  public String getEndTimeStampString() throws WetoTimeStampException
  {
    String result = null;
    if(getEndDate() != null)
    {
      String time = new WetoTimeStamp(getEndDate()).toString();
      int secondsBoundary = time.lastIndexOf(':');
      result = time.substring(0, secondsBoundary);
    }
    return result;
  }

}
