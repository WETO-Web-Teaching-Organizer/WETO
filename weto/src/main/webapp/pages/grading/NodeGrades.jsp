<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="resultsPeriod[0] != null || resultsPeriod[1] != null">
  <h2>
    <s:text name="grading.header.resultsPeriod" />
  </h2>
  <p>
    <weto:timePeriod starting="${resultsPeriod[0]}" ending="${resultsPeriod[1]}" />
  </p>
</s:if>
<s:if test="!gradeTable.isEmpty()">
  <h3>
    <s:text name="grading.title.gradeTable" />
  </h3>
  <table class="dataTable">
    <tr>
      <th>
        <s:text name="general.header.score" />
      </th>
      <s:iterator var="entry" value="gradeTable">
        <td><s:property value="#entry[0]" /></td>
      </s:iterator>
    </tr>
    <tr>
      <th>
        <s:text name="general.header.mark" />
      </th>
      <s:iterator var="entry" value="gradeTable">
        <td>
          <s:if test="#entry[1] != null && !#entry[1].isEmpty()">
            ${entry[1]}
          </s:if>
          <s:else>
            <s:text name="grading.header.failed"/>
          </s:else>
        </td>
      </s:iterator>
    </tr>
  </table>
</s:if>
<h2><s:text name="grading.title.grades" /></h2>

<s:if test="navigator.teacher">
  <table>
    <tr>
      <td>
        <div class="contentBox">
          <s:url action="viewNodeGrades" var="viewNodeGradesUrl">
            <s:param name="taskId" value="taskId" />
            <s:param name="tabId" value="tabId" />
            <s:param name="dbId" value="dbId" />
          </s:url>
          <form action="${viewNodeGradesUrl}" method="post">
            <input type="submit" value="<s:text name="groups.header.filter" />" class="linkButton" />
            <select name="filterGroupId">
              <s:iterator var="group" value="groupList">
                <option value="${key}" <s:if test="%{filterGroupId == key}">selected="selected"</s:if> >
                  ${value}
                </option>
              </s:iterator>
            </select>
          </form>
        </div>
      </td>
      <td>
        <button class="btn btn-default-small" onclick="$('#gradeTable').find('.unspecifiedGrade').toggle()"><s:text name="grading.header.toggleShowUnspecifiedGrades" /></button>
      </td>
      <td>
        <s:text name="grading.header.validGradeCount">
          <s:param name="value" value="%{validGradeCount}"></s:param>
        </s:text>
      </td>
    </tr>
  </table>
  <!-- View for teacher -->
  <s:if test="!students.isEmpty()">
    <div class="table-responsive">
      <table class="table tablesorter" id="gradeTable">
        <thead>
          <tr>
            <th></th>
            <th><s:text name="general.header.receiver" /></th>
            <th><s:text name="general.header.group" /></th>
              <s:iterator var="gradeTask" value="tasks">
              <th>${gradeTask.name}</th>
              </s:iterator>
          </tr>
        </thead>
        <tbody>
          <s:iterator var="student" value="students" status="studentLoop">
            <s:if test="grades[#studentLoop.index][0] == null">
              <s:set var="rowClass">unspecifiedGrade</s:set>
            </s:if>
            <s:else>
              <s:set var="rowClass">hasGrade</s:set>
            </s:else>
            <tr class="${rowClass}">
              <td>${studentLoop.count}</td>
              <td>
                <s:url action="viewStudent" var="viewStudentUrl">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="studentId" value="%{#student.userId}" />
                </s:url>
                <s:a href="%{viewStudentUrl}" name="%{#student.userId }">
                  ${student.lastName}, ${student.firstName}
                </s:a>
                (${student.loginName})
              </td>
              <td>
                ${groupMembers[student.userId]}
              </td>
              <s:if test="courseUserId == #student.userId) && !resultsPeriodActive)">
                <td>
                  <s:text name="general.header.notAvailable" />
                </td>
              </s:if>
              <s:else>
                <s:iterator var="grade" value="grades[#studentLoop.index]" status="gradeLoop">
                  <s:if test="courseUserId == #student.userId">
                    <s:if test="!resultsPeriods[#gradeLoop.index]">
                      <td>
                        <s:text name="general.header.notAvailable" />
                      </td>
                    </s:if>
                    <s:elseif test="#grade != null">
                      <td>${grade}</td>
                    </s:elseif>
                    <s:else>
                      <td>
                        <s:text name="general.header.unspecified" />
                      </td>
                    </s:else>
                  </s:if>
                  <s:elseif test="#grade != null">
                    <td>${grade}</td>
                  </s:elseif>
                  <s:else>
                    <td>
                      <s:text name="general.header.unspecified" />
                    </td>
                  </s:else>
                  <s:set var="grade" value="null"/>
                </s:iterator>
              </s:else>
            </tr>
          </s:iterator>
        </tbody>
      </table>
    </div>
  </s:if>
  <s:else>
    <p><s:text name="grading.header.noStudents" /></p>
  </s:else>
</s:if>
<s:else>
  <div class="table-responsive">
    <table class="table tablesorter" id="gradeTable">
      <thead>
        <tr>
          <th><s:text name="general.header.receiver" /></th>
          <th><s:text name="general.header.group" /></th>
            <s:iterator var="gradeTask" value="tasks">
            <th>${gradeTask.name}</th>
            </s:iterator>
        </tr>
      </thead>
      <tbody>
        <s:iterator var="student" value="students" status="studentLoop">
          <tr>
            <td>
              ${student.lastName}, ${student.firstName} (${student.loginName})
            </td>
            <td>
              ${groupMembers[student.userId]}
            </td>
            <s:if test="courseUserId == #student.userId) && !resultsPeriodActive)">
              <td>
                <s:text name="general.header.notAvailable" />
              </td>
            </s:if>
            <s:else>
              <s:iterator var="grade" value="grades[#studentLoop.index]" status="gradeLoop">
                <s:if test="courseUserId == #student.userId">
                  <s:if test="!resultsPeriods[#gradeLoop.index]">
                    <td>
                      <s:text name="general.header.notAvailable" />
                    </td>
                  </s:if>
                  <s:elseif test="#grade != null">
                    <td>${grade}</td>
                  </s:elseif>
                  <s:else>
                    <td>
                      <s:text name="general.header.unspecified" />
                    </td>
                  </s:else>
                </s:if>
                <s:elseif test="#grade != null">
                  <td>${grade}</td>
                </s:elseif>
                <s:else>
                  <td>
                    <s:text name="general.header.unspecified" />
                  </td>
                </s:else>
                <s:set var="grade" value="null"/>
              </s:iterator>
            </s:else>
          </tr>
        </s:iterator>
      </tbody>
    </table>
  </div>
</s:else>
<script>
  $(function (){
  var table = $("#gradeTable");
  table.tablesorter({sortList: [[1, 0]], widgets: ['zebra']
  <s:if test="!tasks.isEmpty()">
  , headers: {
    <s:iterator var="gradeTask" value="tasks" status="loop">
      <s:if test="#loop.index > 0">,</s:if>
      ${loop.index + 2}: {sorter: 'digit'}
    </s:iterator>
  }
  </s:if>
  });
  table.bind("sortEnd", function() {
  var i = 1;
  table.find("tr:gt(0)").each(function(){
  $(this).find("td:eq(0)").text(i);
  i++;
  });
  });
  });
</script>
