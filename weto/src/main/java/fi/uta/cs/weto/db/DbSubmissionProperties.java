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
 * Generated database access class for table SubmissionProperties.
 *
 */
public class DbSubmissionProperties extends SqlAssignableObject implements
        Cloneable
{
  private SqlInteger taskIdData;
  private SqlInteger taskIdKC;
  private SqlLongvarchar propertiesData;
  private SqlLongvarchar propertiesKC;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbSubmissionProperties()
  {
    super();
    taskIdData = new SqlInteger();
    taskIdKC = new SqlInteger();
    propertiesData = new SqlLongvarchar();
    propertiesKC = new SqlLongvarchar(null);
    timeStampData = new SqlInteger();
    taskIdData.setPrime(true);
    propertiesData.setPrime(true);
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
    taskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 1));
    taskIdKC.jdbcSetValue(taskIdData.jdbcGetValue());
    propertiesData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 2));
    propertiesKC.jdbcSetValue(propertiesData.jdbcGetValue());
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 3));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("SubmissionProperties\n");
    sb.append("taskId:" + taskIdData.toString() + "\n");
    sb.append("properties:" + propertiesData.toString() + "\n");
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
    if(!(obj instanceof DbSubmissionProperties))
    {
      return false;
    }
    DbSubmissionProperties dbObj = (DbSubmissionProperties) obj;
    if(!taskIdData.equals(dbObj.taskIdData))
    {
      return false;
    }
    if(!propertiesData.equals(dbObj.propertiesData))
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
   * Gets the raw data object for taskId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTaskIdData()
  {
    return taskIdData;
  }

  /**
   * Gets the raw data object for properties attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getPropertiesData()
  {
    return propertiesData;
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
   * Inserts this object to the database table SubmissionProperties.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!propertiesData.isValid())
    {
      throw new ObjectNotValidException("properties");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "insert into SubmissionProperties (taskId, properties, timeStamp) values (?, ?, ?);";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, taskIdData.jdbcGetValue());
    ps.setObject(2, propertiesData.jdbcGetValue());
    ps.setObject(3, timeStampData.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    if(rows != 1)
    {
      throw new SQLException("Insert did not return 1 row");
    }
  }

  /**
   * Updates the row of this object in the database table SubmissionProperties.
   * The row is identified by the primary key attribute(s).
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
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!propertiesData.isValid())
    {
      throw new ObjectNotValidException("properties");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "update SubmissionProperties set taskId = ?, properties = ?, timeStamp = ? where taskId = ? AND properties = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, taskIdData.jdbcGetValue());
    ps.setObject(2, propertiesData.jdbcGetValue());
    ps.setObject(3, timeStampData.jdbcGetValue());
    ps.setObject(4, taskIdKC.jdbcGetValue());
    ps.setObject(5, propertiesKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    try
    {
      taskIdKC.jdbcSetValue(taskIdData.jdbcGetValue());
      propertiesKC.jdbcSetValue(propertiesData.jdbcGetValue());
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
   * Deletes the row of this object from the database table
   * SubmissionProperties. The row is identified by the primary key
   * attribute(s).
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
           = "delete from SubmissionProperties where taskId = ? AND properties = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, taskIdKC.jdbcGetValue());
    ps.setObject(2, propertiesKC.jdbcGetValue());
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
   * Selects the row of this object from the database table SubmissionProperties
   * and updates the attributes accordingly. The row is identified by the
   * primary key attribute(s).
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
           = "select * from SubmissionProperties where taskId = ? AND properties = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, taskIdData.jdbcGetValue());
    ps.setObject(2, propertiesData.jdbcGetValue());
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
   * SubmissionProperties.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type
   * SubmissionProperties. The iterator is closed when hasNext() returns false
   * or the iterator is finalized.
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
      prepareString = "select * from SubmissionProperties";
    }
    else
    {
      prepareString = "select * from SubmissionProperties where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, SubmissionProperties.class);
  }

}

// End of file.
