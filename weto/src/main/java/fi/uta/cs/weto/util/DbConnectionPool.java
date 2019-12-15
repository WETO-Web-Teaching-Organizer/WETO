/*
 * Copyright (c) 1998 by Gefion software.
 *
 * Permission to use, copy, and distribute this software for
 * NON-COMMERCIAL purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 *
 */
package fi.uta.cs.weto.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This class represents a connection pool. It creates new connections on
 * demand, up to a max number if specified. It also makes sure a connection is
 * still open before it is returned to a client.
 */
public class DbConnectionPool
{
  private final static Logger logger = Logger.getLogger(DbConnectionPool.class);

  /**
   * Name of the connection pool
   */
  private String name;

  /**
   * Database URL
   */
  private String url;

  /**
   * Database username
   */
  private String username;

  /**
   * Database password
   */
  private String password;

  /**
   * Connections available for use
   */
  private List<Connection> freeConnections = new ArrayList<>();

  /**
   * Connections reserved.
   */
  private List<Connection> reservedConnections = new ArrayList<>();

  /**
   * Maximum number of connections. Set to zero if there is no limit.
   */
  private int connectionsMax;

  /**
   * Creates new connection pool.
   *
   * @param name The pool name
   * @param url The JDBC URL for the database
   * @param username The database user, or null
   * @param password The database user password, or null
   * @param connectionsMax The maximal number of connections, or 0 for no limit
   */
  public DbConnectionPool(String name, String url, String username,
          String password, int connectionsMax)
  {
    this.name = name;
    this.url = url;
    this.username = username;
    this.password = password;
    this.connectionsMax = connectionsMax;
  }

  /**
   * Releases connection back to the connection pool and notifies threads
   * waiting for a connection to become available.
   *
   * @param connection The connection to be released
   */
  public synchronized boolean freeConnection(Connection connection)
  {
    if(connection == null)
    {
      logger.error("Connection is null");
      return false;
    }

    if(reservedConnections.contains(connection))
    {
      //logger.debug("Freeing connection: " + connection.hashCode());
      reservedConnections.remove(connection);
      freeConnections.add(connection);
      notifyAll();
      return true;
    }

    return false;
  }

  /**
   * Reserves connection from the pool. If there are no connections available,
   * new connection is created unless maximum number of connections has been
   * reached.
   *
   * @return database connection. Returns null if connection cannot be reserved.
   */
  public synchronized Connection getConnection()
  {
    Connection connection = null;
    boolean found = false;
    //logger.debug("Searching for free connection.");

    while(freeConnections.size() > 0)
    {
      try
      {
        // Pick the first Connection in the Vector
        // to get round-robin usage
        connection = freeConnections.get(0);
        freeConnections.remove(0);

        if(!connection.isClosed())
        {
          Statement stmt = connection.createStatement();
          String sql = "SELECT NOW()";
          ResultSet rs = stmt.executeQuery(sql);
          if(rs.next())
          {
            found = true;
            break;
          }
        }
      }
      catch(SQLException e)
      {
        //logger.warn("SQLException: " + e.getMessage());
        //logger.warn("Retrying..");
      }
    }

    if(!found)
    {
      if(connectionsMax == 0 || reservedConnections.size() < connectionsMax)
      {
        //logger.debug("Creating new connection. (" + (reservedConnections.size()
        //        + 1) + "/" + connectionsMax + ")");
        connection = newConnection();
        if(connection == null)
        {
          logger.error("Connection creation failed!");
        }
      }
    }
    else
    {
      //logger.debug("Found free connection.");
    }

    if(connection != null)
    {
      //logger.debug("Reserving connection: " + connection.hashCode());
      reservedConnections.add(connection);
    }
    else
    {
      logger.debug("Null connection");

      for(Connection conn : reservedConnections)
      {
        logger.debug(conn.hashCode());
      }
    }
    return connection;
  }

  /**
   * Reserves connection from the pool. If there are no connections available,
   * new connection is created unless maximum number of connections has been
   * reached.
   *
   * @param timeout timeout value for waiting a connection to become available
   *
   * @return database connection. Returns null if connection cannot be reserved.
   */
  public synchronized Connection getConnection(long timeout)
  {
    long startTime = new Date().getTime();
    Connection con;
    while((con = getConnection()) == null)
    {
      try
      {
        wait(timeout);
      }
      catch(InterruptedException e)
      {
      }
      if((new Date().getTime() - startTime) >= timeout)
      {
        // Timeout has expired
        return null;
      }
    }
    return con;
  }

  /**
   * Closes all available connections.
   */
  public synchronized void release()
  {
    for(Connection con : freeConnections)
    {
      try
      {
        con.close();
        //logger.info("Closed connection for pool " + name);
      }
      catch(SQLException e)
      {
        logger.error("Can't close connection for pool " + name);
      }
    }
    freeConnections.clear();
  }

  /**
   * Creates a new connection, using a userid and password if specified.
   */
  private Connection newConnection()
  {
    Connection connection;
    try
    {
      if(username == null)
      {
        connection = DriverManager.getConnection(url);
      }
      else
      {
        connection = DriverManager.getConnection(url, username, password);
      }

      connection.setAutoCommit(false);

      //logger.info("Created a new connection in pool " + name);
    }
    catch(SQLException e)
    {
      logger.error("Can't create a new connection for " + url);
      return null;
    }
    return connection;
  }

}
