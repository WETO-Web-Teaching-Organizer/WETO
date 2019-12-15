<%@ include file="/WEB-INF/taglibs.jsp"%>
<form action="<s:url action="commitOrganizeSubtasks" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="subtaskJSON" value='${subtaskJSON}' />
  <s:text name="editTask.title.organizeSubtasks" />
  &nbsp;
  <input type="submit" value="<s:text name="general.header.saveChanges" />" class="linkButton" />
</form>
<div id="navHeader">
  ${task.name}
</div>
<ul class="treeview" id="organizedTree">
  ${organizedSubtaskTree}
</ul>
