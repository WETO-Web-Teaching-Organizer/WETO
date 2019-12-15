package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Properties;

public class SubmissionPropertiesActions
{
  public static class Edit extends WetoTeacherAction
  {
    private String filePatterns;
    private String patternDescriptions;
    private boolean allowZipping;
    private boolean allowInlineFiles;
    private boolean caseInsensitive;
    private boolean allowTestRun;
    private String oldSubmissionLimit;

    public Edit()
    {
      super(Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      filePatterns = null;
      oldSubmissionLimit = null;
      try
      {
        SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
                taskId);
        Properties properties = new Properties();
        properties.load(new StringReader(sp.getProperties()));
        filePatterns = properties.getProperty("filePatterns");
        patternDescriptions = properties.getProperty("patternDescriptions");
        allowZipping = Boolean.valueOf(properties.getProperty("allowZipping"));
        caseInsensitive = Boolean.valueOf(properties.getProperty(
                "caseInsensitive"));
        allowInlineFiles = Boolean.valueOf(properties.getProperty(
                "allowInlineFiles"));
        allowTestRun = Boolean.valueOf(properties.getProperty("allowTestRun"));
        oldSubmissionLimit = properties.getProperty("oldSubmissionLimit");
        try
        {
          Integer.valueOf(oldSubmissionLimit);
        }
        catch(NumberFormatException e)
        {
          oldSubmissionLimit = null;
        }
      }
      catch(NoSuchItemException e)
      {
      }
      return SUCCESS;
    }

    public String getFilePatterns()
    {
      return filePatterns;
    }

    public String getPatternDescriptions()
    {
      return patternDescriptions;
    }

    public boolean isAllowZipping()
    {
      return allowZipping;
    }

    public boolean isCaseInsensitive()
    {
      return caseInsensitive;
    }

    public boolean isAllowInlineFiles()
    {
      return allowInlineFiles;
    }

    public boolean isAllowTestRun()
    {
      return allowTestRun;
    }

    public String getOldSubmissionLimit()
    {
      return oldSubmissionLimit;
    }

  }

  public static class Save extends WetoTeacherAction
  {
    private String filePatterns;
    private String patternDescriptions;
    private boolean allowZipping;
    private boolean allowInlineFiles;
    private boolean caseInsensitive;
    private boolean allowTestRun;
    private String oldSubmissionLimit;

    public Save()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, Tab.SUBMISSIONS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      SubmissionProperties sp;
      boolean doUpdate = false;
      try
      {
        sp = SubmissionProperties.select1ByTaskId(conn, taskId);
        doUpdate = true;
      }
      catch(NoSuchItemException e)
      {
        sp = new SubmissionProperties();
        sp.setTaskId(taskId);
      }
      Properties properties = new Properties();
      if(filePatterns != null)
      {
        properties.setProperty("filePatterns", filePatterns);
      }
      if(patternDescriptions != null)
      {
        properties.setProperty("patternDescriptions", patternDescriptions);
      }
      properties.setProperty("allowZipping", allowZipping ? "true" : "false");
      properties.setProperty("allowInlineFiles", allowInlineFiles ? "true"
                                                         : "false");
      properties.setProperty("caseInsensitive", caseInsensitive ? "true"
                                                : "false");
      properties.setProperty("allowTestRun", allowTestRun ? "true" : "false");
      if(oldSubmissionLimit != null)
      {
        properties.setProperty("oldSubmissionLimit", oldSubmissionLimit);
      }
      StringWriter sw = new StringWriter();
      properties.store(sw, null);
      sp.setProperties(sw.toString());
      if(doUpdate)
      {
        sp.update(conn);
      }
      else
      {
        sp.insert(conn);
      }
      addActionMessage(getText("submissionProperties.message.success"));
      return SUCCESS;
    }

    public void setFilePatterns(String filePatterns)
    {
      if(filePatterns != null && !filePatterns.isEmpty())
      {
        this.filePatterns = filePatterns;
      }
      else
      {
        this.filePatterns = null;
      }
    }

    public void setPatternDescriptions(String patternDescriptions)
    {
      if(patternDescriptions != null && !patternDescriptions.isEmpty())
      {
        this.patternDescriptions = patternDescriptions;
      }
      else
      {
        this.patternDescriptions = null;
      }
    }

    public void setAllowZipping(boolean allowZipping)
    {
      this.allowZipping = allowZipping;
    }

    public void setAllowInlineFiles(boolean allowInlineFiles)
    {
      this.allowInlineFiles = allowInlineFiles;
    }

    public void setCaseInsensitive(boolean caseInsensitive)
    {
      this.caseInsensitive = caseInsensitive;
    }

    public void setAllowTestRun(boolean allowTestRun)
    {
      this.allowTestRun = allowTestRun;
    }

    public void setOldSubmissionLimit(String oldSubmissionLimit)
    {
      this.oldSubmissionLimit = null;
      try
      {
        Integer limit = Integer.valueOf(oldSubmissionLimit);
        if(limit >= 0)
        {
          this.oldSubmissionLimit = oldSubmissionLimit;
        }
      }
      catch(NumberFormatException e)
      {
      }
    }

  }
}
