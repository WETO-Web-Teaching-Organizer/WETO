package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class StudentView extends BeanStudentView
{
  public static StudentView select1ByUserId(Connection conn, Integer userId)
          throws NoSuchItemException, SQLException
  {
    StudentView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "userid=" + userId);
    if(iter.hasNext())
    {
      result = (StudentView) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  /**
   * Retrieves StudentView beans with the given task identifier.
   *
   * @param connection open database connection
   * @param taskId task identifier
   * @return collection of StudenView beans
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<StudentView> selectByTaskId(Connection connection,
          Integer taskId)
          throws SQLException
  {
    ArrayList<StudentView> result = new ArrayList<>();
    Iterator<?> iter = StudentView.selectionIterator(connection, "taskid="
            + taskId);
    while(iter.hasNext())
    {
      result.add((StudentView) iter.next());
    }
    return result;
  }

  public static ArrayList<StudentView> selectSubmittersByTaskId(
          Connection conn, Integer taskId)
          throws SQLException, InvalidValueException
  {
    ArrayList<StudentView> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery(
              "SELECT taskid, email, userid, studentnumber, loginname, "
              + "firstname, lastname FROM studentview INNER JOIN "
              + "(SELECT DISTINCT userid FROM submission WHERE taskid="
              + taskId + ") AS x USING(userid) WHERE taskid=" + taskId);
      while(rs.next())
      {
        StudentView row = new StudentView();
        row.setFromResultSet(rs, 0);
        result.add(row);
      }
    }
    return result;
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

  @Override
  public java.lang.String getStudentNumber()
  {
    return WetoUtilities.getRandomStudentNumber(getUserId());
  }
   */
}
