package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.zip.GZIPInputStream;
import javax.xml.bind.DatatypeConverter;

public class DownloadFeedback extends WetoCourseAction
{
  private Integer tagId;
  private InputStream documentStream;
  private int contentLength;
  private final int bufferSize = 8192;

  public DownloadFeedback()
  {
    super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Integer taskId = getTaskId();
    Connection conn = getCourseConnection();
    Integer userId = getCourseUserId();
    Tag feedbackTag = Tag.select1ById(conn, tagId);
    Integer submitterId = feedbackTag.getAuthorId();
    boolean compilerFeedback = TagType.COMPILER_RESULT.getValue().equals(
            feedbackTag.getType());
    boolean gradingFeedback = TagType.FEEDBACK.getValue().equals(feedbackTag
            .getType());
    Integer feedbackTaskId = null;
    if(compilerFeedback)
    {
      feedbackTaskId = feedbackTag.getTaggedId();
    }
    else if(gradingFeedback)
    {
      if(!taskId.equals(feedbackTag.getTaggedId()))
      {
        feedbackTaskId = Submission.select1ById(conn, feedbackTag.getTaggedId())
                .getTaskId();
      }
      else
      {
        feedbackTaskId = feedbackTag.getTaggedId();
      }
    }
    if(!taskId.equals(feedbackTaskId) || !haveViewRights(Tab.SUBMISSIONS
            .getBit(), userId.equals(submitterId), true))
    {
      throw new WetoActionException(getText("general.error.accessDenied"));
    }
    documentStream = new GZIPInputStream(new ByteArrayInputStream(
            DatatypeConverter.parseBase64Binary(feedbackTag.getText())),
            bufferSize);
    contentLength = feedbackTag.getStatus();
    return SUCCESS;
  }

  public Integer getTagId()
  {
    return tagId;
  }

  public void setTagId(Integer tagId)
  {
    this.tagId = tagId;
  }

  public InputStream getDocumentStream()
  {
    return documentStream;
  }

  public int getContentLength()
  {
    return contentLength;
  }

  public int getBufferSize()
  {
    return bufferSize;
  }

}
