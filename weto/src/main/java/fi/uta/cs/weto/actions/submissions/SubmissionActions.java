package fi.uta.cs.weto.actions.submissions;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.AutoGradeTestScore;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionDocument;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.FileSubmissionBean;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionError;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class SubmissionActions
{
  public static class View extends WetoCourseAction
  {
    private static final SubmissionStatus[] submissionStates = SubmissionStatus
            .values();
    private static final SubmissionError[] submissionErrors = SubmissionError
            .values();

    private Integer submissionId;
    private Submission submission;
    private String[] submissionPeriod;
    private boolean updateable;
    private String patternDescriptions;
    private boolean allowZipping;
    private boolean allowInlineFiles;
    private boolean overWriteExisting = true;
    private boolean allowTestRun;
    private File documentFile;
    private String documentFileFileName;
    private ArrayList<Document> documents;
    private boolean[] duplicates;
    private Task submissionTask;
    private UserTaskView submissionUser;
    private Integer queuePos;
    private ArrayList<AutoGradeTestScore> testScores;
    private Integer compilerResultId;
    private Integer[] fullFeedbackIds;
    private boolean deleteConfirm;
    private boolean quizAnswer;
    private ArrayList<Grade> grades;
    private boolean createGradeRights;
    private HashMap<Integer, UserTaskView> reviewerMap;
    private ArrayList<Integer> newDocumentIds;

    public View()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      try
      {
        submission = Submission.select1ById(conn, submissionId);
      }
      catch(NoSuchItemException e)
      {
        // Submission doesn't exist
        throw new WetoActionException(getText(
                "submissions.error.notFound"));
      }
      validateCourseSubtaskId(submission.getTaskId());
      Integer userId = getCourseUserId();
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      submissionTask = Task.select1ById(conn, submissionTaskId);
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
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
                      .isStudent(), ignoreGroups);
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"));
      }
      WetoTimeStamp[] submitLimits = PermissionModel.getTimeStampLimits(conn,
              userId, submissionTaskId, PermissionType.SUBMISSION,
              getNavigator().isTeacher());
      submissionPeriod = WetoTimeStamp.limitsToStrings(submitLimits);
      updateable = false;
      submissionUser = UserTaskView.select1ByTaskIdAndUserId(conn,
              submissionTaskId, submitterId);
      WetoTimeStamp[] gradingLimits = PermissionModel.getTimeStampLimits(conn,
              userId, submissionTaskId, PermissionType.GRADING, getNavigator()
                      .isTeacher());
      boolean gradingPeriodActive = (PermissionModel.checkTimeStampLimits(
              gradingLimits)
              == PermissionModel.CURRENT);
      WetoTimeStamp[] resultsLimits = PermissionModel.getTimeStampLimits(conn,
              userId, submissionTaskId, PermissionType.RESULTS);
      boolean resultsPeriodActive = (PermissionModel.checkTimeStampLimits(
              resultsLimits)
              == PermissionModel.CURRENT);
      boolean allGradesRights
                      = haveViewRights(Tab.GRADING.getBit(), false, true);
      if(allGradesRights)
      {
        grades = Grade.selectByTaskIdAndReceiverId(conn, submissionTaskId,
                submitterId);
        createGradeRights = (haveCreateRights(Tab.GRADING.getBit(), false, true)
                && gradingPeriodActive);
      }
      else if(haveViewRights(Tab.GRADING.getBit(), ownSubmission, false)
              && resultsPeriodActive)
      {
        grades = Grade.selectPeerVisibleByTaskIdAndReceiverId(conn,
                submissionTaskId, submitterId);
        createGradeRights = (haveCreateRights(Tab.GRADING.getBit(),
                ownSubmission, false) && gradingPeriodActive);
      }
      else
      {
        grades = new ArrayList<>();
        createGradeRights = false;
      }
      Collections.sort(grades, new Comparator<Grade>()
      {
        @Override
        public int compare(Grade a, Grade b)
        {
          return b.getTimeStamp().compareTo(a.getTimeStamp());
        }

      });
      // Create a map of visible (non-anonymous) reviewers
      reviewerMap = new HashMap<>();
      for(Grade grade : grades)
      {
        Integer reviewerId = grade.getReviewerId();
        if(reviewerId != null)
        {
          try
          {
            UserTaskView reviewer = UserTaskView.select1ByTaskIdAndUserId(conn,
                    submissionTaskId, reviewerId);
            // Students can't see peer-reviewers' names
            if(allGradesRights || !ClusterType.STUDENTS.getValue().equals(
                    reviewer.getClusterType()))
            {
              reviewerMap.put(reviewerId, reviewer);
            }
          }
          catch(NoSuchItemException e)
          {
          }
        }
      }
      String result;
      quizAnswer = SubmissionStatus.QUIZ_SUBMISSION.getValue().equals(submission
              .getStatus());
      if(quizAnswer)
      {
        result = viewQuizSubmission();
      }
      else
      {
        result = viewFileSubmission(conn, userId, submission, submissionTaskId,
                submitLimits, ownSubmission);
      }
      // Add the submission view event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), submissionTaskId, userId,
                LogEvent.VIEW_SUBMISSION.getValue(), submissionId, null,
                getRequest().getRemoteAddr()).insert(conn);
      }
      return result;
    }

    private String viewQuizSubmission()
            throws WetoActionException, SQLException, WetoTimeStampException,
                   NoSuchItemException
    {
      updateable = false;
      return SUCCESS;
    }

    private String viewFileSubmission(Connection conn, Integer userId,
            Submission submission, Integer submissionTaskId,
            WetoTimeStamp[] timeLimits, boolean ownSubmission)
            throws WetoActionException, SQLException, WetoTimeStampException,
                   NoSuchItemException, IOException, InvalidValueException,
                   ObjectNotValidException, TooManyItemsException
    {
      updateable = haveUpdateRights(Tab.SUBMISSIONS.getBit(), ownSubmission,
              true);
      if(PermissionModel.checkTimeStampLimits(timeLimits)
              != PermissionModel.CURRENT)
      {
        updateable = false;
      }
      // Fetch submission properties for this task.
      SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
              submissionTaskId);
      Properties properties = new Properties();
      properties.load(new StringReader(sp.getProperties()));
      String filePatterns = properties.getProperty("filePatterns");
      patternDescriptions = properties.getProperty("patternDescriptions");
      if((patternDescriptions == null) || patternDescriptions.isEmpty())
      {
        patternDescriptions = filePatterns;
      }
      allowZipping = Boolean.valueOf(properties.getProperty("allowZipping"));
      allowInlineFiles = Boolean.valueOf(properties.getProperty(
              "allowInlineFiles"));
      allowTestRun = Boolean.valueOf(properties.getProperty("allowTestRun"));
      // Check whether the submission is editable (updateable) and the user is
      // trying to add a new file (document) to the submission.
      // A document can be added only if file name patterns have been defined.
      if(updateable && (documentFileFileName != null) && (filePatterns != null))
      {
        boolean caseInsensitive = Boolean.valueOf(properties.getProperty(
                "caseInsensitive"));
        Object[] addResult = SubmissionModel.addFileToSubmission(conn,
                submission, documentFile, documentFileFileName, filePatterns,
                caseInsensitive, allowZipping, allowInlineFiles,
                overWriteExisting);
        newDocumentIds = (ArrayList<Integer>) addResult[0];
        ArrayList<String> excludedFiles = (ArrayList<String>) addResult[1];
        // Add the save submission document event(s) to the log.
        if(!getNavigator().isStudentRole())
        {
          for(Integer newDocId : newDocumentIds)
          {
            new Log(getCourseTaskId(), submissionTaskId, userId,
                    LogEvent.SAVE_SUBMISSION_DOCUMENT.getValue(), submissionId,
                    newDocId, getRequest().getRemoteAddr()).insert(conn);
          }
        }
        if(!excludedFiles.isEmpty())
        {
          StringBuilder sb = new StringBuilder();
          for(String fileName : excludedFiles)
          {
            sb.append(" ").append(fileName);
          }
          addActionError(getText("submissions.error.fileName", new String[]
          {
            sb.toString()
          }));
        }
      }
      // Get a FileSubmissionBean for this submission and unpack data from it.
      FileSubmissionBean fsb = SubmissionModel.getFileSubmissionBean(conn,
              submission, submissionTask, getNavigator().isStudent());
      documents = fsb.getDocuments();
      duplicates = fsb.getDuplicates();
      queuePos = fsb.getQueuePos();
      testScores = fsb.getTestScores();
      compilerResultId = fsb.getCompilerResultId();
      fullFeedbackIds = fsb.getFullFeedbackIds();
      return SUCCESS;
    }

    public SubmissionStatus[] getSubmissionStates()
    {
      return submissionStates;
    }

    public SubmissionError[] getSubmissionErrors()
    {
      return submissionErrors;
    }

    public String getPatternDescriptions()
    {
      return patternDescriptions;
    }

    public boolean isAllowZipping()
    {
      return allowZipping;
    }

    public boolean isAllowInlineFiles()
    {
      return allowInlineFiles;
    }

    public void setOverWriteExisting(boolean overWriteExisting)
    {
      this.overWriteExisting = overWriteExisting;
    }

    public boolean isAllowTestRun()
    {
      return allowTestRun;
    }

    public Submission getSubmission()
    {
      return submission;
    }

    public void setSubmissionId(Integer submissionId)
    {
      this.submissionId = submissionId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public String[] getSubmissionPeriod()
    {
      return submissionPeriod;
    }

    public boolean isUpdateable()
    {
      return updateable;
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

    public ArrayList<Document> getDocuments()
    {
      return documents;
    }

    public boolean[] getDuplicates()
    {
      return duplicates;
    }

    public Task getSubmissionTask()
    {
      return submissionTask;
    }

    public UserTaskView getSubmissionUser()
    {
      return submissionUser;
    }

    public Integer getQueuePos()
    {
      return queuePos;
    }

    public Integer getCompilerResultId()
    {
      return compilerResultId;
    }

    public ArrayList<AutoGradeTestScore> getTestScores()
    {
      return testScores;
    }

    public Integer[] getFullFeedbackIds()
    {
      return fullFeedbackIds;
    }

    public void setDeleteConfirm(boolean deleteConfirm)
    {
      this.deleteConfirm = deleteConfirm;
    }

    public boolean isDeleteConfirm()
    {
      return deleteConfirm;
    }

    public boolean isQuizAnswer()
    {
      return quizAnswer;
    }

    public Date getCurrentTime()
    {
      return new Date();
    }

    public Integer getAnchorId()
    {
      return submissionUser.getUserId();
    }

    public ArrayList<Grade> getGrades()
    {
      return grades;
    }

    public boolean isCreateGradeRights()
    {
      return createGradeRights;
    }

    public HashMap<Integer, UserTaskView> getReviewerMap()
    {
      return reviewerMap;
    }

    public List<Integer> getNewDocumentIds()
    {
      return this.newDocumentIds;
    }

  }

  // A simplified version of the 'ViewFileSubmission'-method above.
  // Used for ajax-uploading files to submissions
  public static class AddSubmissionFile extends WetoCourseAction
  {
    // Form data
    private Integer submissionId;
    private File documentFile;
    private String documentFileFileName;
    private boolean overWriteExisting = true;

    public void setSubmissionId(Integer submissionId)
    {
      this.submissionId = submissionId;
    }

    public void setDocumentFile(File documentFile)
    {
      this.documentFile = documentFile;
    }

    public void setDocumentFileFileName(String documentFileFileName)
    {
      this.documentFileFileName = documentFileFileName;
    }

    public void setOverWriteExisting(boolean overWriteExisting)
    {
      this.overWriteExisting = overWriteExisting;
    }

    // Response data
    private ArrayList<Integer> newDocumentIds;
    private ArrayList<String> excludedFiles;

    public ArrayList<Integer> getNewDocumentIds()
    {
      return newDocumentIds;
    }

    public ArrayList<String> getExcludedFiles()
    {
      return excludedFiles;
    }

    public AddSubmissionFile()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer userId = getCourseUserId();
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
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
                      .isStudent(), ignoreGroups);
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ERROR);
      }
      // Check submission time limits
      WetoTimeStamp[] submitLimits = PermissionModel.getTimeStampLimits(conn,
              userId, submissionTaskId, PermissionType.SUBMISSION,
              getNavigator().isTeacher());
      boolean updateable = haveUpdateRights(Tab.SUBMISSIONS.getBit(),
              ownSubmission, true);
      if(PermissionModel.checkTimeStampLimits(submitLimits)
              != PermissionModel.CURRENT)
      {
        updateable = false;
      }
      // Add the file only if the user has permissions to update the submission.
      if(updateable && (documentFileFileName != null))
      {
        // Fetch submission properties for this task.
        SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
                submissionTaskId);
        Properties properties = new Properties();
        properties.load(new StringReader(sp.getProperties()));
        String filePatterns = properties.getProperty("filePatterns");
        boolean allowZipping = Boolean.valueOf(properties.getProperty(
                "allowZipping"));
        boolean allowInlineFiles = Boolean.valueOf(properties.getProperty(
                "allowInlineFiles"));
        boolean caseInsensitive = Boolean.valueOf(properties.getProperty(
                "caseInsensitive"));
        Object[] addResult = SubmissionModel.addFileToSubmission(conn,
                submission, documentFile, documentFileFileName, filePatterns,
                caseInsensitive, allowZipping, allowInlineFiles,
                overWriteExisting);
        newDocumentIds = (ArrayList<Integer>) addResult[0];
        excludedFiles = (ArrayList<String>) addResult[1];
        // Add the save submission document event(s) to the log.
        if(!getNavigator().isStudentRole())
        {
          for(Integer newDocId : newDocumentIds)
          {
            new Log(getCourseTaskId(), submissionTaskId, userId,
                    LogEvent.SAVE_SUBMISSION_DOCUMENT.getValue(), submissionId,
                    newDocId, getRequest().getRemoteAddr()).insert(conn);
          }
        }
      }
      else
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"), ERROR);
      }
      return SUCCESS;
    }

  }

  // Action class for getting autograding information regarding a submission
  public static class GetJSONAutoGrading extends WetoCourseAction
  {
    public class JSONAutoGradingScore
    {
      private final String feedback;
      private final Integer phase;
      private final Integer processingTime;
      private final Integer testScore;
      private final Integer testNo;

      public JSONAutoGradingScore(String feedback, Integer phase,
              Integer processingTime, Integer testScore, Integer testNo)
      {
        this.feedback = feedback;
        this.phase = phase;
        this.processingTime = processingTime;
        this.testScore = testScore;
        this.testNo = testNo;
      }

      public String getFeedback()
      {
        return feedback;
      }

      public Integer getPhase()
      {
        return phase;
      }

      public Integer getProcessingTime()
      {
        return processingTime;
      }

      public Integer getTestScore()
      {
        return testScore;
      }

      public Integer getTestNo()
      {
        return testNo;
      }

    }

    private Integer submissionId;
    private Integer compilerResultId;
    private List<JSONAutoGradingScore> testScores;
    private Integer[] fullFeedbackIds;

    public Integer getCompilerResultId()
    {
      return compilerResultId;
    }

    public List<JSONAutoGradingScore> getTestScores()
    {
      return testScores;
    }

    public Integer[] getFullFeedbackIds()
    {
      return fullFeedbackIds;
    }

    public void setSubmissionId(int submissionId)
    {
      this.submissionId = submissionId;
    }

    public GetJSONAutoGrading()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    public String action()
            throws WetoActionException, InvalidValueException,
                   NoSuchItemException, SQLException
    {
      int taskId = getTaskId();
      Connection conn = getCourseConnection();
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      ArrayList<Tag> fullErrors = Tag.selectByTaggedIdAndAuthorIdAndType(conn,
              taskId, getCourseUserId(), TagType.COMPILER_RESULT.getValue());
      ArrayList<AutoGradeTestScore> complexScores = AutoGradeTestScore
              .selectBySubmissionId(conn, submissionId);
      ArrayList<Tag> fullFeedback = Tag.selectByTaggedIdAndType(conn,
              submissionId, TagType.FEEDBACK.getValue());
      if(!fullErrors.isEmpty())
      {
        Tag fullError = fullErrors.get(0);
        if(fullError.getRank().equals(submissionId))
        {
          compilerResultId = fullError.getId();
        }
      }
      String incorrectMsg = getText("autograding.message.incorrectResult");
      fullFeedbackIds = new Integer[complexScores.size()];
      for(Tag f : fullFeedback)
      {
        int rank = f.getRank() - 1;
        fullFeedbackIds[(rank < 0) ? 0 : rank] = f.getId();
      }
      if(getNavigator().isStudent())
      {
        int i = 0;
        for(AutoGradeTestScore agts : complexScores)
        {
          String res = agts.getFeedback();
          if(agts.isPrivateScore() && (!res.isEmpty() && ((res.charAt(0)
                  == '+') || (res.charAt(0) == '-') || (res.charAt(0) == ' '))))
          {
            agts.setFeedback(incorrectMsg);
            fullFeedbackIds[i] = null;
          }
          i += 1;
        }
      }
      // Simplify testscores for JSON response
      this.testScores = new ArrayList<>();
      for(AutoGradeTestScore score : complexScores)
      {
        JSONAutoGradingScore simpleScore = new JSONAutoGradingScore(score
                .getFeedback(), score.getPhase(), score.getProcessingTime(),
                score.getTestScore(), score.getTestNo());
        this.testScores.add(simpleScore);
      }
      return SUCCESS;
    }

  }

  // Action class for getting documents related to a submissions. Used to
  // dynamically update the uploaded documents table.
  public static class GetJSONDocuments extends WetoCourseAction
  {
    public class JSONDocument
    {
      private final String fileName;
      private final int fileSize;
      private final String fileDate;
      private final int contentId;
      private final String contentMimeType;
      private final int id;
      private boolean duplicate;

      public JSONDocument(int id, String fileName, int fileSize, String fileDate,
              String contentMimeType, int contentId, boolean duplicate)
      {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
        this.contentId = contentId;
        this.contentMimeType = contentMimeType;
        this.duplicate = duplicate;
      }

      public String getFileName()
      {
        return fileName;
      }

      public int getFileSize()
      {
        return fileSize;
      }

      public String getFileDate()
      {
        return fileDate;
      }

      public int getContentId()
      {
        return contentId;
      }

      public String getContentMimeType()
      {
        return contentMimeType;
      }

      public int getId()
      {
        return id;
      }

      public boolean getDuplicate()
      {
        return duplicate;
      }

    }

    private Integer submissionId;
    private List<JSONDocument> documents;

    public GetJSONDocuments()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer userId = getCourseUserId();
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
      final boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(
              conn, submissionTaskId, userId, submitterId, getNavigator()
                      .isStudent(), ignoreGroups);
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"),
                ERROR);
      }
      documents = new ArrayList<>();
      JSONDocument addedJSONDoc = null;
      for(Document doc : Document.selectBySubmissionId(conn, submissionId))
      { // Mark possible duplicate filenames.
        if((addedJSONDoc != null) && addedJSONDoc.getFileName().equals(doc
                .getFileName()))
        {
          addedJSONDoc.duplicate = true;
        }
        addedJSONDoc = new JSONDocument(doc.getId(), doc.getFileName(), doc
                .getContentFileSize(), doc.getTimeStampString(), doc
                .getContentMimeType(), doc.getContentId(), false);
        documents.add(addedJSONDoc);
      }
      return SUCCESS;
    }

    public void setSubmissionId(Integer submissionId)
    {
      this.submissionId = submissionId;
    }

    public List<JSONDocument> getDocuments()
    {
      return documents;
    }

  }

  public static class Add extends WetoCourseAction
  {
    private Integer submissionId;
    private Integer submitterId;
    private Integer submissionQuota;

    public Add()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, Tab.SUBMISSIONS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      // Verify that the logged user has rights to create a submission.
      if(!haveCreateRights(Tab.SUBMISSIONS.getBit(), userId.equals(submitterId),
              true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"));
      }
      // Check that there are no previous submissions in a 'not submitted' state
      if(!Submission.selectByUserIdAndTaskIdAndStatus(conn, submitterId, taskId,
              SubmissionStatus.NOT_SUBMITTED.getValue()).isEmpty())
      {
        throw new WetoActionException(getText(
                "submissions.error.incompleteSubmission"));
      }
      // Check time permissions for the submission.
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userId,
              taskId, PermissionType.SUBMISSION, getNavigator().isTeacher());
      int checkValue = PermissionModel.checkTimeStampLimits(limits);
      if(checkValue != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Delete older submissions, if quota was exceeded
      SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
              taskId);
      Properties properties = new Properties();
      properties.load(new StringReader(sp.getProperties()));
      try
      { // Check that creating a new submission does not violate the quota limit.
        submissionQuota = Integer.valueOf(properties.getProperty(
                "oldSubmissionLimit"));
        ArrayList<Submission> submissions = Submission.selectByUserIdAndTaskId(
                conn, submitterId, taskId);
        if(submissions.size() > submissionQuota)
        { // The user already has the maximum allowed number
          // of submissions, so a new submission is not created.
          throw new WetoActionException(getText(
                  "submissions.header.submissionQuotaFull", new String[]
                  {
                    Integer.toString(submissions.size())
                  }));
        }
      }
      catch(NumberFormatException e)
      { // No limit specified: quota is unlimited.
      }
      // Create submission.
      Submission submission = new Submission();
      submission.setTaskId(taskId);
      submission.setUserId(submitterId);
      submission.setStatus(SubmissionStatus.NOT_SUBMITTED.getValue());
      submission.insert(conn);
      submissionId = submission.getId();
      // Add the create submission event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, LogEvent.CREATE_SUBMISSION
                .getValue(), submissionId, null, getRequest().getRemoteAddr())
                .insert(conn);
      }
      return SUCCESS;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public void setSubmitterId(Integer submitterId)
    {
      this.submitterId = submitterId;
    }

    public Integer getSubmissionQuota()
    {
      return submissionQuota;
    }

  }

  public static class Complete extends WetoCourseAction
  {
    private Integer submissionId;
    private boolean isTestRun;
    private Integer testNumber;

    public Complete()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, Tab.SUBMISSIONS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      // Retrieve submission from database.
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Verify that the logged user has rights to create a submission.
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
      if(!haveCreateRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"));
      }
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userId,
              taskId, PermissionType.SUBMISSION, getNavigator().isTeacher());
      int checkValue = PermissionModel.checkTimeStampLimits(limits);
      if(checkValue != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      // Check that submission is in the not submitted or not accepted state.
      if(!(submission.getStatus().equals(SubmissionStatus.NOT_SUBMITTED
              .getValue()) || submission.getStatus().equals(
                      SubmissionStatus.NOT_ACCEPTED.getValue())))
      {
        throw new WetoActionException(getText(
                "submissions.error.alreadySubmitted"));
      }
      if(SubmissionDocument.selectBySubmissionId(conn, submission.getId())
              .isEmpty())
      {
        throw new WetoActionException(getText("error.emptySubmission"));
      }
      // Update submission status.
      // If autogradetask exist, insert documents to autoGradeJobQueue.
      if(getTask().getIsAutoGraded())
      {
        SubmissionModel.clearAutoGrading(conn, submission,
                SubmissionStatus.PROCESSING, false);
        submission.setStatus(SubmissionStatus.PROCESSING.getValue());
        AutoGradeJobQueue job = new AutoGradeJobQueue();
        job.setTaskId(taskId);
        job.setDbId(getDbId());
        job.setRefId(submission.getId());
        if(isTestRun)
        {
          SubmissionProperties sp = SubmissionProperties.select1ByTaskId(conn,
                  taskId);
          Properties properties = new Properties();
          properties.load(new StringReader(sp.getProperties()));
          if(Boolean.valueOf(properties.getProperty("allowTestRun")))
          {
            job.setQueuePhase(AutoGradeJobQueue.TEST_RUN);
            if(testNumber != null)
            {
              job.setJobComment(testNumber.toString());
            }
          }
          else
          {
            throw new WetoActionException(getText("general.error.accessDenied"));
          }
        }
        else
        {
          job.setQueuePhase(AutoGradeJobQueue.IMMEDIATE_PUBLIC);
        }
        job.setTestRunning(false);
        job.insert(conn);
      }
      else // Non-autograded submission is set to "accepted" by default.
      {
        submission.setStatus(SubmissionStatus.ACCEPTED.getValue());
      }
      submission.update(conn);
      // Add the submit submission event to the log.
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, LogEvent.SUBMIT_SUBMISSION
                .getValue(), submissionId, null, getRequest().getRemoteAddr())
                .insert(conn);
      }
      addActionMessage(getText("submissions.message.createSuccess"));
      return SUCCESS;
    }

    public void setSubmissionId(Integer submissionId)
    {
      this.submissionId = submissionId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public void setIsTestRun(boolean isTestRun)
    {
      this.isTestRun = isTestRun;
    }

    public void setTestNumber(Integer testNumber)
    {
      this.testNumber = testNumber;
    }

  }

  public static class CommitDelete extends WetoCourseAction
  {
    private Integer submissionId;
    private Integer submitterId;
    private boolean submitted;

    public CommitDelete()
    {
      super(Tab.SUBMISSIONS.getBit(), 0, 0, Tab.SUBMISSIONS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      submitterId = submission.getUserId();
      Integer submitterId = submission.getUserId();
      Integer submissionTaskId = submission.getTaskId();
      // Check that the logged user has the right to delete the submission.
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
      boolean ownSubmission = SubmissionModel.checkSubmissionOwnership(conn,
              submissionTaskId, userId, submitterId, getNavigator().isStudent(),
              ignoreGroups);
      if(!haveDeleteRights(Tab.SUBMISSIONS.getBit(), ownSubmission, true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"));
      }
      // Check permissions for the submission.
      WetoTimeStamp[] limits = PermissionModel.getTimeStampLimits(conn, userId,
              taskId, PermissionType.SUBMISSION, getNavigator().isTeacher());
      int checkValue = PermissionModel.checkTimeStampLimits(limits);
      if(checkValue != PermissionModel.CURRENT)
      {
        throw new WetoActionException(getText(
                "general.error.timePeriodNotActive"));
      }
      if(submitted == false)
      {
        throw new WetoActionException("", INPUT);
      }
      // Delete submission.
      SubmissionModel.deleteSubmission(conn, submission);
      // Add the delete submission event to the log.
      if(false && !getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, LogEvent.DELETE_SUBMISSION
                .getValue(), submissionId, null, getRequest().getRemoteAddr())
                .insert(conn);
      }
      addActionMessage(getText("submissions.message.deleteSuccess"));
      return SUCCESS;
    }

    public void setSubmissionId(Integer submissionId)
    {
      this.submissionId = submissionId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public Integer getSubmitterId()
    {
      return submitterId;
    }

    public void setSubmitted(boolean submitted)
    {
      this.submitted = submitted;
    }

  }

  public static class Resubmit extends WetoTeacherAction
  {
    private Integer submissionId;
    private Integer submitterId;

    public Resubmit()
    {
      super(Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      // Retrieve submission from database.
      Submission submission = Submission.select1ById(conn, submissionId);
      validateCourseSubtaskId(submission.getTaskId());
      submitterId = submission.getUserId();
      // Verify that the logged user has rights to update a submission.
      if(!haveUpdateRights(Tab.SUBMISSIONS.getBit(), userId.equals(submitterId),
              true))
      {
        throw new WetoActionException();
      }
      //Check if submission is currently processed
      if(SubmissionStatus.PROCESSING.getValue().equals(submission.getStatus()))
      {
        addActionError(getText("autograding.error.stillProcessing"));
      }
      else
      {
        SubmissionModel.clearAutoGrading(conn, submission,
                SubmissionStatus.PROCESSING, false);
        // Insert the submission to the autogradejobqueue
        AutoGradeJobQueue job = new AutoGradeJobQueue();
        job.setTaskId(taskId);
        job.setDbId(getDbId());
        job.setRefId(submission.getId());
        job.setQueuePhase(AutoGradeJobQueue.IMMEDIATE_PUBLIC);
        job.setTestRunning(false);
        job.setJobComment(SubmissionModel.resubmitComment);
        job.insert(conn);
        addActionMessage(getText("autograding.message.resubmitSuccess"));
      }
      return SUCCESS;
    }

    public void setSubmissionId(Integer submissionId)
    {
      this.submissionId = submissionId;
    }

    public Integer getSubmissionId()
    {
      return submissionId;
    }

    public Integer getSubmitterId()
    {
      return submitterId;
    }

  }

  public static class ResubmitAll extends WetoTeacherAction
  {
    public ResubmitAll()
    {
      super(Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer userId = getCourseUserId();
      // Process only the latest submission for every user.
      HashSet<Integer> processed = new HashSet<>();
      for(Submission submission : Submission.selectByTaskId(conn, taskId))
      {
        validateCourseSubtaskId(submission.getTaskId());
        if(processed.add(submission.getUserId()))
        {
          Integer submitterId = submission.getUserId();
          // Verify that the logged user has rights to update a submission.
          if(!haveUpdateRights(Tab.SUBMISSIONS.getBit(), userId.equals(
                  submitterId), true))
          {
            addActionError(getText("general.error.accessDenied") + " ("
                    + submitterId + ")");
          }
          else //Check if submission is currently processed
          if(SubmissionStatus.PROCESSING.getValue().equals(submission
                  .getStatus()))
          {
            addActionError(getText("autograding.error.stillProcessing") + " ("
                    + submitterId + ")");
          }
          else
          {
            SubmissionModel.clearAutoGrading(conn, submission,
                    SubmissionStatus.PROCESSING, false);
            // Insert the submission to the autogradejobqueue
            AutoGradeJobQueue job = new AutoGradeJobQueue();
            job.setTaskId(taskId);
            job.setDbId(getDbId());
            job.setRefId(submission.getId());
            job.setQueuePhase(AutoGradeJobQueue.IMMEDIATE_PUBLIC);
            job.setTestRunning(false);
            job.setJobComment(SubmissionModel.resubmitComment);
            job.insert(conn);
          }
        }
      }
      addActionMessage(getText("autograding.message.resubmitAllSuccess"));
      return SUCCESS;
    }

  }

  public static class RemoveEmpty extends WetoTeacherAction
  {
    public RemoveEmpty()
    {
      super(Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS
              .getBit(), Tab.SUBMISSIONS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      for(Submission submission : Submission.selectByTaskId(conn, taskId))
      {
        if(SubmissionDocument.selectBySubmissionId(conn, submission.getId())
                .isEmpty())
        {
          SubmissionModel.deleteSubmission(conn, submission);
        }
      }
      return SUCCESS;
    }

  }

  public static class CompleteUncompleted extends WetoTeacherAction
  {
    public CompleteUncompleted()
    {
      super(Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS.getBit(), Tab.SUBMISSIONS
              .getBit(), Tab.SUBMISSIONS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      for(Submission submission : Submission.selectByTaskId(conn, taskId))
      {
        if(!SubmissionDocument.selectBySubmissionId(conn, submission.getId())
                .isEmpty())
        {
          submission.setStatus(SubmissionStatus.ACCEPTED.getValue());
          submission.update(conn);
        }
      }
      return SUCCESS;
    }

  }
}
