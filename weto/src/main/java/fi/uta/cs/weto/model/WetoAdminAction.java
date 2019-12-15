package fi.uta.cs.weto.model;

import fi.uta.cs.weto.db.Admin;

public abstract class WetoAdminAction extends WetoMasterAction
{
  @Override
  void doPrepare() throws Exception
  {
    super.doPrepare();
    if(getMasterUserId() == null || !Admin.userIsAdmin(getMasterConnection(),
            getMasterUserId()))
    {
      throw new WetoActionException(getText("accessDenied.admin"), ACCESS_DENIED);
    }
  }

}
