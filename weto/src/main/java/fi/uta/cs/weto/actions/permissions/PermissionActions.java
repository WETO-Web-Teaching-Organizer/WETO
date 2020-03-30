package fi.uta.cs.weto.actions.permissions;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionRefType;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PermissionActions
{
  public static class View extends WetoTeacherAction
  {
    private ArrayList<UserTaskView> users;
    private Map<Integer, ArrayList<Permission>> usersPermissions;
    private Map<UserAccount, ArrayList<Permission>> removedUsersPermissions;
    private ArrayList<Permission> allUsersPermissions;
    private Map<Integer, String> types;
    private boolean noPermissions;

    public View()
    {
      super(Tab.PERMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      users = UserTaskView.selectUniqueByTaskId(conn, taskId);
      ArrayList<Permission> permissions = Permission.selectByTaskId(conn,
              taskId);
      noPermissions = permissions.isEmpty();
      usersPermissions = new HashMap<>();
      removedUsersPermissions = new HashMap<>();
      allUsersPermissions = new ArrayList<>();
      for(UserTaskView user : users)
      {
        usersPermissions.put(user.getUserId(), new ArrayList<Permission>());
      }
      for(Permission permission : permissions)
      {
        Integer userId = permission.getUserRefId();
        if(userId != null)
        {
          if(PermissionRefType.USER.getValue().equals(permission
                  .getUserRefType()))
          {
            ArrayList<Permission> userPermissions = usersPermissions
                    .get(userId);
            if(userPermissions != null)
            {
              userPermissions.add(permission);
            }
            else
            {
              try
              {
                UserAccount removedUser = UserAccount.select1ById(conn, userId);
                userPermissions = removedUsersPermissions.get(removedUser);
                if(userPermissions == null)
                {
                  userPermissions = new ArrayList<>();
                  removedUsersPermissions.put(removedUser, userPermissions);
                }
                userPermissions.add(permission);
              }
              catch(NoSuchItemException e)
              {
                //PermissionModel.deleteCoursePermission(conn,
                //getMasterConnection(), permission);
              }
            }
          }
        }
        else
        {
          allUsersPermissions.add(permission);
        }
      }
      types = PermissionType.getTypeMap();
      return INPUT;
    }

    public ArrayList<UserTaskView> getUsers()
    {
      return users;
    }

    public Map<Integer, ArrayList<Permission>> getUsersPermissions()
    {
      return usersPermissions;
    }

    public ArrayList<Permission> getAllUsersPermissions()
    {
      return allUsersPermissions;
    }

    public Map<UserAccount, ArrayList<Permission>> getRemovedUsersPermissions()
    {
      return removedUsersPermissions;
    }

    public Map<Integer, String> getTypes()
    {
      return types;
    }

    public boolean isNoPermissions()
    {
      return noPermissions;
    }

  }

  public static class ViewAll extends WetoTeacherAction
  {
    private ArrayList<String> tasks;
    private ArrayList<Integer> taskIds;
    private ArrayList<ArrayList<UserAccount>> users;
    private ArrayList<ArrayList<Permission>> permissions;
    private Map<Integer, String> types;
    private boolean noPermissions;

    public ViewAll()
    {
      super(Tab.PERMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      tasks = new ArrayList<>();
      taskIds = new ArrayList<>();
      users = new ArrayList<>();
      permissions = new ArrayList<>();
      ArrayDeque<Integer> taskQueue = new ArrayDeque<>();
      taskQueue.addFirst(getTaskId());
      noPermissions = true;
      while(!taskQueue.isEmpty())
      {
        Integer taskId = taskQueue.pollFirst();
        ArrayList<Permission> taskPermissions = Permission.selectByTaskId(
                conn, taskId);
        if(!taskPermissions.isEmpty())
        {
          noPermissions = false;
          Collections.sort(taskPermissions, new Comparator<Permission>()
          {
            @Override
            public int compare(Permission o1, Permission o2)
            {
              int result = 0;
              Integer a = o1.getUserRefId();
              Integer b = o2.getUserRefId();
              if(a != null)
              {
                result = (b == null) ? 1 : a.compareTo(b);
              }
              else if(b != null)
              {
                result = -1;
              }
              return result;
            }

          });
          Task task = Task.select1ById(conn, taskId);
          tasks.add(task.getName());
          taskIds.add(taskId);
          ArrayList<UserAccount> taskUsers = new ArrayList<>();
          for(Permission permission : taskPermissions)
          {
            Integer userId = permission.getUserRefId();
            if(userId != null)
            {
              taskUsers
                      .add(UserAccount.select1ById(conn, userId));
            }
            else
            {
              taskUsers.add(null);
            }
          }
          users.add(taskUsers);
          permissions.add(taskPermissions);
        }
        ArrayList<Integer> subtaskIds = Task.selectSubtaskIds(conn, taskId);
        for(int i = subtaskIds.size() - 1; i >= 0; --i)
        {
          taskQueue.addFirst(subtaskIds.get(i));
        }
      }
      types = PermissionType.getTypeMap();
      return SUCCESS;
    }

    public ArrayList<String> getTasks()
    {
      return tasks;
    }

    public ArrayList<Integer> getTaskIds()
    {
      return taskIds;
    }

    public ArrayList<ArrayList<UserAccount>> getUsers()
    {
      return users;
    }

    public ArrayList<ArrayList<Permission>> getPermissions()
    {
      return permissions;
    }

    public Map<Integer, String> getTypes()
    {
      return types;
    }

    public boolean isNoPermissions()
    {
      return noPermissions;
    }

  }

  public static class Edit extends PermissionBean
  {
    public Edit()
    {
      super(Tab.PERMISSIONS.getBit(), Tab.PERMISSIONS.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      setUsers(UserTaskView.selectUniqueByTaskId(conn, taskId));
      // Are we creating a new permission?
      if(getPermissionId() == null)
      {
        // Set input data for the editing page.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        setStartDate(dateFormat.format(cal.getTime()));
        setStartHours(cal.get(Calendar.HOUR_OF_DAY));
        setStartMinutes(cal.get(Calendar.MINUTE));
        setPermissionType(new Integer[]
        {
          PermissionType.SUBMISSION.getValue()
        });
      }
      else // Edit an existing permission
      {
        Permission permission = Permission.select1ById(conn,
                getPermissionId());
        validateCourseSubtaskId(permission.getTaskId());
        Integer userId = permission.getUserRefId();
        String loginName = "";
        if(userId != null)
        {
          loginName = UserAccount.select1ById(conn, userId).getLoginName();
        }
        setUserLoginName(new String[]
        {
          loginName
        });
        setPermissionType(new Integer[]
        {
          permission.getType()
        });
        Integer startTimeStamp = permission.getStartDate();
        if(startTimeStamp != null)
        {
          WetoTimeStamp t = new WetoTimeStamp(startTimeStamp);
          setStartDate(t.getDateString());
          setStartHours(t.getHour());
          setStartMinutes(t.getMinute());
        }
        Integer endTimeStamp = permission.getEndDate();
        if(endTimeStamp != null)
        {
          WetoTimeStamp t = new WetoTimeStamp(endTimeStamp);
          setEndDate(t.getDateString());
          setEndHours(t.getHour());
          setEndMinutes(t.getMinute());
        }
        setPermissionDetail(permission.getDetail());
      }
      setPermissionTypes(PermissionType.values());
      return INPUT;
    }

  }

  public static class Save extends PermissionBean
  {
    public Save()
    {
      super(Tab.PERMISSIONS.getBit(), Tab.PERMISSIONS.getBit(),
              Tab.PERMISSIONS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      final Connection conn = getCourseConnection();
      final Connection masterConn = getMasterConnection();
      final Integer taskId = getTaskId();
      // First verify the form contents
      String errorMessage = null;
      if((getUserLoginName() == null) || (getUserLoginName().length < 1))
      {
        errorMessage = getText("editPermission.error.noUser");
      }
      if((getPermissionType() == null) || (getPermissionType().length < 1))
      {
        errorMessage = getText("editPermission.error.noType");
      }
      SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
      Integer startTimeStamp = null;
      if(!getStartDate().equals(""))
      {
        Calendar gc = new GregorianCalendar();
        gc.setTime(dateFormatter.parse(getStartDate()));
        gc.set(Calendar.HOUR_OF_DAY, getStartHours());
        gc.set(Calendar.MINUTE, getStartMinutes());
        WetoTimeStamp t = new WetoTimeStamp(gc);
        startTimeStamp = t.getTimeStamp();
      }
      Integer endTimeStamp = null;
      if(!getEndDate().equals(""))
      {
        Calendar gc = new GregorianCalendar();
        gc.setTime(dateFormatter.parse(getEndDate()));
        gc.set(Calendar.HOUR_OF_DAY, getEndHours());
        gc.set(Calendar.MINUTE, getEndMinutes());
        WetoTimeStamp t = new WetoTimeStamp(gc);
        endTimeStamp = t.getTimeStamp();
      }
      if((startTimeStamp != null) && (endTimeStamp != null) && (startTimeStamp
              > endTimeStamp))
      {
        errorMessage = getText("editPermission.error.endDate");
      }
      String permissionDetail = getPermissionDetail();
      if((permissionDetail != null) && permissionDetail.isEmpty())
      {
        permissionDetail = null;
      }
      if(errorMessage != null)
      {
        // Populate information for the view.
        Map<Integer, PermissionType> types = new HashMap<>();
        PermissionType[] typesList = PermissionType.values();
        for(PermissionType type : typesList)
        {
          types.put(type.getValue(), type);
        }
        setUsers(UserTaskView.selectUniqueByTaskId(conn, taskId));
        throw new WetoActionException(errorMessage, INPUT);
      }
      CourseImplementation ci = null;
      final Integer dbId = getDbId();
      try
      {
        ci = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterConn,
                dbId, taskId);
      }
      catch(NoSuchItemException e)
      { // OK: this is not a course root task.
      }
      Permission permission = null;
      // Is this a new permission?
      if(getPermissionId() != null)
      {
        permission = Permission.select1ById(conn, getPermissionId());
      }
      for(String userLoginName : getUserLoginName())
      {
        Integer userRefId = null;
        if(!userLoginName.equals(""))
        {
          // Not empty: locate the corresponding user and set the user id.
          UserAccount user = UserAccount.select1ByLoginName(conn, userLoginName);
          userRefId = user.getId();
        }
        for(Integer permissionType : getPermissionType())
        {
          boolean doUpdate = false;
          // Do we need to fetch or create a permission?
          if(permission == null)
          {
            try // Try to reuse an existing permission of the same type.
            {
              permission = Permission.select1ByTaskIdAndUserIdAndType(conn,
                      taskId, userRefId, permissionType);
              doUpdate = true;

            }
            catch(NoSuchItemException e)
            { // Could not reuse an existing permission, so create a new one.
              permission = new Permission();
              permission.setUserRefType(PermissionRefType.USER.getValue());
              permission.setTaskId(taskId);
            }
          }
          else
          {
            doUpdate = true;
          }
          permission.setUserRefId(userRefId);
          permission.setType(permissionType);
          permission.setStartDate(startTimeStamp);
          permission.setEndDate(endTimeStamp);
          permission.setDetail(permissionDetail);
          if(doUpdate)
          {
            permission.update(conn);
          }
          else
          {
            permission.insert(conn);
          }
          if(ci != null) // A course root task? If yes, update also master database.
          {
            PermissionModel.replicateRootPermission(masterConn, conn, dbId,
                    permission);
          }
          permission = null;
        }
      }
      addActionMessage(getText("editPermission.message.updateSuccess"));
      return (getAllViewTaskId() != null) ? "allView" : SUCCESS;
    }

  }

  public static class Delete extends PermissionBean
  {
    public Delete()
    {
      super(Tab.PERMISSIONS.getBit(), 0, 0, Tab.PERMISSIONS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Connection masterConn = getMasterConnection();
      Permission permission = Permission.select1ById(courseConn,
              getPermissionId());
      validateCourseSubtaskId(permission.getTaskId());
      PermissionModel.deleteCoursePermission(courseConn, masterConn, permission);
      addActionMessage(getText("editPermission.message.deleteSuccess"));
      return (getAllViewTaskId() != null) ? "allView" : SUCCESS;
    }

  }

  private static abstract class PermissionBean extends WetoTeacherAction
  {
    private Integer permissionId;
    private Integer[] permissionType;
    private ArrayList<UserTaskView> users;
    private PermissionType[] permissionTypes;
    private String[] userLoginName;
    private String startDate;
    private String endDate;
    private Integer startHours = 0;
    private Integer startMinutes = 0;
    private Integer endHours = 0;
    private Integer endMinutes = 0;
    private String permissionDetail;

    private Integer allViewTaskId;

    public PermissionBean(int reqOwnerViewBits, int reqOwnerUpdateBits,
            int reqOwnerCreateBits, int reqOwnerDeleteBits)
    {
      super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits,
              reqOwnerDeleteBits);
    }

    public Integer getPermissionId()
    {
      return permissionId;
    }

    public void setPermissionId(Integer permissionId)
    {
      this.permissionId = permissionId;
    }

    public Integer[] getPermissionType()
    {
      return permissionType;
    }

    public void setPermissionType(Integer[] permissionType)
    {
      this.permissionType = permissionType;
    }

    public ArrayList<UserTaskView> getUsers()
    {
      return users;
    }

    public void setUsers(ArrayList<UserTaskView> users)
    {
      this.users = users;
    }

    public PermissionType[] getPermissionTypes()
    {
      return permissionTypes;
    }

    public void setPermissionTypes(PermissionType[] permissionTypes)
    {
      this.permissionTypes = permissionTypes;
    }

    public String[] getUserLoginName()
    {
      return userLoginName;
    }

    public void setUserLoginName(String[] userLoginName)
    {
      this.userLoginName = userLoginName;
    }

    public String getStartDate()
    {
      return startDate;
    }

    public void setStartDate(String startDate)
    {
      this.startDate = startDate;
    }

    public String getEndDate()
    {
      return endDate;
    }

    public void setEndDate(String endDate)
    {
      this.endDate = endDate;
    }

    public Integer getStartHours()
    {
      return startHours;
    }

    public void setStartHours(Integer startHours)
    {
      this.startHours = startHours;
    }

    public Integer getStartMinutes()
    {
      return startMinutes;
    }

    public void setStartMinutes(Integer startMinutes)
    {
      this.startMinutes = startMinutes;
    }

    public Integer getEndHours()
    {
      return endHours;
    }

    public void setEndHours(Integer endHours)
    {
      this.endHours = endHours;
    }

    public Integer getEndMinutes()
    {
      return endMinutes;
    }

    public void setEndMinutes(Integer endMinutes)
    {
      this.endMinutes = endMinutes;
    }

    public String getPermissionDetail()
    {
      return permissionDetail;
    }

    public void setPermissionDetail(String permissionDetail)
    {
      this.permissionDetail = permissionDetail;
    }

    public Integer getAllViewTaskId()
    {
      return allViewTaskId;
    }

    public void setAllViewTaskId(Integer allViewTaskId)
    {
      this.allViewTaskId = allViewTaskId;
    }

  }

}
