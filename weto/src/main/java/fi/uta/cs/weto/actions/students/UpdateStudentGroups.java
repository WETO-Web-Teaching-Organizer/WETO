package fi.uta.cs.weto.actions.students;

import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UpdateStudentGroups extends WetoTeacherAction
{
  private Integer[] userIds;
  private Integer[] groupIds;
  private String[] submitGroups;

  public UpdateStudentGroups()
  {
    super(Tab.STUDENTS.getBit(), Tab.STUDENTS.getBit(), 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Integer taskId = getTaskId();
    ArrayList<GroupView> submitterGroupsView = GroupView.selectByTaskIdAndType(
            conn, taskId, GroupType.SUBMISSION.getValue());
    HashSet<Integer> submitterGroupSet = new HashSet<>();
    for(GroupView member : submitterGroupsView)
    {
      /* member.id is group id. */
      submitterGroupSet.add(member.getId());
    }
    Map<Integer, GroupMember> otherGroupMap = new HashMap<>();
    ArrayList<GroupMember> groupMembers = GroupMember.selectByTaskId(conn,
            taskId);
    for(GroupMember member : groupMembers)
    {
      if(submitterGroupSet.contains(member.getGroupId()))
      {
        member.delete(conn);
      }
      else
      {
        otherGroupMap.put(member.getUserId(), member);
      }
    }
    HashMap<String, ArrayList<Integer>> newSubmitterGroups = new HashMap<>();
    for(int i = 0; i < userIds.length; i++)
    {
      GroupMember member = otherGroupMap.get(userIds[i]);
      // May need to add a new group member?
      if(member == null)
      {
        if(groupIds[i] != -1)
        {
          GroupMember newMember = new GroupMember();
          newMember.setTaskId(taskId);
          newMember.setUserId(userIds[i]);
          newMember.setGroupId(groupIds[i]);
          newMember.insert(conn);
        }
      } // delete group Member
      else if(groupIds[i] == -1)
      {
        member.delete(conn);
      }
      // update group Member
      else if(!member.getGroupId().equals(groupIds[i]))
      {
        member.setGroupId(groupIds[i]);
        member.update(conn);
      }
      String submitGroupName = (submitGroups != null) ? submitGroups[i].trim()
                                       : "";
      if(!submitGroupName.isEmpty())
      {
        ArrayList<Integer> submitterIds = newSubmitterGroups
                .get(submitGroupName);
        if(submitterIds == null)
        {
          submitterIds = new ArrayList<>();
          newSubmitterGroups.put(submitGroupName, submitterIds);
        }
        submitterIds.add(userIds[i]);
      }
    }
    ArrayList<UserGroup> oldSubmitterGroups = UserGroup.selectByTaskIdAndType(
            conn, taskId, GroupType.SUBMISSION.getValue());
    int groupIndex = 0;
    for(Map.Entry<String, ArrayList<Integer>> entry : newSubmitterGroups
            .entrySet())
    {
      ArrayList<Integer> memberIds = entry.getValue();
      if(memberIds.size() > 1)
      {
        boolean doUpdate = false;
        UserGroup submitterGroup;
        if(groupIndex < oldSubmitterGroups.size())
        {
          submitterGroup = oldSubmitterGroups.get(groupIndex);
          doUpdate = true;
        }
        else
        {
          submitterGroup = new UserGroup();
        }
        submitterGroup.setTaskId(taskId);
        submitterGroup.setName(entry.getKey());
        submitterGroup.setType(GroupType.SUBMISSION.getValue());
        if(doUpdate)
        {
          submitterGroup.update(conn);
        }
        else
        {
          submitterGroup.insert(conn);
        }
        for(Integer memberId : entry.getValue())
        {
          GroupMember member = new GroupMember();
          member.setTaskId(taskId);
          member.setUserId(memberId);
          member.setGroupId(submitterGroup.getId());
          member.insert(conn);
        }
        groupIndex += 1;
      }
    }
    for(; groupIndex < oldSubmitterGroups.size(); ++groupIndex)
    {
      oldSubmitterGroups.get(groupIndex).delete(conn);
    }
    addActionMessage(getText("students.message.groupMemberSuccess"));
    return SUCCESS;
  }

  public void setUserIds(Integer[] userIds)
  {
    this.userIds = userIds;
  }

  public void setGroupIds(Integer[] groupIds)
  {
    this.groupIds = groupIds;
  }

  public void setSubmitGroups(String[] submitGroups)
  {
    this.submitGroups = submitGroups;
  }

}
