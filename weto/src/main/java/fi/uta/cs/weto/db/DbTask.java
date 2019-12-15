package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.sqldatatypes.SqlBoolean;
import fi.uta.cs.sqldatatypes.SqlInteger;
import fi.uta.cs.sqldatatypes.SqlLongvarchar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Generated database access class for table Task.
 *
 */
public class DbTask extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlLongvarchar textData;
  private SqlBoolean showTextInParentData;
  private SqlInteger statusData;
  private SqlLongvarchar nameData;
  private SqlInteger rootTaskIdData;
  private SqlInteger componentBitsData;
  private SqlInteger timeStampData;
  private SqlLongvarchar oldTextData;

  /**
   * Default constructor.
   */
  public DbTask()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    textData = new SqlLongvarchar();
    showTextInParentData = new SqlBoolean();
    statusData = new SqlInteger();
    nameData = new SqlLongvarchar();
    rootTaskIdData = new SqlInteger();
    componentBitsData = new SqlInteger();
    timeStampData = new SqlInteger();
    oldTextData = new SqlLongvarchar();
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
    textData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex + 2));
    showTextInParentData.jdbcSetValue((java.lang.Boolean) resultSet.getObject(
            baseIndex + 3));
    statusData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 4));
    nameData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex + 5));
    rootTaskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 6));
    componentBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 7));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 8));
    oldTextData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 9));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Task\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("text:" + textData.toString() + "\n");
    sb.append("showTextInParent:" + showTextInParentData.toString() + "\n");
    sb.append("status:" + statusData.toString() + "\n");
    sb.append("name:" + nameData.toString() + "\n");
    sb.append("rootTaskId:" + rootTaskIdData.toString() + "\n");
    sb.append("componentBits:" + componentBitsData.toString() + "\n");
    sb.append("timeStamp:" + timeStampData.toString() + "\n");
    sb.append("oldText:" + oldTextData.toString() + "\n");
    sb.append("\n");
    return (sb.toString());
  }

  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof DbTask))
    {
      return false;
    }
    DbTask dbObj = (DbTask) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!textData.equals(dbObj.textData))
    {
      return false;
    }
    if(!showTextInParentData.equals(dbObj.showTextInParentData))
    {
      return false;
    }
    if(!statusData.equals(dbObj.statusData))
    {
      return false;
    }
    if(!nameData.equals(dbObj.nameData))
    {
      return false;
    }
    if(!rootTaskIdData.equals(dbObj.rootTaskIdData))
    {
      return false;
    }
    if(!componentBitsData.equals(dbObj.componentBitsData))
    {
      return false;
    }
    if(!timeStampData.equals(dbObj.timeStampData))
    {
      return false;
    }
    if(!oldTextData.equals(dbObj.oldTextData))
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
   * Gets the raw data object for text attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getTextData()
  {
    return textData;
  }

  /**
   * Gets the raw data object for showTextInParent attribute.
   *
   * @return Data object as SqlBoolean.
   */
  public SqlBoolean getShowTextInParentData()
  {
    return showTextInParentData;
  }

  /**
   * Gets the raw data object for status attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getStatusData()
  {
    return statusData;
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
   * Gets the raw data object for rootTaskId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getRootTaskIdData()
  {
    return rootTaskIdData;
  }

  /**
   * Gets the raw data object for componentBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getComponentBitsData()
  {
    return componentBitsData;
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
   * Gets the raw data object for oldText attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getOldTextData()
  {
    return oldTextData;
  }

  /**
   * Inserts this object to the database table Task.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
          ObjectNotValidException
  {
    if(!textData.isValid())
    {
      throw new ObjectNotValidException("text");
    }
    if(!showTextInParentData.isValid())
    {
      throw new ObjectNotValidException("showTextInParent");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!nameData.isValid())
    {
      throw new ObjectNotValidException("name");
    }
    if(!rootTaskIdData.isValid())
    {
      throw new ObjectNotValidException("rootTaskId");
    }
    if(!componentBitsData.isValid())
    {
      throw new ObjectNotValidException("componentBits");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!oldTextData.isValid())
    {
      throw new ObjectNotValidException("oldText");
    }
    String prepareString
            = "insert into Task (text, showTextInParent, status, name, rootTaskId, componentBits, timeStamp, oldText) values (?, ?, ?, ?, ?, ?, ?, ?) returning id;";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, textData.jdbcGetValue());
    ps.setObject(2, showTextInParentData.jdbcGetValue());
    ps.setObject(3, statusData.jdbcGetValue());
    ps.setObject(4, nameData.jdbcGetValue());
    ps.setObject(5, rootTaskIdData.jdbcGetValue());
    ps.setObject(6, componentBitsData.jdbcGetValue());
    ps.setObject(7, timeStampData.jdbcGetValue());
    ps.setObject(8, oldTextData.jdbcGetValue());
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

  /**
   * Updates the row of this object in the database table Task. The row is
   * identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be updated does not exist or is
   * not unique.
   */
  public void update(Connection con) throws SQLException,
          ObjectNotValidException, NoSuchItemException
  {
    if(!idData.isValid())
    {
      throw new ObjectNotValidException("id");
    }
    if(!textData.isValid())
    {
      throw new ObjectNotValidException("text");
    }
    if(!showTextInParentData.isValid())
    {
      throw new ObjectNotValidException("showTextInParent");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!nameData.isValid())
    {
      throw new ObjectNotValidException("name");
    }
    if(!rootTaskIdData.isValid())
    {
      throw new ObjectNotValidException("rootTaskId");
    }
    if(!componentBitsData.isValid())
    {
      throw new ObjectNotValidException("componentBits");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!oldTextData.isValid())
    {
      throw new ObjectNotValidException("oldText");
    }
    String prepareString
            = "update Task set id = ?, text = ?, showTextInParent = ?, status = ?, name = ?, rootTaskId = ?, componentBits = ?, timeStamp = ?, oldText = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, textData.jdbcGetValue());
    ps.setObject(3, showTextInParentData.jdbcGetValue());
    ps.setObject(4, statusData.jdbcGetValue());
    ps.setObject(5, nameData.jdbcGetValue());
    ps.setObject(6, rootTaskIdData.jdbcGetValue());
    ps.setObject(7, componentBitsData.jdbcGetValue());
    ps.setObject(8, timeStampData.jdbcGetValue());
    ps.setObject(9, oldTextData.jdbcGetValue());
    ps.setObject(10, idKC.jdbcGetValue());
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
   * Deletes the row of this object from the database table Task. The row is
   * identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be deleted does not exist.
   */
  public void delete(Connection con) throws SQLException, TooManyItemsException,
          NoSuchItemException
  {
    String prepareString = "delete from Task where id = ?";
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
   * Selects the row of this object from the database table Task and updates the
   * attributes accordingly. The row is identified by the primary key
   * attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws InvalidValueException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   */
  public void select(Connection con) throws SQLException, InvalidValueException,
          NoSuchItemException, ObjectNotValidException
  {
    String prepareString = "select * from Task where id = ?";
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
   * Task.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Task. The
   * iterator is closed when hasNext() returns false or the iterator is
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
      prepareString = "select * from Task";
    }
    else
    {
      prepareString = "select * from Task where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Task.class);
  }

}

// End of file.
