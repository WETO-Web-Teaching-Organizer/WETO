package fi.uta.cs.weto.util;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class ShibbolethAttributes
{
  private final static String loginNameAttribute = WetoUtilities
          .getPackageResource("shibboleth.loginNameAttribute");
  private final static String emailAttribute = WetoUtilities.getPackageResource(
          "shibboleth.emailAttribute");
  private final static String firstNameAttribute = WetoUtilities
          .getPackageResource("shibboleth.firstNameAttribute");
  private final static String lastNameAttribute = WetoUtilities
          .getPackageResource("shibboleth.lastNameAttribute");
  private final static String studentIdAttribute = WetoUtilities
          .getPackageResource("shibboleth.studentIdAttribute");
  private final static String studentIdPrefix = WetoUtilities
          .getPackageResource("shibboleth.studentIdPrefix");

  private String loginName;
  private String email;
  private String firstName;
  private String lastName;
  private String studentNumber;

  private String encodeToUtf8(String str)
  {
    String res = str;
    if(str != null)
    {
      try
      {
        res = new String(str.getBytes("ISO-8859-1"), "UTF-8");
      }
      catch(UnsupportedEncodingException e)
      {
      }
    }
    return res;
  }

  public ShibbolethAttributes(HttpServletRequest request, Logger logger)
  {
    String loginAttr = (String) request.getAttribute(loginNameAttribute);
    this.loginName = encodeToUtf8(loginAttr);
    String emailAttr = (String) request.getAttribute(emailAttribute);
    this.email = encodeToUtf8(emailAttr);
    String fnAttr = (String) request.getAttribute(firstNameAttribute);
    this.firstName = encodeToUtf8(fnAttr);
    String lnAttr = (String) request.getAttribute(lastNameAttribute);
    this.lastName = encodeToUtf8(lnAttr);
    String studentAttr = (String) request.getAttribute(studentIdAttribute);
    this.studentNumber = (studentAttr != null) ? encodeToUtf8(studentAttr)
            .replace(studentIdPrefix, "") : null;
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

}
