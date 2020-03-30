package fi.uta.cs.weto.actions.main;

import static com.opensymphony.xwork2.Action.SUCCESS;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.weto.db.AutoGradeTestScore;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.TaskDocument;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.AggregateFunctionType;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.PermissionRefType;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionError;
import fi.uta.cs.weto.model.SubmissionModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.TaskDocumentStatus;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoProperties;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.TmpFileInputStream;
import fi.uta.cs.weto.util.WetoCsvWriter;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class DownloadTaskActions
{
  public static class View extends WetoTeacherAction
  {
    public View()
    {
      super(getAllRightsBits(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      return SUCCESS;
    }

  }

  public static class Download extends WetoTeacherAction
  {
    private static final int bufferSize = 8192;

    private String fileName = "";
    private String submitterLogins = "";
    private boolean onlyLatestOrBest;
    private boolean doNotCompress;
    private int contentLength;
    private InputStream documentStream;

    private Connection courseConn;

    private final Set<Integer> submitterIdSet = new HashSet<>();
    private final Map<Integer, String> idLoginMap = new HashMap<>();

    public Download()
    {
      super(getAllRightsBits(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      if((fileName == null) || !Pattern.matches("\\S+", fileName))
      {
        fileName = "task.zip";
      }
      courseConn = getCourseConnection();
      Integer courseTaskId = getCourseTaskId();
      Integer taskId = getTaskId();
      Task task = getTask();
      String taskName = atofn(task.getName());
      if(taskName.isEmpty())
      {
        throw new WetoActionException(getText("downloadTask.error.emptyName"));
      }
      if(submitterLogins != null)
      {
        if(submitterLogins.equals("*"))
        {
          ArrayList<StudentView> students = StudentView.selectByTaskId(
                  courseConn, courseTaskId);
          for(StudentView student : students)
          {
            submitterIdSet.add(student.getUserId());
          }
        }
        else if(!submitterLogins.isEmpty())
        {
          for(String login : submitterLogins.split("\\s+", 0))
          {
            try
            {
              submitterIdSet.add(UserAccount.select1ByLoginName(courseConn,
                      login).getId());
            }
            catch(NoSuchItemException e)
            {
            }
          }
        }
      }
      File baseDir = Files.createTempDirectory("weto").toFile();
      File taskDir = new File(baseDir, taskName);
      taskDir.mkdir();
      writeCourseStudents(StudentView.selectByTaskId(courseConn, courseTaskId),
              taskDir);
      writeCourseCss(taskId, taskDir);
      writeTaskSubtree(task, taskDir);
      Path zipFilePath = Files.createTempFile("weto", ".zip");
      File zipFile = zipFilePath.toFile();
      WetoUtilities.zipSubDir(baseDir, taskName, zipFile, doNotCompress);
      WetoUtilities.deleteRecursively(baseDir);
      documentStream = new TmpFileInputStream(zipFile, Files.newInputStream(
              zipFilePath));
      contentLength = (int) zipFile.length();
      return "download";
    }

    private void writeCourseStudents(ArrayList<StudentView> students,
            File taskDir)
            throws IOException
    {
      if(!students.isEmpty())
      {
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                "students.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          for(StudentView student : students)
          {
            row.clear();
            row.add(otoa(student.getUserId()));
            row.add(student.getLoginName());
            row.add(student.getLastName());
            row.add(student.getFirstName());
            row.add(student.getStudentNumber());
            row.add(student.getEmail());
            wcw.writeStrings(row);
            idLoginMap.put(student.getUserId(), student.getLoginName());
          }

        }

      }
    }

    private void writeCourseCss(Integer taskId, File taskDir)
            throws SQLException, IOException, WetoTimeStampException,
                   InvalidValueException, NoSuchItemException
    {
      ArrayList<Tag> cssTags = Tag.selectByTaggedIdAndType(courseConn, taskId,
              TagType.CSS_STYLE.getValue());
      if(!cssTags.isEmpty())
      {
        try(WetoCsvWriter wcw
                                  = new WetoCsvWriter(new File(taskDir,
                        "css_styles.txt")))
        {
          writeTags(cssTags, wcw);
        }
      }
    }

    private void writeTaskSubtree(Task task, File taskDir)
            throws IOException, SQLException, InvalidValueException,
                   WetoTimeStampException, WetoActionException,
                   ObjectNotValidException, FileNotFoundException,
                   NoSuchItemException
    {
      Integer taskId = task.getId();
      String contents = task.getText();
      if((contents != null) && !contents.isEmpty())
      {
        try(BufferedWriter out = new BufferedWriter(new FileWriter(new File(
                taskDir, "contents.html"))))
        {
          out.write(contents);
        }
      }
      writeProperties(task, taskDir);
      writeQuiz(taskId, taskDir);
      writePermissions(taskId, taskDir);
      writeGrades(taskId, taskDir);
      writeSubmissions(taskId, taskDir);
      writeGroups(taskId, taskDir);
      writeDocuments(taskId, taskDir);
      Integer rank = 0;
      for(Task subtask : Task.selectSubtasks(courseConn, taskId))
      {
        String subtaskName = atofn(subtask.getName());
        if(subtaskName.isEmpty())
        {
          throw new WetoActionException(getText("downloadTask.error.emptyName"));
        }
        File subtaskDir = new File(taskDir, rank.toString() + "_" + subtaskName);
        subtaskDir.mkdir();
        writeTaskSubtree(subtask, subtaskDir);
        rank += 1;
      }
    }

    private void writeProperties(Task task, File taskDir)
            throws IOException, WetoTimeStampException
    {
      WetoProperties taskProps = new WetoProperties();
      taskProps.setProperty("taskId", task.getId().toString());
      taskProps.setProperty("hasGrades", new Boolean(task.getHasGrades())
              .toString());
      taskProps.setProperty("hasSubmissions", new Boolean(task
              .getHasSubmissions()).toString());
      taskProps.setProperty("hasForum", new Boolean(task.getHasForum())
              .toString());
      taskProps.setProperty("showTextInParent", task.getShowTextInParent()
              .toString());
      taskProps.setProperty("isHidden", new Boolean(task.getIsHidden())
              .toString());
      taskProps.setProperty("isPublic", new Boolean(task.getIsPublic())
              .toString());
      taskProps.setProperty("isAutoGraded", new Boolean(task.getIsAutoGraded())
              .toString());
      taskProps.setProperty("isQuiz", new Boolean(task.getIsQuiz()).toString());
      taskProps.setProperty("hasReviewInstructions", new Boolean(task
              .getHasReviewInstructions()).toString());
      try(FileWriter out = new FileWriter(new File(taskDir, "properties.txt")))
      {
        taskProps.write(out);
      }
    }

    private void writeQuiz(Integer taskId, File taskDir)
            throws IOException, WetoTimeStampException, SQLException,
                   InvalidValueException, NoSuchItemException
    {
      ArrayList<Tag> questions = Tag.selectByTaggedIdAndType(courseConn, taskId,
              TagType.QUIZ_QUESTION.getValue());
      if(!questions.isEmpty())
      {
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                "questions.txt")))
        {
          writeTags(questions, wcw);
        }
      }
      ArrayList<Tag> answers = Tag.selectByTaggedIdAndType(courseConn, taskId,
              TagType.QUIZ_ANSWER.getValue());
      if(!answers.isEmpty())
      {
        ArrayList<Tag> includedAnswers = new ArrayList<>();
        for(Tag answer : answers)
        {
          if(submitterIdSet.contains(answer.getAuthorId()))
          {
            includedAnswers.add(answer);
          }
        }
        if(!includedAnswers.isEmpty())
        {
          try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                  "answers.txt")))
          {
            writeTags(includedAnswers, wcw);
          }
        }
      }
    }

    private void writePermissions(Integer taskId, File taskDir)
            throws IOException, WetoTimeStampException, SQLException,
                   InvalidValueException, NoSuchItemException
    {
      ArrayList<Permission> permissions = Permission.selectByTaskId(
              courseConn, taskId);
      if(!permissions.isEmpty())
      {
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                "permissions.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          for(Permission permission : permissions)
          {
            row.clear();
            row.add(idtoln(permission.getUserRefId()));
            row.add(getText(PermissionRefType.getType(permission
                    .getUserRefType()).getProperty()));
            if(permission.getType() == null)
            {
              row.add(null);
            }
            else
            {
              row.add(getText(PermissionType.getType(permission.getType())
                      .getProperty()));
            }
            row.add(wtstoa(permission.getStartDate()));
            row.add(wtstoa(permission.getEndDate()));
            row.add(permission.getDetail());
            wcw.writeStrings(row);
          }
        }
      }
    }

    private void writeGrades(Integer taskId, File taskDir)
            throws IOException, WetoTimeStampException, SQLException,
                   InvalidValueException, NoSuchItemException,
                   ObjectNotValidException
    {
      ArrayList<Grade> grades = Grade.selectByTaskId(courseConn, taskId);
      if(!grades.isEmpty())
      {
        ArrayList<Tag> reviews = new ArrayList<>();
        try(WetoCsvWriter wcw = new WetoCsvWriter(
                new File(taskDir, "grades.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          for(Grade grade : grades)
          {
            row.clear();
            row.add(otoa(grade.getId()));
            row.add(idtoln(grade.getReceiverId()));
            row.add(otoa(grade.getMark()));
            if(grade.getStatus() == null)
            {
              row.add(null);
            }
            else
            {
              row.add(getText(GradeStatus.getStatus(grade.getStatus())
                      .getProperty()));
            }
            row.add(wtstoa(grade.getTimeStamp()));
            row.add(idtoln(grade.getReviewerId()));
            wcw.writeStrings(row);
            reviews.addAll(Tag.selectByTaggedIdAndType(courseConn,
                    grade.getId(), TagType.REVIEW.getValue()));
          }
        }
        if(!reviews.isEmpty())
        {
          try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                  "reviews.txt")))
          {
            writeTags(reviews, wcw);
          }
        }
      }
      ArrayList<Tag> reviewInstructions = Tag
              .selectByTaggedIdAndType(courseConn, taskId,
                      TagType.REVIEW_INSTRUCTIONS.getValue());
      if(!reviewInstructions.isEmpty())
      {
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                "review_instructions.txt")))
        {
          writeTags(reviewInstructions, wcw);
        }
      }
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(courseConn, taskId);
        WetoProperties scoringProps = new WetoProperties();
        for(String key : scoring.getPropertySet())
        {
          scoringProps.setProperty(key, scoring.getProperty(key));
        }
        scoringProps.setProperty("gradeTable", scoring.getGradeTable());
        // Map the aggregate function id to its name.
        String agf = scoring.getProperty("aggregateFunction");
        if(agf == null)
        {
          scoringProps.setProperty("aggregateFunction", "");
        }
        else
        {
          scoringProps.setProperty("aggregateFunction", getText(
                  AggregateFunctionType.getType(Integer.parseInt(agf))
                  .getProperty()));
        }
        try(FileWriter out = new FileWriter(new File(taskDir, "scoring.txt")))
        {
          scoringProps.write(out);
        }
      }
      catch(NoSuchItemException e)
      {
      }
    }

    private void writeSubmissions(Integer taskId, File taskDir)
            throws SQLException, WetoTimeStampException, IOException,
                   FileNotFoundException, InvalidValueException,
                   NoSuchItemException, ObjectNotValidException
    {
      File submissionsDir = new File(taskDir, "submissions");
      Map<Integer, ArrayList<Submission>> submissions = SubmissionModel
              .getSubmissions(courseConn, taskId, getCourseUserId(),
                      submitterIdSet, null, null, onlyLatestOrBest, null, this);
      if(!submissions.isEmpty())
      {
        submissionsDir.mkdir();
        Set<Map.Entry<Integer, ArrayList<Submission>>> submissionLists
                                                               = submissions
                .entrySet();
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(submissionsDir,
                "submissions.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          for(Map.Entry<Integer, ArrayList<Submission>> entry : submissionLists)
          {
            for(Submission submission : entry.getValue())
            {
              row.clear();
              row.add(otoa(submission.getId()));
              row.add(idtoln(submission.getUserId()));
              row.add(wtstoa(submission.getTimeStamp()));
              row.add(otoa(submission.getAutoGradeMark()));
              if(submission.getStatus() == null)
              {
                row.add(null);
              }
              else
              {
                row.add(getText(SubmissionStatus.getStatus(
                        submission.getStatus()).getProperty()));
              }
              row.add(submission.getMessage());
              if(submission.getError() == null)
              {
                row.add(null);
              }
              else
              {
                row.add(getText(SubmissionError.getError(submission.getError())
                        .getProperty()));
              }
              wcw.writeStrings(row);
            }
          }
        }
        SubmissionModel
                .loadSubmissions(courseConn, submissions, true, submissionsDir);
      }
      try
      {
        SubmissionProperties sp = SubmissionProperties.select1ByTaskId(
                courseConn, taskId);
        if(!submissionsDir.exists())
        {
          submissionsDir.mkdir();
        }
        try(FileWriter out = new FileWriter(new File(submissionsDir,
                "submissionProperties.txt")))
        {
          WetoProperties subProps = new WetoProperties(new StringReader(sp
                  .getProperties()));
          subProps.write(out);
        }
      }
      catch(NoSuchItemException e)
      {
      }
      try
      {
        AutoGrading ag = AutoGrading.select1ByTaskId(courseConn, taskId);
        Document testFile = Document.select1ById(courseConn, ag.getTestDocId());
        if(!submissionsDir.exists())
        {
          submissionsDir.mkdir();
        }
        try(FileWriter out = new FileWriter(new File(submissionsDir,
                "autograding.txt")))
        {
          WetoProperties agProps = new WetoProperties(new StringReader(ag
                  .getProperties()));
          agProps.write(out);
        }
        DocumentModel.loadDocument(courseConn, testFile, new File(
                submissionsDir, "autograding.zip"));
      }
      catch(NoSuchItemException e)
      {
      }
      ArrayList<AutoGradeTestScore> agScores = AutoGradeTestScore
              .selectByTaskId(courseConn, taskId);
      if(!agScores.isEmpty())
      {
        if(!submissionsDir.exists())
        {
          submissionsDir.mkdir();
        }
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(submissionsDir,
                "autogrades.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          for(AutoGradeTestScore agScore : agScores)
          {
            row.clear();
            row.add(otoa(agScore.getSubmissionId()));
            row.add(otoa(agScore.getPhase()));
            row.add(otoa(agScore.getTestNo()));
            row.add(otoa(agScore.getTestScore()));
            row.add(otoa(agScore.getProcessingTime()));
            row.add(agScore.getFeedback());
            wcw.writeStrings(row);
          }
        }
      }
    }

    private void writeGroups(Integer taskId, File taskDir)
            throws IOException, WetoTimeStampException, SQLException,
                   InvalidValueException, NoSuchItemException
    {
      ArrayList<UserGroup> groups = UserGroup.selectByTaskId(courseConn, taskId);
      ArrayList<GroupMember> members = GroupMember.selectByTaskId(courseConn,
              taskId);
      if(!groups.isEmpty())
      {
        Map<Integer, ArrayList<Integer>> memberMap = new HashMap<>();
        for(GroupMember member : members)
        {
          ArrayList<Integer> list = memberMap.get(member.getGroupId());
          if(list == null)
          {
            list = new ArrayList<>();
            memberMap.put(member.getGroupId(), list);
          }
          list.add(member.getUserId());
        }
        try(WetoCsvWriter wcw = new WetoCsvWriter(
                new File(taskDir, "groups.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          for(UserGroup group : groups)
          {
            row.clear();
            row.add(otoa(group.getId()));
            if(group.getType() == null)
            {
              row.add(null);
            }
            else
            {
              row.add(getText(GroupType.getType(group.getType()).getProperty()));
            }
            row.add(group.getName());
            wcw.writeStrings(row);
            row.clear();
            ArrayList<Integer> list = memberMap.get(group.getId());
            if(list != null)
            {
              for(Integer userId : list)
              {
                row.add(idtoln(userId));
              }
            }
            else
            {
              row.add("");
              row.add("");
            }
            wcw.writeStrings(row);
          }
        }
      }
    }

    private void writeDocuments(Integer taskId, File taskDir)
            throws SQLException, WetoTimeStampException, IOException,
                   FileNotFoundException, InvalidValueException,
                   NoSuchItemException, ObjectNotValidException
    {
      ArrayList<TaskDocument> taskDocuments = TaskDocument.selectByTaskId(
              courseConn, taskId);
      if(!taskDocuments.isEmpty())
      {
        try(WetoCsvWriter wcw = new WetoCsvWriter(new File(taskDir,
                "documents.txt")))
        {
          ArrayList<String> row = new ArrayList<>();
          File documentsDir = new File(taskDir, "documents");
          documentsDir.mkdir();
          for(TaskDocument taskDoc : taskDocuments)
          {
            Document document = Document.select1ById(courseConn, taskDoc
                    .getDocumentId());
            File docFile = new File(documentsDir, document.getFileName());
            if(docFile.exists())
            {
              int i = 1;
              String base = document.getFileName();
              String suffix = "";
              int dot = base.lastIndexOf(".");
              if(dot > 0)
              {
                suffix = base.substring(dot);
                base = base.substring(0, dot);
              }
              do
              {
                docFile = new File(documentsDir, base + "(" + i + ")" + suffix);
                i += 1;
              }
              while(docFile.exists());
            }
            DocumentModel.loadDocument(courseConn, document, docFile);
            row.clear();
            row.add(document.getId().toString());
            row.add(idtoln(taskDoc.getUserId()));
            row.add(getText(TaskDocumentStatus.getStatus(taskDoc.getStatus())
                    .getProperty()));
            row.add(document.getFileName());
            row.add(docFile.getName());
            wcw.writeStrings(row);
          }
        }
      }
    }

    private void writeTags(List<Tag> tags, WetoCsvWriter wcw)
            throws WetoTimeStampException, IOException, SQLException,
                   InvalidValueException, NoSuchItemException
    {
      ArrayList<String> row = new ArrayList<>();
      for(Tag tag : tags)
      {
        row.clear();
        row.add(otoa(tag.getId()));
        row.add(otoa(tag.getTaggedId()));
        row.add(wtstoa(tag.getTimeStamp()));
        row.add(idtoln(tag.getAuthorId()));
        row.add(otoa(tag.getStatus()));
        row.add(otoa(tag.getRank()));
        row.add(tag.getText());
        wcw.writeStrings(row);
      }
    }

    private String idtoln(Integer userId)
            throws SQLException, InvalidValueException, NoSuchItemException
    {
      String result = null;
      if(userId != null)
      {
        result = idLoginMap.get(userId);
        if(result == null)
        {
          result = UserAccount.select1ById(courseConn,
                  userId).getLoginName();
          idLoginMap.put(userId, result);
        }
      }
      return result;
    }

    private static String otoa(Object x)
    {
      return (x == null) ? null : x.toString();
    }

    private static String wtstoa(Integer x) throws WetoTimeStampException
    {
      return (x == null) ? null : new WetoTimeStamp(x).toString();
    }

    private static String atofn(String fileName)
    {
      return (fileName != null) ? fileName.replaceAll("/", "-").trim() : "";
    }

    public void setFileName(String fileName)
    {
      this.fileName = fileName;
    }

    public String getFileName()
    {
      return fileName;
    }

    public void setSubmitterLogins(String submitterLogins)
    {
      this.submitterLogins = submitterLogins;
    }

    public void setOnlyLatestOrBest(Boolean onlyLatestOrBest)
    {
      this.onlyLatestOrBest = onlyLatestOrBest;
    }

    public void setDoNotCompress(Boolean doNotCompress)
    {
      this.doNotCompress = doNotCompress;
    }

    public static int getBufferSize()
    {
      return bufferSize;
    }

    public int getContentLength()
    {
      return contentLength;
    }

    public InputStream getDocumentStream()
    {
      return documentStream;
    }

  }

}
