package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class Scoring extends BeanScoring
{
  private Properties properties = null;

  public Scoring()
  {
    properties = new Properties();
  }

  public static Scoring select1ByTaskId(Connection conn, Integer taskId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException, UnsupportedEncodingException,
                 IOException
  {
    Scoring result = new Scoring();
    result.setTaskId(taskId);
    result.select(conn);
    result.properties.load(new StringReader(result.getProperties()));
    return result;
  }

  private void updateState() throws ObjectNotValidException
  {
    try
    {
      setTimeStamp(new WetoTimeStamp().getTimeStamp());
    }
    catch(WetoTimeStampException | InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    StringWriter propertiesWriter = new StringWriter();
    try
    {
      properties.store(propertiesWriter, "");
      setProperties(propertiesWriter.toString());
    }
    catch(IOException | InvalidValueException ex)
    {
      throw new ObjectNotValidException("Error setting properties.");
    }
  }

  @Override
  public void insert(Connection conn) throws SQLException,
                                             ObjectNotValidException
  {
    updateState();
    super.insert(conn);
  }

  @Override
  public void update(Connection conn) throws SQLException,
                                             ObjectNotValidException,
                                             NoSuchItemException
  {
    updateState();
    super.update(conn);
  }

  public String getProperty(String propertyName)
  {
    return properties.getProperty(propertyName);
  }

  public String getProperty(String propertyName, String defaultValue)
  {
    return properties.getProperty(propertyName, defaultValue);
  }

  public void setProperty(String propertyName, String value)
  {
    if((value != null) && !value.isEmpty())
    {
      properties.setProperty(propertyName, value);
    }
    else
    {
      properties.remove(propertyName);
    }
  }

  public Set<String> getPropertySet()
  {
    return properties.stringPropertyNames();
  }

}
