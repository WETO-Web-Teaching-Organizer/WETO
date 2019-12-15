package fi.uta.cs.weto.model;

public enum Component
{
  TAGS(31, "component.header.TAGS");

  private final int value;
  private final String property;

  private Component(int newValue, String newProperty)
  {
    value = newValue;
    property = newProperty;
  }

  public static Component getComponent(int value)
  {
    for(Component comp : Component.values())
    {
      if(value == comp.getValue())
      {
        return comp;
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

}
