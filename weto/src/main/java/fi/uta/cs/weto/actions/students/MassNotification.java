package fi.uta.cs.weto.actions.students;

import fi.uta.cs.weto.actions.students.ViewStudents;
import fi.uta.cs.weto.db.StudentView;
import fi.uta.cs.weto.model.WetoCourseAction;
import fi.uta.cs.weto.db.NotificationSetting;
import fi.uta.cs.weto.model.Tab;
import fi.uta.cs.weto.model.WetoActionException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class MassNotification {

    public class ViewMassNotification extends WetoCourseAction {
        private List<StudentView> students;

        public ViewMassNotification(int reqOwnerViewBits, int reqOwnerUpdateBits, int reqOwnerCreateBits, int reqOwnerDeleteBits) {
            super(reqOwnerViewBits, reqOwnerUpdateBits, reqOwnerCreateBits, reqOwnerDeleteBits);
        }

        public List<StudentView> getStudents() {
            return students;
        }

        @Override
        public String action() throws Exception {
            Connection courseConnection = getCourseConnection();
            int courseId = getCourseTaskId();
            int userId = getCourseUserId();

            ArrayList<StudentView> students = StudentView.selectByTaskId(courseConnection, courseId);
            return SUCCESS;
        }
    }
}
