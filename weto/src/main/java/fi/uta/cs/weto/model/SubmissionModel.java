package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.AutoGradeTestScore;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionDocument;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SubmissionModel
{
  // Awkward: use DbConnectionManager as a means to access resource files
  public static final String resubmitComment = WetoUtilities.getMessageResource(
          "autograding.header.resubmitComment");

  public static void clearAutoGrading(Connection conn, Submission submission,
          SubmissionStatus newStatus, boolean updateTimeStamp)
          throws SQLException, TooManyItemsException, NoSuchItemException,
                 InvalidValueException, ObjectNotValidException
  {
    Integer submissionId = submission.getId();
    for(AutoGradeJobQueue agjq : AutoGradeJobQueue.selectBySubmissionId(conn,
            submissionId))
    {
      agjq.delete(conn);
    }
    for(AutoGradeTestScore score : AutoGradeTestScore.selectBySubmissionId(conn,
            submissionId))
    {
      score.delete(conn);
    }
    for(Tag feedback : Tag.selectByTaggedIdAndType(conn, submissionId,
            TagType.FEEDBACK.getValue()))
    {
      feedback.delete(conn);
    }
    submission.setStatus(newStatus.getValue());
    submission.setAutoGradeMark(null);
    submission.setMessage(null);
    submission.setError(null);
    if(updateTimeStamp)
    {
      submission.update(conn);
    }
    else
    {
      submission.update(conn, false);
    }
  }

  public static ArrayList<Integer> getSubmissionGroupMemberIds(Connection conn,
          Integer taskId, Integer studentId, boolean ignoreGroups)
          throws SQLException, InvalidValueException
  {
    ArrayList<Integer> submissionUsers = new ArrayList<>();
    submissionUsers.add(studentId);
    // If the user belongs to a submission group, add others' ids.
    if(!ignoreGroups)
    {
      try
      {
        GroupView gv = GroupView.select1InheritedByTaskIdAndUserIdAndType(conn,
                taskId, studentId, GroupType.SUBMISSION.getValue());
        for(GroupMember gm : GroupMember.selectByTaskIdAndGroupId(conn, gv
                .getTaskId(), gv.getId()))
        {
          if(!gm.getUserId().equals(studentId))
          {
            submissionUsers.add(gm.getUserId());
          }
        }
      } // The student has no submission group. Not an error.
      catch(NoSuchItemException e)
      {
      }
    }
    return submissionUsers;
  }

  public static boolean checkSubmissionOwnership(Connection conn,
          Integer taskId, Integer userId, Integer submitterId,
          boolean isStudent, boolean ignoreGroups)
          throws SQLException, InvalidValueException
  {
    boolean ownSubmission = userId.equals(submitterId);
    if(!ownSubmission && !ignoreGroups && isStudent)
    {
      ownSubmission = getSubmissionGroupMemberIds(conn, taskId, userId,
              ignoreGroups).contains(submitterId);
    }
    return ownSubmission;
  }

  public static boolean checkSubmissionOwnership(Integer userId,
          Integer submitterId, boolean isStudent, boolean ignoreGroups,
          ArrayList<Integer> submissionGroupMemberIds)
          throws SQLException, InvalidValueException
  {
    boolean ownSubmission = userId.equals(submitterId);
    if(!ownSubmission && !ignoreGroups && isStudent)
    {
      ownSubmission = submissionGroupMemberIds.contains(submitterId);
    }
    return ownSubmission;
  }

  public static void deleteSubmission(Connection conn, Submission submission)
          throws InvalidValueException, NoSuchItemException, SQLException,
                 TooManyItemsException, ObjectNotValidException
  {
    Integer submissionId = submission.getId();
    ArrayList<SubmissionDocument> submissionDocumentLinks = SubmissionDocument
            .selectBySubmissionId(conn, submissionId);
    for(AutoGradeJobQueue agjq : AutoGradeJobQueue.selectBySubmissionId(conn,
            submissionId))
    {
      agjq.delete(conn);
    }
    for(AutoGradeTestScore score : AutoGradeTestScore.selectBySubmissionId(conn,
            submissionId))
    {
      score.delete(conn);
    }
    for(Tag feedback : Tag.selectByTaggedIdAndType(conn, submissionId,
            TagType.FEEDBACK.getValue()))
    {
      feedback.delete(conn);
    }
    for(SubmissionDocument link : submissionDocumentLinks)
    {
      Integer documentId = link.getDocumentId();
      link.delete(conn);
      DocumentModel.deleteDocument(conn, Document.select1ById(conn, documentId));
    }
    if(SubmissionStatus.QUIZ_SUBMISSION.getValue()
            .equals(submission.getStatus()))
    {
      Integer submissionTaskId = submission.getTaskId();
      Integer submitterId = submission.getUserId();
      ArrayList<Submission> quizSubmissions = Submission
              .selectByUserIdAndTaskIdAndStatus(conn, submitterId,
                      submissionTaskId, SubmissionStatus.QUIZ_SUBMISSION
                      .getValue());
      if(quizSubmissions.size() == 1)
      {
        ArrayList<Tag> quizAnswerTags = Tag.selectByTaggedIdAndAuthorIdAndType(
                conn, submissionTaskId, submitterId, TagType.QUIZ_ANSWER
                .getValue());
        for(Tag tag : quizAnswerTags)
        {
          tag.delete(conn);
        }
        ArrayList<Tag> quizScoreTags = Tag
                .selectByTaggedIdAndAuthorIdAndType(conn, submissionTaskId,
                        submitterId, TagType.QUIZ_SCORE.getValue());
        for(Tag tag : quizScoreTags)
        {
          tag.delete(conn);
        }
      }
    }
    submission.delete(conn);
  }

  public static void deleteStudentTaskSubmissions(Connection conn,
          Integer taskId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 TooManyItemsException, ObjectNotValidException
  {
    HashSet<Integer> teacherIds = new HashSet<>();
    for(UserTaskView user : UserTaskView.selectByTaskIdAndClusterType(conn,
            taskId, ClusterType.TEACHERS.getValue()))
    {
      teacherIds.add(user.getUserId());
    }
    for(Submission submission : Submission.selectByTaskId(conn, taskId))
    {
      if(!teacherIds.contains(submission.getUserId()))
      {
        deleteSubmission(conn, submission);
      }
    }
  }

  public static Map<Integer, ArrayList<Submission>> getSubmissions(
          Connection conn, Integer taskId, Integer loggedUserId,
          Set<Integer> userIds, Integer from, Integer to,
          boolean onlyLatestOrBest, int[] stats, WetoCourseAction wca)
          throws SQLException
  {
    int mostSubmissions = 0;
    int allSubmissions = 0;
    boolean isAutoGraded = wca.getTask().getIsAutoGraded();
    Map<Integer, ArrayList<Submission>> userSubmissions = new HashMap<>();
    HashMap<Integer, Boolean> rights = new HashMap<>();
    HashSet<Integer> accepted = new HashSet<>();
    for(Integer userId : userIds)
    {
      rights.put(userId, wca.haveViewRights(Tab.SUBMISSIONS.getBit(), userId
              .equals(loggedUserId), true));
    }
    // Assumes that selectBytaskId returns the submissions in sorted order
    // according to descending timestamps (newer first).
    for(Submission submission : Submission.selectByTaskId(conn, taskId))
    {
      int userId = submission.getUserId();
      if(userIds.contains(userId) && rights.get(userId).equals(true))
      {
        if(SubmissionStatus.ACCEPTED.getValue().equals(submission.getStatus()))
        {
          accepted.add(userId);
        }
        ArrayList<Submission> submissions = userSubmissions.get(userId);
        if(submissions == null)
        {
          submissions = new ArrayList<>();
          userSubmissions.put(userId, submissions);
        }
        allSubmissions++;
        Integer submissionTime = submission.getTimeStamp();
        boolean fromOk = ((from == null) || (from <= submissionTime));
        boolean toOk = ((to == null) || (submissionTime <= to));
        if(fromOk && toOk)
        {
          Submission previous = submissions.isEmpty() ? null : submissions
                  .get(0);
          // Include only the latest or best submission for each student?
          if((previous != null) && onlyLatestOrBest)
          {
            if(isAutoGraded && (submission.getAutoGradeMark() != null)
                    && ((previous.getAutoGradeMark() == null) || (submission
                    .getAutoGradeMark() > previous.getAutoGradeMark())))
            {
              submissions.clear();
              submissions.add(submission);
            }
          }
          else
          {
            submissions.add(submission);
          }
          if(submissions.size() > mostSubmissions)
          {
            mostSubmissions = submissions.size();
          }
        }
      }
    }
    if(stats != null)
    {
      stats[0] = allSubmissions;
      stats[1] = mostSubmissions;
      stats[2] = accepted.size();
    }
    return userSubmissions;
  }

  public static void loadSubmissions(Connection conn,
          Map<Integer, ArrayList<Submission>> submissions, boolean idPrefix,
          File baseFolder)
          throws SQLException, WetoTimeStampException, FileNotFoundException,
                 IOException, InvalidValueException, NoSuchItemException
  {
    Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    Format timeFormatter = new SimpleDateFormat("HH-mm-ss");
    Set<Map.Entry<Integer, ArrayList<Submission>>> submissionLists
                                                           = submissions
            .entrySet();
    for(Map.Entry<Integer, ArrayList<Submission>> entry : submissionLists)
    {
      UserAccount student = UserAccount.select1ById(conn, entry.getKey());
      File loginNameFolder = new File(baseFolder, student.getLoginName());
      loginNameFolder.mkdir();
      for(Submission submission : entry.getValue())
      {
        String prefix = (idPrefix) ? submission.getId().toString() + "_" : "";
        Date submissionTime = new WetoTimeStamp(submission.getTimeStamp())
                .toCalendar().getTime();
        File submissionDateFolder = new File(loginNameFolder, prefix
                + dateFormatter.format(submissionTime) + "-" + timeFormatter
                .format(submissionTime));
        submissionDateFolder.mkdir();
        for(Document document : Document.selectBySubmissionId(conn, submission
                .getId()))
        {
          DocumentModel.loadDocument(conn, document, new File(
                  submissionDateFolder, document.getFileName()));
        }
      }
    }
  }

  public static ArrayList<Submission> getStudentSubmissions(Connection conn,
          Integer taskId, Integer studentId, boolean ignoreGroups)
          throws SQLException, InvalidValueException
  {
    ArrayList<Submission> submissions = new ArrayList<>();
    for(Integer submitterId : getSubmissionGroupMemberIds(conn, taskId,
            studentId, ignoreGroups))
    {
      submissions.addAll(Submission.selectByUserIdAndTaskId(conn, submitterId,
              taskId));
    }
    return submissions;
  }

  private static final String incorrectMsg = WetoUtilities.getMessageResource(
          "autograding.message.incorrectResult");

  public static FileSubmissionBean getFileSubmissionBean(Connection conn,
          Submission submission, Task submissionTask, boolean isStudent)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException
  {
    Integer submissionTaskId = submissionTask.getId();
    Integer submissionId = submission.getId();
    FileSubmissionBean fsb = new FileSubmissionBean();
    fsb.setSubmission(submission);
    // Get the list of existing documents for this submission.
    ArrayList<Document> documents = Document.selectBySubmissionId(conn,
            submissionId);
    fsb.setDocuments(documents);
    // Update the number of files for this submission, if necessary.
    Integer fileCount = documents.size();
    if(!fileCount.equals(submission.getFileCount()))
    {
      submission.setFileCount(fileCount);
      submission.update(conn, false);
    }
    // Mark documents that have also a newer document with duplicate file name.
    boolean[] duplicates = new boolean[documents.size()];
    for(int i = 1; i < documents.size(); ++i)
    {
      if(documents.get(i).getFileName().equals(documents.get(i - 1)
              .getFileName()))
      {
        duplicates[i - 1] = true;
      }
    }
    fsb.setDuplicates(duplicates);
    // If the submission task is autograded, retrieve and save auto grading
    // test scores and compiler messages.
    if(submissionTask.getIsAutoGraded())
    {
      if(SubmissionStatus.PROCESSING.getValue().equals(submission.getStatus()))
      {
        fsb.setQueuePos(AutoGradeJobQueue.getQueuePos(conn, submissionId));
      }
      Integer compilerResultId = null;
      ArrayList<Tag> fullErrors = Tag.selectByTaggedIdAndAuthorIdAndType(conn,
              submissionTaskId, submission.getUserId(), TagType.COMPILER_RESULT
              .getValue());
      if(!fullErrors.isEmpty())
      {
        Tag fullError = fullErrors.get(0);
        if(fullError.getRank().equals(submissionId))
        {
          compilerResultId = fullError.getId();
        }
      }
      fsb.setCompilerResultId(compilerResultId);
      ArrayList<AutoGradeTestScore> testScores = AutoGradeTestScore
              .selectBySubmissionId(conn, submissionId);
      Integer[] fullFeedbackIds = new Integer[testScores.size()];
      ArrayList<Tag> fullFeedback = Tag.selectByTaggedIdAndType(conn,
              submissionId, TagType.FEEDBACK.getValue());
      for(Tag f : fullFeedback)
      {
        int rank = f.getRank() - 1;
        fullFeedbackIds[(rank < 0) ? 0 : rank] = f.getId();
      }
      if(isStudent)
      {
        int i = 0;
        for(AutoGradeTestScore agts : testScores)
        {
          String res = agts.getFeedback();
          if(agts.isPrivateScore() && (!res.isEmpty() && ((res.charAt(0) == '+')
                  || (res.charAt(0) == '-') || (res.charAt(0) == ' '))))
          {
            agts.setFeedback(incorrectMsg);
            fullFeedbackIds[i] = null;
          }
          i += 1;
        }
      }
      fsb.setTestScores(testScores);
      fsb.setFullFeedbackIds(fullFeedbackIds);
    }
    return fsb;
  }

  public static Object[] addFileToSubmission(Connection conn,
          Submission submission, File documentFile, String documentFileName,
          final String filePatterns, boolean caseInsensitive, boolean unzip,
          boolean allowInlineFiles, boolean overWriteExisting)
          throws IOException, InvalidValueException, SQLException,
                 ObjectNotValidException, TooManyItemsException,
                 NoSuchItemException
  {
    Integer submissionId = submission.getId();
    Integer taskId = submission.getTaskId();
    Object[] result = new Object[2];
    ArrayList<Integer> newDocumentIds = new ArrayList<>();
    ArrayList<String> excludedFiles = new ArrayList<>();
    result[0] = newDocumentIds;
    result[1] = excludedFiles;
    // Compile regular expressions from the file name patterns.
    ArrayList<Pattern> patterns = new ArrayList<>();
    if(filePatterns != null)
    {
      for(String pattern : filePatterns.split("/"))
      {
        pattern = pattern.trim();
        if(!pattern.isEmpty())
        {
          if(caseInsensitive)
          {
            pattern = pattern.toLowerCase();
          }
          patterns.add(Pattern.compile(pattern));
        }
      }
    }
    if(documentFileName != null)
    {
      documentFileName = documentFileName.trim();
    }
    if((documentFile != null) && unzip && documentFileName.endsWith(".zip"))
    {
      File workDir = Files.createTempDirectory("submit").toFile();
      WetoUtilities.unzipFile(documentFile, workDir);
      try
      {
        for(File file : workDir.listFiles(new FilenameFilter()
        {
          private ArrayList<Pattern> patterns;
          private ArrayList<String> excludedFiles;
          private boolean caseInsensitive;

          @Override
          public boolean accept(File dir, String name)
          {
            if(caseInsensitive)
            {
              name = name.toLowerCase();
            }
            boolean result = (filePatterns == null);
            for(Pattern pattern : patterns)
            {
              if(pattern.matcher(name).matches())
              {
                result = true;
                break;
              }
            }
            if(!result)
            {
              excludedFiles.add(name);
            }
            return result;
          }

          private FilenameFilter init(ArrayList<Pattern> patterns,
                  ArrayList<String> excludedFiles, boolean caseInsensitive)
          {
            this.patterns = patterns;
            this.excludedFiles = excludedFiles;
            this.caseInsensitive = caseInsensitive;
            return this;
          }

        }.init(patterns, excludedFiles, caseInsensitive)))
        {
          String fileName = file.getName();
          Document document;
          Document existingDoc = null;
          if(overWriteExisting)
          {
            ArrayList<Document> existingDocs = Document
                    .selectBySubmissionIdAndFileName(conn, submissionId,
                            fileName);
            if(!existingDocs.isEmpty())
            {
              existingDoc = existingDocs.get(0);
              for(int i = 1; i < existingDocs.size(); ++i)
              {
                Integer removeDocId = existingDocs.get(i).getId();
                // Delete submissionDocument.
                SubmissionDocument.select1BySubmissionIdAndDocumentId(conn,
                        taskId, removeDocId).delete(conn);
                DocumentModel.deleteDocument(conn, Document.select1ById(conn,
                        removeDocId));
              }
            }
          }
          document = DocumentModel.replaceDocument(conn, file, fileName,
                  existingDoc);
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
          newDocumentIds.add(document.getId());
          if(doUpdate)
          {
            submissionDocument.update(conn);
          }
          else
          {
            submissionDocument.insert(conn);
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
      String checkFileName = documentFileName;
      if(caseInsensitive)
      {
        checkFileName = checkFileName.toLowerCase();
      }
      boolean fileNameOk = (filePatterns == null);
      for(Pattern pattern : patterns)
      {
        if(pattern.matcher(checkFileName).matches())
        {
          fileNameOk = true;
          break;
        }
      }
      if(fileNameOk)
      {
        Document document = null;
        Document existingDoc = null;
        if((documentFile == null) && allowInlineFiles)
        {
          document = DocumentModel.storeDocument(conn, File.createTempFile(
                  "empty", "txt"), documentFileName);
        }
        else if(documentFile != null)
        {
          if(overWriteExisting)
          {
            ArrayList<Document> existingDocs = Document
                    .selectBySubmissionIdAndFileName(conn, submissionId,
                            documentFileName);
            if(!existingDocs.isEmpty())
            {
              existingDoc = existingDocs.get(0);
              for(int i = 1; i < existingDocs.size(); ++i)
              {
                Integer removeDocId = existingDocs.get(i).getId();
                // Delete submissionDocument.
                SubmissionDocument.select1BySubmissionIdAndDocumentId(conn,
                        taskId, removeDocId).delete(conn);
                DocumentModel.deleteDocument(conn, Document.select1ById(conn,
                        removeDocId));
              }
            }
          }
          document = DocumentModel.replaceDocument(conn, documentFile,
                  documentFileName, existingDoc);
        }
        if(document != null)
        {
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
          newDocumentIds.add(document.getId());
        }

      }
      else
      {
        excludedFiles.add(documentFileName);
      }
    }
    if(!newDocumentIds.isEmpty())
    { // Clear submission status.
      SubmissionModel.clearAutoGrading(conn, submission,
              SubmissionStatus.NOT_SUBMITTED, true);

    }
    return result;
  }

}
