package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Document extends BeanDocument
{
  /**
   * Retrieve document from database.
   *
   * @param conn database connection
   * @param documentId id for the document
   * @return document
   * @throws InvalidValueException
   * @see fi.uta.cs.weto.db.BeanDocument#setDid(java.lang.Integer)
   * @throws SQLException
   * @see fi.uta.cs.weto.db.DbDocument#select(Connection)
   * @throws NoSuchItemException
   * @see fi.uta.cs.weto.db.DbDocument#select(Connection)
   */
  public static Document select1ById(Connection conn, Integer documentId)
          throws InvalidValueException, NoSuchItemException, SQLException
  {
    Document document = new Document();
    document.setId(documentId);
    document.select(conn);
    return document;
  }

  public static ArrayList<Document> selectBySubmissionId(Connection conn,
          Integer submissionId)
          throws SQLException
  {
    ArrayList<Document> result = new ArrayList<>();
    PreparedStatement ps = conn.prepareStatement("SELECT document.* "
            + "FROM document, submissiondocument "
            + "WHERE document.id=submissiondocument.documentid "
            + "AND submissiondocument.submissionid=" + submissionId);
    Iterator<?> iter = new SqlSelectionIterator(ps, Document.class);
    while(iter.hasNext())
    {
      result.add((Document) iter.next());
    }
    // Sort the document list according to their file names.
    Collections.sort(result, new Comparator<Document>()
    {
      @Override
      public int compare(Document o1, Document o2)
      {
        int result = o1.getFileName().compareTo(o2.getFileName());
        return (result != 0) ? result : o1.getTimeStamp().compareTo(o2
                .getTimeStamp());
      }

    });
    return result;
  }

  public static ArrayList<Document> selectBySubmissionIdAndFileName(
          Connection conn, Integer submissionId, String fileName)
          throws SQLException
  {
    ArrayList<Document> result = new ArrayList<>();
    PreparedStatement ps = conn.prepareStatement("SELECT document.* "
            + "FROM document, submissiondocument "
            + "WHERE document.id=submissiondocument.documentid "
            + "AND submissiondocument.submissionid=" + submissionId);
    Iterator<?> iter = new SqlSelectionIterator(ps, Document.class);
    while(iter.hasNext())
    {
      Document document = (Document) iter.next();
      if(document.getFileName().equals(fileName.replace(",", "_")))
      {
        result.add(document);
      }
    }
    return result;
  }

  public static ArrayList<Document> selectByTaskIdAndFileName(Connection conn,
          Integer taskId, String fileName)
          throws SQLException
  {
    ArrayList<Document> result = new ArrayList<>();
    PreparedStatement ps = conn.prepareStatement("SELECT document.* "
            + "FROM document, taskdocument "
            + "WHERE document.id=taskdocument.documentid "
            + "AND taskdocument.taskid=" + taskId);
    Iterator<?> iter = new SqlSelectionIterator(ps, Document.class);
    while(iter.hasNext())
    {
      Document document = (Document) iter.next();
      if(document.getFileName().equals(fileName.replace(",", "_")))
      {
        result.add(document);
      }
    }
    return result;
  }

  /**
   * Inserts this object to the database table Document.
   *
   * @param conn
   * @throws java.sql.SQLException
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  @Override
  public void insert(Connection conn)
          throws SQLException, ObjectNotValidException
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
  public void update(Connection con)
          throws SQLException, ObjectNotValidException, NoSuchItemException
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

  public void update(Connection con, int timeStamp)
          throws SQLException, ObjectNotValidException, NoSuchItemException
  {
    try
    {
      setTimeStamp(timeStamp);
    }
    catch(InvalidValueException e)
    {
      throw new ObjectNotValidException("Error setting time stamp.");
    }
    super.update(con);
  }

  public String getTimeStampString() throws WetoTimeStampException
  {
    return new WetoTimeStamp(getTimeStamp()).toString();
  }

}
