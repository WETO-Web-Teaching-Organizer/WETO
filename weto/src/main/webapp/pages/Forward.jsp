<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:text name="login.instructions.forward" />
<s:url action="viewTask" var="forwardUrl">
  <s:param name="taskId" value="%{orig_action_pars.taskId[0]}" />
  <s:param name="tabId" value="%{orig_action_pars.tabId[0]}" />
  <s:param name="dbId" value="%{orig_action_pars.dbId[0]}" />
</s:url>
<form action="${forwardUrl}" id="forward_form" method="post">
  <c:forEach var="onepar" items="${orig_action_pars}">
    <c:if test="${(onepar.key ne 'username') && (onepar.key ne 'password') && (onepar.key ne 'taskId') && (onepar.key ne 'tabId') && (onepar.key ne 'dbId')}">
      <c:forEach var="oneval" items="${onepar.value}">
        <input type="hidden" name="${onepar.key}" value="${oneval}" />
      </c:forEach>
    </c:if>
  </c:forEach>
  <input type="submit" value="<s:text name="general.header.forward" />" class="linkButton" />
</form>
<script type="text/javascript">
  document.getElementById("forward_form").submit();
</script>