package fi.uta.cs.weto.model;

public enum ClusterType
{
  STUDENTS(0, "clusterType.header.Students"),
  ASSISTANTS(100, "clusterType.header.Assistants"),
  TEACHERS(1000, "clusterType.header.Teachers");

  private final Integer value;
  private final String property;

  private ClusterType(Integer value, String property)
  {
    this.value = value;
    this.property = property;
  }

  static public ClusterType getType(Integer value)
  {
    for(ClusterType type : ClusterType.values())
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
