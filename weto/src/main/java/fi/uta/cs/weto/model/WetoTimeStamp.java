package fi.uta.cs.weto.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class WetoTimeStamp
{
  public static final int YEAR_ZERO = 1970;
  public static final int STAMP_MIN = Integer.MIN_VALUE;
  public static final int STAMP_MAX = Integer.MAX_VALUE;

  private Integer timeStamp;
  private int year;
  private int month;
  private int day;
  private int hour;
  private int minute;
  private int second;

  public WetoTimeStamp() throws WetoTimeStampException
  {
    this(new GregorianCalendar());
  }

  public WetoTimeStamp(Calendar time) throws WetoTimeStampException
  {
    year = time.get(Calendar.YEAR);
    month = 1 + time.get(Calendar.MONTH);
    day = time.get(Calendar.DAY_OF_MONTH);
    hour = time.get(Calendar.HOUR_OF_DAY);
    minute = time.get(Calendar.MINUTE);
    second = time.get(Calendar.SECOND);
    updateTimeStamp();
  }

  public WetoTimeStamp(int y, int mon, int d, int h, int min, int s) throws
          WetoTimeStampException
  {
    year = y;
    month = mon;
    day = d;
    hour = h;
    minute = min;
    second = s;
    updateTimeStamp();
  }

  public WetoTimeStamp(int timeStamp) throws
          WetoTimeStampException
  {
    this.timeStamp = timeStamp;
    updateTimeFields();
  }

  private void updateTimeStamp() throws WetoTimeStampException
  {
    long y = year - YEAR_ZERO;
    long mon = month - 1;
    long d = day - 1;
    long h = hour;
    long min = minute;
    long s = second;
    long longStamp = (y * 12 * 31 * 24 * 60 * 60) + (mon * 31 * 24 * 60 * 60)
            + (d * 24 * 60 * 60) + (h * 60 * 60) + (min * 60) + s;
    longStamp += STAMP_MIN;
    if(longStamp < STAMP_MIN || longStamp > STAMP_MAX)
    {
      throw new WetoTimeStampException("");
    }
    timeStamp = (int) longStamp;
  }

  private void updateTimeFields()
  {
    long longStamp = timeStamp;
    longStamp -= STAMP_MIN;
    second = (int) (longStamp % 60);
    longStamp = longStamp / 60;
    minute = (int) (longStamp % 60);
    longStamp = longStamp / 60;
    hour = (int) (longStamp % 24);
    longStamp = longStamp / 24;
    day = 1 + (int) (longStamp % 31);
    longStamp = longStamp / 31;
    month = 1 + (int) (longStamp % 12);
    longStamp = longStamp / 12;
    year = YEAR_ZERO + (int) longStamp;
  }

  @Override
  public String toString()
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    ps.format("%02d.%02d.%d %02d:%02d:%02d", day, month, year, hour, minute,
            second);
    return baos.toString();
  }

  public Calendar toCalendar()
  {
    return new GregorianCalendar(year, month - 1, day, hour, minute, second);
  }

  public String getDateString()
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    ps.format("%02d.%02d.%d", day, month, year);
    return baos.toString();
  }

  public static Integer dateToStamp(String date) throws WetoTimeStampException,
                                                        NumberFormatException
  {
    Integer result = null;
    if(date != null)
    {
      String[] parts = date.split("\\.");
      if(parts.length == 3)
      {
        result = new WetoTimeStamp(Integer.valueOf(parts[2]), Integer
                .valueOf(parts[1]), Integer.valueOf(parts[0]), 0, 0, 0)
                .getTimeStamp();
      }
    }
    return result;
  }

  public static WetoTimeStamp stringToStampObject(String time)
          throws WetoTimeStampException, NumberFormatException
  {
    WetoTimeStamp result = null;
    if((time != null) && !time.isEmpty())
    {
      String[] dayMon = time.split("\\.", 0);
      if(dayMon.length == 3)
      {
        String[] year = dayMon[2].split(" ", 0);
        if(year.length == 2)
        {
          String[] hourMinSec = year[1].split(":", 0);
          if(hourMinSec.length == 3)
          {
            result = new WetoTimeStamp(Integer.parseInt(year[0]), Integer
                    .parseInt(dayMon[1]), Integer.parseInt(dayMon[0]), Integer
                    .parseInt(hourMinSec[0]), Integer.parseInt(hourMinSec[1]),
                    Integer.parseInt(hourMinSec[2]));
          }
        }
        else
        {
          result = new WetoTimeStamp(Integer.parseInt(year[0]), Integer
                  .parseInt(dayMon[1]), Integer.parseInt(dayMon[0]), 0, 0, 0);
        }
      }
    }
    return result;
  }

  public static Integer stringToStamp(String time)
          throws WetoTimeStampException, NumberFormatException
  {
    Integer result = null;
    WetoTimeStamp ts = stringToStampObject(time);
    if(ts != null)
    {
      result = ts.getTimeStamp();
    }
    return result;
  }

  public static String[] limitsToStrings(WetoTimeStamp[] limits)
  {
    String[] result = new String[2];
    result[0] = result[1] = null;
    if(limits[0] != null)
    {
      String time = limits[0].toString();
      int secondsBoundary = time.lastIndexOf(':');
      result[0] = time.substring(0, secondsBoundary);
    }
    if(limits[1] != null)
    {
      String time = limits[1].toString();
      int secondsBoundary = time.lastIndexOf(':');
      result[1] = time.substring(0, secondsBoundary);
    }
    return result;
  }

  public Integer getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(int timeStamp)
  {
    this.timeStamp = timeStamp;
    updateTimeFields();
  }

  public void setTimeFields(int y, int mon, int d, int h, int min, int s) throws
          WetoTimeStampException
  {
    year = y;
    month = mon;
    day = d;
    hour = h;
    minute = min;
    second = s;
    updateTimeStamp();
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year) throws WetoTimeStampException
  {
    this.year = year;
    updateTimeStamp();
  }

  public int getMonth()
  {
    return month;
  }

  public void setMonth(int month) throws WetoTimeStampException
  {
    this.month = month;
    updateTimeStamp();
  }

  public int getDay()
  {
    return day;
  }

  public void setDay(int day) throws WetoTimeStampException
  {
    this.day = day;
    updateTimeStamp();
  }

  public int getHour()
  {
    return hour;
  }

  public void setHour(int hour) throws WetoTimeStampException
  {
    this.hour = hour;
    updateTimeStamp();
  }

  public int getMinute()
  {
    return minute;
  }

  public void setMinute(int minute) throws WetoTimeStampException
  {
    this.minute = minute;
    updateTimeStamp();
  }

  public int getSecond()
  {
    return second;
  }

  public void setSecond(int second) throws WetoTimeStampException
  {
    this.second = second;
    updateTimeStamp();
  }

}
