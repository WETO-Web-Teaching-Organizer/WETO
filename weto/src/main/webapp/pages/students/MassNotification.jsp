<%@ include file="/WEB-INF/taglibs.jsp"%>

<form action="<s:url action="sendMassNotifications"/>" method="POST">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />

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
                    <td><input type="checkbox" name="studentIDs" value="${student.studentNumberData}"/></td>
                </tr>
            </s:iterator>
            </tbody>
        </table>
    </div>
    <label>
        <s:text name="massNotification.label.message"/>
        <input type="text" name="notificationMessage">
        <input type="submit" value="<s:text name="massNotification.header.sendNotification"/>"/>
    </label>
</form>