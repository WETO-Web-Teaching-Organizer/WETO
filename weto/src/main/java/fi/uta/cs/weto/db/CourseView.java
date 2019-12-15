package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class CourseView extends BeanCourseView
{
  public static ArrayList<CourseView> selectAll(Connection conn)
          throws SQLException
  {
    ArrayList<CourseView> result = new ArrayList<>();
    Iterator<?> iter = CourseView.selectionIterator(conn, null);
    while(iter.hasNext())
    {
      result.add((CourseView) iter.next());
    }
    // Sort the course list according to their ids.
    Collections.sort(result, new Comparator<CourseView>()
    {
      @Override
      public int compare(CourseView o1, CourseView o2)
      {
        return o2.getMasterTaskId().compareTo(o1.getMasterTaskId());
      }

    });
    return result;
  }

  public static CourseView select1ByMasterTaskId(Connection conn, Integer taskId)
          throws NoSuchItemException, SQLException
  {
    CourseView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "mastertaskid=" + taskId);
    if(iter.hasNext())
    {
      result = (CourseView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

}
