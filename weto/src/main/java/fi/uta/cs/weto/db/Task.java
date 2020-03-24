package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TaskStatus;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Task extends BeanTask
{
  public Task() throws InvalidValueException
  {
    super();
    if(getComponentBits() == null)
    {
      setComponentBits(0);
    }
    if(getShowTextInParent() == null)
    {
      setShowTextInParent(false);
    }
    if(getStatus() == null)
    {
      setStatus(0);
    }
  }

  /**
   * Retrieves task with the given task identifier.
   *
   * @param conn
   * @param taskId task identifier
   * @return task
   * @throws SQLException if the JDBC operation fails.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   */
  public static Task select1ById(Connection conn, Integer taskId)
          throws NoSuchItemException, SQLException
  {
    Task result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + taskId);
    if(iter.hasNext())
    {
      result = (Task) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  /**
   * Retrieves all subtasks of the task.
   *
   * @param conn open database connection
   * @param containerId
   * @return
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Task> selectSubtasks(Connection conn,
          Integer containerId)
          throws SQLException
  {
    ArrayList<Task> subtasks = new ArrayList<>();
    String stmt
                   = "SELECT t.* FROM task t, subtasklink l WHERE t.id=l.subtaskid "
            + "AND l.containerid=" + containerId + " ORDER BY l.rank";
    PreparedStatement ps = conn.prepareStatement(stmt);
    Iterator<?> iter = new SqlSelectionIterator(ps, Task.class);
    while(iter.hasNext())
    {
      subtasks.add((Task) iter.next());
    }
    return subtasks;
  }

  /**
   * Retrieves all ids of subtasks of the task.
   *
   * @param conn open database connection
   * @param containerId
   * @return
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<Integer> selectSubtaskIds(Connection conn,
          Integer containerId)
          throws SQLException
  {
    ArrayList<Integer> subtaskIds = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery("SELECT t.id FROM task t, subtasklink l "
              + "WHERE t.id=l.subtaskid AND l.containerid=" + containerId
              + " ORDER BY l.rank");
      while(rs.next())
      {
        subtaskIds.add(rs.getInt(1));
      }
    }
    return subtaskIds;
  }

  public boolean hasTab(Tab tab) throws NoSuchItemException, SQLException
  {
    return ((tab != null) && (getComponentBits() & tab.getBit()) != 0);
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

  public boolean getHasGroups()
  {
    return ((getComponentBits() & Tab.GROUPS.getBit()) != 0);
  }

  public void setHasGroups(boolean has) throws InvalidValueException
  {
    int componentBit = Tab.GROUPS.getBit();
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

  public boolean getIsRandomTask()
  {
    return ((getStatus() & TaskStatus.RANDOM_TASK.getBit()) != 0);
  }

  public void setIsRandomTask(boolean is) throws InvalidValueException
  {
    if(is)
    {
      setStatus(getStatus() | TaskStatus.RANDOM_TASK.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.RANDOM_TASK.getBit()));
    }
  }

  public boolean getIsAutoGraded()
  {
    return ((getStatus() & TaskStatus.AUTOGRADED.getBit()) != 0);
  }

  public void setIsAutoGraded(boolean is) throws InvalidValueException
  {
    if(is)
    {
      setStatus(getStatus() | TaskStatus.AUTOGRADED.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.AUTOGRADED.getBit()));
    }
  }

  public boolean getNoInline()
  {
    return ((getStatus() & TaskStatus.NO_INLINE.getBit()) != 0);
  }

  public void setNoInline(boolean is) throws InvalidValueException
  {
    if(is)
    {
      setStatus(getStatus() | TaskStatus.NO_INLINE.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.NO_INLINE.getBit()));
    }
  }

  public boolean getHasReviewInstructions()
  {
    return ((getStatus() & TaskStatus.REVIEWINSTRUCTIONS.getBit()) != 0);
  }

  public void setHasReviewInstructions(boolean has) throws InvalidValueException
  {
    if(has)
    {
      setStatus(getStatus() | TaskStatus.REVIEWINSTRUCTIONS.getBit());
    }
    else
    {
      setStatus(getStatus() & (~TaskStatus.REVIEWINSTRUCTIONS.getBit()));
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

  @Override
  public void setText(String text) throws InvalidValueException
  {
    setOldText(getText());
    super.setText(text);
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
      throw new ObjectNotValidException("Error setting main bit or time stamp.");
    }
    super.insert(conn);
  }

  public void insertAsRootTask(Connection conn)
          throws ObjectNotValidException, SQLException, InvalidValueException,
                 NoSuchItemException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting main bit or time stamp.");
    }
    setRootTaskId(null);
    super.insert(conn);
    setRootTaskId(getId());
    super.update(conn);
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

  public void update(Connection conn, boolean updateStamp) throws SQLException,
                                                                  ObjectNotValidException,
                                                                  NoSuchItemException
  {
    super.update(conn);
  }

}
