package fi.uta.cs.weto.model;

/**
 * Enumeration type for tag types.
 */
public enum ContentElementType
{
  HTML(0, "html", "html"),
  MULTIPLE_CHOICE(1, "quiz.header.multipleChoice", "multichoice"),
  ESSAY(2, "quiz.header.essay", "essay"),
  SURVEY(3, "quiz.header.survey", "survey"),
  PROGRAM(4, "quiz.header.program", "program");

  private final Integer value;
  private final String property;
  private final String alias;

  private ContentElementType(Integer value, String property, String alias)
  {
    this.value = value;
    this.property = property;
    this.alias = alias;
  }

  public static ContentElementType getByValue(Integer value)
  {
    for(ContentElementType ContentElementType : ContentElementType.values())
    {
      if(ContentElementType.getValue().equals(value))
      {
        return ContentElementType;
      }
    }
    return null;
  }

  public static ContentElementType getByAlias(String alias)
  {
    for(ContentElementType ContentElementType : ContentElementType.values())
    {
      if(ContentElementType.getAlias().equals(alias))
      {
        return ContentElementType;
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

  public String getAlias()
  {
    return alias;
  }

}
