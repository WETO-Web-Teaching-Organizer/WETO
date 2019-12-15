<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:set var="nameUnspecified" value="getText('general.header.unspecified')" />
<s:set var="nameValid" value="getText('general.header.valid')" />
<s:set var="nameVoid" value="getText('general.header.void')" />
<s:set var="nameNull" value="getText('general.header.null')" />
<s:set var="nameHidden" value="getText('general.header.hidden')" />
<s:set var="nameAggregate" value="getText('general.header.aggregate')" />
<s:set var="nameChallenged" value="getText('general.header.challenged')" />
<%
  String[] statusColors =
  {
    "yellow", "green", "red", "white", "grey", "white", "violet"
  };
  String[] statusNames =
  {
    (String) pageContext.getAttribute("nameUnspecified"),
    (String) pageContext.getAttribute("nameValid"),
    (String) pageContext.getAttribute("nameVoid"),
    (String) pageContext.getAttribute("nameNull"),
    (String) pageContext.getAttribute("nameHidden"),
    (String) pageContext.getAttribute("nameAggregate"),
    (String) pageContext.getAttribute("nameChallenged")
  };
  pageContext.setAttribute("statusColors", statusColors);
  pageContext.setAttribute("statusNames", statusNames);
%>
<s:set var="anonymousTXT" value="getText('general.header.anonymous')" />
<h2>
  <s:text name="grading.title.grades" />
</h2>

<%--  View for teachers  --%>
<s:if
  test="(gradingPeriod[0] != null) || (gradingPeriod[1] != null) ||
  (resultsPeriod[0] != null) || (resultsPeriod[1] != null)">
  <div class="contentBox">
    <h3>
      <s:text name="grading.title.periods" />
    </h3>
    <p>
      <s:if test="(gradingPeriod[0] != null) || (gradingPeriod[1] != null)">
        <s:text name="grading.header.gradingPeriod" />:
        <weto:timePeriod starting="${gradingPeriod[0]}"
                         ending="${gradingPeriod[1]}" />
        <br>
      </s:if>
      <s:if test="(resultsPeriod[0] != null) || (resultsPeriod[1] != null)">
        <s:text name="grading.header.resultsPeriod" />:
        <weto:timePeriod starting="${resultsPeriod[0]}"
                         ending="${resultsPeriod[1]}" />
      </s:if>
    </p>
  </div>
</s:if>
<s:if test="calculateAverage">
  <p>
    <s:text name="grading.header.averageScore" />
  </p>
</s:if>
<s:else>
  <p>
    <s:text name="grading.header.sumScore" />
  </p>
</s:else>
<s:if test="!gradeTable.isEmpty()">
  <h4>
    <s:text name="grading.title.gradeTable" />
  </h4>
  <table class="dataTable">
    <tr>
      <th>
        <s:text name="general.header.score" />
      </th>
      <s:iterator var="entry" value="gradeTable">
        <td>${entry[0]}</td>
      </s:iterator>
    </tr>
    <tr>
      <th>
        <s:text name="general.header.mark" />
      </th>
      <s:iterator var="entry" value="gradeTable">
        <s:if test="#entry[1] != null && !entry[1].isEmpty()">
          ${entry[1]}
        </s:if>
        <s:else>
          <s:text name="grading.header.failed"/>
        </s:else>
      </s:iterator>
    </tr>
  </table>
</s:if>
<table>
  <tr>
    <td>
      <div class="contentBox">
        <s:url action="viewTeacherLeafGrades" var="viewLeafGradesUrl">
          <s:param name="taskId" value="taskId" />
          <s:param name="tabId" value="tabId" />
          <s:param name="dbId" value="dbId" />
        </s:url>
        <form action="${viewLeafGradesUrl}" method="post">
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
      <button class="btn btn-default-small" onclick="$('#sortGradeTable').find('.unspecifiedGrade').toggle()"><s:text name="grading.header.toggleShowUnspecifiedGrades" /></button>
      <button class="btn btn-default-small" onclick="$('#sortGradeTable').find('.noGrade').toggle()"><s:text name="grading.header.toggleShowNoGrades" /></button>
    </td>
    <td>
      <s:text name="grading.header.validGradeCount">
        <s:param name="value" value="%{validGradeCount}"></s:param>
      </s:text>
    </td>
  </tr>
</table>
<s:if test="!receiversList.isEmpty()">
  <div class="table-responsive">
    <table class="table tablesorter" id="sortGradeTable">
      <thead>
        <tr>
          <th></th>
          <th>
            <s:text name="general.header.student" />
          </th>
          <th>
            <s:text name="general.header.loginName" />
          </th>
          <th>
            <s:text name="general.header.group" />
          </th>
          <th>
            <s:text name="students.header.submissionGroup" />
          </th>
          <th>
            <s:text name="grading.header.overall" />
          </th>
          <th colspan="${fillerMax}">
            <s:text name="grading.header.gradeComponents" />
          </th>
        </tr>
      </thead>
      <tbody>
        <s:iterator var="receiver" value="receiversList" status="loop">
          <s:if test="studentsGradesMap[#receiver.userId] != null">
            <s:if test="studentsGradesMap[#receiver.userId].get(0).mark != null">
              <s:set var="rowClass">validGrade</s:set>
            </s:if>
            <s:else>
              <s:set var="rowClass">unspecifiedGrade</s:set>
            </s:else>
          </s:if>
          <s:else>
            <s:set var="rowClass">noGrade</s:set>
          </s:else>
          <tr class="${rowClass}">
            <td>${loop.count}</td>
            <%-- Name --%>
            <td>
              <s:url action="viewStudent" var="viewStudentUrl">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="studentId" value="%{#receiver.userId}" />
              </s:url>
              <s:a href="%{viewStudentUrl}" name="%{#receiver.userId}">
                ${receiver.lastName}, ${receiver.firstName}
              </s:a>
              <s:if test="navigator.teacher">
                &nbsp;
                <s:url action="takeStudentRole" var="takeStudentRoleUrl">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="studentId" value="%{#receiver.userId}" />
                </s:url>
                <s:a href="%{takeStudentRoleUrl}" cssClass="smallLinkButton" cssStyle="float: right;">
                  <s:text name="students.header.takeRole" />
                </s:a>
              </s:if>
              <br />
              <s:if test="createRights">
                <s:url action="createGrade" var="createGradeUrl">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="receiverId" value="#receiver.userId" />
                </s:url>
                <s:a href="%{createGradeUrl}" cssClass="linkButton">
                  <s:text name="grading.header.addGrade" />
                </s:a>
              </s:if>
            </td>
            <%-- Login name --%>
            <td>
              ${receiver.loginName}
            </td>
            <%-- Group --%>
            <td>
              ${groupMembers[receiver.userId]}
            </td>
            <td>
              ${submitterGroups[loop.index]}
            </td>
            <%-- Overall grade --%>
            <td>
              <s:if test="(studentsGradesMap[#receiver.userId] != null) && (studentsGradesMap[#receiver.userId].get(0).mark != null)">
                <strong>${studentsGradesMap[receiver.userId].get(0).mark}</strong> <br /><br />
                ${studentsGradesMap[receiver.userId].get(0).timeStampString}
              </s:if>
              <s:else>
                <strong>${nameUnspecified}</strong>
              </s:else>
            </td>
            <s:iterator var="grade" value="studentsGradesMap[#receiver.userId]" status="loop">
              <s:if test="#loop.index > 0">
                <td class="${statusColors[grade.status]}" >
                  <s:if test="visibleMembersMap[#grade.reviewerId] == null">
                    ${anonymousTXT}
                  </s:if>
                  <s:else>
                    ${visibleMembersMap[grade.reviewerId].lastName},
                    ${visibleMembersMap[grade.reviewerId].firstName}
                  </s:else>
                  <br />
                  <s:url action="viewGrade" var="viewGradeUrl">
                    <s:param name="taskId" value="%{taskId}" />
                    <s:param name="tabId" value="%{tabId}" />
                    <s:param name="dbId" value="%{dbId}" />
                    <s:param name="gradeId" value="#grade.id" />
                  </s:url>
                  <s:a href="%{viewGradeUrl}" cssStyle="display: block;">
                    <s:if test="#grade.mark==null">
                      <s:text name="grading.value.empty" />
                    </s:if>
                    <s:else>
                      ${grade.mark}
                    </s:else>
                    <span style="float: right">
                      &nbsp;[${statusNames[grade.status]}]
                    </span>
                  </s:a>
                  ${grade.timeStampString}
                </td>
              </s:if>
            </s:iterator>
            <s:if test="fillerTable[#loop.index] != 0">
              <td colspan="${fillerTable[loop.index]}" class="filler"></td>
            </s:if>
          </tr>
        </s:iterator>
      </tbody>
    </table>
  </div>
  <script>
    $(document).ready(function () {
      $.tablesorter.addParser({
        id: 'date',
        is: function (s) {
          return false;
        },
        format: function (s) {
          var sd = s.match(/\d+\.\d+\.\d+ \d+:\d+/);
          if (sd)
          {
            return sd[0].replace(/(\d+)\.(\d+)\.(\d+) (\d+:\d+)/, "$3-$2-$1 $4");
          }
          return null;
        },
        type: 'text'
      });
      var table = $("#sortGradeTable");

      table.tablesorter({
        sortList: [[1, 0]],
        headers: {
          5: {
            sorter: 'digit'
          },
          6: {
            sorter: 'date'
          }
        }
      });

      table.bind("sortEnd", function () {
        var i = 1;
        table.find("tr:gt(0)").each(function () {
          $(this).find("td:eq(0)").text(i);
          i++;
        });
      });
    });
  </script>
</s:if>
<s:else>
  <p>
    <s:text name="grading.header.noStudents" />
  </p>
</s:else>