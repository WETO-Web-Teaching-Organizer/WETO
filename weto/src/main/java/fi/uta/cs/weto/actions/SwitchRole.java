package fi.uta.cs.weto.actions;

import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;

public class SwitchRole extends WetoCourseAction
{
  public SwitchRole()
  {
    super(Tab.MAIN.getBit(), 0, 0, 0);

  }

  @Override
  public String action() throws Exception
  {
    if(!getNavigator().switchRole(getRequest()))
    {
      throw new WetoActionException(getText("accessDenied.teacher"));
    }
    return SUCCESS;
  }

}
