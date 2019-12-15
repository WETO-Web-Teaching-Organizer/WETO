package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class UserAccount extends BeanUserAccount
{
  /**
   * Returns user by user id.
   *
   * @param conn Database connection
   * @param userId User ID
   * @return UserAccount
   * @throws SQLException @see java.sql.SQLException
   * @throws InvalidValueException
   * @throws NoSuchItemException
   */
  public static UserAccount select1ById(Connection conn, Integer userId)
          throws SQLException, InvalidValueException, NoSuchItemException
  {
    UserAccount result = new UserAccount();
    result.setId(userId);
    result.select(conn);
    return result;
  }

  /**
   * Returns user by login name.
   *
   * @param conn Database connection
   * @param loginName Login name
   * @return UserAccount
   * @throws fi.uta.cs.sqldatamodel.NoSuchItemException
   * @throws fi.uta.cs.sqldatamodel.InvalidValueException
   * @throws SQLException @see java.sql.SQLException
   */
  public static UserAccount select1ByLoginName(Connection conn, String loginName)
          throws NoSuchItemException, InvalidValueException, SQLException
  {
    UserAccount result;
    String prepareString = "SELECT id, loginname, password, firstname, "
            + "lastname, email, timestamp FROM UserAccount "
            + "WHERE loginname LIKE ?";
    PreparedStatement ps = conn.prepareStatement(prepareString);
    ps.setString(1, loginName);
    ResultSet rs = ps.executeQuery();
    if(rs.next())
    {
      result = new UserAccount();
      result.setFromResultSet(rs, 0);
      rs.close();
      ps.close();
    }
    else
    {
      rs.close();
      ps.close();
      throw new NoSuchItemException();
    }
    return result;
  }

  public static UserAccount select1ByEmail(Connection conn, String email)
          throws NoSuchItemException, InvalidValueException, SQLException
  {
    UserAccount result;
    String prepareString = "SELECT id, loginname, password, firstname, "
            + "lastname, email, timestamp FROM UserAccount "
            + "WHERE lower(email) LIKE ? AND password=''";
    PreparedStatement ps = conn.prepareStatement(prepareString);
    ps.setString(1, email.toLowerCase());
    ResultSet rs = ps.executeQuery();
    if(rs.next())
    {
      result = new UserAccount();
      result.setFromResultSet(rs, 0);
      rs.close();
      ps.close();
    }
    else
    {
      rs.close();
      ps.close();
      throw new NoSuchItemException();
    }
    return result;
  }

  public static ArrayList<UserAccount> selectTeachers(
          Connection conn)
          throws SQLException, InvalidValueException
  {
    ArrayList<UserAccount> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT id, loginname, password, firstname, "
              + "lastname, email, useraccount.timestamp FROM useraccount, teacher "
              + "WHERE useraccount.id=teacher.userid ORDER BY lastname, "
              + "firstname, loginname, id");
      while(rs.next())
      {
        UserAccount row = new UserAccount();
        row.setFromResultSet(rs, 0);
        result.add(row);
      }
    }
    return result;
  }

  public static ArrayList<UserAccount> selectAll(Connection conn)
          throws SQLException, InvalidValueException
  {
    ArrayList<UserAccount> users = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "");
    while(iter.hasNext())
    {
      users.add((UserAccount) iter.next());
    }
    return users;
  }

  @Override
  public void insert(Connection conn)
          throws ObjectNotValidException, SQLException
  {
    if(!WetoUtilities.validateLoginName(getLoginName()))
    {
      throw new ObjectNotValidException("Invalid login name: " + getLoginName());
    }
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.insert(conn);
  }

  @Override
  public void update(Connection conn) throws SQLException,
                                             ObjectNotValidException,
                                             NoSuchItemException
  {
    if(!WetoUtilities.validateLoginName(getLoginName()))
    {
      throw new ObjectNotValidException("Invalid login name: " + getLoginName());
    }
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.update(conn);
  }

  /* Overrides for using student anynomization
  @Override
  public java.lang.String getLoginName()
  {
    return WetoUtilities.getRandomLoginName(getId());
  }

  @Override
  public java.lang.String getFirstName()
  {
    return WetoUtilities.getRandomFirstName(getId());
  }

  @Override
  public java.lang.String getLastName()
  {
    return WetoUtilities.getRandomLastName(getId());
  }

  @Override
  public java.lang.String getEmail()
  {
    return WetoUtilities.getRandomEmail(getId());
  }
   */
}
