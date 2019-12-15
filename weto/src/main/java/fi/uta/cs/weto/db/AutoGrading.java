package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

public class AutoGrading extends BeanAutoGrading
{
  /**
   * @param conn
   * @param taskId
   * @return
   * @throws InvalidValueException
   * @throws NoSuchItemException
   * @throws SQLException
   * @throws ObjectNotValidException
   */
  public static AutoGrading select1ByTaskId(Connection conn, Integer taskId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    AutoGrading result = new AutoGrading();
    result.setTaskId(taskId);
    result.select(conn);
    return result;
  }

  public static void deleteByTaskId(Connection conn, Integer taskId)
          throws InvalidValueException, NoSuchItemException, SQLException,
                 TooManyItemsException, ObjectNotValidException
  {
    Iterator<?> iter = selectionIterator(conn, "taskid=" + taskId);
    while(iter.hasNext())
    {
      AutoGrading ag = (AutoGrading) iter.next();
      Integer testDocId = ag.getTestDocId();
      ag.delete(conn);
      if(testDocId != null)
      {
        DocumentModel
                .deleteDocument(conn, Document.select1ById(conn, testDocId));
      }
    }
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
