package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Property extends BeanProperty
{
  public static ArrayList<Property> selectByType(Connection conn, Integer type)
          throws SQLException
  {
    ArrayList<Property> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "type=" + type
            + " ORDER BY timestamp DESC");
    while(iter.hasNext())
    {
      result.add((Property) iter.next());
    }
    return result;
  }

  public static ArrayList<Property> selectByTypeAndRefId(Connection conn,
          Integer type, Integer refId)
          throws SQLException
  {
    ArrayList<Property> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "type=" + type
            + " AND refid=" + refId
            + " ORDER BY timestamp");
    while(iter.hasNext())
    {
      result.add((Property) iter.next());
    }
    return result;
  }

  public static Property select1ByTypeAndRefIdAndKey(Connection conn,
          Integer type, Integer refId, Integer key)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    Property result = new Property();
    result.setType(type);
    result.setRefId(refId);
    result.setKey(key);
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
