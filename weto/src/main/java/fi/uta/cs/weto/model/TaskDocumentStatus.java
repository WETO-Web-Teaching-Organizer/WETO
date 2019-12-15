package fi.uta.cs.weto.model;

public enum TaskDocumentStatus
{
  PRIVATE_PUBLIC(0, "taskDocuments.header.privatePublic"),
  PRIVATE(1, "taskDocuments.header.private"),
  GROUP_PUBLIC(2, "taskDocuments.header.groupPublic"),
  GROUP(3, "taskDocuments.header.group"),
  PUBLIC(4, "taskDocuments.header.public");

  private static final int size = TaskDocumentStatus.values().length;

  private final Integer value;
  private final String property;

  private TaskDocumentStatus(Integer newValue, String newProperty)
  {
    value = newValue;
    property = newProperty;
  }

  static public TaskDocumentStatus getStatus(Integer value)
  {
    for(TaskDocumentStatus status : TaskDocumentStatus.values())
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

  public static int getSize()
  {
    return size;
  }

}
