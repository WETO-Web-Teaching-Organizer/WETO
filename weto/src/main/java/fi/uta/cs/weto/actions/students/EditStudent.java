package fi.uta.cs.weto.actions.students;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.weto.db.Student;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;

public class EditStudent extends WetoTeacherAction
{
  private Integer studentId;
  private StudentView student;
  private boolean submitted;

  public EditStudent()
  {
    super(Tab.STUDENTS.getBit(), Tab.STUDENTS.getBit(), 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    // This step verifies that the user is a student in the current course.
    UserTaskView.select1ByTaskIdAndUserIdAndClusterType(conn, getTaskId(),
            studentId, ClusterType.STUDENTS.getValue());
    if(submitted)
    {
      Connection masterConn = getMasterConnection();
      UserIdReplication uidr = UserIdReplication.select1ByCourseDbUserId(conn,
              studentId);
      Integer masterUserId = uidr.getMasterDbUserId();
      UserAccount tmpUser = UserAccount.select1ById(conn, studentId);
      populateUser(tmpUser);
      tmpUser.update(conn);
      tmpUser.setId(masterUserId);
      tmpUser.select(masterConn);
      populateUser(tmpUser);
      tmpUser.update(masterConn);
      Student st = new Student();
      st.setUserId(studentId);
      st.select(conn);
      st.setStudentNumber(student.getStudentNumber());
      st.update(conn);
      st.setUserId(masterUserId);
      st.select(masterConn);
      st.setStudentNumber(student.getStudentNumber());
      st.update(masterConn);
      addActionMessage(getText("editStudent.message.updateSuccess"));
      return SUCCESS;
    }
    else
    {
      student = StudentView.select1ByUserId(conn, studentId);
      return INPUT;
    }
  }

  private void populateUser(UserAccount tmpUser) throws InvalidValueException
  {
    if(!student.getFirstName().equals(""))
    {
      tmpUser.setFirstName(student.getFirstName());
    }
    if(!student.getLastName().equals(""))
    {
      tmpUser.setLastName(student.getLastName());
    }
    if(!student.getEmail().equals("") && student.getEmail().contains("@"))
    {
      tmpUser.setEmail(student.getEmail());
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

  public void setStudent(StudentView student)
  {
    this.student = student;
  }

  public void setSubmitted(boolean submitted)
  {
    this.submitted = submitted;
  }

}
