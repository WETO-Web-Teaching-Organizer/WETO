package fi.uta.cs.weto.util;

import fi.uta.cs.weto.db.DatabasePool;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.model.GradeStatus;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.SubmissionStatus;
import fi.uta.cs.weto.model.WetoTimeStamp;
import fi.uta.cs.weto.model.WetoTimeStampException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;

public class StudentDataDownloader
{
  private static class IntPair implements Comparable<IntPair>
  {
    Integer x;
    Integer y;

    public IntPair(Integer x, Integer y)
    {
      this.x = x;
      this.y = y;
    }

    @Override
    public int hashCode()
    {
      int hash = 7;
      hash = 29 * hash + Objects.hashCode(this.x);
      hash = 29 * hash + Objects.hashCode(this.y);
      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if(this == obj)
      {
        return true;
      }
      if(obj == null)
      {
        return false;
      }
      if(getClass() != obj.getClass())
      {
        return false;
      }
      final IntPair other = (IntPair) obj;
      if(!Objects.equals(this.x, other.x))
      {
        return false;
      }
      if(!Objects.equals(this.y, other.y))
      {
        return false;
      }
      return true;
    }

    @Override
    public int compareTo(IntPair t)
    {
      int cmp = 0;
      if((this.x != t.x) || (this.y != t.y))
      {
        if(this.x < t.x)
        {
          cmp = -1;
        }
        else if(this.x > t.x)
        {
          cmp = 1;
        }
        else if(this.y < t.y)
        {
          cmp = -1;
        }
        else
        {
          cmp = 1;
        }
      }
      return cmp;
    }

  }

  private static class TaskBean implements Comparable<TaskBean>
  {
    Integer dbId;
    Integer rootTaskId;
    Integer taskId;
    String taskName;

    public TaskBean(Integer dbId, Integer rootTaskId, Integer taskId,
            String taskName)
    {
      this.dbId = dbId;
      this.rootTaskId = rootTaskId;
      this.taskId = taskId;
      this.taskName = taskName;
    }

    @Override
    public int hashCode()
    {
      int hash = 7;
      hash = 29 * hash + Objects.hashCode(this.dbId);
      hash = 29 * hash + Objects.hashCode(this.taskId);
      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if(this == obj)
      {
        return true;
      }
      if(obj == null)
      {
        return false;
      }
      if(getClass() != obj.getClass())
      {
        return false;
      }
      final TaskBean other = (TaskBean) obj;
      if(!Objects.equals(this.dbId, other.dbId))
      {
        return false;
      }
      if(!Objects.equals(this.taskId, other.taskId))
      {
        return false;
      }
      return true;
    }

    @Override
    public int compareTo(TaskBean t)
    {
      int cmp = 0;
      if((this.dbId != t.dbId) || (this.rootTaskId != t.rootTaskId)
              || (this.taskId != t.taskId))
      {
        if(this.dbId < t.dbId)
        {
          cmp = -1;
        }
        else if(this.dbId > t.dbId)
        {
          cmp = 1;
        }
        else if(this.rootTaskId < t.rootTaskId)
        {
          cmp = -1;
        }
        else if(this.rootTaskId > t.rootTaskId)
        {
          cmp = 1;
        }
        else if(this.taskId < t.taskId)
        {
          cmp = -1;
        }
        else
        {
          cmp = 1;
        }
      }
      return cmp;
    }

  }

  private static Connection getConnection(String URL, String user,
          String password)
  {
    Connection conn;
    try
    {
      conn = DriverManager.getConnection(URL, user, password);
    }
    catch(Exception e)
    {
      conn = null;
      e.printStackTrace();
    }
    return conn;
  }

  public static void main(String args[]) throws SQLException,
                                                UnsupportedEncodingException,
                                                FileNotFoundException,
                                                IOException,
                                                WetoTimeStampException
  {
    HashSet<IntPair> taskIncludeSet = new HashSet<>();
    final HashMap<IntPair, Integer> taskOrderMap = new HashMap<>();
    if(args.length > 1)
    {
      InputStreamReader reader = new InputStreamReader(new FileInputStream(
              args[1]), "UTF-8");
      try(BufferedReader br = new BufferedReader(reader))
      {
        String line;
        int rank = 0;
        while((line = br.readLine()) != null)
        {
          if(!line.trim().isEmpty())
          {
            System.err.println(line);
            String[] parts = line.split(" ", 5);
            IntPair taskIds = new IntPair(Integer.valueOf(parts[1]), Integer
                    .valueOf(parts[2]));
            taskIncludeSet.add(taskIds);
            taskOrderMap.put(taskIds, rank);
            rank += 1;
          }
        }
      }
    }
    Properties properties = new Properties();
    try(FileInputStream fis = new FileInputStream(args[0]))
    {
      properties.load(fis);
    }
    String URL = properties.getProperty("URL");
    String user = properties.getProperty("user");
    String password = properties.getProperty("password");
    Connection masterConn = getConnection(URL, user, password);
    if(masterConn != null)
    {
      System.err.println("Connection " + URL + " opened OK");
    }
    else
    {
      System.err.println("Null?");
      System.exit(1);
    }
    ArrayList<DatabasePool> courseDbList = DatabasePool.selectAll(masterConn);
    ArrayList<Connection> courseConns = new ArrayList<>();
    for(DatabasePool dbp : courseDbList)
    {
      Connection conn = getConnection(dbp.getUrl(), dbp.getUsername(), dbp
              .getPassword());
      if(conn != null)
      {
        System.err.println("Connection " + dbp.getUrl() + " opened OK");
        courseConns.add(conn);
      }
    }
    HashMap<Integer, Integer> userIdIndexMap = new HashMap<>();
    ArrayList<Integer> masterUserIds = new ArrayList<>();
    int i = 0;
    try(Statement stat = masterConn.createStatement())
    {
      ResultSet rs = stat.executeQuery("SELECT id FROM useraccount order by id");
      while(rs.next())
      {
        Integer userId = rs.getInt(1);
        masterUserIds.add(userId);
        userIdIndexMap.put(userId, i);
        i += 1;
      }
    }
    final int userCount = masterUserIds.size();
    System.err.println("Usercount: " + userCount);
    HashMap<Integer, String> taskNameMap = new HashMap<>();
    HashMap<IntPair, Integer> userIdMap = new HashMap<>();
    ArrayList<TaskBean> tasks = new ArrayList<>();
    int dbId = 0;
    for(Connection courseConn : courseConns)
    {
      try(Statement stat = courseConn.createStatement())
      {
        ResultSet rs = stat
                .executeQuery("SELECT roottaskid, id, name FROM task");
        while(rs.next())
        {
          Integer rootTaskId = rs.getInt(1);
          Integer taskId = rs.getInt(2);
          String taskName = rs.getString(3);
          if(taskIncludeSet.isEmpty() || taskIncludeSet.contains(new IntPair(
                  dbId, rootTaskId)))
          {
            tasks.add(new TaskBean(dbId, rootTaskId, taskId, taskName));
            taskNameMap.put(taskId, taskName);
          }
        }
      }
      try(Statement stat = courseConn.createStatement())
      {
        ResultSet rs = stat.executeQuery(
                "SELECT masterdbuserid, coursedbuserid FROM useridreplication");
        while(rs.next())
        {
          userIdMap.put(new IntPair(dbId, rs.getInt(2)), rs.getInt(1));
        }
      }
      dbId += 1;
    }
    Collections.sort(tasks, new Comparator<TaskBean>()
    {
      @Override
      public int compare(TaskBean a, TaskBean b)
      {
        int cmp = 0;
        Integer i = taskOrderMap.get(new IntPair(a.dbId, a.rootTaskId));
        Integer j = taskOrderMap.get(new IntPair(b.dbId, b.rootTaskId));
        if((i != null) && (j != null) && !i.equals(j))
        {
          cmp = Integer.compare(i, j);
        }
        else
        {
          cmp = a.compareTo(b);
        }
        return cmp;
      }

    });
    i = 0;
    for(TaskBean ip : tasks)
    {
      System.out.println(i + " " + ip.dbId + " " + ip.rootTaskId + " "
              + ip.taskId + " " + ip.taskName);
      i += 1;
    }
    final int taskCount = tasks.size();
    Float[][][] dataTable = new Float[userCount][][];
    final int gradeCol = 0;
    final int submissionTimeCol = 1;
    final int colCount = 2;
    i = 0;
    for(TaskBean tb : tasks)
    {
      System.err.println("Processing: " + tb.taskName);
      final int taskIndex = i++;
      Connection courseConn = courseConns.get(tb.dbId);
      ArrayList<Grade> grades = Grade.selectByTaskId(courseConn, tb.taskId);
      for(Grade grade : grades)
      {
        if((grade.getMark() != null) && (GradeStatus.AGGREGATE.getValue()
                .equals(grade.getStatus()) || GradeStatus.VALID.getValue()
                .equals(grade.getStatus())))
        {
          Integer userId = grade.getReceiverId();
          Integer masterUserId = userIdMap.get(new IntPair(tb.dbId, userId));
          Integer userIndex = userIdIndexMap.get(masterUserId);
          if(userIndex != null)
          {
            if(dataTable[userIndex] == null)
            {
              dataTable[userIndex] = new Float[taskCount][];
            }
            if(dataTable[userIndex][taskIndex] == null)
            {
              dataTable[userIndex][taskIndex] = new Float[colCount];
              dataTable[userIndex][taskIndex][gradeCol] = 0f;
            }
            dataTable[userIndex][taskIndex][gradeCol] += grade.getMark();
          }
        }
      }
      WetoTimeStamp[] limits = PermissionModel
              .getTaskTimeStampLimits(courseConn, null, null, tb.taskId,
                      PermissionType.SUBMISSION, false);
      if((limits[1] != null) && !limits[1].getTimeStamp().equals(
              WetoTimeStamp.STAMP_MIN))
      {
        limits[1].setHour(0);
        limits[1].setMinute(0);
        limits[1].setSecond(0);
        long deadline = limits[1].toCalendar().getTimeInMillis();
        ArrayList<Submission> submissions = Submission.selectByTaskIdAndStatus(
                courseConn, tb.taskId, SubmissionStatus.ACCEPTED.getValue());
        for(Submission submission : submissions)
        {
          WetoTimeStamp submissionStamp = new WetoTimeStamp(submission
                  .getTimeStamp());
          submissionStamp.setHour(0);
          submissionStamp.setMinute(0);
          submissionStamp.setSecond(0);
          long submissionTime = new WetoTimeStamp(submission.getTimeStamp())
                  .toCalendar().getTimeInMillis();
          long timeSlack = (deadline - submissionTime + 10000000) / (1000 * 3600
                  * 24);
          Integer userId = submission.getUserId();
          Integer masterUserId = userIdMap.get(new IntPair(tb.dbId, userId));
          Integer userIndex = userIdIndexMap.get(masterUserId);
          if(userIndex != null)
          {
            if(dataTable[userIndex] == null)
            {
              dataTable[userIndex] = new Float[taskCount][];
            }
            if(dataTable[userIndex][taskIndex] == null)
            {
              dataTable[userIndex][taskIndex] = new Float[colCount];
            }
            if(dataTable[userIndex][taskIndex][submissionTimeCol] == null)
            {
              dataTable[userIndex][taskIndex][submissionTimeCol]
                      = (float) timeSlack;
            }
          }
        }
      }
    }
    for(int subCol = 0; subCol < colCount; ++subCol)
    {
      try(WetoCsvWriter wcw = new WetoCsvWriter(new File(args[2 + subCol])))
      {
        ArrayList<String> row = new ArrayList<>();
        for(int userRow = 0; userRow < userCount; ++userRow)
        {
          if(dataTable[userRow] != null)
          {
            row.clear();
            for(int taskCol = 0; taskCol < taskCount; ++taskCol)
            {
              String val = " ";
              if((dataTable[userRow][taskCol] != null)
                      && (dataTable[userRow][taskCol][subCol] != null))
              {
                val = dataTable[userRow][taskCol][subCol].toString();
              }
              row.add(val);
            }
            wcw.writeStrings(row);
          }
        }
      }
    }
    for(Connection courseConn : courseConns)
    {
      courseConn.close();
    }
    masterConn.close();
  }

}
