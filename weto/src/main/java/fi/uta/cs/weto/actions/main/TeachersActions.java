package fi.uta.cs.weto.actions.main;

import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;
import java.util.ArrayList;

public class TeachersActions
{
  public static class View extends WetoTeacherAction
  {
    private ArrayList<UserAccount> teachers = new ArrayList<>();
    private ArrayList<UserTaskView> courseTeachers = new ArrayList<>();

    public View()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      // Retrieve the list of all teachers in the master database.
      teachers = UserAccount
              .selectTeachers(getMasterConnection());
      // Retrieve the list of teachers that are already in the course.
      courseTeachers = UserTaskView.selectByTaskIdAndClusterType(
              getCourseConnection(), getNavigator().getCourseTaskId(),
              ClusterType.TEACHERS.getValue());
      return INPUT;
    }

    public ArrayList<UserAccount> getTeachers()
    {
      return teachers;
    }

    public ArrayList<UserTaskView> getCourseTeachers()
    {
      return courseTeachers;
    }

  }

  public static class Add extends WetoTeacherAction
  {
    private Integer[] masterTeacherIds;
    private ArrayList<UserAccount> teachers = new ArrayList<>();
    private ArrayList<UserTaskView> courseTeachers = new ArrayList<>();

    public Add()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Connection masterConn = getMasterConnection();
      Integer courseTaskId = getNavigator().getCourseTaskId();
      CourseImplementation ci = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                      courseTaskId);
      Integer masterCourseTaskId = ci.getMasterTaskId();
      ArrayList<UserAccount> addedTeachers = CourseMemberModel.addTeachers(
              masterConn, courseConn, masterCourseTaskId, masterTeacherIds);
      for(UserAccount addedTeacher : addedTeachers)
      {
        addActionMessage(getText("courseTeachers.message.teacherAdded",
                new String[]
                {
                  addedTeacher.getLastName() + ", " + addedTeacher
                  .getFirstName(),
                  getCourseName()
                }));
      }
      // Prepare teacher lists for the view.
      teachers = UserAccount.selectTeachers(masterConn);
      courseTeachers = UserTaskView.selectByTaskIdAndClusterType(
              courseConn, courseTaskId, ClusterType.TEACHERS.getValue());
      return SUCCESS;
    }

    public void setMasterTeacherIds(Integer[] masterTeacherIds)
    {
      this.masterTeacherIds = masterTeacherIds;
    }

    public ArrayList<UserAccount> getTeachers()
    {
      return teachers;
    }

    public ArrayList<UserTaskView> getCourseTeachers()
    {
      return courseTeachers;
    }

  }

  public static class Delete extends WetoTeacherAction
  {
    private Integer courseTeacherId;
    private ArrayList<UserAccount> teachers = new ArrayList<>();
    private ArrayList<UserTaskView> courseTeachers = new ArrayList<>();

    public Delete()
    {
      super(Tab.MAIN.getBit(), 0, 0, Tab.MAIN.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Connection masterConn = getMasterConnection();
      Integer courseTaskId = getNavigator().getCourseTaskId();
      CourseImplementation ci = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                      courseTaskId);
      Integer masterCourseTaskId = ci.getMasterTaskId();
      UserIdReplication uir = UserIdReplication.select1ByCourseDbUserId(
              courseConn, courseTeacherId);
      Integer masterTeacherId = uir.getMasterDbUserId();
      if(CourseMemberModel.deleteTeacher(masterConn, courseConn,
              masterCourseTaskId, masterTeacherId))
      {
        UserAccount deleteTeacher = UserAccount.select1ById(masterConn,
                masterTeacherId);
        addActionMessage(getText("courseTeachers.message.teacherDeleted",
                new String[]
                {
                  deleteTeacher.getLastName() + ", " + deleteTeacher
                  .getFirstName(), getCourseName()
                }));
      }
      else
      {
        throw new WetoActionException("", INPUT);
      }
      // Refresh teacher list
      teachers = UserAccount.selectTeachers(masterConn);
      courseTeachers = UserTaskView.selectByTaskIdAndClusterType(
              courseConn, courseTaskId, ClusterType.TEACHERS.getValue());
      return SUCCESS;
    }

    public void setCourseTeacherId(Integer courseTeacherId)
    {
      this.courseTeacherId = courseTeacherId;
    }

    public ArrayList<UserAccount> getTeachers()
    {
      return teachers;
    }

    public ArrayList<UserTaskView> getCourseTeachers()
    {
      return courseTeachers;
    }

  }
}
