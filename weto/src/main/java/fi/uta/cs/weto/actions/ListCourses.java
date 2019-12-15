package fi.uta.cs.weto.actions;

import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.weto.db.CourseView;
import fi.uta.cs.weto.db.News;
import fi.uta.cs.weto.db.Student;
import fi.uta.cs.weto.db.Subject;
import fi.uta.cs.weto.db.Task;
import fi.uta.cs.weto.db.UserAccount;
import fi.uta.cs.weto.db.UserTaskView;
import fi.uta.cs.weto.model.ClusterType;
import fi.uta.cs.weto.model.CourseMemberModel;
import fi.uta.cs.weto.model.PermissionModel;
import fi.uta.cs.weto.model.PermissionType;
import fi.uta.cs.weto.model.WetoMasterAction;
import fi.uta.cs.weto.model.WetoTimeStamp;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This action gathers all courses user(if one is logged in) is registered to
 * and all the other courses that the user hasn't registered to and groups them
 * by subject.
 */
public class ListCourses extends WetoMasterAction
{
  private final Map<Integer, ArrayList<CourseView>> courses = new HashMap<>();
  private final Map<Integer, ArrayList<CourseView>> registeredCourses
                                                            = new HashMap<>();
  private final ArrayList<CourseView> inactiveCourses = new ArrayList<>();
  private final Map<Integer, String[]> viewPeriods = new HashMap<>();
  private final Map<Integer, String[]> registerPeriods = new HashMap<>();
  private ArrayList<Subject> subjects;
  private ArrayList<News> news;
  private static final boolean DESC = false;
  private static final boolean ASC = true;

  private static class CourseNameCmp implements Comparator<CourseView>
  {
    private final boolean isAscending;

    public CourseNameCmp(boolean isAscending)
    {
      this.isAscending = isAscending;
    }

    @Override
    public int compare(CourseView a, CourseView b)
    {
      return (isAscending) ? a.getName().compareTo(b.getName()) : b.getName()
              .compareTo(a.getName());
    }

  };

  public ListCourses()
  {
    super(true);
  }

  @Override
  public String action() throws Exception
  {
    Integer masterUserId = getMasterUserId();
    Connection masterConn = getMasterConnection();
    HashSet<Integer> permittedCourses = null;
    if(masterUserId != null)
    {
      UserAccount masterUser = getNavigator().getRealUser();
      String studentNumber = null;
      try
      {
        studentNumber = Student.select1ByUserId(masterConn, masterUserId)
                .getStudentNumber();
      }
      catch(NoSuchItemException e)
      {
      }
      permittedCourses = CourseMemberModel.getPermittedCourses(masterConn,
              masterUser, studentNumber);
    }
    else
    {
      permittedCourses = new HashSet<>();
    }
    for(CourseView course : CourseView.selectAll(masterConn))
    {
      Integer courseMasterTaskId = course.getMasterTaskId();
      Integer courseSubjectId = course.getSubjectId();
      Task courseTask = Task.select1ById(masterConn, courseMasterTaskId);
      boolean isHidden = courseTask.getIsHidden();
      boolean isPublic = courseTask.getIsPublic();
      boolean isStudent = false;
      boolean isShown = false;
      WetoTimeStamp[] viewPeriod = PermissionModel.getTaskTimeStampLimits(
              masterConn, masterUserId, courseMasterTaskId, PermissionType.VIEW,
              false);
      WetoTimeStamp[] registerPeriod = PermissionModel.getTaskTimeStampLimits(
              masterConn, masterUserId, courseMasterTaskId,
              PermissionType.REGISTER, false);
      // If student has registered to course, insert the course to
      // registeredCourses and if not, insert into default courses
      try
      {
        UserTaskView utv = UserTaskView.select1ByTaskIdAndUserId(masterConn,
                courseMasterTaskId, masterUserId);
        isStudent = ClusterType.STUDENTS.getValue().equals(utv.getClusterType());
        if(!(isHidden && isStudent) && (PermissionModel.checkTimeStampLimits(
                viewPeriod) == PermissionModel.CURRENT))
        {
          ArrayList<CourseView> subjectCourses = registeredCourses.get(
                  courseSubjectId);
          if(subjectCourses == null)
          {
            subjectCourses = new ArrayList<>();
            registeredCourses.put(courseSubjectId, subjectCourses);
          }
          //Add the course to subjectCourses that is inside registeredCourses.
          subjectCourses.add(course);
          isShown = true;
        }
        else if(!isStudent)
        {
          inactiveCourses.add(course);
          isShown = true;
        }
      }
      catch(NoSuchItemException e) // User is not a course member
      {
        if((PermissionModel.checkTimeStampLimits(viewPeriod)
                == PermissionModel.CURRENT) && ((PermissionModel
                .checkTimeStampLimits(registerPeriod) == PermissionModel.CURRENT)
                || permittedCourses.contains(courseMasterTaskId) || isPublic))
        {
          ArrayList<CourseView> subjectCourses = courses.get(courseSubjectId);
          if(subjectCourses == null)
          {
            subjectCourses = new ArrayList<>();
            courses.put(courseSubjectId, subjectCourses);
          }
          //Add the course to subjectCourses that is inside courses.
          subjectCourses.add(course);
          isShown = true;
        }
      }
      if(isShown)
      {
        WetoTimeStamp a = viewPeriod[0];
        WetoTimeStamp b = viewPeriod[1];
        viewPeriods.put(courseMasterTaskId, new String[]
        {
          ((a == null) || (a.getTimeStamp().equals(WetoTimeStamp.STAMP_MIN)))
          ? ""
          : a.toString(),
          (b == null) ? getText("general.header.forever") : (b.getTimeStamp()
          .equals(WetoTimeStamp.STAMP_MIN) ? getText("general.header.never") : b
          .toString())
        });
        a = registerPeriod[0];
        b = registerPeriod[1];
        registerPeriods.put(courseMasterTaskId, new String[]
        {
          ((a == null) || (a.getTimeStamp().equals(WetoTimeStamp.STAMP_MIN)))
          ? ""
          : a.toString(),
          (b == null) ? getText("general.header.forever") : (b.getTimeStamp()
          .equals(WetoTimeStamp.STAMP_MIN) ? getText("general.header.never") : b
          .toString())
        });
      }
    }
    for(Map.Entry<Integer, ArrayList<CourseView>> e : courses.entrySet())
    {
      Collections.sort(e.getValue(), new CourseNameCmp(ASC));
    }
    for(Map.Entry<Integer, ArrayList<CourseView>> e : registeredCourses
            .entrySet())
    {
      Collections.sort(e.getValue(), new CourseNameCmp(ASC));
    }
    Collections.sort(inactiveCourses, new CourseNameCmp(DESC));
    subjects = Subject.selectAll(masterConn);
    news = News.selectActive(masterConn);
    Collections.sort(news, new Comparator<News>()
    {
      @Override
      public int compare(News a, News b)
      {
        return b.getStartDate() - a.getStartDate();
      }

    });
    return SUCCESS;
  }

  public Map<Integer, ArrayList<CourseView>> getCourses()
  {
    return courses;
  }

  public Map<Integer, ArrayList<CourseView>> getRegisteredCourses()
  {
    return registeredCourses;
  }

  public ArrayList<CourseView> getInactiveCourses()
  {
    return inactiveCourses;
  }

  public Map<Integer, String[]> getViewPeriods()
  {
    return viewPeriods;
  }

  public Map<Integer, String[]> getRegisterPeriods()
  {
    return registerPeriods;
  }

  public ArrayList<Subject> getSubjects()
  {
    return subjects;
  }

  public ArrayList<News> getNews()
  {
    return news;
  }

}
