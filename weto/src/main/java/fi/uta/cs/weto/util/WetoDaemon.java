package fi.uta.cs.weto.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.NullNotAllowedException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.AutoGradeTestScore;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.GradingModel;
import fi.uta.cs.weto.model.QuestionBean;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.SubmissionError;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipException;
import javax.mail.MessagingException;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FileUtils;

/**
 * WetoDaemon for processing the autoGradeJobQueue.
 *
 * Queue priority values correspond to test phases as follows: 1: immediate
 * public 2: immediate private 3: final public 4: final private
 */
public class WetoDaemon extends Thread
{
  private final String[] URLs;
  private final String[] users;
  private final String[] passwords;
  private final Connection[] conns;
  private final int pollDatabaseInterval;
  private final int minDatabaseInterval;
  private final int printInterval;
  private final int sleepOnError;
  private final long reconnectInterval;
  private final int timeout;
  private final String checkerBaseDir;
  private final String pathToTasksDir;
  private final String pathToTargetDir;
  private final String pathToCompileDir;
  private final String cParameters;
  private final String cppParameters;
  private final int fullFeedback;
  private final int resMaxLength;
  private final int resShortLength;
  private final int portNumber;
  private final String MESSAGE_NEWLINE;
  private static final String EOF = "<EOF>";

  private final ServerSocket serverSocket;

  private Socket clientSocket = null;
  ObjectOutputStream clientObjectOut = null;
  BufferedReader clientReader = null;
  private Integer jobCounter = 0;

  private String errors;
  private int firstTest;
  private int lastTest;
  private int nextPhase;

  public static void main(String args[]) throws IOException
  {
    WetoDaemon wetoD = null;
    try
    {
      wetoD = new WetoDaemon(args[0]);
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
      System.exit(1);
    }
    wetoD.start();
  }

  public WetoDaemon(String propertiesFilename) throws IOException
  {
    Properties properties = new Properties();
    try(FileInputStream fis = new FileInputStream(propertiesFilename))
    {
      properties.load(fis);
    }
    ArrayList<String> urlList = new ArrayList<>();
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<String> passwordList = new ArrayList<>();
    urlList.add(properties.getProperty("URL"));
    userList.add(properties.getProperty("user"));
    passwordList.add(properties.getProperty("password"));
    String URL;
    for(int i = 2; (URL = properties.getProperty("URL" + i)) != null; ++i)
    {
      urlList.add(URL);
      userList.add(properties.getProperty("user" + i));
      passwordList.add(properties.getProperty("password" + i));
    }
    portNumber = Integer.parseInt(properties.getProperty("portNumber"));
    serverSocket = new ServerSocket(portNumber);
    URLs = urlList.toArray(new String[0]);
    users = userList.toArray(new String[0]);
    passwords = passwordList.toArray(new String[0]);
    conns = new Connection[URLs.length];
    try
    {
      boolean someConnectionOk = false;
      for(int i = 0; i < conns.length; ++i)
      {
        conns[i] = getConnection(URLs[i], users[i], passwords[i]);
        if(conns[i] != null)
        {
          System.out.println("Connection " + URLs[i] + " opened OK");
          someConnectionOk = true;
        }
      }
      if(!someConnectionOk)
      {
        throw new SQLException("Could not create any database connections!");
      }
      Runtime.getRuntime().addShutdownHook(
              new Thread()
      {
        @Override
        public void run()
        {
          try
          {
            serverSocket.close();
            for(int i = 0; i < conns.length; ++i)
            {
              if(conns[i] != null)
              {
                conns[i].close();
              }
            }
            System.out.println("Connections closed, now extiting...");
          }
          catch(SQLException | IOException e)
          {
            e.printStackTrace();
          }
        }

      }
      );
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    pollDatabaseInterval = Integer.parseInt(properties.getProperty(
            "pollDatabaseInterval"));
    minDatabaseInterval = Integer.parseInt(properties.getProperty(
            "minDatabaseInterval"));
    printInterval = Integer.parseInt(properties.getProperty(
            "idlingPrintInterval"));
    sleepOnError = Integer.parseInt(properties.getProperty("sleepOnError"));
    reconnectInterval = Long.parseLong(properties.getProperty(
            "reconnectInterval"));
    timeout = Integer.parseInt(properties.getProperty("timeout"));

    checkerBaseDir = properties.getProperty("checkerBaseDir");
    pathToTasksDir = checkerBaseDir + "/" + properties
            .getProperty("tasksSubDir");
    pathToTargetDir = checkerBaseDir + "/" + properties.getProperty(
            "targetSubDir");
    pathToCompileDir = properties.getProperty("pathToCompileDir");
    cParameters = properties.getProperty("cParameters");
    cppParameters = properties.getProperty("cppParameters");
    fullFeedback = Integer.parseInt(properties.getProperty("fullFeedback"));
    resMaxLength = Integer.parseInt(properties.getProperty("resMaxLength"));
    resShortLength = Integer.parseInt(properties.getProperty("resShortLength"));
    MESSAGE_NEWLINE = properties.getProperty("messageNewline", "\n");
    DriverManager.setLoginTimeout(Integer.max(1, sleepOnError / 1000));
  }

  @Override
  public void run()
  {
    final String pollQuery = "queuephase < " + AutoGradeJobQueue.FINAL_PUBLIC
            + " AND testrunning=false ORDER BY timestamp LIMIT 10";
    Connection conn;
    int currSleep = pollDatabaseInterval;
    int currIdling = 0;
    final long[] reconnectTimes = new long[URLs.length];
    for(int connId = 0; true; connId = (connId + 1) % conns.length)
    {
      conn = conns[connId];
      try
      {
        if(conn == null)
        {
          throw new SQLException();
        }
        Iterator<AutoGradeJobQueue> jobs = AutoGradeJobQueue.selectionIterator(
                conn, pollQuery);
        if(jobs.hasNext())
        {
          currSleep = 0;
          while(jobs.hasNext())
          {
            processJob(conn, jobs.next(), connId);
          }
        }
        else
        {
          currSleep = (currSleep < minDatabaseInterval) ? minDatabaseInterval
                              : (currSleep > pollDatabaseInterval / 2)
                                        ? pollDatabaseInterval : 2 * currSleep;
        }
        conn.commit();
      }
      catch(Exception e)
      {
        currSleep = sleepOnError;
        boolean doNotReconnect = false;
        long currTime = System.currentTimeMillis();
        if((currTime - reconnectTimes[connId]) < reconnectInterval)
        {
          doNotReconnect = true;
        }
        try
        {
          if(conn != null)
          {
            conn.rollback();
            doNotReconnect = true;
          }
        }
        catch(SQLException ex)
        {
          conns[connId] = null;
        }
        if(doNotReconnect == false)
        {
          reconnectTimes[connId] = System.currentTimeMillis();
          conns[connId] = getConnection(URLs[connId], users[connId],
                  passwords[connId]);
        }
      }
      finally // Sleep between database polling
      {
        try
        {
          Thread.sleep(currSleep);
          currIdling += currSleep;
          if(currIdling >= printInterval)
          {
            System.out.print("-");
            currIdling = 0;
          }
        }
        catch(InterruptedException e)
        {
        }
      }
    }
  }

  /**
   * @param conn Connection to use
   * @param jobs Jobs to run
   * @throws InvalidValueException
   * @throws SQLException
   * @throws ObjectNotValidException
   * @throws NoSuchItemException
   */
  private void processJob(Connection conn, AutoGradeJobQueue agjq,
          final int connId)
          throws Exception
  {
    final boolean isQuizJob
                          = (AutoGradeJobQueue.INLINE_JOB == agjq.getJobType());
    Submission submission = null;
    Tag quizScoreTag = null;
    try
    {
      agjq.setTestRunning(true);
      agjq.update(conn);
      Tag quizQuestionTag = null;
      Tag quizAnswerTag = null;
      JsonObject quizScoreJson = null;
      if(!isQuizJob)
      {
        submission = Submission.select1ById(conn, agjq.getRefId());
        // Status of quiz jobs is left untouched
        if(!SubmissionStatus.PROCESSING.getValue()
                .equals(submission.getStatus()))
        {
          submission.setStatus(SubmissionStatus.PROCESSING.getValue());
          submission.update(conn, false);
        }
        conn.commit();
      }
      else
      {
        quizAnswerTag = Tag.select1ById(conn, agjq.getRefId());
        quizQuestionTag = Tag.select1ById(conn, quizAnswerTag.getRank());
        try
        {
          quizScoreTag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
                  agjq.getTaskId(), quizAnswerTag.getId(), quizAnswerTag
                  .getAuthorId(), TagType.QUIZ_SCORE.getValue());
          quizScoreTag.setStatus(SubmissionStatus.PROCESSING.getValue());
          quizScoreTag.setText(null);
          quizScoreTag.update(conn);
        }
        catch(NoSuchItemException e)
        {
          quizScoreTag = new Tag();
          quizScoreTag.setType(TagType.QUIZ_SCORE.getValue());
          quizScoreTag.setTaggedId(agjq.getTaskId());
          quizScoreTag.setRank(quizAnswerTag.getId());
          quizScoreTag.setAuthorId(quizAnswerTag.getAuthorId());
          quizScoreTag.setStatus(SubmissionStatus.PROCESSING.getValue());
          quizScoreTag.insert(conn);
        }
        quizScoreJson = new JsonObject();
      }
      boolean runTest = true;
      int phase = agjq.getQueuePhase();
      while(runTest && (phase >= 0))
      {
        runTest
                = process(conn, agjq, submission, quizQuestionTag, quizAnswerTag,
                        quizScoreJson, phase, isQuizJob, connId);
        phase = nextPhase;
        conn.commit();
      }
      if(nextPhase >= 0) // Convention: active phases are non-negative
      {
        agjq.setTestRunning(false);
        agjq.setQueuePhase(nextPhase);
        agjq.update(conn);
      }
      else // Negative phase --> the job is no longer active and is thus deleted.
      {
        agjq.delete(conn);
        if(!isQuizJob)
        {
          if(nextPhase == AutoGradeJobQueue.END_ACCEPTED)
          {
            submission.setStatus(SubmissionStatus.ACCEPTED.getValue());
          }
          else if(nextPhase == AutoGradeJobQueue.END_NOT_ACCEPTED)
          {
            submission.setStatus(SubmissionStatus.NOT_ACCEPTED.getValue());
          }
          else if(nextPhase == AutoGradeJobQueue.END_TEST_RUN)
          {
            submission.setStatus(SubmissionStatus.NOT_SUBMITTED.getValue());
          }
          else if(nextPhase == AutoGradeJobQueue.ERROR_IN_TEST)
          {
            submission.setStatus(SubmissionStatus.NOT_ACCEPTED.getValue());
          }
          submission.update(conn, false);
        }
        else
        {
          quizScoreTag.setText(quizScoreJson.toString());
          if(nextPhase == AutoGradeJobQueue.END_ACCEPTED)
          {
            quizScoreTag.setStatus(SubmissionStatus.ACCEPTED.getValue());
          }
          else if(nextPhase == AutoGradeJobQueue.END_TEST_RUN)
          {
            quizScoreTag.setStatus(SubmissionStatus.NOT_SUBMITTED.getValue());
          }
          else
          {
            quizScoreTag.setStatus(SubmissionStatus.NOT_ACCEPTED.getValue());
          }
          quizScoreTag.update(conn);
        }
      }
    }
    catch(Exception e)
    {
      System.out.println("<!" + e.toString() + "!>");
      e.printStackTrace();
      agjq.delete(conn);
      if(!isQuizJob)
      {
        submission.setStatus(SubmissionStatus.NOT_ACCEPTED.getValue());
        submission.update(conn, false);
      }
      else
      {
        quizScoreTag.setStatus(SubmissionStatus.NOT_ACCEPTED.getValue());
        quizScoreTag.update(conn);
      }
    }
  }

  public boolean process(Connection conn, AutoGradeJobQueue agjq,
          Submission submission, Tag quizQuestionTag, Tag quizAnswerTag,
          JsonObject quizScoreJson, int phase, final boolean isQuizJob,
          final int connId)
          throws Exception
  {
    boolean chainImmediate = false;
    final int taskId = agjq.getTaskId();
    final boolean isTestRun = (phase == AutoGradeJobQueue.TEST_RUN);
    jobCounter += 1;
    try
    {
      Properties properties = new Properties();
      String taskSubDir = null;
      File pckgFileDir = null;
      String quizAnswerText = null;
      Integer quizQuestionId = null;
      String[] questionTexts = null;
      QuestionBean qb = null;
      if(!isQuizJob)
      {
        AutoGrading ag = AutoGrading.select1ByTaskId(conn, taskId);
        properties.load(new StringReader(ag.getProperties()));
        Document pckg = Document.select1ById(conn, ag.getTestDocId());
        taskSubDir = Integer.toString(connId) + "_" + agjq.getDbId() + "_"
                + taskId + "_" + pckg.getId();
        pckgFileDir = new File(pathToTasksDir, taskSubDir);
        //Get autograde documents only once
        if(!pckgFileDir.exists())
        {
          runCommand(new String[]
          {
            "/bin/bash", "-c", "rm -f -R " + pathToTasksDir + "/" + Integer
            .toString(connId) + "_" + agjq.getDbId() + "_" + taskId + "_*"
          });
          if(!writeSubmissionTestData(conn, pckg, pckgFileDir, properties))
          {
            submissionCompilationError(submission, "Internal grader error!",
                    false);
            nextPhase = AutoGradeJobQueue.ERROR_IN_TEST;
            return false;
          }
        }
      }
      else
      {
        quizQuestionId = quizQuestionTag.getId();
        QuizModel quiz = new QuizModel(conn, taskId, null);
        quiz.readQuestion(quizQuestionId);
        qb = quiz.getQuestions().get(0);
        QuizModel.populateAnswers(qb, quizAnswerTag);
        questionTexts = qb.getQuestionTexts();
        if(qb.getUserAnswers() != null)
        {
          quizAnswerText = qb.getUserAnswers()[0][0];
        }
        if(quizAnswerText == null)
        {
          quizAnswerText = "\n";
        }
        else
        {
          quizAnswerText = quizAnswerText + "\n";
        }
        taskSubDir = Integer.toString(connId) + "_" + agjq.getDbId() + "_"
                + taskId + "_" + quizQuestionId + "_" + quizQuestionTag
                .getTimeStamp();
        pckgFileDir = new File(pathToTasksDir, taskSubDir);
        File propFile = new File(pckgFileDir, "properties.txt");
        //Get autograde documents only once
        if(!pckgFileDir.exists())
        {
          runCommand(new String[]
          {
            "/bin/bash", "-c", "rm -f -R " + pathToTasksDir + "/" + Integer
            .toString(connId) + "_" + agjq.getDbId() + "_" + taskId + "_"
            + quizQuestionId + "_*"
          });
          // writeQuizTestData also reads properties.
          if(!writeQuizTestData(qb, pckgFileDir, propFile, properties))
          {
            quizCompilationError(quizScoreJson, "Internal grader error!", false);
            nextPhase = AutoGradeJobQueue.ERROR_IN_TEST;
            return false;
          }
        }
        else
        {
          try(FileInputStream fis = new FileInputStream(propFile))
          {
            properties.load(fis);
          }
        }
      }
      final int imm_public = Integer.parseInt(properties.getProperty(
              "immediatePublicTests"));
      final int imm_private = Integer.parseInt(properties.getProperty(
              "immediatePrivateTests"));
      final int fin_public = Integer.parseInt(properties.getProperty(
              "finalPublicTests"));
      final int fin_private = Integer.parseInt(properties.getProperty(
              "finalPrivateTests"));
      chainImmediate = Boolean.parseBoolean(properties.getProperty(
              "chainImmediateTests"));
      nextPhase = AutoGradeJobQueue.END_ACCEPTED;
      Integer testRunTestNumber = null;
      if(isTestRun)
      {
        nextPhase = AutoGradeJobQueue.END_TEST_RUN;
        try
        {
          testRunTestNumber = Integer.parseInt(agjq.getJobComment());
        }
        catch(Exception e)
        {
          testRunTestNumber = 1;
        }
      }
      else if(phase == AutoGradeJobQueue.IMMEDIATE_PUBLIC && imm_private > 0)
      {
        nextPhase = AutoGradeJobQueue.IMMEDIATE_PRIVATE;
      }
      else if(phase <= AutoGradeJobQueue.IMMEDIATE_PRIVATE && fin_public > 0)
      {
        nextPhase = AutoGradeJobQueue.FINAL_PUBLIC;
      }
      else if(phase <= AutoGradeJobQueue.FINAL_PUBLIC && fin_private > 0)
      {
        nextPhase = AutoGradeJobQueue.FINAL_PRIVATE;
      }
      boolean failFast = false;
      boolean zeroTests = !isTestRun;
      float minScore = 0.0F;
      if(phase == AutoGradeJobQueue.IMMEDIATE_PUBLIC)
      {
        minScore = Float.parseFloat(properties.getProperty(
                "minImmediatePublicScore"));
        failFast = (imm_public == minScore);
        zeroTests = (imm_public == 0);
      }
      else if(phase == AutoGradeJobQueue.IMMEDIATE_PRIVATE)
      {
        minScore = Float.parseFloat(properties.getProperty(
                "minImmediatePrivateScore"));
        failFast = (imm_private == minScore);
        zeroTests = (imm_private == 0);
      }
      else if(phase == AutoGradeJobQueue.FINAL_PRIVATE)
      {
        minScore = Float.parseFloat(properties
                .getProperty("minFinalPublicScore"));
        failFast = (fin_public == minScore);
        zeroTests = (fin_public == 0);
      }
      else if(phase == AutoGradeJobQueue.FINAL_PRIVATE)
      {
        minScore = Float.parseFloat(properties.getProperty(
                "minFinalPrivateScore"));
        failFast = (fin_private == minScore);
        zeroTests = (fin_private == 0);
      }
      String lang = properties.getProperty("programmingLanguage");
      if(lang != null)
      {
        lang = lang.toLowerCase();
      }
      boolean hasFullError = false;
      try
      {
        File compileDir = new File(pathToCompileDir);
        if(compileDir.exists())
        {
          FileUtils.cleanDirectory(compileDir);
        }
        else
        {
          Files.createDirectory(compileDir.toPath());
        }
        // Fetch the submission. Also unzip and detect language, if necessary.
        // This will also ensure that lang is in lowercase.
        if(!isQuizJob)
        {
          lang = writeSubmissionAnswer(conn, submission, compileDir, lang);
        }
        else
        {
          // Add answer template epilogue and prologue, if exist
          String prologue = "";
          if((questionTexts[1] != null) && !questionTexts[1].isEmpty())
          {
            prologue = questionTexts[1] + "\n";
          }
          String epilogue = "";
          if((questionTexts[2] != null) && !questionTexts[2].isEmpty())
          {
            epilogue = questionTexts[2] + "\n";
          }
          String augmentedAnswer = prologue + quizAnswerText + epilogue;
          try(ByteArrayInputStream qas = new ByteArrayInputStream(
                  augmentedAnswer.getBytes()))
          {
            WetoUtilities.streamToFile(qas, new File(compileDir, properties
                    .getProperty("answerFileName")));
          }
        }
        if(lang == null)
        {
          throw new WetoActionException("Could not determine submission type!");
        }
        File targetDir = new File(pathToTargetDir);
        // delete extra files
        runCommand(new String[]
        {
          "/bin/bash", "-c", "rm -f -R " + targetDir + "/*"
        });
        properties.setProperty("_programType", lang);
        properties.setProperty("_taskSubDir", taskSubDir);
        String logNameString = isQuizJob ? quizQuestionId + "q" : Integer
                .toString(submission.getId()) + "s";
        properties.setProperty("_submissionId", logNameString);
        //Compile submission
        int compRes = compileSubmission(properties, lang, compileDir, targetDir,
                pckgFileDir);
        if(!errors.isEmpty())
        {
          Integer submitterId = null;
          if(isQuizJob)
          {
            hasFullError = quizCompilationError(quizScoreJson, errors, (compRes
                    == 0));
            submitterId = quizAnswerTag.getAuthorId();
          }
          else
          {
            hasFullError = submissionCompilationError(submission, errors,
                    (compRes == 0));
            submitterId = submission.getUserId();
          }
          if(hasFullError)
          {
            ArrayList<Tag> fullErrors = Tag.selectByAuthorIdAndType(conn,
                    submitterId, TagType.COMPILER_RESULT.getValue());
            Tag fullError;
            boolean doUpdate = false;
            if(!fullErrors.isEmpty())
            {
              fullError = fullErrors.get(0);
              doUpdate = true;
              for(int i = 1; i < fullErrors.size(); ++i)
              {
                fullErrors.get(i).delete(conn);
              }
            }
            else
            {
              fullError = new Tag();
              fullError.setAuthorId(submitterId);
              fullError.setType(TagType.COMPILER_RESULT.getValue());
            }
            fullError.setTaggedId(taskId);
            if(isQuizJob)
            {
              fullError.setRank(quizAnswerTag.getId());
            }
            else
            {
              fullError.setRank(submission.getId());
            }
            fullError.setStatus(errors.length());
            fullError.setText(WetoUtilities.stringToGzippedBase64(errors));
            if(doUpdate)
            {
              fullError.update(conn);
            }
            else
            {
              fullError.insert(conn);
            }
          }
          errors = "";
        }
        if(compRes != 0) // Compile error: mark the submission not accepted
        {
          nextPhase = AutoGradeJobQueue.END_NOT_ACCEPTED;
        }
        else if(!zeroTests)
        { // Compile was ok: now run the test
          if(testRunTestNumber != null)
          {
            int highTest = imm_public + imm_private + fin_public + fin_private;
            if(testRunTestNumber < 1)
            {
              testRunTestNumber = 1;
            }
            else if(testRunTestNumber > highTest)
            {
              testRunTestNumber = highTest;
            }
            firstTest = lastTest = testRunTestNumber;
          }
          else
          {
            firstTest = 1;
            lastTest = imm_public;
            if(phase >= AutoGradeJobQueue.IMMEDIATE_PRIVATE)
            {
              firstTest = lastTest + 1;
              lastTest += imm_private;
            }
            if(phase >= AutoGradeJobQueue.FINAL_PUBLIC)
            {
              firstTest = lastTest + 1;
              lastTest += fin_public;
            }
            if(phase >= AutoGradeJobQueue.FINAL_PRIVATE)
            {
              firstTest = lastTest + 1;
              lastTest += fin_private;
            }
          }
          properties.setProperty("firstTest", new Integer(firstTest)
                  .toString());
          properties.setProperty("lastTest", new Integer(lastTest).toString());
          properties.setProperty("taskId", new Integer(taskId).toString());
          properties.setProperty("isTestRun", new Boolean(isTestRun)
                  .toString());
          properties.setProperty("failFast", new Boolean(failFast).toString());
          properties.setProperty("jobCounter", jobCounter.toString());
          Float timeLimit = Float
                  .parseFloat(properties.getProperty("timeLimit"));
          Integer testsTimeout = timeout + (int) ((lastTest - firstTest + 1)
                  * timeLimit * 1000);
          properties.setProperty("testsTimeout", testsTimeout.toString());
          System.out.println("?" + logNameString + "?");
          // Writing the properties file to clientSocket triggers the test run
          if(clientSocket == null)
          {
            clientSocket = serverSocket.accept();
            clientSocket.setSoTimeout(timeout
                    + (int) ((lastTest - firstTest + 1) * timeLimit * 1000));
            clientObjectOut = new ObjectOutputStream(clientSocket
                    .getOutputStream());
            clientReader = new BufferedReader(new InputStreamReader(clientSocket
                    .getInputStream()));
          }
          boolean readOk = false;
          String inputLine = null;
          ArrayList<String> resultLines = new ArrayList<>();
          try
          {
            clientObjectOut.reset();
            clientObjectOut.writeObject(properties);
            while(((inputLine = clientReader.readLine()) != null) && !(readOk
                                                                               = inputLine
                    .startsWith(EOF)))
            {
              resultLines.add(inputLine);
            }
          }
          catch(Exception e)
          {
            try // Retry once more
            {
              resultLines.clear();
              try
              {
                clientSocket.close();
              }
              catch(Exception e2)
              {
              }
              clientSocket = serverSocket.accept();
              clientSocket.setSoTimeout(timeout + (int) ((lastTest - firstTest
                      + 1) * timeLimit * 1000));
              clientObjectOut = new ObjectOutputStream(clientSocket
                      .getOutputStream());
              clientReader = new BufferedReader(new InputStreamReader(
                      clientSocket.getInputStream()));
              clientObjectOut.reset();
              clientObjectOut.writeObject(properties);
              while(((inputLine = clientReader.readLine()) != null) && !(readOk
                                                                                 = inputLine
                      .startsWith(EOF)))
              {
                resultLines.add(inputLine);
              }
            }
            catch(Exception e2)
            {
              error(submission, quizScoreJson, conn, e2);
              readOk = false;
              try
              {
                clientSocket.close();
              }
              catch(Exception e3)
              {
              }
              clientSocket = null;
            }
          }
          if(readOk)
          {
            Integer readyJobCounter = null;
            try
            {
              readyJobCounter = Integer.parseInt(inputLine.substring(EOF
                      .length()));
            }
            catch(NumberFormatException e)
            {
            }
            if(jobCounter.equals(readyJobCounter))
            {
              // The results were received ok: fill test scores.
              fillTestScores(conn, taskId, phase, minScore, isTestRun, isQuizJob,
                      qb, quizAnswerTag, properties, submission, quizScoreJson,
                      resultLines);
            }
          }
          else
          {
            error(submission, quizScoreJson, conn, new Exception(
                    "Internal checker error!"));
          }
          // delete extra files
          runCommand(new String[]
          {
            "/bin/bash", "-c", "rm -f -R " + targetDir + "/*"
          });
        }
        else if((imm_public == 0) && (imm_private == 0) && (fin_public == 0)
                && (fin_private == 0) && (submission != null))
        {
          // If there are no tests to run, give point for successful compile
          submission.setAutoGradeMark(1);
        }
      }
      catch(IOException | NullNotAllowedException | MessagingException e)
      {
        error(submission, quizScoreJson, conn, e);
      }
      finally
      { // Remove possibly existing previous full compiler results
        if(!hasFullError)
        {
          Integer submitterId = isQuizJob ? quizAnswerTag.getAuthorId()
                                        : submission.getUserId();
          for(Tag error : Tag.selectByTaggedIdAndAuthorIdAndType(conn, taskId,
                  submitterId, TagType.COMPILER_RESULT.getValue()))
          {
            error.delete(conn);
          }
        }
      }
    }
    catch(Exception e)
    {
      error(submission, quizScoreJson, conn, e);
    }
    return (chainImmediate && (nextPhase
            == AutoGradeJobQueue.IMMEDIATE_PRIVATE));
  }

  /**
   * @param conn	connection to database
   * @param phase	test phase (public or private)
   * @param properties	properties to use
   * @param submissionId	submission id
   * @param submission	submission
   * @param resultFile	where is the results to write
   * @throws IOException
   * @throws InvalidValueException
   * @throws SQLException
   * @throws ObjectNotValidException
   * @throws NoSuchItemException
   */
  private void fillTestScores(Connection conn, Integer taskId, int phase,
          final float minScore, boolean isTestRun, boolean isQuizJob,
          QuestionBean qb, Tag quizAnswerTag, Properties properties,
          Submission submission, JsonObject quizScoreJson,
          ArrayList<String> resultLines)
          throws IOException, InvalidValueException, SQLException,
                 ObjectNotValidException, NoSuchItemException,
                 MessagingException, InterruptedException,
                 WetoTimeStampException, TooManyItemsException
  {
    Integer submissionRef = null;
    Integer submitterId = null;
    if(isQuizJob)
    {
      submissionRef = taskId;
      submitterId = quizAnswerTag.getAuthorId();
    }
    else
    {
      submissionRef = submission.getId();
      submitterId = submission.getUserId();
    }
    // fill the test scores
    float score = 0;
    float totalPhaseScore = 0;
    Integer testNo = firstTest - 1;
    String singleResult = null;
    boolean addedFullFeedback = false;
    // Try to get previous score from database
    if(!isQuizJob)
    {
      try
      {
        score = submission.getAutoGradeMark();
      }
      catch(Exception e)
      {
        //No submission score for previous tests
      }
    }
    if(resultLines.size() > (lastTest - firstTest + 1))
    {
      throw new MessagingException("Wrong line separator type?");
    }
    AutoGradeTestScore agTestScore = null;
    for(String line : resultLines)
    {
      testNo += 1;
      if(!isQuizJob)
      {
        agTestScore = new AutoGradeTestScore();
        agTestScore.setTestNo(testNo);
        agTestScore.setSubmissionId(submissionRef);
        agTestScore.setPhase(phase);
      }
      String splitLine[] = line.split(";", 4);
      if(splitLine.length < 3)
      {
        throw new MessagingException("Checker line error at test #" + testNo);
      }
      int testScore = Integer.parseInt(splitLine[0]);
      int processingTime = Integer.parseInt(splitLine[1]);
      String result = null;
      if(splitLine.length == 4)
      {
        int limit = (fullFeedback > 0) ? resMaxLength : resShortLength;
        if(splitLine[3].length() > limit)
        {
          result = splitLine[3].substring(0, limit).replace('\b', '\n');
        }
        else
        {
          result = splitLine[3].replace('\b', '\n');
        }
      }
      else
      {
        result = "";
      }
      if(result.length() > resShortLength)
      {
        if(!addedFullFeedback)
        {
          try
          {
            ArrayList<Tag> longResults = Tag.selectByAuthorIdAndType(conn,
                    submitterId, TagType.FEEDBACK.getValue());
            int removeCount = longResults.size() - fullFeedback + 1;
            Integer rankRef = isQuizJob ? quizAnswerTag.getId() : testNo;
            for(Tag oldResult : longResults)
            {
              if((removeCount-- > 0) || (submissionRef.equals(oldResult
                      .getTaggedId()) && rankRef.equals(oldResult.getRank())))
              {
                oldResult.delete(conn);
              }
            }
            Tag newResult = new Tag();
            newResult.setAuthorId(submitterId);
            newResult.setType(TagType.FEEDBACK.getValue());
            newResult.setTaggedId(submissionRef);
            newResult.setRank(rankRef);
            newResult.setStatus(result.length());
            newResult.setText(WetoUtilities.stringToGzippedBase64(result));
            newResult.insert(conn);
            addedFullFeedback = true;
          }
          catch(NoSuchItemException | TooManyItemsException | SQLException e)
          {
            System.out.println("<!Error updating long result!>");
          }
        }
        result = result.substring(0, resShortLength);
      }
      totalPhaseScore += testScore;
      if(!isQuizJob)
      {
        agTestScore.setTestScore(testScore);
        agTestScore.setProcessingTime(processingTime);
        agTestScore.setFeedback(result);
        agTestScore.insert(conn);
      }
      else
      {
        singleResult = result;
        if(testScore == 0)
        {
          totalPhaseScore = 0;
          break;
        }
      }
    }
    if(!addedFullFeedback)
    {
      ArrayList<Tag> longResults = Tag
              .selectByAuthorIdAndType(conn, submitterId, TagType.FEEDBACK
                      .getValue());
      Integer rankRef = isQuizJob ? quizAnswerTag.getId() : testNo;
      for(Tag oldResult : longResults)
      {
        if(submissionRef.equals(oldResult.getTaggedId()) && rankRef.equals(
                oldResult.getRank()))
        {
          oldResult.delete(conn);
        }
      }
    }
    if(!isTestRun)
    {
      boolean binarize = Boolean.parseBoolean(properties.getProperty(
              "binaryPhaseScore"));
      boolean privateOnly = Boolean.parseBoolean(properties.getProperty(
              "onlyPrivateScore"));
      boolean autoCreateGrade = Boolean.parseBoolean(properties.getProperty(
              "autoCreateGrade"));
      double factor = 1;
      String scoreFactor = properties.getProperty("scoreFactor");
      if(scoreFactor != null)
      {
        factor = Double.parseDouble(scoreFactor);
      }
      int errType = (phase == AutoGradeJobQueue.IMMEDIATE_PUBLIC)
                            ? SubmissionError.IMMEDIATEPUBLIC_TEST.getValue()
                            : (phase
              == AutoGradeJobQueue.IMMEDIATE_PRIVATE)
                                      ? SubmissionError.IMMEDIATEPRIVATE_TEST
                      .getValue() : (phase == AutoGradeJobQueue.FINAL_PUBLIC)
                                            ? SubmissionError.FINALPUBLIC_TEST
                              .getValue() : SubmissionError.FINALPRIVATE_TEST
                              .getValue();
      if(totalPhaseScore < minScore)
      { // Too few points from this test phase: not accepted.
        if(!isQuizJob)
        {
          submission.setError(errType);
        }
        if(binarize)
        {
          totalPhaseScore = 0;
        }
        totalPhaseScore *= factor;
        score += totalPhaseScore;
        //Skip rest of the tests because the task now already not accepted
        nextPhase = AutoGradeJobQueue.END_NOT_ACCEPTED;
      }
      else if(!(privateOnly && ((phase == AutoGradeJobQueue.IMMEDIATE_PUBLIC)
              || (phase == AutoGradeJobQueue.FINAL_PUBLIC))))
      {  // This test phase was accepted.
        // Check if only private scores should be added to total score
        if(binarize)
        {
          totalPhaseScore = 1;
        }
        totalPhaseScore *= factor;
        score += totalPhaseScore;
      }
      if(autoCreateGrade && ((nextPhase == AutoGradeJobQueue.END_NOT_ACCEPTED)
              || (nextPhase == AutoGradeJobQueue.END_ACCEPTED)))
      { // If this was the last test phase, update grade if necessary.
        Float testMark = null;
        if(isQuizJob)
        {
          for(Tag otherScoreTag : Tag.selectByTaggedIdAndAuthorIdAndType(conn,
                  taskId, submitterId, TagType.QUIZ_SCORE.getValue()))
          {
            if(!otherScoreTag.getRank().equals(quizAnswerTag.getId()))
            {
              String scoreJson = otherScoreTag.getText();
              if(scoreJson != null)
              {
                JsonObject scoreRootJson = new JsonParser().parse(scoreJson)
                        .getAsJsonObject();
                if(scoreRootJson.get("mark") != null)
                {
                  Float otherMark = scoreRootJson.get("mark").getAsFloat();
                  if(otherMark != null)
                  {
                    testMark = (testMark == null) ? otherMark : testMark
                            + otherMark;
                  }
                }
              }
            }
          }
        }
        Grade grade = null;
        for(Grade oldGrade : Grade.selectAnonymousByTaskIdAndReceiverId(conn,
                taskId, submitterId))
        {
          if(!GradeStatus.AGGREGATE.getValue().equals(oldGrade.getStatus()))
          {
            grade = oldGrade;
            break;
          }
        }
        if(nextPhase == AutoGradeJobQueue.END_ACCEPTED)
        {
          float adjustedScore = score;
          ArrayList<Object[]> latePenaltySchedule = GradingModel
                  .fetchPenaltySchedule(conn, taskId);
          if(!latePenaltySchedule.isEmpty())
          {
            int submissionTimeStamp = isQuizJob ? quizAnswerTag.getTimeStamp()
                                              : submission.getTimeStamp();
            for(Object[] lp : latePenaltySchedule)
            {
              WetoTimeStamp wts = (WetoTimeStamp) lp[0];
              if(wts.getTimeStamp() <= submissionTimeStamp)
              {
                adjustedScore -= (Integer) lp[1];
              }
            }
          }
          adjustedScore = Float.max(adjustedScore, 0.0F);
          testMark = (testMark == null) ? adjustedScore : testMark
                  + adjustedScore;
        }
        Integer testStatus = (testMark == null) ? GradeStatus.UNSPECIFIED
                .getValue() : GradeStatus.VALID.getValue();
        if(grade == null)
        {
          grade = new Grade();
          grade.setTaskId(taskId);
          grade.setReviewerId(null);
          grade.setReceiverId(submitterId);
          grade.setMark(testMark);
          grade.setStatus(testStatus);
          grade.insert(conn);
        }
        else
        {
          grade.setMark(testMark);
          grade.setStatus(testStatus);
          grade.update(conn);
        }
        if(isQuizJob)
        {
          String questionName = qb.getQuestionName();
          ArrayList<String> reviewLines = new ArrayList<>();
          Tag review = null;
          ArrayList<Tag> reviews = Tag.selectByTaggedIdAndAuthorIdAndType(conn,
                  grade.getId(), submitterId, TagType.REVIEW.getValue());
          boolean doUpdate = false;
          if(!reviews.isEmpty())
          {
            doUpdate = true;
            review = reviews.get(0);
            for(String line : review.getText().split("<br/>"))
            {
              if(!line.startsWith(questionName))
              {
                reviewLines.add(line);
              }
            }
          }
          reviewLines.add(questionName + ": " + score);
          StringBuilder sb = new StringBuilder();
          for(String line : reviewLines)
          {
            if(sb.length() > 0)
            {
              sb.append("<br/>" + line);
            }
            else
            {
              sb.append(line);
            }
          }
          if(doUpdate)
          {
            review.setText(sb.toString().trim());
            review.update(conn);
          }
          else
          {
            review = new Tag();
            review.setTaggedId(grade.getId());
            review.setType(TagType.REVIEW.getValue());
            review.setAuthorId(submitterId);
            review.setText(sb.toString().trim());
            review.insert(conn);
          }
        }
        if(Task.select1ById(conn, taskId).getHasGrades())
        {
          try
          {
            GradingModel.recalculateStudentGrades(conn, taskId, submitterId);
          }
          catch(Exception e)
          {
            System.out.println(
                    "<!Error updating student grades for task/user"
                    + taskId + "/" + submitterId + ">");
          }
        }
      }
      if(!isQuizJob)
      {
        submission.setAutoGradeMark((int) score);
      }
      else
      {
        quizScoreJson.addProperty("mark", score);
        quizScoreJson.addProperty("feedback", singleResult);
        quizScoreJson.addProperty("test", testNo);
        quizScoreJson.addProperty("phase", phase);
      }
    }
    else if(isQuizJob)
    {
      quizScoreJson.addProperty("feedback", singleResult);
      quizScoreJson.addProperty("test", testNo);
    }
  }

  /**
   * @param returned student submission
   * @param target where to write
   * @throws MessagingException
   * @throws IOException
   * @throws ZipException
   * @throws UnsupportedEncodingException
   */
  private String writeSubmissionAnswer(Connection conn, Submission submission,
          File target, String lang)
          throws MessagingException, IOException, ZipException,
                 UnsupportedEncodingException, SQLException
  {
    for(Document document : Document.selectBySubmissionId(conn, submission
            .getId()))
    {
      boolean isZipped = document.getContentMimeType().equals("application/zip");
      String fileName = document.getFileName();
      if(isZipped && !fileName.endsWith(".zip"))
      {
        fileName = fileName + ".zip";
      }
      File file = new File(target, fileName);
      DocumentModel.loadDocument(conn, document, file);
      if(isZipped)
      { // Unzip the package
        WetoUtilities.unzipFile(file, target);
        // Remove original package
        file.delete();
      }
    }
    if(lang == null)
    {
      for(File file : target.listFiles())
      {
        if(file.isFile())
        {
          if(file.getName().endsWith(".java"))
          {
            lang = "java";
            break;
          }
          else if(file.getName().endsWith(".cpp"))
          {
            lang = "cpp";
            break;
          }
          else if(file.getName().endsWith(".c"))
          {
            lang = "c";
            break;
          }
          else if(file.getName().endsWith(".py"))
          {
            lang = "python";
            break;
          }
          else if(file.getName().endsWith(".py2"))
          {
            lang = "python2";
            break;
          }
          else if(file.getName().endsWith(".py3"))
          {
            lang = "python3";
            break;
          }
          else if(file.getName().endsWith(".sql"))
          {
            lang = "sql";
            break;
          }
        }
      }
    }
    return lang;
  }

  /**
   * @param properties	properties to use
   * @param lang	programming language
   * @param compileDir	where are the programs that need to compile
   * @param targetDir	where to compile
   * @param taskDir	where task files are
   */
  private Integer compileSubmission(Properties properties, String lang,
          File compileDir, File targetDir, File taskDir)
  {
    int returnValue = -1;
    // Flush errors so we can determine is there compilation errors
    errors = "";
    if(Boolean.parseBoolean(properties.getProperty("useMakefile")))
    {
      returnValue = runCommand("make -f " + taskDir + "/Makefile COMPILEDIR="
              + compileDir + " TARGETDIR=" + targetDir + " TASKDIR=" + taskDir);
    }
    else
    {
      String progName = properties.getProperty("testCmd");
      if((progName != null) && !progName.isEmpty())
      {
        progName = progName.split("\\s", 2)[0];
      }
      else
      {
        progName = properties.getProperty("taskFileBase");
      }
      if(lang.equals("cpp") || lang.equals("c++"))
      {
        returnValue = runCommand(new String[]
        {
          "/bin/bash", "-c", "g++ " + cppParameters + " -o " + targetDir + "/"
          + progName + " " + compileDir + "/*.cpp -lm"
        });
      }
      else if(lang.equals("c"))
      {
        returnValue = runCommand(new String[]
        {
          "/bin/bash", "-c", "gcc " + cParameters + " -o " + targetDir + "/"
          + progName + " " + compileDir + "/*.c -lm"
        });
      }
      else if(lang.equals("java"))
      {
        returnValue = runCommand(new String[]
        {
          "/bin/bash", "-c",
          "javac -d " + targetDir + " " + compileDir + "/*.java"
        });
      }
      else if(lang.startsWith("python") || lang.equals("sql"))
      {
        returnValue = runCommand(new String[]
        {
          "/bin/bash", "-c",
          "mv -f " + compileDir + "/* " + targetDir + "/" + progName
        });
      }
    }
    return returnValue;
  }

  /**
   * @param pckg	teacher submitted autograde files
   * @param pckgfiledir	where to write files
   * @param properties	properties to use
   * @throws MessagingException
   * @throws IOException
   * @throws ZipException
   */
  private boolean writeSubmissionTestData(Connection conn, Document pckg,
          File pckgFileDir,
          Properties properties)
          throws MessagingException, IOException, ZipException, SQLException
  {
    if(!pckgFileDir.mkdir())
    {
      System.out.println("<!Error creating directory " + pckgFileDir + "!>");
      return false;
    }
    File pckgFile = new File(pckgFileDir, pckg.getFileName());
    DocumentModel.loadDocument(conn, pckg, pckgFile);
    // Unzip the package
    WetoUtilities.unzipFile(pckgFile, pckgFileDir);
    //Remove original package
    pckgFile.delete();
    String evalMakefile = properties.getProperty("evaluateMakefile");
    if((evalMakefile != null) && !evalMakefile.trim().isEmpty())
    {
      runCommand("make -C " + pckgFileDir + " -f " + evalMakefile);
    }
    File evaluator = new File(pckgFileDir + "/" + properties.getProperty(
            "evaluator"));
    // Allow the checker to execute the evaluator.
    if(evaluator.exists() && evaluator.isFile())
    {
      runCommand(new String[]
      {
        "/bin/bash", "-c",
        "chown wetodaemon:checker " + evaluator
      });
      runCommand(new String[]
      {
        "/bin/bash", "-c", "chmod g+x " + evaluator
      });
    }
    return true;
  }

  private boolean writeQuizTestData(QuestionBean qb, File pckgFileDir,
          File propFile, Properties properties)
          throws MessagingException, IOException, ZipException, SQLException
  {
    if(!pckgFileDir.mkdir())
    {
      System.out.println("<!Error creating directory " + pckgFileDir + "!>");
      return false;
    }
    File pckgFile = new File(pckgFileDir, "_wetotmp.zip");
    try(ByteArrayInputStream qzd = new ByteArrayInputStream(DatatypeConverter
            .parseBase64Binary(qb.getQuestionZipData())))
    {
      WetoUtilities.streamToFile(qzd, pckgFile);
    }
    // Unzip the package
    WetoUtilities.unzipFile(pckgFile, pckgFileDir);
    //Remove original package
    pckgFile.delete();
    try(FileInputStream fis = new FileInputStream(propFile))
    {
      properties.load(fis);
    }
    String evalMakefile = properties.getProperty("evaluateMakefile");
    if((evalMakefile != null) && !evalMakefile.trim().isEmpty())
    {
      runCommand("make -C " + pckgFileDir + " -f " + evalMakefile);
    }
    File evaluator = new File(pckgFileDir + "/" + properties.getProperty(
            "evaluator"));
    // Allow the checker to execute the evaluator.
    if(evaluator.exists() && evaluator.isFile())
    {
      runCommand(new String[]
      {
        "/bin/bash", "-c",
        "chown wetodaemon:checker " + evaluator
      });
      runCommand(new String[]
      {
        "/bin/bash", "-c", "chmod g+x " + evaluator
      });
    }
    return true;
  }

  /**
   * Returns a new connection
   *
   * @return java.sql.Connection connection
   */
  private Connection getConnection(String URL, String user, String password)
  {
    Connection conn;
    try
    {
      conn = DriverManager.getConnection(URL, user, password);
      conn.setAutoCommit(false);
    }
    catch(Exception e)
    {
      conn = null;
      System.out.println("Q");
    }
    return conn;
  }

  private int runCommand(String str)
  {
    int returnValue = -1;
    try
    {
      Process p = Runtime.getRuntime().exec(str);
      System.out.println("<" + str + ">");
      returnValue = runCommandInner(p);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    return returnValue;
  }

  private int runCommand(String[] str)
  {
    int returnValue = -1;
    try
    {
      Process p = Runtime.getRuntime().exec(str);
      System.out.print("<");
      if(str.length >= 3)
      {
        System.out.print(str[2]);
      }
      System.out.println(">");
      returnValue = runCommandInner(p);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    return returnValue;
  }

  private int runCommandInner(Process p)
  {
    int returnValue = -1;
    try
    {
      ProcessWatch pw = new ProcessWatch(p, this);
      // any error message?
      StreamHandler errorHandler = new StreamHandler(p.getErrorStream(),
              "ERROR");
      // any output?
      StreamHandler outputHandler = new StreamHandler(p.getInputStream(),
              "OUTPUT");
      // kick them off
      errorHandler.start();
      outputHandler.start();
      returnValue = p.waitFor();
      //TODO Should warnings be discarded from error messages?
      errors = errorHandler.getErrors();
      pw.interrupt();
    }
    catch(InterruptedException e)
    {
      e.printStackTrace();
    }
    return returnValue;
  }

  public class ProcessWatch extends Thread
  {
    Process p;
    Thread pt;

    ProcessWatch(Process p, Thread t)
    {
      this.p = p;
      this.pt = t;
      start();
    }

    @Override
    public void run()
    {
      try
      {
        sleep(timeout);
        System.out.println("!");
        p.getErrorStream().close();
        p.getInputStream().close();
        p.getOutputStream().close();
        p.destroy();
        pt.interrupt();
      }
      catch(InterruptedException e)
      {
        // e.printStackTrace();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

  }

  private void error(Submission submission, JsonObject quizScoreJson,
          Connection conn, Exception e)
  {
    nextPhase = AutoGradeJobQueue.ERROR_IN_TEST;
    try
    {
      if(submission != null)
      {
        submission.setMessage(e.getMessage());
      }
      else
      {
        quizScoreJson.addProperty("error", e.getMessage());
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private boolean submissionCompilationError(Submission submission,
          String errorMessage, boolean warningOnly)
  {
    boolean incomplete = false;
    final int errorPreviewLen = 500;
    try
    {
      StringBuilder errorPreview = new StringBuilder();
      StringReader sr = new StringReader(errorMessage);
      BufferedReader br = new BufferedReader(sr);
      String line = br.readLine();
      while((line != null) && !incomplete && (errorPreview.length()
              < errorPreviewLen))
      {
        if(errorPreview.length() + line.length() > errorPreviewLen)
        {
          line = line.substring(0, errorPreviewLen - errorPreview.length());
          incomplete = true;
        }
        errorPreview.append(line);
        line = br.readLine();
        if((line != null) && !incomplete)
        {
          errorPreview.append(MESSAGE_NEWLINE);
        }
      }
      if(warningOnly)
      {
        submission.setError(SubmissionError.COMPILATION_WARNING.getValue());
      }
      else
      {
        submission.setError(SubmissionError.COMPILATION_ERROR.getValue());
      }
      submission.setMessage(errorPreview.toString());
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    return incomplete;
  }

  private boolean quizCompilationError(JsonObject quizScoreJson,
          String errorMessage, boolean warningOnly)
  {
    boolean incomplete = false;
    final int errorPreviewLen = 2000;
    try
    {
      StringBuilder errorPreview = new StringBuilder();
      StringReader sr = new StringReader(errorMessage);
      BufferedReader br = new BufferedReader(sr);
      String line = br.readLine();
      while((line != null) && !incomplete && (errorPreview.length()
              < errorPreviewLen))
      {
        if(errorPreview.length() + line.length() > errorPreviewLen)
        {
          line = line.substring(0, errorPreviewLen - errorPreview.length());
          incomplete = true;
        }
        errorPreview.append(line);
        line = br.readLine();
        if((line != null) && !incomplete)
        {
          errorPreview.append(MESSAGE_NEWLINE);
        }
      }
      quizScoreJson.addProperty(warningOnly ? "warning" : "error", errorPreview
              .toString());
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    return incomplete;
  }

}

class StreamHandler extends Thread
{
  InputStream is;
  String type;
  StringBuffer buffer = new StringBuffer();

  StreamHandler(InputStream is, String type)
  {
    this.is = is;
    this.type = type;
  }

  @Override
  public void run()
  {
    try
    {
      InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      String line;
      while((line = br.readLine()) != null)
      {
        if(type.equals("ERROR"))
        {
          buffer.append(line).append("\n");
        }
      }
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  public String getErrors()
  {
    String tmpString = buffer.toString();
    // Set StringBuffer empty so we can reuse it
    buffer.setLength(0);
    return tmpString;
  }

}
