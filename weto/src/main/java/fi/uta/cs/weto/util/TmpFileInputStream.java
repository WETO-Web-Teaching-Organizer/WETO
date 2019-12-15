package fi.uta.cs.weto.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TmpFileInputStream extends BufferedInputStream
{
  private final File tmpFile;

  public TmpFileInputStream(File tmpFile, InputStream in)
  {
    super(in);
    this.tmpFile = tmpFile;
  }

  @Override
  public void close() throws IOException
  {
    super.close();
    tmpFile.delete();
  }

}
