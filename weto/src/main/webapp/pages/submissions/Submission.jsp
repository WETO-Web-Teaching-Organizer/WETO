<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/grading.js"></script>
<s:if test="deleteConfirm">
  <s:set var="statusClass" scope="page">yellow</s:set>
</s:if>
<s:else>
  <s:set var="statusClass" scope="page">white</s:set>
</s:else>
<s:if test="!grades.isEmpty()">
  <s:set var="nameUnspecified" value="getText('general.header.unspecified')" />
  <s:set var="nameValid" value="getText('general.header.valid')" />
  <s:set var="nameVoid" value="getText('general.header.void')" />
  <s:set var="nameNull" value="getText('general.header.null')" />
  <s:set var="nameHidden" value="getText('general.header.hidden')" />
  <s:set var="oneCol">colspan="2"</s:set>
  <s:set var="twoCol">colspan="3"</s:set>
  <%
    String[] statusColors =
    {
      "yellow", "green", "red", "white", "grey"
    };
    String[] statusNames =
    {
      (String) pageContext.getAttribute("nameUnspecified"),
      (String) pageContext.getAttribute("nameValid"),
      (String) pageContext.getAttribute("nameVoid"),
      (String) pageContext.getAttribute("nameNull"),
      (String) pageContext.getAttribute("nameHidden")
    };
    pageContext.setAttribute("statusColors", statusColors);
    pageContext.setAttribute("statusNames", statusNames);
  %>
</s:if>
<s:else>
  <s:set var="twoCol">colspan="2"</s:set>
</s:else>
<div style="background: ${statusClass}">
  <%-- Heading --%>
  <s:if test="deleteConfirm">
    <h2>
      <s:text name="submissions.title.delete" />
    </h2>
  </s:if>
  <s:elseif test="updateable">
    <h2>
      <s:text name="submissions.title.edit" />
    </h2>
  </s:elseif>
  <s:else>
    <h2>
      <s:text name="submissions.title.view" />
    </h2>
  </s:else>
  <s:if test="quizAnswer">
    <div class="contentBox">
      <table>
        <%-- Author --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.student" />:</th>
          <td>
            <s:if test="navigator.teacher">
              <s:url action="viewStudent" var="viewStudentUrl">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="studentId" value="%{submissionUser.userId}" />
              </s:url>
              <s:a href="%{viewStudentUrl}">
                ${submissionUser.lastName}, ${submissionUser.firstName}
              </s:a>
            </s:if>
            <s:else>
              ${submissionUser.lastName}, ${submissionUser.firstName}
            </s:else>
          </td>
          <s:if test="!grades.isEmpty() || createGradeRights">
            <s:set var="anonymousTXT" value="getText('general.header.anonymous')" />
            <td rowspan="3">
              <div class="contentBox">
                <table class="tablesorter">
                  <s:if test="createGradeRights">
                    <td>
                      <s:url action="createGrade" var="createGradeUrl">
                        <s:param name="taskId" value="%{submission.taskId}" />
                        <s:param name="tabId" value="%{gradingTabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                        <s:param name="receiverId" value="%{submission.userId}" />
                        <s:param name="submissionId" value="%{submissionId}" />
                      </s:url>
                      <s:a href="%{createGradeUrl}" cssClass="btn btn-primary">
                        <s:text name="grading.header.addGrade" />
                      </s:a>
                    </td>
                  </s:if>
                  <th>
                    <s:text name="general.header.reviewer" />
                    <br />
                    <s:text name="general.header.mark" />
                    <br />
                    <s:text name="general.header.date" />
                  </th>
                  <s:iterator var="grade" value="grades">
                    <td class="${statusColors[grade.status]}">
                      <s:if test="reviewerMap[#grade.reviewerId] == null">
                        ${anonymousTXT}
                      </s:if>
                      <s:else>
                        ${reviewerMap[grade.reviewerId].lastName},
                        ${reviewerMap[grade.reviewerId].firstName}
                      </s:else>
                      <br />
                      <s:url action="viewGrade" var="viewGradeUrl">
                        <s:param name="taskId" value="%{taskId}" />
                        <s:param name="tabId" value="%{gradingTabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                        <s:param name="gradeId" value="#grade.id" />
                        <s:param name="submissionId" value="%{submissionId}" />
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
                  </s:iterator>
                </table>
              </div>
            </td>
          </s:if>
        </tr>
        <%-- Last modified timestamp --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.lastModified" />:</th>
          <td>
            ${submission.timeStampString}
          </td>
        </tr>
        <%-- Status  --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.status" />:</th>
          <td>
            <s:text name="%{submissionStates[submission.status].property}" />
            <s:if test="queuePos != null">
              (<s:text name="autograding.header.queuePos" />: <span id="queuePos">${queuePos}</span>)
            </s:if>
          </td>
        </tr>
        <%-- Autograde score  --%>
        <s:if test="submission.autoGradeMark != null">
          <tr>
            <th class="topLeft"><s:text name="autograding.header.autogradeScore" />:</th>
            <td>
              ${submission.autoGradeMark}
            </td>
          </tr>
        </s:if>
      </table>
    </div>
    <s:url action="viewTask" var="viewTaskUrl">
      <s:param name="taskId" value="%{taskId}" />
      <s:param name="tabId" value="#mainTabId" />
      <s:param name="dbId" value="%{dbId}" />
      <s:param name="answererId" value="%{submissionUser.userId}" />
    </s:url>
    <iframe src="${viewTaskUrl}" width="100%" onload="resizeIframe(this)"></iframe>
    </s:if>
    <s:else>
    <p>
      <s:text name="submissions.header.submissionPeriod" />:
      <weto:timePeriod starting="${submissionPeriod[0]}" ending="${submissionPeriod[1]}" />
      <br />
      <s:text name="general.header.serverTime" />: <s:date name="currentTime" />
    </p>
    <s:if test="updateable">
      <p>
        <s:text name="submissions.instructions.addFiles" />
      </p>
    </s:if>
    <%-- Allowed file types --%>
    <s:if test="!deleteConfirm && updateable">
      <p>
        <s:text name="submissions.header.allowedFilePatterns" />:
        <strong>${patternDescriptions}</strong>
        <s:if test="allowZipping">
          <br /><s:text name="submissions.instructions.zippingAllowed" />
        </s:if>
      </p>
    </s:if>
    <%-- Submission --%>
    <div class="contentBox">
      <table>
        <%-- Author --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.student" />:</th>
          <td>
            <s:if test="navigator.teacher">
              <s:url action="viewStudent" var="viewStudentUrl">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="studentId" value="%{submissionUser.userId}" />
              </s:url>
              <s:a href="%{viewStudentUrl}">
                ${submissionUser.lastName}, ${submissionUser.firstName}
              </s:a>
            </s:if>
            <s:else>
              ${submissionUser.lastName}, ${submissionUser.firstName}
            </s:else>
          </td>
          <s:if test="!grades.isEmpty()">
            <s:set var="anonymousTXT" value="getText('general.header.anonymous')" />
            <td rowspan="4">
              <div class="contentBox">
                <table class="tablesorter">
                  <s:if test="createGradeRights">
                    <td>
                      <s:url action="createGrade" var="createGradeUrl">
                        <s:param name="taskId" value="%{submission.taskId}" />
                        <s:param name="tabId" value="%{gradingTabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                        <s:param name="receiverId" value="%{submission.userId}" />
                        <s:param name="submissionId" value="%{submissionId}" />
                      </s:url>
                      <s:a href="%{createGradeUrl}" cssClass="btn btn-primary">
                        <s:text name="grading.header.addGrade" />
                      </s:a>
                    </td>
                  </s:if>
                  <th>
                    <s:text name="general.header.reviewer" />
                    <br />
                    <s:text name="general.header.mark" />
                    <br />
                    <s:text name="general.header.date" />
                  </th>
                  <s:iterator var="grade" value="grades">
                    <td class="${statusColors[grade.status]}">
                      <s:if test="reviewerMap[#grade.reviewerId] == null">
                        ${anonymousTXT}
                      </s:if>
                      <s:else>
                        ${reviewerMap[grade.reviewerId].lastName},
                        ${reviewerMap[grade.reviewerId].firstName}
                      </s:else>
                      <br />
                      <s:url action="viewGrade" var="viewGradeUrl">
                        <s:param name="taskId" value="%{taskId}" />
                        <s:param name="tabId" value="%{gradingTabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                        <s:param name="gradeId" value="#grade.id" />
                        <s:param name="submissionId" value="%{submissionId}" />
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
                        </br>
                        ${grade.timeStampString}
                      </s:a>
                    </td>
                  </s:iterator>
                </table>
              </div>
            </td>
          </s:if>
        </tr>
        <%-- Last modified timestamp --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.lastModified" />:</th>
          <td>
            ${submission.timeStampString}
          </td>
        </tr>
        <%-- Status  --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.status" />:</th>
          <td>
            <s:text name="%{submissionStates[submission.status].property}" />
            <s:if test="queuePos != null">
              (<s:text name="autograding.header.queuePos" />: <span id="queuePos">${queuePos}</span>)
            </s:if>
          </td>
        </tr>
        <%-- Error  --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.errors" />:</th>
          <td>
            <s:if test="submission.error == null">
              <s:text name="general.header.noErrors" />
            </s:if>
            <s:else>
              <s:text name="%{submissionErrors[submission.error]}" />
            </s:else>
          </td>
        </tr>
        <%-- Autograde score  --%>
        <s:if test="submission.autoGradeMark != null">
          <tr>
            <th class="topLeft"><s:text name="autograding.header.autogradeScore" />:</th>
            <td>
              ${submission.autoGradeMark}
            </td>
          </tr>
        </s:if>
        <%-- Submission documents --%>
        <tr>
          <th class="topLeft"><s:text name="general.header.files" />:</th>
          <td ${oneCol}>
            <table id="subDocTable" class="tablesorter" style="margin: 0 0 5px 0">
              <thead>
                <tr>
                  <th><s:text name="general.header.fileName" /></th>
                  <th><s:text name="general.header.fileSize" /></th>
                  <th><s:text name="general.header.lastModified" /></th>
                  <th><s:text name="general.header.actions" /></th>
                </tr>
              </thead>
              <tbody>
                <s:if test="updateable">
                  <s:set var="documentAction"><s:text name="general.header.edit" /></s:set>
                </s:if>
                <s:else>
                  <s:set var="documentAction"><s:text name="general.header.view" /></s:set>
                </s:else>
                <s:iterator var="document" value="documents" status="loop">
                  <s:if test="%{duplicates[#loop.index]}">
                    <s:set var="rowColor">class="yellow"</s:set>
                  </s:if>
                  <s:else>
                    <s:set var="rowColor" value="null" />
                  </s:else>
                  <tr ${rowColor}>
                    <td>
                      ${document.fileName}
                    </td>
                    <td>
                      ${document.contentFileSize}
                    </td>
                    <td>
                      ${document.timeStampString}
                    </td>
                    <td>
                      <s:url action="downloadDocument" var="downloadDocumentURL">
                        <s:param name="taskId" value="taskId" />
                        <s:param name="tabId" value="tabId" />
                        <s:param name="dbId" value="dbId" />
                        <s:param name="documentId" value="#document.id" />
                      </s:url>
                      <s:a href="%{downloadDocumentURL}" target="_blank" cssClass="btn btn-default">
                        <s:text name="general.header.download" />
                      </s:a>
                      <s:if test="#document.contentMimeType.startsWith('text') || #document.contentMimeType.equals('application/x-sh')">
                        <s:url action="editTextDocument" var="editDocumentURL">
                          <s:param name="taskId" value="taskId" />
                          <s:param name="tabId" value="tabId" />
                          <s:param name="dbId" value="dbId" />
                          <s:param name="documentId" value="#document.id" />
                        </s:url>
                        <s:a href="%{editDocumentURL}" cssClass="btn btn-default">
                          <s:property value="#documentAction" />
                        </s:a>
                      </s:if>
                      <s:if test="updateable">
                        <s:url action="deleteDocument" var="deleteDocumentURL">
                          <s:param name="taskId" value="taskId" />
                          <s:param name="tabId" value="tabId" />
                          <s:param name="dbId" value="dbId" />
                          <s:param name="documentId" value="#document.id" />
                        </s:url>
                        <s:a href="%{deleteDocumentURL}" cssClass="btn btn-danger">
                          <s:text name="general.header.delete" />
                        </s:a>
                      </s:if>
                    </td>
                  </tr>
                </s:iterator>
              </tbody>
            </table>
          </td>
        </tr>

        <%-- Compiler messages (autograding related) --%>
        <s:if test="submission.message != null && !submission.message.isEmpty()">
          <tr>
            <th class="topLeft">
              <s:text name="autograding.header.compilerOutput" />:
            </th>
            <td>
              <table class="tablesorter" style="margin-left: 0">
                <tbody>
                  <tr>
                    <td>
                      <pre>${submission.message}</pre>
                      <s:if test="compilerResultId != null">
                        <s:url action="viewFeedback" var="viewCompilerResultURL">
                          <s:param name="taskId" value="taskId" />
                          <s:param name="tabId" value="tabId" />
                          <s:param name="dbId" value="dbId" />
                          <s:param name="submissionId" value="submissionId" />
                          <s:param name="tagId" value="compilerResultId" />
                        </s:url>
                        <s:a href="%{viewCompilerResultURL}" target="_blank" cssClass="btn btn-default">
                          <s:text name="submissions.header.fullFeedback" />
                        </s:a>
                      </s:if>
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </s:if>

        <%-- Test scores (autograding related) --%>
        <s:if test="testScores != null && !testScores.isEmpty()">
          <tr>
            <th class="topLeft" ${twoCol}>
              <s:text name="autograding.header.testScores" />:
            </th>
          </tr>
          <tr>
            <td ${twoCol}>
              <table class="tablesorter" id="autogradeTable">
                <thead>
                  <tr>
                    <th><s:text name="autograding.header.testNo" /></th>
                    <th><s:text name="general.header.score" /></th>
                    <th><s:text name="general.header.time" /></th>
                    <th><s:text name="autograding.header.feedback" /></th>
                  </tr>
                </thead>
                <tbody>
                  <s:set var="immediatePublic" value="1" />
                  <s:set var="immediatePrivate" value="2" />
                  <s:set var="finalPublic" value="3" />
                  <s:set var="finalPrivate" value="4" />
                  <s:iterator var="testScore" value="testScores" status="loop">
                    <tr>
                      <td>
                        <s:property value="#testScore.testNo" /><br/>
                        <s:if test="#testScore.phase == #immediatePublic">
                          <s:text name="general.header.public" />
                        </s:if>
                        <s:elseif test="#testScore.phase == #immediatePrivate">
                          <s:text name="general.header.private" />
                        </s:elseif>
                        <s:elseif test="#testScore.phase == #finalPublic">
                          <s:text name="general.header.public" />
                        </s:elseif>
                        <s:elseif test="#testScore.phase == #finalPrivate">
                          <s:text name="general.header.private" />
                        </s:elseif>
                        <s:else>
                          <s:text name="autograding.header.unknownTest" />
                        </s:else>
                      </td>
                      <td><s:property value="#testScore.testScore" /></td>
                      <td><s:property value="#testScore.processingTime" /></td>
                      <td>
                        <s:if test="fullFeedbackIds[#loop.index] != null">
                          <s:url action="viewFeedback" var="viewFeedbackURL">
                            <s:param name="taskId" value="taskId" />
                            <s:param name="tabId" value="tabId" />
                            <s:param name="dbId" value="dbId" />
                            <s:param name="submissionId" value="submissionId" />
                            <s:param name="tagId" value="fullFeedbackIds[#loop.index]" />
                          </s:url>
                          <s:a href="%{viewFeedbackURL}" target="_blank" cssClass="btn btn-default">
                            <s:text name="submissions.header.fullFeedback" />
                          </s:a>
                        </s:if>
                        <pre class="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px">${feedback}</pre>
                      </td>
                    </tr>
                  </s:iterator>
                </tbody>
              </table>
            </td>
          </tr>
        </s:if>
      </table>

      <s:if test="!deleteConfirm && updateable">
        <s:if test="allowInlineFiles">
          <form action="<s:url action="viewSubmission" />" method="post">
            <input type="hidden" name="taskId" value="${taskId}" />
            <input type="hidden" name="tabId" value="${tabId}" />
            <input type="hidden" name="dbId" value="${dbId}" />
            <input type="hidden" name="submissionId" value="${submissionId}" />
            <p><s:text name="submissions.header.createOnlineFile" />: <input type="text" name="documentFileFileName" size="30" />
              <input type="submit" value="<s:text name="general.header.create" />" class="btn btn-default" /></p>
          </form>
        </s:if>
        <form action="<s:url action="viewSubmission" />" method="post" enctype="multipart/form-data">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <input type="hidden" name="submissionId" value="${submissionId}" />
          <p><s:text name="submissions.header.uploadFile" />: <input type="file" name="documentFile" />
            <input type="submit" value="<s:text name="general.header.upload" />" class="btn btn-default" /></p>
        </form>
      </s:if>
    </div>
  </div>
</s:else>

<s:if test="deleteConfirm">
  <form action="<s:url action="deleteSubmission" />" method="post" >
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="submissionId" value="${submissionId}" />
    <input type="hidden" name="submitted" value="true" />
    <p><input type="submit" value="<s:text name="submissions.header.deleteSubmission" />" class="btn btn-danger" /></p>
  </form>
</s:if>
<s:else>
  <s:if test="documents != null && !documents.isEmpty() && (submission.status == 0)">
    <form action="<s:url action="completeSubmission" />" method="post" >
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <input type="hidden" name="submissionId" value="${submissionId}" />
      <p><input type="submit" value="<s:text name="general.header.submit" />" class="btn btn-primary" /></p>
    </form>

    <s:if test="allowTestRun">
      <form action="<s:url action="completeSubmission" />" method="post" >
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <input type="hidden" name="submissionId" value="${submissionId}" />
        <input type="hidden" name="isTestRun" value="true" />
        <p><s:text name="submissions.instructions.testRun" />
          <input type="submit" value="<s:text name="submissions.header.testRun" />" class="btn btn-primary" /></p>
      </form>
    </s:if>
  </s:if>
  <s:if test="navigator.teacher">
    <s:url action="deleteSubmission" var="deleteSubmissionUrl">
      <s:param name="taskId" value="taskId" />
      <s:param name="tabId" value="tabId" />
      <s:param name="dbId" value="dbId" />
      <s:param name="submissionId" value="submission.id" />
    </s:url>
    <s:a href="%{deleteSubmissionUrl}" class="btn btn-danger"
         title= "Delete">
      <s:text name='general.header.delete' />
    </s:a>
    <s:if test="task.isAutoGraded && submission.status > 1">
      <s:url action="resubmit2" var="resubmitUrl">
        <s:param name="taskId" value="taskId" />
        <s:param name="tabId" value="tabId" />
        <s:param name="dbId" value="dbId" />
        <s:param name="submissionId" value="submission.id" />
      </s:url>
      &nbsp;
      <s:a href="%{resubmitUrl}" class="btn btn-primary">
        <s:text name='general.header.resubmit' />
      </s:a>
    </s:if>
  </s:if>
</s:else>
<s:if test="queuePos != null">
  <img id="ajaximg" src="images/ajax-blue.gif" />
</s:if>
<script>
  <s:if test="queuePos != null">
  // If state is "processing", start listening for updates
  function pollAutograding()
  {
    jQuery.ajax({
      url: 'getJSONSubmission.action',
      method: 'GET',
      data: {'dbId': ${dbId}, 'taskId': ${taskId}, 'tabId': ${tabId}, 'submissionId': ${submissionId}},
      success: function (response) {
        var updatedSubmission = response.submission;
        if (updatedSubmission.status == 1) {
          $("#queuePos").html(updatedSubmission.queuePos);
          setTimeout(function () {
            pollAutograding();
          }, 1000);
        } else {
          location.reload(true);
        }
      }
    }
    );
  }

  $(function () {
    var ajaximg = $("#ajaximg");
    ajaximg.css("position", "absolute");
    ajaximg.css("top", Math.max(0, (($(window).height() - ajaximg.outerHeight()) / 2) + $(window).scrollTop()) + "px");
    ajaximg.css("left", Math.max(0, (($(window).width() - ajaximg.outerWidth()) / 2) + $(window).scrollLeft()) + "px");

    setTimeout(function () {
      pollAutograding();
    }, 1000);
  });
  </s:if>

  function resizeIframe(iframe) {
    iframe.height = iframe.contentWindow.document.body.scrollHeight + "px";
  }

  $(function () {
    $("#subDocTable").tablesorter({
      headers: {
        3: {// Disable sorting for the 4th column (whose index is 3)
          sorter: false
        }
      }
    });
    $("#autogradeTable").tablesorter({
      widgets: ['zebra']
    });
    colorDiffFeedback($(".diffCell"));
  });
</script>
