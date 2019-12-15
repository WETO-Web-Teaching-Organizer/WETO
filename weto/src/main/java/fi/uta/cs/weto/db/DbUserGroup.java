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
 * Generated database access class for table UserGroup.
 *
 */
public class DbUserGroup extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlLongvarchar nameData;
  private SqlInteger typeData;
  private SqlInteger taskIdData;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbUserGroup()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    nameData = new SqlLongvarchar();
    typeData = new SqlInteger();
    taskIdData = new SqlInteger();
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
    nameData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex + 2));
    typeData
            .jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 3));
    taskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 4));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 5));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("UserGroup\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("name:" + nameData.toString() + "\n");
    sb.append("type:" + typeData.toString() + "\n");
    sb.append("taskId:" + taskIdData.toString() + "\n");
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
    if(!(obj instanceof DbUserGroup))
    {
      return false;
    }
    DbUserGroup dbObj = (DbUserGroup) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!nameData.equals(dbObj.nameData))
    {
      return false;
    }
    if(!typeData.equals(dbObj.typeData))
    {
      return false;
    }
    if(!taskIdData.equals(dbObj.taskIdData))
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
   * Gets the raw data object for name attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getNameData()
  {
    return nameData;
  }

  /**
   * Gets the raw data object for type attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTypeData()
  {
    return typeData;
  }

  /**
   * Gets the raw data object for taskId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTaskIdData()
  {
    return taskIdData;
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
   * Inserts this object to the database table UserGroup.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!nameData.isValid())
    {
      throw new ObjectNotValidException("name");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "insert into UserGroup (name, type, taskId, timeStamp) values (?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, nameData.jdbcGetValue());
      ps.setObject(2, typeData.jdbcGetValue());
      ps.setObject(3, taskIdData.jdbcGetValue());
      ps.setObject(4, timeStampData.jdbcGetValue());
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
   * Updates the row of this object in the database table UserGroup. The row is
   * identified by the primary key attribute(s).
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
    if(!nameData.isValid())
    {
      throw new ObjectNotValidException("name");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "update UserGroup set id = ?, name = ?, type = ?, taskId = ?, timeStamp = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, nameData.jdbcGetValue());
    ps.setObject(3, typeData.jdbcGetValue());
    ps.setObject(4, taskIdData.jdbcGetValue());
    ps.setObject(5, timeStampData.jdbcGetValue());
    ps.setObject(6, idKC.jdbcGetValue());
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
   * Deletes the row of this object from the database table UserGroup. The row
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
    String prepareString = "delete from UserGroup where id = ?";
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
   * Selects the row of this object from the database table UserGroup and
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
    String prepareString = "select * from UserGroup where id = ?";
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
   * UserGroup.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type UserGroup.
   * The iterator is closed when hasNext() returns false or the iterator is
   * finalized.
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
      prepareString = "select * from UserGroup";
    }
    else
    {
      prepareString = "select * from UserGroup where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, UserGroup.class);
  }

}

// End of file.
