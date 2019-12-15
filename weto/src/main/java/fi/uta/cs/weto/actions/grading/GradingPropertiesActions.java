package fi.uta.cs.weto.actions.grading;

import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.model.AggregateFunctionType;
import fi.uta.cs.weto.model.GradingModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.TreeSet;

public class GradingPropertiesActions
{
  public static class View extends WetoTeacherAction
  {
    private ArrayList<String[]> gradeTable;
    private boolean mandatoryTask;
    private boolean ignoreGroups;
    private boolean requirePeerReview;
    private String roundMode;
    private boolean calculateAverage;
    private Integer aggregateFunction;
    private Float minScore;
    private Float maxScore;
    private Float scoreStep;
    private Integer maxFailedSubtasks;
    private String latePenalties;
    private boolean hasSubtasks;
    private AggregateFunctionType[] aggregateFunctionTypes;

    public View()
    {
      super(Tab.GRADING.getBit(), 0, 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      gradeTable = new ArrayList<>();
      String gradeTableString = scoring.getGradeTable();
      if(gradeTableString != null && !gradeTableString.isEmpty())
      {
        String[] scoreMarkStrings = gradeTableString.split(";", 0);
        for(String scoreMarkString : scoreMarkStrings)
        {
          gradeTable.add(scoreMarkString.split("=", 2));
        }
      }
      mandatoryTask = Boolean.parseBoolean(scoring.getProperty("mandatoryTask"));
      ignoreGroups = Boolean.parseBoolean(scoring.getProperty("ignoreGroups"));
      requirePeerReview = Boolean.parseBoolean(scoring.getProperty(
              "requirePeerReview"));
      roundMode = scoring.getProperty("roundMode", "None");
      calculateAverage = Boolean.parseBoolean(scoring.getProperty(
              "calculateAverage"));
      aggregateFunction = Integer.parseInt(scoring.getProperty(
              "aggregateFunction", "0"));
      minScore = Float.parseFloat(scoring.getProperty("minScore", "0"));
      maxScore = Float.parseFloat(scoring.getProperty("maxScore", "0"));
      scoreStep = Float.parseFloat(scoring.getProperty("scoreStep", "1"));
      try
      {
        maxFailedSubtasks = Integer.parseInt(scoring.getProperty(
                "maxFailedSubtasks"));
      }
      catch(NumberFormatException e)
      {
        maxFailedSubtasks = null;
      }
      latePenalties = scoring.getProperty("latePenalties");
      hasSubtasks = SubtaskView.hasSubtasks(conn, taskId);
      aggregateFunctionTypes = AggregateFunctionType.values();
      return SUCCESS;
    }

    public ArrayList<String[]> getGradeTable()
    {
      return gradeTable;
    }

    public boolean isMandatoryTask()
    {
      return mandatoryTask;
    }

    public boolean isIgnoreGroups()
    {
      return ignoreGroups;
    }

    public boolean isRequirePeerReview()
    {
      return requirePeerReview;
    }

    public String getRoundMode()
    {
      return roundMode;
    }

    public boolean isCalculateAverage()
    {
      return calculateAverage;
    }

    public Integer getAggregateFunction()
    {
      return aggregateFunction;
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

    public Integer getMaxFailedSubtasks()
    {
      return maxFailedSubtasks;
    }

    public String getLatePenalties()
    {
      return latePenalties;
    }

    public boolean isHasSubtasks()
    {
      return hasSubtasks;
    }

    public AggregateFunctionType[] getAggregateFunctionTypes()
    {
      return aggregateFunctionTypes;
    }

  }

  public static class AddToGradeTable extends WetoTeacherAction
  {
    private String score;
    private Float mark;

    public AddToGradeTable()
    {
      super(Tab.GRADING.getBit(), 0, Tab.GRADING.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      ArrayList<GradingModel.GradeTableEntry> oldGT = GradingModel
              .decodeGradeTable(scoring.getGradeTable());
      GradingModel.GradeTableEntry newEntry = GradingModel
              .decodeGradeTableScore(score);
      newEntry.setMark(mark);
      oldGT.add(newEntry);
      ArrayList<GradingModel.GradeTableEntry> newGT = GradingModel
              .sortGradeTable(oldGT);
      scoring.setGradeTable(GradingModel.gradeTableToString(newGT));
      scoring.update(conn);
      addActionMessage(getText(
              "gradingProperties.message.addGradeTableEntrySuccess"));
      return SUCCESS;
    }

    public void setScore(String score)
    {
      this.score = score;
    }

    public void setMark(String mark)
    {
      this.mark = Float.parseFloat(mark);
    }

  }

  public static class SaveGradeTable extends WetoTeacherAction
  {
    private String[] entryScores;
    private String[] entryMarks;
    private String[] checkedEntries;

    public SaveGradeTable()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      ArrayList<GradingModel.GradeTableEntry> gradeTable = new ArrayList<>();
      for(int i = 0; i < entryScores.length; ++i)
      {
        GradingModel.GradeTableEntry entry = GradingModel.decodeGradeTableScore(
                entryScores[i]);
        if((entryMarks[i] != null) && !entryMarks[i].trim().isEmpty())
        {
          entry.setMark(Float.parseFloat(entryMarks[i]));
        }
        gradeTable.add(entry);
      }
      gradeTable = GradingModel.sortGradeTable(gradeTable);
      scoring.setGradeTable(GradingModel.gradeTableToString(gradeTable));
      scoring.update(conn);
      addActionMessage(
              getText("gradingProperties.message.saveGradeTableSuccess"));
      return SUCCESS;
    }

    public void setEntryScores(String[] entryScores)
    {
      this.entryScores = entryScores;
    }

    public void setEntryMarks(String[] entryMarks)
    {
      this.entryMarks = entryMarks;
    }

    public void setCheckedEntries(String[] checkedEntries)
    {
      this.checkedEntries = checkedEntries;
    }

  }

  public static class DeleteFromGradeTable extends WetoTeacherAction
  {
    private String[] entryScores;
    private String[] entryMarks;
    private String[] checkedEntries;

    public DeleteFromGradeTable()
    {
      super(Tab.GRADING.getBit(), 0, 0, Tab.GRADING.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      TreeSet<GradingModel.GradeTableEntry> deletedScores = new TreeSet<>();
      for(String scoreString : checkedEntries)
      {
        deletedScores.add(GradingModel.decodeGradeTableScore(scoreString));
      }
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      ArrayList<GradingModel.GradeTableEntry> oldGradeTable = GradingModel
              .decodeGradeTable(scoring.getGradeTable());
      ArrayList<GradingModel.GradeTableEntry> newGradeTable = new ArrayList<>();
      for(GradingModel.GradeTableEntry entry : oldGradeTable)
      {
        if(!deletedScores.contains(entry))
        {
          newGradeTable.add(entry);
        }
      }
      scoring.setGradeTable(GradingModel.gradeTableToString(newGradeTable));
      scoring.update(conn);
      addActionMessage(getText(
              "gradingProperties.message.deleteFromGradeTableSuccess"));
      return SUCCESS;
    }

    public void setEntryScores(String[] entryScores)
    {
      this.entryScores = entryScores;
    }

    public void setEntryMarks(String[] entryMarks)
    {
      this.entryMarks = entryMarks;
    }

    public void setCheckedEntries(String[] checkedEntries)
    {
      this.checkedEntries = checkedEntries;
    }

  }

  public static class GenerateGradeTable extends WetoTeacherAction
  {
    private Float lowerScore;
    private Float lowerMark;
    private Float upperScore;
    private Float upperMark;
    private String roundMode = "None";

    public GenerateGradeTable()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), Tab.GRADING.getBit(), 0);
    }

    private Float round(Float f)
    {
      if("None".equals(roundMode))
      {
        return f;
      }
      else if("Nearest".equals(roundMode))
      {
        return (float) Math.round(f);
      }
      else if("Floor".equals(roundMode))
      {
        return (float) Math.floor(f);
      }
      else
      {
        return (float) Math.ceil(f);
      }
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      ArrayList<GradingModel.GradeTableEntry> gradeTable = GradingModel
              .decodeGradeTable(scoring.getGradeTable());
      // calculate the step interval
      Float firstDiff = upperScore - lowerScore;
      Float secondDiff = upperMark - lowerMark;
      if(firstDiff < secondDiff)
      {
        Float markStep = secondDiff / firstDiff;
        Float tmpMark = lowerMark;
        for(float tmpScore = lowerScore; tmpScore <= upperScore; ++tmpScore)
        {
          gradeTable.add(new GradingModel.GradeTableEntry(round(tmpScore),
                  round(tmpMark), GradingModel.GradeTableEntry.EXACT_BOUND));
          tmpMark += markStep;
        }
      }
      else
      {
        Float scoreStep = firstDiff / secondDiff;
        Float tmpScore = lowerScore;
        for(float tmpMark = lowerMark; tmpMark <= upperMark; ++tmpMark)
        {
          gradeTable.add(new GradingModel.GradeTableEntry(round(tmpScore),
                  round(tmpMark), GradingModel.GradeTableEntry.EXACT_BOUND));
          tmpScore += scoreStep;
        }
      }
      gradeTable = GradingModel.sortGradeTable(gradeTable);
      scoring.setGradeTable(GradingModel.gradeTableToString(gradeTable));
      scoring.update(conn);
      addActionMessage(getText(
              "gradingProperties.message.generateGradeTableSuccess"));
      return SUCCESS;
    }

    public void setLowerScore(String lowerScore)
    {
      this.lowerScore = Float.parseFloat(lowerScore);
    }

    public void setLowerMark(String lowerMark)
    {
      this.lowerMark = Float.parseFloat(lowerMark);
    }

    public void setUpperScore(String upperScore)
    {
      this.upperScore = Float.parseFloat(upperScore);
    }

    public void setUpperMark(String upperMark)
    {
      this.upperMark = Float.parseFloat(upperMark);
    }

    public void setRoundMode(String roundMode)
    {
      this.roundMode = roundMode;
    }

  }

  public static class SaveGradingProperties extends WetoTeacherAction
  {
    private Integer aggregateFunction;
    private Float minScore;
    private Float maxScore;
    private Float scoreStep;
    private Integer maxFailedSubtasks;
    private boolean calculateAverage;
    private boolean mandatoryTask;
    private boolean ignoreGroups;
    private boolean requirePeerReview;
    private String roundMode;
    private String latePenalties;

    public SaveGradingProperties()
    {
      super(Tab.GRADING.getBit(), Tab.GRADING.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      Scoring scoring = Scoring.select1ByTaskId(conn, taskId);
      scoring.setProperty("minScore", (minScore != null) ? minScore.toString()
                                              : null);
      scoring.setProperty("maxScore", (maxScore != null) ? maxScore.toString()
                                              : null);
      scoring.setProperty("scoreStep", (scoreStep != null) ? scoreStep
              .toString() : null);
      scoring.setProperty("maxFailedSubtasks", (maxFailedSubtasks != null)
                                                       ? maxFailedSubtasks
              .toString()
                                                       : null);
      scoring.setProperty("aggregateFunction", (aggregateFunction != null)
                                                       ? aggregateFunction
              .toString()
                                                       : null);
      scoring.setProperty("latePenalties", latePenalties);
      scoring
              .setProperty("calculateAverage", Boolean
                      .toString(calculateAverage));
      scoring.setProperty("mandatoryTask", Boolean.toString(mandatoryTask));
      scoring.setProperty("ignoreGroups", Boolean.toString(ignoreGroups));
      scoring.setProperty("roundMode", (roundMode != null) ? roundMode : "None");
      scoring.setProperty("requirePeerReview", Boolean.toString(
              requirePeerReview));
      scoring.update(conn);
      addActionMessage(getText("gradingProperties.message.saveSuccess"));
      return SUCCESS;
    }

    public void setAggregateFunction(Integer aggregateFunction)
    {
      this.aggregateFunction = aggregateFunction;
    }

    public void setMinScore(String minScore)
    {
      this.minScore = Float.parseFloat(minScore);
    }

    public void setMaxScore(String maxScore)
    {
      this.maxScore = Float.parseFloat(maxScore);
    }

    public void setScoreStep(String scoreStep)
    {
      this.scoreStep = Float.parseFloat(scoreStep);
    }

    public void setMaxFailedSubtasks(Integer maxFailedSubtasks)
    {
      this.maxFailedSubtasks = maxFailedSubtasks;
    }

    public void setMandatoryTask(Boolean mandatoryTask)
    {
      this.mandatoryTask = mandatoryTask;
    }

    public void setIgnoreGroups(boolean ignoreGroups)
    {
      this.ignoreGroups = ignoreGroups;
    }

    public void setRequirePeerReview(boolean requirePeerReview)
    {
      this.requirePeerReview = requirePeerReview;
    }

    public void setCalculateAverage(Boolean calculateAverage)
    {
      this.calculateAverage = calculateAverage;
    }

    public void setRoundMode(String roundMode)
    {
      this.roundMode = roundMode;
    }

    public void setLatePenalties(String latePenalties)
    {
      this.latePenalties = latePenalties;
    }

  }
}
