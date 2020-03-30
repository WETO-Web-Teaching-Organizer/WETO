package fi.uta.cs.weto.model;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Grade;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.Log;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.Submission;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.TaskDocument;
import fi.uta.cs.weto.db.UserGroup;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.struts2.ServletActionContext;

public class TaskModel
{
  public static final String INLINE_ELEMENT_PREFIX = "<div data-weto=";
  public static final String INLINE_ELEMENT_SUFFIX = "</div>";
  public static final String INLINE_ELEMENT_PATTERN = INLINE_ELEMENT_PREFIX
          + "\"type=(\\d+) taskId=(\\d+) refId=(\\d+)\">([^<]*)"
          + INLINE_ELEMENT_SUFFIX;
  public static final Pattern inlineElementPattern = Pattern.compile(
          INLINE_ELEMENT_PATTERN);

  public static Integer deleteCourseDbTask(Connection courseConn,
          Connection masterConn, Integer taskId)
          throws SQLException, TooManyItemsException, WetoActionException,
                 InvalidValueException, ObjectNotValidException,
                 NoSuchItemException,
                 IOException, WetoTimeStampException
  {
    Integer containerId = null;
    // Delete subtask link.
    try
    {
      SubtaskLink link = SubtaskLink.select1BySubtaskId(courseConn, taskId);
      containerId = link.getContainerId();
      link.delete(courseConn);
    }
    catch(NoSuchItemException e)
    {
    }
    // Delete scoring rules, submissions, submission propertiess, grades,
    // gradetable, permissions, tags, groups and task related general
    // properties. Then finally delete the task itself.
    try
    {
      Scoring.select1ByTaskId(courseConn, taskId).delete(courseConn);
    }
    catch(NoSuchItemException e)
    {
    }
    try
    {
      AutoGrading.deleteByTaskId(courseConn, taskId);
    }
    catch(NoSuchItemException e)
    {
    }
    for(Submission submission : Submission.selectByTaskId(courseConn, taskId))
    {
      SubmissionModel.deleteSubmission(courseConn, submission);
    }
    try
    {
      SubmissionProperties.select1ByTaskId(courseConn, taskId)
              .delete(courseConn);
    }
    catch(NoSuchItemException e)
    {
    }
    for(Grade grade : Grade.selectByTaskId(courseConn, taskId))
    {
      GradingModel.deleteGrade(courseConn, grade);
    }
    PermissionModel.deleteAllCoursePermissions(courseConn, masterConn, taskId);
    Tag
            .deleteByTaggedIdAndType(courseConn, taskId, TagType.CSS_STYLE
                    .getValue());
    Tag.deleteByTaggedIdAndType(courseConn, taskId, TagType.FORUM_TOPIC
            .getValue());
    Tag.deleteByTaggedIdAndType(courseConn, taskId, TagType.COMPILER_RESULT
            .getValue());
    Tag.deleteByTaggedIdAndType(courseConn, taskId, TagType.QUIZ_QUESTION
            .getValue());
    Tag.deleteByTaggedIdAndType(courseConn, taskId, TagType.QUIZ_ANSWER
            .getValue());
    Tag.deleteByTaggedIdAndType(courseConn, taskId, TagType.REVIEW_INSTRUCTIONS
            .getValue());
    deleteTaskGroups(courseConn, taskId);
    for(Property property : PropertyModel.getPendingStudents(courseConn, taskId))
    {
      property.delete(courseConn);
    }
    try
    {
      PropertyModel.getNavigationTreeUpdate(courseConn, taskId).delete(
              courseConn);
    }
    catch(NoSuchItemException e)
    {
    }
    for(TaskDocument link : TaskDocument.selectByTaskId(courseConn, taskId))
    {
      Integer documentId = link.getDocumentId();
      link.delete(courseConn);
      DocumentModel.deleteDocument(courseConn, Document.select1ById(courseConn,
              documentId));
    }
    Log.deleteByTaskId(courseConn, taskId);
    Task.select1ById(courseConn, taskId).delete(courseConn);
    return containerId;
  }

  public static void deleteGroup(Connection conn, UserGroup group)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 TooManyItemsException, ObjectNotValidException
  {
    // Delete first group members and then the group itself
    ArrayList<GroupMember> groupMembers = GroupMember.selectByTaskIdAndGroupId(
            conn, group.getTaskId(), group.getId());
    for(GroupMember member : groupMembers)
    {
      member.delete(conn);
    }
    group.delete(conn);
  }

  public static void deleteTaskGroups(Connection conn, Integer taskId)
          throws SQLException, InvalidValueException, NoSuchItemException,
                 TooManyItemsException, ObjectNotValidException
  {
    ArrayList<UserGroup> groups = UserGroup.selectByTaskId(conn, taskId);
    for(UserGroup group : groups)
    {
      deleteGroup(conn, group);
    }
  }

  private static final String DOWNLOAD_DOCUMENT_PREFIX
                                      = "downloadTaskDocument\\.action\\?";
  private static final String DOWNLOAD_DOCUMENT_PATTERN
                                      = "downloadTaskDocument\\.action\\?(\\&(amp;)?)?taskId=\\d+\\&(amp;)?tabId=\\d+\\&(amp;)?dbId=\\d+\\&(amp;)?documentId=(\\d+)";

  private static final Pattern downloadDocumentPattern;

  static
  {
    downloadDocumentPattern = Pattern.compile(DOWNLOAD_DOCUMENT_PATTERN);
  }

  public static String migrateStringDocumentIds(String text, Integer taskId,
          Integer dbId, HashMap<Integer, Integer[]> idMap)
  {
    String res = null;
    Matcher m = downloadDocumentPattern.matcher(text);
    String replacementStart = DOWNLOAD_DOCUMENT_PREFIX + "&amp;taskId=";
    String replacementCont = "&amp;tabId=" + Tab.MAIN.getValue() + "&amp;dbId="
            + dbId + "&amp;documentId=";
    StringBuffer sb = new StringBuffer();
    boolean found = false;
    while(m.find())
    {
      Integer oldId = Integer.parseInt(m.group(m.groupCount()));
      Integer[] newIds = idMap.get(oldId);
      if(newIds == null)
      {
        newIds = new Integer[]
        {
          oldId, taskId
        };
      }
      else
      {
        found = true;
      }
      m.appendReplacement(sb, replacementStart + newIds[1] + replacementCont
              + newIds[0]);
    }
    m.appendTail(sb);
    if(found)
    {
      res = sb.toString();
    }
    return res;
  }

  public static void migrateTaskDocumentIds(Connection conn, Task task,
          Integer dbId, HashMap<Integer, Integer[]> idMap)
          throws InvalidValueException, SQLException, ObjectNotValidException,
                 NoSuchItemException
  {
    String migratedText = migrateStringDocumentIds(task.getText(), task.getId(),
            dbId, idMap);
    if(migratedText != null)
    {
      task.setText(migratedText);
      task.update(conn);
    }
  }

  static final Pattern linkPattern = Pattern.compile(
          "(src|href)=\"\\S+taskId=(\\d+)\\S*\"");
  static final Pattern taskIdPattern = Pattern.compile("taskId=(\\d+)");
  static final Pattern docIdPattern = Pattern.compile("documentId=(\\d+)");

  public static void remapLinkTaskAndDocumentIds(Connection conn,
          Map<Integer, Integer> taskIdMap,
          Map<Integer, Integer[]> taskDocumentIdMap)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException
  {
    for(Map.Entry<Integer, Integer> e : taskIdMap.entrySet())
    {
      Task workTask = Task.select1ById(conn, e.getValue());
      String origTaskText = workTask.getText();
      StringBuilder taskTextSB = new StringBuilder();
      Matcher linkMatcher = linkPattern.matcher(origTaskText);
      int copiedUntil = 0;
      while(linkMatcher.find())
      {
        taskTextSB.append(origTaskText.substring(copiedUntil, linkMatcher
                .start()));
        copiedUntil = linkMatcher.end();
        String origTaskIdStr = linkMatcher.group(2);
        Integer origTaskId = Integer.parseInt(origTaskIdStr);
        String linkStr = linkMatcher.group();
        if(taskIdMap.containsKey(origTaskId))
        {
          Integer newTaskId = taskIdMap.get(origTaskId);
          Matcher taskIdMatcher = taskIdPattern.matcher(linkStr);
          linkStr = taskIdMatcher.replaceAll("taskId=" + newTaskId);
          Matcher docIdMatcher = docIdPattern.matcher(linkStr);
          if(docIdMatcher.find())
          {
            String origDocIdStr = docIdMatcher.group(1);
            Integer origDocId = Integer.parseInt(origDocIdStr);
            if(taskDocumentIdMap.containsKey(origDocId))
            {
              Integer newDocId = taskDocumentIdMap.get(origDocId)[0];
              docIdMatcher.reset();
              linkStr = docIdMatcher.replaceAll("documentId=" + newDocId);
            }
          }
        }
        taskTextSB.append(linkStr);
      }
      taskTextSB.append(origTaskText.substring(copiedUntil));
      workTask.setText(taskTextSB.toString());
      workTask.update(conn);
    }
  }

  public static void migrateInlineQuestionIds(Connection courseConn,
          Integer origTaskId, Task copyTask,
          HashMap<Integer, Integer> quizQuestionMap) throws
          InvalidValueException, SQLException, ObjectNotValidException,
          NoSuchItemException
  {
    String html = copyTask.getText();
    if(html != null)
    {
      HashSet<Integer> inlineQuestionTypes = new HashSet<>();
      inlineQuestionTypes.add(ContentElementType.ESSAY.getValue());
      inlineQuestionTypes.add(ContentElementType.MULTIPLE_CHOICE.getValue());
      inlineQuestionTypes.add(ContentElementType.PROGRAM.getValue());
      inlineQuestionTypes.add(ContentElementType.SURVEY.getValue());
      StringBuilder updatedHtml = new StringBuilder();
      Matcher m = inlineElementPattern.matcher(html);
      boolean hasInline = false;
      int i = 0;
      while(m.find())
      {
        hasInline = true;
        String precedingHtml = html.substring(i, m.start()).trim();
        if(!precedingHtml.isEmpty())
        {
          updatedHtml.append(precedingHtml);
        }
        i = m.end();
        Integer questionType = Integer.parseInt(m.group(1));
        Integer questionTaskId = Integer.parseInt(m.group(2));
        Integer questionId = Integer.parseInt(m.group(3));
        if(inlineQuestionTypes.contains(questionType) && origTaskId.equals(
                questionTaskId) && quizQuestionMap.containsKey(questionId))
        {
          updatedHtml.append(INLINE_ELEMENT_PREFIX + "\"type=" + questionType
                  + " taskId=" + copyTask.getId() + " refId=" + quizQuestionMap
                  .get(questionId) + "\">" + m.group(4) + INLINE_ELEMENT_SUFFIX);
        }
        else
        {
          updatedHtml.append(m.group());
        }
      }
      if(i < html.length())
      {
        updatedHtml.append(html.substring(i));
      }
      if(!hasInline)
      {
        copyTask.setNoInline(true);
      }
      else
      {
        copyTask.setText(updatedHtml.toString());
      }
      copyTask.update(courseConn, false);
    }
  }

  public static String createCourseCssFilename(Integer dbId,
          Integer courseTaskId)
  {
    return "course" + dbId + "_" + courseTaskId + ".css";
  }

  public static File createCourseCssFilePath(String cssFilename)
  {
    return new File(ServletActionContext.getServletContext().getRealPath("/css/"
            + cssFilename));
  }

  public static void selectRandomTasks(Connection masterConn,
          Connection courseConn, Integer dbId, Integer courseTaskId,
          Integer userId)
          throws SQLException, WetoTimeStampException, NoSuchItemException,
                 InvalidValueException, ObjectNotValidException
  {
    Permission regPermission = null;
    try
    {
      regPermission = Permission.select1ByTaskIdAndUserIdAndType(courseConn,
              courseTaskId, userId, PermissionType.REGISTER.getValue());
    }
    catch(NoSuchItemException e)
    {
      try
      {
        regPermission = Permission.select1ByTaskIdAndUserIdAndType(courseConn,
                courseTaskId, null, PermissionType.REGISTER.getValue());
      }
      catch(NoSuchItemException e2)
      {
      }
    }
    Calendar now = new GregorianCalendar();
    Integer startTimeStamp = new WetoTimeStamp(now).getTimeStamp();
    Integer endTimeStamp = null;
    final int extraMinutes = 15;
    Integer extraEndTimeStamp = null;
    String conditionString = null;
    if((regPermission != null) && (regPermission.getDetail() != null))
    {
      conditionString = regPermission.getDetail();
      String[] conditions = conditionString.split(PermissionModel.COND_SEP);
      for(String condition : conditions)
      {
        String[] keyVal = condition.split("=");
        if(PermissionModel.DURATION.equals(keyVal[0].toLowerCase()))
        {
          try
          {
            Integer minutes = Integer.parseInt(keyVal[1]);
            now.add(Calendar.MINUTE, minutes);
            endTimeStamp = new WetoTimeStamp(now).getTimeStamp();
            now.add(Calendar.MINUTE, extraMinutes);
            extraEndTimeStamp = new WetoTimeStamp(now).getTimeStamp();
          }
          catch(NumberFormatException e)
          {
          }
          break;
        }
      }
    }
    // If registration had an expiration value, limit course view rights.
    if(endTimeStamp != null)
    {
      final Integer[] coursePermissionTypes =
      {
        PermissionType.VIEW.getValue(), PermissionType.SUBMISSION.getValue(),
        PermissionType.RESULTS.getValue()
      };
      for(Integer permissionType : coursePermissionTypes)
      {
        Permission permission = null;
        boolean doUpdate = false;
        try
        {
          permission = Permission.select1ByTaskIdAndUserIdAndType(courseConn,
                  courseTaskId, userId, permissionType);
          doUpdate = true;
        }
        catch(NoSuchItemException e)
        {
          permission = new Permission();
          permission.setTaskId(courseTaskId);
          permission.setType(permissionType);
          permission.setUserRefType(PermissionRefType.USER.getValue());
          permission.setUserRefId(userId);
        }
        permission.setStartDate(startTimeStamp);
        // Submission limit is strict; the rest have some extra slack.
        if(permissionType.equals(PermissionType.SUBMISSION.getValue()))
        {
          permission.setEndDate(endTimeStamp);
        }
        else
        {
          permission.setEndDate(extraEndTimeStamp);
        }
        permission.setDetail(conditionString);
        if(doUpdate)
        {
          permission.update(courseConn);
        }
        else
        {
          permission.insert(courseConn);
        }
        if(permissionType.equals(PermissionType.VIEW.getValue()))
        {
          // Reflect course root view permission to master database.
          PermissionModel
                  .replicateRootPermission(masterConn, courseConn, userId,
                          permission);
        }
      }
    }
    Stack<Task> taskStack = new Stack<>();
    taskStack.push(Task.select1ById(courseConn, courseTaskId));
    final Integer[] taskPermissionTypes =
    {
      PermissionType.VIEW.getValue()
    };
    while(!taskStack.empty())
    {
      Task parent = taskStack.pop();
      ArrayList<Task> children = Task.selectSubtasks(courseConn, parent.getId());
      HashMap<String, ArrayList<Task>> taskGroups = new HashMap<>();
      for(Task child : children)
      {
        if(child.getIsRandomTask())
        {
          String token = child.getName().split("\\s", 2)[0];
          ArrayList<Task> group = taskGroups.get(token);
          if(group == null)
          {
            group = new ArrayList<>();
            taskGroups.put(token, group);
          }
          group.add(child);
        }
        taskStack.push(child);
      }
      for(ArrayList<Task> group : taskGroups.values())
      {
        Integer selectedId = group.get(new Random().nextInt(group.size()))
                .getId();
        for(Integer permissionType : taskPermissionTypes)
        {
          Permission permission = null;
          boolean doUpdate = false;
          try
          {
            permission = Permission.select1ByTaskIdAndUserIdAndType(courseConn,
                    selectedId, userId, permissionType);
            doUpdate = true;
          }
          catch(NoSuchItemException e)
          {
            permission = new Permission();
            permission.setTaskId(selectedId);
            permission.setType(permissionType);
            permission.setUserRefType(PermissionRefType.USER.getValue());
            permission.setUserRefId(userId);
          }
          // Set only start time: course-level permissions limit the end time.
          permission.setStartDate(startTimeStamp);
          if(doUpdate)
          {
            permission.update(courseConn);
          }
          else
          {
            permission.insert(courseConn);
          }
        }
      }
    }
  }

}
