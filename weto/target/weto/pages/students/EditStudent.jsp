<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="editStudent.title" /></h2>
<form action="<s:url action="editStudent" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="studentId" value="${student.userId}" />
  <input type="hidden" name="submitted" value="true" />
  <table>
    <tr>
      <td><s:text name="general.header.loginName" />:</td>
      <td>${student.loginName}</td>
    </tr>
    <tr>
      <td><s:text name="general.header.firstName" />:</td>
      <td><input type="text" name="student.firstName" value="${student.firstName}" size="30" /></td>
    </tr>
    <tr>
      <td><s:text name="general.header.lastName" />:</td>
      <td><input type="text" name="student.lastName" value="${student.lastName}" size="30" /></td>
    </tr>
    <tr>
      <td><s:text name="general.header.email" />:</td>
      <td><input type="text" name="student.email" value="${student.email}" size="30" /></td>
    </tr>
    <tr>
      <td><s:text name="general.header.studentNumber" />:</td>
      <td><input type="text" name="student.studentNumber" value="${student.studentNumber}" size="30" /></td>
    </tr>
  </table>
  <input type="submit" value="<s:text name="general.header.save" />" class="linkButton" />
</form>
