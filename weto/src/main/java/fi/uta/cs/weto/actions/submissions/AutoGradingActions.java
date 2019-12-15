package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.AutoGradeTestScore;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionDocument;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.SubmissionError;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.util.DbInputStream;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoGradingActions
{
  public static class View extends WetoTeacherAction
  {
    private static final Map<Integer, String> submissionStates;
    private static final Map<Integer, String> submissionErrors;

    static
    {
      submissionStates = new HashMap<>();
      for(SubmissionStatus status : SubmissionStatus.values())
      {
        submissionStates.put(status.getValue(), status.getProperty());
      }
      submissionErrors = new HashMap<>();
      for(SubmissionError error : SubmissionError.values())
      {
        submissionErrors.put(error.getValue(), error.getProperty());
      }
    }

    private String properties;
    private Document autoGradingDocument;
    private boolean autoGraded;
    private Submission submission;
    private Integer submissionId;
    private File documentFile;
    private String documentFileFileName;
    private ArrayList<Document> documents;
    private boolean[] duplicates;
    private Integer queuePos;
    private ArrayList<AutoGradeTestScore> testScores;
    private Integer compilerResultId;
    private Integer[] fullFeedbackIds;
    private boolean allowZipping;
    private boolean overWriteExisting = true;

    public View()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      Task task = getTask();
      autoGraded = task.getIsAutoGraded();
      // Try to retrieve existing autogradetask for input.
      try
      {
        AutoGrading autoGrading = AutoGrading.select1ByTaskId(conn, taskId);
        properties = autoGrading.getProperties();
        if(autoGrading.getTestDocId() != null)
        {
          autoGradingDocument = Document.select1ById(conn, autoGrading
                  .getTestDocId());
        }
        try
        {
          submission = Submission.select1LatestSubmissionByUserIdAndTaskId(conn,
                  userId, taskId);
        }
        catch(NoSuchItemException e)
        {
          submission = new Submission();
          submission.setTaskId(taskId);
          submission.setUserId(userId);
          submission.setStatus(SubmissionStatus.NOT_SUBMITTED.getValue());
          submission.insert(conn);
        }
        submissionId = submission.getId();
        if(documentFileFileName != null)
        {
          boolean documentAdded = false;
          if((documentFile != null) && allowZipping && documentFileFileName
                  .endsWith(".zip"))
          {
            File workDir = Files.createTempDirectory("submit").toFile();
            WetoUtilities.unzipFile(documentFile, workDir);
            try
            {
              for(File file : workDir.listFiles())
              {
                if(file.isFile())
                {
                  String fileName = file.getName();
                  Document existingDoc = null;
                  if(overWriteExisting)
                  {
                    ArrayList<Document> existingDocs = Document
                            .selectBySubmissionIdAndFileName(
                                    conn, submissionId, fileName);
                    if(!existingDocs.isEmpty())
                    {
                      existingDoc = existingDocs.get(0);
                      for(int i = 1; i < existingDocs.size(); ++i)
                      {
                        Integer removeDocId = existingDocs.get(i).getId();
                        // Delete submissionDocument.
                        SubmissionDocument.select1BySubmissionIdAndDocumentId(
                                conn, taskId,
                                removeDocId).delete(conn);
                        DocumentModel.deleteDocument(conn, Document.select1ById(
                                conn,
                                removeDocId));
                      }
                    }
                  }
                  Document document = DocumentModel.replaceDocument(conn, file,
                          fileName, existingDoc);
                  boolean doUpdate = false;
                  SubmissionDocument submissionDocument;
                  if(existingDoc != null)
                  {
                    doUpdate = true;
                    submissionDocument = SubmissionDocument
                            .select1BySubmissionIdAndDocumentId(conn,
                                    submissionId, existingDoc.getId());
                  }
                  else
                  {
                    submissionDocument = new SubmissionDocument();
                  }
                  submissionDocument.setSubmissionId(submissionId);
                  submissionDocument.setDocumentId(document.getId());
                  if(doUpdate)
                  {
                    submissionDocument.update(conn);
                  }
                  else
                  {
                    submissionDocument.insert(conn);
                  }
                  documentAdded = true;
                }
              }
            }
            finally
            {
              WetoUtilities.deleteRecursively(workDir);
            }
          }
          else
          {
            Document document;
            Document existingDoc = null;
            if(documentFile == null)
            {
              document = DocumentModel.storeDocument(conn, File.createTempFile(
                      "empty", "txt"), documentFileFileName);
            }
            else
            {
              if(overWriteExisting)
              {
                ArrayList<Document> existingDocs = Document
                        .selectBySubmissionIdAndFileName(conn, submissionId,
                                documentFileFileName);
                if(!existingDocs.isEmpty())
                {
                  existingDoc = existingDocs.get(0);
                  for(int i = 1; i < existingDocs.size(); ++i)
                  {
                    Integer removeDocId = existingDocs.get(i).getId();
                    // Delete submissionDocument.
                    SubmissionDocument.select1BySubmissionIdAndDocumentId(conn,
                            taskId, removeDocId).delete(conn);
                    DocumentModel.deleteDocument(conn, Document
                            .select1ById(conn, removeDocId));
                  }
                }
              }
              document = DocumentModel.replaceDocument(conn, documentFile,
                      documentFileFileName, existingDoc);
            }
            boolean doUpdate = false;
            SubmissionDocument submissionDocument;
            if(existingDoc != null)
            {
              doUpdate = true;
              submissionDocument = SubmissionDocument
                      .select1BySubmissionIdAndDocumentId(conn, submissionId,
                              existingDoc.getId());
            }
            else
            {
              submissionDocument = new SubmissionDocument();
            }
            submissionDocument.setSubmissionId(submissionId);
            submissionDocument.setDocumentId(document.getId());
            if(doUpdate)
            {
              submissionDocument.update(conn);
            }
            else
            {
              submissionDocument.insert(conn);
            }
            documentAdded = true;
          }
          if(documentAdded)
          { // Clear submission status.
            SubmissionModel.clearAutoGrading(conn, submission,
                    SubmissionStatus.NOT_SUBMITTED, true);

          }
        }
        // Get the list of existing documents for this submission.
        documents = Document.selectBySubmissionId(conn, submissionId);
        // Update the number of files for this submission, if necessary.
        Integer fileCount = documents.size();
        if(!fileCount.equals(submission.getFileCount()))
        {
          submission.setFileCount(fileCount);
          submission.update(conn, false);
        }
        // Mark documents that have also a newer document with duplicate file name.
        duplicates = new boolean[documents.size()];
        for(int i = 1; i < documents.size(); ++i)
        {
          if(documents.get(i).getFileName().equals(documents.get(i - 1)
                  .getFileName()))
          {
            duplicates[i - 1] = true;
          }
        }
        if(submission.getStatus().equals(SubmissionStatus.PROCESSING.getValue()))
        {
          queuePos = AutoGradeJobQueue.getQueuePos(conn, submissionId);
        }
        ArrayList<Tag> fullErrors = Tag.selectByTaggedIdAndAuthorIdAndType(conn,
                taskId, userId, TagType.COMPILER_RESULT.getValue());
        if(!fullErrors.isEmpty())
        {
          Tag fullError = fullErrors.get(0);
          if(fullError.getRank().equals(submissionId))
          {
            compilerResultId = fullError.getId();
          }
        }
        testScores = AutoGradeTestScore.selectBySubmissionId(conn, submissionId);
        fullFeedbackIds = new Integer[testScores.size()];
        ArrayList<Tag> fullFeedback = Tag.selectByTaggedIdAndType(conn,
                submissionId, TagType.FEEDBACK.getValue());
        for(Tag f : fullFeedback)
        {
          fullFeedbackIds[f.getRank() - 1] = f.getId();
        }
      }
      catch(NoSuchItemException e)
      {
        //Load defaults
        properties = "";
        try(BufferedReader in = new BufferedReader(new InputStreamReader(
                View.class.getResourceAsStream("AutoGrading.properties"))))
        {
          String str;
          while((str = in.readLine()) != null)
          {
            properties += str + "\n";
          }
        }
        autoGraded = false;
        task.setIsAutoGraded(false);
        task.update(conn);
      }
      return SUCCESS;
    }

    public String getProperties()
    {
      return properties;
    }

    public Document getAutoGradingDocument()
    {
      return autoGradingDocument;
    }

    public boolean isAutoGraded()
    {
      return autoGraded;
    }

    public Map<Integer, String> getSubmissionStates()
    {
      return submissionStates;
    }

    public Map<Integer, String> getSubmissionErrors()
    {
      return submissionErrors;
    }

    public Submission getSubmission()
    {
      return submission;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public ArrayList<Document> getDocuments()
    {
      return documents;
    }

    public boolean[] getDuplicates()
    {
      return duplicates;
    }

    public Integer getQueuePos()
    {
      return queuePos;
    }

    public ArrayList<AutoGradeTestScore> getTestScores()
    {
      return testScores;
    }

    public Integer getCompilerResultId()
    {
      return compilerResultId;
    }

    public Integer[] getFullFeedbackIds()
    {
      return fullFeedbackIds;
    }

    public void setDocumentFile(File documentFile)
    {
      this.documentFile = documentFile;
    }

    public void setDocumentFileFileName(String documentFileFileName)
    {
      this.documentFileFileName = documentFileFileName;
    }

    public void setDocumentFileContentType(String documentFileContentType)
    {
    }

    public boolean isAllowZipping()
    {
      return allowZipping;
    }

    public void setAllowZipping(boolean allowZipping)
    {
      this.allowZipping = allowZipping;
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

  public static class Save extends WetoTeacherAction
  {
    private File document;
    private String documentFileName;
    private String properties;

    public Save()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), Tab.GRADING.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Document newDocument = null;
      if(document != null && document.length() != 0)
      {
        if(documentFileName.endsWith(".zip"))
        {
          newDocument = DocumentModel.storeDocument(conn, document,
                  documentFileName);
        }
        else
        {
          addActionError(getText("autograding.error.notZipFile"));
        }
      }
      try
      {
        // Try to update existing autogradetask.
        AutoGrading autoGrading = AutoGrading.select1ByTaskId(conn, taskId);
        // Retrieve currently linked document from the database.
        Document currentDocument = null;
        if(autoGrading.getTestDocId() != null)
        {
          currentDocument = Document.select1ById(conn, autoGrading
                  .getTestDocId());
        }
        // Update properties.
        autoGrading.setProperties(properties);
        if(newDocument != null)
        {
          autoGrading.setTestDocId(newDocument.getId());
        }
        autoGrading.update(conn);
        // Delete old document.
        if(newDocument != null && currentDocument != null)
        {
          DocumentModel.deleteDocument(conn, currentDocument);
        }
      }
      catch(NoSuchItemException e)
      {
        // There is no existing autogradetask. Create new one.
        AutoGrading autoGrading = new AutoGrading();
        autoGrading.setProperties(properties);
        autoGrading.setTaskId(taskId);
        if(newDocument != null)
        {
          autoGrading.setTestDocId(newDocument.getId());
        }
        autoGrading.insert(conn);
      }
      Task task = getTask();
      if(!task.getIsAutoGraded())
      {
        task.setIsAutoGraded(true);
        task.update(conn);
      }
      addActionMessage(getText("autograding.message.saveSuccess"));
      return SUCCESS;
    }

    public void setDocument(File document)
    {
      this.document = document;
    }

    public void setDocumentFileName(String documentFileName)
    {
      this.documentFileName = documentFileName;
    }

    public void setProperties(String properties)
    {
      this.properties = properties;
    }

    public void setDocumentContentType(String documentContentType)
    {
    }

  }

  public static class Delete extends WetoTeacherAction
  {
    public Delete()
    {
      super(Tab.GRADING.getBit(), 0, 0, Tab.GRADING.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      AutoGrading.deleteByTaskId(conn, taskId);
      AutoGradeJobQueue.deleteByTaskId(conn, taskId);
      for(Submission submission : Submission.selectByTaskId(conn, taskId))
      {
        AutoGradeTestScore.deleteBySubmissionId(conn, submission.getId());
        for(Tag feedback : Tag.selectByTaggedIdAndType(conn, submission.getId(),
                TagType.FEEDBACK.getValue()))
        {
          feedback.delete(conn);
        }
        if(userId.equals(submission.getUserId()))
        {
          submission.delete(conn);
        }
      }
      Tag.deleteByTaggedIdAndType(conn, taskId, TagType.COMPILER_RESULT
              .getValue());
      Task task = getTask();
      task.setIsAutoGraded(false);
      task.update(conn);
      addActionMessage(getText("autograding.message.deleteSuccess"));
      return SUCCESS;
    }

  }

  public static class Download extends WetoTeacherAction
  {
    private InputStream documentStream;
    private String contentType;
    private int contentLength;
    private Document document;
    private final int bufferSize = 8192;

    public Download()
    {
      super(Tab.GRADING.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      // Retrieve and output document.
      AutoGrading autoGrading = AutoGrading.select1ByTaskId(conn, getTaskId());
      document = Document.select1ById(conn, autoGrading.getTestDocId());
      InputStream dbStream = new DbInputStream(getDbSession(), DocumentModel
              .getDocumentInputStream(conn, document));
      contentType = document.getMimeType();
      contentLength = document.getFileSize();
      documentStream = dbStream;
      // Prevent WetoCourseAction from committing by setting committed to true:
      // DbInputStream will handle it.
      setCommitted(true);
      return SUCCESS;
    }

    public InputStream getDocumentStream()
    {
      return documentStream;
    }

    public String getContentType()
    {
      return contentType + ";charset=UTF-8";
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

}
