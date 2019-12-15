package fi.uta.cs.weto.model;

public enum LogEvent
{
  VIEW_MAIN(0, "log.header.viewMain"),
  VIEW_STUDENTS(1, "log.header.viewStudents"),
  VIEW_PERMISSIONS(2, "log.header.viewPermissions"),
  VIEW_GRADING(3, "log.header.viewGrading"),
  VIEW_SUBMISSIONS(4, "log.header.viewSubmissions"),
  VIEW_FORUM(5, "log.header.viewForum"),
  VIEW_GROUPS(6, "log.header.viewGroups"),
  VIEW_TASK_DOCUMENTS(7, "log.header.viewTaskDocuments"),
  VIEW_LOG(8, "log.header.viewLog"),
  VIEW_SUBMISSION(9, "log.header.viewSubmission"),
  CREATE_SUBMISSION(10, "log.header.createSubmission"),
  DELETE_SUBMISSION(11, "log.header.deleteSubmission"),
  SUBMIT_SUBMISSION(12, "log.header.submitSubmission"),
  SAVE_SUBMISSION_DOCUMENT(13, "log.header.saveSubmissionDocument"),
  DELETE_SUBMISSION_DOCUMENT(14, "log.header.deleteSubmissionDocument"),
  DOWNLOAD_DOCUMENT(15, "log.header.downloadDocument"),
  VIEW_REVIEWINSTRUCTIONS(16, "log.header.viewReviewInstructions"),
  CREATE_GRADE(17, "log.header.createGrade"),
  DELETE_GRADE(18, "log.header.deleteGrade"),
  UPDATE_GRADE(19, "log.header.updateGrade"),
  REMOVE_STUDENT(20, "log.header.removeStudent"),
  SAVE_QUIZ_ANSWER(21, "quiz.header.saveQuizAnswer"),
  UPDATE_FORUM_MESSAGE(22, "log.header.updateForumMessage"),
  VIEW_GRADE_DISCUSSION(23, "log.header.viewGradeDiscussion"),
  UPDATE_GRADE_DISCUSSION(24, "log.header.updateGradeDiscussionMessage"),
  ACCEPT_CHALLENGED_GRADE(25, "log.header.acceptChallengedGrade");

  private final Integer value;
  private final String property;

  private LogEvent(Integer newValue, String newProperty)
  {
    value = newValue;
    property = newProperty;
  }

  public static LogEvent getEvent(Integer value)
  {
    for(LogEvent event : LogEvent.values())
    {
      if(event.getValue().equals(value))
      {
        return event;
      }
    }
    return null;
  }

  public Integer getValue()
  {
    return value;
  }

  public String getProperty()
  {
    return property;
  }

}
