<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="grading.header.view" /></h2>
<%-- GRADE VIEW  --%>
<div class="table-responsive">
  <table class="tablesorter table">
    <tr>
      <td>
        <table class="table">
          <%-- REVIEWER --%>
          <tr>
            <th class="topLeft">
              <s:text name="general.header.reviewer" />
            </th>
            <td>
              <s:if test="reviewer == null">
                <s:text name="general.header.anonymous" />
              </s:if>
              <s:else>
                <s:if test="navigator.teacher">
                  <s:url action="viewStudent" var="viewStudentUrl">
                    <s:param name="taskId" value="%{taskId}" />
                    <s:param name="tabId" value="%{tabId}" />
                    <s:param name="dbId" value="%{dbId}" />
                    <s:param name="studentId" value="%{#reviewer.id}" />
                  </s:url>
                  <s:a href="%{viewStudentUrl}" anchor="%{#reviewer.id }">
                    ${reviewer.lastName}, ${reviewer.firstName}
                  </s:a>
                </s:if>
                <s:else>
                  ${reviewer.lastName}, ${reviewer.firstName}
                </s:else>
              </s:else>
            </td>
          </tr>
          <%-- RECEIVER --%>
          <tr>
            <th class="topLeft">
              <s:text name="general.header.receiver" />
            </th>
            <td>
              <s:if test="receiver == null">
                <s:text name="general.header.anonymous" />
              </s:if>
              <s:else>
                <s:if test="navigator.teacher">
                  <s:url action="viewStudent" var="viewStudentUrl">
                    <s:param name="taskId" value="%{taskId}" />
                    <s:param name="tabId" value="%{tabId}" />
                    <s:param name="dbId" value="%{dbId}" />
                    <s:param name="studentId" value="%{#receiver.id}" />
                  </s:url>
                  <s:a href="%{viewStudentUrl}" anchor="%{#receiver.id }">
                    ${receiver.lastName}, ${receiver.firstName}
                  </s:a>
                </s:if>
                <s:else>
                  ${receiver.lastName}, ${receiver.firstName}
                </s:else>
              </s:else>
            </td>
          </tr>
          <%-- MARK --%>
          <tr>
            <th class="topLeft"><s:text name="general.header.mark" /></th>
            <td>${grade.mark}</td>
          </tr>
          <%-- GRADING DATE --%>
          <tr>
            <th class="topLeft">
              <s:text name="general.header.date" />
            </th>
            <td>
              ${grade.timeStampString}
            </td>
          </tr>
        </table>
      </td>
      <%-- SUBMISSION VIEW --%>
      <s:if test="submission != null">
        <td>
          <div class="contentBox">
            <h4>
              <s:text name="submissions.header.latestSubmission" />
              <s:url action="viewSubmission" var="viewSubmissionUrl">
                <s:param name="taskId" value="taskId" />
                <s:param name="tabId" value="submissionsTabId" />
                <s:param name="dbId" value="dbId" />
                <s:param name="submissionId" value="submission.id" />
              </s:url>
              <s:a href="%{viewSubmissionUrl}" cssClass="linkButton">
                <s:text name="general.header.view" />
              </s:a>
            </h4>
            <table>
              <%-- LAST MODIFIED --%>
              <tr>
                <th class="topLeft">
                  <s:text name="general.header.lastModified" />:
                </th>
                <td>
                  ${submission.timeStampString}
                </td>
              </tr>
              <%-- FILE LIST --%>
              <tr>
                <th class="topLeft">
                  <s:text name="general.header.files" />:
                </th>
                <td>
                  <table class="dataTable">
                    <tr>
                      <th class="topLeft">
                        <s:text name="general.header.fileName" />
                      </th>
                      <th class="topLeft">
                        <s:text name="general.header.fileSize" />
                      </th>
                    </tr>
                    <s:iterator var="document" value="documents">
                      <tr>
                        <td>
                          <s:url action="downloadDocument" var="downloadDocumentURL">
                            <s:param name="taskId" value="%{taskId}" />
                            <s:param name="tabId" value="%{tabId}" />
                            <s:param name="dbId" value="%{dbId}" />
                            <s:param name="documentId" value="#document.id" />
                          </s:url>
                          <s:a href="%{downloadDocumentURL}">
                            ${document.fileName}
                          </s:a>
                        </td>
                        <td>
                          ${document.contentFileSize}
                        </td>
                      </tr>
                    </s:iterator>
                  </table>
                </td>
              </tr>
            </table>
          </div>
        </td>
      </s:if>
    </tr>
  </table>
</div>
<%-- REVIEWS --%>
<s:if test="!reviews.isEmpty()">
  <div class="contentBox">
    <h3><s:text name="general.header.reviews" /></h3>
    <s:iterator var="review" value="reviews" status="loop">
      <p>
        <i>
          <s:if test="authors[#loop.index] != null">
            ${authors[loop.index]}
          </s:if>
          <s:else>
            <s:text name="general.header.anonymous" />
          </s:else>
          :
        </i>
        <br />
      </p>
      ${review.text}
      <p>
        <small>
          ${review.timeStampString}
        </small>
      </p>
    </s:iterator>
  </div>
</s:if>
