package fi.uta.cs.weto.actions.taskdocuments;

import com.opensymphony.xwork2.ActionSupport;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.TaskDocument;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.Navigator;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.DbInputStream;
import fi.uta.cs.weto.util.DbTransactionContext;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.NoSuchElementException;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

public class DownloadTaskDocument extends ActionSupport
{
  private Integer documentId;
  private InputStream documentStream;
  private String contentType;
  private int contentLength;
  private Document document;
  private final int bufferSize = 8192;

  private Integer taskId;
  private Integer tabId;
  private Integer dbId;

  @Override
  public final String execute()
  {
    final HttpServletRequest request = ServletActionContext.getRequest();
    final DbTransactionContext dbSession = DbTransactionContext.getInstance(
            request);
    try
    {
      final Connection masterConnection = dbSession.getConnection("master");
      Navigator navigator = null;
      Integer masterUserId = null;
      Integer courseUserId = null;
      Connection courseConn = null;
      try
      {
        // Try to retrieve a course database id from the navigator.
        navigator = Navigator.getInstance(request);
        courseConn = dbSession.getConnection(navigator.getDatabases().get(dbId));
        masterUserId = navigator.getMasterUserId();
        if(masterUserId != null)
        {
          UserIdReplication uidr = UserIdReplication.select1ByMasterDbUserId(
                  courseConn, masterUserId);
          courseUserId = uidr.getCourseDbUserId();
        }
      }
      catch(NoSuchElementException e)
      {
        navigator = null;
        courseConn = dbSession.getConnection(DatabasePool.selectNamesWithMaster(
                masterConnection).get(dbId));
      }

      // Verify that the taskId and the documentId correspond to each other.
      // This is important because the document access permission is based on task
      // viewing permissions.
      TaskDocument.select1ByTaskIdAndDocumentId(courseConn, taskId, documentId);

      // Check access rights (teachers always have rights)
      Task courseTask = Task.select1ById(courseConn, taskId);
      Integer clusterType = null;
      if(courseUserId != null)
      {
        try
        {
          clusterType = UserTaskView.select1ByTaskIdAndUserId(courseConn,
                  courseTask.getRootTaskId(), courseUserId).getClusterType();
        }
        catch(NoSuchItemException e)
        {
        }
      }
      if(!ClusterType.TEACHERS.getValue().equals(clusterType))
      {
        Integer tmpTaskId = courseTask.getId();
        boolean isHidden = false;
        boolean isNotPublic = false;
        while(true)
        {
          isHidden = isHidden || courseTask.getIsHidden();
          isNotPublic = isNotPublic || !courseTask.getIsPublic();
          try
          {
            SubtaskLink link = SubtaskLink.select1BySubtaskId(courseConn,
                    tmpTaskId);
            tmpTaskId = link.getContainerId();
            courseTask = Task.select1ById(courseConn, tmpTaskId);
          }
          catch(NoSuchItemException e)
          {
            break;
          }
        }
        WetoTimeStamp[] viewPeriod = PermissionModel.getTimeStampLimits(
                courseConn, request.getRemoteAddr(), courseUserId, taskId,
                PermissionType.VIEW);
        if(isHidden || (PermissionModel.checkTimeStampLimits(viewPeriod)
                != PermissionModel.CURRENT) || ((clusterType == null)
                && isNotPublic))
        {
          dbSession.cancelAll();
          return ERROR;
        }
      }

      // Retrieve and output document.
      document = Document.select1ById(courseConn, documentId);
      long oid = document.getContentId();
      LargeObjectManager lobMan = ((org.postgresql.PGConnection) courseConn)
              .getLargeObjectAPI();
      LargeObject lob = lobMan.open(oid, LargeObjectManager.READ);
      InputStream dbStream = new DbInputStream(dbSession, lob.getInputStream());
      contentType = document.getMimeType();
      if(document.getContentMimeType().endsWith("zip"))
      {
        contentLength = document.getFileSize();
        documentStream = dbStream;
      }
      else
      {
        contentType = document.getContentMimeType();
        contentLength = document.getContentFileSize();
        ZipInputStream zip = new ZipInputStream(dbStream);
        zip.getNextEntry();
        documentStream = zip;
      }
      // DbInputStream will commit in the end.
      return SUCCESS;
    }
    catch(Exception e)
    {
      dbSession.cancelAll();
      return ERROR;
    }
  }

  public void setTaskId(Integer taskId)
  {
    this.taskId = taskId;
  }

  public void setTabId(Integer tabId)
  {
    this.tabId = tabId;
  }

  public void setDbId(Integer dbId)
  {
    this.dbId = dbId;
  }

  public void setDocumentId(Integer documentId)
  {
    this.documentId = documentId;
  }

  public InputStream getDocumentStream()
  {
    return documentStream;
  }

  public String getContentType()
  {
    return contentType.startsWith("text") ? contentType + ";charset=UTF-8"
                   : contentType;
  }

  public String getContentDisposition() throws UnsupportedEncodingException
  {
    return "inline;filename=\"" + document.getFileName() + "\"";
  }

  public int getContentLength()
  {
    return contentLength;
  }

  public int getBufferSize()
  {
    return bufferSize;
  }

}
