package fi.uta.cs.weto.model;

import fi.uta.cs.weto.util.WetoUtilities;

public class QuestionBean extends ContentElementBean
{
  private String questionName;
  private String[] questionTexts;
  private String[] choices;
  private Float[] scoreFractions;
  private String[] feedbacks;
  private String detail;
  private String numbering;
  private boolean shuffle;
  private boolean singleAnswer;
  private String questionZipData;
  private String questionZipName;
  private final Integer taskId;
  private final Integer questionId;
  private String[][] userAnswers;
  private String userAnswerDate;
  private Float resultMark;
  private String resultFeedback;
  private String resultError;
  private Integer resultTest;
  private Integer resultFullFeedback;

  public QuestionBean(Integer contentElementType, Integer taskId,
          Integer questionId)
  {
    super(contentElementType);
    this.taskId = taskId;
    this.questionId = questionId;
  }

  public QuestionBean(QuestionBean qb)
  {
    super(qb.getContentElementType());
    this.questionName = qb.questionName;
    this.questionTexts = qb.questionTexts;
    this.choices = qb.choices;
    this.scoreFractions = qb.scoreFractions;
    this.feedbacks = qb.feedbacks;
    this.detail = qb.detail;
    this.numbering = qb.numbering;
    this.shuffle = qb.shuffle;
    this.singleAnswer = qb.singleAnswer;
    this.questionZipData = qb.questionZipData;
    this.questionZipName = qb.questionZipName;
    this.taskId = qb.taskId;
    this.questionId = qb.questionId;
  }

  public Integer getQuestionType()
  {
    return getContentElementType();
  }

  public String getQuestionName()
  {
    return questionName;
  }

  public void setQuestionName(String questionName)
  {
    this.questionName = questionName;
  }

  public String[] getQuestionTexts()
  {
    return questionTexts;
  }

  public void setQuestionTexts(String[] questionTexts)
  {
    this.questionTexts = questionTexts;
  }

  public String[] getChoices()
  {
    return choices;
  }

  public void setChoices(String[] choices)
  {
    this.choices = choices;
  }

  public Float[] getScoreFractions()
  {
    return scoreFractions;
  }

  public void setScoreFractions(Float[] scoreFractions)
  {
    this.scoreFractions = scoreFractions;
  }

  public String[] getFeedbacks()
  {
    return feedbacks;
  }

  public Float getResultMark()
  {
    return resultMark;
  }

  public void setResultMark(Float resultMark)
  {
    this.resultMark = resultMark;
  }

  public String getResultFeedback()
  {
    return WetoUtilities.escapeHtml(resultFeedback);
  }

  public void setResultFeedback(String resultFeedback)
  {
    this.resultFeedback = resultFeedback;
  }

  public String getResultError()
  {
    return WetoUtilities.escapeHtml(resultError);
  }

  public void setResultError(String resultError)
  {
    this.resultError = resultError;
  }

  public Integer getResultTest()
  {
    return resultTest;
  }

  public void setResultTest(Integer resultTest)
  {
    this.resultTest = resultTest;
  }

  public Integer getResultFullFeedback()
  {
    return resultFullFeedback;
  }

  public void setResultFullFeedback(Integer resultFullFeedback)
  {
    this.resultFullFeedback = resultFullFeedback;
  }

  public void setFeedbacks(String[] feedbacks)
  {
    this.feedbacks = feedbacks;
  }

  public String getDetail()
  {
    return detail;
  }

  public void setDetail(String detail)
  {
    this.detail = detail;
  }

  public String getNumbering()
  {
    return numbering;
  }

  public void setNumbering(String numbering)
  {
    this.numbering = numbering;
  }

  public boolean isShuffle()
  {
    return shuffle;
  }

  public void setShuffle(boolean shuffle)
  {
    this.shuffle = shuffle;
  }

  public boolean isSingleAnswer()
  {
    return singleAnswer;
  }

  public void setSingleAnswer(boolean singleAnswer)
  {
    this.singleAnswer = singleAnswer;
  }

  public String getQuestionZipData()
  {
    return questionZipData;
  }

  public void setQuestionZipData(String questionZipData)
  {
    this.questionZipData = questionZipData;
  }

  public String getQuestionZipName()
  {
    return questionZipName;
  }

  public void setQuestionZipName(String questionZipName)
  {
    this.questionZipName = questionZipName;
  }

  public Integer getTaskId()
  {
    return taskId;
  }

  public Integer getQuestionId()
  {
    return questionId;
  }

  public String[][] getUserAnswers()
  {
    return userAnswers;
  }

  public void setUserAnswers(String[][] userAnswers)
  {
    this.userAnswers = userAnswers;
  }

  public String getUserAnswerDate()
  {
    return userAnswerDate;
  }

  public void setUserAnswerDate(String userAnswerDate)
  {
    this.userAnswerDate = userAnswerDate;
  }

}
