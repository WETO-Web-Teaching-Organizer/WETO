package fi.uta.cs.weto.actions.students;

import fi.uta.cs.weto.actions.students.ViewStudents;
import fi.uta.cs.weto.db.*;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MassNotification {

    public static class ViewMassNotification extends WetoCourseAction {
        public List<StudentView> students;

        public ViewMassNotification(int reqOwnerViewBits, int reqOwnerUpdateBits, int reqOwnerCreateBits, int reqOwnerDeleteBits) {
            super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits, reqOwnerDeleteBits);
        }
        public ViewMassNotification() {
            super(Tab.MAIN.getBit(), 0, 0, 0);
            students = null;
        }

        public List<StudentView> getStudents() {
            return students;
        }

        public void setStudents(List<StudentView> students) {
            this.students = students;
        }

        @Override
        public String action() throws Exception {
            Connection courseConnection = getCourseConnection();
            int courseId = getCourseTaskId();
            int userId = getCourseUserId();

            try{
                students = StudentView.selectByTaskId(courseConnection, courseId);
            }
            catch (SQLException e){
                throw new WetoActionException("Failed to retrieve students.");
            }
            return SUCCESS;
        }
    }
    public static class SendMassNotification extends WetoCourseAction {

        private String notificationMessage;
        private List<String> studentIDs;

        public String getNotificationMessage() {
            return notificationMessage;
        }

        public void setNotificationMessage(String notificationMessage) {
            this.notificationMessage = notificationMessage;
        }

        public List<String> getStudentIDs() {
            return studentIDs;
        }

        public void setStudentIDs(List<String> studentIDs) {
            this.studentIDs = studentIDs;
        }


        @Override
        public String action() throws Exception {
            Connection courseConnection = getCourseConnection();
            Connection masterConnection = getMasterConnection();
            try{
                CourseImplementation masterCourse = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterConnection, getDbId(), getCourseTaskId());
                for(String studentID : studentIDs){
                    Student student = Student.select1ByStudentNumber(courseConnection, studentID);

                    UserIdReplication uIDr = UserIdReplication.select1ByCourseDbUserId(courseConnection,
                            student.getUserId());
                    Integer masterUserId = uIDr.getMasterDbUserId();

                    UserAccount masterUserAccount = UserAccount.select1ById(masterConnection, masterUserId);
                    Notification newNotification = new Notification(masterUserAccount.getId(), masterCourse.getMasterTaskId(), Notification.MASS_NOTIFICATION, null);
                        if(notificationMessage.isEmpty()) {
                            throw new WetoActionException("Failed to send notifications, please add text to notification.");
                        }
                        newNotification.setMessage(notificationMessage);
                        newNotification.createNotification(masterConnection, courseConnection);
                }
            }
            catch(WetoActionException e){
                throw e;
            }
            catch (Exception e){
                throw new WetoActionException("Failed to send notifications, please select students to send notifications.");
            }
            return SUCCESS;
        }

        public SendMassNotification(int reqOwnerViewBits, int reqOwnerUpdateBits, int reqOwnerCreateBits, int reqOwnerDeleteBits) {
            super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits, reqOwnerDeleteBits);
        }

        public SendMassNotification() {
            super(Tab.MAIN.getBit(), 0, 0, 0);
        }
    }
}
