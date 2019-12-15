<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="deleteTask.title" /></h2>
<p><s:text name="deleteTask.text.confirmation" /></p>
<form action="<s:url action="commitDeleteTask" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="submit" value="<s:text name="general.header.delete" />" class="linkButton" />
</form>
<br />
<h3><s:text name="deleteTask.header.currentContents" />:</h3>
<script>
  function resizeIframe(iframe) {
    iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
  }
</script>
<div class="contentBox">
  <s:url action="viewTask" var="viewTaskUrl">
    <s:param name="taskId" value="%{taskId}" />
    <s:param name="tabId" value="#mainTabId" />
    <s:param name="dbId" value="%{dbId}" />
    <s:param name="answererId" value="%{navigator.user.id}" />
  </s:url>
  <iframe src="${viewTaskUrl}" width="100%" onload="resizeIframe(this)"></iframe>
</div>
