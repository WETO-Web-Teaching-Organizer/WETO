package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class TagView extends BeanTagView
{
  private String timeString;

  /**
   * Retrieves collection tags associated to the given taggable object and tag
   * type.
   *
   * @param conn open database connection
   * @param taggedId
   * @param tagType tag type identifier
   * @return collection of tags associated with the task and type
   * @throws SQLException if the JDBC operation fails.
   */
  public static ArrayList<TagView> selectByTaggedIdAndType(Connection conn,
          Integer taggedId, Integer tagType)
          throws SQLException, WetoTimeStampException
  {
    ArrayList<TagView> tags = new ArrayList<>();
    Iterator<?> iter = TagView.selectionIterator(conn, "taggedid=" + taggedId
            + " AND type=" + tagType + " ORDER BY rank, timestamp");
    while(iter.hasNext())
    {
      TagView tag = (TagView) iter.next();
      Integer ts = tag.getTimeStamp();
      tag.timeString = (ts != null) ? new WetoTimeStamp(ts).toString() : "";
      tags.add(tag);
    }
    return tags;
  }

  public static ArrayList<TagView> selectByTaggedIdAndStatusAndType(
          Connection conn, Integer taggedId, Integer status, Integer tagType)
          throws SQLException, WetoTimeStampException
  {
    ArrayList<TagView> tags = new ArrayList<>();
    Iterator<?> iter = TagView.selectionIterator(conn, "taggedid=" + taggedId
            + " AND status=" + status + " AND type=" + tagType
            + " ORDER BY rank, timestamp");
    while(iter.hasNext())
    {
      TagView tag = (TagView) iter.next();
      Integer ts = tag.getTimeStamp();
      tag.timeString = (ts != null) ? new WetoTimeStamp(ts).toString() : "";
      tags.add(tag);
    }
    return tags;
  }

  public static TagView select1ByIdAndType(Connection conn, Integer id,
          Integer tagType)
          throws SQLException, WetoTimeStampException, NoSuchItemException
  {
    TagView result;
    SqlSelectionIterator iter = (SqlSelectionIterator) selectionIterator(conn,
            "id=" + id + " AND type=" + tagType);
    if(iter.hasNext())
    {
      result = (TagView) iter.next();
      Integer ts = result.getTimeStamp();
      result.timeString = (ts != null) ? new WetoTimeStamp(ts).toString() : "";
      iter.close();
    }
    else
    {
      throw new NoSuchItemException();
    }
    return result;
  }

  public String getTimeString()
  {
    return timeString;
  }

}
