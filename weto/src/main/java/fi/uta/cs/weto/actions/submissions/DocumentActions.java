package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionDocument;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.DbInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

public class DocumentActions
{
  private static final HashMap<String, String> textTypeMap;

  static
  {
    textTypeMap = new HashMap<>();
    textTypeMap.put("cpp", "c_cpp");
    textTypeMap.put("hpp", "c_cpp");
    textTypeMap.put("c", "c_cpp");
    textTypeMap.put("h", "c_cpp");
    textTypeMap.put("java", "java");
    textTypeMap.put("hs", "haskell");
    textTypeMap.put("xml", "xml");
    textTypeMap.put("tex", "latex");
    textTypeMap.put("sql", "sql");
    textTypeMap.put("php", "php");
    textTypeMap.put("js", "javascript");
    textTypeMap.put("html", "html");
    textTypeMap.put("htm", "html");
    textTypeMap.put("css", "css");
    textTypeMap.put("py", "python");
    textTypeMap.put("py2", "python");
    textTypeMap.put("py3", "python");
  }

  public static class EditText extends WetoCourseAction
  {
    private Integer documentId;
    private Integer submissionId;
    private String documentText;
    private String fileName;
    private String fileSize;
    private String fileTimeStamp;
    private boolean updateable;
    private String textType;

    public EditText()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      // Retrieve submission document id from the link from the database.
      submissionId = SubmissionDocument.select1ByDocumentId(conn, documentId)
              .getSubmissionId();
      // Retrieve submission.
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to view/edit the submission.
      // If the user is a student but not the submitter, check submission group.
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, getTaskId());
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
              .isStudent(), ignoreGroups);
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ACCESS_DENIED);
      }
      updateable = haveUpdateRights(Tab.SUBMISSIONS.getBit(), ownSubmission,
              true);
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userIP,
              userId, submission.getTaskId(), PermissionType.SUBMISSION,
              getNavigator().isTeacher());
      if(PermissionModel.checkTimeStampLimits(limits) != PermissionModel.CURRENT)
      {
        updateable = false;
      }
      // Retrieve and output document.
      Document document = Document.select1ById(conn, documentId);
      documentText = DocumentModel.readTextDocument(conn, document);
      fileName = document.getFileName();
      fileSize = document.getContentFileSize().toString();
      fileTimeStamp = document.getTimeStampString();
      int i = fileName.lastIndexOf('.');
      String suffix = (i >= 0) ? fileName.substring(i + 1) : null;
      textType = textTypeMap.get(suffix);
      if(textType == null)
      {
        textType = "text";
      }
      return SUCCESS;
    }

    public void setDocumentId(Integer documentId)
    {
      this.documentId = documentId;
    }

    public Integer getDocumentId()
    {
      return documentId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public String getDocumentText()
    {
      return documentText;
    }

    public String getFileName()
    {
      return fileName;
    }

    public String getFileSize()
    {
      return fileSize;
    }

    public String getFileTimeStamp()
    {
      return fileTimeStamp;
    }

    public boolean isUpdateable()
    {
      return updateable;
    }

    public String getTextType()
    {
      return textType;
    }

    @Override
    public String getPageTitle()
    {
      return fileName + " (" + super.getPageTitle() + ")";
    }

  }

  public static class SaveText extends WetoCourseAction
  {
    private Integer documentId;
    private Integer submissionId;
    private String documentText;
    private InputStream messageStream;

    public SaveText()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      // Retrieve submission document id from the link from the database.
      submissionId = SubmissionDocument.select1ByDocumentId(conn, documentId)
              .getSubmissionId();
      // Retrieve submission.
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to edit the submission.
      // If the user is a student but not the submitter, check submission group.
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, getTaskId());
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
              .isStudent(), ignoreGroups);
      if(!haveUpdateRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ACCESS_DENIED);
      }
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userIP,
              userId, submission.getTaskId(), PermissionType.SUBMISSION,
              getNavigator().isTeacher());
      if(PermissionModel.checkTimeStampLimits(limits)
              != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Retrieve and update document.
      Document document = Document.select1ById(conn, documentId);
      DocumentModel.updateTextDocument(conn, document, documentText);
      SubmissionModel.clearAutoGrading(conn, submission,
              SubmissionStatus.NOT_SUBMITTED, true);
      String msg = new WetoTimeStamp().toString() + ";" + document
              .getContentFileSize().toString() + ";" + documentText;
      messageStream = new ByteArrayInputStream(msg.getBytes("UTF-8"));
      addActionMessage(getText("submissions.message.documentSaved"));
      // Add the save submission document event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), submission.getTaskId(), userId,
                LogEvent.SAVE_SUBMISSION_DOCUMENT.getValue(), submissionId,
                document.getId(), getRequest().getRemoteAddr()).insert(conn);
      }
      return SUCCESS;
    }

    public void setDocumentId(Integer documentId)
    {
      this.documentId = documentId;
    }

    public Integer getDocumentId()
    {
      return documentId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public void setDocumentText(String documentText)
    {
      this.documentText = (documentText != null) ? documentText.replaceAll(
              "\r\n", "\n") : null;
    }

    public InputStream getMessageStream()
    {
      return messageStream;
    }

  }

  public static class Delete extends WetoCourseAction
  {
    private Integer documentId;
    private Integer submissionId;
    private String documentText;
    private String fileName;
    private String fileSize;
    private String fileTimeStamp;
    private String textType;

    public Delete()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      // Retrieve submission document id from the link from the database.
      submissionId = SubmissionDocument.select1ByDocumentId(conn, documentId)
              .getSubmissionId();
      // Retrieve submission.
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to edit the submission.
      // If the user is a student but not the submitter, check submission group.
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, getTaskId());
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
              .isStudent(), ignoreGroups);
      if(!haveUpdateRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ACCESS_DENIED);
      }
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userIP,
              userId, submission.getTaskId(), PermissionType.SUBMISSION,
              getNavigator().isTeacher());
      if(PermissionModel.checkTimeStampLimits(limits)
              != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Retrieve and output document.
      Document document = Document.select1ById(conn, documentId);
      fileName = document.getFileName();
      fileSize = document.getContentFileSize().toString();
      fileTimeStamp = document.getTimeStampString();
      if(document.getContentMimeType().startsWith("text"))
      {
        documentText = DocumentModel.readTextDocument(conn, document);
        int i = fileName.lastIndexOf('.');
        String suffix = (i >= 0) ? fileName.substring(i + 1) : null;
        textType = textTypeMap.get(suffix);
        if(textType == null)
        {
          textType = "text";
        }
      }
      else
      {
        documentText = null;
        textType = null;
      }
      return SUCCESS;
    }

    public void setDocumentId(Integer documentId)
    {
      this.documentId = documentId;
    }

    public Integer getDocumentId()
    {
      return documentId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public String getDocumentText()
    {
      return documentText;
    }

    public String getFileName()
    {
      return fileName;
    }

    public String getFileSize()
    {
      return fileSize;
    }

    public String getFileTimeStamp()
    {
      return fileTimeStamp;
    }

    public String getTextType()
    {
      return textType;
    }

    @Override
    public String getPageTitle()
    {
      return fileName + " (" + super.getPageTitle() + ")";
    }

  }

  public static class CommitDelete extends WetoCourseAction
  {
    private Integer documentId;
    private Integer submissionId;

    public CommitDelete()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      // Retrieve submission document id from the link from the database.
      SubmissionDocument link = SubmissionDocument.select1ByDocumentId(conn,
              documentId);
      submissionId = link.getSubmissionId();
      // Retrieve submission.
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to edit the submission.
      // If the user is a student but not the submitter, check submission group.
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, getTaskId());
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
              .isStudent(), ignoreGroups);
      if(!haveUpdateRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ACCESS_DENIED);
      }
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userIP,
              userId, submission.getTaskId(), PermissionType.SUBMISSION,
              getNavigator().isTeacher());
      if(PermissionModel.checkTimeStampLimits(limits)
              != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Delete both the submission link and the document itself.
      link.delete(conn);
      Document document = Document.select1ById(conn, documentId);
      DocumentModel.deleteDocument(conn, document);
      SubmissionModel.clearAutoGrading(conn, submission,
              SubmissionStatus.NOT_SUBMITTED, true);
      // Update the number of files for this submission.
      submission.setFileCount(Document.selectBySubmissionId(conn, submissionId)
              .size());
      submission.update(conn, false);
      addActionMessage(getText("submissions.message.documentDeleted"));
      // Add the save submission document event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), submission.getTaskId(), userId,
                LogEvent.DELETE_SUBMISSION_DOCUMENT.getValue(), submissionId,
                document.getId(), getRequest().getRemoteAddr()).insert(conn);
      }
      return SUCCESS;
    }

    public void setDocumentId(Integer documentId)
    {
      this.documentId = documentId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

  }

  public static class Download extends WetoCourseAction
  {
    private Integer documentId;
    private InputStream documentStream;
    private String contentType;
    private int contentLength;
    private Document document;
    private final int bufferSize = 8192;

    public Download()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      // Retrieve submission document link from the database.
      SubmissionDocument submissionDocument = SubmissionDocument
              .select1ByDocumentId(conn, documentId);
      // Retrieve submission.
      Submission submission = Submission.select1ById(conn, submissionDocument
              .getSubmissionId());
      validateCourseSubtaskId(submission.getTaskId());
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to view the submission.
      // If the user is a student but not the submitter, check submission group.
      boolean ignoreGroups = false;
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, getTaskId());
        ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      }
      catch(NoSuchItemException e)
      {
      }
      ArrayList<Integer> submissionMemberIds = SubmissionModel
              .getSubmissionGroupMemberIds(conn, taskId, userId, ignoreGroups);
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              userId, submitterId, getNavigator().isStudent(), ignoreGroups,
              submissionMemberIds);
      // Check if there are grades for the current task and the owner of the
      // document. If there are, check if the current user is a reviewer trying
      // to review documents.
      ArrayList<Grade> grades = Grade.selectPeerVisibleByTaskIdAndReceiverId(
              conn, taskId, submitterId);
      boolean isReviewer = false;
      final String userIP = getNavigator().getUserIP();
      WetoTimeStamp[] gradingLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, submission.getTaskId(), PermissionType.GRADING);
      WetoTimeStamp[] challengeLimits = PermissionModel.getTimeStampLimits(conn,
              userIP, userId, submission.getTaskId(),
              PermissionType.GRADE_CHALLENGE);
      if((PermissionModel.checkTimeStampLimits(gradingLimits)
              == PermissionModel.CURRENT) || (PermissionModel
                      .checkTimeStampLimits(challengeLimits)
              == PermissionModel.CURRENT))
      {
        for(Grade grade : grades)
        {
          // Current user is a reviewer for this grade?
          if(SubmissionModel.checkSubmissionOwnership(userId, grade
                  .getReviewerId(), getNavigator().isStudent(), ignoreGroups,
                  submissionMemberIds))
          {
            isReviewer = true;
            break;
          }
        }
      }
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), ownSubmission || isReviewer,
              true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"));
      }
      // Retrieve and output document.
      document = Document.select1ById(conn, documentId);
      InputStream dbStream = new DbInputStream(getDbSession(), DocumentModel
              .getDocumentInputStream(conn, document));
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
      // Prevent WetoCourseAction from committing by setting committed to true:
      // DbInputStream will handle it.
      setCommitted(true);
      // Add the download document event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, LogEvent.DOWNLOAD_DOCUMENT
                .getValue(), document.getId(), null, getRequest()
                .getRemoteAddr()).insert(conn);
      }
      return SUCCESS;
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
      return "inline; filename=" + document.getFileName();
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
