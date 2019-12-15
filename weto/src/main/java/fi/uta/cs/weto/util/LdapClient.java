package fi.uta.cs.weto.util;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * LDAP Session class.
 */
public class LdapClient extends ActionSupport implements AutoCloseable
{
  private static final String url = WetoUtilities.getPackageResource("ldap.url");
  private static final String basedn = WetoUtilities.getPackageResource(
          "ldap.basedn");
  private DirContext context = null;

  public void connect(String username, String password)
          throws NamingException
  {
    Hashtable<String, String> env = new Hashtable<>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, url);
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, "uid=" + username + ",ou=People,"
            + basedn);
    env.put(Context.SECURITY_CREDENTIALS, password);
    // Computer center ldap
    env.put("java.naming.ldap.version", "2");
    // for department's ldap
    //env.put("java.naming.ldap.version", "3");
    context = new InitialDirContext(env);
  }

  public Attributes getAttributes(String loginName) throws NamingException
  {
    String name = "uid=" + loginName + ",ou=people," + basedn;
    Attributes attributes = context.getAttributes(name);
    return attributes;
  }

  public void close() throws NamingException
  {
    if(context != null)
    {
      context.close();
    }
  }

}
