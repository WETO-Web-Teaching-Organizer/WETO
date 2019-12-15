package fi.uta.cs.weto.actions;

import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.weto.model.Navigator;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class PollLogin extends ActionSupport
{
  @Override
  public final String execute() throws Exception
  {
    final HttpServletRequest request = ServletActionContext.getRequest();
    Navigator sessionNavigator;
    Integer sessionMasterUserId;
    try
    {
      // Try to retrieve a master user id from the navigator.
      sessionNavigator = Navigator.getInstance(request);
      sessionMasterUserId = sessionNavigator.getRealMasterUserId();
    }
    catch(NoSuchElementException e)
    {
      sessionNavigator = null;
      sessionMasterUserId = null;
    }
    if(sessionMasterUserId != null)
    {
      return SUCCESS;
    }
    else
    {
      return INPUT;
    }
  }

}
