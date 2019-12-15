package fi.uta.cs.weto.actions;

import fi.uta.cs.weto.model.WetoMasterAction;
import org.apache.log4j.Logger;

public class Logout extends WetoMasterAction
{
  private static final Logger logger = Logger.getLogger(Logout.class);

  public Logout()
  {
    super(true);
  }

  @Override
  public String action() throws Exception
  {
    closeSession();
    return SUCCESS;
  }

}
