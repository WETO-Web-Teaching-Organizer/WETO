<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="students.length == 0">
  <p><s:text name="pendingstudents.message.noPending" /></p>
</s:if>
<s:else>
  <p><s:text name="pendingStudents.instructions" /></p>
  <form action="<s:url action="viewPendingStudents" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <table class="tablesorter" id="pendingStudentTable">
      <thead>
        <tr>
          <th><s:text name="general.header.lastName" /></th>
          <th><s:text name="general.header.firstName" /></th>
          <th><s:text name="general.header.loginName" /></th>
          <th><s:text name="general.header.email" /></th>
          <th><s:text name="general.header.group" /></th>
          <th><s:text name="general.header.selected" /></th>
        </tr>
      </thead>
      <tbody>
        <s:iterator value="students" var="student" status="status">
          <tr>
            <td>${student[0]}</td>
            <td>${student[1]}</td>
            <td>${student[2]}</td>
            <td><a href="mailto:${student[3]}">${student[3]}</a></td>
            <td>${student[4]}</td>
            <td>
              <input type="checkbox" name="acceptIds" value="${ids[status.index]}" />
            </td>
          </tr>
        </s:iterator>
      </tbody>
    </table>
    <input type="submit" name="action:acceptPending" value="<s:text name="pendingStudents.header.acceptSelected" />" class="linkButton" />
    <input type="submit" name="action:removePending" value="<s:text name="pendingStudents.header.removeSelected" />" class="linkButton" />
  </form>
  <script>
    $(function () {
      $("#pendingStudentTable").tablesorter({widgets: ['zebra']});
    });
  </script>
</s:else>