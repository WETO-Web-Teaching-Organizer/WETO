package fi.uta.cs.weto.model;

public enum TaskStatus
{
  HIDDEN(0, "general.header.hidden"),
  QUIZ(1, "general.header.quiz"),
  AUTOGRADED(2, "general.header.autoGraded"),
  NO_INLINE(3, "NO_INLINE"),
  REVIEWINSTRUCTIONS(4, "general.header.reviewInstructions"),
  PUBLIC(5, "general.header.public");

  private final Integer value;
  private final String property;

  private TaskStatus(Integer newValue, String newProperty)
  {
    value = newValue;
    property = newProperty;
  }

  static public TaskStatus getStatus(Integer value)
  {
    for(TaskStatus status : TaskStatus.values())
    {
      if(status.getValue().equals(value))
      {
        return status;
      }
    }
    return null;
  }

  public Integer getValue()
  {
    return value;
  }

  public int getBit()
  {
    return (1 << value);
  }

  public String getProperty()
  {
    return property;
  }

}
