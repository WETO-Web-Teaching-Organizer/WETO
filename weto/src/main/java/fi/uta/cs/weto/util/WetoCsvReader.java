package fi.uta.cs.weto.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class WetoCsvReader extends CsvListReader
{
  private static final Pattern nullString = Pattern.compile("\'+null\'+");

  public WetoCsvReader(Reader reader) throws IOException
  {
    super(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
  }

  public WetoCsvReader(File file) throws IOException
  {
    super(new FileReader(file), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
  }

  @Override
  public List<String> read() throws IOException
  {
    List<String> result = super.read();
    if(result != null)
    {
      for(int i = 0; i < result.size(); ++i)
      {
        result.set(i, decodeNull(result.get(i)));
      }
    }
    return result;
  }

  public static String decodeNull(String str)
  {
    if(str == null)
    {
      return "";
    }
    else if(str.equals("null"))
    {
      return null;
    }
    else if(nullString.matcher(str).matches())
    {
      return str.substring(1, str.length() - 1);
    }
    return str;
  }

}
