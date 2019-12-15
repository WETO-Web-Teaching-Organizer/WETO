<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="pendingStudents">
  <s:url action="viewPendingStudents" var="pendingStudentsUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="1" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <a href="${pendingStudentsUrl}" class="btn btn-danger">
    <s:text name="students.header.pendingStudents" />
  </a>
</s:if>
<p>
  <s:property
    value="getText('students.header.numberOfStudents', {students.size()})"
    escapeHtml="false" />
</p>
<s:if test="!students.isEmpty()">
  <form action="<s:url action="updateStudentsGroups" />" method="post">
    <p>
      <input type="submit" value="<s:text name="general.header.saveChanges" />" class="linkButton" />
    </p>
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <div class="table-responsive">
      <table class="tablesorter table" id="studentTable">
        <thead>
          <tr>
            <th><s:text name="general.header.name" /></th>
            <th><s:text name="general.header.studentNumber" /></th>
            <th><s:text name="general.header.loginName" /></th>
            <th><s:text name="general.header.email" /></th>
            <th><s:text name="general.header.group" /></th>
            <th><s:text name="students.header.submissionGroup" /></th>
          </tr>
        </thead>
        <tbody class="studentTable-body">
          <s:iterator var="student" value="students" status="loop">
            <tr>
              <td>
                <s:url action="viewStudent" var="viewStudentUrl">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="studentId" value="%{#student.userId}" />
                </s:url>
                <s:a href="%{viewStudentUrl}" name="%{#student.userId}">
                  ${student.lastName}, ${student.firstName}
                </s:a>
                <s:if test="navigator.teacher">
                  &nbsp;
                  <s:url action="takeStudentRole" var="takeStudentRoleUrl">
                    <s:param name="taskId" value="%{taskId}" />
                    <s:param name="tabId" value="%{tabId}" />
                    <s:param name="dbId" value="%{dbId}" />
                    <s:param name="studentId" value="%{#student.userId}" />
                  </s:url>
                  <s:a href="%{takeStudentRoleUrl}" cssClass="smallLinkButton" cssStyle="float: right;">
                    <s:text name="students.header.takeRole" />
                  </s:a>
                </s:if>
              </td>
              <td><s:property value="#student.studentNumber" /></td>
              <td>${student.loginName}</td>
              <td><a href="mailto:${student.email}">${student.email}</a></td>
              <td>
                <input type="hidden" name="userIds" value="${student.userId}" />
                <s:if test="hasOwnGroups">
                  <select name="groupIds">
                    <option value="-1" <s:if test="%{groupMemberMap[#student.userId] == -1}">selected="selected"</s:if> >
                        -
                      </option>
                    <s:iterator var="group" value="groupList">
                      <option value="${group.id}" <s:if test="%{groupMemberMap[#student.userId] == #group.id}">selected="selected"</s:if> >
                        ${group.name}
                      </option>
                    </s:iterator>
                  </select>
                </s:if>
                <s:else>
                  ${groupMap[groupMemberMap[student.userId]]} (<i>inherited</i>)
                </s:else>
              </td>
              <td><input type="text" name="submitGroups" value="${submitterGroups[loop.index]}" size="25"></td>
            </tr>
          </s:iterator>
        </tbody>
      </table>
    </div>
    <p>
      <input type="submit" value="<s:text name="general.header.saveChanges" />" class="linkButton" />
    </p>
  </form>
</s:if>
<script>
  $(function () {
    $("#studentTable").tablesorter({
      widgets: ['zebra'],
      cancelSelection: true,
      sortList: [[0, 0]],
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
