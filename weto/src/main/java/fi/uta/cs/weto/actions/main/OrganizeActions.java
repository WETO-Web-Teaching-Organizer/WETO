package fi.uta.cs.weto.actions.main;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.actions.grading.ReviewInstructionsActions;
import fi.uta.cs.weto.db.AutoGrading;
import fi.uta.cs.weto.db.CourseView;
import fi.uta.cs.weto.db.Document;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.Scoring;
import fi.uta.cs.weto.db.SubmissionProperties;
import fi.uta.cs.weto.db.SubtaskLink;
import fi.uta.cs.weto.db.SubtaskView;
import fi.uta.cs.weto.db.Tag;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.TaskDocument;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.DocumentModel;
import fi.uta.cs.weto.model.InstructionBean;
import fi.uta.cs.weto.model.QuizModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TagType;
import fi.uta.cs.weto.model.TaskModel;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.lang.StringEscapeUtils;

public class OrganizeActions
{
  public static class OrganizeSubtasks extends WetoTeacherAction
  {
    private final ArrayList<String> courseDbId_TaskIds;
    private final ArrayList<String> courseNames;
    private String otherCourseDbId_TaskId;
    private String thisCourseTree;
    private String otherCourseTree;
    private String otherCourseName;

    public OrganizeSubtasks()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
      courseDbId_TaskIds = new ArrayList<>();
      courseNames = new ArrayList<>();
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer taskId = getTaskId();
      Integer dbId = getDbId();
      Connection masterConn = getMasterConnection();
      HashSet<Integer> courseIds = new HashSet<>();
      for(UserTaskView userCourse : UserTaskView.selectByUserIdAndClusterType(
              masterConn, getMasterUserId(), ClusterType.TEACHERS.getValue()))
      {
        courseIds.add(userCourse.getTaskId());
      }
      for(CourseView course : CourseView.selectAll(masterConn))
      {
        if(courseIds.contains(course.getMasterTaskId()))
        {
          courseDbId_TaskIds.add(course.getDatabaseId() + "_" + course
                  .getCourseTaskId());
          courseNames.add(course.getName());
        }
      }
      StringBuilder thisCourseTreeSB = new StringBuilder();
      buildCourseTree(courseConn, dbId, SubtaskView.selectByContainerId(
              courseConn, taskId), thisCourseTreeSB);
      thisCourseTree = thisCourseTreeSB.toString();
      if(otherCourseDbId_TaskId == null)
      {
        otherCourseDbId_TaskId = dbId + "_" + getCourseTaskId();
      }
      String[] ids = otherCourseDbId_TaskId.split("_", 2);
      Integer otherCourseDbId = Integer.valueOf(ids[0]);
      Integer otherCourseTaskId = Integer.valueOf(ids[1]);
      Connection otherConn;
      if(!otherCourseDbId.equals(dbId))
      {
        otherConn = getDbSession().getConnection(getNavigator().getDatabase(
                otherCourseDbId));
      }
      else
      {
        otherConn = getCourseConnection();
      }
      Task otherCourse = Task.select1ById(otherConn, otherCourseTaskId);
      otherCourseName = otherCourse.getName();
      ArrayList<SubtaskView> otherCourseRoot = new ArrayList<>();
      SubtaskView rootTaskView = new SubtaskView();
      rootTaskView.setName(otherCourseName);
      rootTaskView.setId(otherCourseTaskId);
      otherCourseRoot.add(rootTaskView);
      StringBuilder otherCourseTreeSB = new StringBuilder();
      buildCourseTree(otherConn, otherCourseDbId, otherCourseRoot,
              otherCourseTreeSB);
      otherCourseTree = otherCourseTreeSB.toString();
      return SUCCESS;
    }

    public ArrayList<String> getCourseDbId_TaskIds()
    {
      return courseDbId_TaskIds;
    }

    public ArrayList<String> getCourseNames()
    {
      return courseNames;
    }

    public void setOtherCourseDbId_TaskId(String otherCourseDbId_TaskId)
    {
      this.otherCourseDbId_TaskId = otherCourseDbId_TaskId;
    }

    public String getOtherCourseDbId_TaskId()
    {
      return otherCourseDbId_TaskId;
    }

    public String getThisCourseTree()
    {
      return thisCourseTree;
    }

    public String getOtherCourseTree()
    {
      return otherCourseTree;
    }

    public String getOtherCourseName()
    {
      return otherCourseName;
    }

  }

  public static class OtherCourseTree extends WetoTeacherAction
  {
    private String otherCourseDbId_TaskId;
    private InputStream treeStream;

    public OtherCourseTree()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Integer dbId = getDbId();
      if(otherCourseDbId_TaskId == null)
      {
        otherCourseDbId_TaskId = dbId + "_" + getCourseTaskId();
      }
      String[] ids = otherCourseDbId_TaskId.split("_", 2);
      Integer otherCourseDbId = Integer.valueOf(ids[0]);
      Integer otherCourseTaskId = Integer.valueOf(ids[1]);
      Connection otherConn;
      if(!otherCourseDbId.equals(dbId))
      {
        otherConn = getDbSession().getConnection(getNavigator().getDatabase(
                otherCourseDbId));
      }
      else
      {
        otherConn = getCourseConnection();
      }
      Task otherCourse = Task.select1ById(otherConn, otherCourseTaskId);
      ArrayList<SubtaskView> otherCourseRoot = new ArrayList<>();
      SubtaskView rootTaskView = new SubtaskView();
      rootTaskView.setName(otherCourse.getName());
      rootTaskView.setId(otherCourse.getId());
      otherCourseRoot.add(rootTaskView);
      StringBuilder otherCourseTreeSB = new StringBuilder();
      buildCourseTree(otherConn, otherCourseDbId, otherCourseRoot,
              otherCourseTreeSB);
      treeStream = new ByteArrayInputStream(otherCourseTreeSB.toString()
              .getBytes("UTF-8"));
      return SUCCESS;
    }

    public void setOtherCourseDbId_TaskId(String otherCourseDbId_TaskId)
    {
      this.otherCourseDbId_TaskId = otherCourseDbId_TaskId;
    }

    public InputStream getTreeStream()
    {
      return treeStream;
    }

  }

  private static void buildCourseTree(Connection courseConnection,
          final int dbId, ArrayList<SubtaskView> tasks, StringBuilder tree)
          throws SQLException
  {
    tree.append("<ul>");
    for(SubtaskView task : tasks)
    {
      ArrayList<SubtaskView> subtasks = SubtaskView.selectByContainerId(
              courseConnection, task.getId());
      if(!subtasks.isEmpty())
      {
        tree.append("<li><span id=\"" + dbId + "_" + task.getId() + "\">"
                + task.getName() + "</span>");
        buildCourseTree(courseConnection, dbId, subtasks, tree);
        tree.append("</li>");
      }
      else
      {
        tree.append("<li><span id=\"" + dbId + "_" + task.getId() + "\">"
                + task.getName() + "</span></li>");
      }
    }
    tree.append("</ul>");
  }

  public static class ConfirmSubtasks extends WetoTeacherAction
  {
    private String subtaskJSON;
    private StringBuilder organizedSubtaskTree;

    public ConfirmSubtasks()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      organizedSubtaskTree = new StringBuilder();
      Integer dbId = getDbId();
      ArrayDeque<Integer> rankStack = new ArrayDeque<>();
      String key = null;
      JsonReader reader = new JsonReader(new StringReader(subtaskJSON));
      for(JsonToken event = reader.peek(); !event.equals(
          JsonToken.END_DOCUMENT); event = reader.peek())
      {
        if(event.equals(JsonToken.BEGIN_OBJECT))
        {
          reader.beginObject();
        }
        else if(event.equals(JsonToken.END_OBJECT))
        {
          reader.endObject();
          if(!rankStack.isEmpty())
          {
            organizedSubtaskTree.append("</li>");
          }
        }
        else if(event.equals(JsonToken.BEGIN_ARRAY))
        {
          reader.beginArray();
          if(!rankStack.isEmpty())
          {
            organizedSubtaskTree.append("<ul>");
          }
          rankStack.push(-1);
        }
        else if(event.equals(JsonToken.END_ARRAY))
        {
          reader.endArray();
          if(!rankStack.isEmpty())
          {
            organizedSubtaskTree.append("</ul>");
          }
          rankStack.pop();
        }
        else if(event.equals(JsonToken.NAME))
        {
          key = reader.nextName();
        }
        else if("id".equals(key) && event.equals(JsonToken.STRING))
        {
          String[] taskIdAndName = reader.nextString().split(":", 2);
          String[] idInformation = taskIdAndName[0].split("_", 4);
          if(idInformation.length == 2) // dbId_taskId
          {
            Integer linkDbId = Integer.valueOf(idInformation[0]);
            Integer taskId = Integer.valueOf(idInformation[1]);
            organizedSubtaskTree.append("<li>").append(
                    "<span class=\"copyItem\">").append(
                            "<a href=\"viewTask.action?taskId=")
                    .append(taskId).append("&amp;tabId=0&amp;dbId=").append(
                    linkDbId).append("\">").append(taskIdAndName[1])
                    .append("</a>").append("</span>");
          }
          else if(idInformation.length == 4) // parentId_rank_dbId_taskId
          {
            Integer linkDbId = dbId;
            Integer parentId = Integer.valueOf(idInformation[0]);
            Integer rank = Integer.valueOf(idInformation[1]);
            Integer taskId = Integer.valueOf(idInformation[3]);
            Integer newRank = rankStack.pop() + 1;
            rankStack.push(newRank);
            if(rank != newRank)
            {
              organizedSubtaskTree.append("<li>").append(
                      "<span class=\"moveItem\">").append(
                              "<a href=\"viewTask.action?taskId=").append(
                              taskId).append("&amp;tabId=0&amp;dbId=").append(
                      linkDbId).append("\">").append(taskIdAndName[1])
                      .append("</a>").append("</span>").append(" [").append(
                      parentId).append(":").append(rankStack.peek())
                      .append(" (").append(rank).append(")]");
            }
            else
            {
              organizedSubtaskTree.append("<li>").append(
                      "<a href=\"viewTask.action?taskId=").append(taskId)
                      .append("&amp;tabId=0&amp;dbId=").append(linkDbId)
                      .append("\">").append(taskIdAndName[1]).append("</a>")
                      .append(" [").append(parentId).append(":").append(
                      rankStack.peek()).append("]");
            }
          }
        }
        else
        {
          reader.skipValue();
        }
      }
      return SUCCESS;
    }

    public String getSubtaskJSON()
    {
      return StringEscapeUtils.escapeXml(subtaskJSON);
    }

    public void setSubtaskJSON(String subtaskJSON)
    {
      this.subtaskJSON = subtaskJSON;
    }

    public String getOrganizedSubtaskTree()
    {
      return organizedSubtaskTree.toString();
    }

  }

  public static class CommitSubtasks extends WetoTeacherAction
  {

    private String subtaskJSON;

    public CommitSubtasks()
    {
      super(Tab.MAIN.getBit(), Tab.MAIN.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Integer masterUserId = getMasterUserId();
      Connection courseConn = getCourseConnection();
      Integer dbId = getDbId();
      Integer courseTaskId = getCourseTaskId();
      ArrayDeque<Integer> parentStack = new ArrayDeque<>();
      Integer currId = getTaskId();
      ArrayDeque<Integer> rankStack = new ArrayDeque<>();
      String key = null;
      JsonReader reader = new JsonReader(new StringReader(subtaskJSON));
      for(JsonToken event = reader.peek(); !event.equals(JsonToken.END_DOCUMENT);
          event = reader.peek())
      {
        if(event.equals(JsonToken.BEGIN_OBJECT))
        {
          reader.beginObject();
        }
        else if(event.equals(JsonToken.END_OBJECT))
        {
          reader.endObject();
        }
        else if(event.equals(JsonToken.BEGIN_ARRAY))
        {
          reader.beginArray();
          parentStack.push(currId);
          rankStack.push(-1);
        }
        else if(event.equals(JsonToken.END_ARRAY))
        {
          reader.endArray();
          currId = parentStack.pop();
          rankStack.pop();
        }
        else if(event.equals(JsonToken.NAME))
        {
          key = reader.nextName();
        }
        else if("id".equals(key) && event.equals(JsonToken.STRING))
        {
          String[] taskIdAndName = reader.nextString().split(":", 2);
          String[] idInformation = taskIdAndName[0].split("_", 4);
          Integer parentId = parentStack.peek();
          if(idInformation.length == 2) // dbId_taskId  (copy task)
          {
            Integer linkDbId = Integer.valueOf(idInformation[0]);
            Integer taskId = Integer.valueOf(idInformation[1]);
            Integer newRank = rankStack.pop() + 1;
            rankStack.push(newRank);
            Connection copyConn;
            if(!linkDbId.equals(dbId))
            {
              copyConn = getDbSession().getConnection(getNavigator()
                      .getDatabase(linkDbId));
            }
            else
            {
              copyConn = courseConn;
            }
            // Copy the task and link it to parent task
            Task copyTask = Task.select1ById(copyConn, taskId);
            // But first verify that the user is a teacher of the copytask.
            UserTaskView.select1ByTaskIdAndUserIdAndClusterType(copyConn,
                    copyTask.getRootTaskId(), UserIdReplication
                    .select1ByMasterDbUserId(copyConn, masterUserId)
                    .getCourseDbUserId(), ClusterType.TEACHERS.getValue());
            copyTask.setRootTaskId(courseTaskId);
            copyTask.insert(courseConn);
            currId = copyTask.getId();
            SubtaskLink link = new SubtaskLink();
            link.setContainerId(parentId);
            link.setSubtaskId(currId);
            link.setRank(newRank);
            link.insert(courseConn);
            // Copy other task-related information (scoring rules etc.)
            try
            {
              Scoring scoring = Scoring.select1ByTaskId(copyConn, taskId);
              scoring.setTaskId(currId);
              scoring.insert(courseConn);
            }
            catch(NoSuchItemException e)
            {
            }
            try
            {
              SubmissionProperties properties = SubmissionProperties
                      .select1ByTaskId(copyConn, taskId);
              properties.setTaskId(currId);
              properties.insert(courseConn);
            }
            catch(NoSuchItemException e)
            {
            }
            for(UserGroup group : UserGroup.selectByTaskId(copyConn, taskId))
            {
              group.setTaskId(currId);
              group.insert(courseConn);
            }
            for(Permission perm : Permission.selectByTaskIdAndUserId(
                    copyConn, taskId, null))
            {
              perm.setTaskId(currId);
              perm.insert(courseConn);
            }
            HashMap<Integer, Integer[]> taskDocMap = new HashMap<>();
            for(TaskDocument taskDoc : TaskDocument.selectByTaskId(copyConn,
                    taskId))
            {
              Integer originalId = taskDoc.getDocumentId();
              Document document = Document.select1ById(copyConn, originalId);
              File tmpFile = Files.createTempFile("weto", ".tmp").toFile();
              DocumentModel.loadDocument(copyConn, document, tmpFile);
              Document documentCopy = DocumentModel.storeDocument(courseConn,
                      tmpFile, document.getFileName());
              taskDoc.setTaskId(currId);
              Integer newId = documentCopy.getId();
              taskDoc.setDocumentId(newId);
              taskDoc.insert(courseConn);
              taskDocMap.put(originalId, new Integer[]
              {
                newId, currId
              });
            }
            HashMap<Integer, Integer> quizQuestionMap = new HashMap<>();
            for(TagType tagType : TagType.values())
            {
              if(tagType.isCopyWithTask())
              {
                for(Tag tag : Tag.selectByTaggedIdAndType(copyConn, taskId,
                        tagType.getValue()))
                {
                  Integer origTagId = tag.getId();
                  tag.setTaggedId(currId);
                  if(TagType.REVIEW_INSTRUCTIONS.getValue().equals(tagType
                          .getValue()))
                  {
                    InstructionBean instruction = ReviewInstructionsActions
                            .instructionTagToBean(tag);
                    String newText = TaskModel.migrateStringDocumentIds(
                            instruction.getText(), currId, getDbId(), taskDocMap);
                    if(newText != null)
                    {
                      instruction.setText(newText);
                      tag.setText(ReviewInstructionsActions.buildXML(instruction
                              .getName(), instruction.getText(), instruction
                              .getMinPoints(), instruction.getMaxPoints()));
                    }
                  }
                  tag.insert(courseConn);
                  if(TagType.QUIZ_QUESTION.getValue().equals(tagType.getValue()))
                  {
                    quizQuestionMap.put(origTagId, tag.getId());
                  }
                }
              }
            }
            if(!quizQuestionMap.isEmpty())
            {
              TaskModel.migrateInlineQuestionIds(courseConn, taskId, copyTask,
                      quizQuestionMap);
            }
            if(!taskDocMap.isEmpty())
            {
              TaskModel.migrateTaskDocumentIds(courseConn, copyTask, dbId,
                      taskDocMap);
              if(!quizQuestionMap.isEmpty())
              {
                QuizModel quiz = new QuizModel(courseConn, currId, null);
                quiz.readQuestions();
                quiz.migrateQuestionDocumentIds(dbId, taskDocMap);
              }
            }
            try
            {
              AutoGrading ag = AutoGrading.select1ByTaskId(copyConn, taskId);
              ag.setTaskId(currId);
              File tmpFile = null;
              try
              {
                Document testDoc = Document.select1ById(copyConn, ag
                        .getTestDocId());
                tmpFile = Files.createTempFile("weto", ".zip").toFile();
                DocumentModel.loadDocument(courseConn, testDoc, tmpFile);
                Document testDocCopy = DocumentModel.storeDocument(courseConn,
                        tmpFile, "autograding.zip");
                ag.setTestDocId(testDocCopy.getId());
              }
              catch(NoSuchItemException e)
              {
              }
              finally
              {
                if(tmpFile != null)
                {
                  tmpFile.delete();
                }
              }
              ag.insert(courseConn);
            }
            catch(NoSuchItemException e)
            {
            }
          }
          else if(idInformation.length == 4) // parentId_rank_dbId_taskId (move task)
          {
            Integer taskId = currId = Integer.valueOf(idInformation[3]);
            Integer newRank = rankStack.pop() + 1;
            rankStack.push(newRank);
            SubtaskLink link = SubtaskLink.select1BySubtaskId(courseConn,
                    taskId);
            if(!link.getContainerId().equals(parentId) || !link.getRank()
                    .equals(newRank))
            {
              link.setContainerId(parentId);
              link.setRank(newRank);
              link.update(courseConn);
            }
          }
        }
        else
        {
          reader.skipValue();
        }
      }
      refreshNavigationTree();
      addActionMessage(getText("organize.message.success"));
      return SUCCESS;
    }

    public void setSubtaskJSON(String subtaskJSON)
    {
      this.subtaskJSON = StringEscapeUtils.unescapeXml(subtaskJSON);
    }

  }

}
