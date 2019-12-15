package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.sqldatatypes.SqlInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Generated database access class for table RightsCluster.
 *
 */
public class DbRightsCluster extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlInteger taskIdData;
  private SqlInteger ownerViewBitsData;
  private SqlInteger ownerUpdateBitsData;
  private SqlInteger ownerCreateBitsData;
  private SqlInteger ownerDeleteBitsData;
  private SqlInteger timeStampData;
  private SqlInteger generalViewBitsData;
  private SqlInteger generalUpdateBitsData;
  private SqlInteger generalCreateBitsData;
  private SqlInteger generalDeleteBitsData;
  private SqlInteger typeData;

  /**
   * Default constructor.
   */
  public DbRightsCluster()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    taskIdData = new SqlInteger();
    ownerViewBitsData = new SqlInteger();
    ownerUpdateBitsData = new SqlInteger();
    ownerCreateBitsData = new SqlInteger();
    ownerDeleteBitsData = new SqlInteger();
    timeStampData = new SqlInteger();
    generalViewBitsData = new SqlInteger();
    generalUpdateBitsData = new SqlInteger();
    generalCreateBitsData = new SqlInteger();
    generalDeleteBitsData = new SqlInteger();
    typeData = new SqlInteger();
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
    taskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 2));
    ownerViewBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 3));
    ownerUpdateBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 4));
    ownerCreateBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 5));
    ownerDeleteBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 6));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 7));
    generalViewBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 8));
    generalUpdateBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 9));
    generalCreateBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 10));
    generalDeleteBitsData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 11));
    typeData.jdbcSetValue((java.lang.Integer) resultSet
            .getObject(baseIndex + 12));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("RightsCluster\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("taskId:" + taskIdData.toString() + "\n");
    sb.append("ownerViewBits:" + ownerViewBitsData.toString() + "\n");
    sb.append("ownerUpdateBits:" + ownerUpdateBitsData.toString() + "\n");
    sb.append("ownerCreateBits:" + ownerCreateBitsData.toString() + "\n");
    sb.append("ownerDeleteBits:" + ownerDeleteBitsData.toString() + "\n");
    sb.append("timeStamp:" + timeStampData.toString() + "\n");
    sb.append("generalViewBits:" + generalViewBitsData.toString() + "\n");
    sb.append("generalUpdateBits:" + generalUpdateBitsData.toString() + "\n");
    sb.append("generalCreateBits:" + generalCreateBitsData.toString() + "\n");
    sb.append("generalDeleteBits:" + generalDeleteBitsData.toString() + "\n");
    sb.append("type:" + typeData.toString() + "\n");
    sb.append("\n");
    return (sb.toString());
  }

  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof DbRightsCluster))
    {
      return false;
    }
    DbRightsCluster dbObj = (DbRightsCluster) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!taskIdData.equals(dbObj.taskIdData))
    {
      return false;
    }
    if(!ownerViewBitsData.equals(dbObj.ownerViewBitsData))
    {
      return false;
    }
    if(!ownerUpdateBitsData.equals(dbObj.ownerUpdateBitsData))
    {
      return false;
    }
    if(!ownerCreateBitsData.equals(dbObj.ownerCreateBitsData))
    {
      return false;
    }
    if(!ownerDeleteBitsData.equals(dbObj.ownerDeleteBitsData))
    {
      return false;
    }
    if(!timeStampData.equals(dbObj.timeStampData))
    {
      return false;
    }
    if(!generalViewBitsData.equals(dbObj.generalViewBitsData))
    {
      return false;
    }
    if(!generalUpdateBitsData.equals(dbObj.generalUpdateBitsData))
    {
      return false;
    }
    if(!generalCreateBitsData.equals(dbObj.generalCreateBitsData))
    {
      return false;
    }
    if(!generalDeleteBitsData.equals(dbObj.generalDeleteBitsData))
    {
      return false;
    }
    if(!typeData.equals(dbObj.typeData))
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
   * Gets the raw data object for taskId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTaskIdData()
  {
    return taskIdData;
  }

  /**
   * Gets the raw data object for ownerViewBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getOwnerViewBitsData()
  {
    return ownerViewBitsData;
  }

  /**
   * Gets the raw data object for ownerUpdateBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getOwnerUpdateBitsData()
  {
    return ownerUpdateBitsData;
  }

  /**
   * Gets the raw data object for ownerCreateBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getOwnerCreateBitsData()
  {
    return ownerCreateBitsData;
  }

  /**
   * Gets the raw data object for ownerDeleteBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getOwnerDeleteBitsData()
  {
    return ownerDeleteBitsData;
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
   * Gets the raw data object for generalViewBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getGeneralViewBitsData()
  {
    return generalViewBitsData;
  }

  /**
   * Gets the raw data object for generalUpdateBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getGeneralUpdateBitsData()
  {
    return generalUpdateBitsData;
  }

  /**
   * Gets the raw data object for generalCreateBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getGeneralCreateBitsData()
  {
    return generalCreateBitsData;
  }

  /**
   * Gets the raw data object for generalDeleteBits attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getGeneralDeleteBitsData()
  {
    return generalDeleteBitsData;
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
   * Inserts this object to the database table RightsCluster.
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
    if(!ownerViewBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerViewBits");
    }
    if(!ownerUpdateBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerUpdateBits");
    }
    if(!ownerCreateBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerCreateBits");
    }
    if(!ownerDeleteBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerDeleteBits");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!generalViewBitsData.isValid())
    {
      throw new ObjectNotValidException("generalViewBits");
    }
    if(!generalUpdateBitsData.isValid())
    {
      throw new ObjectNotValidException("generalUpdateBits");
    }
    if(!generalCreateBitsData.isValid())
    {
      throw new ObjectNotValidException("generalCreateBits");
    }
    if(!generalDeleteBitsData.isValid())
    {
      throw new ObjectNotValidException("generalDeleteBits");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    String prepareString
           = "insert into RightsCluster (taskId, ownerViewBits, ownerUpdateBits, ownerCreateBits, ownerDeleteBits, timeStamp, generalViewBits, generalUpdateBits, generalCreateBits, generalDeleteBits, type) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, taskIdData.jdbcGetValue());
      ps.setObject(2, ownerViewBitsData.jdbcGetValue());
      ps.setObject(3, ownerUpdateBitsData.jdbcGetValue());
      ps.setObject(4, ownerCreateBitsData.jdbcGetValue());
      ps.setObject(5, ownerDeleteBitsData.jdbcGetValue());
      ps.setObject(6, timeStampData.jdbcGetValue());
      ps.setObject(7, generalViewBitsData.jdbcGetValue());
      ps.setObject(8, generalUpdateBitsData.jdbcGetValue());
      ps.setObject(9, generalCreateBitsData.jdbcGetValue());
      ps.setObject(10, generalDeleteBitsData.jdbcGetValue());
      ps.setObject(11, typeData.jdbcGetValue());
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
   * Updates the row of this object in the database table RightsCluster. The row
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
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!ownerViewBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerViewBits");
    }
    if(!ownerUpdateBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerUpdateBits");
    }
    if(!ownerCreateBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerCreateBits");
    }
    if(!ownerDeleteBitsData.isValid())
    {
      throw new ObjectNotValidException("ownerDeleteBits");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!generalViewBitsData.isValid())
    {
      throw new ObjectNotValidException("generalViewBits");
    }
    if(!generalUpdateBitsData.isValid())
    {
      throw new ObjectNotValidException("generalUpdateBits");
    }
    if(!generalCreateBitsData.isValid())
    {
      throw new ObjectNotValidException("generalCreateBits");
    }
    if(!generalDeleteBitsData.isValid())
    {
      throw new ObjectNotValidException("generalDeleteBits");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    String prepareString
           = "update RightsCluster set id = ?, taskId = ?, ownerViewBits = ?, ownerUpdateBits = ?, ownerCreateBits = ?, ownerDeleteBits = ?, timeStamp = ?, generalViewBits = ?, generalUpdateBits = ?, generalCreateBits = ?, generalDeleteBits = ?, type = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, taskIdData.jdbcGetValue());
    ps.setObject(3, ownerViewBitsData.jdbcGetValue());
    ps.setObject(4, ownerUpdateBitsData.jdbcGetValue());
    ps.setObject(5, ownerCreateBitsData.jdbcGetValue());
    ps.setObject(6, ownerDeleteBitsData.jdbcGetValue());
    ps.setObject(7, timeStampData.jdbcGetValue());
    ps.setObject(8, generalViewBitsData.jdbcGetValue());
    ps.setObject(9, generalUpdateBitsData.jdbcGetValue());
    ps.setObject(10, generalCreateBitsData.jdbcGetValue());
    ps.setObject(11, generalDeleteBitsData.jdbcGetValue());
    ps.setObject(12, typeData.jdbcGetValue());
    ps.setObject(13, idKC.jdbcGetValue());
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
   * Deletes the row of this object from the database table RightsCluster. The
   * row is identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be deleted does not exist.
   */
  public void delete(Connection con) throws SQLException, TooManyItemsException,
                                            NoSuchItemException
  {
    String prepareString = "delete from RightsCluster where id = ?";
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
   * Selects the row of this object from the database table RightsCluster and
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
    String prepareString = "select * from RightsCluster where id = ?";
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
   * RightsCluster.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type
   * RightsCluster. The iterator is closed when hasNext() returns false or the
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
      prepareString = "select * from RightsCluster";
    }
    else
    {
      prepareString = "select * from RightsCluster where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, RightsCluster.class);
  }

}

// End of file.
