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
 * Generated database access class for table Property.
 *
 */
public class DbProperty extends SqlAssignableObject implements Cloneable
{
  private SqlInteger typeData;
  private SqlInteger typeKC;
  private SqlInteger refIdData;
  private SqlInteger refIdKC;
  private SqlInteger keyData;
  private SqlInteger keyKC;
  private SqlLongvarchar valueData;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbProperty()
  {
    super();
    typeData = new SqlInteger();
    typeKC = new SqlInteger();
    refIdData = new SqlInteger();
    refIdKC = new SqlInteger();
    keyData = new SqlInteger();
    keyKC = new SqlInteger();
    valueData = new SqlLongvarchar();
    timeStampData = new SqlInteger();
    typeData.setPrime(true);
    refIdData.setPrime(true);
    keyData.setPrime(true);
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
    typeData
            .jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 1));
    typeKC.jdbcSetValue(typeData.jdbcGetValue());
    refIdData.jdbcSetValue((java.lang.Integer) resultSet
            .getObject(baseIndex + 2));
    refIdKC.jdbcSetValue(refIdData.jdbcGetValue());
    keyData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 3));
    keyKC.jdbcSetValue(keyData.jdbcGetValue());
    valueData
            .jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex + 4));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 5));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Property\n");
    sb.append("type:" + typeData.toString() + "\n");
    sb.append("refId:" + refIdData.toString() + "\n");
    sb.append("key:" + keyData.toString() + "\n");
    sb.append("value:" + valueData.toString() + "\n");
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
    if(!(obj instanceof DbProperty))
    {
      return false;
    }
    DbProperty dbObj = (DbProperty) obj;
    if(!typeData.equals(dbObj.typeData))
    {
      return false;
    }
    if(!refIdData.equals(dbObj.refIdData))
    {
      return false;
    }
    if(!keyData.equals(dbObj.keyData))
    {
      return false;
    }
    if(!valueData.equals(dbObj.valueData))
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
   * Gets the raw data object for type attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTypeData()
  {
    return typeData;
  }

  /**
   * Gets the raw data object for refId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getRefIdData()
  {
    return refIdData;
  }

  /**
   * Gets the raw data object for key attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getKeyData()
  {
    return keyData;
  }

  /**
   * Gets the raw data object for value attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getValueData()
  {
    return valueData;
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
   * Inserts this object to the database table Property.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!refIdData.isValid())
    {
      throw new ObjectNotValidException("refId");
    }
    if(!keyData.isValid())
    {
      throw new ObjectNotValidException("key");
    }
    if(!valueData.isValid())
    {
      throw new ObjectNotValidException("value");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "insert into Property (type, refId, key, value, timeStamp) values (?, ?, ?, ?, ?);";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, typeData.jdbcGetValue());
    ps.setObject(2, refIdData.jdbcGetValue());
    ps.setObject(3, keyData.jdbcGetValue());
    ps.setObject(4, valueData.jdbcGetValue());
    ps.setObject(5, timeStampData.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    if(rows != 1)
    {
      throw new SQLException("Insert did not return 1 row");
    }
  }

  /**
   * Updates the row of this object in the database table Property. The row is
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
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!refIdData.isValid())
    {
      throw new ObjectNotValidException("refId");
    }
    if(!keyData.isValid())
    {
      throw new ObjectNotValidException("key");
    }
    if(!valueData.isValid())
    {
      throw new ObjectNotValidException("value");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "update Property set type = ?, refId = ?, key = ?, value = ?, timeStamp = ? where type = ? AND refId = ? AND key = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, typeData.jdbcGetValue());
    ps.setObject(2, refIdData.jdbcGetValue());
    ps.setObject(3, keyData.jdbcGetValue());
    ps.setObject(4, valueData.jdbcGetValue());
    ps.setObject(5, timeStampData.jdbcGetValue());
    ps.setObject(6, typeKC.jdbcGetValue());
    ps.setObject(7, refIdKC.jdbcGetValue());
    ps.setObject(8, keyKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    try
    {
      typeKC.jdbcSetValue(typeData.jdbcGetValue());
      refIdKC.jdbcSetValue(refIdData.jdbcGetValue());
      keyKC.jdbcSetValue(keyData.jdbcGetValue());
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
   * Deletes the row of this object from the database table Property. The row is
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
    String prepareString
           = "delete from Property where type = ? AND refId = ? AND key = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, typeKC.jdbcGetValue());
    ps.setObject(2, refIdKC.jdbcGetValue());
    ps.setObject(3, keyKC.jdbcGetValue());
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
   * Selects the row of this object from the database table Property and updates
   * the attributes accordingly. The row is identified by the primary key
   * attribute(s).
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
    String prepareString
           = "select * from Property where type = ? AND refId = ? AND key = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, typeData.jdbcGetValue());
    ps.setObject(2, refIdData.jdbcGetValue());
    ps.setObject(3, keyData.jdbcGetValue());
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
   * Property.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Property.
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
      prepareString = "select * from Property";
    }
    else
    {
      prepareString = "select * from Property where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Property.class);
  }

}

// End of file.
