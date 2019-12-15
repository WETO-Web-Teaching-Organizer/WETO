package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class News extends BeanNews
{
  /**
   * Returns a vector of all news found in the master database
   *
   * @param conn connection to master db
   * @return ArrayList of news
   * @throws java.sql.SQLException
   */
  public static ArrayList<News> selectAll(Connection conn)
          throws SQLException
  {
    ArrayList<News> result = new ArrayList<>();
    Iterator<?> newsIterator = selectionIterator(conn, null);
    while(newsIterator.hasNext())
    {
      result.add((News) newsIterator.next());
    }
    return result;
  }

  /**
   * Retrieve news with given id.
   *
   * @param conn open master database connection
   * @param newsId news identifier
   * @return news
   * @throws InvalidValueException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   * @throws SQLException if the JDBC operation fails.
   */
  public static News select1ById(Connection conn, Integer newsId)
          throws InvalidValueException, NoSuchItemException, SQLException
  {
    News news = new News();
    news.setId(newsId);
    news.select(conn);
    return news;
  }

  /**
   * Returns all news that have a showUntilDate later than the current date/time
   *
   * @param conn Connection to master db
   * @return ArrayList of currently active News items
   * @throws SQLException
   * @throws fi.uta.cs.weto.model.WetoTimeStampException
   */
  public static ArrayList<News> selectActive(Connection conn) throws
          SQLException, WetoTimeStampException
  {
    ArrayList<News> result = new ArrayList<>();
    int nowStamp = new WetoTimeStamp(new GregorianCalendar()).getTimeStamp();
    Iterator<?> iter = selectionIterator(conn,
            "(startdate IS NULL OR startdate <= " + nowStamp
            + ") AND (enddate IS NULL OR enddate >= " + nowStamp + ")");
    while(iter.hasNext())
    {
      result.add((News) iter.next());
    }
    return result;
  }

  public String getStartDateString() throws WetoTimeStampException
  {
    if(getStartDate() != null)
    {
      return new WetoTimeStamp(getStartDate()).toString();
    }
    else
    {
      return new String("");
    }
  }

  public String getEndDateString() throws WetoTimeStampException
  {
    if(getEndDate() != null)
    {
      return new WetoTimeStamp(getEndDate()).toString();
    }
    else
    {
      return new String("");
    }
  }

  @Override
  public void insert(Connection conn) throws SQLException,
                                             ObjectNotValidException
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

}
