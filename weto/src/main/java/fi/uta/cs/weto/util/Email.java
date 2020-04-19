package fi.uta.cs.weto.util;

import fi.uta.cs.weto.model.WetoTimeStamp;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

public class Email
{
  private static final Logger logger = Logger.getLogger(Email.class);
  private static final int bufferSize = 2048;

  private static int nextEmailsIndex = 0;
  private static int futureEmailsIndex = 1 - nextEmailsIndex;
  private static final Object[] emailMaps =
  {
    new ConcurrentHashMap<String, String[]>(),
    new ConcurrentHashMap<String, String[]>()
  };

  private static final Timer timer = new Timer(true);
  private static final int emailSendInterval = 1000 * 60 * Integer.parseInt(
          WetoUtilities.getPackageResource("email.pollInterval.minutes"));
  private static final String smtpServer = WetoUtilities.getPackageResource(
          "email.smtp.url");
  private static final String wetoEmailAddress = WetoUtilities
          .getPackageResource("email.address.weto");

  private static class EmailDaemon extends TimerTask
  {
    public void run()
    {
      ConcurrentHashMap<String, String[]> emailMap
                                                  = (ConcurrentHashMap<String, String[]>) emailMaps[nextEmailsIndex];
      for(String emailKey : emailMap.keySet())
      {
        String[] emailInfo = emailMap.get(emailKey);
        if(emailInfo != null)
        {
          boolean scheduledLater = false;
          if((emailInfo.length > 3) && (emailInfo[3] != null))
          {
            try
            {
              int now = new WetoTimeStamp().getTimeStamp();
              int scheduledTime = Integer.parseInt(emailInfo[3]);
              scheduledLater = (now < scheduledTime);
            }
            catch(Exception e)
            {
            }
          }
          if(!scheduledLater)
          {
            try
            {
              sendMail(emailInfo[0], emailInfo[1], emailInfo[2]);
            }
            catch(Exception e)
            {
              logger.debug("Sending mail to " + emailInfo[0] + " failed");
            }
            finally
            {
              emailMap.remove(emailKey);
            }
          }
        }
      }
      nextEmailsIndex = 1 - nextEmailsIndex;
      futureEmailsIndex = 1 - nextEmailsIndex;
    }

  }

  static
  {
    timer.scheduleAtFixedRate(new EmailDaemon(), 0, emailSendInterval);
  }

  public static void scheduleEmail(String loginName, String key, String email,
          String subject, String message)
  {
    ConcurrentHashMap<String, String[]> emailMap
                                                = (ConcurrentHashMap<String, String[]>) emailMaps[futureEmailsIndex];
    String emailKey = loginName + ":" + key;
    emailMap.put(emailKey, new String[]
    {
      email, subject, message
    });
  }

  public static void cancelEmail(String loginName, String key)
  {
    String emailKey = loginName + ":" + key;
    ConcurrentHashMap<String, String[]> emailMapA
                                                = (ConcurrentHashMap<String, String[]>) emailMaps[0];
    ConcurrentHashMap<String, String[]> emailMapB
                                                = (ConcurrentHashMap<String, String[]>) emailMaps[1];
    emailMapA.remove(emailKey);
    emailMapB.remove(emailKey);
  }

  public static void sendMail(String[] recipients, String subject,
          String message)
          throws MessagingException
  {
    InternetAddress[] addresses = new InternetAddress[recipients.length];
    for(int i = 0; i < recipients.length; i++)
    {
      addresses[i] = new InternetAddress(recipients[i]);
    }
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpServer);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(false);
    Message msg = new MimeMessage(session);
    InternetAddress addressFrom = new InternetAddress(wetoEmailAddress);
    msg.setFrom(addressFrom);
    msg.setRecipients(Message.RecipientType.TO, addresses);
    msg.setSubject(subject);
    msg.setContent(message, "text/plain");
    Transport.send(msg);
  }

  public static void sendMail(String recipient, String subject,
          String message)
          throws MessagingException
  {
    logger.debug("Sending mail to " + recipient);
    InternetAddress address = new InternetAddress(recipient);
    Properties props = new Properties();
    props.put("mail.smtp.host", smtpServer);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(false);
    Message msg = new MimeMessage(session);
    InternetAddress addressFrom = new InternetAddress(wetoEmailAddress);
    msg.setFrom(addressFrom);
    msg.setRecipient(Message.RecipientType.TO, address);
    msg.setSubject(subject);
    msg.setContent(message, "text/plain");
    Transport.send(msg);
  }

  // Parsing recipients for sendMail and calling it till array looped through.
  public static void massEmail(String[] recipients, String subject, String message)
          throws MessagingException
  {
    int i;
    for(i = 0; i < recipients.length; i++){
      String recipient = recipients[i];
      sendMail(recipient, subject, message);
    }
  }

}
