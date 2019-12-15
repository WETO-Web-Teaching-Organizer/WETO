package fi.uta.cs.weto.actions.admin;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Student;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoAdminAction;
import fi.uta.cs.weto.util.PasswordHash;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public abstract class EditUsersActions
{
  public static class View extends WetoAdminAction
  {
    private ArrayList<UserAccount> users;

    @Override
    public String action() throws Exception
    {
      users = UserAccount.selectAll(getMasterConnection());
      Collections.sort(users, new Comparator<UserAccount>()
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
      return SUCCESS;
    }

    public ArrayList<UserAccount> getUsers()
    {
      return users;
    }

    public int getUsersSize()
    {
      return users.size();
    }

  }

  public static class AddForm extends UserBean
  {
    @Override
    public String action() throws Exception
    {
      return SUCCESS;
    }

    public boolean isNewUser()
    {
      return true;
    }

  }

  public static class CommitAdd extends UserBean
  {
    private boolean setPassword;

    @Override
    public String action() throws Exception
    {
      if(!checkUserBean(true))
      {
        throw new WetoActionException("", INPUT);
      }
      Connection masterConn = getMasterConnection();
      String loginName = getLoginName();
      try
      {
        UserAccount.select1ByLoginName(masterConn, loginName);
        throw new WetoActionException(getText("editUsers.message.userExists",
                new String[]
                {
                  loginName
                }),
                INPUT);
      }
      catch(NoSuchItemException e)
      {
        String pass = getPassword();
        if(setPassword && (pass != null) && !pass.isEmpty())
        {
          setPassword(PasswordHash.createHash(getPassword()));
        }
        else
        {
          setPassword("");
        }
        UserAccount addUser = new UserAccount();
        String addStudentNumber = readFromUserBean(addUser);
        addUser.insert(masterConn);
        if((addStudentNumber != null) && !addStudentNumber.isEmpty())
        {
          try
          {
            Student.select1ByStudentNumber(masterConn, addStudentNumber);
            throw new WetoActionException(
                    getText("editUsers.message.userExists",
                            new String[]
                            {
                              addStudentNumber
                            }),
                    INPUT);
          }
          catch(NoSuchItemException e2)
          {
            Student student = new Student();
            student.setStudentNumber(addStudentNumber);
            student.setUserId(addUser.getId());
            student.insert(masterConn);
          }
        }
        addActionMessage(getText("editUsers.message.userAdded", new String[]
        {
          loginName
        }));
        return SUCCESS;
      }

    }

    public boolean isSetPassword()
    {
      return setPassword;
    }

    public void setSetPassword(boolean setPassword)
    {
      this.setPassword = setPassword;
    }

  }

  public static class UpdateForm extends UserBean
  {
    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      UserAccount masterUser = UserAccount.select1ByLoginName(masterConn,
              getLoginName());
      String studentNumber = null;
      try
      {
        studentNumber = Student.select1ByUserId(masterConn, masterUser.getId())
                .getStudentNumber();
      }
      catch(NoSuchItemException e)
      {
      }
      writeToUserBean(masterUser, studentNumber);
      return SUCCESS;
    }

    public boolean isNewUser()
    {
      return false;
    }

  }

  public static class CommitUpdate extends UserBean
  {
    private boolean setPassword;

    @Override
    public String action() throws Exception
    {
      if(!checkUserBean(false))
      {
        throw new WetoActionException("", INPUT);
      }
      Connection masterConn = getMasterConnection();
      UserAccount oldUser = UserAccount.select1ByLoginName(masterConn,
              getLoginName());
      if(setPassword)
      {
        if(!getPassword().isEmpty())
        {
          oldUser.setPassword(PasswordHash.createHash(getPassword()));
        }
        else
        {
          oldUser.setPassword("");
        }
      }
      oldUser.setFirstName(getFirstName());
      oldUser.setLastName(getLastName());
      oldUser.setEmail(getEmail());
      oldUser.update(masterConn);
      String studentNumber = getStudentNumber();
      boolean updateStudentNumber = false;
      if((studentNumber != null) && !studentNumber.isEmpty())
      {
        try
        {
          Student student = Student.select1ByUserId(masterConn, oldUser.getId());
          if(!studentNumber.equals(student.getStudentNumber()))
          {
            student.setStudentNumber(studentNumber);
            student.update(masterConn);
            updateStudentNumber = true;
          }
        }
        catch(NoSuchItemException e)
        {
          Student student = new Student();
          student.setStudentNumber(studentNumber);
          student.setUserId(oldUser.getId());
          student.insert(masterConn);
        }
      }
      // also update in course databases
      for(Map.Entry<Integer, String> dbEntry : getNavigator().getDatabases()
              .entrySet())
      {
        if(dbEntry.getKey() != DatabasePool.MASTER_ID)
        {
          Connection courseConn = getConnection(dbEntry.getValue());
          try
          {
            UserAccount courseUser = UserAccount.select1ByLoginName(courseConn,
                    getLoginName());
            courseUser.setFirstName(getFirstName());
            courseUser.setLastName(getLastName());
            courseUser.setEmail(getEmail());
            courseUser.update(courseConn);
            if(updateStudentNumber)
            {
              Student student = Student.select1ByUserId(courseConn, courseUser
                      .getId());
              student.setStudentNumber(studentNumber);
              student.update(courseConn);
            }
          }
          catch(NoSuchItemException e)
          {
          }
        }
      }
      addActionMessage(getText("editUsers.message.updateSuccess"));
      return SUCCESS;
    }

    public boolean isSetPassword()
    {
      return setPassword;
    }

    public void setSetPassword(boolean setPassword)
    {
      this.setPassword = setPassword;
    }

  }

  private static abstract class UserBean extends WetoAdminAction
  {
    private String loginName;
    private String password;
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String email;

    public boolean checkUserBean(boolean checkLoginName)
    {
      boolean result = true;
      if(checkLoginName && ((loginName == null) || loginName.isEmpty()))
      {
        result = false;
        addFieldError("loginName", getText("general.error.required",
                new String[]
                {
                  getText("general.header.loginName")
                }));
      }
      if((firstName == null) || firstName.isEmpty())
      {
        result = false;
        addFieldError("firstName", getText("general.error.required",
                new String[]
                {
                  getText("general.header.firstName")
                }));
      }
      if((lastName == null) || lastName.isEmpty())
      {
        result = false;
        addFieldError("lastName", getText("general.error.required",
                new String[]
                {
                  getText("general.header.lastName")
                }));
      }
      if((email == null) || email.isEmpty())
      {
        result = false;
        addFieldError("email", getText("general.error.required",
                new String[]
                {
                  getText("general.header.email")
                }));
      }
      return result;
    }

    public String readFromUserBean(UserAccount user) throws
            InvalidValueException
    {
      user.setLoginName(loginName);
      user.setPassword(password);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setEmail(email);
      return studentNumber;
    }

    public void writeToUserBean(UserAccount user, String studentNumber)
    {
      loginName = user.getLoginName();
      password = user.getPassword();
      firstName = user.getFirstName();
      lastName = user.getLastName();
      email = user.getEmail();
      this.studentNumber = studentNumber;
    }

    public String getLoginName()
    {
      return loginName;
    }

    public void setLoginName(String loginName)
    {
      this.loginName = trimToLower(loginName);
    }

    public String getPassword()
    {
      return password;
    }

    public void setPassword(String password)
    {
      this.password = trim(password);
    }

    public String getStudentNumber()
    {
      return studentNumber;
    }

    public void setStudentNumber(String studentNumber)
    {
      this.studentNumber = trim(studentNumber);
    }

    public String getFirstName()
    {
      return firstName;
    }

    public void setFirstName(String firstName)
    {
      this.firstName = trim(firstName);
    }

    public String getLastName()
    {
      return lastName;
    }

    public void setLastName(String lastName)
    {
      this.lastName = trim(lastName);
    }

    public String getEmail()
    {
      return email;
    }

    public void setEmail(String email)
    {
      this.email = trim(email);
    }

    private String trimToLower(String s)
    {
      return (s != null) ? s.trim().toLowerCase() : null;
    }

    private String trim(String s)
    {
      return (s != null) ? s.trim() : null;
    }

  }

  public static class BatchCreate extends WetoAdminAction
  {
    private File usersFile = null;
    private String usersText = null;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      Reader reader = null;
      if((usersText != null) && !usersText.isEmpty())
      {
        reader = new StringReader(usersText);
      }
      else if(usersFile != null)
      {
        reader = new InputStreamReader(new FileInputStream(usersFile), "UTF-8");
      }
      if(reader != null)
      {
        try(BufferedReader br = new BufferedReader(reader))
        {
          String line;
          while((line = br.readLine()) != null)
          {
            UserAccount addUser = new UserAccount();
            // loginName, firstName, lastName, email, studentnumber
            String[] userInfo = line.split(";", 0);
            String loginName = userInfo[0].trim().toLowerCase();
            if(WetoUtilities.validateLoginName(loginName))
            {
              try
              {
                UserAccount.select1ByLoginName(masterConn, loginName);
                addActionError(getText("editUsers.message.userExists",
                        new String[]
                        {
                          loginName
                        }));
              }
              catch(NoSuchItemException e)
              {

                addUser.setLoginName(loginName);
                addUser.setPassword("");
                addUser.setFirstName("");
                addUser.setLastName("");
                addUser.setEmail("");
                String studentNumber = null;
                if(userInfo.length == 6)
                {
                  addUser.setPassword(PasswordHash
                          .createHash(userInfo[1].trim()));
                  addUser.setFirstName(userInfo[2].trim());
                  addUser.setLastName(userInfo[3].trim());
                  addUser.setEmail(userInfo[4].trim());
                  studentNumber = userInfo[5].trim();
                }
                else if(userInfo.length > 2)
                {
                  addUser.setFirstName(userInfo[1].trim());
                  addUser.setLastName(userInfo[2].trim());
                  if(userInfo.length > 3)
                  {
                    addUser.setEmail(userInfo[3].trim());
                  }
                  if(userInfo.length > 4)
                  {
                    studentNumber = userInfo[4].trim();
                  }
                }
                else if(userInfo.length == 2)
                {
                  studentNumber = userInfo[1].trim();
                }
                addUser.insert(masterConn);
                if((studentNumber != null) && !studentNumber.isEmpty())
                {
                  try
                  {
                    Student.select1ByStudentNumber(masterConn, studentNumber);
                    addActionError(getText("editUsers.message.userExists",
                            new String[]
                            {
                              studentNumber
                            }));
                  }
                  catch(NoSuchItemException e2)
                  {
                    Student student = new Student();
                    student.setStudentNumber(studentNumber);
                    student.setUserId(addUser.getId());
                    student.insert(masterConn);
                  }
                }
                addActionMessage(getText("editUsers.message.userAdded",
                        new String[]
                        {
                          loginName
                        }));
              }
            }
            else
            {
              addActionError(getText("editUsers.error.loginName", new String[]
              {
                userInfo[0]
              }));
            }
          }
        }
        addActionMessage(getText("students.message.uploadSuccess"));
        return SUCCESS;
      }
      return INPUT;
    }

    public void setUsersFile(File usersFile)
    {
      this.usersFile = usersFile;
    }

    public void setUsersText(String usersText)
    {
      this.usersText = usersText;
    }

  }

}
