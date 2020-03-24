<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="tabs.size() > 1">
  <ul id="tabnav" class="tabnav-sticky" style="display: none">
    <s:iterator var="tab" value="tabs">
      <s:if test='%{#tab.value == tabId}'>
        <li class="active">
        </s:if>
        <s:else>
        <li>
        </s:else>
        <s:if test="anchorId != null">
          <s:url action="viewTask" var="viewTaskUrl" anchor="%{anchorId}">
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{value}" />
            <s:param name="dbId" value="%{dbId}" />
          </s:url>
        </s:if>
        <s:else>
          <s:url action="viewTask" var="viewTaskUrl">
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{value}" />
            <s:param name="dbId" value="%{dbId}" />
          </s:url>
        </s:else>
        <s:a href="%{viewTaskUrl}">
          <s:if test="hasToolMenu && navigator.teacher">
            <span class="tabmenu-link-plus">
              <s:text name="%{#tab.property}" />
            </span>
            <span class="glyphicon glyphicon-plus"></span>
          </s:if>
          <s:else>
            <span class="tabmenu-link">
              <s:text name="%{#tab.property}" />
            </span>
          </s:else>
        </s:a>
        <!-- If tab has action menu, print it as a dropdown -->
        <s:if test="hasToolMenu && navigator.teacher">
          <ul class="actionmenu">
            <tiles:insertTemplate template="/commons/actionmenus/${tab.value}.jsp" />
          </ul>
        </s:if>
      </li>
    </s:iterator>
  </ul>
</s:if>