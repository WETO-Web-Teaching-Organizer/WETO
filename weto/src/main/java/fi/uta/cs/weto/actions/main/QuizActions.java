package fi.uta.cs.weto.actions.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.model.ContentElementType;
import fi.uta.cs.weto.model.LogEvent;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.QuestionBean;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.util.TmpFileInputStream;
import fi.uta.cs.weto.util.WetoCsvWriter;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class QuizActions
{
  public static class Edit extends WetoTeacherAction
  {
    private QuizModel quiz;

    public Edit()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      String result = SUCCESS;
      quiz = new QuizModel(courseConn, getTaskId(), getCourseUserId());
      quiz.readQuestions();
      for(QuestionBean q : quiz.getQuestions())
      {
        try
        {
          Tag answer = Tag
                  .select1ByTaggedIdAndRankAndAuthorIdAndType(courseConn,
                          getTaskId(), q.getQuestionId(), getCourseUserId(),
                          TagType.QUIZ_ANSWER.getValue());
          QuizModel.populateAnswers(q, answer);
          QuizModel.populateResults(courseConn, q, answer, this);
        }
        catch(NoSuchItemException e)
        {
        }
      }
      return result;
    }

    public QuizModel getQuiz()
    {
      return quiz;
    }

    public boolean isQuizOpen()
    {
      return true;
    }

  }

  public static class SaveQuestion extends WetoTeacherAction
  {
    private Integer newQuestionType;
    private String newQuestionName;
    private Integer newQuestionId;
    private String[] newQuestionTexts;
    private String[] newAnswers;
    private String[] newFeedbacks;
    private Float[] newScores;
    private boolean newSingleAnswer;
    private String newDetail;
    private boolean newShuffle;
    private String newNumbering;
    private File newQuestionFile;
    private String newQuestionFileFileName;

    public SaveQuestion()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer taskId = getTaskId();
      String result = SUCCESS;
      QuizModel quiz = new QuizModel(courseConn, taskId, getCourseUserId());
      quiz.readQuestions();
      List<QuestionBean> oldQuestions = quiz.getQuestions();
      QuestionBean qb = null;
      if(newQuestionId != null)
      {
        for(QuestionBean oq : oldQuestions)
        {
          if(oq.getQuestionId().equals(newQuestionId))
          {
            qb = oq;
            break;
          }
        }
      }
      if(qb == null)
      {
        qb = new QuestionBean(newQuestionType, taskId, null);
        oldQuestions.add(qb);
      }
      qb.setQuestionName(newQuestionName);
      qb.setQuestionTexts(newQuestionTexts);
      if(newQuestionType.equals(ContentElementType.MULTIPLE_CHOICE.getValue()))
      {
        qb.setChoices(newAnswers);
        qb.setFeedbacks(newFeedbacks);
        qb.setScoreFractions(newScores);
        qb.setNumbering(newNumbering);
        qb.setShuffle(newShuffle);
        qb.setSingleAnswer(newSingleAnswer);
      }
      else if(newQuestionType.equals(ContentElementType.SURVEY.getValue()))
      {
        qb.setChoices(newAnswers);
        qb.setSingleAnswer(newSingleAnswer);
        qb.setDetail(newDetail);
      }
      else if(newQuestionType.equals(ContentElementType.PROGRAM.getValue()))
      {
        qb.setNumbering(newNumbering); // Holds language!
        if(newQuestionFile != null && newQuestionFile.length() != 0)
        {
          if(newQuestionFileFileName.endsWith(".zip"))
          {
            qb.setQuestionZipData(WetoUtilities.fileToBase64(newQuestionFile));
            qb.setQuestionZipName(newQuestionFileFileName);
          }
          else
          {
            addActionError(getText("autograding.error.notZipFile"));
          }
        }
      }
      quiz.saveQuestions();
      addActionMessage(getText("quiz.message.questionSaved"));
      return result;
    }

    public void setNewQuestionType(Integer newQuestionType)
    {
      this.newQuestionType = newQuestionType;
    }

    public void setNewQuestionName(String newQuestionName)
    {
      this.newQuestionName = newQuestionName;
    }

    public void setNewQuestionId(Integer newQuestionId)
    {
      this.newQuestionId = newQuestionId;
    }

    public void setNewQuestionTexts(String[] newQuestionTexts)
    {
      this.newQuestionTexts = newQuestionTexts;
    }

    public void setNewAnswers(String[] newAnswers)
    {
      this.newAnswers = newAnswers;
    }

    public void setNewFeedbacks(String[] newFeedbacks)
    {
      this.newFeedbacks = newFeedbacks;
    }

    public void setNewDetail(String newDetail)
    {
      this.newDetail = newDetail;
    }

    public void setNewScores(String[] newScores)
    {
      this.newScores = new Float[newScores.length];
      for(int i = 0; i < newScores.length; ++i)
      {
        if((newScores[i] != null) && !newScores[i].isEmpty())
        {
          this.newScores[i] = Float.valueOf(newScores[i]);
        }
        else
        {
          this.newScores[i] = 0F;
        }
      }
    }

    public void setNewSingleAnswer(boolean newSingleAnswer)
    {
      this.newSingleAnswer = newSingleAnswer;
    }

    public void setNewShuffle(boolean newShuffle)
    {
      this.newShuffle = newShuffle;
    }

    public void setNewNumbering(String newNumbering)
    {
      this.newNumbering = newNumbering;
    }

    public void setNewQuestionFile(File newQuestionFile)
    {
      this.newQuestionFile = newQuestionFile;
    }

    public void setNewQuestionFileFileName(String newQuestionFileFileName)
    {
      this.newQuestionFileFileName = newQuestionFileFileName;
    }

    public File getNewQuestionFile()
    {
      return newQuestionFile;
    }

    public String getNewQuestionFileFileName()
    {
      return newQuestionFileFileName;
    }

    public void setNewQuestionFileContentType(String newQuestionFileContentType)
    {
    }

  }

  public static class DownloadZip extends WetoTeacherAction
  {
    private Integer questionId;
    private InputStream documentStream;
    private String contentType;
    private int contentLength;
    private QuestionBean qb;
    private final int bufferSize = 8192;

    public DownloadZip()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      // Retrieve the question.
      QuizModel quiz = new QuizModel(conn, getTaskId(), null);
      quiz.readQuestion(questionId);
      qb = quiz.getQuestions().get(0);
      InputStream dataStream = new ByteArrayInputStream(DatatypeConverter
              .parseBase64Binary(qb.getQuestionZipData()));
      contentLength = dataStream.available();
      documentStream = dataStream;
      return SUCCESS;
    }

    public void setQuestionId(Integer questionId)
    {
      this.questionId = questionId;
    }

    public InputStream getDocumentStream()
    {
      return documentStream;
    }

    public String getContentType()
    {
      return contentType + ";charset=UTF-8";
    }

    public String getContentDisposition()
    {
      return "inline;filename=\"" + qb.getQuestionZipName() + "\"";
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

  public static class DeleteQuestion extends WetoTeacherAction
  {
    private Integer questionId;

    public DeleteQuestion()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer taskId = getTaskId();
      ArrayList<Tag> oldQuestions = Tag.selectByTaggedIdAndType(courseConn,
              taskId, TagType.QUIZ_QUESTION.getValue());
      int rank = 0;
      for(Tag question : oldQuestions)
      {
        if(question.getId().equals(questionId))
        {
          question.delete(courseConn);
          addActionMessage(getText("quiz.message.questionDeleted"));
        }
        else
        {
          if(!question.getRank().equals(rank))
          {
            question.setRank(rank);
            question.update(courseConn);
          }
          rank += 1;
        }
      }
      return SUCCESS;
    }

    public void setQuestionId(Integer questionId)
    {
      this.questionId = questionId;
    }

  }

  public static class SaveQuestionOrder extends WetoTeacherAction
  {
    private String newQuestionOrder;

    public SaveQuestionOrder()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer taskId = getTaskId();
      String[] orderStrings = newQuestionOrder.split(",");
      HashMap<Integer, Integer> rankMap = new HashMap<>();
      int i = 0;
      for(String orderStr : orderStrings)
      {
        Integer questionTagId = Integer.valueOf(orderStr);
        rankMap.put(questionTagId, i++);
      }
      ArrayList<Tag> oldQuestions = Tag.selectByTaggedIdAndType(courseConn,
              taskId, TagType.QUIZ_QUESTION.getValue());
      for(Tag questionTag : oldQuestions)
      {
        questionTag.setRank(rankMap.get(questionTag.getId()));
        questionTag.update(courseConn);
      }
      addActionMessage(getText("quiz.message.changesSaved"));
      return SUCCESS;
    }

    public void setNewQuestionOrder(String newQuestionOrder)
    {
      this.newQuestionOrder = newQuestionOrder;
    }

  }

  public static class SaveAnswer extends WetoCourseAction
  {
    private static final Whitelist whitelist;

    static
    {
      whitelist = Whitelist.relaxed();
      String extraAttributes = WetoUtilities.getPackageResource(
              "jsoup.whitelist.extraAttributes");
      if(extraAttributes != null)
      {
        for(String attrString : extraAttributes.split(";", 0))
        {
          String[] attrs = attrString.trim().split(" ");
          whitelist.addAttributes(attrs[0], Arrays.copyOfRange(attrs, 1,
                  attrs.length));
        }
      }
      String extraProtocols = WetoUtilities.getPackageResource(
              "jsoup.whitelist.extraProtocols");
      if(extraProtocols != null)
      {
        for(String protString : extraProtocols.split(";", 0))
        {
          String[] prots = protString.trim().split(" ");
          whitelist.addProtocols(prots[0], prots[1], Arrays
                  .copyOfRange(prots, 2, prots.length));
        }
      }
    }

    private Map<String, ArrayList<ArrayList<String>>> answers;
    private Integer questionId;
    private InputStream messageStream;
    private boolean autograde = false;
    private boolean testRun = false;
    private Integer testNumber;

    public SaveAnswer()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer userId = getCourseUserId();
      Integer taskId = getTaskId();
      boolean doNotClean = false;
      try
      {
        Tag question = Tag.select1ById(courseConn, questionId);
        if(!question.getTaggedId().equals(taskId) || !question.getType().equals(
                TagType.QUIZ_QUESTION.getValue()))
        {
          throw new WetoActionException();
        }
        doNotClean = question.getText().contains("<question type=\"program\">")
                || question.getText()
                .contains("<question type=\"multichoice\">");
      }
      catch(NoSuchItemException e)
      {
        throw new WetoActionException();
      }
      WetoTimeStamp[] timeLimits = PermissionModel
              .getTimeStampLimits(courseConn, userId, taskId,
                      PermissionType.SUBMISSION);
      boolean submissionOpen = getNavigator().isTeacher() || (PermissionModel
              .checkTimeStampLimits(timeLimits) == PermissionModel.CURRENT);
      if(!submissionOpen)
      {
        throw new WetoActionException(getText("quiz.error.isClosed"));
      }
      StringWriter sw = new StringWriter();
      StringBuilder sb = new StringBuilder();
      String questionIdString = questionId.toString();
      sb.append(new WetoTimeStamp().toString() + ";");
      if((answers != null) && answers.containsKey(questionIdString))
      {
        WetoCsvWriter wcw = new WetoCsvWriter(sw);
        ArrayList<ArrayList<String>> answerList = answers.get(questionIdString);
        int i = 0;
        final List<String> emptyList = new ArrayList<>();
        emptyList.add(null);
        final ArrayList<String> cleanedList = new ArrayList<>();
        for(List<String> subList : answerList)
        {
          if(subList != null)
          {
            Tag question = Tag.select1ById(courseConn, questionId);
            if(subList.size() == 1 && question.getText().contains("<question type=\"multichoice\">")){
              List<String> newList = new ArrayList<>();
              String testString = subList.get(0);
              while(testString.contains("</p>,<p>")){
                for(int n = 1; n < testString.length(); n++){
                  if(testString.charAt(n) == ',' && testString.charAt(n-1) == '>' && testString.charAt(n+1) == '<'){
                    newList.add(testString.substring(0, n));
                    testString = testString.substring(n+1);
                    break;
                  }
                }
              }
              newList.add(testString);
              subList = newList;
            }
            cleanedList.clear();
            for(String a : subList)
            {
              if(i++ > 0)
              {
                sb.append("\n");
              }
              String cleanA = (a != null) ? (doNotClean ? a.replaceAll("\r\n",
                      "\n") : Jsoup.clean(a.replaceAll("\r\n", "\n"), "",
                              whitelist, new Document.OutputSettings()
                              .prettyPrint(false))) : "";
              cleanedList.add(cleanA);
              sb.append(cleanA);
            }
            wcw.writeStrings(cleanedList);
          }
          else
          {
            if(i++ > 0)
            {
              sb.append("\n");
            }
            wcw.writeStrings(emptyList);
          }
        }
      }
      String answerString = sw.toString();
      Tag answer;
      boolean doUpdate = false;
      try
      {
        answer = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(courseConn,
                taskId, questionId, userId, TagType.QUIZ_ANSWER.getValue());
        doUpdate = true;
      }
      catch(NoSuchItemException e)
      {
        answer = new Tag();
        answer.setTaggedId(taskId);
        answer.setRank(questionId);
        answer.setAuthorId(userId);
        answer.setType(TagType.QUIZ_ANSWER.getValue());
      }
      answer.setText(answerString);
      if(doUpdate)
      {
        answer.update(courseConn);
      }
      else
      {
        answer.insert(courseConn);
      }
      ArrayList<Submission> quizSubmissions = Submission
              .selectByUserIdAndTaskIdAndStatus(courseConn,
                      userId, taskId, SubmissionStatus.QUIZ_SUBMISSION
                      .getValue());
      Submission quizSubmission = null;
      if(quizSubmissions.isEmpty())
      {
        quizSubmission = new Submission();
        quizSubmission.setTaskId(taskId);
        quizSubmission.setUserId(userId);
        quizSubmission.setStatus(SubmissionStatus.QUIZ_SUBMISSION.getValue());
        quizSubmission.insert(courseConn);
      }
      else
      {
        quizSubmission = quizSubmissions.get(0);
        quizSubmission.update(courseConn);
        for(int i = 1; i < quizSubmissions.size(); ++i)
        {
          quizSubmissions.get(i).delete(courseConn);
        }
      }
      // If autograding was requested, insert a job to autoGradeJobQueue.
      if(autograde == true)
      {
        AutoGradeJobQueue job = new AutoGradeJobQueue();
        job.setTaskId(taskId);
        job.setDbId(getDbId());
        job.setJobType(AutoGradeJobQueue.INLINE_JOB);
        job.setRefId(answer.getId());
        if(testRun)
        {
          job.setQueuePhase(AutoGradeJobQueue.TEST_RUN);
          if(testNumber != null)
          {
            job.setJobComment(testNumber.toString());
          }
        }
        else
        {
          job.setQueuePhase(AutoGradeJobQueue.IMMEDIATE_PUBLIC);
        }
        job.setTestRunning(false);
        job.insert(courseConn);
        try
        {
          Tag quizScoreTag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(
                  courseConn, taskId, answer.getId(), userId, TagType.QUIZ_SCORE
                  .getValue());
          quizScoreTag.setStatus(SubmissionStatus.PROCESSING.getValue());
          quizScoreTag.setText(null);
          quizScoreTag.update(courseConn);
        }
        catch(NoSuchItemException e)
        {
        }
      }
      else
      {
        // Clear possible prior scores
        try
        {
          Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(courseConn, taskId,
                  answer.getId(), userId, TagType.QUIZ_SCORE.getValue()).delete(
                          courseConn);
        }
        catch(NoSuchItemException e)
        {
        }
      }
      messageStream = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
      if(!getNavigator().isStudentRole())
      {
        new Log(getCourseTaskId(), taskId, userId, LogEvent.SAVE_QUIZ_ANSWER
                .getValue(), questionId, answer.getId(), getRequest()
                .getRemoteAddr()).insert(courseConn);
      }
      return SUCCESS;
    }

    public Map<String, ArrayList<ArrayList<String>>> getAnswers()
    {
      return answers;
    }

    public void setAnswers(Map<String, ArrayList<ArrayList<String>>> answers)
    {
      this.answers = answers;
    }

    public void setQuestionId(Integer questionId)
    {
      this.questionId = questionId;
    }

    public InputStream getMessageStream()
    {
      return messageStream;
    }

    public void setAutograde(boolean autograde)
    {
      this.autograde = autograde;
    }

    public void setTestRun(boolean testRun)
    {
      this.testRun = testRun;
    }

    public void setTestNumber(Integer testNumber)
    {
      this.testNumber = testNumber;
    }

  }

  public static class ViewAnswers extends WetoTeacherAction
  {
    private Integer questionId;
    private ArrayList<ArrayList<QuestionBean>> answers = new ArrayList<>();
    private ArrayList<UserAccount> answerers = new ArrayList<>();
    private ArrayList<QuestionBean> questions = new ArrayList<>();
    private Float minScore;
    private Float maxScore;
    private Float scoreStep;

    public ViewAnswers()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      try
      {
        Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
        minScore = Float.parseFloat(scoring.getProperty("minScore", "0"));
        maxScore = Float.parseFloat(scoring.getProperty("maxScore", "0"));
        if(minScore < maxScore)
        {
          scoreStep = Float.parseFloat(scoring.getProperty("scoreStep", "1"));
        }
      }
      catch(NoSuchItemException e)
      {
      }
      String result = SUCCESS;
      QuizModel quiz = new QuizModel(conn, getTaskId(), getCourseUserId());
      if(questionId != null)
      {
        quiz.readQuestion(questionId);
      }
      else
      {
        quiz.readQuestions();
      }
      HashMap<Integer, QuestionBean> questionMap = new HashMap<>();
      for(QuestionBean qb : quiz.getQuestions())
      {
        questionMap.put(qb.getQuestionId(), qb);
        questions.add(qb);
      }
      HashMap<Integer, HashMap<Integer, Tag>> answerMap = new HashMap<>();
      for(Tag answer : Tag.selectByTaggedIdAndType(conn, taskId,
              TagType.QUIZ_ANSWER.getValue()))
      {
        if(questionMap.containsKey(answer.getRank()))
        {
          HashMap<Integer, Tag> userAnswers = answerMap
                  .get(answer.getAuthorId());
          if(userAnswers == null)
          {
            userAnswers = new HashMap<>();
            answerMap.put(answer.getAuthorId(), userAnswers);
          }
          userAnswers.put(answer.getRank(), answer);
        }
      }
      for(Integer userId : answerMap.keySet())
      {
        answerers.add(UserAccount.select1ById(conn, userId));
      }
      answerers.sort(new Comparator<UserAccount>()
      {
        @Override
        public int compare(UserAccount o1, UserAccount o2)
        {
          int result = o1.getLastName().compareToIgnoreCase(o2.getLastName());
          if(result == 0)
          {
            result = o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
          }
          if(result == 0)
          {
            result = o1.getLoginName().compareToIgnoreCase(o2.getLoginName());
          }
          return result;
        }

      });
      for(UserAccount answerer : answerers)
      {
        ArrayList<QuestionBean> qbs = new ArrayList<>();
        answers.add(qbs);
        HashMap<Integer, Tag> userAnswers = answerMap.get(answerer.getId());
        for(QuestionBean question : questions)
        {
          Tag answerTag = userAnswers.get(question.getQuestionId());
          QuestionBean qb = null;
          if(answerTag != null)
          {
            qb = new QuestionBean(questionMap.get(answerTag.getRank()));
            QuizModel.populateAnswers(qb, answerTag);
            QuizModel.populateResults(conn, qb, answerTag, this);
          }
          qbs.add(qb);
        }
      }
      return result;
    }

    public void setQuestionId(Integer questionId)
    {
      this.questionId = questionId;
    }

    public Float getMinScore()
    {
      return minScore;
    }

    public Float getMaxScore()
    {
      return maxScore;
    }

    public Float getScoreStep()
    {
      return scoreStep;
    }

    public ArrayList<ArrayList<QuestionBean>> getAnswers()
    {
      return answers;
    }

    public ArrayList<UserAccount> getAnswerers()
    {
      return answerers;
    }

    public ArrayList<QuestionBean> getQuestions()
    {
      return questions;
    }

  }

  public static class ExportAnswers extends WetoTeacherAction
  {
    private static final int bufferSize = 8192;
    private int contentLength;
    private InputStream documentStream;
    private Integer questionId;

    public ExportAnswers()
    {
      super(Tab.MAIN.getBit(), 0, Tab.MAIN.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      String result = SUCCESS;
      QuizModel quiz = new QuizModel(conn, getTaskId(), getCourseUserId());
      ArrayList<UserAccount> answerers = new ArrayList<>();
      ArrayList<QuestionBean> questions = new ArrayList<>();
      if(questionId != null)
      {
        quiz.readQuestion(questionId);
      }
      else
      {
        quiz.readQuestions();
      }
      HashMap<Integer, QuestionBean> questionMap = new HashMap<>();
      for(QuestionBean qb : quiz.getQuestions())
      {
        questionMap.put(qb.getQuestionId(), qb);
        questions.add(qb);
      }
      HashMap<Integer, HashMap<Integer, Tag>> answerMap = new HashMap<>();
      for(Tag answer : Tag.selectByTaggedIdAndType(conn, taskId,
              TagType.QUIZ_ANSWER.getValue()))
      {
        if(questionMap.containsKey(answer.getRank()))
        {
          HashMap<Integer, Tag> userAnswers = answerMap
                  .get(answer.getAuthorId());
          if(userAnswers == null)
          {
            userAnswers = new HashMap<>();
            answerMap.put(answer.getAuthorId(), userAnswers);
          }
          userAnswers.put(answer.getRank(), answer);
        }
      }
      for(Integer userId : answerMap.keySet())
      {
        answerers.add(UserAccount.select1ById(conn, userId));
      }
      answerers.sort(new Comparator<UserAccount>()
      {
        @Override
        public int compare(UserAccount o1, UserAccount o2)
        {
          int result = o1.getLastName().compareToIgnoreCase(o2.getLastName());
          if(result == 0)
          {
            result = o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
          }
          if(result == 0)
          {
            result = o1.getLoginName().compareToIgnoreCase(o2.getLoginName());
          }
          return result;
        }

      });
      Workbook wb = new XSSFWorkbook();
      Sheet sheet = wb.createSheet("new sheet");
      sheet.setDefaultColumnWidth(35);
      CellStyle cs = wb.createCellStyle();
      cs.setWrapText(true);
      cs.setShrinkToFit(true);
      short rowIdx = 0;
      Row row = sheet.createRow(rowIdx++);
      int colIdx = 0;
      Cell cell = row.createCell(colIdx++);
      cell.setCellStyle(cs);
      for(QuestionBean question : questions)
      {
        cell = row.createCell(colIdx++);
        cell.setCellStyle(cs);
        cell.setCellValue(Jsoup.parse(question.getQuestionTexts()[0]).text());
      }
      for(UserAccount answerer : answerers)
      {
        row = sheet.createRow(rowIdx++);
        colIdx = 0;
        row.createCell(colIdx++).setCellValue(answerer.getLastName() + ", "
                + answerer.getFirstName());
        HashMap<Integer, Tag> userAnswers = answerMap.get(answerer.getId());
        for(QuestionBean question : questions)
        {
          Tag answerTag = userAnswers.get(question.getQuestionId());
          if(answerTag != null)
          {
            QuestionBean qb = new QuestionBean(questionMap.get(answerTag
                    .getRank()));
            QuizModel.populateAnswers(qb, answerTag);
            cell = row.createCell(colIdx++);
            cell.setCellStyle(cs);
            if(qb.getUserAnswers() != null)
            {
              cell.setCellValue(Jsoup.parse(qb.getUserAnswers()[0][0]).text());
            }
          }
          else
          {
            row.createCell(colIdx++);
          }
        }
      }
      Path exportPath = Files.createTempFile("weto", ".xlsx");
      File exportFile = exportPath.toFile();
      try(FileOutputStream fileOut = new FileOutputStream(exportFile))
      {
        wb.write(fileOut);
      }
      documentStream = new TmpFileInputStream(exportFile, Files.newInputStream(
              exportPath));
      contentLength = (int) exportFile.length();
      return result;
    }

    public void setQuestionId(Integer questionId)
    {
      this.questionId = questionId;
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

  public static class SaveGrades extends WetoTeacherAction
  {
    private String[] questionMarks;

    public SaveGrades()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), Tab.GRADING.getBit(),
              Tab.GRADING.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      for(String questionUserMark : questionMarks)
      {
        String[] parts = questionUserMark.split("_", 3);
        Integer questionId = Integer.parseInt(parts[0]);
        Integer answererId = Integer.parseInt(parts[1]);
        Float mark = null;
        if((parts.length == 3) && !parts[2].isEmpty())
        {
          mark = Float.parseFloat(parts[2]);
        }
        if(mark != null)
        {
          Tag quizAnswerTag = null;
          try
          {
            quizAnswerTag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
                    taskId, questionId, answererId, TagType.QUIZ_ANSWER
                    .getValue());
          }
          catch(NoSuchItemException e)
          {
          }
          if(quizAnswerTag != null)
          {
            Tag quizScoreTag = null;
            boolean doUpdate = false;
            try
            {
              quizScoreTag = Tag
                      .select1ByTaggedIdAndRankAndAuthorIdAndType(conn, taskId,
                              quizAnswerTag.getId(), answererId,
                              TagType.QUIZ_SCORE.getValue());
              doUpdate = true;
            }
            catch(NoSuchItemException e)
            {
              quizScoreTag = new Tag();
              quizScoreTag.setType(TagType.QUIZ_SCORE.getValue());
              quizScoreTag.setTaggedId(taskId);
              quizScoreTag.setRank(quizAnswerTag.getId());
              quizScoreTag.setAuthorId(answererId);
            }
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("mark", mark);
            quizScoreTag.setText(resultJson.toString());
            quizScoreTag.setStatus(SubmissionStatus.QUIZ_SUBMISSION.getValue());
            if(doUpdate)
            {
              quizScoreTag.update(conn);
            }
            else
            {
              quizScoreTag.insert(conn);
            }
          }
        }
      }
      return SUCCESS;
    }

    public void setQuestionMarks(String[] questionMarks)
    {
      this.questionMarks = questionMarks;
    }

  }

  public static class GetQuizScoreJSON extends WetoCourseAction
  {
    private Integer quizQuestionId;
    private Integer queuePos;
    private InputStream messageStream;

    public GetQuizScoreJSON()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    public String action() throws Exception
    {
      String result = SUCCESS;
      // ADD RESULT PERMISSION CHECK (OR CHECK ITS CORRECTNESS...)
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      Integer taskId = getTaskId();
      Tag quizAnswerTag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
              taskId, quizQuestionId, userId, TagType.QUIZ_ANSWER.getValue());
      try
      {
        Tag quizScoreTag = Tag
                .select1ByTaggedIdAndRankAndAuthorIdAndType(conn,
                        taskId, quizAnswerTag.getId(), userId,
                        TagType.QUIZ_SCORE
                        .getValue());
        if(SubmissionStatus.PROCESSING.getValue().equals(quizScoreTag
                .getStatus()))
        {
          queuePos = AutoGradeJobQueue.getQuizQueuePos(conn, quizAnswerTag
                  .getId());
          result = INPUT;
        }
        else
        {
          JsonObject resultJson = new JsonParser().parse(quizScoreTag
                  .getText()).getAsJsonObject();
          boolean hideFeedback = false;
          if(resultJson.has("feedback") && resultJson.has("mark") && !resultJson
                  .get("mark").isJsonNull())
          {
            String feedback = resultJson.getAsJsonPrimitive("feedback")
                    .getAsString();
            if(!"OK".equals(feedback) && resultJson.has("phase")
                    && !resultJson.get("phase").isJsonNull())
            {
              int phase = resultJson.getAsJsonPrimitive("phase").getAsInt();
              hideFeedback = (phase == AutoGradeJobQueue.IMMEDIATE_PRIVATE)
                      && getNavigator().isStudent();
            }
          }
          if(hideFeedback)
          {
            resultJson.remove("feedback");
            resultJson.addProperty("feedback", getText(
                    "autograding.message.incorrectResult"));
          }
          else
          {
            Integer fullFeedbackId = null;
            ArrayList<Tag> fullFeedbacks = Tag.selectByTaggedIdAndRankAndType(
                    conn, taskId, quizAnswerTag.getId(), TagType.FEEDBACK
                    .getValue());
            if(!fullFeedbacks.isEmpty())
            {
              fullFeedbackId = fullFeedbacks.get(0).getId();
            }
            else
            {
              ArrayList<Tag> fullErrors = Tag.selectByTaggedIdAndRankAndType(
                      conn, taskId, quizAnswerTag.getId(),
                      TagType.COMPILER_RESULT.getValue());
              if(!fullErrors.isEmpty())
              {
                fullFeedbackId = fullErrors.get(0).getId();
              }
            }
            if(fullFeedbackId != null)
            {
              resultJson.addProperty("fullFeedbackId", fullFeedbackId);
            }
          }
          messageStream = new ByteArrayInputStream(resultJson.toString()
                  .getBytes("UTF-8"));
        }
      }
      catch(NoSuchItemException e)
      {
        queuePos = AutoGradeJobQueue
                .getQuizQueuePos(conn, quizAnswerTag.getId());
        result = INPUT;
      }
      return result;
    }

    public void setQuizQuestionId(Integer quizQuestionId)
    {
      this.quizQuestionId = quizQuestionId;
    }

    public Integer getQueuePos()
    {
      return queuePos;
    }

    public InputStream getMessageStream()
    {
      return messageStream;
    }

  }

  public static class FullFeedback extends WetoCourseAction
  {
    private Integer tagId;
    private String feedback;
    private boolean compilerFeedback;

    public FullFeedback()
    {
      super(Tab.MAIN.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer userId = getCourseUserId();
      Tag feedbackTag = Tag.select1ById(conn, tagId);
      Tag quizAnswerTag = Tag.select1ById(conn, feedbackTag.getRank());
      Integer submitterId = quizAnswerTag.getAuthorId();
      validateCourseSubtaskId(quizAnswerTag.getTaggedId());
      if(!haveViewRights(Tab.SUBMISSIONS.getBit(), userId.equals(submitterId),
              true))
      {
        throw new WetoActionException(getText("general.error.accessDenied"));
      }
      compilerFeedback = TagType.COMPILER_RESULT.getValue().equals(feedbackTag
              .getType());
      feedback = WetoUtilities.gzippedBase64ToString(feedbackTag.getText());
      return SUCCESS;
    }

    public void setTagId(Integer tagId)
    {
      this.tagId = tagId;
    }

    public String getFeedback()
    {
      return feedback;
    }

    public boolean isCompilerFeedback()
    {
      return compilerFeedback;
    }

  }
}
