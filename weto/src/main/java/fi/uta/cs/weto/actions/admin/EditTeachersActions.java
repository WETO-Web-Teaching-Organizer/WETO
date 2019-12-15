package fi.uta.cs.weto.actions.admin;

import static com.opensymphony.xwork2.Action.SUCCESS;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.CourseView;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.Teacher;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.WetoAdminAction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class EditTeachersActions
{
  public static class View extends WetoAdminAction
  {
    private ArrayList<UserAccount> userList;
    private ArrayList<UserAccount> teachers;
    private ArrayList<CourseView> courses;
    private ArrayList<Object[]> courseTeachers;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      userList = UserAccount.selectAll(masterConn);
      Collections.sort(userList, new Comparator<UserAccount>()
      {
        @Override
        public int compare(UserAccount o1, UserAccount o2)
        {
          int result = o1.getLastName().compareToIgnoreCase(o2.getLastName());
          if(result == 0)
          {
            result = o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
          }
          if(result == 0)
          {
            result = o1.getLoginName().compareToIgnoreCase(o2.getLoginName());
          }
          return result;
        }

      });
      teachers = UserAccount.selectTeachers(masterConn);
      Collections.sort(teachers, new Comparator<UserAccount>()
      {
        @Override
        public int compare(UserAccount o1, UserAccount o2)
        {
          int result = o1.getLastName().compareToIgnoreCase(o2.getLastName());
          if(result == 0)
          {
            result = o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
          }
          if(result == 0)
          {
            result = o1.getLoginName().compareToIgnoreCase(o2.getLoginName());
          }
          return result;
        }

      });
      courses = CourseView.selectAll(masterConn);
      Collections.sort(courses, new Comparator<CourseView>()
      {
        @Override
        public int compare(CourseView o1, CourseView o2)
        {
          return o1.getName().compareTo(o2.getName());
        }

      });
      courseTeachers = new ArrayList<>();
      for(CourseView course : courses)
      {
        ArrayList<UserTaskView> aCourseTeachers = UserTaskView
                .selectByTaskIdAndClusterType(masterConn, course
                        .getMasterTaskId(), ClusterType.TEACHERS.getValue());
        Collections.sort(aCourseTeachers, new Comparator<UserTaskView>()
        {
          @Override
          public int compare(UserTaskView o1, UserTaskView o2)
          {
            int result = o1.getLastName().compareToIgnoreCase(o2.getLastName());
            if(result == 0)
            {
              result = o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
            }
            if(result == 0)
            {
              result = o1.getLoginName().compareToIgnoreCase(o2.getLoginName());
            }
            return result;
          }

        });
        for(UserTaskView aTeacher : aCourseTeachers)
        {
          Object[] tmp = new Object[2];
          tmp[0] = course.getName();
          tmp[1] = aTeacher;
          courseTeachers.add(tmp);
        }
      }
      return SUCCESS;
    }

    public ArrayList<UserAccount> getUserList()
    {
      return userList;
    }

    public ArrayList<UserAccount> getTeachers()
    {
      return teachers;
    }

    public ArrayList<CourseView> getCourses()
    {
      return courses;
    }

    public ArrayList<Object[]> getCourseTeachers()
    {
      return courseTeachers;
    }

  }

  public static class Add extends WetoAdminAction
  {
    private Integer[] teacherUserId;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      for(Integer addTeacherId : teacherUserId)
      {
        UserAccount user = UserAccount.select1ById(masterConn, addTeacherId);
        try
        {
          Teacher.select1ByUserId(masterConn, addTeacherId);
          // Feedback message "Teacher already in the database"
          addActionError(getText("teachers.message.teacherExisted",
                  new String[]
                  {
                    user.getLastName() + ", " + user.getFirstName()
                  }));
        }
        catch(NoSuchItemException e)
        {
          Teacher teacher = new Teacher();
          teacher.setUserId(addTeacherId);
          teacher.insert(masterConn);
          // Feedback message "New teacher added"
          addActionMessage(getText("teachers.message.teacherAdded",
                  new String[]
                  {
                    user.getLastName() + ", " + user.getFirstName()
                  }));
        }
      }
      return SUCCESS;
    }

    public void setTeacherUserId(Integer[] teacherUserId)
    {
      this.teacherUserId = teacherUserId;
    }

  }

  public static class Delete extends WetoAdminAction
  {
    private Integer teacherUserId;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      Connection courseConn;
      Map<Integer, Connection> connMap = new HashMap<>();
      Teacher.select1ByUserId(masterConn, teacherUserId).delete(masterConn);
      UserAccount deleteTeacher = UserAccount.select1ById(masterConn,
              teacherUserId);
      // Remove as teacher from all course teacher clusters.
      for(CourseView course : CourseView.selectAll(masterConn))
      {
        courseConn = connMap.get(course.getDatabaseId());
        if(courseConn == null)
        {
          courseConn = getConnection(course.getDatabaseId());
          connMap.put(course.getDatabaseId(), courseConn);
        }
        if(CourseMemberModel.deleteTeacher(masterConn, courseConn,
                course.getMasterTaskId(), teacherUserId))
        {
          UserIdReplication uir = UserIdReplication.select1ByMasterDbUserId(
                  courseConn, teacherUserId);
          try
          {
            Teacher.select1ByUserId(courseConn, uir.getCourseDbUserId()).delete(
                    courseConn);
          }
          catch(NoSuchItemException e)
          {
          }
          addActionMessage(getText("courseTeachers.message.teacherDeleted",
                  new String[]
                  {
                    deleteTeacher.getLastName() + ", " + deleteTeacher
                    .getFirstName(),
                    course.getName()
                  }));
        }
      }
      return SUCCESS;
    }

    public void setTeacherUserId(Integer teacherUserId)
    {
      this.teacherUserId = teacherUserId;
    }

  }

  public static class AddToCourse extends WetoAdminAction
  {
    private Integer[] teacherUserId;
    private Integer masterCourseTaskId;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      CourseImplementation course = CourseImplementation.select1ByMasterTaskId(
              masterConn, masterCourseTaskId);
      Connection courseConn = getConnection(course.getDatabaseId());
      final String courseName = Task.select1ById(masterConn, masterCourseTaskId)
              .getName();
      ArrayList<UserAccount> addedTeachers = CourseMemberModel.addTeachers(
              masterConn, courseConn, masterCourseTaskId, teacherUserId);
      for(UserAccount addedTeacher : addedTeachers)
      {
        addActionMessage(getText("courseTeachers.message.teacherAdded",
                new String[]
                {
                  addedTeacher.getLastName() + ", " + addedTeacher
                  .getFirstName(),
                  courseName
                }));
      }
      return SUCCESS;
    }

    public void setTeacherUserId(Integer[] teacherUserId)
    {
      this.teacherUserId = teacherUserId;
    }

    public void setCourseTaskId(Integer courseTaskId)
    {
      this.masterCourseTaskId = courseTaskId;
    }

  }

  public static class DeleteFromCourse extends WetoAdminAction
  {
    private Integer[] teacherUserId;
    private Integer masterCourseTaskId;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      CourseImplementation course = CourseImplementation.select1ByMasterTaskId(
              masterConn, masterCourseTaskId);
      Connection courseConn = getConnection(course.getDatabaseId());
      final String courseName = Task.select1ById(masterConn, masterCourseTaskId)
              .getName();
      for(Integer deleteTeacherId : teacherUserId)
      {
        if(CourseMemberModel.deleteTeacher(masterConn, courseConn,
                masterCourseTaskId, deleteTeacherId))
        {
          UserAccount deleteTeacher = UserAccount.select1ById(masterConn,
                  deleteTeacherId);
          addActionMessage(getText("courseTeachers.message.teacherDeleted",
                  new String[]
                  {
                    deleteTeacher.getLastName() + ", " + deleteTeacher
                    .getFirstName(),
                    courseName
                  }));
        }
      }
      return SUCCESS;
    }

    public void setTeacherUserId(Integer[] teacherUserId)
    {
      this.teacherUserId = teacherUserId;
    }

    public void setCourseTaskId(Integer courseTaskId)
    {
      this.masterCourseTaskId = courseTaskId;
    }

  }

}
