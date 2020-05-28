package fi.uta.cs.weto.model;

/**
 * Enumeration type for tag types.
 */
public enum TagType
{
  CSS_STYLE(0, "tag.header.note", true),
  REVIEW(1, "tag.header.review", false),
  FORUM_TOPIC(2, "tag.header.forumTopic", false),
  FEEDBACK(3, "tag.header.feedback", false),
  COMPILER_RESULT(4, "tag.header.compilerResult", false),
  QUIZ_QUESTION(5, "tag.header.quizQuestion", true),
  QUIZ_ANSWER(6, "tag.header.quizAnswer", false),
  REVIEW_INSTRUCTIONS(7, "tag.header.reviewInstructions", true),
  QUIZ_SCORE(8, "tag.header.quizScore", false),
  FORUM_MESSAGE(9, "tag.header.forumMessage", false),
  GRADE_DISCUSSION(10, "tag.header.gradeDiscussion", false),
  // Forum subscription-tag type. Used when forum is subscribed for receiving notifications when a new topic is added.
  FORUM_SUBSCRIPTION(11, "tag.header.forumSubscription", false),
  // Forum topic subscription-tag type. Used when forum topic is subscribed for receiving notifications.
  FORUM_TOPIC_SUBSCRIPTION(12, "tag.header.forumTopicSubscription", false);

  private final Integer value;
  private final String property;
  private final boolean copyWithTask;

  private TagType(Integer value, String property, boolean copyWithTask)
  {
    this.value = value;
    this.property = property;
    this.copyWithTask = copyWithTask;
  }

  public static TagType getTag(Integer value)
  {
    for(TagType tagType : TagType.values())
    {
      if(tagType.getValue().equals(value))
      {
        return tagType;
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

  public boolean isCopyWithTask()
  {
    return copyWithTask;
  }

}
