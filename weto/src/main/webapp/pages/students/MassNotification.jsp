<%@ include file="/WEB-INF/taglibs.jsp"%>


<form id="massNotificationForm" action="<s:url action="sendMassNotifications"/>" method="POST">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />

    <s:if test="!students.isEmpty()">
        <h2><s:text name="massNotification.header.sendMassNotifications" /></h2>
        <div class="table-responsive">
            <table class="tablesorter table" id="studentTable">
                <thead>
                <tr>
                    <th><s:text name="general.header.name" /></th>
                    <th><s:text name="general.header.studentNumber" /></th>
                    <th><s:text name="general.header.loginName" /></th>
                    <th><s:text name="general.header.email" /></th>
                    <th><s:text name="general.header.selected"/></th>
                </tr>
                </thead>
                <tbody class="studentTable-body">
                <s:iterator var="student" value="%{students}" status="loop">
                    <tr>
                        <td>
                            <s:url action="viewStudent" var="viewStudentUrl">
                                <s:param name="taskId" value="%{taskId}" />
                                <s:param name="tabId" value="%{tabId}" />
                                <s:param name="dbId" value="%{dbId}" />
                                <s:param name="studentId" value="%{#student.userId}" />
                            </s:url>
                            <s:a href="%{viewStudentUrl}" name="%{#student.userId}">
                                ${student.lastName}, ${student.firstName}
                            </s:a>
                        </td>
                        <td><s:property value="#student.studentNumber" /></td>
                        <td>${student.loginName}</td>
                        <td><a href="mailto:${student.email}">${student.email}</a></td>
                        <td><input type="checkbox" class="studentIdCheckbox" name="studentIDs" value="${student.studentNumberData}"/></td>
                    </tr>
                </s:iterator>
                </tbody>
            </table>
        </div>
    </s:if>
    <s:else>
        <p>Student list is empty.</p>
    </s:else>

    <div>
        <div class="selectionButtons">
            <button type="button" id="selectAll"><s:text name="massNotification.button.selectAll" /></button>
            <button type="button" id="deselectAll"><s:text name="massNotification.button.deselectAll" /></button>
        </div>
        <br><br>
        <label for="notificationMessage"><s:text name="massNotification.label.message"/></label>
        <input type="text" id="notificationMessage" class="form-control" name="notificationMessage">
        <input type="submit" class="linkButton" value="<s:text name="massNotification.header.sendNotification"/>"
               style="margin: 0.5em 0 0 0;" onclick="return confirm('Are you sure?')"/>
    </div>
    <script>
        $(document).ready(function() {
            $("#selectAll").click(function(event) {
                event.preventDefault();
                $(".studentIdCheckbox").prop("checked", true);
            });
            $("#deselectAll").click(function(event) {
                event.preventDefault();
                $(".studentIdCheckbox").prop("checked", false);
            });
        });
    </script>
</form>
