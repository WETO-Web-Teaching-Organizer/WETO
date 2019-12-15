package fi.uta.cs.weto.actions.admin;

import fi.uta.cs.weto.db.ClusterIdReplication;
import fi.uta.cs.weto.db.ClusterMember;
import fi.uta.cs.weto.db.CourseImplementation;
import fi.uta.cs.weto.db.CourseView;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.RightsCluster;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.TaskModel;
import fi.uta.cs.weto.model.WetoAdminAction;
import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public abstract class DeleteCourseActions
{
  public static class Input extends WetoAdminAction
  {
    private ArrayList<CourseView> courses;

    @Override
    public String action() throws Exception
    {
      courses = CourseView.selectAll(getMasterConnection());
      return SUCCESS;
    }

    public ArrayList<CourseView> getCourses()
    {
      return courses;
    }

  }

  public static class Confirm extends WetoAdminAction
  {
    private Integer masterCourseTaskId;
    private String taskName;
    private String taskText;

    @Override
    public String action() throws Exception
    {
      Task task = Task.select1ById(getMasterConnection(), masterCourseTaskId);
      taskName = task.getName();
      taskText = task.getText();
      return SUCCESS;
    }

    public void setCourseTaskId(Integer courseTaskId)
    {
      this.masterCourseTaskId = courseTaskId;
    }

    public Integer getCourseTaskId()
    {
      return masterCourseTaskId;
    }

    public String getTaskName()
    {
      return taskName;
    }

    public String getTaskText()
    {
      return taskText;
    }

  }

  public static class Commit extends WetoAdminAction
  {
    private static final Logger logger = Logger.getLogger(Commit.class);

    private Integer masterCourseTaskId;

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      CourseView course = CourseView.select1ByMasterTaskId(masterConn,
              masterCourseTaskId);
      Integer courseDbId = course.getDatabaseId();
      Connection courseConn = getConnection(courseDbId);
      Integer courseTaskId = course.getCourseTaskId();
      ArrayDeque<Integer> dfsStack = new ArrayDeque<>(Task.selectSubtaskIds(
              courseConn, courseTaskId));
      Map<Integer, Integer> subtasksLeftMap = new HashMap<>();
      Map<Integer, Integer> containerIdMap = new HashMap<>();
      // The stack processes all subtasks
      while(!dfsStack.isEmpty())
      {
        Integer currTaskId = dfsStack.getLast();
        Integer subtasksLeft = subtasksLeftMap.get(currTaskId);
        if(subtasksLeft == null)
        {
          ArrayList<Integer> subtaskIds = Task.selectSubtaskIds(courseConn,
                  currTaskId);
          subtasksLeft = subtaskIds.size();
          subtasksLeftMap.put(currTaskId, subtasksLeft);
          dfsStack.addAll(subtaskIds);
          for(Integer subtaskId : subtaskIds)
          {
            containerIdMap.put(subtaskId, currTaskId);
          }
        }
        if(subtasksLeft < 1)
        {
          TaskModel.deleteCourseDbTask(courseConn, masterConn, currTaskId);
          dfsStack.removeLast();
          Integer containerId = containerIdMap.get(currTaskId);
          if(containerId != null)
          {
            subtasksLeftMap.put(containerId, subtasksLeftMap.get(containerId)
                    - 1);
          }
        }
      }
      // Now remove course membership information and finally the course task
      // both in course and master databases.
      for(RightsCluster courseDbCluster : RightsCluster.selectByTaskId(
              courseConn, courseTaskId))
      {
        ClusterIdReplication cir = ClusterIdReplication
                .select1ByCourseDbClusterId(courseConn, courseDbCluster.getId());
        Integer masterDbClusterId = cir.getMasterDbClusterId();
        cir.delete(courseConn);
        for(ClusterMember member : ClusterMember.selectByClusterId(courseConn,
                courseDbCluster.getId()))
        {
          member.delete(courseConn);
        }
        courseDbCluster.delete(courseConn);
        RightsCluster masterDbCluster = RightsCluster.select1ById(masterConn,
                masterDbClusterId);
        for(ClusterMember member : ClusterMember.selectByClusterId(masterConn,
                masterDbCluster.getId()))
        {
          member.delete(masterConn);
        }
        masterDbCluster.delete(masterConn);
      }
      TaskModel.deleteCourseDbTask(courseConn, masterConn, courseTaskId);
      // Finally delete permissions, permitted students and task from master db
      for(Permission permission : Permission.selectByTaskId(masterConn,
              masterCourseTaskId))
      {
        permission.delete(masterConn);
      }
      CourseMemberModel.emptyPermittedStudents(masterConn, masterCourseTaskId);
      CourseImplementation.select1ByMasterTaskId(masterConn, masterCourseTaskId)
              .delete(masterConn);
      Task.select1ById(masterConn, masterCourseTaskId).delete(masterConn);
      addActionMessage(getText("deleteCourse.message.success", new String[]
      {
        course.getName()
      }));
      logger.debug("User " + getMasterUserId() + " deleted the course " + course
              .getName());
      return SUCCESS;
    }

    public void setCourseTaskId(Integer courseTaskId)
    {
      this.masterCourseTaskId = courseTaskId;
    }

  }

}
