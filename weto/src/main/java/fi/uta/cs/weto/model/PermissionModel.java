package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.PermissionIdReplication;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.SubtaskView;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PermissionModel
{
  public static final int FUTURE = -1;
  public static final int CURRENT = 0;
  public static final int PAST = 1;

  private static boolean permissionConditionOk(Connection conn,
          Permission permission, Integer userId)
          throws SQLException
  {
    Integer conditionTaskId = Integer.parseInt(permission.getDetail());
    return Grade.hasValidAggregateGrade(conn, conditionTaskId, userId);
  }

  private static void setWetoTimeStamps(Connection conn, Permission permission,
          Integer userId, WetoTimeStamp[] timeLimits)
          throws SQLException, WetoTimeStampException
  {
    if((permission == null) || ((permission.getDetail() != null)
            && !permissionConditionOk(conn, permission, userId)))
    { // No permission found or condition not ok: set to empty time interval
      timeLimits[1] = new WetoTimeStamp(WetoTimeStamp.STAMP_MIN);
    }
    else
    {
      if(permission.getStartDate() != null)
      {
        timeLimits[0] = new WetoTimeStamp(permission.getStartDate());
      }
      if(permission.getEndDate() != null)
      {
        timeLimits[1] = new WetoTimeStamp(permission.getEndDate());
      }
    }
  }

  public static WetoTimeStamp[] getTaskTimeStampLimits(Connection conn,
          Integer userId, Integer taskId, PermissionType type, boolean isTeacher)
          throws SQLException, WetoTimeStampException
  {
    Permission permission = null;
    WetoTimeStamp[] timeLimits = new WetoTimeStamp[2];
    if(!isTeacher) // Teachers always have rights: check only for other users
    {
      try
      {
        permission = Permission.select1ByTaskIdAndUserIdAndType(conn, taskId,
                userId, type.getValue());
      }
      catch(NoSuchItemException e)
      {
        if(userId != null)
        {
          try
          {
            permission = Permission
                    .select1ByTaskIdAndUserIdAndType(conn, taskId,
                            null, type.getValue());
          }
          catch(NoSuchItemException e2)
          {
          }
        }
      }
      setWetoTimeStamps(conn, permission, userId, timeLimits);
    }
    return timeLimits;
  }

  // A version that does not give special privileges for teachers
  public static WetoTimeStamp[] getTaskTimeStampLimits(Connection conn,
          Integer userId, Integer taskId, PermissionType type)
          throws SQLException, WetoTimeStampException
  {
    return getTaskTimeStampLimits(conn, userId, taskId, type, false);
  }

  /**
   * Get time limits for given permission type from the database. If time limits
   * aren't available in the given task parent tasks are searched until time
   * limits are found.
   *
   * @param conn open database connection
   * @param userId user identifier
   * @param taskId task identifier
   * @param type permission type
   * @return
   * @throws SQLException
   * @throws fi.uta.cs.weto.model.WetoTimeStampException
   */
  public static WetoTimeStamp[] getTimeStampLimits(Connection conn,
          Integer userId, Integer taskId, PermissionType type, boolean isTeacher)
          throws SQLException, WetoTimeStampException
  {
    Permission permission = null;
    WetoTimeStamp[] timeLimits = new WetoTimeStamp[2];
    if(!isTeacher) // Teachers always have rights: check only for other users
    {
      int currTaskId = taskId;
      while(true)
      {
        try
        {
          permission = Permission.select1ByTaskIdAndUserIdAndType(conn,
                  currTaskId, userId, type.getValue());
          break;
        }
        catch(NoSuchItemException e)
        {
          if((userId != null) && (permission == null))
          {
            try
            {
              permission = Permission.select1ByTaskIdAndUserIdAndType(conn,
                      currTaskId, null, type.getValue());
              break;
            }
            catch(NoSuchItemException e2)
            {
            }
          }
          try
          {
            currTaskId = SubtaskLink.select1BySubtaskId(conn, currTaskId)
                    .getContainerId();
          }
          catch(NoSuchItemException e2)
          {
            break;
          }
        }
      }
      setWetoTimeStamps(conn, permission, userId, timeLimits);
    }
    return timeLimits;
  }

  // A version that does not give special privileges for teachers
  public static WetoTimeStamp[] getTimeStampLimits(Connection conn,
          Integer userId, Integer taskId, PermissionType type)
          throws SQLException, WetoTimeStampException
  {
    return getTimeStampLimits(conn, userId, taskId, type, false);
  }

  public static int checkTimeStampLimits(WetoTimeStamp[] limits)
          throws SQLException, WetoTimeStampException
  {
    WetoTimeStamp now = new WetoTimeStamp();
    if(limits[0] != null && now.getTimeStamp() < limits[0].getTimeStamp())
    {
      return FUTURE;
    }
    else if(limits[1] != null && now.getTimeStamp() >= limits[1].getTimeStamp())
    {
      return PAST;
    }
    else
    {
      return CURRENT;
    }
  }

  public static int checkTimeStampLimits(WetoTimeStamp now,
          WetoTimeStamp[] limits)
          throws SQLException, WetoTimeStampException
  {
    if(limits[0] != null && now.getTimeStamp() < limits[0].getTimeStamp())
    {
      return FUTURE;
    }
    else if(limits[1] != null && now.getTimeStamp() >= limits[1].getTimeStamp())
    {
      return PAST;
    }
    else
    {
      return CURRENT;
    }
  }

  public static boolean viewPermissionActive(Connection conn, Integer taskId,
          Integer userId, boolean isTeacher)
          throws SQLException, WetoTimeStampException
  {
    boolean isActive = true;
    if(!isTeacher)
    {
      WetoTimeStamp[] viewPeriod = getTimeStampLimits(conn, userId, taskId,
              PermissionType.VIEW, false);
      isActive = (checkTimeStampLimits(viewPeriod) == PermissionModel.CURRENT);
    }
    return isActive;
  }

  public static HashSet<Integer> getViewableCourseSubtasks(Connection conn,
          Integer courseTaskId, final boolean isTeacher,
          HashMap<Integer, ArrayList<SubtaskView>> subtaskListMap,
          Integer userId)
          throws SQLException, InvalidValueException, WetoTimeStampException
  {
    HashSet<Integer> viewableSubtaskSet = new HashSet<>();
    if(!isTeacher)
    {
      ArrayList<Permission> courseViewPermissions = Permission
              .selectByCourseTaskIdAndUserIdAndType(conn, courseTaskId, userId,
                      PermissionType.VIEW.getValue());
      HashMap<Integer, Permission> permissionMap = new HashMap<>();
      for(Permission permission : courseViewPermissions)
      {
        Permission previous = permissionMap.get(permission.getTaskId());
        if((previous == null) || (previous.getUserRefId() == null))
        {
          permissionMap.put(permission.getTaskId(), permission);
        }
      }
      ArrayDeque<Integer> workQueue = new ArrayDeque<>();
      workQueue.add(courseTaskId);
      WetoTimeStamp now = new WetoTimeStamp();
      while(!workQueue.isEmpty())
      {
        Integer currTaskId = workQueue.removeFirst();
        Permission permission = permissionMap.get(currTaskId);
        WetoTimeStamp[] timeLimits = new WetoTimeStamp[2];
        if(permission != null)
        {
          setWetoTimeStamps(conn, permission, userId, timeLimits);
        }
        if(checkTimeStampLimits(now, timeLimits) == PermissionModel.CURRENT)
        {
          viewableSubtaskSet.add(currTaskId);
          ArrayList<SubtaskView> children = subtaskListMap.get(currTaskId);
          if(children != null)
          {
            for(SubtaskView child : children)
            {
              workQueue.add(child.getId());
            }
          }
        }
      }
    }
    else
    {
      viewableSubtaskSet.addAll(subtaskListMap.keySet());
      for(ArrayList<SubtaskView> childSubtasks : subtaskListMap.values())
      {
        for(SubtaskView subtask : childSubtasks)
        {
          viewableSubtaskSet.add(subtask.getId());
        }
      }
    }
    return viewableSubtaskSet;
  }

  public static void deleteCoursePermission(Connection courseConn,
          Connection masterConn, Permission permission)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 InvalidValueException
  {
    try
    {
      PermissionIdReplication pid = PermissionIdReplication
              .select1ByCourseDbPermissionId(courseConn, permission.getId());
      pid.delete(courseConn);
      Permission.select1ById(masterConn, pid.getMasterDbPermissionId()).delete(
              masterConn);
    }
    catch(NoSuchItemException e)
    {
    }
    permission.delete(courseConn);
  }

  public static void deleteAllCoursePermissions(Connection courseConn,
          Connection masterConn, Integer taskId)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 ObjectNotValidException, InvalidValueException
  {
    ArrayList<Permission> permissions = Permission.selectByTaskId(courseConn,
            taskId);
    for(Permission permission : permissions)
    {
      deleteCoursePermission(courseConn, masterConn, permission);
    }
  }

}
