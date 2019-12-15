package fi.uta.cs.weto.actions.students;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.PropertyModel;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoTeacherAction;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;

public class PendingStudentsActions
{
  public static class View extends WetoTeacherAction
  {
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String[]> students = new ArrayList<>();

    public View()
    {
      super(Tab.STUDENTS.getBit(), 0, Tab.STUDENTS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Connection masterConn = getMasterConnection();
      Integer courseTaskId = getCourseTaskId();
      ArrayList<Property> pending = PropertyModel.getPendingStudents(courseConn,
              courseTaskId);
      for(Property p : pending)
      {
        try
        {
          Integer masterUserId = p.getKey();
          String groupIdStr = p.getValue();
          UserAccount user = UserAccount.select1ById(masterConn, masterUserId);
          String idStr = masterUserId.toString().concat(";");
          String groupName = null;
          if((groupIdStr != null) && !groupIdStr.isEmpty())
          {
            UserGroup group = UserGroup.select1ById(courseConn, Integer
                    .parseInt(groupIdStr));
            groupName = group.getName();
            idStr = idStr.concat(groupIdStr);
          }
          ids.add(idStr);
          String[] studentStrs = new String[]
          {
            user.getLastName(),
            user.getFirstName(),
            user.getLoginName(),
            user.getEmail(),
            groupName
          };
          students.add(studentStrs);
        }
        catch(NoSuchItemException e)
        {
          p.delete(courseConn);
        }
      }
      return INPUT;
    }

    public ArrayList<String> getIds()
    {
      return ids;
    }

    public ArrayList<String[]> getStudents()
    {
      return students;
    }

  }

  public static class Accept extends WetoTeacherAction
  {
    private ArrayList<String> acceptIds;

    public Accept()
    {
      super(Tab.STUDENTS.getBit(), 0, Tab.STUDENTS.getBit(), 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection masterConn = getMasterConnection();
      Connection courseConn = getCourseConnection();
      Integer courseTaskId = getCourseTaskId();
      for(String idPair : acceptIds)
      {
        String[] parts = idPair.split(";");
        Integer masterUserId = Integer.parseInt(parts[0]);
        UserAccount addUser = UserAccount.select1ById(masterConn, masterUserId);
        UserGroup addGroup = new UserGroup();
        addGroup.setTaskId(courseTaskId);
        if(parts.length > 1)
        {
          addGroup.setId(Integer.parseInt(parts[1]));
        }
        CourseMemberModel.addStudent(masterConn, courseConn, addUser, null,
                addGroup, false, false);
      }
      addActionMessage(getText("pendingstudents.message.accepted"));
      return SUCCESS;
    }

    public void setAcceptIds(ArrayList<String> acceptIds)
    {
      this.acceptIds = acceptIds;
    }

  }

  public static class Remove extends WetoTeacherAction
  {
    private ArrayList<String> removeIds;

    public Remove()
    {
      super(Tab.STUDENTS.getBit(), Tab.STUDENTS.getBit(), 0, 0);
    }

    @Override
    public String action() throws Exception
    {
      Connection courseConn = getCourseConnection();
      Integer courseTaskId = getCourseTaskId();
      HashSet<Integer> removeUserIds = new HashSet<>();
      for(String idPair : removeIds)
      {
        removeUserIds.add(Integer.parseInt(idPair.split(";")[0]));
      }
      ArrayList<Property> pending = PropertyModel.getPendingStudents(courseConn,
              courseTaskId);
      for(Property p : pending)
      {
        if(removeUserIds.contains(p.getKey()))
        {
          p.delete(courseConn);
        }
      }
      addActionMessage(getText("pendingstudents.message.notAccepted"));
      return SUCCESS;
    }

    // The form uses the name acceptIds, as it is shared with the accept action.
    public void setAcceptIds(ArrayList<String> removeIds)
    {
      this.removeIds = removeIds;
    }

  }
}
