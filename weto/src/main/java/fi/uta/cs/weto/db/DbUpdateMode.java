package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.sqldatatypes.SqlBoolean;
import fi.uta.cs.sqldatatypes.SqlInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Generated database access class for table UpdateMode. Table for checking if
 * weto is in update mode
 */
public class DbUpdateMode extends SqlAssignableObject implements Cloneable
{
  private SqlBoolean inUpdateModeData;
  private SqlBoolean inUpdateModeKC;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbUpdateMode()
  {
    super();
    // true if weto is in update mode and false in normal mode
    inUpdateModeData = new SqlBoolean();
    inUpdateModeKC = new SqlBoolean();
    timeStampData = new SqlInteger();
    inUpdateModeData.setPrime(true);
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
          SQLException, InvalidValueException, ObjectNotValidException
  {
    inUpdateModeData.jdbcSetValue((java.lang.Boolean) resultSet.getObject(
            baseIndex + 1));
    inUpdateModeKC.jdbcSetValue(inUpdateModeData.jdbcGetValue());
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 2));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("UpdateMode\n");
    sb.append("inUpdateMode:" + inUpdateModeData.toString() + "\n");
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
    if(!(obj instanceof DbUpdateMode))
    {
      return false;
    }
    DbUpdateMode dbObj = (DbUpdateMode) obj;
    if(!inUpdateModeData.equals(dbObj.inUpdateModeData))
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
   * Gets the raw data object for inUpdateMode attribute.
   *
   * @return Data object as SqlBoolean.
   */
  public SqlBoolean getInUpdateModeData()
  {
    return inUpdateModeData;
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
   * Inserts this object to the database table UpdateMode.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!inUpdateModeData.isValid())
    {
      throw new ObjectNotValidException("inUpdateMode");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "insert into UpdateMode (inUpdateMode, timeStamp) values (?, ?);";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, inUpdateModeData.jdbcGetValue());
    ps.setObject(2, timeStampData.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    if(rows != 1)
    {
      throw new SQLException("Insert did not return 1 row");
    }
  }

  /**
   * Updates the row of this object in the database table UpdateMode. The row is
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
    if(!inUpdateModeData.isValid())
    {
      throw new ObjectNotValidException("inUpdateMode");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "update UpdateMode set inUpdateMode = ?, timeStamp = ? where inUpdateMode = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, inUpdateModeData.jdbcGetValue());
    ps.setObject(2, timeStampData.jdbcGetValue());
    ps.setObject(3, inUpdateModeKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    try
    {
      inUpdateModeKC.jdbcSetValue(inUpdateModeData.jdbcGetValue());
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
   * Deletes the row of this object from the database table UpdateMode. The row
   * is identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be deleted does not exist.
   */
  public void delete(Connection con) throws SQLException, TooManyItemsException,
                                            NoSuchItemException,
                                            ObjectNotValidException
  {
    String prepareString = "delete from UpdateMode where inUpdateMode = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, inUpdateModeKC.jdbcGetValue());
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
   * Selects the row of this object from the database table UpdateMode and
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
                                            NoSuchItemException,
                                            ObjectNotValidException
  {
    String prepareString = "select * from UpdateMode where inUpdateMode = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, inUpdateModeData.jdbcGetValue());
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
   * UpdateMode.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type UpdateMode.
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
      prepareString = "select * from UpdateMode";
    }
    else
    {
      prepareString = "select * from UpdateMode where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, UpdateMode.class);
  }

}

// End of file.
