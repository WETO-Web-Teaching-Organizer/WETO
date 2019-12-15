package fi.uta.cs.weto.actions;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.Student;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.PropertyModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoMasterAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinCourseActions
{
  public static class View extends WetoMasterAction
  {
    private Integer taskId;
    private Integer dbId;
    private String courseName;
    private Map<Integer, String> groupList;
    private boolean alreadyPending;
    private boolean publicViewingOk;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      Integer masterTaskId = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, dbId, taskId)
              .getMasterTaskId();
      Integer masterUserId = getMasterUserId();
      // Verify that the user is not already a course member
      try
      {
        UserTaskView.select1ByTaskIdAndUserId(masterConn, masterTaskId,
                masterUserId);
        throw new WetoActionException(getText(
                "courseRegistration.message.alreadyMember"));
      }
      catch(NoSuchItemException e)
      { // Ok: was not yet a course member
      }
      CourseImplementation course = CourseImplementation.select1ByMasterTaskId(
              masterConn, masterTaskId);
      Task courseTask = Task.select1ById(masterConn, masterTaskId);
      courseName = courseTask.getName();
      // Is the course currently available for registering?
      String studentNumber = null;
      try
      {
        studentNumber = Student.select1ByUserId(masterConn, masterUserId)
                .getStudentNumber();
      }
      catch(NoSuchItemException e)
      {
      }
      UserAccount masterUser = getNavigator().getUser();
      if(checkPermittedSet(masterConn, masterUser, masterTaskId, studentNumber)
              || checkRegisterPermissions(masterConn, masterUser, masterTaskId))
      {
        Integer courseTaskId = course.getCourseTaskId();
        // Get the databasepool for course
        DatabasePool dbPoolEntry = new DatabasePool();
        dbPoolEntry.setId(course.getDatabaseId());
        dbPoolEntry.select(masterConn);
        String courseDbName = dbPoolEntry.getName();
        // Open connection to the course database.
        Connection courseConn = getConnection(courseDbName);
        groupList = new HashMap<>();
        ArrayList<UserGroup> groups = UserGroup
                .selectByTaskIdAndType(courseConn, courseTaskId,
                        GroupType.EXERCISE.getValue());
        for(UserGroup userGroup : groups)
        {
          groupList.put(userGroup.getId(), userGroup.getName());
        }
        try
        {
          PropertyModel
                  .getPendingStudent(courseConn, courseTaskId, masterUserId);
          alreadyPending = true;
        }
        catch(NoSuchItemException e)
        {
          alreadyPending = false;
        }
      }
      else
      { // A null groupList signals that the course is not available
        groupList = null;
      }
      publicViewingOk = courseTask.getIsPublic() && checkViewPermissions(
              masterConn, masterUserId, masterTaskId);
      return SUCCESS;
    }

    public Integer getTaskId()
    {
      return taskId;
    }

    public void setTaskId(Integer taskId)
    {
      this.taskId = taskId;
    }

    public Integer getDbId()
    {
      return dbId;
    }

    public void setDbId(Integer dbId)
    {
      this.dbId = dbId;
    }

    public String getCourseName()
    {
      return courseName;
    }

    public Map<Integer, String> getGroupList()
    {
      return groupList;
    }

    public boolean isAlreadyPending()
    {
      return alreadyPending;
    }

    public boolean isPublicViewingOk()
    {
      return publicViewingOk;
    }

  }

  public static class Commit extends WetoMasterAction
  {
    private Integer taskId;
    private Integer dbId;
    private Integer groupId;

    @Override
    public String action() throws Exception
    {
      String result = SUCCESS;
      Connection masterConn = getMasterConnection();
      CourseImplementation course = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, dbId, taskId);
      Integer masterTaskId = course.getMasterTaskId();
      UserAccount addUser = getNavigator().getUser();
      Integer masterUserId = addUser.getId();
      // Is the course currently available for registering and viewing?
      String studentNumber = null;
      try
      {
        studentNumber = Student.select1ByUserId(masterConn, masterUserId)
                .getStudentNumber();
      }
      catch(NoSuchItemException e)
      {
      }
      boolean inPermittedSet = checkPermittedSet(masterConn, addUser,
              masterTaskId, studentNumber);
      if(inPermittedSet || checkRegisterPermissions(masterConn, addUser,
              masterTaskId))
      {
        Connection courseConn = getConnection(dbId);
        if(course.getAcceptAllStudents() || inPermittedSet)
        {
          UserGroup addGroup = new UserGroup();
          addGroup.setTaskId(taskId);
          if(groupId != null)
          {
            addGroup.setId(groupId);
          }
          CourseMemberModel.addStudent(masterConn, courseConn, addUser, null,
                  addGroup, false, false);
          addActionMessage(getText("courseRegistration.message.registered"));
          result = "forward";
        }
        else
        {
          String groupIdStr = (groupId != null) ? groupId.toString() : null;
          try
          {
            Property pending = PropertyModel.getPendingStudent(courseConn,
                    taskId, masterUserId);
            pending.setValue(groupIdStr);
            pending.update(courseConn);
          }
          catch(NoSuchItemException e)
          {
            PropertyModel.insertPendingStudent(courseConn, taskId, masterUserId,
                    groupIdStr);
          }
          addActionMessage(getText("courseRegistration.message.pending"));
        }
      }
      else
      {
        throw new WetoActionException(getText(
                "courseRegistration.error.courseClosed"));
      }
      return result;
    }

    public void setTaskId(Integer taskId)
    {
      this.taskId = taskId;
    }

    public Integer getTaskId()
    {
      return taskId;
    }

    public void setDbId(Integer dbId)
    {
      this.dbId = dbId;
    }

    public Integer getDbId()
    {
      return dbId;
    }

    public Integer getTabId()
    {
      return Tab.MAIN.getValue();
    }

    public void setGroupId(Integer groupId)
    {
      this.groupId = groupId;
    }

  }

  private static boolean checkPermittedSet(Connection masterConn,
          UserAccount masterUser, Integer masterTaskId, String studentNumber)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, TooManyItemsException
  {
    return CourseMemberModel.getPermittedCourses(masterConn, masterUser,
            studentNumber).contains(masterTaskId);
  }

  private static boolean checkRegisterPermissions(Connection masterConn,
          UserAccount masterUser, Integer masterTaskId)
          throws SQLException, WetoTimeStampException
  {
    WetoTimeStamp[] registerPeriod = PermissionModel.getTaskTimeStampLimits(
            masterConn, masterUser.getId(), masterTaskId,
            PermissionType.REGISTER);
    return (PermissionModel.checkTimeStampLimits(registerPeriod)
            == PermissionModel.CURRENT);
  }

  private static boolean checkViewPermissions(Connection masterConn,
          Integer masterUserId, Integer masterTaskId)
          throws SQLException, WetoTimeStampException
  {
    WetoTimeStamp[] viewPeriod = PermissionModel.getTaskTimeStampLimits(
            masterConn, masterUserId, masterTaskId, PermissionType.VIEW);
    return (PermissionModel.checkTimeStampLimits(viewPeriod)
            == PermissionModel.CURRENT);
  }

}
