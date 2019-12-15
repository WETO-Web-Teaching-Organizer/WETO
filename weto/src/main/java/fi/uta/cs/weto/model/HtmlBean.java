package fi.uta.cs.weto.model;

public class HtmlBean extends ContentElementBean
{
  private final String html;

  public HtmlBean(Integer contentElementType, String html)
  {

    super(contentElementType);
    this.html = html;
  }

  public String getHtml()
  {
    return html;
  }

}
