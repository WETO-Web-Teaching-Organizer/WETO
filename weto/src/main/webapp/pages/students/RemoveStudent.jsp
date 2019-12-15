<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="removeStudent.title" /></h2>
<p><s:property value="%{getText('removeStudent.instructions', {student.lastName, student.firstName, courseName})}" escapeHtml="false" /></p>
<form action="<s:url action="removeStudent" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="studentId" value="${student.userId}" />
  <input type="hidden" name="submitted" value="true" />
  <input type="submit" value="<s:text name="general.header.confirm" />" class="linkButton" />
</form>
