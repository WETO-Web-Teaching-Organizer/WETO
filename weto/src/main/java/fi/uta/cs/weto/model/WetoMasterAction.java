package fi.uta.cs.weto.model;

import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.actions.Login;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.util.DbTransactionContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public abstract class WetoMasterAction extends ActionSupport
{
  public static final String ACCESS_DENIED = "masterAccessDenied";

  private Connection masterConnection;
  private DbTransactionContext dbSession;
  private HttpServletRequest request;
  private Navigator navigator;
  private Integer masterUserId;
  private boolean committed;

  private final boolean skipLoginCheck;

  public WetoMasterAction()
  {
    skipLoginCheck = false;
  }

  public WetoMasterAction(boolean skipLoginCheck)
  {
    this.skipLoginCheck = skipLoginCheck;
  }

  public abstract String action() throws Exception;

  void doPrepare() throws Exception
  {
    committed = false;
    request = ServletActionContext.getRequest();
    dbSession = DbTransactionContext.getInstance(request);
    Navigator sessionNavigator;
    Integer sessionMasterUserId;
    masterConnection = dbSession.getConnection("master");
    try
    {
      // Try to retrieve a master user id from the navigator.
      sessionNavigator = Navigator.getOrCreateMasterInstance(request,
              masterConnection);
      sessionMasterUserId = sessionNavigator.getRealMasterUserId();
    }
    catch(FailedLoginException e)
    {
      sessionNavigator = null;
      sessionMasterUserId = null;
    }
    if(!skipLoginCheck)
    {
      Login.checkLogin(this, request, sessionNavigator, sessionMasterUserId);
    }
    // If the last page visited by the user was a course page (e.g. in
    // another tab/window), then navigator holds that course's information.
    if((sessionNavigator != null) && (sessionMasterUserId != null)
            && (sessionNavigator.getDbId() != DatabasePool.MASTER_ID))
    {
      // Replace the course navigator with one for master database.
      sessionNavigator = Navigator.createMasterInstance(request,
              masterConnection, sessionMasterUserId, sessionNavigator
              .getDatabases(), DatabasePool.MASTER_ID);
    }
    masterUserId = sessionMasterUserId;
    navigator = sessionNavigator;
  }

  @Override
  public final String execute() throws Exception
  {
    try
    {
      try
      {
        doPrepare();
        String result = action();
        commit();
        return result;
      }
      catch(WetoActionException e)
      {
        String msg = e.getMessage();
        if(msg != null && !msg.isEmpty())
        {
          addActionError(msg);
        }
        return e.getResult();
      }
      catch(Exception e)
      {
        addActionError(e.getMessage());
        return ERROR;
      }
      finally
      {
        close();
      }
    }
    catch(Exception e)
    {
      addActionError(getText("general.error.system"));
      return ERROR;
    }
  }

  final void commit()
  {
    if(dbSession != null && !committed)
    {
      dbSession.commitAll();
      committed = true;
    }
  }

  final void close()
  {
    if(dbSession != null && !committed)
    {
      dbSession.cancelAll();
      committed = true;
    }
  }

  public final void createMasterNavigator(Connection masterConn,
          Integer masterUserId, HashMap<Integer, String> databases, Integer dbId)
          throws SQLException, NoSuchItemException, InvalidValueException,
                 WetoTimeStampException
  {
    Navigator.createMasterInstance(request, masterConn, masterUserId, databases,
            dbId);
  }

  public final void closeSession()
  {
    Navigator.reset(request);
    request.getSession().invalidate();
  }

  public final Connection getConnection(String database)
  {
    return dbSession.getConnection(database);
  }

  public final Connection getConnection(Integer dbId)
  {
    return dbSession.getConnection(navigator.getDatabase(dbId));
  }

  public final Connection getMasterConnection()
  {
    return masterConnection;
  }

  public final HttpServletRequest getRequest()
  {
    return request;
  }

  public final Navigator getNavigator()
  {
    return navigator;
  }

  public final Integer getMasterUserId()
  {
    return masterUserId;
  }

}
