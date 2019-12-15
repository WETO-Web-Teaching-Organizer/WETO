package fi.uta.cs.weto.model;

public enum PropertyType
{
  PENDING_STUDENTS(0),
  UPDATE_NAVIGATION_TREE(1),
  PERMITTED_STUDENTS(2),
  PERMITTED_GROUPS(3);

  private final Integer value;

  private PropertyType(Integer value)
  {
    this.value = value;
  }

  static public PropertyType getType(Integer value)
  {
    for(PropertyType type : PropertyType.values())
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

}
