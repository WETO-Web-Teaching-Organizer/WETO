<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="grading.title.calculateGrades" /></h2>
<p><s:text name="grading.instructions.calculateGrades" /></p>
<form action="<s:url action="calculateGrades" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="submitted" value="true" />
  <input type="submit"
         value="<s:text name="general.header.confirm" />" class="linkButton" />
</form>
