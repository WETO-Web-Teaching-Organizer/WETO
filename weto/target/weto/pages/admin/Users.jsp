<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <form action="<s:url action="addUser" />" method="post">
    <input type="submit" value="<s:text name="users.header.addUser" />" class="linkButton" />
  </form>
  <p><s:property value="getText('users.header.numberOfUsers', {usersSize})" escapeHtml="false" /></p>
  <s:if test="usersSize > 0" >
    <table class="tablesorter" id="usersTable">
      <thead>
        <tr>
          <th><s:text name="general.header.loginName" /></th>
          <th><s:text name="general.header.firstName" /></th>
          <th><s:text name="general.header.lastName" /></th>
          <th><s:text name="general.header.email" /></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <s:iterator var="user" value="users">
          <tr>
            <td>
              ${user.loginName}
            </td>
            <td>
              ${user.firstName}
            </td>
            <td>
              ${user.lastName}
            </td>
            <td>
              <a href="mailto:${user.email}">${user.email}</a>
            </td>
            <td>
              <form action="<s:url action="updateUser" />" method="post">
                <input type="hidden" name="loginName" value="<s:property value="#user.loginName" />" />
                <input type="submit" value="<s:text name="general.header.edit" />" class="linkButton" />
              </form>
            </td>
          </tr>
        </s:iterator>
      </tbody>
    </table>
  </s:if>
</div>
<script>
  $(function () {
    $("#usersTable").tablesorter({
      headers: {
        4: {
          sorter: false
        }
      },
      widgets: ['zebra']
    });
  });
</script>
