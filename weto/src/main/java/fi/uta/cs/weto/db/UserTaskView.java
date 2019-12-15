package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.GradeStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class UserTaskView extends BeanUserTaskView
{
  public static ArrayList<UserTaskView> selectByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<UserTaskView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((UserTaskView) iter.next());
    }
    return result;
  }

  public static ArrayList<UserTaskView> selectUniqueByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<UserTaskView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      result.add((UserTaskView) iter.next());
    }
    if(!result.isEmpty())
    {
      removeDuplicates(result);
    }
    return result;
  }

  public static ArrayList<Integer> selectPeerReviewerIdsByTaskId(Connection conn,
          Integer taskId)
          throws SQLException
  {
    ArrayList<Integer> result = new ArrayList<>();
    PreparedStatement ps = conn.prepareStatement(
            "SELECT usertaskview.userid FROM usertaskview, grade WHERE usertaskview.clustertype="
            + ClusterType.STUDENTS.getValue()
            + " AND grade.reviewerid=usertaskview.userid AND usertaskview.taskid="
            + taskId + " AND grade.taskid=" + taskId + " AND (grade.status="
            + GradeStatus.VALID.getValue() + " OR grade.status="
            + GradeStatus.VOID.getValue() + ")");
    ResultSet rs = ps.executeQuery();
    while(rs.next())
    {
      result.add(rs.getInt(1));
    }
    rs.close();
    ps.close();
    return result;
  }

  public static ArrayList<UserTaskView> selectByTaskIdAndClusterType(
          Connection conn, Integer taskId, Integer clusterType)
          throws SQLException
  {
    ArrayList<UserTaskView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId
            + " AND clustertype=" + clusterType);
    while(iter.hasNext())
    {
      result.add((UserTaskView) iter.next());
    }
    return result;
  }

  public static ArrayList<UserTaskView> selectByUserIdAndClusterType(
          Connection conn, Integer userId, Integer clusterType)
          throws SQLException
  {
    ArrayList<UserTaskView> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "userid=" + userId
            + " AND clustertype=" + clusterType);
    while(iter.hasNext())
    {
      result.add((UserTaskView) iter.next());
    }
    return result;
  }

  public static UserTaskView select1ByTaskIdAndUserId(Connection conn,
          Integer taskId, Integer userId)
          throws NoSuchItemException, SQLException
  {
    UserTaskView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " AND userid=" + userId
            + " ORDER BY clustertype DESC");
    if(iter.hasNext())
    {
      result = (UserTaskView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static UserTaskView select1ByTaskIdAndUserIdAndClusterType(
          Connection conn, Integer taskId, Integer userId, Integer clusterType)
          throws NoSuchItemException, SQLException
  {
    UserTaskView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "taskid=" + taskId + " AND userid=" + userId + " AND clustertype="
            + clusterType);
    if(iter.hasNext())
    {
      result = (UserTaskView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  private static void removeDuplicates(ArrayList<UserTaskView> users)
  {
    // Step 1: sort according to last name, first name and user id
    Collections.sort(users, new Comparator<UserTaskView>()
    {
      @Override
      public int compare(UserTaskView a, UserTaskView b)
      {
        int result = a.getLastName().compareTo(b.getLastName());
        if(result == 0)
        {
          result = a.getFirstName().compareTo(b.getFirstName());
        }
        if(result == 0)
        {
          result = a.getUserId().compareTo(b.getUserId());
        }
        return result;
      }

    });
    // Remove duplicate user ids (same user can belong to different clusters)
    int i = 0;
    int j = 1;
    while(true)
    {
      Integer iVal = users.get(i++).getUserId();
      while((j < users.size()) && users.get(j).getUserId().equals(iVal))
      {
        j += 1;
      }
      if(j < users.size())
      {
        if(i != j)
        {
          users.set(i, users.get(j));
        }
        j += 1;
      }
      else
      {
        break;
      }
    }
    for(j = users.size(); j > i; --j)
    {
      users.remove(j - 1);
    }
  }

  /* Overrides for using student anynomization
  @Override
  public java.lang.String getLoginName()
  {
    return WetoUtilities.getRandomLoginName(getUserId());
  }

  @Override
  public java.lang.String getFirstName()
  {
    return WetoUtilities.getRandomFirstName(getUserId());
  }

  @Override
  public java.lang.String getLastName()
  {
    return WetoUtilities.getRandomLastName(getUserId());
  }

  @Override
  public java.lang.String getEmail()
  {
    return WetoUtilities.getRandomEmail(getUserId());
  }
   */
}
