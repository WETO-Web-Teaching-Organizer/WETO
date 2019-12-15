package fi.uta.cs.weto.model;

/* A simple data storage for a single review instruction */
public class InstructionBean
{
  private String text;
  private String name;
  private float minPoints;
  private float maxPoints;
  private int rank;
  private int id;

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public float getMinPoints()
  {
    return minPoints;
  }

  public void setMinPoints(float minPoints)
  {
    this.minPoints = minPoints;
  }

  public float getMaxPoints()
  {
    return maxPoints;
  }

  public void setMaxPoints(float maxPoints)
  {
    this.maxPoints = maxPoints;
  }

  public int getRank()
  {
    return rank;
  }

  public void setRank(int rank)
  {
    this.rank = rank;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

}
