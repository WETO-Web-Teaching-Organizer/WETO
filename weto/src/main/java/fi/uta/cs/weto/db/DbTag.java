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
 * Generated database access class for table Tag.
 *
 */
public class DbTag extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlInteger authorIdData;
  private SqlInteger statusData;
  private SqlInteger rankData;
  private SqlInteger timeStampData;
  private SqlLongvarchar textData;
  private SqlInteger typeData;
  private SqlInteger taggedIdData;

  /**
   * Default constructor.
   */
  public DbTag()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    authorIdData = new SqlInteger();
    statusData = new SqlInteger();
    rankData = new SqlInteger();
    timeStampData = new SqlInteger();
    textData = new SqlLongvarchar();
    // Specifies the type of the tag (e.g. note, forum message, review) as an integer id.
    typeData = new SqlInteger();
    taggedIdData = new SqlInteger();
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
    authorIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 2));
    statusData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 3));
    rankData
            .jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 4));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 5));
    textData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex + 6));
    typeData
            .jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 7));
    taggedIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 8));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Tag\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("authorId:" + authorIdData.toString() + "\n");
    sb.append("status:" + statusData.toString() + "\n");
    sb.append("rank:" + rankData.toString() + "\n");
    sb.append("timeStamp:" + timeStampData.toString() + "\n");
    sb.append("text:" + textData.toString() + "\n");
    sb.append("type:" + typeData.toString() + "\n");
    sb.append("taggedId:" + taggedIdData.toString() + "\n");
    sb.append("\n");
    return (sb.toString());
  }

  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof DbTag))
    {
      return false;
    }
    DbTag dbObj = (DbTag) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!authorIdData.equals(dbObj.authorIdData))
    {
      return false;
    }
    if(!statusData.equals(dbObj.statusData))
    {
      return false;
    }
    if(!rankData.equals(dbObj.rankData))
    {
      return false;
    }
    if(!timeStampData.equals(dbObj.timeStampData))
    {
      return false;
    }
    if(!textData.equals(dbObj.textData))
    {
      return false;
    }
    if(!typeData.equals(dbObj.typeData))
    {
      return false;
    }
    if(!taggedIdData.equals(dbObj.taggedIdData))
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
   * Gets the raw data object for authorId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getAuthorIdData()
  {
    return authorIdData;
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
   * Gets the raw data object for rank attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getRankData()
  {
    return rankData;
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
   * Gets the raw data object for text attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getTextData()
  {
    return textData;
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
   * Gets the raw data object for taggedId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTaggedIdData()
  {
    return taggedIdData;
  }

  /**
   * Inserts this object to the database table Tag.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!authorIdData.isValid())
    {
      throw new ObjectNotValidException("authorId");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!rankData.isValid())
    {
      throw new ObjectNotValidException("rank");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!textData.isValid())
    {
      throw new ObjectNotValidException("text");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!taggedIdData.isValid())
    {
      throw new ObjectNotValidException("taggedId");
    }
    String prepareString
           = "insert into Tag (authorId, status, rank, timeStamp, text, type, taggedId) values (?, ?, ?, ?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, authorIdData.jdbcGetValue());
      ps.setObject(2, statusData.jdbcGetValue());
      ps.setObject(3, rankData.jdbcGetValue());
      ps.setObject(4, timeStampData.jdbcGetValue());
      ps.setObject(5, textData.jdbcGetValue());
      ps.setObject(6, typeData.jdbcGetValue());
      ps.setObject(7, taggedIdData.jdbcGetValue());
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
   * Updates the row of this object in the database table Tag. The row is
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
    if(!authorIdData.isValid())
    {
      throw new ObjectNotValidException("authorId");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!rankData.isValid())
    {
      throw new ObjectNotValidException("rank");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!textData.isValid())
    {
      throw new ObjectNotValidException("text");
    }
    if(!typeData.isValid())
    {
      throw new ObjectNotValidException("type");
    }
    if(!taggedIdData.isValid())
    {
      throw new ObjectNotValidException("taggedId");
    }
    String prepareString
           = "update Tag set id = ?, authorId = ?, status = ?, rank = ?, timeStamp = ?, text = ?, type = ?, taggedId = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, authorIdData.jdbcGetValue());
    ps.setObject(3, statusData.jdbcGetValue());
    ps.setObject(4, rankData.jdbcGetValue());
    ps.setObject(5, timeStampData.jdbcGetValue());
    ps.setObject(6, textData.jdbcGetValue());
    ps.setObject(7, typeData.jdbcGetValue());
    ps.setObject(8, taggedIdData.jdbcGetValue());
    ps.setObject(9, idKC.jdbcGetValue());
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
   * Deletes the row of this object from the database table Tag. The row is
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
    String prepareString = "delete from Tag where id = ?";
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
   * Selects the row of this object from the database table Tag and updates the
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
                                            NoSuchItemException
  {
    String prepareString = "select * from Tag where id = ?";
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
   * Constructs and returns a selection iterator for rows in database table Tag.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Tag. The
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
      prepareString = "select * from Tag";
    }
    else
    {
      prepareString = "select * from Tag where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Tag.class);
  }

}

// End of file.
