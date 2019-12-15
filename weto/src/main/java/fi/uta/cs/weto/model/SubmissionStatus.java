package fi.uta.cs.weto.model;

public enum SubmissionStatus
{
  NOT_SUBMITTED(0, "submission.header.notSubmitted"),
  PROCESSING(1, "submission.header.processing"),
  ACCEPTED(2, "submission.header.accepted"),
  NOT_ACCEPTED(3, "submission.header.notAccepted"),
  QUIZ_SUBMISSION(4, "submission.header.quizSubmission");

  private final Integer value;
  private final String property;

  private SubmissionStatus(Integer newValue, String newProperty)
  {
    value = newValue;
    property = newProperty;
  }

  private static final SubmissionStatus[] statuses = SubmissionStatus.values();

  public static SubmissionStatus getStatus(Integer value)
  {
    if((value >= 0) && (value < statuses.length))
    {
      return statuses[value];
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
