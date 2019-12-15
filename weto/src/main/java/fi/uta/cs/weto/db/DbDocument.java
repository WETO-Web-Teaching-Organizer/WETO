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
 * Generated database access class for table Document.
 *
 */
public class DbDocument extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlLongvarchar fileNameData;
  private SqlInteger fileSizeData;
  private SqlLongvarchar mimeTypeData;
  private SqlInteger contentFileSizeData;
  private SqlLongvarchar contentMimeTypeData;
  private SqlInteger contentIdData;
  private SqlInteger timeStampData;

  /**
   * Default constructor.
   */
  public DbDocument()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    fileNameData = new SqlLongvarchar();
    fileSizeData = new SqlInteger();
    mimeTypeData = new SqlLongvarchar();
    contentFileSizeData = new SqlInteger();
    contentMimeTypeData = new SqlLongvarchar();
    contentIdData = new SqlInteger();
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
    fileNameData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 2));
    fileSizeData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 3));
    mimeTypeData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 4));
    contentFileSizeData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 5));
    contentMimeTypeData.jdbcSetValue((java.lang.String) resultSet.getObject(
            baseIndex + 6));
    contentIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 7));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 8));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Document\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("fileName:" + fileNameData.toString() + "\n");
    sb.append("fileSize:" + fileSizeData.toString() + "\n");
    sb.append("mimeType:" + mimeTypeData.toString() + "\n");
    sb.append("contentFileSize:" + contentFileSizeData.toString() + "\n");
    sb.append("contentMimeType:" + contentMimeTypeData.toString() + "\n");
    sb.append("contentId:" + contentIdData.toString() + "\n");
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
    if(!(obj instanceof DbDocument))
    {
      return false;
    }
    DbDocument dbObj = (DbDocument) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!fileNameData.equals(dbObj.fileNameData))
    {
      return false;
    }
    if(!fileSizeData.equals(dbObj.fileSizeData))
    {
      return false;
    }
    if(!mimeTypeData.equals(dbObj.mimeTypeData))
    {
      return false;
    }
    if(!contentFileSizeData.equals(dbObj.contentFileSizeData))
    {
      return false;
    }
    if(!contentMimeTypeData.equals(dbObj.contentMimeTypeData))
    {
      return false;
    }
    if(!contentIdData.equals(dbObj.contentIdData))
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
   * Gets the raw data object for fileName attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getFileNameData()
  {
    return fileNameData;
  }

  /**
   * Gets the raw data object for fileSize attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getFileSizeData()
  {
    return fileSizeData;
  }

  /**
   * Gets the raw data object for mimeType attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getMimeTypeData()
  {
    return mimeTypeData;
  }

  /**
   * Gets the raw data object for contentFileSize attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getContentFileSizeData()
  {
    return contentFileSizeData;
  }

  /**
   * Gets the raw data object for contentMimeType attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getContentMimeTypeData()
  {
    return contentMimeTypeData;
  }

  /**
   * Gets the raw data object for contentId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getContentIdData()
  {
    return contentIdData;
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
   * Inserts this object to the database table Document.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
                                            ObjectNotValidException
  {
    if(!fileNameData.isValid())
    {
      throw new ObjectNotValidException("fileName");
    }
    if(!fileSizeData.isValid())
    {
      throw new ObjectNotValidException("fileSize");
    }
    if(!mimeTypeData.isValid())
    {
      throw new ObjectNotValidException("mimeType");
    }
    if(!contentFileSizeData.isValid())
    {
      throw new ObjectNotValidException("contentFileSize");
    }
    if(!contentMimeTypeData.isValid())
    {
      throw new ObjectNotValidException("contentMimeType");
    }
    if(!contentIdData.isValid())
    {
      throw new ObjectNotValidException("contentId");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "insert into Document (fileName, fileSize, mimeType, contentFileSize, contentMimeType, contentId, timeStamp) values (?, ?, ?, ?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, fileNameData.jdbcGetValue());
      ps.setObject(2, fileSizeData.jdbcGetValue());
      ps.setObject(3, mimeTypeData.jdbcGetValue());
      ps.setObject(4, contentFileSizeData.jdbcGetValue());
      ps.setObject(5, contentMimeTypeData.jdbcGetValue());
      ps.setObject(6, contentIdData.jdbcGetValue());
      ps.setObject(7, timeStampData.jdbcGetValue());
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
   * Updates the row of this object in the database table Document. The row is
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
    if(!fileNameData.isValid())
    {
      throw new ObjectNotValidException("fileName");
    }
    if(!fileSizeData.isValid())
    {
      throw new ObjectNotValidException("fileSize");
    }
    if(!mimeTypeData.isValid())
    {
      throw new ObjectNotValidException("mimeType");
    }
    if(!contentFileSizeData.isValid())
    {
      throw new ObjectNotValidException("contentFileSize");
    }
    if(!contentMimeTypeData.isValid())
    {
      throw new ObjectNotValidException("contentMimeType");
    }
    if(!contentIdData.isValid())
    {
      throw new ObjectNotValidException("contentId");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    String prepareString
           = "update Document set id = ?, fileName = ?, fileSize = ?, mimeType = ?, contentFileSize = ?, contentMimeType = ?, contentId = ?, timeStamp = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, fileNameData.jdbcGetValue());
    ps.setObject(3, fileSizeData.jdbcGetValue());
    ps.setObject(4, mimeTypeData.jdbcGetValue());
    ps.setObject(5, contentFileSizeData.jdbcGetValue());
    ps.setObject(6, contentMimeTypeData.jdbcGetValue());
    ps.setObject(7, contentIdData.jdbcGetValue());
    ps.setObject(8, timeStampData.jdbcGetValue());
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
   * Deletes the row of this object from the database table Document. The row is
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
    String prepareString = "delete from Document where id = ?";
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
   * Selects the row of this object from the database table Document and updates
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
    String prepareString = "select * from Document where id = ?";
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
   * Document.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Document.
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
      prepareString = "select * from Document";
    }
    else
    {
      prepareString = "select * from Document where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Document.class);
  }

}

// End of file.
