package fi.uta.cs.weto.model;

import java.util.HashMap;
import java.util.Map;

public enum PermissionType
{
  SUBMISSION(1, "permissions.header.submission", false),
  GRADING(2, "permissions.header.grading", false),
  RESULTS(3, "permissions.header.results", false),
  VIEW(4, "permissions.header.view", false),
  REGISTER(5, "permissions.header.register", true),
  WITHDRAW(6, "permissions.header.withdraw", true),
  FORUM_TOPIC(7, "permissions.header.forumTopic", false),
  FORUM_REPLY(8, "permissions.header.forumReply", false),
  GRADE_CHALLENGE(9, "permissions.header.challengeGrade", false);

  private final Integer value;
  private final String property;
  private final boolean coursePermission;

  private PermissionType(Integer value, String property,
          boolean coursePermission)
  {
    this.value = value;
    this.property = property;
    this.coursePermission = coursePermission;
  }

  static public PermissionType getType(Integer value)
  {
    for(PermissionType type : PermissionType.values())
    {
      if(type.getValue().equals(value))
      {
        return type;
      }
    }
    return null;
  }

  static public Map<Integer, String> getTypeMap()
  {
    HashMap<Integer, String> typeMap = new HashMap<>();
    for(PermissionType type : PermissionType.values())
    {
      typeMap.put(type.getValue(), type.getProperty());
    }
    return typeMap;
  }

  public Integer getValue()
  {
    return value;
  }

  public String getProperty()
  {
    return property;
  }

  public boolean isCoursePermission()
  {
    return coursePermission;
  }

}
