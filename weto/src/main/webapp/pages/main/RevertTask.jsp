<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="/wetoextra/mathjax/MathJax.js?config=TeX-AMS-MML_HTMLorMML"></script>
<script type="text/x-mathjax-config">
  MathJax.Hub.Config({
  "HTML-CSS": {
  minScaleAdjust: 80
  }
  });
</script>
<div class="contentBox">
  <h2>
    <s:text name="editTask.title.revert" />
  </h2>
  <form action="<s:url action="commitRevertTask" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <p>
      <s:text name="editTask.instructions.revert" />
    </p>
    <input type="submit" value="<s:text name="editTask.header.revert" />" class="linkButton" />
  </form>
</div>
${task.oldText}
<s:if test="!subtasks.isEmpty()">
  <h3>
    <s:text name="general.header.subtasks" />
  </h3>
  <ul>
    <s:iterator var="subtask" value="subtasks" status="stat">
      <li>
        <s:url action="viewTask" var="viewTaskUrl">
          <s:param name="taskId" value="#subtask.id" />
          <s:param name="tabId" value="#mainTabId" />
          <s:param name="dbId" value="dbId" />
        </s:url>
        <s:if test="#subtask.isHidden">
          <s:a href="%{viewTaskUrl}" class="hiddenFromStudents">
            ${subtask.name}
          </s:a>
        </s:if>
        <s:else>
          <s:a href="%{viewTaskUrl}">
            ${subtask.name}
          </s:a>
        </s:else>
        <s:if test="#subtask.showTextInParent && (#subtask.text != null)">
          <br>
          <small> ${subtask.text} </small>
        </s:if>
      </li>
    </s:iterator>
  </ul>
</s:if>
