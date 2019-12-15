<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h2><s:text name="permissions.title" /></h2>
  <div class="contentBox">
    <div class="row">
      <p class="col-xs-12 col-md-4">
        <s:url action="editPermission" var="editPermissionUrl">
          <s:param name="taskId" value="taskId" />
          <s:param name="tabId" value="tabId" />
          <s:param name="dbId" value="dbId" />
        </s:url>
        <s:a href="%{editPermissionUrl}" cssClass="linkButton">
          <s:text name="permissions.header.addPermission" />
        </s:a>
      </p>
      <div class ="col-xs-12 col-md-8">
        <form action="<s:url action="viewAllPermissions" />" method="post">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <input type="submit" value="<s:text name="permissions.header.viewAll" />" class="linkButton" />
        </form>
      </div>
    </div>
    <s:if test="noPermissions">
      <p><s:text name="permissions.message.noPermissions" /></p>
    </s:if>
    <s:else>
      <div class="table-responsive">
        <table class="tablesorter table" id="permissionTable">
          <thead>
            <tr>
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
            <s:iterator var="permission" value="allUsersPermissions">
              <tr>
                <td>
                  <s:text name="general.header.allUsers" />
                </td>
                <td>
                  <s:text name="%{types[#permission.type]}" />
                </td>
                <td>
                  ${permission.startDateString}
                </td>
                <td>
                  ${permission.startTime}
                </td>
                <td>
                  <s:if test="#permission.endDate == null">
                    <s:text name="general.header.notAvailable" />
                  </s:if>
                  <s:else>
                    ${permission.endDateString}
                  </s:else>
                </td>
                <td>
                  <s:if test="#permission.endTime == null">
                    <s:text name="general.header.notAvailable" />
                  </s:if>
                  <s:else>
                    ${permission.endTime}
                  </s:else>
                </td>
                <td>
                  <s:url action="editPermission" var="editPermissionUrl">
                    <s:param name="taskId" value="taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="permissionId" value="#permission.id" />
                  </s:url>
                  <s:a href="%{editPermissionUrl}" cssClass="linkButton">
                    <s:text name="general.header.edit" />
                  </s:a>
                  <s:url action="deletePermission" var="deletePermissionUrl">
                    <s:param name="taskId" value="taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="permissionId" value="#permission.id" />
                  </s:url>
                  &nbsp;&nbsp;
                  <s:a href="%{deletePermissionUrl}" cssClass="linkButton">
                    <s:text name="general.header.delete" />
                  </s:a>
                </td>
              </tr>
            </s:iterator>
            <s:iterator value="removedUsersPermissions">
              <s:iterator var="permission" value="value">
                <tr>
                  <td>
                    <s:url action="viewStudent" var="viewStudentUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="studentId" value="%{key.userId}" />
                    </s:url>
                    <s:a href="%{viewStudentUrl}" name="%{key.userId}">
                      <i>${key.lastName}, ${key.firstName}</i>
                    </s:a>
                    <i>(${key.loginName})</i>
                  </td>
                  <td>
                    <s:text name="%{types[#permission.type]}" />
                  </td>
                  <td>
                    ${permission.startDateString}
                  </td>
                  <td>
                    ${permission.startTime}
                  </td>
                  <td>
                    <s:if test="#permission.endDate == null">
                      <s:text name="general.header.notAvailable" />
                    </s:if>
                    <s:else>
                      ${permission.endDateString}
                    </s:else>
                  </td>
                  <td>
                    <s:if test="#permission.endTime == null">
                      <s:text name="general.header.notAvailable" />
                    </s:if>
                    <s:else>
                      ${permission.endTime}
                    </s:else>
                  </td>
                  <td>
                    <s:url action="editPermission" var="editPermissionUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="permissionId" value="#permission.id" />
                    </s:url>
                    <s:a href="%{editPermissionUrl}" cssClass="linkButton">
                      <s:text name="general.header.edit" />
                    </s:a>
                    <s:url action="deletePermission" var="deletePermissionUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="permissionId" value="#permission.id" />
                    </s:url>
                    &nbsp;&nbsp;
                    <s:a href="%{deletePermissionUrl}" cssClass="linkButton">
                      <s:text name="general.header.delete" />
                    </s:a>
                  </td>
                </tr>
              </s:iterator>
            </s:iterator>
            <s:iterator var="user" value="users">
              <s:iterator var="permission" value="usersPermissions[#user.userId]">
                <tr>
                  <td>
                    <s:url action="viewStudent" var="viewStudentUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="studentId" value="%{#user.userId}" />
                    </s:url>
                    <s:a href="%{viewStudentUrl}" name="%{#user.userId}">
                      ${user.lastName}, ${user.firstName}
                    </s:a>
                    (${user.loginName})
                  </td>
                  <td>
                    <s:text name="%{types[#permission.type]}" />
                  </td>
                  <td>
                    ${permission.startDateString}
                  </td>
                  <td>
                    ${permission.startTime}
                  </td>
                  <td>
                    <s:if test="#permission.endDate == null">
                      <s:text name="general.header.notAvailable" />
                    </s:if>
                    <s:else>
                      ${permission.endDateString}
                    </s:else>
                  </td>
                  <td>
                    <s:if test="#permission.endTime == null">
                      <s:text name="general.header.notAvailable" />
                    </s:if>
                    <s:else>
                      ${permission.endTime}
                    </s:else>
                  </td>
                  <td>
                    <s:url action="editPermission" var="editPermissionUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="permissionId" value="#permission.id" />
                    </s:url>
                    <s:a href="%{editPermissionUrl}" cssClass="linkButton">
                      <s:text name="general.header.edit" />
                    </s:a>
                    <s:url action="deletePermission" var="deletePermissionUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="permissionId" value="#permission.id" />
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
</div>
<script>
  $(function () {
    $("#permissionTable").tablesorter({
      headers: {
        6: {// Disable sorting for the 7th column (whose index is 6)
          sorter: false
        }
      },
      widgets: ['zebra']
    });
  });
</script>
