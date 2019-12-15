<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h2><s:text name="deleteSubmissions.title" />: ${task.name}</h2>
  <p><s:text name="deleteSubmissions.text.confirmation" /></p>
  <form action="<s:url action="commitDeleteTaskSubmissions" />" method="post">
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="submit" value="<s:text name="general.header.delete" />" class="linkButton" />
  </form>
</div>
