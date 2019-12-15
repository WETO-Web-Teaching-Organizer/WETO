package fi.uta.cs.weto.model;

public abstract class ContentElementBean
{
  private final Integer contentElementType;

  public ContentElementBean(Integer contentElementType)
  {
    this.contentElementType = contentElementType;
  }

  public Integer getContentElementType()
  {
    return contentElementType;
  }

}
