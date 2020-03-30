package fi.uta.cs.weto.actions;

import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Student;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.model.Navigator;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoMasterAction;
import fi.uta.cs.weto.util.DbConnectionManager;
import fi.uta.cs.weto.util.PasswordHash;
import fi.uta.cs.weto.util.ShibbolethAttributes;
import fi.uta.cs.weto.util.ShibbolethLogin;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class Login extends WetoMasterAction
{
  private static final Logger logger = Logger.getLogger(Login.class);

  private static UserAccount syncDatabaseUser(UserAccount user, String loginName,
          String firstName, String lastName, String email, String studentNumber,
          Connection masterConn, HashMap<Integer, String> databases)
          throws InvalidValueException, ObjectNotValidException, SQLException,
                 NoSuchItemException
  {
    /* When name anynomyzation was used, no user information was changed.
    if(true)
    {
      return user;
    }
     */

    if(user == null)
    {
      user = new UserAccount();
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setEmail(email);
      user.setLoginName(loginName);
      user.setPassword("");
      user.insert(masterConn);
      if((studentNumber != null) && !studentNumber.isEmpty())
      {
        Student student = new Student();
        student.setUserId(user.getId());
        student.setStudentNumber(studentNumber);
        student.insert(masterConn);
      }
    }
    else
    {
      boolean updateUserInfo = false;
      // Compare Shibboleth information with Weto user information and update
      // if something is different.
      if(stringUpdated(firstName, user.getFirstName()))
      {
        user.setFirstName(firstName);
        updateUserInfo = true;
      }
      if(stringUpdated(lastName, user.getLastName()))
      {
        user.setLastName(lastName);
        updateUserInfo = true;
      }
      if(stringUpdated(email, user.getEmail()))
      {
        user.setEmail(email);
        updateUserInfo = true;
      }
      if(updateUserInfo)
      {
        user.update(masterConn);
      }
      boolean updateStudentNumber = false;
      try
      { // Also check student number
        Student student = Student.select1ByUserId(masterConn, user.getId());
        updateStudentNumber = stringUpdated(studentNumber, student
                .getStudentNumber());
        if(updateStudentNumber)
        {
          student.setStudentNumber(studentNumber);
          student.update(masterConn);
        }
      }
      catch(NoSuchItemException e)
      { // The current user is not a student
      }
      // If something was updated, reflect changes also to course databases.
      if(updateUserInfo || updateStudentNumber)
      {
        DbConnectionManager manager = DbConnectionManager.getInstance();
        for(Map.Entry<Integer, String> dbEntry : databases.entrySet())
        {
          if(!Objects.equals(dbEntry.getKey(), DatabasePool.MASTER_ID))
          {
            Connection courseConn = manager.getConnection(dbEntry.getValue());
            if(courseConn != null)
            {
              try
              {
                UserAccount courseUser = UserAccount
                        .select1ByLoginName(courseConn, user.getLoginName());
                if(updateUserInfo)
                {
                  courseUser.setFirstName(user.getFirstName());
                  courseUser.setLastName(user.getLastName());
                  courseUser.setEmail(user.getEmail());
                  courseUser.update(courseConn);
                }
                if(updateStudentNumber)
                {
                  try
                  {
                    Student student = Student.select1ByUserId(courseConn,
                            courseUser.getId());
                    student.setStudentNumber(studentNumber);
                    student.update(courseConn);
                  }
                  catch(NoSuchItemException e)
                  { // Not (usually) an error: the current user is not a student
                  }
                }
              }
              catch(NoSuchItemException e)
              {
              }
              finally
              {
                try
                {
                  courseConn.commit();
                }
                catch(SQLException e1)
                {
                  try
                  {
                    courseConn.rollback();
                  }
                  catch(SQLException e2)
                  {
                  }
                }
                finally
                {
                  manager.freeConnection(courseConn);
                }
              }
            }
          }
        }
      }
    }
    return user;
  }

  public static UserAccount retrieveShibbolethAttributes(
          HttpServletRequest request, Connection masterConn,
          HashMap<Integer, String> databases)
          throws WetoActionException, InvalidValueException, SQLException,
                 ObjectNotValidException, FailedLoginException,
                 NoSuchItemException
  {
    ShibbolethAttributes sa = new ShibbolethAttributes(request, logger);
    // Verify that Shibboleth attributes could be obtained
    if(!sa.isOK())
    {
      throw new FailedLoginException();
    }
    // Retrieve user information from Shibboleth.
    String loginName = sa.getLoginName();
    String email = sa.getEmail();
    String firstName = sa.getFirstName();
    String lastName = sa.getLastName();
    String studentNumber = sa.getStudentNumber();
    UserAccount user = null;
    try
    {
      user = UserAccount.select1ByLoginName(masterConn, loginName);
    }
    catch(NoSuchItemException e)
    {
    }
    return syncDatabaseUser(user, loginName, firstName, lastName, email,
            studentNumber, masterConn, databases);
  }

  private String username;
  private String password;
  private Integer taskId;
  private Integer tabId;
  private Integer dbId;
  private String orig_action_name;
  private Map<String, String[]> orig_action_pars;

  public Login()
  {
    super(true);
  }

  private static boolean stringUpdated(String a, String b)
  {
    return (a != null) && !a.isEmpty() && !a.equals(b);
  }

  private UserAccount shibbolethLogin(UserAccount user, Connection masterConn,
          HashMap<Integer, String> databases)
          throws Exception
  {
    ShibbolethLogin sl = new ShibbolethLogin(username, password, logger);
    String slUser = sl.getLoginName();
    // Verify that Shibboleth returned the same username
    if(slUser == null || !(slUser.startsWith(username + "@") || slUser.equals(
            username)))
    {
      throw new WetoActionException(getText("login.error.credentials"),
              ACCESS_DENIED);
    }
    // Verify that Shibboleth attributes could be obtained
    if(!sl.isOK())
    {
      throw new FailedLoginException();
    }
    // Retrieve user information from Shibboleth.
    String firstName = sl.getFirstName();
    String lastName = sl.getLastName();
    String email = sl.getEmail();
    String studentNumber = sl.getStudentNumber();
    if(!slUser.equals(username))
    {
      try
      {
        user = UserAccount.select1ByLoginName(masterConn, slUser);
      }
      catch(NoSuchItemException e)
      {
      }
    }
    return syncDatabaseUser(user, slUser, firstName, lastName, email,
            studentNumber, masterConn, databases);
  }

  @Override
  public String action() throws Exception
  {
    Connection masterConn = getMasterConnection();
    HashMap<Integer, String> databases = DatabasePool.selectNamesWithMaster(
            masterConn);
    UserAccount user = null;
    if(!checkUsernameAndPassword())
    {
      throw new WetoActionException("", INPUT);
    }
    try
    {
      user = UserAccount.select1ByLoginName(masterConn, username);
    }
    catch(NoSuchItemException e)
    {
    }
    if(user == null || user.getPassword().isEmpty())
    {
      user = shibbolethLogin(user, masterConn, databases);
    }
    else if(!PasswordHash.validatePassword(password, user.getPassword()))
    {
      throw new WetoActionException(getText("login.error.credentials"),
              ACCESS_DENIED);
    }
    // Create a navigator instance with user id,
    createMasterNavigator(masterConn, user.getId(), databases,
            DatabasePool.MASTER_ID);
    orig_action_name = (String) getRequest().getSession().getAttribute(
            "orig_action_name");
    getRequest().getSession().removeAttribute("orig_action_name");
    if((orig_action_name != null) && !orig_action_name.isEmpty())
    {
      orig_action_pars = ((HashMap<String, String[]>) getRequest().getSession()
              .getAttribute("orig_action_pars"));
      getRequest().getSession().removeAttribute("orig_action_pars");
      return "forward";
    }
    else if((taskId != null) && (tabId != null) && (dbId != null))
    {
      return "VIEWTASK";
    }
    else
    {
      return SUCCESS;
    }
  }

  private boolean checkUsernameAndPassword()
  {
    boolean result = true;
    if(username == null)
    {
      result = false;
      addFieldError("username", getText("general.error.required", new String[]
      {
        getText("general.header.username")
      }));
    }
    else if(username.length() < 2)
    {
      result = false;
      addFieldError("username", getText("general.error.minLength", new String[]
      {
        getText("general.header.username"), "2"
      }));
    }
    if(password == null)
    {
      result = false;
      addFieldError("password", getText("general.error.required", new String[]
      {
        getText("general.header.password")
      }));
    }
    else if(password.length() < 2)
    {
      result = false;
      addFieldError("password", getText("general.error.minLength", new String[]
      {
        getText("general.header.password"), "2"
      }));
    }
    return result;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username.trim().toLowerCase();
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getOrig_action_name()
  {
    return orig_action_name;
  }

  public Map<String, String[]> getOrig_action_pars()
  {
    return orig_action_pars;
  }

  public void setTaskId(Integer taskId)
  {
    this.taskId = taskId;
  }

  public void setTabId(Integer tabId)
  {
    this.tabId = tabId;
  }

  public void setDbId(Integer dbId)
  {
    this.dbId = dbId;
  }

  public Integer getTaskId()
  {
    return taskId;
  }

  public Integer getTabId()
  {
    return tabId;
  }

  public Integer getDbId()
  {
    return dbId;
  }

  public static final void checkLogin(ActionSupport as,
          HttpServletRequest request, Navigator navigator, Integer masterUserId)
          throws WetoActionException
  {
    if(navigator == null || masterUserId == null)
    {
      /* Store action name and parameters to allow to later redirect to it.
      request.getSession().setAttribute("orig_action_name", ActionContext
              .getContext().getName());
      HashMap<String, String[]> parCopyMap = new HashMap<>();
      for(Map.Entry<String, String[]> entry
                  : (Set<Map.Entry<String, String[]>>) request.getParameterMap()
              .entrySet())
      {
        String[] parArr = entry.getValue();
        String[] parCopyArr = new String[parArr.length];
        for(int i = 0; i < parArr.length; ++i)
        {
          parCopyArr[i] = WetoUtilities.escapeHtml(parArr[i]);
        }
        parCopyMap.put(entry.getKey(), parCopyArr);
      }
      request.getSession().setAttribute("orig_action_pars", parCopyMap); */
      // The user has not logged in yet.
      throw new WetoActionException(as.getText("general.header.loginRequired"),
              "login");
    }
  }

}
