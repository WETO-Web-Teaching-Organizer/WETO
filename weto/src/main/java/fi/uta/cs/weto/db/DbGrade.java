package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;
import java.sql.*;
import java.util.Iterator;

/**
 * Generated database access class for table Grade.
 *
 */
public class DbGrade extends SqlAssignableObject implements Cloneable
{
  private SqlInteger taskIdData;
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlInteger reviewerIdData;
  private SqlInteger receiverIdData;
  private SqlReal markData;
  private SqlInteger statusData;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbGrade()
  {
    super();
    taskIdData = new SqlInteger();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    reviewerIdData = new SqlInteger();
    receiverIdData = new SqlInteger();
    markData = new SqlReal();
    statusData = new SqlInteger();
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
    taskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 1));
    idData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 2));
    idKC.jdbcSetValue(idData.jdbcGetValue());
    reviewerIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 3));
    receiverIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 4));
    markData.jdbcSetValue((java.lang.Float) resultSet.getObject(baseIndex + 5));
    statusData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 6));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 7));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Grade\n");
    sb.append("taskId:" + taskIdData.toString() + "\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("reviewerId:" + reviewerIdData.toString() + "\n");
    sb.append("receiverId:" + receiverIdData.toString() + "\n");
    sb.append("mark:" + markData.toString() + "\n");
    sb.append("status:" + statusData.toString() + "\n");
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
    if(!(obj instanceof DbGrade))
    {
      return false;
    }
    DbGrade dbObj = (DbGrade) obj;
    if(!taskIdData.equals(dbObj.taskIdData))
    {
      return false;
    }
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!reviewerIdData.equals(dbObj.reviewerIdData))
    {
      return false;
    }
    if(!receiverIdData.equals(dbObj.receiverIdData))
    {
      return false;
    }
    if(!markData.equals(dbObj.markData))
    {
      return false;
    }
    if(!statusData.equals(dbObj.statusData))
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
   * Gets the raw data object for id attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getIdData()
  {
    return idData;
  }

  /**
   * Gets the raw data object for reviewerId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getReviewerIdData()
  {
    return reviewerIdData;
  }

  /**
   * Gets the raw data object for receiverId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getReceiverIdData()
  {
    return receiverIdData;
  }

  /**
   * Gets the raw data object for mark attribute.
   *
   * @return Data object as SqlReal.
   */
  public SqlReal getMarkData()
  {
    return markData;
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
   * Gets the raw data object for timeStamp attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTimeStampData()
  {
    return timeStampData;
  }

  /**
   * Inserts this object to the database table Grade.
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
    if(!reviewerIdData.isValid())
    {
      throw new ObjectNotValidException("reviewerId");
    }
    if(!receiverIdData.isValid())
    {
      throw new ObjectNotValidException("receiverId");
    }
    if(!markData.isValid())
    {
      throw new ObjectNotValidException("mark");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
                   = "insert into Grade (taskId, reviewerId, receiverId, mark, status, timeStamp) values (?, ?, ?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, taskIdData.jdbcGetValue());
      ps.setObject(2, reviewerIdData.jdbcGetValue());
      ps.setObject(3, receiverIdData.jdbcGetValue());
      ps.setObject(4, markData.jdbcGetValue());
      ps.setObject(5, statusData.jdbcGetValue());
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
   * Updates the row of this object in the database table Grade. The row is
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
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!idData.isValid())
    {
      throw new ObjectNotValidException("id");
    }
    if(!reviewerIdData.isValid())
    {
      throw new ObjectNotValidException("reviewerId");
    }
    if(!receiverIdData.isValid())
    {
      throw new ObjectNotValidException("receiverId");
    }
    if(!markData.isValid())
    {
      throw new ObjectNotValidException("mark");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
                   = "update Grade set taskId = ?, id = ?, reviewerId = ?, receiverId = ?, mark = ?, status = ?, timeStamp = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, taskIdData.jdbcGetValue());
    ps.setObject(2, idData.jdbcGetValue());
    ps.setObject(3, reviewerIdData.jdbcGetValue());
    ps.setObject(4, receiverIdData.jdbcGetValue());
    ps.setObject(5, markData.jdbcGetValue());
    ps.setObject(6, statusData.jdbcGetValue());
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
   * Deletes the row of this object from the database table Grade. The row is
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
    String prepareString = "delete from Grade where id = ?";
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
   * Selects the row of this object from the database table Grade and updates
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
    String prepareString = "select * from Grade where id = ?";
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
   * Grade.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Grade. The
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
      prepareString = "select * from Grade";
    }
    else
    {
      prepareString = "select * from Grade where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Grade.class);
  }

}

// End of file.
