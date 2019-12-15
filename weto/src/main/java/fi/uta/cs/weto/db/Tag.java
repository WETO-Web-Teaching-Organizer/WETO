package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Tag extends BeanTag
{
  public Tag() throws InvalidValueException
  {
    super();
    this.setStatus(0);
    this.setRank(0);
    this.setText(null);
  }

  /**
   * Retrieves tag with the given tag identifier.
   *
   * @param conn
   * @param tagId
   * @return tag
   * @throws InvalidValueException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   * @throws ObjectNotValidException
   * @throws SQLException if the JDBC operation fails.
   */
  public static Tag select1ById(Connection conn, Integer tagId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    Tag result = new Tag();
    result.setId(tagId);
    result.select(conn);
    return result;
  }

  /**
   * Retrieves tags associated with the given tagged object and tag type.
   *
   * @param conn
   * @param taggedId tagged object identifier
   * @param tagType tag type identifier
   * @return collection of tags
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Tag> selectByTaggedIdAndType(Connection conn,
          Integer taggedId, Integer tagType)
          throws SQLException
  {
    ArrayList<Tag> tags = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taggedid=" + taggedId
            + " AND type=" + tagType + " ORDER BY rank, timestamp");
    while(iter.hasNext())
    {
      tags.add((Tag) iter.next());
    }
    return tags;
  }

  public static ArrayList<Tag> selectByAuthorIdAndType(Connection conn,
          Integer authorId, Integer tagType)
          throws SQLException
  {
    ArrayList<Tag> tags = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "authorid=" + authorId
            + " AND type=" + tagType + " ORDER BY timestamp");
    while(iter.hasNext())
    {
      tags.add((Tag) iter.next());
    }
    return tags;
  }

  public static ArrayList<Tag> selectByTaggedIdAndAuthorIdAndType(
          Connection conn, Integer taggedId, Integer authorId, Integer tagType)
          throws SQLException
  {
    ArrayList<Tag> tags = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taggedid=" + taggedId
            + " AND authorid=" + authorId + " AND type=" + tagType
            + " ORDER BY timestamp");
    while(iter.hasNext())
    {
      tags.add((Tag) iter.next());
    }
    return tags;
  }

  public static ArrayList<Tag> selectByTaggedIdAndStatusAndType(
          Connection conn, Integer taggedId, Integer status, Integer tagType)
          throws SQLException
  {
    ArrayList<Tag> tags = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taggedid=" + taggedId
            + " AND status=" + status + " AND type=" + tagType
            + " ORDER BY timestamp");
    while(iter.hasNext())
    {
      tags.add((Tag) iter.next());
    }
    return tags;
  }

  public static ArrayList<Tag> selectByTaggedIdAndRankAndType(Connection conn,
          Integer taggedId, Integer rank, Integer tagType)
          throws SQLException
  {
    ArrayList<Tag> tags = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taggedid=" + taggedId
            + " AND rank=" + rank + " AND type=" + tagType);
    while(iter.hasNext())
    {
      tags.add((Tag) iter.next());
    }
    return tags;
  }

  public static Tag select1ByTaggedIdAndRankAndAuthorIdAndType(Connection conn,
          Integer taggedId, Integer rank, Integer authorId, Integer tagType)
          throws SQLException, NoSuchItemException
  {
    Tag result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taggedid=" + taggedId + " AND rank=" + rank + " AND authorid="
            + authorId + " AND type=" + tagType);
    if(iter.hasNext())
    {
      result = (Tag) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  /**
   * Deletes tags associated with the given tagged object.
   *
   * @param conn open database connection
   * @param taggedId tagged object identifier
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   * @throws fi.uta.cs.sqldatamodel.ObjectNotValidException
   */
  public static void deleteByTaggedIdAndType(Connection conn, Integer taggedId,
          Integer tagType)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 ObjectNotValidException
  {
    ArrayList<Tag> tags = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taggedid=" + taggedId
            + " AND type=" + tagType);
    while(iter.hasNext())
    {
      ((Tag) iter.next()).delete(conn);
    }
  }

  @Override
  public void insert(Connection conn)
          throws ObjectNotValidException, SQLException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.insert(conn);
  }

  public void insert(Connection conn, int timeStamp)
          throws ObjectNotValidException, SQLException
  {
    try
    {
      setTimeStamp(timeStamp);
    }
    catch(InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.insert(conn);
  }

  @Override
  public void update(Connection conn) throws SQLException,
                                             ObjectNotValidException,
                                             NoSuchItemException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.update(conn);
  }

  public String getTimeStampString() throws WetoTimeStampException
  {
    String time = new WetoTimeStamp(getTimeStamp()).toString();
    int secondsBoundary = time.lastIndexOf(':');
    return time.substring(0, secondsBoundary);
  }

}
