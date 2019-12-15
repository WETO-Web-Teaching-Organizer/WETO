<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:set var="notSubmittedTXT" value="%{getText('submission.header.notSubmitted')}" />
<s:set var="processingTXT" value="%{getText('submission.header.processing')}" />
<s:set var="acceptedTXT" value="%{getText('submission.header.accepted')}" />
<s:set var="notAcceptedTXT" value="%{getText('submission.header.notAccepted')}" />
<s:set var="quizSubmissionTXT" value="%{getText('submission.header.quizSubmission')}" />
<s:set var="unspecifiedTXT" value="getText('general.header.unspecified')" />
<s:set var="validTXT" value="getText('general.header.valid')" />
<s:set var="voidTXT" value="getText('general.header.void')" />
<s:set var="nullTXT" value="getText('general.header.null')" />
<s:set var="hiddenTXT" value="getText('general.header.hidden')" />
<s:set var="aggregateTXT" value="getText('general.header.aggregate')" />
<%
  String[] gradeColors =
  {
    "yellow", "green", "red", "white", "grey", "white"
  };
  String[] gradeNames =
  {
    (String) pageContext.getAttribute("unspecifiedTXT"),
    (String) pageContext.getAttribute("validTXT"),
    (String) pageContext.getAttribute("voidTXT"),
    (String) pageContext.getAttribute("nullTXT"),
    (String) pageContext.getAttribute("hiddenTXT"),
    (String) pageContext.getAttribute("aggregateTXT")
  };
  pageContext.setAttribute("gradeColors", gradeColors);
  pageContext.setAttribute("gradeNames", gradeNames);
  String[] subColors =
  {
    "yellow", "white", "green", "red", "grey"
  };
  String[] subNames =
  {
    (String) pageContext.getAttribute("notSubmittedTXT"),
    (String) pageContext.getAttribute("processingTXT"),
    (String) pageContext.getAttribute("acceptedTXT"),
    (String) pageContext.getAttribute("notAcceptedTXT"),
    (String) pageContext.getAttribute("quizSubmissionTXT")
  };
  pageContext.setAttribute("subColors", subColors);
  pageContext.setAttribute("subNames", subNames);
%>
<h3><s:text name="viewStudent.title" /></h3>
<s:if test="student == null">
  <p><s:text name="viewStudent.message.onlyStudents" /></p>
</s:if>
<s:else>
  <table class="tablesorter">
    <thead>
      <tr>
        <th><s:text name="general.header.studentNumber" /></th>
        <th><s:text name="general.header.name" /></th>
        <th><s:text name="general.header.loginName" /></th>
        <th><s:text name="general.header.email" /></th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>
          <s:property value="student.studentNumber" />
        </td>
        <td>${student.lastName}, ${student.firstName}</td>
        <td>${student.loginName}</td>
        <td><a href="mailto:${student.email}">${student.email}</a></td>
        <td>
          <s:if test="navigator.teacher">
            <s:url action="editStudent" var="editStudentUrl">
              <s:param name="taskId" value="taskId" />
              <s:param name="tabId" value="tabId" />
              <s:param name="dbId" value="dbId" />
              <s:param name="studentId" value="%{student.userId}" />
              <s:param name="submitted" value="false" />
            </s:url>
            <s:a href="%{editStudentUrl}" class="btn btn-default">
              <s:text name="viewStudent.header.edit"/>
            </s:a>
          </s:if>
        </td>
      </tr>
    </tbody>
  </table>
  <h3><s:property value="%{getText('viewStudent.instructions.remove', {withdrawLimit})}" escapeHtml="false" /></h3>
  <s:if test="canWithdraw">
    <s:url action="removeStudent" var="removeStudentUrl">
      <s:param name="taskId" value="taskId" />
      <s:param name="tabId" value="tabId" />
      <s:param name="dbId" value="dbId" />
      <s:param name="studentId" value="%{student.userId}" />
      <s:param name="submitted" value="false" />
    </s:url>
    <s:a href="%{removeStudentUrl}" class="btn btn-default">
      <s:text name="viewStudent.header.remove"/>
    </s:a>
  </s:if>
  <h3><s:text name="viewStudent.header.studentActivity" /></h3>
  <table class="tablesorter" id="studentTable">
    <thead>
      <tr>
        <th><s:text name="general.header.task" /></th>
        <th><s:text name="viewStudent.header.latestSubmission" /></th>
        <th><s:text name="viewStudent.header.receivedGrades" /></th>
        <th><s:text name="viewStudent.header.givenGrades" /></th>
        <th><s:text name="permissions.title" /></th>
      </tr>
    </thead>
    <tbody>
      <s:iterator value="dataRows" var="dataRow">
        <tr>
          <td>${dataRow.taskName}</td>
          <td>
            <s:if test="#dataRow.latestSubmission != null">
              <s:url action="viewSubmission" var="viewSubmissionUrl">
                <s:param name="taskId" value="#dataRow.taskId" />
                <s:param name="tabId" value="%{submissionsTabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="submissionId" value="#dataRow.latestSubmission[0]" />
              </s:url>
              <span class="${subColors[dataRow.latestSubmission[1]]}">
                <s:a href="%{viewSubmissionUrl}">
                  [${subNames[dataRow.latestSubmission[1]]}] ${dataRow.latestSubmission[2]}
                </s:a>
              </span>
            </s:if>
            <s:else>
              <s:text name="general.header.notAvailable" />
            </s:else>
          </td>
          <td>
            <s:if test="!#dataRow.receivedGrades.isEmpty()">
              <s:iterator value="#dataRow.receivedGrades" var="receivedGrade">
                <s:url action="viewGrade" var="viewGradeUrl">
                  <s:param name="taskId" value="#dataRow.taskId" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="gradeId" value="#receivedGrade[0]" />
                </s:url>
                <span class="${gradeColors[receivedGrade[1]]}">
                  <s:a href="%{viewGradeUrl}">
                    <span style="font-weight: bold">${receivedGrade[2]}</span>
                    [${gradeNames[receivedGrade[1]]}] ${receivedGrade[3]}
                  </s:a>
                </span>
                <br />
              </s:iterator>
            </s:if>
            <s:else>
              <s:text name="general.header.notAvailable" />
            </s:else>
          </td>
          <td>
            <s:if test="!#dataRow.givenGrades.isEmpty()">
              <s:iterator value="#dataRow.givenGrades" var="givenGrade">
                <s:url action="viewGrade" var="viewGradeUrl">
                  <s:param name="taskId" value="#dataRow.taskId" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="gradeId" value="#givenGrade[0]" />
                </s:url>
                <span class="${gradeColors[givenGrade[1]]}">
                  <s:a href="%{viewGradeUrl}">
                    <span style="font-weight: bold">${givenGrade[2]}</span>
                    [${gradeNames[givenGrade[1]]}] ${givenGrade[3]}
                  </s:a>
                </span>
                <br />
              </s:iterator>
            </s:if>
            <s:else>
              <s:text name="general.header.notAvailable" />
            </s:else>
          </td>
          <td>
            <s:if test="!#dataRow.permissions.isEmpty()">
              <s:iterator value="#dataRow.permissions" var="permission">
                <s:if test="navigator.teacher">
                  <s:url action="editPermission" var="editPermissionUrl">
                    <s:param name="taskId" value="#dataRow.taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="permissionId" value="#permission[0]" />
                  </s:url>
                  <s:a href="%{editPermissionUrl}">
                    ${permission[1]}<br />
                    <s:if test="#permission[2] == null">
                      <s:text name="general.header.notAvailable" />
                    </s:if>
                    <s:else>
                      ${permission[2]}
                    </s:else>
                    -
                    <s:if test="#permission[3] == null">
                      <s:text name="general.header.notAvailable" />
                    </s:if>
                    <s:else>
                      ${permission[3]}
                    </s:else>
                  </s:a>
                </s:if>
                <s:else>
                  ${permission[1]}<br />
                  <s:if test="#permission[2] == null">
                    <s:text name="general.header.notAvailable" />
                  </s:if>
                  <s:else>
                    ${permission[2]}
                  </s:else>
                  -
                  <s:if test="#permission[3] == null">
                    <s:text name="general.header.notAvailable" />
                  </s:if>
                  <s:else>
                    ${permission[3]}
                  </s:else>
                </s:else>
                <br />
              </s:iterator>
            </s:if>
            <s:else>
              <s:text name="general.header.notAvailable" />
            </s:else>
          </td>
        </tr>
      </s:iterator>
    </tbody>
  </table>
  <script>
    $(function () {
      $("#studentTable").tablesorter();
    });
  </script>
</s:else>
