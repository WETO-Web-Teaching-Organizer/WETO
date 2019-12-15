package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TaskStatus;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SubtaskView extends BeanSubtaskView
{
  public static boolean hasSubtasks(Connection conn, Integer taskId)
          throws SQLException
  {
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "containerid=" + taskId);
    boolean result = iter.hasNext();
    iter.close();
    return result;
  }

  /**
   * Retrieve list of subtasks for given container task.
   *
   * @param conn open database connection
   * @param containerId container task identifier
   * @return list of subtasks
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<SubtaskView> selectByContainerId(Connection conn,
          Integer containerId)
          throws SQLException
  {
    ArrayList<SubtaskView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "containerid=" + containerId);
    while(iter.hasNext())
    {
      result.add((SubtaskView) iter.next());
    }
    return result;
  }

  public static ArrayList<SubtaskView> selectByCourseTaskId(Connection conn,
          Integer courseTaskId)
          throws SQLException
  {
    ArrayList<SubtaskView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "roottaskid=" + courseTaskId);
    while(iter.hasNext())
    {
      result.add((SubtaskView) iter.next());
    }
    return result;
  }

  /**
   * Retrieve a subtaskview identified by parameter taskId
   *
   * @param conn open database connection
   * @param taskId subtask task identifier
   * @return subtaskview object
   * @throws fi.uta.cs.sqldatamodel.NoSuchItemException
   * @throws SQLException if the JDBC operation fails.
   */
  public static SubtaskView select1ById(Connection conn, Integer taskId)
          throws NoSuchItemException, SQLException
  {
    SubtaskView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + taskId);
    if(iter.hasNext())
    {
      result = (SubtaskView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public boolean hasTab(Tab tab) throws NoSuchItemException, SQLException
  {
    return ((getComponentBits() & tab.getBit()) != 0);
  }

  public static boolean hasTab(Connection conn, Integer taskId, Tab tab)
          throws NoSuchItemException, SQLException
  {
    return Task.select1ById(conn, taskId).hasTab(tab);
  }

  public boolean getHasSubmissions()
  {
    return ((getComponentBits() & Tab.SUBMISSIONS.getBit()) != 0);
  }

  public void setHasSubmissions(boolean has) throws InvalidValueException
  {
    int componentBit = Tab.SUBMISSIONS.getBit();
    if(has)
    {
      setComponentBits(getComponentBits() | componentBit);
    }
    else
    {
      setComponentBits(getComponentBits() & (~componentBit));
    }
  }

  public boolean getHasGrades()
  {
    return ((getComponentBits() & Tab.GRADING.getBit()) != 0);
  }

  public void setHasGrades(boolean has) throws InvalidValueException
  {
    int componentBit = Tab.GRADING.getBit();
    if(has)
    {
      setComponentBits(getComponentBits() | componentBit);
    }
    else
    {
      setComponentBits(getComponentBits() & (~componentBit));
    }
  }

  public boolean getHasForum()
  {
    return ((getComponentBits() & Tab.FORUM.getBit()) != 0);
  }

  public void setHasForum(boolean has) throws InvalidValueException
  {
    int componentBit = Tab.FORUM.getBit();
    if(has)
    {
      setComponentBits(getComponentBits() | componentBit);
    }
    else
    {
      setComponentBits(getComponentBits() & (~componentBit));
    }
  }

  public boolean getIsHidden()
  {
    return ((getStatus() & TaskStatus.HIDDEN.getBit()) != 0);
  }

  public void setIsHidden(boolean is) throws InvalidValueException
  {
    if(is)
    {
      setStatus(getStatus() | TaskStatus.HIDDEN.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.HIDDEN.getBit()));
    }
  }

  public boolean getIsQuiz()
  {
    return ((getStatus() & TaskStatus.QUIZ.getBit()) != 0);
  }

  public void setIsQuiz(boolean is) throws InvalidValueException
  {
    if(is)
    {
      setStatus(getStatus() | TaskStatus.QUIZ.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.QUIZ.getBit()));
    }
  }

  public boolean getIsPublic()
  {
    return ((getStatus() & TaskStatus.PUBLIC.getBit()) != 0);
  }

  public void setIsPublic(boolean is) throws InvalidValueException
  {
    if(is)
    {
      setStatus(getStatus() | TaskStatus.PUBLIC.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.PUBLIC.getBit()));
    }
  }

}
