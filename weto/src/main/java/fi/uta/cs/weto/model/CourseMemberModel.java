package fi.uta.cs.weto.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.weto.db.ClusterIdReplication;
import fi.uta.cs.weto.db.ClusterMember;
import fi.uta.cs.weto.db.GroupMember;
import fi.uta.cs.weto.db.Permission;
import fi.uta.cs.weto.db.PermissionIdReplication;
import fi.uta.cs.weto.db.Property;
import fi.uta.cs.weto.db.RightsCluster;
import fi.uta.cs.weto.db.Student;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.Teacher;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserGroup;
import fi.uta.cs.weto.db.UserIdReplication;
import fi.uta.cs.weto.util.WetoUtilities;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;

public class CourseMemberModel
{
  public static void addStudent(Connection masterConn, Connection courseConn,
          UserAccount addUser, String studentNumber, UserGroup addGroup,
          boolean updateUserInfo, boolean addNewUser)
          throws InvalidValueException, SQLException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException, IOException
  {
    Integer courseTaskId = addGroup.getTaskId();
    String loginName = addUser.getLoginName();
    // If necessary, create user, student and clustermember entries into the
    // master database
    UserAccount masterUser;
    boolean updateUser = false;
    try
    {
      try
      {
        masterUser = UserAccount.select1ByLoginName(masterConn, loginName);
      }
      catch(NoSuchItemException e2)
      {
        String email = addUser.getEmail();
        if((email != null) && !email.isEmpty())
        {
          masterUser = UserAccount
                  .select1ByEmail(masterConn, addUser.getEmail());
          loginName = masterUser.getLoginName();
        }
        else
        {
          throw e2;
        }
      }
      // Update user information if necessary.
      if(updateUserInfo)
      {
        if(!addUser.getFirstName().isEmpty())
        {
          masterUser.setFirstName(addUser.getFirstName());
          updateUser = true;
        }
        if(!addUser.getLastName().isEmpty())
        {
          masterUser.setLastName(addUser.getLastName());
          updateUser = true;
        }
        if(!addUser.getEmail().isEmpty())
        {
          masterUser.setEmail(addUser.getEmail());
          updateUser = true;
        }
        if(updateUser)
        {
          masterUser.update(masterConn);
        }
      }
    }
    catch(NoSuchItemException e)
    {
      if(addNewUser)
      {
        masterUser = addUser;
        masterUser.insert(masterConn);
      }
      else
      {
        throw e;
      }
    }
    Integer masterUserId = masterUser.getId();
    try
    {
      Student masterStudent = Student.select1ByUserId(masterConn, masterUserId);
      studentNumber = masterStudent.getStudentNumber();
    }
    catch(NoSuchItemException e)
    {
      if(studentNumber == null || studentNumber.isEmpty())
      {
        studentNumber = WetoUtilities.getStudentNumberFromLoginName(
                masterUser.getLoginName(), false);
        if(studentNumber == null)
        {
          studentNumber = "";
        }
      }
      Student masterStudent = new Student();
      masterStudent.setUserId(masterUserId);
      masterStudent.setStudentNumber(studentNumber);
      masterStudent.insert(masterConn);
    }
    RightsCluster courseCluster = RightsCluster.select1ByTaskIdAndType(
            courseConn, courseTaskId, ClusterType.STUDENTS.getValue());
    ClusterIdReplication cir = ClusterIdReplication
            .select1ByCourseDbClusterId(courseConn, courseCluster.getId());
    RightsCluster masterCluster = RightsCluster.select1ById(masterConn, cir
            .getMasterDbClusterId());
    try
    {
      ClusterMember.select1ByClusterIdAndUserId(masterConn, masterCluster
              .getId(), masterUserId);
    }
    catch(NoSuchItemException e)
    {
      ClusterMember masterCm = new ClusterMember();
      masterCm.setClusterId(masterCluster.getId());
      masterCm.setUserId(masterUserId);
      masterCm.insert(masterConn);
    }
    UserAccount courseUser;
    Integer courseUserId;
    try
    {
      courseUser = UserAccount.select1ByLoginName(courseConn, loginName);
      courseUserId = courseUser.getId();
      if(updateUser)
      {
        courseUser.setFirstName(masterUser.getFirstName());
        courseUser.setLastName(masterUser.getLastName());
        courseUser.setEmail(masterUser.getEmail());
        courseUser.update(courseConn);
      }
    }
    catch(NoSuchItemException e)
    {
      // Add entries to user, student, clustermember and user id replication
      courseUser = new UserAccount();
      courseUser.setLoginName(loginName);
      courseUser.setFirstName(masterUser.getFirstName());
      courseUser.setLastName(masterUser.getLastName());
      courseUser.setEmail(masterUser.getEmail());
      courseUser.setPassword("");
      courseUser.insert(courseConn);
      courseUserId = courseUser.getId();
    }
    try
    {
      Student.select1ByUserId(courseConn, courseUserId);
    }
    catch(NoSuchItemException e)
    {
      Student courseStudent = new Student();
      courseStudent.setUserId(courseUserId);
      courseStudent.setStudentNumber(studentNumber);
      courseStudent.insert(courseConn);
    }
    try
    {
      ClusterMember.select1ByClusterIdAndUserId(courseConn, courseCluster
              .getId(), courseUserId);
    }
    catch(NoSuchItemException e)
    {
      ClusterMember courseCm = new ClusterMember();
      courseCm.setClusterId(courseCluster.getId());
      courseCm.setUserId(courseUserId);
      courseCm.insert(courseConn);
    }
    try
    {
      UserIdReplication.select1ByMasterDbUserId(courseConn, masterUserId);
    }
    catch(NoSuchItemException e)
    {
      UserIdReplication uidr = new UserIdReplication();
      uidr.setMasterDbUserId(masterUserId);
      uidr.setCourseDbUserId(courseUserId);
      uidr.insert(courseConn);
    }
    Integer masterTaskId = masterCluster.getTaskId();
    boolean inPermittedCourses = CourseMemberModel.getPermittedCourses(
            masterConn, addUser, studentNumber).contains(masterTaskId);
    if(inPermittedCourses)
    {
      try
      {
        Property groupPermitProperty = Property.select1ByTypeAndRefIdAndKey(
                masterConn, PropertyType.PERMITTED_GROUPS.getValue(),
                masterTaskId, 0);
        HashMap<String, String> loginGroupMap = new HashMap<>();
        HashMap<String, String> emailGroupMap = new HashMap<>();
        HashMap<String, String> studentNumberGroupMap = new HashMap<>();
        readPermittedGroupJson(groupPermitProperty.getValue(), loginGroupMap,
                emailGroupMap, studentNumberGroupMap);
        String addLoginName = addUser.getLoginName();
        if(addLoginName != null)
        {
          addLoginName = addLoginName.trim().toLowerCase();
        }
        String addEmail = addUser.getEmail();
        if(addEmail != null)
        {
          addEmail = addEmail.trim().toLowerCase();
        }
        if(studentNumber != null)
        {
          studentNumber = studentNumber.trim().toLowerCase();
        }
        if((addLoginName != null) && loginGroupMap.containsKey(addLoginName))
        {
          addGroup.setName(loginGroupMap.get(addLoginName));
        }
        else if((addEmail != null) && emailGroupMap.containsKey(addEmail))
        {
          addGroup.setName(emailGroupMap.get(addEmail));
        }
        else if((studentNumber != null) && studentNumberGroupMap.containsKey(
                studentNumber))
        {
          addGroup.setName(studentNumberGroupMap.get(studentNumber));
        }
      }
      catch(NoSuchItemException e)
      {
      }
    }
    // Add to a group, if necessary
    try
    {
      String groupName = addGroup.getName();
      Integer groupId = addGroup.getId();
      if((groupName != null) && !groupName.isEmpty())
      {
        UserGroup group = UserGroup.select1ByTaskIdAndName(courseConn,
                courseTaskId, groupName);
        groupId = group.getId();
      }
      else if(groupId != null)
      {
        // Check that the group exists
        UserGroup.select1ById(courseConn, groupId);
      }
      // Did we find a group?
      if(groupId != null)
      {
        GroupMember member;
        boolean updateMember = true;
        ArrayList<GroupMember> members = GroupMember.selectByTaskIdAndUserId(
                courseConn, courseTaskId, courseUserId);
        if(!members.isEmpty())
        {
          member = members.get(0);
        }
        else
        {
          updateMember = false;
          member = new GroupMember();
        }
        member.setGroupId(groupId);
        member.setTaskId(courseTaskId);
        member.setUserId(courseUserId);
        if(updateMember)
        {
          member.update(courseConn);
        }
        else
        {
          member.insert(courseConn);
        }
      }
    }
    catch(NoSuchItemException e)
    {
      // No group information; do nothing
    }
    // As last, remove possible information about pending or permitted student.
    try
    {
      Property pending = PropertyModel.getPendingStudent(courseConn,
              courseTaskId, masterUserId);
      pending.delete(courseConn);
    }
    catch(NoSuchItemException e)
    { // No pending information. Not an error.
    }
    if(inPermittedCourses)
    {
      ArrayList<String[]> removeList = new ArrayList<>();
      removeList.add(new String[]
      {
        addUser.getLoginName(), addUser.getEmail(), studentNumber
      });
      CourseMemberModel.updatePermittedStudents(masterConn, masterTaskId,
              new ArrayList<String[]>(), removeList);
    }
  }

  private static ArrayList<UserAccount> addMembers(Connection masterConn,
          Connection courseConn, Integer masterCourseTaskId,
          Integer[] masterUserIds, final Integer clusterType)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException
  {
    ArrayList<UserAccount> addedMembers = new ArrayList<>();
    // Get the member cluster for course
    RightsCluster masterCluster = RightsCluster.select1ByTaskIdAndType(
            masterConn, masterCourseTaskId, clusterType);
    // Add new clustermember if not found
    ClusterMember newMember = new ClusterMember();
    for(Integer masterUserId : masterUserIds)
    {
      newMember.setUserId(masterUserId);
      newMember.setClusterId(masterCluster.getId());
      try
      {
        newMember.select(masterConn);
      }
      catch(NoSuchItemException e)
      {
        newMember.insert(masterConn);
      }
      UserAccount addedMember = UserAccount
              .select1ById(masterConn, masterUserId);
      // If not yet there, replicate user information to course database.
      UserIdReplication uidr;
      try
      {
        uidr = UserIdReplication.select1ByMasterDbUserId(courseConn,
                masterUserId);
      }
      catch(NoSuchItemException e)
      {
        addedMember.insert(courseConn);
        uidr = new UserIdReplication();
        uidr.setMasterDbUserId(masterUserId);
        uidr.setCourseDbUserId(addedMember.getId());
        uidr.insert(courseConn);
      }
      if(ClusterType.TEACHERS.equals(clusterType))
      {
        // Check if the user is already in the master and course database
        // teacher tables. If not, add user to them.
        try
        {
          Teacher.select1ByUserId(masterConn, masterUserId);
        }
        catch(NoSuchItemException e)
        {
          Teacher teacher = new Teacher();
          teacher.setUserId(masterUserId);
          teacher.insert(masterConn);
        }
        try
        {
          Teacher.select1ByUserId(courseConn, uidr.getCourseDbUserId());
        }
        catch(NoSuchItemException e)
        {
          Teacher teacher = new Teacher();
          teacher.setUserId(uidr.getCourseDbUserId());
          teacher.insert(courseConn);
        }
      }
      ClusterIdReplication cir = ClusterIdReplication
              .select1ByMasterDbClusterId(courseConn, masterCluster.getId());
      RightsCluster courseCluster = RightsCluster.select1ById(courseConn, cir
              .getCourseDbClusterId());
      try
      {
        ClusterMember.select1ByClusterIdAndUserId(courseConn, courseCluster
                .getId(), uidr.getCourseDbUserId());
      }
      catch(NoSuchItemException e)
      {
        ClusterMember clusterMember = new ClusterMember();
        clusterMember.setClusterId(courseCluster.getId());
        clusterMember.setUserId(uidr.getCourseDbUserId());
        clusterMember.insert(courseConn);
      }
      addedMembers.add(addedMember);
    }
    return addedMembers;
  }

  public static ArrayList<UserAccount> addTeachers(Connection masterConn,
          Connection courseConn, Integer masterCourseTaskId,
          Integer[] masterUserIds)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException
  {
    return addMembers(masterConn, courseConn, masterCourseTaskId, masterUserIds,
            ClusterType.TEACHERS.getValue());
  }

  public static ArrayList<UserAccount> addAssistants(Connection masterConn,
          Connection courseConn, Integer masterCourseTaskId,
          Integer[] masterUserIds)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException
  {
    return addMembers(masterConn, courseConn, masterCourseTaskId, masterUserIds,
            ClusterType.ASSISTANTS.getValue());
  }

  private static boolean deleteMember(Connection masterConn,
          Connection courseConn, Integer masterCourseTaskId,
          Integer masterUserId, final Integer clusterType)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException, TooManyItemsException
  {
    boolean result = true;
    // Get the member clusters for the course
    RightsCluster masterCluster = RightsCluster.select1ByTaskIdAndType(
            masterConn, masterCourseTaskId, clusterType);
    ClusterIdReplication cir = ClusterIdReplication.select1ByMasterDbClusterId(
            courseConn, masterCluster.getId());
    RightsCluster courseCluster = RightsCluster.select1ById(courseConn, cir
            .getCourseDbClusterId());
    // Remove from both above-fetched teacher clusters, if exists there
    try
    {
      ClusterMember.select1ByClusterIdAndUserId(masterConn, masterCluster
              .getId(), masterUserId).delete(masterConn);
    }
    catch(NoSuchItemException e)
    {
      result = false;
    }
    try
    {
      UserIdReplication uidr = UserIdReplication.select1ByMasterDbUserId(
              courseConn, masterUserId);
      ClusterMember.select1ByClusterIdAndUserId(courseConn, courseCluster
              .getId(), uidr.getCourseDbUserId()).delete(courseConn);
    }
    catch(NoSuchItemException e)
    {
      result = false;
    }
    return result;
  }

  public static boolean deleteTeacher(Connection masterConn,
          Connection courseConn, Integer masterCourseTaskId,
          Integer masterTeacherId)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException, TooManyItemsException
  {
    return deleteMember(masterConn, courseConn, masterCourseTaskId,
            masterTeacherId, ClusterType.TEACHERS.getValue());
  }

  public static boolean deleteAssistant(Connection masterConn,
          Connection courseConn, Integer masterCourseTaskId,
          Integer masterAssistantId)
          throws NoSuchItemException, SQLException, InvalidValueException,
                 ObjectNotValidException, TooManyItemsException
  {
    return deleteMember(masterConn, courseConn, masterCourseTaskId,
            masterAssistantId, ClusterType.ASSISTANTS.getValue());
  }

  public static HashSet<Integer> getPermittedCourses(Connection masterConn,
          UserAccount user, String studentNumber)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, TooManyItemsException
  {
    HashSet<Integer> result = new HashSet<>();
    ServletContext context = ServletActionContext.getServletContext();
    HashMap<String, HashSet<Integer>> allPermittedLogins
                                              = (HashMap<String, HashSet<Integer>>) context
            .getAttribute("permittedLogins");
    HashMap<String, HashSet<Integer>> allPermittedEmails
                                              = (HashMap<String, HashSet<Integer>>) context
            .getAttribute("permittedEmails");
    HashMap<String, HashSet<Integer>> allPermittedStudentNumbers
                                              = (HashMap<String, HashSet<Integer>>) context
            .getAttribute("permittedStudentNumbers");
    if((allPermittedLogins == null) || (allPermittedEmails == null)
            || (allPermittedStudentNumbers == null))
    { // The step below refreshes the permitted student information.
      updatePermittedStudents(masterConn, user.getId(),
              new ArrayList<String[]>(), new ArrayList<String[]>());
      allPermittedLogins = (HashMap<String, HashSet<Integer>>) context
              .getAttribute("permittedLogins");
      allPermittedEmails = (HashMap<String, HashSet<Integer>>) context
              .getAttribute("permittedEmails");
      allPermittedStudentNumbers = (HashMap<String, HashSet<Integer>>) context
              .getAttribute("permittedStudentNumbers");
    }
    HashSet<Integer> userLoginCourses = allPermittedLogins.get(user
            .getLoginName().trim().toLowerCase());
    if(userLoginCourses != null)
    {
      result.addAll(userLoginCourses);
    }
    HashSet<Integer> userEmailCourses = allPermittedEmails.get(user.getEmail()
            .trim().toLowerCase());
    if(userEmailCourses != null)
    {
      result.addAll(userEmailCourses);
    }
    if(studentNumber != null) // A user does not need to have a student number!
    {
      HashSet<Integer> userStudentNumberCourses = allPermittedStudentNumbers
              .get(studentNumber.trim().toLowerCase());
      if(userStudentNumberCourses != null)
      {
        result.addAll(userStudentNumberCourses);
      }
    }
    return result;
  }

  public static void updatePermittedStudents(Connection masterConn,
          Integer masterTaskId, ArrayList<String[]> permittedList,
          ArrayList<String[]> removeList)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, TooManyItemsException
  {
    doUpdatePermittedStudents(masterConn, masterTaskId, permittedList,
            removeList, false);
  }

  public static void emptyPermittedStudents(Connection masterConn,
          Integer masterTaskId)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, TooManyItemsException
  {
    doUpdatePermittedStudents(masterConn, masterTaskId,
            new ArrayList<String[]>(), new ArrayList<String[]>(), true);
  }

  private static void doUpdatePermittedStudents(Connection masterConn,
          Integer masterTaskId, ArrayList<String[]> permittedList,
          ArrayList<String[]> removeList, final boolean removeAll)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, IOException, TooManyItemsException
  {
    ArrayList<Property> permitProperties = Property.selectByType(masterConn,
            PropertyType.PERMITTED_STUDENTS.getValue());
    Property permitProperty = null;
    boolean doUpdate = false;
    boolean doInsert = false;
    if(permitProperties.isEmpty())
    {
      permitProperty = new Property();
      permitProperty.setType(PropertyType.PERMITTED_STUDENTS.getValue());
      permitProperty.setRefId(0);
      permitProperty.setKey(0);
      permitProperty.setValue("");
      doInsert = true;
    }
    else
    {
      permitProperty = permitProperties.get(0);
    }
    HashMap<String, HashSet<Integer>> permittedLogins = new HashMap<>();
    HashMap<String, HashSet<Integer>> permittedEmails = new HashMap<>();
    HashMap<String, HashSet<Integer>> permittedStudentNumbers = new HashMap<>();
    if((permitProperty.getValue() != null) && !permitProperty.getValue().trim()
            .isEmpty())
    {
      JsonObject rootJson = new JsonParser().parse(permitProperty.getValue())
              .getAsJsonObject();
      JsonObject loginMap = rootJson.getAsJsonObject("logins");
      if(loginMap != null)
      {
        for(Map.Entry<String, JsonElement> entry : loginMap.entrySet())
        {
          String login = entry.getKey().trim().toLowerCase();
          HashSet<Integer> taskIdSet = permittedLogins.get(login);
          if(taskIdSet == null)
          {
            taskIdSet = new HashSet<>();
            permittedLogins.put(login, taskIdSet);
          }
          JsonArray taskList = entry.getValue().getAsJsonArray();
          for(JsonElement jsonTaskId : taskList)
          {
            Integer taskId = jsonTaskId.getAsInt();
            if(!removeAll || !taskId.equals(masterTaskId))
            {
              taskIdSet.add(taskId);
            }
          }
        }
      }
      JsonObject emailMap = rootJson.getAsJsonObject("emails");
      if(emailMap != null)
      {
        for(Map.Entry<String, JsonElement> entry : emailMap.entrySet())
        {
          String email = entry.getKey().trim().toLowerCase();
          HashSet<Integer> taskIdSet = permittedEmails.get(email);
          if(taskIdSet == null)
          {
            taskIdSet = new HashSet<>();
            permittedEmails.put(email, taskIdSet);
          }
          JsonArray taskList = entry.getValue().getAsJsonArray();
          for(JsonElement jsonTaskId : taskList)
          {
            Integer taskId = jsonTaskId.getAsInt();
            if(!removeAll || !taskId.equals(masterTaskId))
            {
              taskIdSet.add(taskId);
            }
          }
        }
      }
      JsonObject studentNumberMap = rootJson.getAsJsonObject("studentNumbers");
      if(studentNumberMap != null)
      {
        for(Map.Entry<String, JsonElement> entry : studentNumberMap.entrySet())
        {
          String studentNumber = entry.getKey().trim().toLowerCase();
          HashSet<Integer> taskIdSet = permittedStudentNumbers.get(
                  studentNumber);
          if(taskIdSet == null)
          {
            taskIdSet = new HashSet<>();
            permittedStudentNumbers.put(studentNumber, taskIdSet);
          }
          JsonArray taskList = entry.getValue().getAsJsonArray();
          for(JsonElement jsonTaskId : taskList)
          {
            Integer taskId = jsonTaskId.getAsInt();
            if(!removeAll || !taskId.equals(masterTaskId))
            {
              taskIdSet.add(taskId);
            }
          }
        }
      }
    }
    ArrayList<String[]> addGroupList = new ArrayList<>();
    for(String[] permitInfo : permittedList)
    {
      String addLoginName = permitInfo[0];
      String addEmail = permitInfo[1];
      String addStudentNumber = permitInfo[2];
      if((permitInfo.length > 3) && (permitInfo[3] != null) && !permitInfo[3]
              .isEmpty())
      {
        addGroupList.add(permitInfo);
      }
      if((addLoginName != null) && !addLoginName.isEmpty())
      {
        addLoginName = addLoginName.trim().toLowerCase();
        HashSet<Integer> taskIdSet = permittedLogins.get(addLoginName);
        if(taskIdSet == null)
        {
          taskIdSet = new HashSet<>();
          permittedLogins.put(addLoginName, taskIdSet);
        }
        taskIdSet.add(masterTaskId);
        doUpdate = true;
      }
      if((addEmail != null) && !addEmail.isEmpty())
      {
        addEmail = addEmail.trim().toLowerCase();
        HashSet<Integer> taskIdSet = permittedEmails.get(addEmail);
        if(taskIdSet == null)
        {
          taskIdSet = new HashSet<>();
          permittedEmails.put(addEmail, taskIdSet);
        }
        taskIdSet.add(masterTaskId);
        doUpdate = true;
      }
      if((addStudentNumber != null) && !addStudentNumber.isEmpty())
      {
        addStudentNumber = addStudentNumber.trim().toLowerCase();
        HashSet<Integer> taskIdSet = permittedStudentNumbers.get(
                addStudentNumber);
        if(taskIdSet == null)
        {
          taskIdSet = new HashSet<>();
          permittedStudentNumbers.put(addStudentNumber, taskIdSet);
        }
        taskIdSet.add(masterTaskId);
        doUpdate = true;
      }
    }
    for(String[] removeInfo : removeList)
    {
      String removeLoginName = removeInfo[0];
      String removeEmail = removeInfo[1];
      String removeStudentNumber = removeInfo[2];
      if((removeLoginName != null) && !removeLoginName.isEmpty())
      {
        removeLoginName = removeLoginName.trim().toLowerCase();
        HashSet<Integer> taskIdSet = permittedLogins.get(removeLoginName);
        if(taskIdSet != null)
        {
          taskIdSet.remove(masterTaskId);
          if(taskIdSet.isEmpty())
          {
            permittedLogins.remove(removeLoginName);
          }
        }
        doUpdate = true;
      }
      if((removeEmail != null) && !removeEmail.isEmpty())
      {
        removeEmail = removeEmail.trim().toLowerCase();
        HashSet<Integer> taskIdSet = permittedEmails.get(removeEmail);
        if(taskIdSet != null)
        {
          taskIdSet.remove(masterTaskId);
          if(taskIdSet.isEmpty())
          {
            permittedEmails.remove(removeEmail);
          }
        }
        doUpdate = true;
      }
      if((removeStudentNumber != null) && !removeStudentNumber.isEmpty())
      {
        removeStudentNumber = removeStudentNumber.trim().toLowerCase();
        HashSet<Integer> taskIdSet = permittedStudentNumbers.get(
                removeStudentNumber);
        if(taskIdSet != null)
        {
          taskIdSet.remove(masterTaskId);
          if(taskIdSet.isEmpty())
          {
            permittedStudentNumbers.remove(removeStudentNumber);
          }
        }
        doUpdate = true;
      }
    }
    ServletContext context = ServletActionContext.getServletContext();
    context.setAttribute("permittedLogins", permittedLogins);
    context.setAttribute("permittedEmails", permittedEmails);
    context.setAttribute("permittedStudentNumbers", permittedStudentNumbers);
    if(doUpdate || doInsert)
    {
      updatePermittedGroups(masterConn, masterTaskId, permittedLogins,
              permittedEmails, permittedStudentNumbers, addGroupList);
      JsonObject jsonLoginMap = new JsonObject();
      for(Map.Entry<String, HashSet<Integer>> entry : permittedLogins.entrySet())
      {
        String login = entry.getKey();
        JsonArray jsonLogins = new JsonArray();
        for(Integer taskId : entry.getValue())
        {
          jsonLogins.add(taskId);
        }
        jsonLoginMap.add(login, jsonLogins);
      }
      JsonObject jsonEmailMap = new JsonObject();
      for(Map.Entry<String, HashSet<Integer>> entry : permittedEmails.entrySet())
      {
        String email = entry.getKey();
        JsonArray jsonEmails = new JsonArray();
        for(Integer taskId : entry.getValue())
        {
          jsonEmails.add(taskId);
        }
        jsonEmailMap.add(email, jsonEmails);
      }
      JsonObject jsonStudentNumberMap = new JsonObject();
      for(Map.Entry<String, HashSet<Integer>> entry : permittedStudentNumbers
              .entrySet())
      {
        String studentNumber = entry.getKey();
        JsonArray jsonStudentNumbers = new JsonArray();
        for(Integer taskId : entry.getValue())
        {
          jsonStudentNumbers.add(taskId);
        }
        jsonStudentNumberMap.add(studentNumber, jsonStudentNumbers);
      }
      JsonObject rootJson = new JsonObject();
      rootJson.add("logins", jsonLoginMap);
      rootJson.add("emails", jsonEmailMap);
      rootJson.add("studentNumbers", jsonStudentNumberMap);
      permitProperty.setValue(rootJson.toString());
      if(doInsert)
      {
        permitProperty.insert(masterConn);
      }
      else if(doUpdate)
      {
        permitProperty.update(masterConn);
      }
    }
  }

  private static void readPermittedGroupJson(String groupPermitJson,
          HashMap<String, String> loginGroupMap,
          HashMap<String, String> emailGroupMap,
          HashMap<String, String> studentNumberGroupMap)
  {
    if((groupPermitJson != null) && !groupPermitJson.trim().isEmpty())
    {
      JsonObject rootJson = new JsonParser().parse(groupPermitJson)
              .getAsJsonObject();
      JsonObject loginMap = rootJson.getAsJsonObject("loginGroups");
      if(loginMap != null)
      {
        for(Map.Entry<String, JsonElement> entry : loginMap.entrySet())
        {
          String login = entry.getKey().trim().toLowerCase();
          String group = entry.getValue().getAsString();
          loginGroupMap.put(login, group);
        }
      }
      JsonObject emailMap = rootJson.getAsJsonObject("emailGroups");
      if(emailMap != null)
      {
        for(Map.Entry<String, JsonElement> entry : emailMap.entrySet())
        {
          String email = entry.getKey().trim().toLowerCase();
          String group = entry.getValue().getAsString();
          emailGroupMap.put(email, group);
        }
      }
      JsonObject studentNumberMap = rootJson.getAsJsonObject(
              "studentNumberGroups");
      if(studentNumberMap != null)
      {
        for(Map.Entry<String, JsonElement> entry : studentNumberMap.entrySet())
        {
          String studentNumber = entry.getKey().trim().toLowerCase();
          String group = entry.getValue().getAsString();
          studentNumberGroupMap.put(studentNumber, group);
        }
      }
    }
  }

  private static void updatePermittedGroups(Connection masterConn,
          Integer masterTaskId,
          HashMap<String, HashSet<Integer>> permittedLogins,
          HashMap<String, HashSet<Integer>> permittedEmails,
          HashMap<String, HashSet<Integer>> permittedStudentNumbers,
          ArrayList<String[]> addGroupList)
          throws SQLException, InvalidValueException, ObjectNotValidException,
                 NoSuchItemException, TooManyItemsException
  {
    Property groupPermitProperty = null;
    boolean doInsert = false;
    try
    {
      groupPermitProperty = Property.select1ByTypeAndRefIdAndKey(masterConn,
              PropertyType.PERMITTED_GROUPS.getValue(), masterTaskId, 0);
    }
    catch(NoSuchItemException e)
    {
      groupPermitProperty = new Property();
      groupPermitProperty.setType(PropertyType.PERMITTED_GROUPS.getValue());
      groupPermitProperty.setRefId(masterTaskId);
      groupPermitProperty.setKey(0);
      groupPermitProperty.setValue("");
      doInsert = true;
    }
    HashMap<String, String> loginGroupMap = new HashMap<>();
    HashMap<String, String> emailGroupMap = new HashMap<>();
    HashMap<String, String> studentNumberGroupMap = new HashMap<>();
    readPermittedGroupJson(groupPermitProperty.getValue(), loginGroupMap,
            emailGroupMap, studentNumberGroupMap);
    for(String[] permitInfo : addGroupList)
    {
      String addLoginName = permitInfo[0];
      String addEmail = permitInfo[1];
      String addStudentNumber = permitInfo[2];
      String groupName = permitInfo[3];
      if((addLoginName != null) && !addLoginName.isEmpty())
      {
        addLoginName = addLoginName.trim().toLowerCase();
        loginGroupMap.put(addLoginName, groupName);
      }
      if((addEmail != null) && !addEmail.isEmpty())
      {
        addEmail = addEmail.trim().toLowerCase();
        emailGroupMap.put(addEmail, groupName);
      }
      if((addStudentNumber != null) && !addStudentNumber.isEmpty())
      {
        addStudentNumber = addStudentNumber.trim().toLowerCase();
        studentNumberGroupMap.put(addStudentNumber, groupName);
      }
    }
    JsonObject jsonLoginGroupMap = new JsonObject();
    for(Map.Entry<String, String> entry : loginGroupMap.entrySet())
    {
      String login = entry.getKey();
      HashSet<Integer> loginSet = permittedLogins.get(login);
      if((loginSet != null) && loginSet.contains(masterTaskId))
      {
        String groupName = entry.getValue();
        jsonLoginGroupMap.addProperty(login, groupName);
      }
    }
    JsonObject jsonEmailGroupMap = new JsonObject();
    for(Map.Entry<String, String> entry : emailGroupMap.entrySet())
    {
      String email = entry.getKey();
      HashSet<Integer> emailSet = permittedEmails.get(email);
      if((emailSet != null) && emailSet.contains(masterTaskId))
      {
        String groupName = entry.getValue();
        jsonEmailGroupMap.addProperty(email, groupName);
      }
    }
    JsonObject jsonStudentNumberGroupMap = new JsonObject();
    for(Map.Entry<String, String> entry : studentNumberGroupMap.entrySet())
    {
      String studentNumber = entry.getKey();
      HashSet<Integer> studentNumberSet = permittedStudentNumbers.get(
              studentNumber);
      if((studentNumberSet != null) && studentNumberSet.contains(masterTaskId))
      {
        String groupName = entry.getValue();
        jsonStudentNumberGroupMap.addProperty(studentNumber, groupName);
      }
    }
    if((jsonLoginGroupMap.size() > 0) || (jsonEmailGroupMap.size() > 0)
            || (jsonStudentNumberGroupMap.size() > 0))
    {
      JsonObject rootJson = new JsonObject();
      rootJson.add("loginGroups", jsonLoginGroupMap);
      rootJson.add("emailGroups", jsonEmailGroupMap);
      rootJson.add("studentNumberGroups", jsonStudentNumberGroupMap);
      groupPermitProperty.setValue(rootJson.toString());
      if(doInsert)
      {
        groupPermitProperty.insert(masterConn);
      }
      else
      {
        groupPermitProperty.update(masterConn);
      }
    }
    else if(!doInsert)
    {
      groupPermitProperty.delete(masterConn);
    }
  }

  // Student rights are based on old Weto rules. These could be clarified:
  // which rights should students actually have?
  public static RightsCluster createMasterStudentCluster(Connection masterConn,
          Task masterTask)
          throws SQLException, InvalidValueException, ObjectNotValidException
  {
    RightsCluster result = new RightsCluster();
    result.setTaskId(masterTask.getId());
    result.setType(ClusterType.STUDENTS.getValue());
    int rights = 0;
    for(Tab t : Tab.values())
    {
      // Students have no view rights for STUDENTS, PERMISSIONS or TASK_DOCUMENTS.
      if((t != Tab.STUDENTS) && (t != Tab.PERMISSIONS) && (t
              != Tab.TASK_DOCUMENTS))
      {
        rights |= t.getBit();
      }
    }
    for(Component c : Component.values())
    {
      rights |= c.getBit();
    }
    result.setOwnerViewBits(rights);
    // Students may be able to create/update tags, grades and submissions.
    rights = (Tab.SUBMISSIONS.getBit() | Tab.GRADING.getBit() | Tab.FORUM
            .getBit() | Component.TAGS.getBit());
    result.setOwnerUpdateBits(rights);
    result.setOwnerCreateBits(rights);
    // Students by default cannot delete anything except their own submissions.
    result.setOwnerDeleteBits(Tab.SUBMISSIONS.getBit());
    // Students have no general rights.
    result.setGeneralViewBits(0);
    result.setGeneralUpdateBits(0);
    result.setGeneralCreateBits(0);
    result.setGeneralDeleteBits(0);
    result.insert(masterConn);
    return result;
  }

  public static RightsCluster createMasterAssistantCluster(Connection masterConn,
          Task masterTask)
          throws SQLException, InvalidValueException, ObjectNotValidException
  {
    RightsCluster result = new RightsCluster();
    result.setTaskId(masterTask.getId());
    result.setType(ClusterType.ASSISTANTS.getValue());
    // Assistants have all rights?
    int rights = WetoCourseAction.getAllRightsBits();
    result.setOwnerViewBits(rights);
    result.setOwnerUpdateBits(rights);
    result.setOwnerCreateBits(rights);
    result.setOwnerDeleteBits(rights);
    result.setGeneralViewBits(rights);
    result.setGeneralUpdateBits(0);
    result.setGeneralCreateBits(0);
    result.setGeneralDeleteBits(0);
    result.insert(masterConn);
    return result;
  }

  public static RightsCluster createMasterTeacherCluster(Connection masterConn,
          Task masterTask)
          throws SQLException, InvalidValueException, ObjectNotValidException
  {
    RightsCluster result = new RightsCluster();
    result.setTaskId(masterTask.getId());
    result.setType(ClusterType.TEACHERS.getValue());
    // Teachers have all rights.
    int rights = WetoCourseAction.getAllRightsBits();
    result.setOwnerViewBits(rights);
    result.setOwnerUpdateBits(rights);
    result.setOwnerCreateBits(rights);
    result.setOwnerDeleteBits(rights);
    result.setGeneralViewBits(rights);
    result.setGeneralUpdateBits(rights);
    result.setGeneralCreateBits(rights);
    result.setGeneralDeleteBits(rights);
    result.insert(masterConn);
    return result;
  }

  public static void createDefaultPermissions(Connection masterConn,
          Integer taskId, String startDate, String endDate)
          throws WetoActionException, InvalidValueException,
                 WetoTimeStampException, ObjectNotValidException, SQLException
  {
    Integer startStamp;
    Integer endStamp;
    try
    {
      startStamp = WetoTimeStamp.dateToStamp(startDate);
      endStamp = WetoTimeStamp.dateToStamp(endDate);
    }
    catch(NumberFormatException | WetoTimeStampException e)
    {
      throw new WetoActionException(WetoUtilities.getMessageResource(
              "error.illegalTime"));
    }
    Permission generalViewPermission = new Permission();
    generalViewPermission.setUserRefId(null);
    generalViewPermission.setUserRefType(PermissionRefType.USER.getValue());
    generalViewPermission.setTaskId(taskId);
    generalViewPermission.setType(PermissionType.VIEW.getValue());
    generalViewPermission.setStartDate(startStamp);
    generalViewPermission.setEndDate(endStamp);
    generalViewPermission.insert(masterConn);
  }

  public static void prepareCourseDatabase(Connection masterConn,
          Integer teacherUserId, Task task, Connection courseConn)
          throws ObjectNotValidException, SQLException, InvalidValueException,
                 NoSuchItemException, WetoActionException
  {
    // 1. Duplicate task to the course database.
    Integer masterTaskId = task.getId();
    task.insertAsRootTask(courseConn);
    Integer courseTaskId = task.getId();

    // 2. Duplicate clusters and cluster members to the course database.
    // Note: this also duplicates the teacher him/herself, as the teacher
    // should now already be in the course's master database teacher cluster.
    ArrayList<RightsCluster> masterClusters = RightsCluster.selectByTaskId(
            masterConn, masterTaskId);
    for(RightsCluster courseCluster : masterClusters)
    {
      Integer masterClusterId = courseCluster.getId();
      courseCluster.setTaskId(courseTaskId);
      courseCluster.insert(courseConn);
      Integer courseClusterId = courseCluster.getId();
      ClusterIdReplication cidr = new ClusterIdReplication();
      cidr.setMasterDbClusterId(masterClusterId);
      cidr.setCourseDbClusterId(courseClusterId);
      cidr.insert(courseConn);
      ArrayList<ClusterMember> members = ClusterMember.selectByClusterId(
              masterConn, masterClusterId);
      for(ClusterMember member : members)
      {
        UserIdReplication uidr;
        try
        {
          uidr = UserIdReplication.select1ByMasterDbUserId(courseConn, member
                  .getUserId());
        }
        catch(NoSuchItemException e)
        {
          UserAccount user = UserAccount.select1ById(masterConn, member
                  .getUserId());
          user.insert(courseConn);
          uidr = new UserIdReplication();
          uidr.setMasterDbUserId(member.getUserId());
          // Now user.getId() returns the id in course database.
          uidr.setCourseDbUserId(user.getId());
          uidr.insert(courseConn);
        }
        ClusterMember cm = new ClusterMember();
        cm.setClusterId(courseClusterId);
        cm.setUserId(uidr.getCourseDbUserId());
        cm.insert(courseConn);
      }
    }

    // 3. Copy permissions
    for(Permission permission : Permission.selectByTaskId(masterConn,
            masterTaskId))
    {
      Integer masterPermissionId = permission.getId();
      permission.setTaskId(courseTaskId);
      if(permission.getUserRefId() != null)
      {
        if(PermissionRefType.USER.getValue().equals(permission
                .getUserRefType()))
        {
          UserIdReplication uidr = UserIdReplication.select1ByMasterDbUserId(
                  courseConn, permission.getUserRefId());
          permission.setUserRefId(uidr.getCourseDbUserId());
        }
        else if(PermissionRefType.CLUSTER.getValue().equals(permission
                .getUserRefType()))
        {
          ClusterIdReplication cidr = ClusterIdReplication
                  .select1ByMasterDbClusterId(
                          courseConn, permission.getUserRefId());
          permission.setUserRefId(cidr.getCourseDbClusterId());
        }
        else
        {
          continue;
        }
      }
      else
      {
        permission.setUserRefId(null);
      }
      permission.insert(courseConn);
      PermissionIdReplication ridr = new PermissionIdReplication();
      ridr.setMasterDbPermissionId(masterPermissionId);
      ridr.setCourseDbPermissionId(permission.getId());
      ridr.insert(courseConn);
    }

    // 4. Insert teacher to course databasebe, if not already there.
    UserIdReplication uidr = null;
    try
    {
      uidr = UserIdReplication.select1ByMasterDbUserId(courseConn,
              teacherUserId);
      Teacher.select1ByUserId(courseConn, uidr.getCourseDbUserId());
    }
    catch(NoSuchItemException e)
    {
      if(uidr != null)
      {
        Teacher teacher = Teacher.select1ByUserId(masterConn, teacherUserId);
        teacher.setUserId(uidr.getCourseDbUserId());
        teacher.insert(courseConn);
      }
      else
      {
        throw new WetoActionException();
      }
    }
  }

}
