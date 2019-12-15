package fi.uta.cs.weto.actions.grading;

import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.GradingModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoTeacherAction;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ExportGrades extends WetoTeacherAction
{
  private boolean submitted;
  private String delimiter;
  private boolean name;
  private boolean studentNumber;
  private boolean loginName;
  private boolean email;
  private boolean groups;
  private boolean excludeUnspecified;
  private boolean excludeSubtasks;
  private InputStream documentStream;
  private int contentLength;
  final private static String notAvailable = WetoUtilities.getMessageResource(
          "general.header.notAvailable");

  public ExportGrades()
  {
    super(Tab.GRADING.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Integer taskId = getTaskId();
    if(!haveViewRights(Tab.GRADING.getBit(), false, true))
    {
      throw new WetoActionException(getText("general.error.accessDenied"),
              ACCESS_DENIED);
    }
    String result;
    if(!submitted)
    {
      result = INPUT;
    }
    else
    {
      if((delimiter == null) || "".equals(delimiter))
      {
        delimiter = ";";
      }
      ArrayList<Task> tasks = new ArrayList<>();
      tasks.add(getTask());
      if(!excludeSubtasks)
      {
        tasks.addAll(GradingModel.getGradableSubtasks(conn, tasks.get(0)));
      }
      ArrayList<StudentView> students = StudentView.selectByTaskId(conn, taskId);
      ArrayList<ArrayList<Float>> grades;
      if(tasks.size() > 1)
      {
        grades = GradingModel.retrieveAggregateGrades(conn, tasks, students);
      }
      else
      {
        grades = GradingModel.retrieveAllGrades(conn, tasks, students);
      }
      HashMap<Integer, HashMap<Integer, String>> groupMemberMap
                                                         = new HashMap<>();
      ArrayList<Integer> groupTypeList = new ArrayList<>();
      if(groups)
      {
        HashSet<Integer> groupTypeSet = new HashSet<>();
        ArrayList<GroupView> groupMembers = GroupView.selectInheritedByTaskId(
                conn, taskId);
        for(GroupView groupMember : groupMembers)
        {
          groupTypeSet.add(groupMember.getType());
          HashMap<Integer, String> userGroups = groupMemberMap.get(groupMember
                  .getUserId());
          if(userGroups == null)
          {
            userGroups = new HashMap<>();
            groupMemberMap.put(groupMember.getUserId(), userGroups);
          }
          userGroups.put(groupMember.getType(), groupMember.getName());
        }
        groupTypeList.addAll(groupTypeSet);
      }
      String newLine = System.getProperty("line.separator");
      StringBuilder document = new StringBuilder();
      for(int i = 0; i < students.size(); ++i)
      {
        StudentView student = students.get(i);
        ArrayList<Float> studentGrades = grades.get(i);
        if(!excludeUnspecified || (!studentGrades.isEmpty() && studentGrades
                .get(0) != null))
        {
          boolean first = true;
          if(name)
          {
            if(!first)
            {
              document.append(delimiter);
            }
            document.append(convertStr(student.getLastName()) + ", "
                    + convertStr(student.getFirstName()));
            first = false;
          }
          if(studentNumber)
          {
            if(!first)
            {
              document.append(delimiter);
            }
            document.append(convertStr(student.getStudentNumber()));
            first = false;
          }
          if(loginName)
          {
            if(!first)
            {
              document.append(delimiter);
            }
            document.append(convertStr(student.getLoginName()));
            first = false;
          }
          if(email)
          {
            if(!first)
            {
              document.append(delimiter);
            }
            document.append(convertStr(student.getEmail()));
            first = false;
          }
          if(groups)
          {
            for(Integer groupType : groupTypeList)
            {
              if(!first)
              {
                document.append(delimiter);
              }
              if(groupMemberMap.containsKey(student.getUserId()))
              {
                document.append(convertStr(groupMemberMap
                        .get(student.getUserId()).get(groupType)));
              }
              first = false;
            }
          }
          for(Float mark : studentGrades)
          {
            if(!first)
            {
              document.append(delimiter);
            }
            document.append(convertStr(mark));
            first = false;
          }
          document.append(newLine);
        }
      }
      byte[] documentBytes = document.toString().getBytes("UTF-8");
      documentStream = new ByteArrayInputStream(documentBytes);
      contentLength = documentBytes.length;
      result = SUCCESS;
    }
    return result;
  }

  private String convertStr(Object o)
  {
    String string = (o != null) ? o.toString().trim() : "";
    return ((string != null) && !string.isEmpty()) ? string : notAvailable;
  }

  public void setSubmitted(boolean submitted)
  {
    this.submitted = submitted;
  }

  public void setDelimiter(String delimiter)
  {
    this.delimiter = delimiter;
  }

  public void setName(boolean name)
  {
    this.name = name;
  }

  public void setStudentNumber(boolean studentNumber)
  {
    this.studentNumber = studentNumber;
  }

  public void setLoginName(boolean loginName)
  {
    this.loginName = loginName;
  }

  public void setEmail(boolean email)
  {
    this.email = email;
  }

  public void setGroups(boolean groups)
  {
    this.groups = groups;
  }

  public void setExcludeUnspecified(boolean excludeUnspecified)
  {
    this.excludeUnspecified = excludeUnspecified;
  }

  public void setExcludeSubtasks(boolean excludeSubtasks)
  {
    this.excludeSubtasks = excludeSubtasks;
  }

  public InputStream getDocumentStream()
  {
    return documentStream;
  }

  public String getContentType()
  {
    return "text/plain";
  }

  public String getContentCharSet()
  {
    return "UTF-8";
  }

  public String getContentDisposition()
  {
    return "inline;filename=\"exported_grades.txt\"";
  }

  public int getContentLength()
  {
    return contentLength;
  }

}
