package fi.uta.cs.weto.actions.students;

import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.PropertyModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewStudents extends WetoTeacherAction
{
  private ArrayList<StudentView> students;
  private ArrayList<UserGroup> groupList;
  private Map<Integer, String> groupMap;
  private Map<Integer, Integer> groupMemberMap;
  private String[] submitterGroups;
  private boolean hasOwnGroups;
  private boolean pendingStudents;

  public ViewStudents()
  {
    super(Tab.STUDENTS.getBit(), 0, 0, 0);
  }

  @Override
  public String action() throws Exception
  {
    Connection conn = getCourseConnection();
    Integer taskId = getTaskId();
    students = StudentView.selectByTaskId(conn, taskId);
    groupList = UserGroup.selectInheritedByTaskIdAndNotType(conn, taskId,
            GroupType.SUBMISSION.getValue());
    hasOwnGroups = (groupList.isEmpty() || groupList.get(0).getTaskId().equals(
            taskId));
    groupMap = new HashMap<>();
    groupMap.put(-1, "-");
    for(UserGroup userGroup : groupList)
    {
      groupMap.put(userGroup.getId(), userGroup.getName());
    }
    ArrayList<GroupView> submitterGroupList = GroupView
            .selectInheritedByTaskIdAndType(conn, taskId, GroupType.SUBMISSION
                    .getValue());
    HashMap<Integer, GroupView> submitterMap = new HashMap<>();
    for(GroupView gv : submitterGroupList)
    {
      submitterMap.put(gv.getUserId(), gv);
    }
    groupMemberMap = new HashMap<>();
    submitterGroups = new String[students.size()];
    int i = 0;
    for(StudentView st : students)
    {
      groupMemberMap.put(st.getUserId(), -1);
      GroupView submitterGroup = submitterMap.get(st.getUserId());
      if(submitterGroup != null)
      {
        submitterGroups[i] = submitterGroup.getName();
      }
      else
      {
        submitterGroups[i] = "";
      }
      i += 1;
    }
    ArrayList<GroupView> groupMembers;
    if(hasOwnGroups)
    {
      groupMembers = GroupView.selectByTaskIdAndNotType(conn, taskId,
              GroupType.SUBMISSION.getValue());
    }
    else
    {
      groupMembers = GroupView.selectInheritedByTaskIdAndNotType(conn, taskId,
              GroupType.SUBMISSION.getValue());
    }
    for(GroupView gv : groupMembers)
    {
      groupMemberMap.put(gv.getUserId(), gv.getId());
    }
    ArrayList<Property> pending = PropertyModel.getPendingStudents(conn,
            getNavigator().getCourseTaskId());
    pendingStudents = !pending.isEmpty();
    return INPUT;
  }

  public ArrayList<StudentView> getStudents()
  {
    return students;
  }

  public ArrayList<UserGroup> getGroupList()
  {
    return groupList;
  }

  public Map<Integer, String> getGroupMap()
  {
    return groupMap;
  }

  public Map<Integer, Integer> getGroupMemberMap()
  {
    return groupMemberMap;
  }

  public String[] getSubmitterGroups()
  {
    return submitterGroups;
  }

  public boolean isHasOwnGroups()
  {
    return hasOwnGroups;
  }

  public boolean isPendingStudents()
  {
    return pendingStudents;
  }

}
