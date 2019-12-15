package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.sqldatatypes.SqlInteger;
import fi.uta.cs.sqldatatypes.SqlLongvarchar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Generated database access class for table UserAccount.
 *
 */
public class DbUserAccount extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlLongvarchar loginNameData;
  private SqlLongvarchar passwordData;
  private SqlLongvarchar firstNameData;
  private SqlLongvarchar lastNameData;
  private SqlLongvarchar emailData;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbUserAccount()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    loginNameData = new SqlLongvarchar();
    passwordData = new SqlLongvarchar();
    firstNameData = new SqlLongvarchar();
    lastNameData = new SqlLongvarchar();
    emailData = new SqlLongvarchar();
    timeStampData = new SqlInteger();
    idData.setPrime(true);
  }

  /**
   * Updates the data from the given ResultSet object.
   *
   * @param resultSet ResultSet object containing the data.
   * @param baseIndex Base index of the columns in the ResultSet (exclusive).
   * @throws SQLException if the JDBC operation fails.
   * @throws InvalidValueException if the attributes are invalid.
   */
  public void setFromResultSet(ResultSet resultSet, int baseIndex) throws
          SQLException, InvalidValueException
  {
    idData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 1));
    idKC.jdbcSetValue(idData.jdbcGetValue());
    loginNameData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 2));
    passwordData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 3));
    firstNameData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 4));
    lastNameData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 5));
    emailData
            .jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex + 6));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 7));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("UserAccount\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("loginName:" + loginNameData.toString() + "\n");
    sb.append("password:" + passwordData.toString() + "\n");
    sb.append("firstName:" + firstNameData.toString() + "\n");
    sb.append("lastName:" + lastNameData.toString() + "\n");
    sb.append("email:" + emailData.toString() + "\n");
    sb.append("timeStamp:" + timeStampData.toString() + "\n");
    sb.append("\n");
    return (sb.toString());
  }

  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof DbUserAccount))
    {
      return false;
    }
    DbUserAccount dbObj = (DbUserAccount) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!loginNameData.equals(dbObj.loginNameData))
    {
      return false;
    }
    if(!passwordData.equals(dbObj.passwordData))
    {
      return false;
    }
    if(!firstNameData.equals(dbObj.firstNameData))
    {
      return false;
    }
    if(!lastNameData.equals(dbObj.lastNameData))
    {
      return false;
    }
    if(!emailData.equals(dbObj.emailData))
    {
      return false;
    }
    if(!timeStampData.equals(dbObj.timeStampData))
    {
      return false;
    }
    return true;
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Gets the raw data object for id attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getIdData()
  {
    return idData;
  }

  /**
   * Gets the raw data object for loginName attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getLoginNameData()
  {
    return loginNameData;
  }

  /**
   * Gets the raw data object for password attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getPasswordData()
  {
    return passwordData;
  }

  /**
   * Gets the raw data object for firstName attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getFirstNameData()
  {
    return firstNameData;
  }

  /**
   * Gets the raw data object for lastName attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getLastNameData()
  {
    return lastNameData;
  }

  /**
   * Gets the raw data object for email attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getEmailData()
  {
    return emailData;
  }

  /**
   * Gets the raw data object for timeStamp attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTimeStampData()
  {
    return timeStampData;
  }

  /**
   * Inserts this object to the database table UserAccount.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!loginNameData.isValid())
    {
      throw new ObjectNotValidException("loginName");
    }
    if(!passwordData.isValid())
    {
      throw new ObjectNotValidException("password");
    }
    if(!firstNameData.isValid())
    {
      throw new ObjectNotValidException("firstName");
    }
    if(!lastNameData.isValid())
    {
      throw new ObjectNotValidException("lastName");
    }
    if(!emailData.isValid())
    {
      throw new ObjectNotValidException("email");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "insert into UserAccount (loginName, password, firstName, lastName, email, timeStamp) values (?, ?, ?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, loginNameData.jdbcGetValue());
      ps.setObject(2, passwordData.jdbcGetValue());
      ps.setObject(3, firstNameData.jdbcGetValue());
      ps.setObject(4, lastNameData.jdbcGetValue());
      ps.setObject(5, emailData.jdbcGetValue());
      ps.setObject(6, timeStampData.jdbcGetValue());
      ResultSet rs = ps.executeQuery();
      if(!rs.next())
      {
        throw new SQLException("Insert did not return 1 row");
      }
      else
      {
        try
        {
          Integer id = rs.getInt(1);
          idData.jdbcSetValue(id);
          idKC.jdbcSetValue(id);
        }
        catch(Exception e)
        {
          throw new SQLException("Insert could not update id");
        }
      }
    }
  }

  /**
   * Updates the row of this object in the database table UserAccount. The row
   * is identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be updated does not exist or is
   * not unique.
   */
  public void update(Connection con) throws SQLException,
                                            ObjectNotValidException,
                                            NoSuchItemException
  {
    if(!idData.isValid())
    {
      throw new ObjectNotValidException("id");
    }
    if(!loginNameData.isValid())
    {
      throw new ObjectNotValidException("loginName");
    }
    if(!passwordData.isValid())
    {
      throw new ObjectNotValidException("password");
    }
    if(!firstNameData.isValid())
    {
      throw new ObjectNotValidException("firstName");
    }
    if(!lastNameData.isValid())
    {
      throw new ObjectNotValidException("lastName");
    }
    if(!emailData.isValid())
    {
      throw new ObjectNotValidException("email");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "update UserAccount set id = ?, loginName = ?, password = ?, firstName = ?, lastName = ?, email = ?, timeStamp = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, loginNameData.jdbcGetValue());
    ps.setObject(3, passwordData.jdbcGetValue());
    ps.setObject(4, firstNameData.jdbcGetValue());
    ps.setObject(5, lastNameData.jdbcGetValue());
    ps.setObject(6, emailData.jdbcGetValue());
    ps.setObject(7, timeStampData.jdbcGetValue());
    ps.setObject(8, idKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    try
    {
      idKC.jdbcSetValue(idData.jdbcGetValue());
    }
    catch(InvalidValueException e)
    {
      // Ignored (isValid already called)
    }
    if(rows != 1)
    {
      throw new NoSuchItemException();
    }
  }

  /**
   * Deletes the row of this object from the database table UserAccount. The row
   * is identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be deleted does not exist.
   */
  public void delete(Connection con) throws SQLException, TooManyItemsException,
                                            NoSuchItemException
  {
    String prepareString = "delete from UserAccount where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    if(rows > 1)
    {
      throw new TooManyItemsException();
    }
    if(rows < 1)
    {
      throw new NoSuchItemException();
    }
  }

  /**
   * Selects the row of this object from the database table UserAccount and
   * updates the attributes accordingly. The row is identified by the primary
   * key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws InvalidValueException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   */
  public void select(Connection con) throws SQLException, InvalidValueException,
                                            NoSuchItemException
  {
    String prepareString = "select * from UserAccount where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ResultSet rs = ps.executeQuery();
    if(rs.next())
    {
      setFromResultSet(rs, 0);
      rs.close();
      ps.close();
    }
    else
    {
      rs.close();
      ps.close();
      throw new NoSuchItemException();
    }
  }

  /**
   * Constructs and returns a selection iterator for rows in database table
   * UserAccount.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type
   * UserAccount. The iterator is closed when hasNext() returns false or the
   * iterator is finalized.
   *
   * @throws SQLException if the JDBC operation fails.
   *
   * Note that the iterator may throw SqlSelectionIteratorException (which is a
   * runtime exception) when its methods are called.
   */
  public static Iterator selectionIterator(Connection con, String whereClause)
          throws SQLException
  {
    String prepareString;
    if(whereClause == null)
    {
      whereClause = "";
    }
    if(whereClause.equals(""))
    {
      prepareString = "select * from UserAccount";
    }
    else
    {
      prepareString = "select * from UserAccount where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, UserAccount.class);
  }

}

// End of file.
