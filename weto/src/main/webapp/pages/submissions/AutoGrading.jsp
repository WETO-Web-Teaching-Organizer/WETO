<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="autograding.title" /></h2>
<p>
  <s:if test="autoGraded">
    <s:text name="autograding.instructions.existing" />
  </s:if>
  <s:else>
    <s:text name="autograding.instructions.new" />
  </s:else>
</p>
<table>
  <tr>
    <td style="vertical-align: top">
      <div class="contentBox">
        <form action="<s:url action="saveAutoGrading" />" method="post" enctype="multipart/form-data">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <h4><s:text name="autograding.header.properties" /></h4>
          <table>
            <tr>
              <td colspan="2"><s:textarea name="properties" cols="50" rows="25" /></td>
            </tr>
            <s:if test="autoGradingDocument != null">
              <tr>
                <td><s:text name="grading.header.oldDocument" />:</td>
                <td>
                  <s:url action="downloadAutograding" var="downloadAutogradingURL">
                    <s:param name="taskId" value="taskId" />
                    <s:param name="tabId" value="tabId" />
                    <s:param name="dbId" value="dbId" />
                  </s:url>
                  <s:a href="%{downloadAutogradingURL}" target="_blank">
                    ${autoGradingDocument.fileName}
                  </s:a>
                </td>
              </tr>
              <tr>
                <td><s:text name="general.header.fileSize" />:</td>
                <td>
                  ${autoGradingDocument.contentFileSize}
                </td>
              </tr>
            </s:if>
            <tr>
              <td><s:text name="grading.header.newDocument" />:</td>
              <td><input type="file" name="document" /></td>
            </tr>
            <tr>
              <s:if test="autoGraded">
                <td>
                  <input type="submit" value="<s:text name="general.header.saveChanges" />" class="linkButton" />
                </td>
                <td>
                  <input type="submit" name="action:deleteAutoGrading" value="<s:text name="autograding.header.remove" />" class="linkButton" />
                </td>
              </s:if>
              <s:else>
                <td>
                  <input type="submit" value="<s:text name="general.header.create" />" class="linkButton" />
                </td>
                <td>
                </td>
              </s:else>
            </tr>
          </table>
        </form>
      </div>
    </td>
    <s:if test="submissionId != null">
      <td style="vertical-align: top">
        <div class="contentBox">
          <h4><s:text name="autograding.header.testSubmission" /></h4>
          <table>
            <%-- Author --%>
            <tr>
              <th class="topLeft"><s:text name="general.header.user" />:</th>
              <td>
                ${navigator.user.lastName}, ${navigator.user.firstName}
              </td>
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
                <s:text name="%{submissionStates[submission.status]}" />
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
              <td>
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
                          <s:a href="%{downloadDocumentURL}" target="_blank" cssClass="linkButton">
                            <s:text name="general.header.download" />
                          </s:a>
                          <s:if test="#document.contentMimeType.startsWith('text')">
                            <s:url action="editTestTextDocument" var="editDocumentURL">
                              <s:param name="taskId" value="taskId" />
                              <s:param name="tabId" value="tabId" />
                              <s:param name="dbId" value="dbId" />
                              <s:param name="documentId" value="#document.id" />
                            </s:url>
                            <s:a href="%{editDocumentURL}" cssClass="linkButton">
                              <s:text name="general.header.edit" />
                            </s:a>
                          </s:if>
                          <s:url action="deleteTestDocument" var="deleteDocumentURL">
                            <s:param name="taskId" value="taskId" />
                            <s:param name="tabId" value="tabId" />
                            <s:param name="dbId" value="dbId" />
                            <s:param name="documentId" value="#document.id" />
                          </s:url>
                          <s:a href="%{deleteDocumentURL}" cssClass="linkButton">
                            <s:text name="general.header.delete" />
                          </s:a>
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
                          <pre><s:property value="submission.message" escapeHtml="false"/></pre>
                          <s:if test="compilerResultId != null">
                            <s:url action="viewFeedback" var="viewCompilerResultURL">
                              <s:param name="taskId" value="taskId" />
                              <s:param name="tabId" value="tabId" />
                              <s:param name="dbId" value="dbId" />
                              <s:param name="submissionId" value="submissionId" />
                              <s:param name="tagId" value="compilerResultId" />
                            </s:url>
                            <s:a href="%{viewCompilerResultURL}" target="_blank" cssClass="linkButton">
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
                <th class="topLeft" colspan="2">
                  <s:text name="autograding.header.testScores" />:
                </th>
              </tr>
              <tr>
                <td colspan="2">
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
                            <pre class="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px"><s:property value="#testScore.feedback" /></pre>
                          </td>
                        </tr>
                      </s:iterator>
                    </tbody>
                  </table>
                </td>
              </tr>
            </s:if>
          </table>
          <form action="<s:url action="viewAutoGrading" />" method="post">
            <input type="hidden" name="taskId" value="${taskId}" />
            <input type="hidden" name="tabId" value="${tabId}" />
            <input type="hidden" name="dbId" value="${dbId}" />
            <p><s:text name="submissions.header.createOnlineFile" />: <input type="text" name="documentFileFileName" size="30" />
              <input type="submit" value="<s:text name="general.header.create" />" class="smallLinkButton" /></p>
          </form>



          <s:url action="viewAutoGrading" var="viewAutoGradingUrl" />
          <form action="<s:url action="viewAutoGrading" />" method="post" enctype="multipart/form-data" class="dropzone" id="addFileDropzone">
            <p>
              <label><input type="checkbox" id="allowZippingBox" name="allowZipping" value="true" <s:if test="allowZipping">checked="checked"</s:if> />
                <s:text name="submissions.header.allowZipping" /></label>
              <br />
              <label><input type="checkbox" id="overWriteBox" name="overWriteExisting" value="true" <s:if test="overWriteExisting">checked="checked"</s:if> />
                <s:text name="taskDocuments.header.overWrite" /></label>
            </p>
            <div class="fallback">
              <input type="hidden" name="taskId" value="${taskId}" />
              <input type="hidden" name="tabId" value="${tabId}" />
              <input type="hidden" name="dbId" value="${dbId}" />
              <p><s:text name="submissions.header.uploadFile" />: <input type="file" name="documentFile" />
                <input type="submit" value="<s:text name="general.header.upload" />" class="smallLinkButton" /></p>
            </div>
          </form>
          <s:if test="documents != null && !documents.isEmpty()">
            <form action="<s:url action="submitTestSubmission" />" method="post" >
              <input type="hidden" name="taskId" value="${taskId}" />
              <input type="hidden" name="tabId" value="${tabId}" />
              <input type="hidden" name="dbId" value="${dbId}" />
              <input type="hidden" name="submissionId" value="${submissionId}" />
              <input type="hidden" name="submitterId" value="${courseUserId}" />
              <p><input type="submit" value="<s:text name="general.header.submit" />" class="linkButton" /></p>
            </form>
          </s:if>
        </div>
      </td>
    </s:if>
  </tr>
</table>
<s:if test="queuePos != null">
  <img id="ajaximg" src="images/ajax-blue.gif" />
</s:if>
<script>
  <s:if test="queuePos != null">
  // If state is "processing", start listening for updates
  var ajaximg = $("#ajaximg");
  ajaximg.css("position", "absolute");
  ajaximg.css("top", Math.max(0, (($(window).height() - ajaximg.outerHeight()) / 2) + $(window).scrollTop()) + "px");
  ajaximg.css("left", Math.max(0, (($(window).width() - ajaximg.outerWidth()) / 2) + $(window).scrollLeft()) + "px");

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

  setTimeout(function () {
    pollAutograding();
  }, 1000);
  </s:if>

  var baseUrl = '${viewAutoGradingUrl}?taskId=${taskId}&tabId=${tabId}&dbId=${dbId}';
    Dropzone.options.addFileDropzone =
            {
              paramName: 'documentFile',
              url: '${viewAutoGradingUrl}?taskId=${taskId}&tabId=${tabId}&dbId=${dbId}',
                          method: 'POST',
                          addRemoveLinks: false,
                          init: function ()
                          {
                            this.on("addedfile", function (file)
                            {
                              this.emit("thumbnail", file, "images/globalFile.png");
                            });
                            this.on("processing", function (file) {
                              var az = document.getElementById('allowZippingBox').checked ? '&allowZipping=true' : '&allowZipping=false';
                              var ow = document.getElementById('overWriteBox').checked ? '&overWriteExisting=true' : '&overWriteExisting=false';
                              this.options.url = baseUrl + az + ow;
                            });
                            this.on('queuecomplete', function (file)
                            {
                              var az = document.getElementById('allowZippingBox').checked ? '&allowZipping=true' : '&allowZipping=false';
                              var ow = document.getElementById('overWriteBox').checked ? '&overWriteExisting=true' : '&overWriteExisting=false';
                              window.location.search = 'taskId=${taskId}&tabId=${tabId}&dbId=${dbId}' + az + ow;
                            });
                          }
                        };
                colorDiffFeedback($(".diffCell"));
</script>