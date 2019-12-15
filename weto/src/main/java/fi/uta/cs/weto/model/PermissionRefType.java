package fi.uta.cs.weto.model;

public enum PermissionRefType
{
  USER(0, "general.header.user"),
  CLUSTER(1, "general.header.cluster"),
  GROUP(2, "general.header.group");

  private final Integer value;
  private final String property;

  private PermissionRefType(Integer value, String property)
  {
    this.value = value;
    this.property = property;
  }

  static public PermissionRefType getType(Integer value)
  {
    for(PermissionRefType type : PermissionRefType.values())
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

}
