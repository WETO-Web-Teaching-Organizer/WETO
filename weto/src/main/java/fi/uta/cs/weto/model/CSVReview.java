package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.util.WetoCsvReader;
import fi.uta.cs.weto.util.WetoCsvWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CSVReview
{
  // 'texts' contains actual reviews.
  private ArrayList<String> texts;
  // 'secondaryTexts' contains other user's (e.g. teacher's) comments regarding a review.

  // Full definition: secondaryTexts are reviews that are written by users that are not the
  // owners of the related grade. For example if a teacher comments on a review, the comment becomes a
  // secondary text. If teacher takes ownership of the Grade, the previously primary review
  // becomes secondary, since the teacher is now marked as the reviewer in the Grade.
  private ArrayList<String> secondaryTexts;
  private ArrayList<String> marks;
  // 'secondaryMarks' works just like secondaryTexts but for marks.
  private ArrayList<String> secondaryMarks;
  private int id;
  private int authorId;

  public CSVReview()
  {
    this.texts = new ArrayList<>();
    this.marks = new ArrayList<>();
    this.secondaryTexts = new ArrayList<>();
    this.secondaryMarks = new ArrayList<>();
  }

  public CSVReview(Tag reviewTag)
  {
    this();
    this.id = reviewTag.getId();
    this.authorId = reviewTag.getAuthorId();
    this.readCSV(reviewTag.getText(), this.texts, this.marks);
  }

  private String createCSV() throws IOException
  {
    StringWriter strwriter = new StringWriter();
    WetoCsvWriter csvwriter = new WetoCsvWriter(strwriter);
    ArrayList<String> row = new ArrayList<>();
    for(int i = 0; i < texts.size(); ++i)
    {
      String text = texts.get(i);
      row.add(text);
      String mark = null;
      if(i < marks.size())
      {
        mark = marks.get(i);
      }
      row.add(mark);
    }
    csvwriter.writeStrings(row);
    return strwriter.toString();
  }

  public static void readCSV(String CSV, ArrayList<String> texts,
          ArrayList<String> marks)
  {
    if(CSV == null || CSV.length() == 0)
    {
      return;
    }
    try
    {
      StringReader streader = new StringReader(CSV);
      WetoCsvReader csvreader = new WetoCsvReader(streader);
      List<String> CSVValues = csvreader.read();
      for(int i = 0; i < CSVValues.size(); ++i)
      {
        // Text are even elements
        if(i % 2 == 0)
        {
          texts.add(CSVValues.get(i));
        }
        // Marks are odd values
        else
        {
          marks.add(CSVValues.get(i));
        }
      }
    }
    catch(IOException e)
    {
    }
  }

  /**
   * Reads the review and prepares it for use
   *
   * @param conn Database Connection used for reading
   * @throws SQLException
   * @throws IOException
   */
  public static CSVReview read(Connection conn, int id)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 ObjectNotValidException
  {
    Tag reviewTag;
    CSVReview result = new CSVReview();
    reviewTag = Tag.select1ById(conn, id);
    String CSV = reviewTag.getText();
    result.readCSV(CSV, result.texts, result.marks);
    result.setId(id);
    return result;
  }

  public static CSVReview read(Tag reviewTag)
  {
    CSVReview result = new CSVReview();
    String CSV = reviewTag.getText();
    result.readCSV(CSV, result.texts, result.marks);
    result.setId(reviewTag.getId());
    return result;
  }

  public static CSVReview read1ForGradeAndAuthor(Connection conn,
          Grade relatedGrade, int authorId) throws SQLException, IOException,
                                                   NoSuchItemException
  {
    CSVReview result = new CSVReview();
    try
    {
      Tag reviewTag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
              relatedGrade.getId(), 0, authorId, TagType.REVIEW.getValue());
      result.id = reviewTag.getId();
      result.authorId = reviewTag.getAuthorId();
      // If Grade owner is the writer of review Tag, it's a primary text.
      if(reviewTag.getAuthorId().equals(relatedGrade.getReviewerId()))
      {
        result.readCSV(reviewTag.getText(), result.texts, result.marks);
      }
      else
      {
        result.readCSV(reviewTag.getText(), result.secondaryTexts,
                result.secondaryMarks);
      }
    }
    // In some cases, there are no review Tag for Grades and this is not necessarily an error
    catch(NoSuchItemException e)
    {
      result = null;
    }
    return result;
  }

  public static CSVReview read1ForGradeAndSubmissionGroup(Connection conn,
          Grade relatedGrade, ArrayList<Integer> submissionMemberIds)
          throws SQLException, IOException, NoSuchItemException
  {
    CSVReview result = null;
    ArrayList<Tag> reviewTags = Tag.selectByTaggedIdAndType(conn, relatedGrade
            .getId(), TagType.REVIEW.getValue());
    if(!reviewTags.isEmpty())
    {
      for(Tag reviewTag : reviewTags)
      {
        if(submissionMemberIds.contains(reviewTag.getAuthorId()))
        {
          result = new CSVReview();
          result.id = reviewTag.getId();
          result.authorId = reviewTag.getAuthorId();
          // If Grade owner is the writer of review Tag, it's a primary text.
          result.readCSV(reviewTag.getText(), result.texts, result.marks);
          break;
        }
      }
    }
    return result;
  }

  /**
   * Reads all review Tags related to a Grade and constructs a single CSVReview
   * from them. Useful when there's a need to associate secondary comments (e.g.
   * teacher's remarks on a peer review) to the CSVReview object.
   *
   * @param conn Connection object for using database.
   * @param relatedGrade Related Grade object.
   * @return
   * @throws SQLException
   * @throws IOException
   */
  public static CSVReview readAllForGrade(Connection conn, Grade relatedGrade)
          throws SQLException, IOException
  {
    CSVReview result = null;
    ArrayList<Tag> reviewTags = Tag.selectByTaggedIdAndType(conn, relatedGrade
            .getId(), TagType.REVIEW.getValue());
    if(!reviewTags.isEmpty())
    {
      result = new CSVReview();
      for(Tag reviewTag : reviewTags)
      {
        if(reviewTag.getAuthorId().equals(relatedGrade.getReviewerId()))
        {
          result.readCSV(reviewTag.getText(), result.texts, result.marks);
        }
        else
        {
          result.readCSV(reviewTag.getText(), result.secondaryTexts,
                  result.secondaryMarks);
        }
      }
    }
    return result;
  }

  /**
   * Prepares a review for saving and updates it to database.
   *
   * @param conn Connection object for using database.
   * @param id Related tag id.
   * @throws SQLException
   * @throws InvalidValueException
   * @throws ObjectNotValidException
   * @throws NoSuchItemException
   * @throws IOException
   * @throws NullPointerException
   */
  public void update(Connection conn, int id)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, NullPointerException
  {
    Tag reviewTag = Tag.select1ById(conn, id);
    reviewTag.setText(createCSV());
    reviewTag.update(conn);
  }

  public void update(Connection conn, Tag reviewTag)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, NullPointerException
  {
    reviewTag.setText(createCSV());
    reviewTag.update(conn);
  }

  /**
   * Overload with no separately defined Tag id.
   *
   * @param conn Connection object for using database.
   * @throws SQLException
   * @throws InvalidValueException
   * @throws ObjectNotValidException
   * @throws NoSuchItemException
   * @throws IOException
   * @throws NullPointerException
   */
  public void update(Connection conn)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, NullPointerException
  {
    Tag reviewTag = Tag.select1ById(conn, this.id);
    reviewTag.setText(createCSV());
    reviewTag.update(conn);
  }

  public Boolean getEmpty()
  {
    if(this.texts == null)
    {
      return true;
    }
    else
    {
      for(String text : this.texts)
      {
        if(text.length() >= 0)
        {
          return false;
        }
      }

      return true;
    }
  }

  public Boolean getSecondaryEmpty()
  {
    if(this.secondaryTexts == null)
    {
      return true;
    }
    else
    {
      for(String text : this.secondaryTexts)
      {
        if(text.length() >= 0)
        {
          return false;
        }
      }

      return true;
    }
  }

  /* Returns all texts in pretty format */
  public String getAllTexts()
  {
    StringBuilder strbuilder = new StringBuilder();
    for(String text : this.texts)
    {
      strbuilder.append(text);
      strbuilder.append(" ");
    }
    for(String text : this.secondaryTexts)
    {
      strbuilder.append(text);
      strbuilder.append(" ");
    }
    return strbuilder.toString();
  }

  public ArrayList<String> getTexts()
  {
    return texts;
  }

  public ArrayList<String> getSecondaryTexts()
  {
    return secondaryTexts;
  }

  public void setTexts(ArrayList<String> texts)
  {
    this.texts = texts;
  }

  public ArrayList<String> getMarks()
  {
    return marks;
  }

  public ArrayList<String> getSecondaryMarks()
  {
    return secondaryMarks;
  }

  public void setMarks(ArrayList<String> marks)
  {
    this.marks = marks;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public void setAuthorId(int authorId)
  {
    this.authorId = authorId;
  }

  public int getAuthorId()
  {
    return authorId;
  }

}
