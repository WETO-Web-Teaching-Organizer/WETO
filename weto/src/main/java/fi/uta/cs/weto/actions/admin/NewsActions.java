package fi.uta.cs.weto.actions.admin;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.weto.db.News;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoAdminAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public abstract class NewsActions
{
  public static class View extends WetoAdminAction
  {
    private ArrayList<News> news;

    @Override
    public String action() throws Exception
    {
      news = News.selectAll(getMasterConnection());
      return INPUT;
    }

    public ArrayList<News> getNews()
    {
      return news;
    }

  }

  public static class Edit extends FormBean
  {
    @Override
    public String action() throws Exception
    {
      if(getNewsId() != null)
      {
        News news = News.select1ById(getMasterConnection(), getNewsId());
        // Retrieve input data for editor.
        Integer startStamp = news.getStartDate();
        if(startStamp != null)
        {
          WetoTimeStamp tmp = new WetoTimeStamp(startStamp);
          setStartDate(tmp.getDateString());
          setStartHour(tmp.getHour());
          setStartMinute(tmp.getMinute());
        }
        Integer endStamp = news.getEndDate();
        if(endStamp != null)
        {
          WetoTimeStamp tmp = new WetoTimeStamp(endStamp);
          setEndDate(tmp.getDateString());
          setEndHour(tmp.getHour());
          setEndMinute(tmp.getMinute());
        }
        if(news.getTitle() != null)
        {
          setTitle(news.getTitle());
        }
        if(news.getText() != null)
        {
          setText(news.getText());
        }
      }
      else
      {
        WetoTimeStamp tmp = new WetoTimeStamp();
        setStartDate(tmp.getDateString());
        setStartHour(tmp.getHour());
        setStartMinute(tmp.getMinute());
      }
      return SUCCESS;
    }

  }

  public static class Update extends FormBean
  {
    @Override
    public String action() throws Exception
    {
      if(getNewsId() != null)
      {
        // Update existing news.
        News oldNews = News.select1ById(getMasterConnection(), getNewsId());
        populate(this, oldNews);
        oldNews.update(getMasterConnection());
      }
      else
      {
        // Create new news.
        News news = new News();
        populate(this, news);
        news.insert(getMasterConnection());
      }
      return SUCCESS;
    }

  }

  public static class Delete extends FormBean
  {

    @Override
    public String action() throws Exception
    {
      News news = News.select1ById(getMasterConnection(), getNewsId());
      news.delete(getMasterConnection());
      return SUCCESS;
    }

  }

  private static void populate(FormBean fb, News news) throws
          WetoTimeStampException, InvalidValueException
  {
    if(fb.getStartDate() != null)
    {
      news.setStartDate(new WetoTimeStamp(fb.getStartYear(), fb.getStartMonth(),
              fb.getStartDay(), fb.getStartHour(), fb.getStartMinute(), 0)
              .getTimeStamp());
    }
    else
    {
      news.setStartDate(null);
    }
    if(fb.getEndDate() != null)
    {
      news.setEndDate(new WetoTimeStamp(fb.getEndYear(), fb.getEndMonth(),
              fb.getEndDay(), fb.getEndHour(), fb.getEndMinute(), 0)
              .getTimeStamp());
    }
    else
    {
      news.setEndDate(null);
    }
    if(!fb.getTitle().equals(""))
    {
      news.setTitle(fb.getTitle());
    }
    news.setText(fb.getText());
  }

  private static abstract class FormBean extends WetoAdminAction
  {
    private Integer startYear;
    private Integer startMonth;
    private Integer startDay;
    private Integer startHour;
    private Integer startMinute;
    private Integer endYear;
    private Integer endMonth;
    private Integer endDay;
    private Integer endHour;
    private Integer endMinute;

    private Integer newsId;
    private String title;
    private String text;

    public String getStartDate()
    {
      String result = null;
      if((startYear != null) && (startMonth != null) && (startDay != null))
      {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ps.format("%02d.%02d.%d", startDay, startMonth, startYear);
        result = baos.toString();
      }
      return result;
    }

    public void setStartDate(String date) throws WetoActionException
    {
      if(date == null || date.equals(""))
      {
        startYear = startMonth = startDay = null;
      }
      else
      {
        try
        {
          String[] parts = date.split("\\.");
          startDay = Integer.valueOf(parts[0]);
          startMonth = Integer.valueOf(parts[1]);
          startYear = Integer.valueOf(parts[2]);
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
        {
          throw new WetoActionException(getText("error.illegalTime"));
        }
      }
    }

    public String getEndDate()
    {
      String result = null;
      if((endYear != null) && (endMonth != null) && (endDay != null))
      {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ps.format("%02d.%02d.%d", endDay, endMonth, endYear);
        result = baos.toString();
      }
      return result;
    }

    public void setEndDate(String date) throws WetoActionException
    {
      if(date == null || date.equals(""))
      {
        endYear = endMonth = endDay = null;
      }
      else
      {
        try
        {
          String[] parts = date.split("\\.");
          endDay = Integer.valueOf(parts[0]);
          endMonth = Integer.valueOf(parts[1]);
          endYear = Integer.valueOf(parts[2]);
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
        {
          throw new WetoActionException(getText("error.illegalTime"));
        }
      }
    }

    Integer getStartYear()
    {
      return startYear;
    }

    Integer getStartMonth()
    {
      return startMonth;
    }

    Integer getStartDay()
    {
      return startDay;
    }

    public Integer getStartHour()
    {
      return startHour;
    }

    public void setStartHour(Integer startHour)
    {
      this.startHour = startHour;
    }

    public Integer getStartMinute()
    {
      return startMinute;
    }

    public void setStartMinute(Integer startMinute)
    {
      this.startMinute = startMinute;
    }

    Integer getEndYear()
    {
      return endYear;
    }

    Integer getEndMonth()
    {
      return endMonth;
    }

    Integer getEndDay()
    {
      return endDay;
    }

    public Integer getEndHour()
    {
      return endHour;
    }

    public void setEndHour(Integer endHour)
    {
      this.endHour = endHour;
    }

    public Integer getEndMinute()
    {
      return endMinute;
    }

    public void setEndMinute(Integer endMinute)
    {
      this.endMinute = endMinute;
    }

    public Integer getNewsId()
    {
      return newsId;
    }

    public void setNewsId(Integer newsId)
    {
      this.newsId = newsId;
    }

    public String getTitle()
    {
      return title;
    }

    public void setTitle(String title)
    {
      this.title = title;
    }

    public String getText()
    {
      return text;
    }

    public void setText(String text)
    {

      this.text = (text != null) ? text.replaceAll("\r\n", "\n") : null;
    }

  }
}
