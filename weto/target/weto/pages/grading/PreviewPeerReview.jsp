<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="peerReview.title.reviewers" /></h2>
<form action="<s:url action="setupPeerReview" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="form.reviewerCount" value="${form.reviewerCount}" />
  <input type="hidden" name="form.omitRootTaskGrades" value="${form.omitRootTaskGrades}" />
  <input type="hidden" name="form.reviewOwnSubmission" value="${form.reviewOwnSubmission}" />
  <input type="hidden" name="form.overwritePreviousGrades" value="${form.overwritePreviousGrades}" />
  <input type="hidden" name="form.createPermissions" value="${form.createPermissions}" />
  <input type="hidden" name="form.permissionDate" value="${form.permissionDate}" />
  <input type="hidden" name="form.minScore" value="${form.minScore}" />
  <input type="hidden" name="form.maxScore" value="${form.maxScore}" />
  <s:iterator value="form.createSubtaskGrades" var="selectedSubtaskId">
    <input type="hidden" name="form.createSubtaskGrades"
           value="${selectedSubtaskId}" />
  </s:iterator>
  <s:iterator value="allReviewersForSubmitters" var="revGroupList">
    <s:iterator value="#revGroupList" var="revGroup">
      <s:iterator value="#revGroup.reviewers" var="reviewer">
        <input type="hidden" name="reviewInfoStrings"
               value="${revGroup.taskId};${revGroup.reviewee.id}=${reviewer.id}" />
      </s:iterator>
    </s:iterator>
  </s:iterator>
  <table class="dataTable">
    <thead>
      <tr>
        <th><s:text name="peerReview.header.reviewee" /></th>
        <th><s:text name="peerReview.header.reviewers" /></th>
      </tr>
    </thead>
    <tbody>
      <s:iterator value="allReviewersForSubmitters" var="revGroupList">
        <tr>
          <td colspan="2">${revGroupList.get(0).taskName}</td>
        </tr>
        <s:iterator value="#revGroupList" var="revGroup">
          <tr>
            <td>
              ${revGroup.reviewee.getFirstName()}&#32;
              ${revGroup.reviewee.lastName}&#32;
              (${revGroup.reviewee.loginName})
            </td>
            <td>
              <s:iterator value="#revGroup.reviewers" var="reviewer">
                ${reviewer.firstName}&#32;${reviewer.lastName}&#32;(${reviewer.loginName})<br />
              </s:iterator>
            </td>
          </tr>
        </s:iterator>
      </s:iterator>
    </tbody>
  </table>
  <table>
    <tr>
      <td><input type="submit" name="step" value="<s:text name="peerReview.header.backToSetup" />" class="linkButton" /></td>
      <td><input type="submit" name="step" value="<s:text name="peerReview.header.save" />" class="linkButton" /></td>
    </tr>
  </table>
</form>