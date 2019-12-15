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
 * Generated database access class for table Permission.
 *
 */
public class DbPermission extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlInteger userRefIdData;
  private SqlInteger userRefTypeData;
  private SqlInteger taskIdData;
  private SqlInteger typeData;
  private SqlInteger startDateData;
  private SqlInteger endDateData;
  private SqlLongvarchar detailData;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbPermission()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    userRefIdData = new SqlInteger();
    userRefTypeData = new SqlInteger();
    taskIdData = new SqlInteger();
    typeData = new SqlInteger();
    startDateData = new SqlInteger();
    endDateData = new SqlInteger();
    detailData = new SqlLongvarchar();
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
    userRefIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 2));
    userRefTypeData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 3));
    taskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 4));
    typeData
            .jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 5));
    startDateData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 6));
    endDateData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 7));
    detailData.jdbcSetValue((java.lang.String) resultSet
            .getObject(baseIndex + 8));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 9));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Permission\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("userRefId:" + userRefIdData.toString() + "\n");
    sb.append("userRefType:" + userRefTypeData.toString() + "\n");
    sb.append("taskId:" + taskIdData.toString() + "\n");
    sb.append("type:" + typeData.toString() + "\n");
    sb.append("startDate:" + startDateData.toString() + "\n");
    sb.append("endDate:" + endDateData.toString() + "\n");
    sb.append("detail:" + detailData.toString() + "\n");
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
    if(!(obj instanceof DbPermission))
    {
      return false;
    }
    DbPermission dbObj = (DbPermission) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!userRefIdData.equals(dbObj.userRefIdData))
    {
      return false;
    }
    if(!userRefTypeData.equals(dbObj.userRefTypeData))
    {
      return false;
    }
    if(!taskIdData.equals(dbObj.taskIdData))
    {
      return false;
    }
    if(!typeData.equals(dbObj.typeData))
    {
      return false;
    }
    if(!startDateData.equals(dbObj.startDateData))
    {
      return false;
    }
    if(!endDateData.equals(dbObj.endDateData))
    {
      return false;
    }
    if(!detailData.equals(dbObj.detailData))
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
   * Gets the raw data object for userRefId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getUserRefIdData()
  {
    return userRefIdData;
  }

  /**
   * Gets the raw data object for userRefType attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getUserRefTypeData()
  {
    return userRefTypeData;
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
   * Gets the raw data object for type attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTypeData()
  {
    return typeData;
  }

  /**
   * Gets the raw data object for startDate attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getStartDateData()
  {
    return startDateData;
  }

  /**
   * Gets the raw data object for endDate attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getEndDateData()
  {
    return endDateData;
  }

  /**
   * Gets the raw data object for detail attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getDetailData()
  {
    return detailData;
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
   * Inserts this object to the database table Permission.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
          ObjectNotValidException
  {
    if(!userRefIdData.isValid())
    {
      throw new ObjectNotValidException("userRefId");
    }
    if(!userRefTypeData.isValid())
    {
      throw new ObjectNotValidException("userRefType");
    }
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!startDateData.isValid())
    {
      throw new ObjectNotValidException("startDate");
    }
    if(!endDateData.isValid())
    {
      throw new ObjectNotValidException("endDate");
    }
    if(!detailData.isValid())
    {
      throw new ObjectNotValidException("detail");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
            = "insert into Permission (userRefId, userRefType, taskId, type, startDate, endDate, detail, timeStamp) values (?, ?, ?, ?, ?, ?, ?, ?) returning id;";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, userRefIdData.jdbcGetValue());
    ps.setObject(2, userRefTypeData.jdbcGetValue());
    ps.setObject(3, taskIdData.jdbcGetValue());
    ps.setObject(4, typeData.jdbcGetValue());
    ps.setObject(5, startDateData.jdbcGetValue());
    ps.setObject(6, endDateData.jdbcGetValue());
    ps.setObject(7, detailData.jdbcGetValue());
    ps.setObject(8, timeStampData.jdbcGetValue());
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
   * Updates the row of this object in the database table Permission. The row is
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
    if(!userRefIdData.isValid())
    {
      throw new ObjectNotValidException("userRefId");
    }
    if(!userRefTypeData.isValid())
    {
      throw new ObjectNotValidException("userRefType");
    }
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!startDateData.isValid())
    {
      throw new ObjectNotValidException("startDate");
    }
    if(!endDateData.isValid())
    {
      throw new ObjectNotValidException("endDate");
    }
    if(!detailData.isValid())
    {
      throw new ObjectNotValidException("detail");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
            = "update Permission set id = ?, userRefId = ?, userRefType = ?, taskId = ?, type = ?, startDate = ?, endDate = ?, detail = ?, timeStamp = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, userRefIdData.jdbcGetValue());
    ps.setObject(3, userRefTypeData.jdbcGetValue());
    ps.setObject(4, taskIdData.jdbcGetValue());
    ps.setObject(5, typeData.jdbcGetValue());
    ps.setObject(6, startDateData.jdbcGetValue());
    ps.setObject(7, endDateData.jdbcGetValue());
    ps.setObject(8, detailData.jdbcGetValue());
    ps.setObject(9, timeStampData.jdbcGetValue());
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
   * Deletes the row of this object from the database table Permission. The row
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
    String prepareString = "delete from Permission where id = ?";
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
   * Selects the row of this object from the database table Permission and
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
    String prepareString = "select * from Permission where id = ?";
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
   * Permission.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Permission.
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
      prepareString = "select * from Permission";
    }
    else
    {
      prepareString = "select * from Permission where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Permission.class);
  }

}

// End of file.
