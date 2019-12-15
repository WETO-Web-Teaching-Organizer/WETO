package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class DatabasePool extends BeanDatabasePool
{
  public static final Integer MASTER_ID = null;

  public static ArrayList<DatabasePool> selectAll(Connection conn)
          throws SQLException
  {
    ArrayList<DatabasePool> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, null);
    while(iter.hasNext())
    {
      result.add((DatabasePool) iter.next());
    }
    return result;
  }

  public static Vector<DatabasePool> selectAllVector(Connection conn)
          throws SQLException
  {
    Vector<DatabasePool> result = new Vector<>();
    Iterator<?> iter = selectionIterator(conn, null);
    while(iter.hasNext())
    {
      result.add((DatabasePool) iter.next());
    }
    return result;
  }

  public static HashMap<Integer, String> selectNamesWithMaster(Connection conn)
          throws SQLException
  {
    HashMap<Integer, String> result = new HashMap<>();
    result.put(MASTER_ID, "master");
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery("SELECT id, name FROM databasepool");
      while(rs.next())
      {
        result.put(rs.getInt(1), rs.getString(2));
      }
    }
    return result;
  }

  public static ArrayList<DatabasePool> selectIdsAndNames(Connection conn)
          throws SQLException, InvalidValueException
  {
    ArrayList<DatabasePool> result = new ArrayList<>();
    try(Statement stat = conn.createStatement())
    {
      ResultSet rs = stat.executeQuery("SELECT id, name FROM databasepool");
      while(rs.next())
      {
        DatabasePool row = new DatabasePool();
        row.setId(rs.getInt(1));
        row.setName(rs.getString(2));
        result.add(row);
      }
    }
    return result;
  }

  public static DatabasePool select1ById(Connection conn, Integer databaseId)
          throws NoSuchItemException, SQLException
  {
    DatabasePool result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + databaseId);
    if(iter.hasNext())
    {
      result = (DatabasePool) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
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

}
