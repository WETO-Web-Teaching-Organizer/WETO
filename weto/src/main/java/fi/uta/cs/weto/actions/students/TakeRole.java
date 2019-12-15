package fi.uta.cs.weto.actions.students;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;

public class TakeRole extends WetoTeacherAction
{
  private Integer studentId;

  public TakeRole()
  {
    super(Tab.MAIN.getBit(), 0, 0, 0);

  }

  @Override
  public String action() throws Exception
  {
    Integer masterStudentId = null;
    UserAccount studentUser = null;
    Integer clusterType = null;
    Connection conn = getCourseConnection();
    try
    {
      UserTaskView student = UserTaskView.select1ByTaskIdAndUserId(conn,
              getTaskId(), studentId);
      studentUser = UserAccount.select1ById(conn, studentId);
      UserIdReplication uir = UserIdReplication.select1ByCourseDbUserId(conn,
              studentId);
      masterStudentId = uir.getMasterDbUserId();
      clusterType = student.getClusterType();
    }
    catch(NoSuchItemException e)
    {
    }
    if(!(ClusterType.STUDENTS.getValue().equals(clusterType) && getNavigator()
            .takeStudentRole(getRequest(), masterStudentId, studentId,
                    studentUser)))
    {
      throw new WetoActionException(getText("accessDenied.teacher"));
    }
    return SUCCESS;
  }

  public void setStudentId(Integer studentId)
  {
    this.studentId = studentId;
  }

}
