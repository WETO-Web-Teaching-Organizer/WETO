package fi.uta.cs.weto.actions.students;

import fi.uta.cs.weto.actions.students.ViewStudents;
import fi.uta.cs.weto.db.*;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;

import java.sql.Connection;
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

            students = StudentView.selectByTaskId(courseConnection, courseId);
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
            CourseImplementation masterCourse = CourseImplementation.select1ByDatabaseIdAndCourseTaskId(masterConnection, getDbId(), getCourseTaskId());
            for(String studentID : studentIDs){
                Student student = Student.select1ByStudentNumber(courseConnection, studentID);
                UserAccount masterUserAccount = UserAccount.select1ById(masterConnection, student.getUserId());
                Notification newNotification = new Notification(masterUserAccount.getId(), masterCourse.getMasterTaskId(), Notification.MASS_NOTIFICATION, null);
                newNotification.setMessage(notificationMessage);
                newNotification.createNotification(masterConnection, courseConnection);
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
