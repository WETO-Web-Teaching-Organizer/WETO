package fi.uta.cs.weto.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class DbTransactionContext
{
  private final static Logger logger = Logger.getLogger(
          DbTransactionContext.class);
  private final static String ATTRIBUTE_NAME = "dbSession";

  private List<Connection> reservedConnections = new ArrayList<>();

  public static DbTransactionContext getInstance(HttpServletRequest request)
  {
    if(request.getAttribute(ATTRIBUTE_NAME) == null)
    {
      request.setAttribute(ATTRIBUTE_NAME, new DbTransactionContext());
    }

    return (DbTransactionContext) request.getAttribute(ATTRIBUTE_NAME);
  }

  public Connection getConnection(String name)
  {
    DbConnectionManager manager = DbConnectionManager.getInstance();
    Connection connection = manager.getConnection(name);
    reservedConnections.add(connection);
    return connection;
  }

  public void commitAll()
  {
    DbConnectionManager manager = DbConnectionManager.getInstance();

    for(Connection connection : reservedConnections)
    {
      try
      {
        connection.commit();
        //logger.debug("commitAll: committed");
      }
      catch(SQLException e1)
      {
        try
        {
          logger.warn("commitAll: commit failed");
          connection.rollback();
        }
        catch(SQLException e2)
        {
          logger.warn("commitAll: rollback failed");
        }
      }
      finally
      {
        manager.freeConnection(connection);
      }
    }

    reservedConnections.clear();
  }

  public void cancelAll()
  {
    DbConnectionManager manager = DbConnectionManager.getInstance();

    for(Connection connection : reservedConnections)
    {
      try
      {
        connection.rollback();
        //logger.debug("cancelAll: rollbacked");
      }
      catch(SQLException e2)
      {
        logger.warn("cancelAll: rollback failed");
      }
      finally
      {
        manager.freeConnection(connection);
      }
    }

    reservedConnections.clear();
  }

}
