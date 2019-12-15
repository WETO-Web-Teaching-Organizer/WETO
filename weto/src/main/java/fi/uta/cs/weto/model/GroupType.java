package fi.uta.cs.weto.model;

public enum GroupType
{
  EXERCISE(0, "groups.header.exercise", false),
  REVIEW(1, "groups.header.review", false),
  SUBMISSION(2, "groups.header.submission", false);

  private final Integer value;
  private final String property;
  private final boolean implicit; // A group that has no name; defined by members.

  private GroupType(Integer value, String property, boolean implicit)
  {
    this.value = value;
    this.property = property;
    this.implicit = implicit;
  }

  static public GroupType getType(Integer value)
  {
    for(GroupType type : GroupType.values())
    {
      if(type.getValue().equals(value))
      {
        return type;
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

  public boolean isImplicit()
  {
    return implicit;
  }

}
