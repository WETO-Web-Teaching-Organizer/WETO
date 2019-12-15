package fi.uta.cs.weto.model;

import fi.uta.cs.weto.util.WetoCsvReader;
import fi.uta.cs.weto.util.WetoCsvWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class WetoProperties
{
  private Integer nextRank;

  private final Properties properties;

  private void put(String key, String value)
  {
    String numberedValue = value + "_" + nextRank.toString();
    nextRank += 1;
    properties.setProperty(key, numberedValue);
  }

  private String get(String key)
  {
    String result = properties.getProperty(key);
    if(result != null)
    {
      result = result.substring(0, result.lastIndexOf("_"));
    }
    return result;
  }

  public WetoProperties()
  {
    properties = new Properties();
    nextRank = 0;
  }

  public WetoProperties(Reader reader) throws IOException
  {
    this();
    StringBuilder tmpCopy = new StringBuilder();
    try(BufferedReader br = new BufferedReader(reader))
    {
      String endl = System.lineSeparator();
      String line;
      boolean contLine = false;
      while((line = br.readLine()) != null)
      {
        String trimmed = line.trim();
        if(!(contLine || trimmed.isEmpty() || trimmed.startsWith("#") || trimmed
                .startsWith("!")))
        {
          tmpCopy.append(nextRank.toString() + "_" + line + endl);
          nextRank += 1;
        }
        else
        {
          tmpCopy.append(line + endl);
        }
        contLine = line.endsWith("\\");
      }
    }
    Properties tmpProperties = new Properties();
    tmpProperties.load(new StringReader(tmpCopy.toString()));
    for(String key : tmpProperties.stringPropertyNames())
    {
      String[] parts = key.split("_", 2);
      String value = WetoCsvReader.decodeNull(tmpProperties.getProperty(key));
      if(value != null)
      {
        properties.setProperty(parts[1], value + "_" + parts[0]);
      }
    }
  }

  public String getProperty(String key)
  {
    return get(key);
  }

  public void setProperty(String key, String value)
  {
    put(key, WetoCsvWriter.encodeNull(value));
  }

  public void removeProperty(String key)
  {
    properties.remove(key);
  }

  public Set<String> getPropertySet()
  {
    return properties.stringPropertyNames();
  }

  private class Entry implements Comparable<Entry>
  {
    Integer rank;
    ArrayList<String> list;

    Entry(Integer rank, ArrayList<String> list)
    {
      this.rank = rank;
      this.list = list;
    }

    @Override
    public int compareTo(Entry o)
    {
      return rank.compareTo(o.rank);
    }

  }

  public void write(Writer writer) throws IOException
  {
    StringWriter tmpCopy = new StringWriter();
    properties.store(tmpCopy, null);
    ArrayList<Entry> propList = new ArrayList<>();
    try(BufferedReader br = new BufferedReader(new StringReader(tmpCopy
            .toString())))
    {
      String line;
      boolean prevCont = false;
      boolean currCont = false;
      Entry property = new Entry(null, new ArrayList<String>());
      while((line = br.readLine()) != null)
      {
        String trimmed = line.trim();
        currCont = line.endsWith("\\");
        if(!prevCont)
        {
          if(!property.list.isEmpty())
          {
            propList.add(property);
            property = new Entry(null, new ArrayList<String>());
          }
          if(!(trimmed.isEmpty() || trimmed.startsWith("#") || trimmed
                  .startsWith("!")))
          {
            if(!currCont)
            {
              int pos = line.lastIndexOf("_");
              property.rank = Integer.valueOf(line.substring(pos + 1));
              line = line.substring(0, pos);
            }
            property.list.add(line);
          }
        }
        else
        {
          if(!currCont)
          {
            int pos = line.lastIndexOf("_");
            property.rank = Integer.valueOf(line.substring(pos + 1));
            line = line.substring(0, pos);
          }
          property.list.add(line);
        }
        prevCont = currCont;
      }
      if(!property.list.isEmpty())
      {
        propList.add(property);
      }
    }
    Collections.sort(propList);
    try(BufferedWriter wr = new BufferedWriter(writer))
    {
      for(Entry entry : propList)
      {
        for(String line : entry.list)
        {
          wr.write(line);
          wr.newLine();
        }
      }
    }
  }

}
