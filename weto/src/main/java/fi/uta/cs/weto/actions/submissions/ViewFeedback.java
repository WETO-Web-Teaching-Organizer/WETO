package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;

public class ViewFeedback extends WetoCourseAction
{
  private Integer tagId;
  private Integer submissionId;
  private String feedback;
  private boolean compilerFeedback;

  public ViewFeedback()
  {
    super(Tab.MAIN.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Integer taskId = getTaskId();
    Connection conn = getCourseConnection();
    Integer userId = getCourseUserId();
    Tag feedbackTag = Tag.select1ById(conn, tagId);
    Integer submitterId = feedbackTag.getAuthorId();
    compilerFeedback = TagType.COMPILER_RESULT.getValue().equals(feedbackTag
            .getType());
    boolean gradingFeedback = TagType.FEEDBACK.getValue().equals(feedbackTag
            .getType());
    Integer feedbackTaskId = null;
    if(compilerFeedback)
    {
      feedbackTaskId = feedbackTag.getTaggedId();
    }
    else if(gradingFeedback)
    {
      if(submissionId != null)
      {
        if(submissionId.equals(feedbackTag.getTaggedId()))
        {
          feedbackTaskId = Submission.select1ById(conn, feedbackTag
                  .getTaggedId()).getTaskId();
        }
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
    feedback = WetoUtilities.gzippedBase64ToString(feedbackTag.getText());
    return SUCCESS;
  }

  public void setTagId(Integer tagId)
  {
    this.tagId = tagId;
  }

  public void setSubmissionId(Integer submissionId)
  {
    this.submissionId = submissionId;
  }

  public String getFeedback()
  {
    return feedback;
  }

  public boolean isCompilerFeedback()
  {
    return compilerFeedback;
  }

}
