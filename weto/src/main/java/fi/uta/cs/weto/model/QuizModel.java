package fi.uta.cs.weto.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.AutoGradeJobQueue;
import fi.uta.cs.weto.db.Tag;
import static fi.uta.cs.weto.model.TaskModel.migrateStringDocumentIds;
import fi.uta.cs.weto.util.WetoCsvReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;

public class QuizModel
{
  private ArrayList<QuestionBean> questions;
  private final Connection connection;
  private final Integer taskId;
  private final Integer authorId;

  public QuizModel(Connection connection, Integer taskId, Integer authorId)
  {
    questions = new ArrayList<>();
    this.connection = connection;
    this.taskId = taskId;
    this.authorId = authorId;
  }

  private boolean decodeQuestions(ArrayList<Tag> questionList)
  {
    boolean hadQuestions = false;
    try
    {
      for(Tag question : questionList)
      {
        String content = question.getText();
        if((content != null) && content.startsWith("<?xml version="))
        {
          if(!hadQuestions)
          {
            hadQuestions = true;
            questions.clear();
          }
          Document questionDefinition = new SAXBuilder().build(new StringReader(
                  content));
          // get the root element and then questionGroups which are root's
          // children
          Element questionElement = questionDefinition.getRootElement();
          String questionTypeStr = questionElement.getAttributeValue("type");
          ContentElementType questionType = ContentElementType.getByAlias(
                  questionTypeStr);
          if(questionType != null)
          {
            QuestionBean qb = new QuestionBean(questionType.getValue(), taskId,
                    question.getId());
            Element questionNameElement = questionElement.getChild("name");
            qb.setQuestionName(questionNameElement.getChildText("text"));
            List<Element> questionTextElements = questionElement.getChildren(
                    "questiontext");
            String[] questionTexts = new String[questionTextElements.size()];
            int i = 0;
            for(Element questionTextElement : questionTextElements)
            {
              questionTexts[i++] = questionTextElement.getChildText("text");
            }
            qb.setQuestionTexts(questionTexts);
            if(questionType.equals(ContentElementType.MULTIPLE_CHOICE))
            {
              List<Element> answers = questionElement.getChildren("answer");
              int choiceCount = answers.size();
              qb.setScoreFractions(new Float[choiceCount]);
              Element shuffleanswers = questionElement.getChild(
                      "shuffleanswers");
              boolean doShuffle = shuffleanswers.getText().equals("1");
              qb.setShuffle(doShuffle);
              Element answernumbering = questionElement.getChild(
                      "answernumbering");
              qb.setNumbering(answernumbering.getText());
              qb.setChoices(new String[choiceCount]);
              qb.setFeedbacks(new String[choiceCount]);
              i = 0;
              for(Element answerElement : answers)
              {
                qb.getChoices()[i] = answerElement.getChildText("text");
                qb.getFeedbacks()[i] = answerElement.getChild("feedback")
                        .getChildText("text");
                try
                {
                  qb.getScoreFractions()[i] = Float.valueOf(answerElement
                          .getAttributeValue("fraction"));
                }
                catch(NumberFormatException e)
                {
                  qb.getScoreFractions()[i] = 0.0F;
                }
                i++;
              }
              if(doShuffle)
              {
                Random rnd = new Random();
                for(i = choiceCount; i > 1;)
                {
                  int j = rnd.nextInt(i--);
                  Float f = qb.getScoreFractions()[i];
                  qb.getScoreFractions()[i] = qb.getScoreFractions()[j];
                  qb.getScoreFractions()[j] = f;
                  String s = qb.getChoices()[i];
                  qb.getChoices()[i] = qb.getChoices()[j];
                  qb.getChoices()[j] = s;
                  s = qb.getFeedbacks()[i];
                  qb.getFeedbacks()[i] = qb.getFeedbacks()[j];
                  qb.getFeedbacks()[j] = s;
                }
              }
              Element single = questionElement.getChild("single");
              qb.setSingleAnswer(single.getText().equals("true"));
            }
            else if(questionType.equals(ContentElementType.SURVEY))
            {
              List<Element> answers = questionElement.getChildren("answer");
              int choiceCount = answers.size();
              qb.setChoices(new String[choiceCount]);
              i = 0;
              for(Element answerElement : answers)
              {
                qb.getChoices()[i] = answerElement.getChildText("text");
                i++;
              }
              Element single = questionElement.getChild("single");
              qb.setSingleAnswer(single.getText().equals("true"));
              Element detail = questionElement.getChild("detail");
              qb.setDetail(detail.getText());
            }
            else if(questionType.equals(ContentElementType.PROGRAM))
            {
              try
              {
                Element answernumbering = questionElement.getChild(
                        "answernumbering");
                qb.setNumbering(answernumbering.getText());
              }
              catch(Exception e)
              {
              }
              Element zipData = questionElement.getChild("zipData");
              if(zipData != null)
              {
                qb.setQuestionZipData(zipData.getText());
              }
              Element zipName = questionElement.getChild("zipName");
              if(zipName != null)
              {
                qb.setQuestionZipName(zipName.getText());
              }
            }
            questions.add(qb);
          }
        }
      }
    }
    catch(JDOMException | IOException e)
    {
      e.printStackTrace();
      return false;
    }
    return hadQuestions;
  }

  public boolean readQuestions()
  {
    boolean hadQuestions = false;
    try
    {
      hadQuestions = decodeQuestions(Tag.selectByTaggedIdAndType(connection,
              taskId, TagType.QUIZ_QUESTION.getValue()));
    }
    catch(SQLException e)
    {
      e.printStackTrace();
    }
    return hadQuestions;
  }

  public boolean readQuestion(Integer questionId)
  {
    boolean hadQuestions = false;
    try
    {
      Tag question = Tag.select1ById(connection, questionId);
      ArrayList<Tag> qList = new ArrayList<>();
      qList.add(question);
      hadQuestions = decodeQuestions(qList);
    }
    catch(InvalidValueException | NoSuchItemException | ObjectNotValidException
                  | SQLException e)
    {
      e.printStackTrace();
    }
    return hadQuestions;
  }

  public void saveQuestions()
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException
  {
    XMLOutputter xmlOutput = new XMLOutputter();
    // Display nice output
    xmlOutput.setFormat(Format.getRawFormat());
    xmlOutput.getFormat().setLineSeparator(LineSeparator.NL);
    ArrayList<Tag> oldQuestions = Tag.selectByTaggedIdAndType(connection,
            taskId, TagType.QUIZ_QUESTION.getValue());
    HashMap<Integer, Tag> oldQuestionMap = new HashMap<>();
    for(Tag questionTag : oldQuestions)
    {
      oldQuestionMap.put(questionTag.getId(), questionTag);
    }
    int rank = 0;
    for(QuestionBean question : questions)
    {
      Element questionElement = new Element("question");
      Element questionNameElement = new Element("name");
      Element questionNameTextElement = new Element("text");
      ContentElementType type = ContentElementType.getByValue(question
              .getQuestionType());
      questionElement.setAttribute("type", type.getAlias());
      questionNameTextElement.addContent(question.getQuestionName());
      questionNameElement.addContent(questionNameTextElement);
      questionElement.addContent(questionNameElement);
      for(String questionText : question.getQuestionTexts())
      {
        Element questionTextElement = new Element("questiontext");
        Element questionTextTextElement = new Element("text");
        questionTextTextElement.setText(questionText);
        questionTextElement.addContent(questionTextTextElement);
        questionElement.addContent(questionTextElement);
      }
      if(type.equals(ContentElementType.MULTIPLE_CHOICE))
      {
        String[] choices = question.getChoices();
        String[] feedbacks = question.getFeedbacks();
        Float[] fractions = question.getScoreFractions();
        if(choices != null)
        {
          int choiceCount = choices.length;
          for(int i = 0; i < choiceCount; i++)
          {
            Element answerElement = new Element("answer");
            Element answerTextElement = new Element("text");
            Element feedbackElement = new Element("feedback");
            Element feedbackTextElement = new Element("text");
            answerElement.setAttribute("fraction", fractions[i].toString());
            answerTextElement.setText(choices[i]);
            answerElement.setContent(answerTextElement);
            feedbackTextElement.setText(feedbacks[i]);
            feedbackElement.setContent(feedbackTextElement);
            answerElement.addContent(feedbackElement);
            questionElement.addContent(answerElement);
          }
        }
        Element singleElement = new Element("single");
        singleElement.setText(question.isSingleAnswer() ? "true" : "false");
        questionElement.addContent(singleElement);
        Element shuffleAnswersElement = new Element("shuffleanswers");
        shuffleAnswersElement.setText(question.isShuffle() ? "1" : "0");
        questionElement.addContent(shuffleAnswersElement);
        Element answerNumberingElement = new Element("answernumbering");
        answerNumberingElement.setText(question.getNumbering());
        questionElement.addContent(answerNumberingElement);
      }
      else if(type.equals(ContentElementType.SURVEY))
      {
        String[] choices = question.getChoices();
        int choiceCount = choices.length;
        for(int i = 0; i < choiceCount; i++)
        {
          Element answerElement = new Element("answer");
          Element answerTextElement = new Element("text");
          answerTextElement.setText(choices[i]);
          answerElement.setContent(answerTextElement);
          questionElement.addContent(answerElement);
        }
        Element singleElement = new Element("single");
        singleElement.setText(question.isSingleAnswer() ? "true" : "false");
        questionElement.addContent(singleElement);
        Element detailElement = new Element("detail");
        detailElement.setText(question.getDetail());
        questionElement.addContent(detailElement);
      }
      else if(type.equals(ContentElementType.PROGRAM))
      {
        Element answerNumberingElement = new Element("answernumbering");
        answerNumberingElement.setText(question.getNumbering());
        questionElement.addContent(answerNumberingElement);
        Element zipDataElement = new Element("zipData");
        zipDataElement.setText(question.getQuestionZipData());
        questionElement.addContent(zipDataElement);
        Element zipNameElement = new Element("zipName");
        zipNameElement.setText(question.getQuestionZipName());
        questionElement.addContent(zipNameElement);
      }
      Integer questionId = question.getQuestionId();
      Tag questionTag;
      boolean doUpdate = false;
      if(questionId != null)
      {
        questionTag = oldQuestionMap.get(questionId);
        doUpdate = true;
        oldQuestionMap.remove(questionId);
      }
      else
      {
        questionTag = new Tag();
        questionTag.setTaggedId(taskId);
        questionTag.setType(TagType.QUIZ_QUESTION.getValue());
      }
      questionTag.setAuthorId(authorId);
      questionTag.setText(xmlOutput
              .outputString(new Document(questionElement)));
      questionTag.setRank(rank++);
      //Status must be set to something
      questionTag.setStatus(0);
      if(doUpdate)
      {
        questionTag.update(connection);
      }
      else
      {
        questionTag.insert(connection);
      }
    }
    for(Tag removedQuestion : oldQuestionMap.values())
    {
      removedQuestion.delete(connection);
    }
  }

  public void migrateQuestionDocumentIds(Integer dbId,
          HashMap<Integer, Integer[]> idMap)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException
  {
    XMLOutputter xmlOutput = new XMLOutputter();
    // Display nice output
    xmlOutput.setFormat(Format.getRawFormat());
    xmlOutput.getFormat().setLineSeparator(LineSeparator.NL);
    ArrayList<Tag> oldQuestions = Tag.selectByTaggedIdAndType(connection,
            taskId, TagType.QUIZ_QUESTION.getValue());
    HashMap<Integer, Tag> oldQuestionMap = new HashMap<>();
    for(Tag questionTag : oldQuestions)
    {
      oldQuestionMap.put(questionTag.getId(), questionTag);
    }
    for(QuestionBean question : questions)
    {
      Element questionElement = new Element("question");
      Element questionNameElement = new Element("name");
      Element questionNameTextElement = new Element("text");
      ContentElementType type = ContentElementType.getByValue(question
              .getQuestionType());
      questionElement.setAttribute("type", type.getAlias());
      questionNameTextElement.addContent(question.getQuestionName());
      questionNameElement.addContent(questionNameTextElement);
      questionElement.addContent(questionNameElement);
      for(String questionText : question.getQuestionTexts())
      {
        String migratedText = migrateStringDocumentIds(questionText, taskId,
                dbId, idMap);
        if(migratedText != null)
        {
          questionText = migratedText;
        }
        Element questionTextElement = new Element("questiontext");
        Element questionTextTextElement = new Element("text");
        questionTextTextElement.setText(questionText);
        questionTextElement.addContent(questionTextTextElement);
        questionElement.addContent(questionTextElement);
      }
      if(type.equals(ContentElementType.MULTIPLE_CHOICE))
      {
        String[] choices = question.getChoices();
        String[] feedbacks = question.getFeedbacks();
        Float[] fractions = question.getScoreFractions();
        if(choices != null)
        {
          int choiceCount = choices.length;
          for(int i = 0; i < choiceCount; i++)
          {
            Element answerElement = new Element("answer");
            Element answerTextElement = new Element("text");
            Element feedbackElement = new Element("feedback");
            Element feedbackTextElement = new Element("text");
            answerElement.setAttribute("fraction", fractions[i].toString());
            answerTextElement.setText(choices[i]);
            answerElement.setContent(answerTextElement);
            feedbackTextElement.setText(feedbacks[i]);
            feedbackElement.setContent(feedbackTextElement);
            answerElement.addContent(feedbackElement);
            questionElement.addContent(answerElement);
          }
        }
        Element singleElement = new Element("single");
        singleElement.setText(question.isSingleAnswer() ? "true" : "false");
        questionElement.addContent(singleElement);
        Element shuffleAnswersElement = new Element("shuffleanswers");
        shuffleAnswersElement.setText(question.isShuffle() ? "1" : "0");
        questionElement.addContent(shuffleAnswersElement);
        Element answerNumberingElement = new Element("answernumbering");
        answerNumberingElement.setText(question.getNumbering());
        questionElement.addContent(answerNumberingElement);
      }
      else if(type.equals(ContentElementType.SURVEY))
      {
        String[] choices = question.getChoices();
        int choiceCount = choices.length;
        for(int i = 0; i < choiceCount; i++)
        {
          Element answerElement = new Element("answer");
          Element answerTextElement = new Element("text");
          answerTextElement.setText(choices[i]);
          answerElement.setContent(answerTextElement);
          questionElement.addContent(answerElement);
        }
        Element singleElement = new Element("single");
        singleElement.setText(question.isSingleAnswer() ? "true" : "false");
        questionElement.addContent(singleElement);
        Element detailElement = new Element("detail");
        detailElement.setText(question.getDetail());
        questionElement.addContent(detailElement);
      }
      else if(type.equals(ContentElementType.PROGRAM))
      {
        Element answerNumberingElement = new Element("answernumbering");
        answerNumberingElement.setText(question.getNumbering());
        questionElement.addContent(answerNumberingElement);
        Element zipDataElement = new Element("zipData");
        zipDataElement.setText(question.getQuestionZipData());
        questionElement.addContent(zipDataElement);
        Element zipNameElement = new Element("zipName");
        zipNameElement.setText(question.getQuestionZipName());
        questionElement.addContent(zipNameElement);
      }
      Integer questionId = question.getQuestionId();
      Tag questionTag = oldQuestionMap.get(questionId);
      questionTag.setText(xmlOutput.outputString(new Document(questionElement)));
      questionTag.update(connection);
    }
  }

  public boolean gradeMultipleChoiceQuestions(Integer answererId)
          throws SQLException, IOException, InvalidValueException,
                 ObjectNotValidException, WetoTimeStampException
  {
    boolean hadMultipleChoiceQuestions = false;
    HashMap<Integer, Float> totalScoreMap = new HashMap<>();
    for(QuestionBean qb : questions)
    {
      if(ContentElementType.MULTIPLE_CHOICE.getValue().equals(qb
              .getQuestionType()))
      {
        hadMultipleChoiceQuestions = true;
        ArrayList<Tag> answers;
        if(answererId != null)
        {
          answers = new ArrayList<>();
          try
          {
            answers.add(Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(
                    connection, taskId, qb.getQuestionId(), answererId,
                    TagType.QUIZ_ANSWER.getValue()));
          }
          catch(NoSuchItemException e)
          {
          }
        }
        else
        {
          answers = Tag.selectByTaggedIdAndRankAndType(connection, taskId, qb
                  .getQuestionId(), TagType.QUIZ_ANSWER.getValue());
        }
        for(Tag answer : answers)
        {
          populateAnswers(qb, answer);
          String[][] answerTable = qb.getUserAnswers();
          if((answerTable != null) && (answerTable[0] != null))
          {
            Float answerScore = 0.0F;
            JsonArray casesJson = new JsonArray();
            for(int i = 0; i < answerTable[0].length; ++i)
            {
              if("true".equals(answerTable[0][i]))
              {
                Float scoreFraction = qb.getScoreFractions()[i];
                String feedback = qb.getFeedbacks()[i];
                answerScore += ((scoreFraction != null) ? scoreFraction : 0.0F);
                JsonObject scoreJson = new JsonObject();
                scoreJson.addProperty("choice", i);
                scoreJson.addProperty("score", (scoreFraction != null)
                                                       ? scoreFraction : 0.0F);
                scoreJson.addProperty("feedback", feedback);
                casesJson.add(scoreJson);
              }
            }
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("mark", answerScore);
            resultJson.add("cases", casesJson);
            Tag quizScoreTag = new Tag();
            quizScoreTag.setType(TagType.QUIZ_SCORE.getValue());
            quizScoreTag.setTaggedId(taskId);
            quizScoreTag.setRank(answer.getId());
            quizScoreTag.setAuthorId(answer.getAuthorId());
            quizScoreTag.setText(resultJson.toString());
            quizScoreTag.setStatus(SubmissionStatus.QUIZ_SUBMISSION.getValue());
            quizScoreTag.insert(connection);
            Float score = totalScoreMap.get(answer.getAuthorId());
            if(score == null)
            {
              score = answerScore;
            }
            else
            {
              score += answerScore;
            }
            totalScoreMap.put(answer.getAuthorId(), score);
          }
        }
      }
    }
    return hadMultipleChoiceQuestions;
  }

  public QuestionBean selectRandomQuestion()
  {
    int rnd = 0;
    if(questions.size() > 1)
    {
      rnd = new Random().nextInt(questions.size() - 1);
    }
    return questions.get(rnd);
  }

  public QuestionBean selectQuestion(Integer questionId)
  {
    QuestionBean result = null;
    for(QuestionBean question : questions)
    {
      if(question.getQuestionId().equals(questionId))
      {
        result = question;
        break;
      }
    }
    return result;
  }

  public ArrayList<QuestionBean> getQuestions()
  {
    return questions;
  }

  public void setQuestions(ArrayList<QuestionBean> questions)
  {
    this.questions = questions;
  }

  public static void populateAnswers(QuestionBean q, Tag answer)
          throws IOException, WetoTimeStampException
  {
    String answerString = answer.getText();
    q.setUserAnswerDate(answer.getTimeStampString());
    q.setUserAnswers(null);
    if((answerString != null) && !answerString.isEmpty())
    {
      boolean notAllEmpty = false;
      StringReader sr = new StringReader(answerString);
      WetoCsvReader wcr = new WetoCsvReader(sr);
      ArrayList<List<String>> answerLists = new ArrayList<>();
      int i = 0;
      for(List<String> al = wcr.read(); al != null; al = wcr.read())
      {
        answerLists.add(al);
        notAllEmpty = true;
      }
      if(notAllEmpty)
      {
        if(q.getQuestionType().equals(ContentElementType.MULTIPLE_CHOICE
                .getValue()))
        {
          String[] choices = q.getChoices();
          String[] userAnswers = new String[choices.length];
          HashMap<String, Integer> choiceMap = new HashMap<>();
          i = 0;
          for(String choice : choices)
          {
            choiceMap.put(choice, i++);
          }
          for(String userAnswer : answerLists.get(0))
          {
            Integer index = choiceMap.get(userAnswer);
            if(index != null)
            {
              userAnswers[index] = "true";
            }
          }
          String[][] ua = new String[1][];
          ua[0] = userAnswers;
          q.setUserAnswers(ua);
        }
        else if(q.getQuestionType().equals(ContentElementType.ESSAY.getValue())
                || q.getQuestionType().equals(ContentElementType.PROGRAM
                        .getValue()))
        {
          String[][] ua = new String[1][];
          ua[0] = answerLists.get(0).toArray(new String[0]);
          q.setUserAnswers(ua);
        }
        else if(q.getQuestionType().equals(ContentElementType.SURVEY.getValue()))
        {
          final String[] choices = q.getChoices();
          final HashMap<String, Integer> choiceMap = new HashMap<>();
          i = 0;
          for(String choice : choices)
          {
            choiceMap.put(choice, i++);
          }
          String[][] ua = new String[answerLists.size()][];
          i = 0;
          for(List<String> al : answerLists)
          {
            String[] userAnswers = new String[choices.length];
            for(String userAnswer : al)
            {
              Integer index = choiceMap.get(userAnswer);
              if(index != null)
              {
                userAnswers[index] = "true";
              }
            }
            ua[i++] = userAnswers;
          }
          q.setUserAnswers(ua);
        }
      }
    }
  }

  public static void populateResults(Connection conn, QuestionBean qb,
          Tag quizAnswerTag, WetoCourseAction wca)
          throws SQLException
  {
    try
    {
      Tag quizScoreTag = Tag.select1ByTaggedIdAndRankAndAuthorIdAndType(conn, qb
              .getTaskId(), quizAnswerTag.getId(), quizAnswerTag.getAuthorId(),
              TagType.QUIZ_SCORE.getValue());
      String resultString = quizScoreTag.getText();
      final boolean isStudent = wca.getNavigator().isStudent();
      final String notShownStr = wca.getText(
              "autograding.message.incorrectResult");
      if(resultString != null)
      {
        resultString = resultString.trim();
        if(!resultString.isEmpty())
        {
          JsonObject resultJson = new JsonParser().parse(quizScoreTag.getText())
                  .getAsJsonObject();
          float mark = 0.0F;
          JsonElement markJson = resultJson.get("mark");
          if((markJson != null) && !markJson.isJsonNull())
          {
            mark = markJson.getAsFloat();
          }
          JsonArray casesJson = resultJson.getAsJsonArray("cases");
          if(casesJson != null)
          {
            HashMap<Integer, Integer> fullFeedbackMap = new HashMap<>();
            ArrayList<Tag> fullFeedbackTags = Tag
                    .selectByTaggedIdAndRankAndType(conn, qb.getTaskId(),
                            quizAnswerTag.getId(), TagType.FEEDBACK.getValue());
            for(Tag fullFeedbackTag : fullFeedbackTags)
            {
              String text = fullFeedbackTag.getText();
              if((text != null) && text.startsWith("{"))
              {
                JsonObject fullFeedbackJson = new JsonParser().parse(text)
                        .getAsJsonObject();
                int test = Integer.MIN_VALUE;
                JsonElement testJson = fullFeedbackJson.get("test");
                if((testJson != null) && !testJson.isJsonNull())
                {
                  test = testJson.getAsInt();
                }
                fullFeedbackMap.put(test, fullFeedbackTag.getId());
              }
            }
            String[] feedbacks = new String[casesJson.size()];
            Integer[] testNos = new Integer[casesJson.size()];
            Float[] scores = new Float[casesJson.size()];
            Integer[] fullFeedbacks = new Integer[casesJson.size()];
            int i = 0;
            for(JsonElement caseElem : casesJson)
            {
              JsonObject caseJson = caseElem.getAsJsonObject();
              String feedback = caseJson.get("feedback").getAsString();
              JsonElement phaseJson = caseJson.get("phase");
              feedbacks[i] = feedback;
              boolean hideFeedback = false;
              if((phaseJson != null) && !phaseJson.isJsonNull() && !"OK".equals(
                      feedback))
              {
                int phase = phaseJson.getAsInt();
                if(isStudent
                        && ((phase == AutoGradeJobQueue.IMMEDIATE_PRIVATE)
                        || (phase == AutoGradeJobQueue.FINAL_PRIVATE)))
                {
                  hideFeedback = true;
                  feedbacks[i] = notShownStr;
                }
              }
              JsonElement testJson = caseJson.get("test");
              if((testJson != null) && !testJson.isJsonNull())
              {
                int test = testJson.getAsInt();
                if(!hideFeedback)
                {
                  fullFeedbacks[i] = fullFeedbackMap.get(test);
                }
                testNos[i] = test;
              }
              float score = 0.0F;
              JsonElement scoreJson = caseJson.get("score");
              if((scoreJson != null) && !scoreJson.isJsonNull())
              {
                score = scoreJson.getAsFloat();
              }
              scores[i] = score;
              i += 1;
            }
            qb.setResultFeedbacks(feedbacks);
            qb.setResultTestNos(testNos);
            qb.setResultScores(scores);
            qb.setResultFullFeedbacks(fullFeedbacks);
          }
          qb.setResultMark(mark);
          JsonElement errorJson = resultJson.get("error");
          if(errorJson == null)
          {
            errorJson = resultJson.get("warning");
          }
          if((errorJson != null) && !errorJson.isJsonNull())
          {
            qb.setResultError(errorJson.getAsString());
            ArrayList<Tag> fullErrors = Tag.selectByTaggedIdAndRankAndType(
                    conn, qb.getTaskId(), quizAnswerTag.getId(),
                    TagType.COMPILER_RESULT.getValue());
            if(!fullErrors.isEmpty())
            {
              qb.setResultFullError(fullErrors.get(0).getId());
            }
          }
        }
      }
    }
    catch(NoSuchItemException e2)
    {
    }
  }

  public static Map<Integer, Map.Entry<StringBuilder, Float>> getQuizScores(
          Connection conn, Integer taskId, Integer studentId, QuizModel quiz,
          ArrayList<Tag> quizScores)
          throws SQLException, IOException, InvalidValueException,
                 ObjectNotValidException, WetoTimeStampException,
                 TooManyItemsException, NoSuchItemException
  {
    Map<Integer, Map.Entry<StringBuilder, Float>> quizScoreMap = new HashMap<>();
    if(quiz == null)
    {
      quiz = new QuizModel(conn, taskId, null);
      quiz.readQuestions();
    }
    if(quizScores == null)
    {
      if(studentId != null)
      {
        quizScores = Tag.selectByTaggedIdAndAuthorIdAndType(conn, taskId,
                studentId, TagType.QUIZ_SCORE.getValue());
      }
      else
      {
        quizScores = Tag.selectByTaggedIdAndType(conn, taskId,
                TagType.QUIZ_SCORE.getValue());
      }
    }
    HashMap<Integer, QuestionBean> questionMap = new HashMap<>();
    for(QuestionBean question : quiz.getQuestions())
    {
      questionMap.put(question.getQuestionId(), question);
    }
    ArrayList<Tag> quizAnswers = null;
    if(studentId != null)
    {
      quizAnswers = Tag.selectByTaggedIdAndAuthorIdAndType(conn, taskId,
              studentId, TagType.QUIZ_ANSWER.getValue());
    }
    else
    {
      quizAnswers = Tag.selectByTaggedIdAndType(conn, taskId,
              TagType.QUIZ_ANSWER.getValue());
    }
    HashMap<Integer, Integer> answerQuestionMap = new HashMap<>();
    for(Tag quizAnswer : quizAnswers)
    {
      answerQuestionMap.put(quizAnswer.getId(), quizAnswer.getRank());
    }
    HashMap<Map.Entry<Integer, Integer>, Tag> scoreTagMap = new HashMap<>();
    for(Tag quizScore : quizScores)
    {
      Map.Entry<Integer, Integer> questionScore
                                          = new AbstractMap.SimpleEntry<Integer, Integer>(
                      quizScore.getAuthorId(), quizScore.getRank());
      Tag prevScore = scoreTagMap.get(questionScore);
      if(prevScore != null)
      {
        prevScore.delete(conn);
      }
      scoreTagMap.put(questionScore, quizScore);
    }
    for(Tag quizScore : scoreTagMap.values())
    {
      studentId = quizScore.getAuthorId();
      JsonObject scoreRootJson = new JsonParser().parse(quizScore.getText())
              .getAsJsonObject();
      Float score = (scoreRootJson.get("mark") != null) ? scoreRootJson.get(
              "mark").getAsFloat() : 0.0F;
      Integer questionId = answerQuestionMap.get(quizScore.getRank());
      String scoreComment = null;
      if(questionId != null)
      {
        QuestionBean question = questionMap.get(questionId);
        if(question != null)
        {
          scoreComment = question.getQuestionName() + ": " + score;
        }
      }
      Map.Entry<StringBuilder, Float> studentScore = quizScoreMap.get(
              studentId);
      if(studentScore == null)
      {
        studentScore = new AbstractMap.SimpleEntry<StringBuilder, Float>(
                new StringBuilder(), 0.0F);
        quizScoreMap.put(studentId, studentScore);
      }
      studentScore.setValue(studentScore.getValue() + score);
      if(scoreComment != null)
      {
        if(studentScore.getKey().length() > 0)
        {
          studentScore.getKey().append("<br/>" + scoreComment);
        }
        else
        {
          studentScore.getKey().append(scoreComment);
        }
      }
    }
    return quizScoreMap;
  }

}
