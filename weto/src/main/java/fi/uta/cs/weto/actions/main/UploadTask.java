package fi.uta.cs.weto.actions.main;

import static com.opensymphony.xwork2.Action.SUCCESS;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.actions.grading.ReviewInstructionsActions;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionDocument;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.TaskDocument;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.AggregateFunctionType;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.InstructionBean;
import fi.uta.cs.weto.model.PermissionRefType;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.SubmissionError;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.TaskDocumentStatus;
import fi.uta.cs.weto.model.TaskModel;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoProperties;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import fi.uta.cs.weto.util.WetoCsvReader;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.supercsv.io.CsvListReader;

public class UploadTask extends WetoTeacherAction
{
  private File taskFile = null;

  private String zipRootDir = "";
  private String submitterLogins = "";
  private String studentLogins = "";
  private boolean autoSelectFirst;

  private Connection courseConn;
  private Connection masterConn;
  private Integer teacherUserId;
  private Integer courseRootTaskId;

  private final HashMap<Integer, Integer> gradeIdMap = new HashMap<>();
  private final HashMap<String, Integer> loginIdMap = new HashMap<>();
  private final HashSet<String> submitterLoginSet = new HashSet<>();
  private final HashSet<String> discardLoginSet = new HashSet<>();
  private final HashMap<Integer, Integer> taskIdMap = new HashMap<>();
  private final HashMap<Integer, Integer[]> taskDocumentIdMap = new HashMap<>();

  public UploadTask()
  {
    super(getAllRightsBits(), getAllRightsBits(), getAllRightsBits(),
            getAllRightsBits());
  }

  private class SubDirFilter implements DirectoryStream.Filter<Path>
  {
    @Override
    public boolean accept(Path entry) throws IOException
    {
      return entry.toFile().isDirectory();
    }

  }

  @Override
  public String action() throws Exception
  {
    if(taskFile != null)
    {
      File workDir = Files.createTempDirectory("upload").toFile();
      WetoUtilities.unzipFile(taskFile, workDir);
      courseConn = getCourseConnection();
      masterConn = getMasterConnection();
      teacherUserId = getCourseUserId();
      Integer taskId = getTaskId();
      Task task = getTask();
      courseRootTaskId = task.getRootTaskId();
      File startDir = workDir;
      if(autoSelectFirst)
      {
        try(DirectoryStream<Path> subDirs = Files.newDirectoryStream(workDir
                .toPath(), new SubDirFilter()))
        {
          for(Path subDirPath : subDirs)
          {
            startDir = subDirPath.toFile();
            break;
          }
        }
      }
      else if(!zipRootDir.isEmpty())
      {
        startDir = new File(workDir, zipRootDir);
      }
      if(!startDir.exists() || !startDir.isDirectory())
      {
        throw new WetoActionException(getText(
                "uploadTask.error.illegalZipRoot",
                new String[]
                {
                  zipRootDir
                }));
      }
      Set<String> studentLoginSet = new HashSet<>();
      boolean allStudents = false;
      if(studentLogins != null)
      {
        if(studentLogins.equals("*"))
        {
          allStudents = true;
        }
        else
        {
          for(String login : studentLogins.split("\\s+", 0))
          {
            studentLoginSet.add(login);
          }
        }
      }
      readCourseStudents(task, startDir, studentLoginSet, allStudents);
      if(submitterLogins != null)
      {
        if(submitterLogins.equals("*"))
        {
          submitterLoginSet.addAll(loginIdMap.keySet());
        }
        else
        {
          for(String login : submitterLogins.split("\\s+", 0))
          {
            submitterLoginSet.add(login);
          }
        }
      }
      readCourseCss(taskId, startDir);
      readTaskDir(task, startDir);
      WetoUtilities.deleteRecursively(workDir);
      TaskModel.remapLinkTaskAndDocumentIds(courseConn, taskIdMap,
              taskDocumentIdMap);
      for(Integer newTaskId : taskIdMap.values())
      {
        QuizModel quiz = new QuizModel(courseConn, newTaskId, null);
        if(quiz.readQuestions())
        {
          quiz.migrateQuestionDocumentIds(getDbId(), taskDocumentIdMap);
        }
        for(Tag instructionTag : Tag.selectByTaggedIdAndType(courseConn,
                newTaskId, TagType.REVIEW_INSTRUCTIONS.getValue()))
        {
          InstructionBean instruction = ReviewInstructionsActions
                  .instructionTagToBean(instructionTag);
          String newText = TaskModel.migrateStringDocumentIds(instruction
                  .getText(), newTaskId, getDbId(), taskDocumentIdMap);
          if(newText != null)
          {
            instruction.setText(newText);
            instructionTag.setText(ReviewInstructionsActions.buildXML(
                    instruction.getName(), instruction.getText(), instruction
                    .getMinPoints(), instruction.getMaxPoints()));
            instructionTag.update(courseConn);
          }
        }
      }
      refreshNavigationTree();
      addActionMessage(getText("uploadTask.message.success"));
      return SUCCESS;
    }
    return ERROR;
  }

  private final int studentCols = 6;

  private void readCourseStudents(Task task, File taskDir,
          Set<String> studentLoginSet, boolean allStudents)
          throws IOException, InvalidValueException, SQLException,
                 ObjectNotValidException, NoSuchItemException,
                 WetoActionException, TooManyItemsException
  {
    File file;
    try(CsvListReader students = new WetoCsvReader(file = new File(taskDir,
            "students.txt")))
    {
      List<String> row;
      UserGroup group = new UserGroup();
      group.setTaskId(task.getRootTaskId());
      while((row = students.read()) != null)
      {
        if(row.size() == studentCols)
        {
          String loginName = row.get(1).trim().toLowerCase();
          if(allStudents)
          {
            studentLoginSet.add(loginName);
          }
          if(studentLoginSet.contains(loginName))
          {
            UserAccount user;
            try
            {
              user = UserAccount.select1ByLoginName(masterConn, loginName);
            }
            catch(NoSuchItemException e)
            {
              user = new UserAccount();
              user.setLoginName(loginName);
              // Local user: set empty password.
              user.setPassword("");
              user.setLastName(row.get(2));
              user.setFirstName(row.get(3));
              user.setEmail(row.get(5));
            }
            String studentNumber = row.get(4);
            CourseMemberModel.addStudent(masterConn, courseConn, user,
                    studentNumber, group, false, true);
            user = UserAccount.select1ByLoginName(courseConn, loginName);
            loginIdMap.put(loginName, user.getId());
          }
          else
          {
            discardLoginSet.add(loginName);
          }
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + " had " + row.size() + " parts: "
                  + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
  }

  private final static int tagCols = 7;

  private void readCourseCss(Integer taskId, File taskDir) throws
          InvalidValueException, IOException, ObjectNotValidException,
          SQLException, WetoActionException, WetoTimeStampException
  {
    readTaskTags(courseConn, taskId, taskDir, "css_styles.txt",
            TagType.CSS_STYLE.getValue(), null);
  }

  private class SubtaskDirFilter implements DirectoryStream.Filter<Path>
  {
    @Override
    public boolean accept(Path entry) throws IOException
    {
      File dir = entry.toFile();
      return (dir.isDirectory() && Pattern.compile("\\d+_").matcher(dir
              .getName()).lookingAt());
    }

  }

  private String normalize(String name)
  {
    String result = name;
    if(!Normalizer.isNormalized(name, Normalizer.Form.NFC))
    {
      result = Normalizer.normalize(name, Normalizer.Form.NFC);
    }
    return result;
  }

  private void readTaskDir(Task task, File taskDir)
          throws IOException, SQLException, InvalidValueException,
                 WetoTimeStampException, WetoActionException,
                 ObjectNotValidException, FileNotFoundException,
                 NoSuchItemException
  {
    Integer taskId = task.getId();
    File contentsFile = new File(taskDir, "contents.html");
    try(ByteArrayOutputStream text = new ByteArrayOutputStream())
    {
      WetoUtilities.fileToStream(contentsFile, text);
      task.setText(text.toString("UTF-8"));
    }
    catch(FileNotFoundException e)
    {
      task.setText("");
    }
    boolean isLeafTask = true;
    try(DirectoryStream<Path> subtaskDirs = Files.newDirectoryStream(taskDir
            .toPath(), new SubtaskDirFilter()))
    {
      for(Path subtaskPath : subtaskDirs)
      {
        isLeafTask = false;
        break;
      }
    }
    Integer origTaskId = readProperties(task, taskDir);
    task.update(courseConn); // The later operations do not use/change task
    if(origTaskId != null)
    {
      taskIdMap.put(origTaskId, taskId);
    }
    readDocuments(taskId, taskDir);
    readQuiz(taskId, taskDir, task, origTaskId);
    readPermissions(taskId, taskDir);
    readGrades(taskId, taskDir, isLeafTask);
    readSubmissions(taskId, taskDir);
    readGroups(taskId, taskDir);
    // Changes to a course root task are reflected to master database.
    if(taskId.equals(task.getRootTaskId()))
    {
      CourseImplementation ci = CourseImplementation
              .select1ByDatabaseIdAndCourseTaskId(masterConn, getDbId(), taskId);
      Task masterTask = Task.select1ById(masterConn, ci.getMasterTaskId());
      masterTask.setText(task.getText());
      masterTask.setShowTextInParent(task.getShowTextInParent());
      masterTask.setStatus(task.getStatus());
      masterTask.setComponentBits(task.getComponentBits());
      masterTask.update(masterConn);
    }
    try(DirectoryStream<Path> subtaskDirs = Files.newDirectoryStream(taskDir
            .toPath(), new SubtaskDirFilter()))
    {
      for(Path subtaskPath : subtaskDirs)
      {
        File subtaskDir = subtaskPath.toFile();
        // Subtask dirs should be named in the form "rank_subtaskname".
        String[] parts = subtaskDir.getName().split("_", 2);
        Task subtask = new Task();
        subtask.setName(normalize(parts[1]));
        subtask.setRootTaskId(courseRootTaskId);
        subtask.insert(courseConn);
        SubtaskLink link = new SubtaskLink();
        link.setContainerId(taskId);
        link.setSubtaskId(subtask.getId());
        link.setRank(Integer.parseInt(parts[0]));
        link.insert(courseConn);
        readTaskDir(subtask, subtaskDir);
      }
    }
  }

  private Integer readProperties(Task task, File taskDir)
          throws IOException, InvalidValueException, SQLException,
                 ObjectNotValidException, NoSuchItemException
  {
    Properties taskProps = new Properties();
    try(FileReader properties = new FileReader(new File(taskDir,
            "properties.txt")))
    {
      taskProps.load(properties);
    }
    catch(FileNotFoundException e)
    {
    }
    Integer origTaskId = atoi(taskProps.getProperty("taskId"));
    task.setHasGrades(Boolean.valueOf(taskProps.getProperty("hasGrades")));
    task.setHasSubmissions(Boolean.valueOf(taskProps.getProperty(
            "hasSubmissions")));
    task.setHasForum(Boolean.valueOf(taskProps.getProperty("hasForum")));
    task.setShowTextInParent(Boolean.valueOf(taskProps.getProperty(
            "showTextInParent")));
    // Do not set the hidden property if this is the upload root task
    // (uploader may wish to keep the uploaded task tree hidden)
    if(!task.getId().equals(getTaskId()))
    {
      task.setIsHidden(Boolean.valueOf(taskProps.getProperty("isHidden")));
    }
    task.setIsPublic(Boolean.valueOf(taskProps.getProperty("isPublic")));
    task.setIsAutoGraded(Boolean.valueOf(taskProps.getProperty("isAutoGraded")));
    task.setIsQuiz(Boolean.valueOf(taskProps.getProperty("isQuiz")));
    task.setHasReviewInstructions(Boolean.valueOf(taskProps.getProperty(
            "hasReviewInstructions")));
    return origTaskId;
  }

  private void readQuiz(Integer taskId, File taskDir, Task task,
          Integer origTaskId)
          throws IOException, InvalidValueException, ObjectNotValidException,
                 SQLException, WetoActionException, NoSuchItemException,
                 WetoTimeStampException
  {
    HashMap<Integer, Integer> questionIdMap = new HashMap<>();
    File file;
    try(CsvListReader questions = new WetoCsvReader(file = new File(taskDir,
            "questions.txt")))
    {
      List<String> row;
      while((row = questions.read()) != null)
      {
        if(row.size() == tagCols)
        {
          Tag tag = new Tag();
          tag.setTaggedId(taskId);
          tag.setAuthorId(teacherUserId);
          tag.setStatus(atoi(row.get(4)));
          tag.setRank(atoi(row.get(5)));
          tag.setText(row.get(6));
          tag.setType(TagType.QUIZ_QUESTION.getValue());
          tag.insert(courseConn, WetoTimeStamp.stringToStamp(row.get(2)));
          questionIdMap.put(atoi(row.get(0)), tag.getId());
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
    if(!questionIdMap.isEmpty())
    {
      TaskModel.migrateInlineQuestionIds(courseConn, origTaskId, task,
              questionIdMap);
    }
    try(CsvListReader answers = new WetoCsvReader(file = new File(taskDir,
            "answers.txt")))
    {
      List<String> row;
      while((row = answers.read()) != null)
      {
        if(row.size() == tagCols)
        {
          String login = row.get(3);
          if(loginIdMap.containsKey(login))
          {
            Integer userId = loginIdMap.get(login);
            if(userId != null)
            {
              Tag tag = new Tag();
              tag.setTaggedId(taskId);
              tag.setAuthorId(userId);
              tag.setStatus(atoi(row.get(4)));
              tag.setRank(questionIdMap.get(atoi(row.get(5))));
              tag.setText(row.get(6));
              tag.setType(TagType.QUIZ_ANSWER.getValue());
              tag.insert(courseConn, WetoTimeStamp.stringToStamp(row.get(2)));
            }
          }
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
  }

  private final static int permissionCols = 5;

  private void readPermissions(Integer taskId, File taskDir)
          throws IOException, InvalidValueException, WetoTimeStampException,
                 ObjectNotValidException, SQLException, WetoActionException
  {
    File file;
    try(CsvListReader permissions = new WetoCsvReader(file = new File(taskDir,
            "permissions.txt")))
    {
      List<String> row;
      while((row = permissions.read()) != null)
      {
        if(row.size() == permissionCols)
        {
          String login = row.get(0);
          if(!discardLoginSet.contains(login))
          {
            Permission permission = new Permission();
            permission.setTaskId(taskId);
            Integer userId = null;
            if(login != null)
            {
              userId = loginIdMap.get(login);
              if(userId == null)
              {
                userId = teacherUserId;
              }
            }
            permission.setUserRefId(userId);
            Integer permissionRefType = null;
            String permissionRefName = row.get(1);
            for(PermissionRefType type : PermissionRefType.values())
            {
              if(permissionRefName.equals(getText(type.getProperty())))
              {
                permissionRefType = type.getValue();
                break;
              }
            }
            permission.setUserRefType(permissionRefType);
            Integer permissionType = null;
            String permissionName = row.get(2);
            if(permissionName != null)
            {
              for(PermissionType type : PermissionType.values())
              {
                if(permissionName.equals(getText(type.getProperty())))
                {
                  permissionType = type.getValue();
                  break;
                }
              }
            }
            permission.setType(permissionType);
            permission.setStartDate(WetoTimeStamp.stringToStamp(row.get(3)));
            permission.setEndDate(WetoTimeStamp.stringToStamp(row.get(4)));
            permission.insert(courseConn);
          }
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
  }

  private final static int gradeCols = 6;

  private void readGrades(Integer taskId, File taskDir, boolean isLeafTask)
          throws IOException, InvalidValueException, ObjectNotValidException,
                 SQLException, WetoActionException, WetoTimeStampException,
                 NoSuchItemException
  {
    File file;
    try(CsvListReader grades = new WetoCsvReader(file = new File(taskDir,
            "grades.txt")))
    {
      List<String> row;
      while((row = grades.read()) != null)
      {
        if(row.size() == gradeCols)
        {
          String receiverLogin = row.get(1);
          String reviewerLogin = row.get(5);
          if(loginIdMap.containsKey(receiverLogin) && !discardLoginSet.contains(
                  reviewerLogin))
          {
            Grade grade = new Grade();
            grade.setTaskId(taskId);
            grade.setReceiverId(loginIdMap.get(receiverLogin));
            grade.setMark(atof(row.get(2)));
            Integer gradeStatus;
            if(isLeafTask || (reviewerLogin != null))
            {
              gradeStatus = GradeStatus.UNSPECIFIED.getValue();
              String gradeStatusName = row.get(3);
              if(gradeStatusName != null)
              {
                for(GradeStatus status : GradeStatus.values())
                {
                  if(gradeStatusName.equals(getText(status.getProperty())))
                  {
                    gradeStatus = status.getValue();
                    break;
                  }
                }
              }
            }
            else
            {
              gradeStatus = GradeStatus.AGGREGATE.getValue();
            }
            grade.setStatus(gradeStatus);
            Integer reviewerId = null;
            if(reviewerLogin != null)
            {
              reviewerId = loginIdMap.get(reviewerLogin);
              if(reviewerId == null)
              {
                reviewerId = teacherUserId;
              }
            }
            grade.setReviewerId(reviewerId);
            String timeString = row.get(4);
            if((timeString != null) && !timeString.isEmpty())
            {
              grade.insert(courseConn, WetoTimeStamp.stringToStamp(timeString));
            }
            else
            {
              grade.insert(courseConn);
            }
            gradeIdMap.put(Integer.valueOf(row.get(0)), grade.getId());
          }
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
    try(CsvListReader reviews = new WetoCsvReader(file = new File(taskDir,
            "reviews.txt")))
    {
      List<String> row;
      while((row = reviews.read()) != null)
      {
        if(row.size() == tagCols)
        {
          Integer gradeId = gradeIdMap.get(atoi(row.get(1)));
          if(gradeId != null)
          {
            Tag tag = new Tag();
            tag.setTaggedId(gradeIdMap.get(atoi(row.get(1))));
            Integer userId = loginIdMap.get(row.get(3));
            if(userId == null)
            {
              userId = teacherUserId;
            }
            tag.setAuthorId(userId);
            tag.setStatus(atoi(row.get(4)));
            tag.setRank(atoi(row.get(5)));
            tag.setText(row.get(6));
            tag.setType(TagType.REVIEW.getValue());
            tag.insert(courseConn, WetoTimeStamp.stringToStamp(row.get(2)));
          }
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
    readTaskTags(courseConn, taskId, taskDir, "review_instructions.txt",
            TagType.REVIEW_INSTRUCTIONS.getValue(), null);
    try(FileReader properties = new FileReader(new File(taskDir, "scoring.txt")))
    {
      WetoProperties scoringProps = new WetoProperties(properties);
      Scoring scoring;
      boolean doUpdate = false;
      try
      {
        scoring = Scoring.select1ByTaskId(courseConn, taskId);
        doUpdate = true;
      }
      catch(NoSuchItemException e)
      {
        scoring = new Scoring();
        scoring.setTaskId(taskId);
      }
      String gradeTable = scoringProps.getProperty("gradeTable");
      if(gradeTable != null)
      {
        scoring.setGradeTable(gradeTable);
        scoringProps.removeProperty(gradeTable);
      }
      for(String key : scoringProps.getPropertySet())
      {
        scoring.setProperty(key, scoringProps.getProperty(key));
      }
      // Map the the aggregate function name to its id. The default is sum.
      Integer functionType = AggregateFunctionType.SUM.getValue();
      String functionName = scoring.getProperty("aggregateFunction");
      if(functionName != null)
      {
        for(AggregateFunctionType type : AggregateFunctionType.values())
        {
          if(functionName.equals(getText(type.getProperty())))
          {
            functionType = type.getValue();
            break;
          }
        }
      }
      scoring.setProperty("aggregateFunction", functionType.toString());
      if(doUpdate)
      {
        scoring.update(courseConn);
      }
      else
      {
        scoring.insert(courseConn);
      }
    }
    catch(FileNotFoundException e)
    {
    }
  }

  private class DirFilter implements DirectoryStream.Filter<Path>
  {
    @Override
    public boolean accept(Path entry) throws IOException
    {
      return entry.toFile().isDirectory();
    }

  }

  private final static int submissionCols = 7;

  private void readSubmissions(Integer taskId, File taskDir)
          throws IOException, InvalidValueException, SQLException,
                 ObjectNotValidException, WetoActionException,
                 WetoTimeStampException,
                 NoSuchItemException
  {
    File submissionsDir = new File(taskDir, "submissions");
    if(submissionsDir.exists() && submissionsDir.isDirectory())
    {
      Integer submitterId = null;
      File file;
      Map<Integer, Integer> submissionIdMap = new HashMap<>();
      Map<Integer, Integer> submissionTimeStampMap = new HashMap<>();
      try(CsvListReader submissions = new WetoCsvReader(file = new File(
              submissionsDir, "submissions.txt")))
      {
        List<String> row;
        while((row = submissions.read()) != null)
        {
          if(row.size() == submissionCols)
          {
            String login = row.get(1);
            if(submitterLoginSet.contains(login) || !discardLoginSet.contains(
                    login))
            {
              Submission submission = new Submission();
              submission.setTaskId(taskId);
              submitterId = loginIdMap.get(login);
              if(submitterId == null)
              {
                submitterId = teacherUserId;
              }
              submission.setUserId(submitterId);
              submission.setAutoGradeMark(atoi(row.get(3)));

              // Saving as null will cause DB errors, change default value to "NOT SUBMITTED"
              Integer submissionStatus = SubmissionStatus.NOT_SUBMITTED
                      .getValue();
              String submissionStatusName = row.get(4);
              if(submissionStatusName != null)
              {
                for(SubmissionStatus status : SubmissionStatus.values())
                {
                  if(submissionStatusName.equalsIgnoreCase(getText(status
                          .getProperty())))
                  {
                    submissionStatus = status.getValue();
                    break;
                  }
                }
              }
              submission.setStatus(submissionStatus);
              submission.setMessage(row.get(5));
              Integer submissionError = null;
              String submissionErrorName = row.get(6);
              if(submissionErrorName != null)
              {
                for(SubmissionError error : SubmissionError.values())
                {
                  if(submissionErrorName.equals(getText(error.getProperty())))
                  {
                    submissionError = error.getValue();
                    break;
                  }
                }
              }
              submission.setError(submissionError);
              Integer timeStamp = WetoTimeStamp.stringToStamp(row.get(2));
              submission.insert(courseConn, timeStamp);
              Integer submissionId = atoi(row.get(0));
              submissionIdMap.put(submissionId, submission.getId());
              submissionTimeStampMap.put(submissionId, timeStamp);
            }
          }
          else
          {
            throw new WetoActionException(getText("uploadTask.error.corrupt")
                    + "(" + file.getPath() + ": " + row + ")");
          }
        }
      }
      catch(FileNotFoundException e)
      {
      }
      try(DirectoryStream<Path> userDirs = Files.newDirectoryStream(
              submissionsDir.toPath(), new DirFilter()))
      {
        for(Path userDir : userDirs)
        {
          try(DirectoryStream<Path> submissionDirs = Files.newDirectoryStream(
                  userDir, new DirFilter()))
          {
            for(Path submissionDir : submissionDirs)
            {
              File dir = submissionDir.toFile();
              String[] idAndTime = dir.getName().split("_", 0);
              if(idAndTime.length == 2)
              {
                Integer id = Integer.valueOf(idAndTime[0]);
                if(submissionIdMap.containsKey(id))
                {
                  for(File submissionFile : dir.listFiles())
                  {
                    Document submissionDoc = DocumentModel.storeDocument(
                            courseConn, submissionFile, submissionFile.getName());
                    submissionDoc.update(courseConn, submissionTimeStampMap.get(
                            id));
                    SubmissionDocument link = new SubmissionDocument();
                    link.setSubmissionId(submissionIdMap.get(id));
                    link.setDocumentId(submissionDoc.getId());
                    link.insert(courseConn);
                  }
                }
              }
              else
              {
                throw new WetoActionException(getText(
                        "uploadTask.error.corrupt") + "(" + dir.getPath()
                        + ")");
              }
            }
          }
        }
      }
      try(FileReader pr = new FileReader(new File(submissionsDir,
              "submissionProperties.txt")))
      {
        WetoProperties sp = new WetoProperties(pr);
        SubmissionProperties properties = new SubmissionProperties();
        properties.setTaskId(taskId);
        StringWriter sw = new StringWriter();
        sp.write(sw);
        properties.setProperties(sw.toString());
        properties.insert(courseConn);
      }
      catch(FileNotFoundException e)
      {
      }
      Integer testDocId = null;
      File testDocZip = new File(submissionsDir, "autograding.zip");
      if(testDocZip.exists())
      {
        Document testDocument = DocumentModel
                .storeDocument(courseConn, testDocZip, "autograding.zip");
        testDocId = testDocument.getId();
      }
      try(FileReader pr = new FileReader(new File(submissionsDir,
              "autograding.txt")))
      {
        WetoProperties ap = new WetoProperties(pr);
        AutoGrading ag = new AutoGrading();
        ag.setTaskId(taskId);
        StringWriter sw = new StringWriter();
        ap.write(sw);
        ag.setProperties(sw.toString());
        ag.setTestDocId(testDocId);
        ag.insert(courseConn);
      }
      catch(FileNotFoundException e)
      {
      }
    }
  }

  private static final int groupCols = 3;

  private void readGroups(Integer taskId, File taskDir)
          throws IOException, InvalidValueException, ObjectNotValidException,
                 SQLException, WetoActionException
  {
    File file;
    try(CsvListReader groups = new WetoCsvReader(file = new File(taskDir,
            "groups.txt")))
    {
      List<String> row;
      while((row = groups.read()) != null)
      {
        if(row.size() == groupCols)
        {
          UserGroup group = new UserGroup();
          group.setTaskId(taskId);
          Integer groupType = null;
          String groupTypeName = row.get(1);
          if(groupTypeName != null)
          {
            for(GroupType type : GroupType.values())
            {
              if(groupTypeName.equals(getText(type.getProperty())))
              {
                groupType = type.getValue();
                break;
              }
            }
          }
          group.setType(groupType);
          group.setName(row.get(2));
          group.insert(courseConn);
          if((row = groups.read()) != null)
          {
            for(String login : row)
            {
              Integer id = loginIdMap.get(login);
              if(id != null)
              {
                GroupMember member = new GroupMember();
                member.setTaskId(taskId);
                member.setGroupId(group.getId());
                member.setUserId(id);
                member.insert(courseConn);
              }
            }
          }
          else
          {
            throw new WetoActionException(getText("uploadTask.error.corrupt")
                    + "(" + file.getPath() + ": " + row + ")");
          }
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
  }

  private class FileFilter implements DirectoryStream.Filter<Path>
  {
    @Override
    public boolean accept(Path entry) throws IOException
    {
      return entry.toFile().isFile();
    }

  }

  private static final int documentCols = 5;

  private void readDocuments(Integer taskId, File taskDir)
          throws IOException, InvalidValueException, SQLException,
                 ObjectNotValidException, WetoActionException,
                 NoSuchItemException
  {
    File documentsDir = new File(taskDir, "documents");
    if(documentsDir.exists() && documentsDir.isDirectory())
    {
      File file;
      Map<String, String> docNameMap = new HashMap<>();
      Map<String, Integer> docNameIdMap = new HashMap<>();
      Map<String, TaskDocument> docTaskDocMap = new HashMap<>();
      try(CsvListReader taskDocs = new WetoCsvReader(file = new File(taskDir,
              "documents.txt")))
      {
        List<String> row;
        while((row = taskDocs.read()) != null)
        {
          if(row.size() == documentCols)
          {
            String diskFileName = row.get(4);
            docNameMap.put(diskFileName, row.get(3));
            docNameIdMap.put(diskFileName, Integer.valueOf(row.get(0)));
            TaskDocument taskDoc = new TaskDocument();
            taskDoc.setTaskId(taskId);
            Integer id = loginIdMap.get(row.get(1));
            taskDoc.setUserId((id != null) ? id : teacherUserId);
            Integer docStatus = TaskDocumentStatus.PRIVATE.getValue();
            String docStatusName = row.get(2);
            if(docStatusName != null)
            {
              for(TaskDocumentStatus status : TaskDocumentStatus.values())
              {
                if(docStatusName.equals(getText(status.getProperty())))
                {
                  if(!(status.equals(TaskDocumentStatus.GROUP) || status.equals(
                          TaskDocumentStatus.GROUP_PUBLIC)))
                  {
                    docStatus = status.getValue();
                  }
                  break;
                }
              }
            }
            taskDoc.setStatus(docStatus);
            docTaskDocMap.put(diskFileName, taskDoc);
          }
          else
          {
            throw new WetoActionException(getText("uploadTask.error.corrupt")
                    + "(" + file.getPath() + ": " + row + ")");
          }
        }
      }
      catch(FileNotFoundException e)
      {
      }
      try(DirectoryStream<Path> docFiles = Files.newDirectoryStream(
              documentsDir.toPath(), new FileFilter()))
      {
        for(Path docPath : docFiles)
        {
          File docFile = docPath.toFile();
          String docFileName = docFile.getName();
          String docName = docNameMap.get(docFileName);
          TaskDocument link = docTaskDocMap.get(docFileName);
          if((docName == null) || (link == null))
          {
            throw new WetoActionException(getText("uploadTask.error.corrupt")
                    + "(" + docFile.getPath() + ")");
          }
          Document document = DocumentModel.storeDocument(courseConn, docFile,
                  docName);
          Integer documentId = document.getId();
          link.setDocumentId(documentId);
          link.insert(courseConn);
          taskDocumentIdMap.put(docNameIdMap.get(docFileName), new Integer[]
          {
            documentId, taskId
          });
        }
      }
    }
  }

  private void readTaskTags(Connection conn, Integer taskId, File taskDir,
          String tagFileName, Integer tagType, Integer commonUserId)
          throws IOException, InvalidValueException, ObjectNotValidException,
                 SQLException, WetoTimeStampException, WetoActionException
  {
    File file;
    try(CsvListReader tags = new WetoCsvReader(file = new File(taskDir,
            tagFileName)))
    {
      List<String> row;
      while((row = tags.read()) != null)
      {
        if(row.size() == tagCols)
        {
          Tag tag = new Tag();
          tag.setTaggedId(taskId);
          Integer userId = commonUserId;
          if(userId == null)
          {
            String login = row.get(3);
            userId = loginIdMap.get(login);
            if(userId == null)
            {
              userId = teacherUserId;
            }
          }
          tag.setAuthorId(userId);
          tag.setStatus(atoi(row.get(4)));
          tag.setRank(atoi(row.get(5)));
          tag.setText(row.get(6));
          tag.setType(tagType);
          tag.insert(conn, WetoTimeStamp.stringToStamp(row.get(2)));
        }
        else
        {
          throw new WetoActionException(getText("uploadTask.error.corrupt")
                  + "(" + file.getPath() + ": " + row + ")");
        }
      }
    }
    catch(FileNotFoundException e)
    {
    }
  }

  private Integer atoi(String s)
  {
    Integer result = null;
    if(s != null)
    {
      try
      {
        result = Integer.valueOf(s);
      }
      catch(NumberFormatException e)
      {
      }
    }
    return result;
  }

  private Float atof(String s)
  {
    Float result = null;
    if(s != null)
    {
      try
      {
        result = Float.valueOf(s);
      }
      catch(NumberFormatException e)
      {
      }
    }
    return result;
  }

  public void setTaskFile(File taskFile)
  {
    this.taskFile = taskFile;
  }

  public void setZipRootDir(String zipRootDir)
  {
    this.zipRootDir = zipRootDir;
  }

  public void setSubmitterLogins(String submitterLogins)
  {
    this.submitterLogins = submitterLogins;
  }

  public void setStudentLogins(String studentLogins)
  {
    this.studentLogins = studentLogins;
  }

  public void setAutoSelectFirst(boolean autoSelectFirst)
  {
    this.autoSelectFirst = autoSelectFirst;
  }

}
