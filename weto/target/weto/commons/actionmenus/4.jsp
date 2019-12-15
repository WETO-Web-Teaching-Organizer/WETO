<%@ include file="/WEB-INF/taglibs.jsp"%>
<li>
<s:url action="editSubmissionProperties" var="editSubmissionPropertiesUrl">
  <s:param name="taskId" value="taskId" />
  <s:param name="tabId" value="4" />
  <s:param name="dbId" value="dbId" />
</s:url> <s:a href="%{editSubmissionPropertiesUrl}">
  <s:text name="submissions.header.editProperties" />
</s:a>
</li>
<li>
<s:url action="viewAutoGrading" var="viewAutoGradingUrl">
  <s:param name="taskId" value="taskId" />
  <s:param name="tabId" value="4" />
  <s:param name="dbId" value="dbId" />
</s:url>
<s:a href="%{viewAutoGradingUrl}">
  <s:text name="submissions.header.editAutoGrading" />
</s:a>
</li>
