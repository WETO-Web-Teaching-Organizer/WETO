package fi.uta.cs.weto.actions.students;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;

public class ImportStudents extends WetoTeacherAction
{
  private File studentsFile = null;
  private String studentsText = null;

  public ImportStudents()
  {
    super(Tab.STUDENTS.getBit(), Tab.STUDENTS.getBit(), Tab.STUDENTS.getBit(),
            0);
  }

  @Override
  public String action() throws Exception
  {
    Connection courseConn = getCourseConnection();
    Connection masterConn = getMasterConnection();
    Integer courseTaskId = getCourseTaskId();
    Integer masterTaskId = CourseImplementation
            .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(),
                    courseTaskId).getMasterTaskId();
    Reader reader = null;
    if((studentsText != null) && !studentsText.isEmpty())
    {
      reader = new StringReader(studentsText);
    }
    else if(studentsFile != null)
    {
      reader = new InputStreamReader(new FileInputStream(studentsFile), "UTF-8");
    }
    if(reader != null)
    {
      ArrayList<String[]> permittedList = new ArrayList<>();
      try(BufferedReader br = new BufferedReader(reader))
      {
        String line;
        while((line = br.readLine()) != null)
        {
          UserAccount addUser = new UserAccount();
          UserGroup addGroup = new UserGroup();
          addGroup.setTaskId(courseTaskId);
          // loginName, firstName, lastName, email, studentnumber, groupName
          String[] studentInfo = line.split(";", 0);
          String loginName = studentInfo[0].trim().toLowerCase();
          addUser.setLoginName(loginName);
          addUser.setPassword("");
          addUser.setFirstName("");
          addUser.setLastName("");
          addUser.setEmail("");
          addGroup.setName("");
          String studentNumber = "";
          if(studentInfo.length == 2)
          {
            addGroup.setName(studentInfo[1].trim());
          }
          else if(studentInfo.length > 2)
          {
            addUser.setFirstName(studentInfo[1].trim());
            addUser.setLastName(studentInfo[2].trim());
            if(studentInfo.length > 3)
            {
              addUser.setEmail(studentInfo[3].trim());
            }
            if(studentInfo.length > 4)
            {
              studentNumber = studentInfo[4].trim();
            }
            if(studentInfo.length > 5)
            {
              addGroup.setName(studentInfo[5].trim());
            }
          }
          try
          {
            CourseMemberModel.addStudent(masterConn, courseConn, addUser,
                    studentNumber, addGroup, true, false);
          }
          catch(NoSuchItemException e)
          {
            permittedList.add(new String[]
            {
              loginName, addUser.getEmail(), studentNumber, addGroup.getName()
            });
          }
        }
      }
      addActionMessage(getText("students.message.uploadSuccess"));
      if(!permittedList.isEmpty())
      {
        CourseMemberModel.updatePermittedStudents(masterConn, masterTaskId,
                permittedList, new ArrayList<String[]>());
        addActionError(getText("students.message.addedToPermitted"));
      }
      return SUCCESS;
    }
    return INPUT;
  }

  public void setStudentsFile(File studentsFile)
  {
    this.studentsFile = studentsFile;
  }

  public void setStudentsText(String studentsText)
  {
    this.studentsText = studentsText;
  }

}
