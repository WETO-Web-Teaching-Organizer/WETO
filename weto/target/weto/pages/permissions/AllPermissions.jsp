<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h2><s:text name="permissions.title.all" /></h2>
  <s:if test="noPermissions">
    <p><s:text name="permissions.message.noPermissions" /></p>
  </s:if>
  <s:else>
    <div class ="table-responsive">
      <table class="tablesorter">
        <thead>
          <tr>
            <th><s:text name="general.header.task" /></th>
            <th><s:text name="general.header.user" /></th>
            <th><s:text name="general.header.type" /></th>
            <th><s:text name="general.header.startDate" /></th>
            <th><s:text name="general.header.startTime" /></th>
            <th><s:text name="general.header.endDate" /></th>
            <th><s:text name="general.header.endTime" /></th>
            <th><s:text name="general.header.actions" /></th>
          </tr>
        </thead>
        <tbody>
          <s:iterator var="permissionTask" value="tasks" status="loop">
            <s:iterator value="%{users[#loop.index]}" status="loop2">
              <tr>
                <td>
                  <s:url action="viewTask" var="viewTaskUrl">
                    <s:param name="taskId" value="taskIds[#loop.index]" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                  </s:url>
                  <s:a href="%{viewTaskUrl}" name="task%{taskIds[#loop.index]}">
                    ${permissionTask}
                  </s:a>
                </td>
                <td>
                  <s:if test="users[#loop.index][#loop2.index] == null">
                    <s:text name="general.header.allUsers" />
                  </s:if>
                  <s:else>
                    <s:url action="viewStudent" var="viewStudentUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="studentId" value="%{users[#loop.index][#loop2.index].id}" />
                    </s:url>
                    <s:a href="%{viewStudentUrl}">
                      ${users[loop.index][loop2.index].lastName}, ${users[loop.index][loop2.index].firstName}
                    </s:a>
                    (${users[loop.index][loop2.index].loginName})
                  </s:else>
                </td>
                <td>
                  <s:text name="%{types[permissions[#loop.index][#loop2.index].type]}" />
                </td>
                <td>
                  ${permissions[loop.index][loop2.index].startDateString}
                </td>
                <td>
                  ${permissions[loop.index][loop2.index].startTime}
                </td>
                <td>
                  <s:if test="permissions[#loop.index][#loop2.index].endDate == null">
                    <s:text name="general.header.notAvailable" />
                  </s:if>
                  <s:else>
                    ${permissions[loop.index][loop2.index].endDateString}
                  </s:else>
                </td>
                <td>
                  <s:if test="permissions[#loop.index][#loop2.index].endTime == null">
                    <s:text name="general.header.notAvailable" />
                  </s:if>
                  <s:else>
                    ${permissions[loop.index][loop2.index].endTime}
                  </s:else>
                </td>
                <td>
                  <s:url action="editPermission" var="editPermissionUrl">
                    <s:param name="taskId" value="permissions[#loop.index][#loop2.index].taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="allViewTaskId" value="taskId" />
                    <s:param name="permissionId" value="permissions[#loop.index][#loop2.index].id" />
                  </s:url>
                  <s:a href="%{editPermissionUrl}" cssClass="linkButton">
                    <s:text name="general.header.edit" />
                  </s:a>
                  <s:url action="deletePermission" var="deletePermissionUrl">
                    <s:param name="taskId" value="permissions[#loop.index][#loop2.index].taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="allViewTaskId" value="taskId" />
                    <s:param name="permissionId" value="permissions[#loop.index][#loop2.index].id" />
                  </s:url>
                  &nbsp;&nbsp;
                  <s:a href="%{deletePermissionUrl}" cssClass="linkButton">
                    <s:text name="general.header.delete" />
                  </s:a>
                </td>
              </tr>
            </s:iterator>
          </s:iterator>
        </tbody>
      </table>
    </div>
  </s:else>
</div>
<script>
  $(function () {
    $(".tablesorter").tablesorter({
      // pass the headers argument and assing a object
      headers: {
        // assign the seventh column (we start counting zero)
        7: {
          // disable it by setting the property sorter to false
          sorter: false
        }
      },
      widgets: ['zebra']
    });
  });
</script>
