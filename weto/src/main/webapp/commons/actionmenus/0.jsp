<%@ include file="/WEB-INF/taglibs.jsp"%>
<%-- Task Settings --%>
<li>
  <s:url action="taskSettings" var="taskSettingsUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{taskSettingsUrl}">
    <s:text name="mainTab.header.settings" />
  </s:a>
</li>
<%-- Edit task / quiz --%>
<li>
  <s:url action="editTask" var="editTaskUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{editTaskUrl}">
    <s:text name="mainTab.header.editTask" />
  </s:a>
</li>
<li>
  <s:url action="revertTask" var="revertTaskUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{revertTaskUrl}">
    <s:text name="mainTab.header.revertTask" />
  </s:a>
</li>
<li>
  <s:url action="editQuiz" var="editQuizUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{editQuizUrl}">
    <s:text name="mainTab.header.editQuiz" />
  </s:a>
</li>
<%-- Create task--%>
<li>
  <s:url action="createTask" var="createTaskUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url> <s:a href="%{createTaskUrl}">
    <s:text name="mainTab.header.createTask" />
  </s:a>
</li>
<li>
  <s:url action="deleteTaskSubmissions" var="deleteTaskSubmissionsUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{deleteTaskSubmissionsUrl}">
    <s:text name="mainTab.header.deleteSubmissions" />
  </s:a>
</li>
<s:if test="!courseTask">
  <%-- Delete task --%>
  <li>
    <s:url action="deleteTask" var="deleteTaskUrl">
      <s:param name="taskId" value="taskId" />
      <s:param name="tabId" value="0" />
      <s:param name="dbId" value="dbId" />
    </s:url>
    <s:a href="%{deleteTaskUrl}">
      <s:text name="mainTab.header.deleteTask" />
    </s:a>
  </li>
</s:if>
<%-- Download/Upload course --%>
<li>
  <s:url action="downloadTask" var="downloadTaskUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{downloadTaskUrl}">
    <s:text name="mainTab.header.downloadTask" />
  </s:a>
</li>
<%-- Organize subtasks --%>
<li>
  <s:url action="organizeSubtasks" var="organizeSubtasksUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="0" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{organizeSubtasksUrl}">
    <s:text name="mainTab.header.organizeSubtasks" />
  </s:a>
</li>
