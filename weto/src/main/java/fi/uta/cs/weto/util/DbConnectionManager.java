/*
 * Copyright (c) 1998 by Gefion software.
 *
 * Permission to use, copy, and distribute this software for
 * NON-COMMERCIAL purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 *
 */
package fi.uta.cs.weto.util;

import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.weto.db.DatabasePool;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * This class is a Singleton that provides access to one or many connection
 * pools defined in a Property file. A client gets access to the single instance
 * through the static getInstance() method and can then check-out and check-in
 * connections from a pool. When the client shuts down it should call the
 * release() method to close all open connections and do other clean up.
 */
public class DbConnectionManager extends ActionSupport
{
  private final static Logger logger = Logger.getLogger(
          DbConnectionManager.class);

  /**
   * Holds the instance of DBConnectionManager
   */
  static private DbConnectionManager instance;

  /**
   * Number of clients that hold a reference to the instance of
   * DBConnectionManager
   */
  static private int clientCount;

  /**
   * Collection of database drivers
   */
  private Vector<Driver> drivers = new Vector<>();

  /**
   * Connection pools for the databases.
   */
  private Hashtable<String, DbConnectionPool> pools = new Hashtable<>();

  /**
   * Returns the single instance, creating one if it's the first time this
   * method is called.
   *
   * @return DBConnectionManager The single instance.
   */
  static synchronized public DbConnectionManager getInstance()
  {
    if(instance == null)
    {
      instance = new DbConnectionManager();
    }
    clientCount++;
    return instance;
  }

  /**
   * A private constructor since this is a Singleton
   */
  private DbConnectionManager()
  {
    String driverNames = WetoUtilities.getPackageResource("database.drivers");
    String url = WetoUtilities.getPackageResource("database.url");
    String username = WetoUtilities.getPackageResource("database.username");
    String password = WetoUtilities.getPackageResource("database.password");
    String maxConnections = WetoUtilities.getPackageResource(
            "database.connections.max");

    // Read optional master.connections.max property
    Integer masterConnectionsMax = null;
    if(maxConnections != null)
    {
      try
      {
        masterConnectionsMax = Integer.valueOf(maxConnections);
      }
      catch(NumberFormatException e)
      {
        logger.warn(
                "database.connections.max has an invalid value, setting maximum "
                + "number of connections to 10");
      }
    }

    // If property does not exist or does not have a valid value, then set
    // the maximum number of connections to hard-coded default 10.
    if(masterConnectionsMax == null)
    {
      masterConnectionsMax = 10;
    }

    // Load and register JDBC drivers.
    StringTokenizer st = new StringTokenizer(driverNames);
    while(st.hasMoreElements())
    {
      String driverName = st.nextToken().trim();

      try
      {
        Driver driver = (Driver) Class.forName(driverName)
                .newInstance();
        DriverManager.registerDriver(driver);
        drivers.addElement(driver);
        //logger.info("Registered JDBC driver " + driverName);
      }
      catch(ClassNotFoundException | InstantiationException |
            IllegalAccessException | SQLException e)
      {
        logger.error("Unable to register JDBC driver: " + driverName
                + ", Exception: " + e);
      }
    }

    // Create master database connection pool.
    DbConnectionPool pool = new DbConnectionPool("master", url, username,
            password, masterConnectionsMax);
    pools.put("master", pool);

    Connection connection = null;
    try
    {
      connection = getConnection("master");
      Vector<DatabasePool> poolBeans = DatabasePool.selectAllVector(connection);

      for(DatabasePool poolBean : poolBeans)
      {
        pool = new DbConnectionPool(
                poolBean.getName(),
                poolBean.getUrl(),
                poolBean.getUsername(),
                poolBean.getPassword(),
                masterConnectionsMax);
        pools.put(poolBean.getName(), pool);
      }
    }
    catch(SQLException e)
    {
      logger.error("Retrieving database pools from the master database failed");
    }
    finally
    {
      freeConnection(connection);
    }
  }

  /**
   * Returns a connection to the named pool.
   *
   * @param connection Database connection
   */
  public boolean freeConnection(Connection connection)
  {
    for(DbConnectionPool pool : pools.values())
    {
      if(pool.freeConnection(connection))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns an open connection. If no one is available, and the max number of
   * connections has not been reached, a new connection is created.
   *
   * @param name The pool name as defined in the properties file
   * @return Connection The connection or null
   */
  public Connection getConnection(String name)
  {
    DbConnectionPool pool = (DbConnectionPool) pools.get(name);
    Connection connection = pool.getConnection();
    return connection;
  }

  /**
   * Returns an open connection. If no one is available, and the max number of
   * connections has not been reached, a new connection is created. If the max
   * number has been reached, waits until one is available or the specified time
   * has elapsed.
   *
   * @param name The pool name as defined in the properties file
   * @param time The number of milliseconds to wait
   * @return Connection The connection or null
   */
  public Connection getConnection(String name, long time)
  {
    DbConnectionPool pool = (DbConnectionPool) pools.get(name);
    Connection connection = pool.getConnection(time);
    return connection;
  }

  /**
   * Closes all open connections and deregisters all drivers.
   */
  public synchronized void release()
  {
    // Wait until called by the last client
    if(--clientCount != 0)
    {
      return;
    }

    Enumeration<DbConnectionPool> allPools = pools.elements();
    while(allPools.hasMoreElements())
    {
      DbConnectionPool pool = (DbConnectionPool) allPools.nextElement();
      pool.release();
    }
    Enumeration<Driver> allDrivers = drivers.elements();
    while(allDrivers.hasMoreElements())
    {
      Driver driver = (Driver) allDrivers.nextElement();
      try
      {
        DriverManager.deregisterDriver(driver);
        //logger.info("Deregistered JDBC driver " + driver.getClass().getName());
      }
      catch(SQLException e)
      {
        logger.error("Can't deregister JDBC driver: " + driver.getClass()
                .getName());
      }
    }
  }

}
