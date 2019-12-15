package fi.uta.cs.weto.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.regex.Pattern;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class WetoCsvWriter implements Closeable
{
  private static final Pattern nullString = Pattern.compile("\'+null\'+");
  private CsvListWriter clw;
  private Writer writer;

  public WetoCsvWriter(Writer writer) throws IOException
  {
    this.writer = writer;
    clw = new CsvListWriter(writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
  }

  public WetoCsvWriter(File file) throws IOException
  {
    writer = new FileWriter(file);
    clw = new CsvListWriter(writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
  }

  public void writeStrings(List<String> row) throws IOException
  {
    if(row.isEmpty())
    {
      return;
    }
    for(int i = 0; i < row.size(); ++i)
    {
      row.set(i, encodeNull(row.get(i)));
    }
    clw.write(row);
    clw.flush();
  }

  public static String encodeNull(String str)
  {
    if(str == null)
    {
      return "null";
    }
    else if(nullString.matcher(str).matches())
    {
      return "\'" + str + "\'";
    }
    return str;
  }

  public Writer getWriter()
  {
    return writer;
  }

  @Override
  public void close() throws IOException
  {
    clw.close();
  }

}
