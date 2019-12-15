package fi.uta.cs.weto.actions.students;

import static com.opensymphony.xwork2.Action.SUCCESS;
import fi.uta.cs.weto.db.ClusterMember;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.RightsCluster;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.util.ArrayList;

public class RemoveStudent extends WetoCourseAction
{
  private Integer studentId;
  private StudentView student;
  private boolean submitted;

  public RemoveStudent()
  {
    super(Tab.MAIN.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Integer courseTaskId = getCourseTaskId();
    Integer userId = getCourseUserId();
    final boolean isTeacher = getNavigator().isTeacher();
    WetoTimeStamp[] withdrawLimits = PermissionModel.getTimeStampLimits(conn,
            userId, courseTaskId, PermissionType.WITHDRAW, isTeacher);
    int withdrawValue = PermissionModel.checkTimeStampLimits(withdrawLimits);
    // Verify that the current user is allowed to withdraw the specified student
    if((withdrawValue != PermissionModel.CURRENT) || !(isTeacher || userId
            .equals(studentId)))
    {
      throw new WetoActionException(getText("general.error.timePeriodNotActive"));
    }
    student = StudentView.select1ByUserId(conn, studentId);
    if(submitted)
    {
      Connection masterConn = getMasterConnection();
      UserIdReplication uidr = UserIdReplication.select1ByCourseDbUserId(conn,
              studentId);
      Integer masterUserId = uidr.getMasterDbUserId();
      CourseImplementation ci = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                      courseTaskId);
      Integer masterTaskId = ci.getMasterTaskId();
      RightsCluster courseCluster = RightsCluster.select1ByTaskIdAndType(conn,
              courseTaskId, ClusterType.STUDENTS.getValue());
      RightsCluster masterCluster = RightsCluster.select1ByTaskIdAndType(
              masterConn, masterTaskId, ClusterType.STUDENTS.getValue());
      ClusterMember courseCm = ClusterMember.select1ByClusterIdAndUserId(conn,
              courseCluster.getId(), studentId);
      courseCm.delete(conn);
      ClusterMember masterCm = ClusterMember.select1ByClusterIdAndUserId(
              masterConn, masterCluster.getId(), masterUserId);
      masterCm.delete(masterConn);
      ArrayList<GroupMember> groups = GroupMember
              .selectByUserId(conn, studentId);
      for(GroupMember gm : groups)
      {
        if(Task.select1ById(conn, gm.getTaskId()).getRootTaskId().equals(
                courseTaskId))
        {
          gm.delete(conn);
        }
      }
      ArrayList<String> name = new ArrayList<>();
      name.add(student.getLastName());
      name.add(student.getFirstName());
      addActionMessage(getText("removeStudent.message.removeSuccess", name));
      // Add the submission view event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(courseTaskId, courseTaskId, userId, LogEvent.REMOVE_STUDENT
                .getValue(), studentId, null, getRequest().getRemoteAddr())
                .insert(conn);
      }
      return isTeacher ? SUCCESS : "courselist";
    }
    else
    {
      return INPUT;
    }
  }

  public void setStudentId(Integer studentId)
  {
    this.studentId = studentId;
  }

  public StudentView getStudent()
  {
    return student;
  }

  public void setSubmitted(boolean submitted)
  {
    this.submitted = submitted;
  }

}
