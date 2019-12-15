<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:set var="submissionsTitleTXT" value="%{getText('submissions.title')}" />
<s:set var="studentFilePatternsUndefinedTXT" value="%{getText('submissions.message.studentFilePatternsUndefined')}" />
<s:set var="teacherFilePatternsUndefinedTXT" value="%{getText('submissions.message.teacherFilePatternsUndefined')}" />
<s:set var="submissionPeriodTXT" value="%{getText('submissions.header.submissionPeriod')}" />
<s:set var="submissionPeriodActiveTXT" value="%{getText('submissions.header.submissionPeriodActive')}" />
<s:set var="noTimeLimitsTXT" value="%{getText('submissions.header.noTimeLimits')}" />
<s:set var="instructionsTXT" value="%{getText('submissions.instructions.general')}" />
<s:set var="filterTXT" value="%{getText('submissions.header.filter')}" />
<s:set var="fromTXT" value="%{getText('general.header.from')}" />
<s:set var="toTXT" value="%{getText('general.header.to')}" />
<s:set var="showIfZeroSubmissionsTXT" value="%{getText('submissions.header.showStudentsWithoutSubmissions')}" />
<s:set var="latestOrBestTXT" value="%{getText('submissions.header.downloadLatestOrBest')}" />
<s:set var="doNotCompressTXT" value="%{getText('submissions.header.doNotCompress')}" />
<s:set var="noOfSubmissionsTXT" value="%{getText('submissions.header.numberOfSubmissions')}" />
<s:set var="updateTXT" value="%{getText('general.header.update')}" />
<s:set var="groupTXT" value="%{getText('general.header.group')}" />
<s:set var="submitGroupTXT" value="%{getText('students.header.submissionGroup')}" />
<s:set var="studentTXT" value="%{getText('general.header.student')}" />
<s:set var="currentTXT" value="%{getText('submissions.header.current')}" />
<s:set var="previousTXT" value="%{getText('submissions.header.previous')}" />
<s:set var="addSubmissionTXT" value="%{getText('submissions.header.addSubmission')}" />
<s:set var="editTXT" value="%{getText('general.header.edit')}" />
<s:set var="deleteTXT" value="%{getText('general.header.delete')}" />
<s:set var="viewTXT" value="%{getText('general.header.view')}" />
<s:set var="resubmitTXT" value="%{getText('general.header.resubmit')}" />
<s:set var="processingTXT" value="1" />
<script src="js/grading.js"></script>
<!-- Some explanation text about submissions -->
<div id="submissionInstructions">
  <div class="header_field">
    <script>
      $(function () {
        $(".help-sign").click(function (event) {
          $(this).parent().siblings(".help-field").toggle();
        });
      });
    </script>
    <h2>${submissionsTitleTXT}</h2>
    <div class= "help-sign">
      <span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="right" data-original-title="Help">
      </span>
    </div>
  </div>
  <div class="help-field panel">
    ${instructionsTXT}
  </div>
  <s:if test="navigator.teacher && ((generalSubmissionPeriod[0] != null) || (generalSubmissionPeriod[1] != null))">
    <p>
      ${submissionPeriodTXT}:
      <weto:timePeriod starting="${generalSubmissionPeriod[0]}" ending="${generalSubmissionPeriod[1]}" />
    </p>
  </s:if>
  <s:elseif test="!navigator.teacher && ((submissionPeriod[0] != null) || (submissionPeriod[1] != null))">
    <p>
      ${submissionPeriodTXT}:
      <weto:timePeriod starting="${submissionPeriod[0]}" ending="${submissionPeriod[1]}" />
    </p>
  </s:elseif>
  <s:else>
    <p>${noTimeLimitsTXT}</p>
  </s:else>
  <s:if test="submissionPeriodActive">
    <p>
      <s:if test="submissionQuota != null">
        <s:if test="submissionQuota != 1">
          <s:text name="submissions.header.submissionPeriodActive" />&#32;
          <span id="submissionQuotaMessage">
            <s:text name="submissions.header.submissionQuota">
              <s:param>${submissionQuota}</s:param>
            </s:text>
            <s:if test="submissionQuota == mostSubmissions">
              &#32;<s:text name="submissions.header.submissionQuotaFull">
                <s:param>${submissionQuota}</s:param>
              </s:text>
            </s:if>
          </span>
        </s:if>
        <s:else>
          <s:text name="submissions.header.submissionPeriodActiveOne" />
          <span id="submissionQuotaMessage"></span>
        </s:else>
      </s:if>
      <s:else>
        <s:text name="submissions.header.submissionPeriodActive" />&#32;
        <span id="submissionQuotaMessage"></span>
        <s:text name="submissions.header.submissionQuotaUnlimited" />
      </s:else>
    </p>
  </s:if>
</div>
<s:if test="navigator.teacher">
  <s:if test="!allowedFilePatternsDefined">
    <p><strong>${teacherFilePatternsUndefinedTXT}</strong></p>
  </s:if>
  <div class="filter">
    ${filterTXT}
    <form action="<s:url action="viewSubmissions" escapeAmp="false">
            <s:param name="taskId" value="taskId" />
            <s:param name="tabId" value="tabId" />
            <s:param name="dbId" value="dbId" /></s:url>" method="post">
      <label for="from">${fromTXT}</label>
      <input type="text" name="from" value="${from}" id="from" />
      <label for="to">${toTXT}</label>
      <input type="text" name="to" value="${to}" id="to" />
      <input type="checkbox" name="showAllStudents" id="sas" value="true"
             <s:if test="showAllStudents">checked="checked"</s:if> />
      <label for="sas">${showIfZeroSubmissionsTXT}</label>
      <input type="submit" value="${updateTXT}" class="btn-primary-small" />
    </form>
    <script>
      $(function () {
        $.datepicker.setDefaults($.datepicker.regional[ "fi" ]);
        $("#from").datepicker({
          onSelect: function (selectedDate) {
            $("#to").datepicker("option", "minDate", selectedDate);
          }
        });
        $("#to").datepicker({
          onSelect: function (selectedDate) {
            $("#from").datepicker("option", "maxDate", selectedDate);
          }
        });
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
            return s;
          },
          type: 'text'
        });
        $("#sortSubmissionTable").tablesorter({
        sortList: [[3, 0]],
                headers: {
                0: {
                sorter: false
                },
                        4: {
                        sorter: 'date'
                        }
      <s:iterator begin="2" end="mostSubmissions" status="loop">
                ,${loop.index + 5}: {
                sorter: false
                }
      </s:iterator>
                }
        });
      });
    </script>
  </div>
  <p>
    <s:text name="submissions.header.numberOfSubmissions">
      <s:param>${submissionsTotal}</s:param>
      <s:param>${userSubmissions.size()}</s:param>
      <s:param>${students.size()}</s:param>
      <s:param>${acceptedStudents}</s:param>
      <s:param>${submissionsShowing}</s:param>
      <s:param>${studentsShowing}</s:param>
    </s:text>
  </p>
  <s:url action="removeEmptySubmissions" var="removeEmptyUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="tabId" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:url action="completeUncompletedSubmissions" var="completeUncompletedUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="tabId" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <table>
    <tr>
      <th><s:text name="submissions.header.cleanup" />:</th>
      <td>
        <s:a href="%{removeEmptyUrl}" class="btn btn-primary-small">
          <s:text name="submissions.header.removeEmpty" />
        </s:a>
      </td>
      <td>
        <s:a href="%{completeUncompletedUrl}" class="btn btn-primary-small">
          <s:text name="submissions.header.completeUncompleted" />
        </s:a>
      </td>
    </tr>
  </table>
  <form action="<s:url action="exportSubmissions" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <%-- SUBMISSION DOWNLOAD --%>
    <input type="hidden" name="from" value="${from}" />
    <input type="hidden" name="to" value="${to}" />
    <table>
      <tr>
        <td><input type="submit" value="<s:text name="submissions.header.downloadSubmissions" />" class="btn btn-primary-small" /></td>
        <td><input type="checkbox" name="onlyLatestOrBest" id="olob" value="true" /><label for="olob">${latestOrBestTXT}</label></td>
        <td><input type="checkbox" name="doNotCompress" id="dnc" value="true" /><label for="dnc">${doNotCompressTXT}</label></td>
      </tr>
    </table>
    <s:if test="task.isAutoGraded && (submissionsTotal > 0)">
      <s:url action="resubmitAll" var="resubmitAllUrl">
        <s:param name="taskId" value="taskId" />
        <s:param name="tabId" value="tabId" />
        <s:param name="dbId" value="dbId" />
      </s:url>
      <s:a href="%{resubmitAllUrl}" class="btn-default-small">
        <s:text name="general.header.resubmitAll"/>
      </s:a>
    </s:if>
    <div class="table-responsive">
      <table class="table tablesorter" id="sortSubmissionTable">
        <%-- HEADERS --%>
        <thead>
          <tr>
            <th>
              <input type="checkbox" name="downloadAll" checked="checked" onclick="checkAll();">
            </th>
            <th>${groupTXT}</th>
            <th>${submitGroupTXT}</th>
            <th>${studentTXT}</th>
              <s:if test="mostSubmissions > 0">
              <th>${currentTXT}</th>
                <s:iterator begin="2" end="mostSubmissions" status="loop">
                <th><i>${previousTXT}&nbsp;${loop.index + 1}</i></th>
                </s:iterator>
              </s:if>
          </tr>
        </thead>
        <%-- DATA ROWS --%>
        <tbody>
          <s:iterator var="student" value="students" status="studentLoop">
            <s:if test="showAllStudents || !userSubmissions[#student.userId].isEmpty()">
              <tr>
                <%-- CHECKBOX and GROUP --%>
                <td>
                  <input type="checkbox" name="userIds" value="${student.userId}" checked="checked" />
                </td>
                <td>
                  ${groupMember[student.userId]}
                </td>
                <td>
                  ${submitterGroups[studentLoop.index]}
                </td>
                <%-- STUDENT --%>
                <th>
                  <s:url action="viewStudent" var="viewStudentUrl">
                    <s:param name="taskId" value="taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="studentId" value="%{#student.userId}" />
                  </s:url>
                  <s:a href="%{viewStudentUrl}" name="%{#student.userId}" id="%{#student.userId}">
                    ${student.lastName}, ${student.firstName}
                  </s:a>
                  <br /> ${student.loginName}<br />
                  <s:if test="submissionPeriodActive && createRights">
                    <s:url action="createSubmission" var="createSubmissionUrl">
                      <s:param name="taskId" value="taskId" />
                      <s:param name="tabId" value="tabId" />
                      <s:param name="dbId" value="dbId" />
                      <s:param name="submitterId" value="#student.userId" />
                    </s:url>
                    <s:a href="%{createSubmissionUrl}" class="btn-primary-small">
                      ${addSubmissionTXT}
                    </s:a>
                  </s:if>
                </th>

                <%-- SUBMISSIONS --%>
                <s:if test="mostSubmissions > 0">
                  <s:if test="submissionPeriodActive && deleteRights">
                    <s:set var="canDelete" value="true" />
                  </s:if>
                  <s:else>
                    <s:set var="canDelete" value="false" />
                  </s:else>
                  <s:iterator var="submission" value="userSubmissions[#student.userId]" status="loop">
                    <s:if test="#submission.status == notSubmittedState">
                      <s:set var="statusClass" scope="page">yellow</s:set>
                    </s:if>
                    <s:elseif test="#loop.index == 0">
                      <s:if test="#submission.status == acceptedState">
                        <s:if test="#submission.message != null">
                          <s:set var="statusClass" scope="page">yellow</s:set>
                        </s:if>
                        <s:elseif test="#submission.error == null">
                          <s:set var="statusClass" scope="page">green</s:set>
                        </s:elseif>
                        <s:elseif test="#submission.error != null">
                          <s:set var="statusClass" scope="page">red</s:set>
                        </s:elseif>
                        <s:else>
                          <s:set var="statusClass" scope="page">none</s:set>
                        </s:else>
                      </s:if>
                      <s:elseif test="#submission.status == notAcceptedState">
                        <s:set var="statusClass" scope="page">red</s:set>
                      </s:elseif>
                      <s:else>
                        <s:set var="statusClass" scope="page">none</s:set>
                      </s:else>
                    </s:elseif>
                    <s:else>
                      <s:set var="statusClass" scope="page">grey</s:set>
                    </s:else>
                    <td class="${statusClass}">
                      <s:url action="viewSubmission" var="viewSubmissionUrl">
                        <s:param name="taskId" value="taskId" />
                        <s:param name="tabId" value="tabId" />
                        <s:param name="dbId" value="dbId" />
                        <s:param name="submissionId" value="#submission.id" />
                      </s:url>
                      <s:if test="(#submission.status == notSubmittedState) &&
                            submissionPeriodActive && updateRights">
                        <s:a href="%{viewSubmissionUrl}" class="btn-default-small">
                          ${editTXT}
                        </s:a>&nbsp;
                        <s:if test="#canDelete">
                          <s:url action="deleteSubmission" var="deleteSubmissionUrl">
                            <s:param name="taskId" value="taskId" />
                            <s:param name="tabId" value="tabId" />
                            <s:param name="dbId" value="dbId" />
                            <s:param name="submissionId" value="#submission.id" />
                          </s:url>
                          <s:a href="%{deleteSubmissionUrl}" class="btn-default-small"
                               title="delete"><s:text name='general.header.delete' />
                          </s:a>&nbsp;
                        </s:if>
                      </s:if>
                      <s:else>
                        <s:a href="%{viewSubmissionUrl}" class="btn-default-small">
                          ${viewTXT}
                        </s:a>&nbsp;
                        <s:if test="#canDelete">
                          <s:url action="deleteSubmission" var="deleteSubmissionUrl">
                            <s:param name="taskId" value="taskId" />
                            <s:param name="tabId" value="tabId" />
                            <s:param name="dbId" value="dbId" />
                            <s:param name="submissionId" value="#submission.id" />
                          </s:url>
                          <s:a href="%{deleteSubmissionUrl}" class="btn-default-small"
                               title= "Delete">
                            <s:text name='general.header.delete' />
                          </s:a>&nbsp;
                        </s:if>
                        <s:if test="task.isAutoGraded && #submission.status != #processingTXT">
                          <s:url action="resubmit" var="resubmitUrl">
                            <s:param name="taskId" value="taskId" />
                            <s:param name="tabId" value="tabId" />
                            <s:param name="dbId" value="dbId" />
                            <s:param name="submissionId" value="#submission.id" />
                          </s:url>
                          <s:a href="%{resubmitUrl}" class="btn-primary-small">
                            ${resubmitTXT}
                          </s:a>&nbsp;
                        </s:if>
                      </s:else>
                      <br />
                      ${submission.timeStampString}<br />
                      <s:if test="#submission.fileCount != null">
                        <strong>${submission.fileCount}</strong>&nbsp;
                      </s:if>
                      <s:if test="#loop.index > 0">
                        (<del><s:text name="%{submissionStates[#submission.status].property}" /></del>)
                      </s:if>
                      <s:else>
                        <s:text name="%{submissionStates[#submission.status].property}" />
                      </s:else>
                    </td>
                  </s:iterator>
                  <s:if test="userSubmissions[#student.userId] != null && userSubmissions[#student.userId].size() < mostSubmissions">
                    <td colspan="${mostSubmissions - userSubmissions[student.userId].size()}" class="filler">
                    </td>
                  </s:if>
                  <s:elseif test="userSubmissions[#student.userId] == null || userSubmissions[#student.userId].size() < 1">
                    <td colspan="${mostSubmissions}" class="filler">
                    </td>
                  </s:elseif>
                </s:if>
              </tr>
              <tr class="expand-child">
                <td colspan="${mostSubmissions + 3}"></td>
              </tr>
            </s:if>
          </s:iterator>
        </tbody>
      </table>
    </div>
  </form>
  <script>
    $(function () {
      var checked = true;
      function checkAll() {
        if (checked == false) {
          checked = true;
        } else {
          checked = false;
        }
        var boxes = document.getElementsByName('userIds');
        for (var i = 0; i < boxes.length; i++) {
          boxes[i].checked = checked;
        }
      }}
    );
  </script>
</s:if>
<!-- STUDENT VIEW FOR QUIZ TASKS -->
<s:elseif test='task.isQuiz'>
  <table class="tablesorter" id="sortSubmissionTable">
    <%-- HEADERS --%>
    <thead>
      <tr>
        <th>${studentTXT}</th>
          <s:if test="mostSubmissions > 0">
          <th>${currentTXT}</th>
            <s:iterator begin="2" end="mostSubmissions" status="loop">
            <th><i>${previousTXT}&nbsp;${loop.index + 1}</i></th>
            </s:iterator>
          </s:if>
      </tr>
    </thead>
    <%-- DATA ROWS --%>
    <tbody>
      <tr>
        <%-- STUDENT --%>
        <th>
          ${student.lastName}, ${student.firstName}
          <br /> ${student.loginName}<br />
          <s:if test="submissionPeriodActive && (createRights || ownerCreateRights)">
            <s:url action="createSubmission" var="createSubmissionUrl">
              <s:param name="taskId" value="taskId" />
              <s:param name="tabId" value="tabId" />
              <s:param name="dbId" value="dbId" />
              <s:param name="submitterId" value="#student.userId" />
            </s:url>
            <s:a href="%{createSubmissionUrl}" cssClass="linkButton">
              ${addSubmissionTXT}
            </s:a>
          </s:if>
        </th>
        <%-- SUBMISSIONS --%>
        <s:if test="mostSubmissions > 0">
          <s:if test="submissionPeriodActive && deleteRights">
            <s:set var="canDelete" value="true" />
          </s:if>
          <s:else>
            <s:set var="canDelete" value="false" />
          </s:else>
          <s:iterator var="submission" value="submissions" status="loop">
            <s:if test="#submission.status == notSubmittedState">
              <s:set var="statusClass" scope="page">yellow</s:set>
            </s:if>
            <s:elseif test="#loop.index == 0">
              <s:if test="#submission.status == acceptedState">
                <s:if test="#submission.message != null">
                  <s:set var="statusClass" scope="page">yellow</s:set>
                </s:if>
                <s:elseif test="#submission.error == null">
                  <s:set var="statusClass" scope="page">green</s:set>
                </s:elseif>
                <s:elseif test="#submission.error != null">
                  <s:set var="statusClass" scope="page">red</s:set>
                </s:elseif>
                <s:else>
                  <s:set var="statusClass" scope="page">none</s:set>
                </s:else>
              </s:if>
              <s:elseif test="#submission.status == notAcceptedState">
                <s:set var="statusClass" scope="page">red</s:set>
              </s:elseif>
              <s:else>
                <s:set var="statusClass" scope="page">none</s:set>
              </s:else>
            </s:elseif>
            <s:else>
              <s:set var="statusClass" scope="page">grey</s:set>
            </s:else>
            <td class="${statusClass}">
              <s:url action="viewSubmission" var="viewSubmissionUrl">
                <s:param name="taskId" value="taskId" />
                <s:param name="tabId" value="tabId" />
                <s:param name="dbId" value="dbId" />
                <s:param name="submissionId" value="#submission.id" />
              </s:url>
              <s:if test="(#submission.status == notSubmittedState) &&
                    submissionPeriodActive && updateRights">
                <s:a href="%{viewSubmissionUrl}" cssClass="linkButton">
                  ${editTXT}
                </s:a>
                <s:if test="#canDelete">
                  <s:url action="deleteSubmission" var="deleteSubmissionUrl">
                    <s:param name="taskId" value="taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="submissionId" value="#submission.id" />
                  </s:url>
                  <s:a href="%{deleteSubmissionUrl}" cssClass="linkButton">
                    ${deleteTXT}
                  </s:a>
                </s:if>
              </s:if>
              <s:else>
                <s:a href="%{viewSubmissionUrl}" cssClass="linkButton">
                  ${viewTXT}
                </s:a>
                <s:if test="#canDelete">
                  <s:url action="deleteSubmission" var="deleteSubmissionUrl">
                    <s:param name="taskId" value="taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                    <s:param name="submissionId" value="#submission.id" />
                  </s:url>
                  <s:a href="%{deleteSubmissionUrl}" cssClass="linkButton">
                    ${deleteTXT}
                  </s:a>
                </s:if>
              </s:else>
              <br />
              ${submission.timeStampString}<br />
              <s:if test="#loop.index > 0">
                (<del><s:text name="%{submissionStates[#submission.status].property}" /></del>)
              </s:if>
              <s:else>
                <s:text name="%{submissionStates[#submission.status].property}" />
              </s:else>
            </td>
          </s:iterator>
          <s:if test="submissions.size() < mostSubmissions">
            <td colspan="${mostSubmissions - submissions.size()}" class="filler">
            </td>
          </s:if>
          <s:elseif test="submissions.size() < 1">
            <td colspan="${mostSubmissions}" class="filler">
            </td>
          </s:elseif>
        </s:if>
      </tr>
      <tr class="expand-child">
        <td colspan="${mostSubmissions + 1}"></td>
      </tr>
    </tbody>
  </table>
</s:elseif>
<!-- STUDENT VIEW -->
<s:else>
  <!-- No file patterns => submission are not allowed (yet) -->
  <s:if test="!allowedFilePatternsDefined">
    <p><strong>${studentFilePatternsUndefinedTXT}</strong></p>
  </s:if>
  <s:else>
    <!-- Interactive react submission -->
    <div id="submissionsList-container"></div>
    <script src="js/components/js/DocumentTable.js"></script>
    <script src="js/components/js/SubmissionCreateFileForm.js"></script>
    <script src="js/components/js/SubmissionsList.js"></script>
    <script src="js/components/js/SubmissionDropZone.js"></script>
    <script src="js/components/js/SubmissionTitle.js"></script>
    <script src="js/components/js/TestScoreTable.js"></script>
    <script>
    $(function () {
      var submList = React.createElement(SubmissionsList,
              {
                // General options
                taskId: ${taskId}, tabId: ${tabId}, dbId: ${dbId},
                submissionPeriodActive: ${submissionPeriodActive},
                submitterId: ${courseUserId},
                hasGrades: ${task.hasGrades},
                // How many submissions are shown at a time
                submissionsLimit: 10,
                // Language
                createVerb: "<s:text name='general.header.create'/>",
                createHeader: "<s:text name='submissions.header.createOnlineFile' />",
                documentTableHeader: "<s:text name='submissions.header.files' />",
                dropzoneHeader: "<s:text name='submissions.header.addFilesToSubmission' />",
                submitButtonText: "<s:text name='submissions.header.completeSubmission' />",
                deleteVerb: "<s:text name='general.header.delete' />",
                confirmDeleteText: "<s:text name='submissions.header.reallyDeleteSubmission' />",
                cancelText: "<s:text name='general.header.cancel' />",
                gradeLinkText: "<s:text name='submissions.header.gradesForThisTask' />",
                zippingInstructions: "<s:text name='submissions.instructions.zippingAllowed' />",
                current: "<s:text name='submissions.header.current' />",
                previous: "<s:text name='submissions.header.previous' />",
                testRunButtonText: "<s:text name='submissions.header.testRun' />",
                autoGradeMarkHeader: "<s:text name='autograding.header.autogradeScore' />",
                compilerOutputHeader: "<s:text name='autograding.header.compilerOutput' />",
                errorHeader: "<s:text name='general.header.errors' />",
                errorMessageHeader: "<s:text name='errors.header' />",
                fileNamesNotAllowedHeader: "<s:text name='submissions.error.fileNameWithoutParam' />"
              }
      );
      React.render(submList, document.getElementById("submissionsList-container"));
    });
    </script>
  </s:else>
</s:else>
