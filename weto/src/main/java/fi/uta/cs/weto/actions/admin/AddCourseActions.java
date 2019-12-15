package fi.uta.cs.weto.actions.admin;

import fi.uta.cs.weto.db.ClusterMember;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.RightsCluster;
import fi.uta.cs.weto.db.Subject;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.WetoAdminAction;
import java.sql.Connection;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public abstract class AddCourseActions
{
  public static class Input extends FormBean
  {
    private ArrayList<UserAccount> teachers;
    private ArrayList<Subject> subjects;
    private ArrayList<DatabasePool> databases;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      teachers = UserAccount.selectTeachers(masterConn);
      subjects = Subject.selectIdsAndNames(masterConn);
      databases = DatabasePool.selectIdsAndNames(masterConn);
      return SUCCESS;
    }

    public ArrayList<UserAccount> getTeachers()
    {
      return teachers;
    }

    public ArrayList<Subject> getSubjects()
    {
      return subjects;
    }

    public ArrayList<DatabasePool> getDatabases()
    {
      return databases;
    }

  }

  public static class Confirm extends FormBean
  {
    @Override
    public String action() throws Exception
    {
      return SUCCESS;
    }

  }

  public static class Commit extends FormBean
  {
    private static final Logger logger = Logger.getLogger(Commit.class);

    @Override
    public String action() throws Exception
    {
      // The strings with names "*IdAndName" are of form "id=name".
      // The id and name are parsed below with the split method.
      String[] courseTeacher = getMainTeacherIdAndName().split("=", 2);
      Integer teacherId = new Integer(courseTeacher[0]);
      String[] courseDatabase = getDatabaseIdAndName().split("=", 2);
      Integer courseDatabaseId = new Integer(courseDatabase[0]);
      String courseDatabaseName = courseDatabase[1];
      String[] subject = getSubjectIdAndName().split("=", 2);
      Integer subjectId = new Integer(subject[0]);
      Connection masterConn = getMasterConnection();
      Connection courseConn = getConnection(courseDatabaseName);
      Task newTask = new Task();
      newTask.setName(getCourseName());
      newTask.insertAsRootTask(masterConn);
      CourseImplementation ci = new CourseImplementation();
      ci.setMasterTaskId(newTask.getId());
      ci.setSubjectId(subjectId);
      ci.setDatabaseId(courseDatabaseId);
      // ci will be inserted some steps below
      RightsCluster studentCluster = CourseMemberModel
              .createMasterStudentCluster(masterConn, newTask);
      RightsCluster assistantCluster = CourseMemberModel
              .createMasterAssistantCluster(masterConn, newTask);
      RightsCluster teacherCluster = CourseMemberModel
              .createMasterTeacherCluster(masterConn, newTask);
      // Add the teacher to the teacher's cluster
      ClusterMember cm = new ClusterMember();
      cm.setClusterId(teacherCluster.getId());
      cm.setUserId(teacherId);
      cm.insert(masterConn);
      // Create viewing permissions for all users.
      CourseMemberModel.createDefaultPermissions(masterConn, newTask.getId(),
              getStartDate(), getEndDate());
      CourseMemberModel.prepareCourseDatabase(masterConn, teacherId, newTask,
              courseConn);
      // The previous step duplicated newTask into the course database.
      // Take the course task id from it and finalize CourseImplementation ci.
      ci.setCourseTaskId(newTask.getId());
      ci.insert(masterConn);
      logger.debug("Course created by " + getNavigator().getUser()
              .getFirstName() + " " + getNavigator().getUser().getLastName());
      // Give feedback to user
      addActionMessage(getText("addCourse.messages.confirm"));
      return SUCCESS;
    }

  }

  private static abstract class FormBean extends WetoAdminAction
  {
    private String courseName;
    private String subjectIdAndName;
    private String databaseIdAndName;
    private String startDate;
    private String endDate;
    private String mainTeacherIdAndName;

    public String getCourseName()
    {
      return courseName;
    }

    public void setCourseName(String courseName)
    {
      this.courseName = courseName;
    }

    public String getSubjectIdAndName()
    {
      return subjectIdAndName;
    }

    public void setSubjectIdAndName(String subjectIdAndName)
    {
      this.subjectIdAndName = subjectIdAndName;
    }

    public String getDatabaseIdAndName()
    {
      return databaseIdAndName;
    }

    public void setDatabaseIdAndName(String databaseIdAndName)
    {
      this.databaseIdAndName = databaseIdAndName;
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

    public String getMainTeacherIdAndName()
    {
      return mainTeacherIdAndName;
    }

    public void setMainTeacherIdAndName(String mainTeacherIdAndName)
    {
      this.mainTeacherIdAndName = mainTeacherIdAndName;
    }

  }
}
