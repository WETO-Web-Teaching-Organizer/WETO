package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class SubmissionDocument extends BeanSubmissionDocument
{

  /**
   * Retrieve submission document links.
   *
   * @param conn open database connection
   * @param submissionId submission identifier
   * @return submission document links
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<SubmissionDocument> selectBySubmissionId(
          Connection conn, Integer submissionId)
          throws SQLException
  {
    ArrayList<SubmissionDocument> result = new ArrayList<>();
    Iterator<?> iter = selectionIterator(conn, "submissionid=" + submissionId);
    while(iter.hasNext())
    {
      result.add((SubmissionDocument) iter.next());
    }
    return result;
  }

  /**
   * Retrieve document link from database.
   *
   * @param conn database connection
   * @param documentId id for the document
   * @return submission document
   * @throws SQLException if the JDBC operation fails.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   */
  public static SubmissionDocument select1ByDocumentId(Connection conn,
          Integer documentId)
          throws NoSuchItemException, SQLException
  {
    SubmissionDocument result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "documentid=" + documentId);
    if(iter.hasNext())
    {
      result = (SubmissionDocument) iter.next();
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public static SubmissionDocument select1BySubmissionIdAndDocumentId(
          Connection conn,
          Integer submissionId, Integer documentId)
          throws NoSuchItemException, SQLException
  {
    SubmissionDocument result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "submissionid=" + submissionId + " AND documentid=" + documentId);
    if(iter.hasNext())
    {
      result = (SubmissionDocument) iter.next();
      iter.close();
    }
    else
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

}
