package fi.uta.cs.weto.model;

import java.util.HashSet;
import java.util.Map;

public enum AggregateFunctionType
{
  SUM(0, new Sum(), "general.header.sum"),
  MIN(1, new Min(), "general.header.min"),
  MAX(2, new Max(), "general.header.max");

  private final Integer value;
  private final String property;
  private final AggregateFunction af;

  private AggregateFunctionType(Integer value, AggregateFunction af, String name)
  {
    this.value = value;
    this.af = af;
    this.property = name;
  }

  public Integer getValue()
  {
    return value;
  }

  public Float getAggregateScore(Integer userId,
          Map<Integer, Map<Integer, Float>> subtasksMarks, Integer failedMax,
          HashSet<Integer> mandatorySubtasks)
  {
    Boolean failedMandatory = false;
    for(Map.Entry<Integer, Map<Integer, Float>> entry : subtasksMarks.entrySet())
    {
      Integer subtaskId = entry.getKey();
      Float score = entry.getValue().get(userId);
      if(GradingModel.MANDATORY_FAILED.equals(score) || (mandatorySubtasks
              .contains(subtaskId) && (score == null)))
      {
        failedMandatory = true;
        break;
      }
    }
    return failedMandatory ? GradingModel.MANDATORY_FAILED : af
            .getAggregateScore(userId, subtasksMarks, failedMax);
  }

  public String getProperty()
  {
    return property;
  }

  static public AggregateFunctionType getType(Integer value)
  {
    for(AggregateFunctionType type : AggregateFunctionType.values())
    {
      if(type.getValue().equals(value))
      {
        return type;
      }
    }

    return null;
  }

  private interface AggregateFunction
  {
    public Float getAggregateScore(Integer userId,
            Map<Integer, Map<Integer, Float>> subtaskMarks, Integer failedMax);

  }

  private static class Sum implements AggregateFunction
  {
    @Override
    public Float getAggregateScore(Integer userId,
            Map<Integer, Map<Integer, Float>> subtasksMarks, Integer failedMax)
    {
      int failed = 0;
      boolean hasScore = false;
      Float score = null;
      for(Map.Entry<Integer, Map<Integer, Float>> entry : subtasksMarks
              .entrySet())
      {
        Float subtaskMark = entry.getValue().get(userId);
        if(subtaskMark == null)
        {
          failed++;
          if((failedMax != null) && (failed > failedMax))
          {
            score = null;
            break;
          }
        }
        else
        {
          hasScore = true;
          score = (score == null) ? subtaskMark : score + subtaskMark;
        }
      }
      if((score == null) && hasScore && ((failedMax == null) || (failed
              <= failedMax)))
      {
        score = 0f;
      }
      return score;
    }

  }

  private static class Min implements AggregateFunction
  {
    @Override
    public Float getAggregateScore(Integer userId,
            Map<Integer, Map<Integer, Float>> subtasksMarks, Integer failedMax)
    {
      int failed = 0;
      boolean hasScore = false;
      Float score = null;
      for(Map.Entry<Integer, Map<Integer, Float>> entry : subtasksMarks
              .entrySet())
      {
        Float subtaskMark = entry.getValue().get(userId);
        if(subtaskMark == null)
        {
          failed++;
          if((failedMax != null) && (failed > failedMax))
          {
            score = null;
            break;
          }
        }
        else if((score == null) || (subtaskMark < score))
        {
          hasScore = true;
          score = subtaskMark;
        }
      }
      if((score == null) && hasScore && ((failedMax == null) || (failed
              <= failedMax)))
      {
        score = 0f;
      }
      return score;
    }

  }

  private static class Max implements AggregateFunction
  {
    @Override
    public Float getAggregateScore(Integer userId,
            Map<Integer, Map<Integer, Float>> subtasksMarks, Integer failedMax)
    {
      int failed = 0;
      boolean hasScore = false;
      Float score = null;
      for(Map.Entry<Integer, Map<Integer, Float>> entry : subtasksMarks
              .entrySet())
      {
        Float subtaskMark = entry.getValue().get(userId);
        if(subtaskMark == null)
        {
          failed++;
          if((failedMax != null) && (failed > failedMax))
          {
            score = null;
            break;
          }
        }
        else if((score == null) || (subtaskMark > score))
        {
          hasScore = true;
          score = subtaskMark;
        }
      }
      if((score == null) && hasScore && ((failedMax == null) || (failed
              <= failedMax)))
      {
        score = 0f;
      }
      return score;
    }

  }
}
