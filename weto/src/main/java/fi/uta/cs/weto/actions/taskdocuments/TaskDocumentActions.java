package fi.uta.cs.weto.actions.taskdocuments;

import static com.opensymphony.xwork2.Action.SUCCESS;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.TaskDocument;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TaskDocumentStatus;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.util.TmpFileInputStream;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TaskDocumentActions
{
  public static class View extends WetoCourseAction
  {
    private final ArrayList<Document> documents = new ArrayList<>();

    // Whether the upload information should be displayed
    private final boolean allowUpload = true;
    // Whether the user has permission to delete documents
    private final boolean allowDelete = true;
    // Whether the view should be a table, or thumbnails
    private boolean showAsThumbnails = false;

    private boolean overWriteExisting = true;

    public View()
    {
      super(Tab.TASK_DOCUMENTS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      ArrayList<TaskDocument> taskDocuments = TaskDocument.selectByTaskId(
              courseConn, getTaskId());
      for(TaskDocument taskDoc : taskDocuments)
      {
        documents.add(Document.select1ById(courseConn, taskDoc.getDocumentId()));
      }
      Collections.sort(documents, new Comparator<Document>()
      {
        @Override
        public int compare(Document o1, Document o2)
        {
          return o1.getFileName().compareToIgnoreCase(o2.getFileName());
        }

      });
      return SUCCESS;
    }

    public ArrayList<Document> getDocuments()
    {
      return documents;
    }

    public boolean getAllowUpload()
    {
      return allowUpload;
    }

    public boolean getAllowDelete()
    {
      return allowDelete;
    }

    public void setShowAsThumbnails(boolean showAsThumbnails)
    {
      this.showAsThumbnails = showAsThumbnails;
    }

    public boolean getShowAsThumbnails()
    {
      return showAsThumbnails;
    }

    public boolean isOverWriteExisting()
    {
      return overWriteExisting;
    }

    public void setOverWriteExisting(boolean overWriteExisting)
    {
      this.overWriteExisting = overWriteExisting;
    }

  }

  public static class Add extends WetoCourseAction
  {
    private File documentFile = null;
    private String documentFileFileName;

    // Whether the view should be a table, or thumbnails
    private boolean showAsThumbnails = false;

    private boolean overWriteExisting = false;

    public Add()
    {
      super(Tab.TASK_DOCUMENTS.getBit(), 0, Tab.TASK_DOCUMENTS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      if(documentFile != null)
      {
        Document existingDoc = null;
        if(overWriteExisting)
        {
          ArrayList<Document> existingDocs = Document.selectByTaskIdAndFileName(
                  conn, taskId, documentFileFileName);
          if(!existingDocs.isEmpty())
          {
            existingDoc = existingDocs.get(0);
            for(int i = 1; i < existingDocs.size(); ++i)
            {
              Integer removeDocId = existingDocs.get(i).getId();
              // Delete taskDocument.
              TaskDocument.select1ByTaskIdAndDocumentId(conn, taskId,
                      removeDocId).delete(conn);
              DocumentModel.deleteDocument(conn, Document.select1ById(conn,
                      removeDocId));
            }
          }
        }
        Document document = DocumentModel.replaceDocument(conn, documentFile,
                documentFileFileName, existingDoc);
        boolean doUpdate = false;
        TaskDocument taskDocument;
        if(existingDoc != null)
        {
          doUpdate = true;
          taskDocument = TaskDocument.select1ByTaskIdAndDocumentId(conn, taskId,
                  existingDoc.getId());
        }
        else
        {
          taskDocument = new TaskDocument();
        }
        taskDocument.setTaskId(taskId);
        taskDocument.setDocumentId(document.getId());
        taskDocument.setUserId(getCourseUserId());
        taskDocument.setStatus(TaskDocumentStatus.PRIVATE_PUBLIC.getValue());
        taskDocument.setTimeStamp(document.getTimeStamp());
        if(doUpdate)
        {
          taskDocument.update(conn);
        }
        else
        {
          taskDocument.insert(conn);
        }
        addActionMessage(getText("taskDocuments.message.createSuccess"));
      }
      return SUCCESS;
    }

    public void setDocumentFile(File documentFile)
    {
      this.documentFile = documentFile;
    }

    public void setDocumentFileFileName(String documentFileFileName)
    {
      this.documentFileFileName = documentFileFileName;
    }

    public boolean isShowAsThumbnails()
    {
      return showAsThumbnails;
    }

    public void setShowAsThumbnails(boolean showAsThumbnails)
    {
      this.showAsThumbnails = showAsThumbnails;
    }

    public void setOverWriteExisting(boolean overWriteExisting)
    {
      this.overWriteExisting = overWriteExisting;
    }

  }

  public static class ConfirmDelete extends WetoCourseAction
  {
    private Integer[] documentIds;
    private ArrayList<Document> documents;

    public ConfirmDelete()
    {
      super(Tab.TASK_DOCUMENTS.getBit(), 0, 0, Tab.TASK_DOCUMENTS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      if(!((documentIds != null) && (documentIds.length > 0)))
      {
        throw new WetoActionException(getText("taskDocuments.error.noDocuments"));
      }
      documents = new ArrayList<>();
      Connection courseConn = getCourseConnection();
      for(Integer documentId : documentIds)
      {
        TaskDocument.select1ByTaskIdAndDocumentId(courseConn, getTaskId(),
                documentId);
        documents.add(Document.select1ById(courseConn, documentId));
      }
      return INPUT;
    }

    public void setDocumentIds(Integer[] documentIds)
    {
      this.documentIds = documentIds;
    }

    public ArrayList<Document> getDocuments()
    {
      return documents;
    }

  }

  public static class CommitDelete extends WetoCourseAction
  {

    private Integer[] documentIds;

    public CommitDelete()
    {
      super(Tab.TASK_DOCUMENTS.getBit(), 0, 0, Tab.TASK_DOCUMENTS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      if(!((documentIds != null) && (documentIds.length > 0)))
      {
        throw new WetoActionException(getText("taskDocuments.error.noDocuments"));
      }
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      for(Integer documentId : documentIds)
      {
        // Delete taskDocument. This step also coincidentally verifies that the
        // document and task ids correspond to each other.
        TaskDocument.select1ByTaskIdAndDocumentId(conn, taskId, documentId)
                .delete(conn);
        DocumentModel.deleteDocument(conn, Document
                .select1ById(conn, documentId));
      }
      addActionMessage(getText("taskDocuments.message.deleteSuccess"));
      return SUCCESS;
    }

    public void setDocumentIds(Integer[] documentIds)
    {
      this.documentIds = documentIds;
    }

  }

  public static class DownloadSelected extends WetoCourseAction
  {
    private static final int bufferSize = 8192;
    private Integer[] documentIds;
    private int contentLength;
    private InputStream documentStream;

    public DownloadSelected()
    {
      super(Tab.TASK_DOCUMENTS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      if(!((documentIds != null) && (documentIds.length > 0)))
      {
        throw new WetoActionException(getText("taskDocuments.error.noDocuments"));
      }
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      File baseDir = Files.createTempDirectory("weto").toFile();
      baseDir.mkdir();
      for(Integer documentId : documentIds)
      {
        // Delete taskDocument. This step also coincidentally verifies that the
        // document and task ids correspond to each other.
        TaskDocument.select1ByTaskIdAndDocumentId(conn, taskId, documentId);
        Document document = Document.select1ById(conn, documentId);
        DocumentModel.loadDocument(conn, document, new File(baseDir, document
                .getFileName()));
      }
      Path exportZipPath = Files.createTempFile("weto", ".zip");
      File exportZipFile = exportZipPath.toFile();
      WetoUtilities.zipSubDir(baseDir, ".", exportZipFile, false);
      WetoUtilities.deleteRecursively(baseDir);
      documentStream = new TmpFileInputStream(exportZipFile, Files
              .newInputStream(exportZipPath));
      contentLength = (int) exportZipFile.length();
      return SUCCESS;
    }

    public void setDocumentIds(Integer[] documentIds)
    {
      this.documentIds = documentIds;
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
}
