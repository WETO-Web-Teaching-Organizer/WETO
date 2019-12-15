package fi.uta.cs.weto.util;

import java.net.URLEncoder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ShibbolethLogin
{
  private final static String infoUrl = WetoUtilities.getPackageResource(
          "shibboleth.infoPage");
  private final static String shibbolethUrl = WetoUtilities.getPackageResource(
          "shibboleth.idp");
  private final static String studentIdPrefix = WetoUtilities
          .getPackageResource("shibboleth.studentIdPrefix");

  private String loginName;
  private String email;
  private String firstName;
  private String lastName;
  private String studentNumber;
  private String errorMessage;

  public ShibbolethLogin(String user, String pass, Logger logger)
  {
    String loginHtml = "EMPTYLOGINHTML";
    String continueHtml = "EMPTYCONTINUEHTML";
    String infoHtml = "EMPTYINFOHTML";
    try(CloseableHttpClient httpClient = HttpClients.custom()
            .setRedirectStrategy(new LaxRedirectStrategy()).build())
    {
      HttpClientContext httpContext = HttpClientContext.create();
      httpContext.setCookieStore(new BasicCookieStore());
      loginHtml = WetoUtilities.getPageContent(httpClient, httpContext, infoUrl);
      Document loginPage = Jsoup.parse(loginHtml);
      Element loginForm = loginPage.getElementsByTag("form").first();
      String url = StringUtil.resolve(shibbolethUrl, loginForm.attr("action"));
      StringBuilder parSB = new StringBuilder();
      for(Element inputEl : loginForm.getElementsByTag("input"))
      {
        String name = inputEl.attr("name");
        String value = inputEl.attr("value");
        if("j_username".equals(name))
        {
          value = user;
        }
        else if("j_password".equals(name))
        {
          value = pass;
        }
        if(parSB.length() > 0)
        {
          parSB.append("&");
        }
        parSB.append(name + "=" + URLEncoder.encode(value, "UTF-8"));
      }
      parSB.append("&_eventId_proceed=Accept");
      continueHtml = WetoUtilities.submitForm(httpClient, httpContext, url,
              parSB
              .toString());
      Document continuePage = Jsoup.parse(continueHtml);
      Element continueForm = continuePage.getElementsByTag("form").first();
      parSB = new StringBuilder();
      for(Element inputEl : continueForm.getElementsByTag("input"))
      {
        String name = inputEl.attr("name");
        String value = inputEl.attr("value");
        if(parSB.length() > 0)
        {
          parSB.append("&");
        }
        parSB.append(name + "=" + URLEncoder.encode(value, "UTF-8"));
      }
      String continueUrl
                     = "https://wetodev.sis.uta.fi/Shibboleth.sso/SAML2/POST";
      infoHtml = WetoUtilities.submitForm(httpClient, httpContext, continueUrl,
              parSB
              .toString());
      Document infoPage = Jsoup.parse(infoHtml);
      boolean authenticationOk = false;
      try
      {
        String userNameString = infoPage.select("td:matchesOwn(Username)")
                .first().nextElementSibling().ownText().trim();
        if(userNameString.startsWith(user))
        {
          authenticationOk = true;
          this.loginName = userNameString;
        }
        this.firstName = infoPage.select("td:containsOwn(Given name)").first()
                .nextElementSibling().ownText().trim();
        this.lastName = infoPage.select("td:containsOwn(Surname)").first()
                .nextElementSibling().ownText().trim();
        this.email = infoPage.select("td:containsOwn(E-mail)").first()
                .nextElementSibling().ownText().trim();
        this.studentNumber = infoPage.select("td:containsOwn(" + studentIdPrefix
                + ")").last().ownText().trim().substring(studentIdPrefix
                        .length());
      }
      catch(Exception e)
      {
      }
      if(!authenticationOk)
      {
        logger.debug("Login failed for " + user);
        try
        {
          errorMessage = infoPage.getElementsByClass("form-error").first()
                  .ownText().trim();
        }
        catch(Exception e)
        {
        }
      }
    }
    catch(Exception e)
    {
      logger.debug("Login failed for " + user);
    }
  }

  public String getLoginName()
  {
    return loginName;
  }

  public String getEmail()
  {
    return email;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getStudentNumber()
  {
    return studentNumber;
  }

  public boolean isOK()
  {
    return (loginName != null) && (email != null) && (firstName != null)
            && (lastName != null);
  }

  public String getErrorMessage()
  {
    return errorMessage;
  }

}
