package fi.uta.cs.weto.model;

import fi.uta.cs.weto.db.AutoGradeTestScore;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Submission;
import java.util.ArrayList;

public class FileSubmissionBean
{
  private Submission submission;
  private ArrayList<Document> documents;
  private boolean[] duplicates;
  private Integer queuePos;
  private ArrayList<AutoGradeTestScore> testScores;
  private Integer compilerResultId;
  private Integer[] fullFeedbackIds;

  public Submission getSubmission()
  {
    return submission;
  }

  public void setSubmission(Submission submission)
  {
    this.submission = submission;
  }

  public ArrayList<Document> getDocuments()
  {
    return documents;
  }

  public void setDocuments(ArrayList<Document> documents)
  {
    this.documents = documents;
  }

  public boolean[] getDuplicates()
  {
    return duplicates;
  }

  public void setDuplicates(boolean[] duplicates)
  {
    this.duplicates = duplicates;
  }

  public Integer getQueuePos()
  {
    return queuePos;
  }

  public void setQueuePos(Integer queuePos)
  {
    this.queuePos = queuePos;
  }

  public ArrayList<AutoGradeTestScore> getTestScores()
  {
    return testScores;
  }

  public void setTestScores(ArrayList<AutoGradeTestScore> testScores)
  {
    this.testScores = testScores;
  }

  public Integer getCompilerResultId()
  {
    return compilerResultId;
  }

  public void setCompilerResultId(Integer compilerResultId)
  {
    this.compilerResultId = compilerResultId;
  }

  public Integer[] getFullFeedbackIds()
  {
    return fullFeedbackIds;
  }

  public void setFullFeedbackIds(Integer[] fullFeedbackIds)
  {
    this.fullFeedbackIds = fullFeedbackIds;
  }

}
