package fi.uta.cs.weto.model;

public enum GradeStatus
{
  UNSPECIFIED(0, "general.header.unspecified", false),
  VALID(1, "general.header.valid", true),
  VOID(2, "general.header.void", true),
  NULL(3, "general.header.null", true),
  HIDDEN(4, "general.header.hidden", true),
  AGGREGATE(5, "general.header.aggregate", false),
  CHALLENGED(6, "general.header.challenged", true);

  private final Integer value;
  private final String property;
  private final Boolean userCanSet;

  private GradeStatus(Integer value, String property, Boolean userCanSet)
  {
    this.value = value;
    this.property = property;
    this.userCanSet = userCanSet;
  }

  static public GradeStatus getStatus(Integer value)
  {
    for(GradeStatus status : GradeStatus.values())
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

  public String getProperty()
  {
    return property;
  }

  public Boolean getUserCanSet()
  {
    return userCanSet;
  }

}
