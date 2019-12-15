<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="peerReview.title.setup"/></h2>
<form action="<s:url action="setupPeerReview" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <table class="dataTable">
    <thead>
      <tr>
        <th><s:text name="peerReview.header.rootTaskName" />:</th>
        <th>${rootTask.name}</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td><s:text name="peerReview.header.submitterCount" />:</td>
        <td>${submitterCount}</td>
      </tr>
      <tr>
        <td><s:text name="peerReview.header.subtaskCount" />:</td>
        <td>${subtasks.size()}</td>
      </tr>
      <tr>
        <td><s:text name="peerReview.header.reviewerCount" />:</td>
        <td><input type="text" name="form.reviewerCount" value="${form.reviewerCount}" size="2" /></td>
      </tr>
      <tr>
        <td><s:text name="peerReview.header.reviewOwnSubmissions" /></td>
        <td>
          <input type="checkbox" name="form.reviewOwnSubmission" value="true"
                 <s:if test="form.reviewOwnSubmission">checked="checked"</s:if> />
          </td>
        </tr>
      <s:if test="hasPreviousGrades">
        <tr>
          <td><s:text name="peerReview.header.overwriteGrades" /></td>
          <td>
            <input type="checkbox" name="form.overwritePreviousGrades" value="true"
                   <s:if test="form.overwritePreviousGrades">checked="checked"</s:if> />
            </td>
          </tr>
      </s:if>
      <s:else>
        <tr>
          <td><s:text name="peerReview.header.createPermissions" /></td>
          <td>
            <input type="checkbox" name="form.createPermissions" value="true"
                   <s:if test="form.createPermissions">checked="checked"</s:if> />
            </td>
          </tr>
          <tr>
            <td><s:text name="peerReview.header.permissionDate" /></td>
          <td><input type="text" name="form.permissionDate" id="permissionDate" value="${form.permissionDate}" size="18" /></td>
        </tr>
        <tr>
          <td><s:text name="grading.header.minScore" />:</td>
          <td><input type="text" name="form.minScore" value="${form.minScore}" size="3" /></td>
        </tr>
        <tr>
          <td><s:text name="grading.header.maxScore" />:</td>
          <td><input type="text" name="form.maxScore" value="${form.maxScore}" size="3" /></td>
        </tr>
      </s:else>
    </tbody>
  </table>
  <h3><s:text name="peerReview.title.tasks"/></h3>
  <table class="dataTable">
    <s:if test="subtasks.size() == 1">
      <tr class="wizarderror">
        <td colspan="2"><s:text name="peerReview.header.noSubtasks"/></td>
      </tr>
    </s:if>
    <s:else>
      <thead>
        <tr>
          <td><s:text name="peerReview.header.omitRootTaskGrades" /></td>
          <td>
            <input type="checkbox" name="form.omitRootTaskGrades" value="true"
                   <s:if test="form.omitRootTaskGrades">checked="checked"</s:if> />
            </td>
          </tr>
          <tr class="wizarddivider">
            <td colspan="2"></td>
          </tr>
          <tr>
            <th colspan="2"><s:text name="peerReview.header.subtasks"/></th>
        </tr>
      </thead>
      <tbody>
        <tr class="wizarddivider">
          <td colspan="2"></td>
        </tr>
        <s:iterator value="subtasks" var="revTask" status="loop">
          <s:if test="!#loop.first">
            <tr>
              <td><s:text name="peerReview.header.subtaskName"/>:</td>
              <td>${revTask.name}</td>
            </tr>
            <%--            <tr>
                          <td><s:text name="grading.header.minScore"/>:</td>
                          <td>${revTask.minScore}</td>
                        </tr>
                        <tr>
                          <td><s:text name="grading.header.maxScore"/>:</td>
                          <td>${revTask.maxScore}</td>
                        </tr> --%>
            <tr>
              <td><s:text name="peerReview.header.createSubtaskGrades"/></td>
              <td>
                <input type="checkbox" name="form.createSubtaskGrades" value="${revTask.id}"
                       <s:if test="%{form.createSubtaskGrades.contains(#revTask.id)}">checked="checked"</s:if> />
                </td>
              </tr>
            <s:if test="!#loop.last">
              <tr class="wizarddivider">
                <td colspan="2"></td>
              </tr>
            </s:if>
          </s:if>
        </s:iterator>
      </tbody>
    </s:else>
  </table>
  <table>
    <tr>
      <td><input type="submit" name="step" value="<s:text name="peerReview.header.preview" />" class="linkButton" /></td>
    </tr>
  </table>
</form>
<script>
  $(function () {
    $.datepicker.setDefaults($.datepicker.regional["fi"]);
    $('#permissionDate').datetimepicker({
      stepMinute: 15
    });
  });
</script>