package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class CourseImplementation extends BeanCourseImplementation
{
  public static final int ACCEPT_ALL_STUDENTS = (1 << 1);

  public static CourseImplementation select1ByMasterTaskId(Connection conn,
          Integer masterTaskId)
          throws NoSuchItemException, SQLException
  {
    CourseImplementation result = null;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "mastertaskid=" + masterTaskId);
    if(iter.hasNext())
    {
      result = (CourseImplementation) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static CourseImplementation select1ByDatabaseIdAndCourseTaskId(
          Connection conn, Integer databaseId, Integer courseTaskId)
          throws NoSuchItemException, SQLException
  {
    CourseImplementation result = null;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "databaseid=" + databaseId + " AND coursetaskid=" + courseTaskId);
    if(iter.hasNext())
    {
      result = (CourseImplementation) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static ArrayList<CourseImplementation> selectAll(Connection conn)
          throws SQLException
  {
    ArrayList<CourseImplementation> result = new ArrayList<>();
    Iterator<?> iter = CourseImplementation.selectionIterator(conn, null);
    while(iter.hasNext())
    {
      result.add((CourseImplementation) iter.next());
    }
    return result;
  }

  @Override
  public void insert(Connection con) throws SQLException,
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
    super.insert(con);
  }

  @Override
  public void update(Connection con) throws SQLException,
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
    super.update(con);
  }

  public boolean getAcceptAllStudents()
  {
    Integer status = getStatus();
    return ((status != null) && ((status & ACCEPT_ALL_STUDENTS) != 0));
  }

  public void setAcceptAllStudents(Boolean value) throws
          InvalidValueException
  {
    if(value != null)
    {
      Integer status = getStatus();
      if(status == null)
      {
        status = 0;
      }
      if(value)
      {
        setStatus(status | ACCEPT_ALL_STUDENTS);
      }
      else
      {
        setStatus(status & (~ACCEPT_ALL_STUDENTS));
      }
    }
  }

}
