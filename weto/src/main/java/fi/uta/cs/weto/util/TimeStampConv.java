package fi.uta.cs.weto.util;

import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeStampConv
{
  public static void main(String[] args)
          throws WetoTimeStampException
  {
    for(int i = 0; i < args.length; ++i)
    {
      try
      {
        Integer timeStamp = Integer.parseInt(args[i]);
        System.out.println("Timestamp " + args[i] + " corresponds to: "
                + new WetoTimeStamp(timeStamp).toString());
      }
      catch(NumberFormatException e)
      {
        DateFormat fullFormat = new SimpleDateFormat("dd.MM.yyyy'T'H:m:s");
        try
        {
          Calendar fullTime = new GregorianCalendar();
          fullTime.setTime(fullFormat.parse(args[i]));
          System.out.println("Time " + args[i] + " corresponds to: "
                  + new WetoTimeStamp(fullTime).getTimeStamp());
        }
        catch(ParseException e2)
        {
          DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
          try
          {
            Calendar date = new GregorianCalendar();
            date.setTime(dateFormat.parse(args[i]));
            System.out.println("Time " + args[i] + " corresponds to: "
                    + new WetoTimeStamp(date).getTimeStamp());
          }
          catch(ParseException e3)
          {
            System.out.println("Bad input: " + args[i]);
          }
        }
      }
    }
  }

}
