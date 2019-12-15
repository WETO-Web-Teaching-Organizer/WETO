package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Student extends BeanStudent
{
  public static Student select1ByUserId(Connection conn, Integer userId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    Student result = new Student();
    result.setUserId(userId);
    result.select(conn);
    return result;
  }

  public static Student select1ByStudentNumber(Connection conn,
          String studentNumber)
          throws NoSuchItemException, SQLException, InvalidValueException
  {
    Student result = null;
    if(studentNumber != null)
    {
      String prepareString = "SELECT userid, studentnumber, timestamp"
              + " FROM Student WHERE lower(studentnumber)=?";
      PreparedStatement ps = conn.prepareStatement(prepareString);
      ps.setString(1, studentNumber.trim().toLowerCase());
      ResultSet rs = ps.executeQuery();
      if(rs.next())
      {
        result = new Student();
        result.setFromResultSet(rs, 0);
      }
      rs.close();
      ps.close();
    }
    if(result == null)
    {
      throw new NoSuchItemException();
    }
    return result;
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

  /* Overrides for using student anynomization
  @Override
  public java.lang.String getStudentNumber()
  {
    return WetoUtilities.getRandomStudentNumber(getUserId());
  }
   */
}
