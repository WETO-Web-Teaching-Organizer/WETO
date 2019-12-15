package fi.uta.cs.weto.actions.log;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.WetoUtilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class LogActions
{
  public static class CourseLog extends WetoTeacherAction
  {
    private static final String[] logHeaders;
    private static final HashMap<Integer, String> logEventNames;
    private static final int logCols = 5;
    private static final int defaultMaxRows = 50;

    static
    {
      logHeaders = new String[logCols];
      logEventNames = new HashMap<>();
      // Awkward: use DbConnectionManager as a means to access resource files
      logHeaders[0] = WetoUtilities.getMessageResource("general.header.user");
      logHeaders[1] = WetoUtilities.getMessageResource("log.header.context");
      logHeaders[2] = WetoUtilities.getMessageResource("log.header.event");
      logHeaders[3] = WetoUtilities.getMessageResource("log.header.address");
      logHeaders[4] = WetoUtilities.getMessageResource("general.header.time");
      logEventNames.put(-1, WetoUtilities.getMessageResource(
              "log.header.allEvents"));
      for(LogEvent event : LogEvent.values())
      {
        logEventNames.put(event.getValue(), WetoUtilities.getMessageResource(
                event.getProperty()));
      }
    }

    private Integer[] userStats = new Integer[3];
    private Integer[] submissionStats = new Integer[3];
    private Integer[] submitterStats = new Integer[3];

    private ArrayList<String[]> logRows;
    private ArrayList<Integer> logEventUserIds;
    private ArrayList<Integer> logEventTaskIds;

    private boolean showSummaryStatistics;
    private String loginName;
    private Integer logTaskId;
    private Integer eventId;
    private String from;
    private String to;
    private Integer maxRows;

    public CourseLog()
    {
      super(Tab.LOG.getBit(), 0, 0, 0);
      maxRows = defaultMaxRows;
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      logRows = new ArrayList<>();
      logEventUserIds = new ArrayList<>();
      logEventTaskIds = new ArrayList<>();
      HashMap<Integer, UserAccount> userMap = new HashMap<>();
      HashMap<Integer, String> taskMap = new HashMap<>();
      String deleted = getText("log.text.deleted");
      Integer logUserId = null;
      if((loginName != null) && !(loginName = loginName.trim()).isEmpty())
      {
        try
        {
          logUserId = UserAccount.select1ByLoginName(conn, loginName).getId();
        }
        catch(NoSuchItemException e)
        {
          addActionError(getText("log.error.invalidLoginName", new String[]
          {
            loginName
          }));
        }
      }
      Integer courseTaskId = getCourseTaskId();
      Calendar cal = new GregorianCalendar();
      if(showSummaryStatistics)
      {
        cal.add(Calendar.DAY_OF_MONTH, -1);
        WetoTimeStamp dayBefore = new WetoTimeStamp(cal);
        cal.add(Calendar.DAY_OF_MONTH, -6);
        WetoTimeStamp weekBefore = new WetoTimeStamp(cal);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        cal.add(Calendar.MONTH, -1);
        WetoTimeStamp monthBefore = new WetoTimeStamp(cal);
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(distinct(userid)) from log where coursetaskid="
                  + courseTaskId + " and timestamp > " + dayBefore
                  .getTimeStamp());
          rs.next();
          userStats[0] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(distinct(userid)) from log where coursetaskid="
                  + courseTaskId + " and timestamp > " + weekBefore
                  .getTimeStamp());
          rs.next();
          userStats[1] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(distinct(userid)) from log where coursetaskid="
                  + courseTaskId + " and timestamp > " + monthBefore
                  .getTimeStamp());
          rs.next();
          userStats[2] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(*) from submission, task where submission.taskid=task.id and task.roottaskid="
                  + courseTaskId + " and submission.timestamp > " + dayBefore
                  .getTimeStamp());
          rs.next();
          submissionStats[0] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(*) from submission, task where submission.taskid=task.id and task.roottaskid="
                  + courseTaskId + " and submission.timestamp > " + weekBefore
                  .getTimeStamp());
          rs.next();
          submissionStats[1] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(*) from submission, task where submission.taskid=task.id and task.roottaskid="
                  + courseTaskId + " and submission.timestamp > " + monthBefore
                  .getTimeStamp());
          rs.next();
          submissionStats[2] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(distinct(submission.userid)) from submission, task where submission.taskid=task.id and task.roottaskid="
                  + courseTaskId + " and submission.timestamp > " + dayBefore
                  .getTimeStamp());
          rs.next();
          submitterStats[0] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(distinct(submission.userid)) from submission, task where submission.taskid=task.id and task.roottaskid="
                  + courseTaskId + " and submission.timestamp > " + weekBefore
                  .getTimeStamp());
          rs.next();
          submitterStats[1] = rs.getInt(1);
          rs.close();
        }
        try(Statement stat = conn.createStatement())
        {
          ResultSet rs = stat.executeQuery(
                  "select count(distinct(submission.userid)) from submission, task where submission.taskid=task.id and task.roottaskid="
                  + courseTaskId + " and submission.timestamp > " + monthBefore
                  .getTimeStamp());
          rs.next();
          submitterStats[2] = rs.getInt(1);
          rs.close();
        }
      }
      Integer fromTimeStamp = null;
      DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
      if(from != null && !from.isEmpty())
      {
        try
        {
          cal.setTime(formatter.parse(from));
          fromTimeStamp = new WetoTimeStamp(cal).getTimeStamp();
        }
        catch(ParseException e)
        {
          addActionError(e.getMessage());
        }
      }
      Integer toTimeStamp = null;
      if(to != null && !to.isEmpty())
      {
        try
        {
          cal.setTime(formatter.parse(to));
          // The to-date is interpreted mean "until the end of the given day"
          cal.set(Calendar.HOUR_OF_DAY, 23);
          cal.set(Calendar.MINUTE, 59);
          cal.set(Calendar.SECOND, 59);
          toTimeStamp = new WetoTimeStamp(cal).getTimeStamp();
        }
        catch(ParseException e)
        {
          addActionError(e.getMessage());
        }
      }
      if((eventId != null) && (eventId < 0))
      {
        eventId = null;
      }
      ArrayList<Log> events = Log.selectByConstraints(conn, courseTaskId,
              logUserId, logTaskId, eventId, fromTimeStamp, toTimeStamp, maxRows);
      for(Log event : events)
      {
        String[] row = new String[logCols];
        Integer eventUserId = event.getUserId();
        UserAccount user = userMap.get(eventUserId);
        if(user == null)
        {
          try
          {
            user = UserAccount.select1ById(conn, eventUserId);
            userMap.put(eventUserId, user);
          }
          catch(NoSuchItemException e)
          {
          }
        }
        row[0] = (user != null) ? user.getLoginName() : "";
        Integer eventTaskId = event.getTaskId();
        String taskName = taskMap.get(eventTaskId);
        if(taskName == null)
        {
          try
          {
            taskName = Task.select1ById(conn, eventTaskId).getName();
            taskName = taskName.concat(" [id=" + eventTaskId + "]");
            taskMap.put(eventTaskId, taskName);
          }
          catch(NoSuchItemException e)
          {
            taskName = deleted;
          }
        }
        row[1] = taskName;
        LogEvent logEvent = LogEvent.getEvent(event.getEvent());
        String eventText = getText(logEvent.getProperty());
        if(logEvent.equals(LogEvent.VIEW_SUBMISSION) || logEvent.equals(
                LogEvent.CREATE_SUBMISSION) || logEvent.equals(
                        LogEvent.SUBMIT_SUBMISSION))
        {
          try
          {
            Submission.select1ById(conn, event.getPar1());
            eventText = eventText.concat(
                    " <a href=\"viewSubmission.action?taskId=" + eventTaskId
                    + "&amp;tabId=" + getSubmissionsTabId() + "&amp;dbId="
                    + getDbId() + "&amp;submissionId=" + event.getPar1() + "\">"
                    + event.getPar1() + "</a>");
          }
          catch(NoSuchItemException e)
          {
            eventText = eventText.concat(" " + event.getPar1() + " (" + deleted
                    + ")");
          }
        }
        else if(logEvent.equals(LogEvent.DELETE_SUBMISSION) || logEvent.equals(
                LogEvent.DELETE_GRADE) || logEvent.equals(
                        LogEvent.REMOVE_STUDENT))
        {
          eventText = eventText.concat(" " + event.getPar1());
        }
        else if(logEvent.equals(LogEvent.SAVE_SUBMISSION_DOCUMENT) || logEvent
                .equals(LogEvent.DELETE_SUBMISSION_DOCUMENT))
        {
          String docName;
          try
          {
            docName = Document.select1ById(conn, event.getPar2())
                    .getFileName();
          }
          catch(NoSuchItemException e)
          {
            docName = deleted;
          }
          eventText = eventText.concat(" " + getText(
                  "log.text.submissionDocument",
                  new String[]
                  {
                    event.getPar2().toString(),
                    docName,
                    "<a href=\"viewSubmission.action?taskId=" + eventTaskId
                    + "&amp;tabId=" + getSubmissionsTabId() + "&amp;dbId="
                    + getDbId() + "&amp;submissionId=" + event.getPar1()
                    + "\">" + event.getPar1() + "</a>"
                  }));
        }
        else if(logEvent.equals(LogEvent.SAVE_SUBMISSION_DOCUMENT))
        {
          String docName;
          try
          {
            docName = Document.select1ById(conn, event.getPar1())
                    .getFileName();
          }
          catch(NoSuchItemException e)
          {
            docName = deleted;
          }
          eventText = eventText.concat(" " + getText(
                  "log.text.document",
                  new String[]
                  {
                    event.getPar1().toString(),
                    docName
                  }));
        }
        else if(logEvent.equals(LogEvent.CREATE_GRADE) || logEvent.equals(
                LogEvent.UPDATE_GRADE))
        {
          try
          {
            Grade.select1ById(conn, event.getPar1());
            eventText = eventText.concat(
                    " <a href=\"viewGrade.action?taskId=" + eventTaskId
                    + "&amp;tabId=" + getGradingTabId() + "&amp;dbId="
                    + getDbId() + "&amp;gradeId=" + event.getPar1() + "\">"
                    + event.getPar1() + "</a>");
          }
          catch(NoSuchItemException e)
          {
            eventText = eventText.concat(" " + event.getPar1() + " (" + deleted
                    + ")");
          }
        }
        row[2] = eventText;
        row[3] = event.getAddress();
        row[4] = event.getTimeStampString();
        logRows.add(row);
        logEventUserIds.add(eventUserId);
        logEventTaskIds.add(eventTaskId);
      }
      return SUCCESS;
    }

    public Integer[] getUserStats()
    {
      return userStats;
    }

    public Integer[] getSubmissionStats()
    {
      return submissionStats;
    }

    public Integer[] getSubmitterStats()
    {
      return submitterStats;
    }

    public String[] getLogHeaders()
    {
      return logHeaders;
    }

    public HashMap<Integer, String> getLogEventNames()
    {
      return logEventNames;
    }

    public ArrayList<String[]> getLogRows()
    {
      return logRows;
    }

    public ArrayList<Integer> getLogEventUserIds()
    {
      return logEventUserIds;
    }

    public ArrayList<Integer> getLogEventTaskIds()
    {
      return logEventTaskIds;
    }

    public String getLoginName()
    {
      return loginName;
    }

    public void setLoginName(String loginName)
    {
      this.loginName = loginName;
    }

    public Integer getLogTaskId()
    {
      return logTaskId;
    }

    public void setLogTaskId(Integer logTaskId)
    {
      this.logTaskId = logTaskId;
    }

    public Integer getEventId()
    {
      return eventId;
    }

    public void setEventId(Integer eventId)
    {
      this.eventId = eventId;
    }

    public String getFrom()
    {
      return from;
    }

    public void setFrom(String from)
    {
      this.from = from;
    }

    public String getTo()
    {
      return to;
    }

    public void setTo(String to)
    {
      this.to = to;
    }

    public Integer getMaxRows()
    {
      return maxRows;
    }

    public void setMaxRows(Integer maxRows)
    {
      this.maxRows = maxRows;
    }

    public boolean isShowSummaryStatistics()
    {
      return showSummaryStatistics;
    }

    public void setShowSummaryStatistics(boolean showSummaryStatistics)
    {
      this.showSummaryStatistics = showSummaryStatistics;
    }

  }
}
