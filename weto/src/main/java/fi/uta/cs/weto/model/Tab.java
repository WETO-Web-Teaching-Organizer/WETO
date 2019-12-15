package fi.uta.cs.weto.model;

public enum Tab
{
  MAIN(0, "tab.header.main", LogEvent.VIEW_MAIN, true),
  STUDENTS(1, "tab.header.students", LogEvent.VIEW_STUDENTS, true),
  PERMISSIONS(2, "tab.header.permissions", LogEvent.VIEW_PERMISSIONS, false),
  GRADING(3, "tab.header.grading", LogEvent.VIEW_GRADING, true),
  SUBMISSIONS(4, "tab.header.submissions", LogEvent.VIEW_SUBMISSIONS, true),
  FORUM(5, "tab.header.forum", LogEvent.VIEW_FORUM, false),
  GROUPS(6, "tab.header.groups", LogEvent.VIEW_GROUPS, false),
  TASK_DOCUMENTS(7, "tab.header.taskDocuments", LogEvent.VIEW_TASK_DOCUMENTS,
          false),
  LOG(8, "tab.header.log", LogEvent.VIEW_LOG, false);
  
  private final int value;
  private final String property;
  private final LogEvent viewEvent;
  private final boolean hasToolMenu;

  private Tab(int value, String property, LogEvent viewEvent,
          boolean hasToolMenu)
  {
    this.value = value;
    this.property = property;
    this.viewEvent = viewEvent;
    this.hasToolMenu = hasToolMenu;
  }

  public static Tab getTab(int value)
  {
    for(Tab tab : Tab.values())
    {
      if(value == tab.getValue())
      {
        return tab;
      }
    }
    return null;
  }

  public int getValue()
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

  public LogEvent getViewEvent()
  {
    return viewEvent;
  }

  public boolean isHasToolMenu()
  {
    return hasToolMenu;
  }

}
