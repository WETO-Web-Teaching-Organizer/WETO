<%@ include file="/WEB-INF/taglibs.jsp"%>
<table>
  <tr>
    <td>
      <h2>
        <s:text name="grading.header.exportGrades" />
      </h2>
      <p>
        <s:text name="grading.instructions.export" />
      </p>
      <form action="<s:url action="exportGrades" />" method="post" target="_blank">
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <input type="hidden" name="submitted" value="true" />
        <table>
          <tr>
            <td><s:text name="general.header.name" /></td>
            <td><input type="checkbox" name="name" value="true" checked="checked" /></td>
          </tr>
          <tr>
            <td><s:text name="general.header.studentNumber" /></td>
            <td><input type="checkbox" name="studentNumber" value="true" checked="checked" /></td>
          </tr>
          <tr>
            <td><s:text name="general.header.loginName" /></td>
            <td><input type="checkbox" name="loginName" value="true" /></td>
          </tr>
          <tr>
            <td><s:text name="general.header.email" /></td>
            <td><input type="checkbox" name="email" value="true" /></td>
          </tr>
          <tr>
            <td><s:text name="general.header.groups" /></td>
            <td><input type="checkbox" name="groups" value="true" /></td>
          </tr>
          <tr>
            <td><s:text name="general.header.delimiter" /></td>
            <td><input type="text" name="delimiter" size="2" value=";" /></td>
          </tr>
          <tr>
            <td><s:text name="grading.header.excludeUnspecified" /></td>
            <td><input type="checkbox" name="excludeUnspecified" value="true" /></td>
          </tr>
          <tr>
            <td><s:text name="grading.header.excludeSubtasks" /></td>
            <td><input type="checkbox" name="excludeSubtasks" value="true" /></td>
          </tr>
          <tr>
            <td colspan="2">
              <input type="submit" value="<s:text name="general.header.export" />" class="linkButton" />
            </td>
          </tr>
        </table>
      </form>
    </td>
  </tr>
</table>
