package fi.uta.cs.weto.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DbInputStream extends BufferedInputStream
{
  private final DbTransactionContext dbSession;

  public DbInputStream(DbTransactionContext dbSession, InputStream in)
  {
    super(in);
    this.dbSession = dbSession;
  }

  @Override
  public void close() throws IOException
  {
    super.close();
    dbSession.commitAll();
  }

}
