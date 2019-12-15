<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="!navigator.teacher">
  <s:if test="!(groupList.empty && parentGroupList.empty)">
    <h4>
      <s:text name="groups.title.studentGroups" />
    </h4>
    <table class="tablesorter">
      <thead>
        <tr>
          <th><s:text name="general.header.name" /></th>
          <th><s:text name="general.header.type" /></th>
          <th><s:text name="groups.header.members"/></th>
        </tr>
      </thead>
      <tbody>
        <s:if test="!parentGroupList.empty">
          <s:iterator var="entry" value="parentGroupList">
            <tr>
              <td>${entry.name}</td>
              <td>${typeNameList[entry.typeId]}</td>
              <td>${entry.size}</td>
            </tr>
          </s:iterator>
        </s:if>
        <s:else>
          <s:iterator var="entry" value="groupList">
            <tr>
              <td>${entry.name}</td>
              <td>${typeNameList[entry.typeId]}</td>
              <td>${entry.size}</td>
            </tr>
          </s:iterator>
        </s:else>
      </tbody>
    </table>
  </s:if>
  <s:else>
    <p><s:text name="groups.header.belongToNone" /></p>
  </s:else>
</s:if>
<s:else>
  <div class="contentBox">
    <s:if test="!parentGroupList.empty">
      <p>
        <s:property value="getText('groups.title.parent', {parentGroupList.size(), parentTaskName})" escapeHtml="false" />
      </p>
      <table class="tablesorter gradeTable">
        <thead>
          <tr>
            <th><s:text name="general.header.name" /></th>
            <th><s:text name="general.header.type" /></th>
            <th><s:text name="groups.header.members"/></th>
          </tr>
        </thead>
        <tbody>
          <s:iterator var="entry" value="parentGroupList">
            <tr>
              <td>${entry.name}</td>
              <td>${typeNameList[entry.typeId]}</td>
              <td>${entry.size}</td>
            </tr>
          </s:iterator>
        </tbody>
      </table>
    </s:if>
    <s:else>
      <h4>
        <s:property value="getText('groups.header.numberOfGroups', {groupList.size()})" escapeHtml="false" />
      </h4>
      <s:if test="!groupList.empty">
        <form action="<s:url action="updateGroups" />" method="post">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <table class="tablesorter gradeTable" id="groupTable">
            <thead>
              <tr>
                <th></th>
                <th><s:text name="general.header.name" /></th>
                <th><s:text name="general.header.type" /></th>
                <th><s:text name="groups.header.members"/></th>
              </tr>
            </thead>
            <tbody>
              <s:iterator var="entry" value="groupList">
                <tr>
                  <td>
                    <input type="hidden" name="oldGroupIds" value="${entry.id}" />
                    <input type="checkbox" name="selectedGroups" value="${entry.id}" />
                  </td>
                  <td>
                    <input type="text" name="groupNames" size="50" value="${entry.name}" />
                  </td>
                  <td>
                    <s:if test="!typeList[#entry.typeId].implicit">
                      <select name="groupTypes">
                        <s:iterator var="type" value="typeNameList" status="loop">
                          <s:if test="!typeList[#loop.index].implicit">
                            <s:if test="#loop.index == #entry.typeId">
                              <option value="${loop.index}" selected="selected">
                                ${type}
                              </option>
                            </s:if>
                            <s:else>
                              <option value="${loop.index}">
                                ${type}
                              </option>
                            </s:else>
                          </s:if>
                        </s:iterator>
                      </select>
                    </s:if>
                    <s:else>
                      <input type="hidden" name="groupTypes" value="${entry.typeId}" />
                      ${typeNameList[entry.typeId]}
                    </s:else>
                  </td>
                  <td>${entry.size}</td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
          <s:if test="groupList != null && groupList.size() > 0">
            <p>
              <button type="submit" name="doUpdate" value="true" class="linkButton"><s:text name="general.header.saveChanges" /></button>
              <button type="submit" name="doDelete" value="true" class="linkButton"><s:text name="general.header.deleteSelected" /></button>
            </p>
          </s:if>
        </form>
        <script>
          $(function () {
            $("#groupTable").tablesorter({
              widgets: ['zebra'],
              cancelSelection: true,
              sortList: [[1, 0]],
              headers: {
                0: {sorter: false},
                3: {sorter: false}
              },
              textExtraction: function (node) {
                var selected = $(node).find('option:selected');
                // Check if this cell has an option, take selected one
                if (selected.length) {
                  var selText = selected.text();
                  if (selText !== "") {
                    return selText;
                  }
                } else {  // Check if this is an input field with a value
                  var input = $(node).find('input[value]');
                  if (input.length) {
                    return input.attr('value');
                  } else {  // Check if there is a link
                    var link = $(node).find('a');
                    if (link.length) {
                      return link.first().text();
                    }
                  }
                }
                // Otherwise return the element text as it is
                return node.innerHTML;
              }
            });
          });
        </script>
      </s:if>
    </s:else>
  </div>
  <div class="contentBox">
    <div class="peerreview-border-box">
      <p>
        <s:text name="groups.instructions.submissionGroup" />
      </p>
    </div>
    <h4><s:text name="groups.header.add" /></h4>
    <form action="<s:url action="addGroup" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <table class="tablesorter gradeTable">
        <thead>
          <tr>
            <th><s:text name="general.header.name" /></th>
            <th><s:text name="general.header.type" /></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td><input type="text" name="groupName" size="50" /></td>
            <td>
              <select name="groupType">
                <s:iterator var="type" value="typeNameList" status="loop">
                  <s:if test="!typeList[#loop.index].implicit">
                    <s:if test="#loop.index == 0">
                      <option value="${loop.index}" selected="selected">
                        ${type}
                      </option>
                    </s:if>
                    <s:else>
                      <option value="${loop.index}">
                        ${type}
                      </option>
                    </s:else>
                  </s:if>
                </s:iterator>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
      <p>
        <input type="submit" value="<s:text name="general.header.add" />" class="linkButton" />
      </p>
    </form>
  </div>
</s:else>
