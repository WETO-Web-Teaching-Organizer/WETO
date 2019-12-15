package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Subject extends BeanSubject
{
  /**
   * Returns all Subjects
   *
   * @param conn Database connection
   * @return ArrayList that is filled with Subject objects
   * @throws SQLException
   */
  public static ArrayList<Subject> selectAll(Connection conn)
          throws SQLException
  {
    ArrayList<Subject> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, null);
    while(iter.hasNext())
    {
      result.add((Subject) iter.next());
    }
    return result;
  }

  public static ArrayList<Subject> selectIdsAndNames(Connection conn)
          throws SQLException, InvalidValueException
  {
    ArrayList<Subject> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery("SELECT id, name FROM subject");
      while(rs.next())
      {
        Subject row = new Subject();
        row.setId(rs.getInt(1));
        row.setName(rs.getString(2));
        result.add(row);
      }
    }
    return result;
  }

  /**
   * Returns Subject by id.
   *
   * @param conn Database connection
   * @param subjectId Subject ID
   * @return Subject
   * @throws SQLException
   * @see java.sql.SQLException
   * @throws InvalidValueException
   * @throws NoSuchItemException
   */
  public static Subject select1ById(Connection conn, Integer subjectId)
          throws SQLException, InvalidValueException, NoSuchItemException
  {
    Subject result = new Subject();
    result.setId(subjectId);
    result.select(conn);
    return result;
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
