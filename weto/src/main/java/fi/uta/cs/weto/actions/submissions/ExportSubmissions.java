package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.TmpFileInputStream;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

public class ExportSubmissions extends WetoTeacherAction
{
  private static final int bufferSize = 8192;
  private Integer[] userIds;
  private String from;
  private String to;
  private boolean onlyLatestOrBest;
  private boolean doNotCompress;
  private int contentLength;
  private InputStream documentStream;

  public ExportSubmissions()
  {
    super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Set<Integer> userIdSet;
    if(userIds != null)
    {
      userIdSet = new HashSet<>(Arrays.asList(userIds));
    }
    else
    {
      userIdSet = new HashSet<>();
    }
    Integer taskId = getTaskId();
    Integer fromTimeStamp = null;
    DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    if(from != null && !from.isEmpty())
    {
      try
      {
        Calendar cal = new GregorianCalendar();
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
        Calendar cal = new GregorianCalendar();
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
    Map<Integer, ArrayList<Submission>> submissions = SubmissionModel
            .getSubmissions(conn, taskId, getCourseUserId(), userIdSet,
                    fromTimeStamp, toTimeStamp, onlyLatestOrBest, null, this);
    if(submissions.isEmpty())
    {
      throw new WetoActionException(getText("submissions.error.noSubmissions"));
    }
    File baseDir = Files.createTempDirectory("weto").toFile();
    baseDir.mkdir();
    SubmissionModel.loadSubmissions(conn, submissions, false, baseDir);
    Path exportZipPath = Files.createTempFile("weto", ".zip");
    File exportZipFile = exportZipPath.toFile();
    Integer level = doNotCompress ? ZipOutputStream.STORED : null;
    WetoUtilities.zipSubDir(baseDir, ".", exportZipFile, doNotCompress);
    WetoUtilities.deleteRecursively(baseDir);
    documentStream = new TmpFileInputStream(exportZipFile, Files.newInputStream(
            exportZipPath));
    contentLength = (int) exportZipFile.length();
    return SUCCESS;
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

  public void setOnlyLatestOrBest(boolean onlyLatestOrBest)
  {
    this.onlyLatestOrBest = onlyLatestOrBest;
  }

  public void setDoNotCompress(boolean doNotCompress)
  {
    this.doNotCompress = doNotCompress;
  }

  public void setUserIds(Integer[] userIds)
  {
    this.userIds = userIds;
  }

  public int getContentLength()
  {
    return contentLength;
  }

  public InputStream getDocumentStream()
  {
    return documentStream;
  }

  public static int getBuffersize()
  {
    return bufferSize;
  }

}
