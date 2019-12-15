package fi.uta.cs.weto.actions.groups;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.GroupView;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.GroupType;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.TaskModel;
import fi.uta.cs.weto.model.WetoActionException;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

public class GroupActions
{
  public static class View extends WetoCourseAction
  {
    private ArrayList<GroupInfo> groupList;
    private ArrayList<GroupInfo> parentGroupList;
    private String parentTaskName;
    private final GroupType[] typeList;
    private String[] typeNameList;

    public View()
    {
      super(Tab.GROUPS.getBit(), 0, 0, 0);
      typeList = GroupType.values();
    }

    @Override
    public String action() throws Exception
    {
      if(typeNameList == null)
      {
        typeNameList = new String[typeList.length];
        int i = 0;
        for(GroupType type : typeList)
        {
          typeNameList[i++] = getText(type.getProperty());
        }
      }
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      groupList = new ArrayList<>();
      Integer groupTaskId = taskId;
      // Students see a list of groups they belong to.
      if(getNavigator().isStudent())
      {
        ArrayList<GroupView> groups = GroupView
                .selectInheritedByTaskIdAndUserId(conn, groupTaskId,
                        getCourseUserId());
        if(!groups.isEmpty())
        {
          groupTaskId = groups.get(0).getTaskId();
        }
        for(GroupView userGroup : groups)
        {
          ArrayList<GroupMember> members = GroupMember.selectByTaskIdAndGroupId(
                  conn, userGroup.getTaskId(), userGroup.getId());
          groupList.add(new GroupInfo(userGroup.getId(), userGroup.getName(),
                  userGroup.getType(), members.size()));
        }
      }
      else // Teachers see the list of all groups.
      {
        ArrayList<UserGroup> groups = UserGroup.selectInheritedByTaskId(conn,
                groupTaskId);
        if(!groups.isEmpty())
        {
          groupTaskId = groups.get(0).getTaskId();
        }
        for(UserGroup userGroup : groups)
        {
          ArrayList<GroupMember> members = GroupMember.selectByTaskIdAndGroupId(
                  conn, userGroup.getTaskId(), userGroup.getId());
          groupList.add(new GroupInfo(userGroup.getId(), userGroup.getName(),
                  userGroup.getType(), members.size()));
        }
      }
      if(!groupTaskId.equals(taskId))
      {
        parentGroupList = groupList;
        groupList = new ArrayList<>();
        Task parentTask = Task.select1ById(conn, groupTaskId);
        parentTaskName = parentTask.getName();
      }
      return INPUT;
    }

    public ArrayList<GroupInfo> getGroupList()
    {
      return groupList;
    }

    public ArrayList<GroupInfo> getParentGroupList()
    {
      return parentGroupList;
    }

    public GroupType[] getTypeList()
    {
      return typeList;
    }

    public String[] getTypeNameList()
    {
      return typeNameList;
    }

    public String getParentTaskName()
    {
      return parentTaskName;
    }

  }

  public static class Add extends GroupBean
  {
    public Add()
    {
      super(Tab.GROUPS.getBit(), 0, Tab.GROUPS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      String groupName = (getGroupName() != null) ? getGroupName().trim() : null;
      if((groupName == null) || groupName.isEmpty())
      {
        throw new WetoActionException(getText("groups.error.groupName"),
                WetoCourseAction.INPUT);
      }
      Integer groupType = getGroupType();
      if(GroupType.getType(groupType).isImplicit())
      {
        throw new WetoActionException(getText("groups.error.invalidGroupType"),
                WetoCourseAction.INPUT);
      }
      if(GroupType.SUBMISSION.getValue().equals(groupType))
      {
        String[] userStrs = groupName.split("[,;\\s]");
        ArrayList<String> loginNames = new ArrayList<>();
        ArrayList<String> badUserStrs = new ArrayList<>();
        for(String userStr : userStrs)
        {
          try
          {
            loginNames.add(UserAccount.select1ByLoginName(conn, userStr)
                    .getLoginName());
          }
          catch(NoSuchItemException e)
          {
            try
            {
              loginNames.add(UserAccount.select1ByEmail(conn, userStr)
                      .getLoginName());
            }
            catch(NoSuchItemException e2)
            {
              badUserStrs.add(userStr);
            }
          }
        }
        if(!badUserStrs.isEmpty())
        {
          throw new WetoActionException(getText(
                  "groups.error.invalidSubmissionGroupUsers", new String[]
                  {
                    String.join(", ", badUserStrs)
                  }), WetoCourseAction.INPUT);
        }
        if(loginNames.size() < 2)
        {
          throw new WetoActionException(getText(
                  "groups.error.invalidSubmissionGroupSize"),
                  WetoCourseAction.INPUT);
        }
        else
        {
          Collections.sort(loginNames);
          String submissionGroupName = String.join(";", loginNames);
          UserGroup group = new UserGroup();
          group.setTaskId(getTaskId());
          group.setName(submissionGroupName);
          group.setType(GroupType.SUBMISSION.getValue());
          group.insert(conn);
          /* UNDER CONSTRUCTION!!! */
          GroupView.select1ByTaskIdAndUserIdAndType(conn, groupType, groupType,
                  groupType);
        }
      }
      else
      {
        UserGroup group = new UserGroup();
        group.setTaskId(getTaskId());
        group.setName(getGroupName());
        group.setType(getGroupType());
        group.insert(getCourseConnection());
      }
      addActionMessage(getText("updateGroup.message.createSuccess"));
      return SUCCESS;
    }

  }

  public static class Update extends GroupBean
  {
    public Update()
    {
      super(Tab.GROUPS.getBit(), Tab.GROUPS.getBit(), Tab.GROUPS.getBit(),
              Tab.GROUPS.getBit());
    }

    @Override
    public String action() throws Exception
    {
      Connection conn = getCourseConnection();
      Integer taskId = getTaskId();
      String[] groupNames = getGroupNames();
      String[] selectedGroups = getSelectedGroups();
      String[] oldGroupIds = getOldGroupIds();
      if(isDoUpdate())
      {
        Integer[] groupTypes = getGroupTypes();
        if((groupNames == null) || (groupNames.length < 1))
        {
          throw new WetoActionException(getText("groups.error.noGroupsExist"),
                  INPUT);
        }
        for(int i = 0; i < groupNames.length; i++)
        {
          UserGroup group = UserGroup.select1ById(conn, new Integer(
                  oldGroupIds[i]));
          validateCourseSubtaskId(group.getTaskId());
          group.setName(groupNames[i]);
          group.setType(groupTypes[i]);
          group.update(conn);
        }
        addActionMessage(getText("updateGroup.message.updateSuccess"));
      }
      else if(isDoDelete())
      {
        if((groupNames == null) || (groupNames.length < 1))
        {
          throw new WetoActionException(getText("groups.error.noGroupsExist"),
                  INPUT);
        }
        for(int i = 0; i < groupNames.length; i++)
        {
          for(String s : selectedGroups)
          {
            if(s.equals(oldGroupIds[i]))
            {
              Integer oldGroupId = Integer.valueOf(oldGroupIds[i]);
              UserGroup oldGroup = UserGroup.select1ById(conn, oldGroupId);
              validateCourseSubtaskId(oldGroup.getTaskId());
              if(!taskId.equals(oldGroup.getTaskId()))
              {
                throw new WetoActionException(getText(
                        "groups.error.invalidGroup"), INPUT);
              }
              else
              {
                TaskModel.deleteGroup(conn, oldGroup);
              }
            }
          }
        }
        addActionMessage(getText("updateGroup.message.deleteSuccess"));
      }
      return SUCCESS;
    }

  }

  public static class GroupInfo
  {
    private final Integer id;
    private final String name;
    private final Integer typeId;
    private final Integer size;

    public GroupInfo(Integer id, String name, Integer typeId, Integer size)
    {
      this.id = id;
      this.name = name;
      this.typeId = typeId;
      this.size = size;
    }

    public Integer getId()
    {
      return id;
    }

    public String getName()
    {
      return name;
    }

    public Integer getTypeId()
    {
      return typeId;
    }

    public Integer getSize()
    {
      return size;
    }

  }

  public static abstract class GroupBean extends WetoTeacherAction
  {
    private String groupName;
    private Integer groupType;
    private String[] oldGroupIds;
    private String[] selectedGroups;
    private String[] groupNames;
    private Integer[] groupTypes;
    private boolean doUpdate;
    private boolean doDelete;

    public GroupBean(int reqOwnerViewBits, int reqOwnerUpdateBits,
            int reqOwnerCreateBits, int reqOwnerDeleteBits)
    {
      super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits,
              reqOwnerDeleteBits);
    }

    public String getGroupName()
    {
      return groupName;
    }

    public void setGroupName(String groupName)
    {
      this.groupName = groupName;
    }

    public Integer getGroupType()
    {
      return groupType;
    }

    public void setGroupType(Integer groupType)
    {
      this.groupType = groupType;
    }

    public String[] getOldGroupIds()
    {
      return oldGroupIds;
    }

    public void setOldGroupIds(String[] oldGroupIds)
    {
      this.oldGroupIds = oldGroupIds;
    }

    public String[] getSelectedGroups()
    {
      return selectedGroups;
    }

    public void setSelectedGroups(String[] selectedGroups)
    {
      this.selectedGroups = selectedGroups;
    }

    public String[] getGroupNames()
    {
      return groupNames;
    }

    public void setGroupNames(String[] groupNames)
    {
      this.groupNames = groupNames;
    }

    public Integer[] getGroupTypes()
    {
      return groupTypes;
    }

    public void setGroupTypes(Integer[] groupTypes)
    {
      this.groupTypes = groupTypes;
    }

    public boolean isDoUpdate()
    {
      return doUpdate;
    }

    public void setDoUpdate(boolean doUpdate)
    {
      this.doUpdate = doUpdate;
    }

    public boolean isDoDelete()
    {
      return doDelete;
    }

    public void setDoDelete(boolean doDelete)
    {
      this.doDelete = doDelete;
    }

  }
}
