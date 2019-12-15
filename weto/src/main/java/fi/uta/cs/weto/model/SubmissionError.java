package fi.uta.cs.weto.model;

public enum SubmissionError
{
  COMPILATION_ERROR(0, "submissions.error.compilationError"),
  IMMEDIATEPUBLIC_TEST(1, "submissions.error.immediatePublicTest"),
  IMMEDIATEPRIVATE_TEST(2, "submissions.error.immediatePrivateTest"),
  FINALPUBLIC_TEST(3, "submissions.error.finalPublicTest"),
  FINALPRIVATE_TEST(4, "submissions.error.finalPrivateTest"),
  COMPILATION_WARNING(5, "submissions.error.compilationWarning");

  private Integer value;
  private String property;

  private SubmissionError(Integer newValue, String newProperty)
  {
    value = newValue;
    property = newProperty;
  }

  private static final SubmissionError[] errors = SubmissionError.values();

  public static SubmissionError getError(Integer value)
  {
    if((value >= 0) && (value < errors.length))
    {
      return errors[value];
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
