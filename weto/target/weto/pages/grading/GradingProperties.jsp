<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="grading.header.gradingProperties" /></h2>
<div class="contentBox">
  <h3><s:text name="grading.title.gradeTable" /></h3>
  <p><s:text name="gradingProperties.instructions" /></p>
  <div class="contentBox">
    <s:if test="gradeTable.size() > 0">
      <h4><s:text name="gradingProperties.header.gradeTableEntries" /></h4>
      <form action="<s:url action="viewGradingProperties" />" method="post">
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <div class="table-responsive">
          <table class="table tablesorter gradeTable">
            <thead>
              <tr>
                <th><s:text name="general.header.score" /></th>
                <th><s:text name="general.header.mark" /></th>
              </tr>
            </thead>
            <tbody>
              <s:iterator var="entry" value="gradeTable">
                <tr>
                  <td>
                    <input type="checkbox" name="checkedEntries" value="${entry[0]}" />
                    <input type="text" name="entryScores" value="${entry[0]}" required pattern="[<>]?[0-9]*\.?[0-9]*" />
                  </td>
                  <td>
                    <input type="text" name="entryMarks" value="${entry[1]}" pattern="[0-9]*\.?[0-9]*" />
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>
        <input type="submit" name="action:saveGradeTable" value="<s:text name="general.header.saveChanges" />" class="linkButton" />
        <input type="submit" name="action:deleteFromGradeTable" value="<s:text name="general.header.deleteEntries" />" class="linkButton" />
      </form>
    </s:if>
    <s:else>
      <p><strong><s:text name="gradingProperties.header.emptyGradingTable" /></strong></p>
    </s:else>
  </div>
  <div class="contentBox">
    <h4><s:text name="gradingProperties.title.addGradeTableEntry" /></h4>
    <form action="<s:url action="addToGradeTable" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <div class="table-responsive">
        <table class="table tablesorter gradeTable">
          <thead>
            <tr>
              <th><s:text name="general.header.score" /></th>
              <th><s:text name="general.header.mark" /></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><input type="text" name="score" value="" required pattern="[<>]?[0-9]*\.?[0-9]*" /></td>
              <td><input type="text" name="mark" value="" pattern="[0-9]*\.?[0-9]*" /></td>
            </tr>
          </tbody>
        </table>
      </div>
      <input type="submit" value="<s:text name="gradingProperties.header.addGradeTableEntry" />" class="linkButton" />
    </form>
  </div>
  <div class="contentBox">
    <form action="<s:url action="generateGradeTable" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <h4><s:text name="gradingProperties.header.generate" /></h4>
      <table class="tablesorter span1" >
        <tbody>
          <tr>
            <td >
              <s:text name="grading.header.lowerBound" />
              &#32;
              <s:text name="general.header.score" />
              &#32;
              <input type="text" name="lowerScore" size="3" required pattern="[0-9]*\.?[0-9]*" />
              &#32;
              <s:text name="general.header.mark" />
              &#32;
              <input type="text" name="lowerMark" size="3" required pattern="[0-9]*\.?[0-9]*" />
            </td>
            <td>
              <s:text name="grading.header.upperBound" />
              &#32;
              <s:text name="general.header.score" />
              &#32;
              <input type="text" name="upperScore" size="3" required pattern="[0-9]*\.?[0-9]*" />
              &#32;
              <s:text name="general.header.mark" />
              &#32;
              <input type="text" name="upperMark" size="3" required pattern="[0-9]*\.?[0-9]*" />
            </td>
            <td><s:text name="grading.header.roundMode" />:
              <select name="roundMode">
                <s:iterator var="type" value="{'None', 'Nearest', 'Floor', 'Ceiling'}">
                  <s:if test="#type == 'Ceiling'">
                    <option value="${type}" selected="selected">
                      <s:text name="grading.header.round%{type}" />
                    </option>
                  </s:if>
                  <s:else>
                    <option value="${type}">
                      <s:text name="grading.header.round%{type}" />
                    </option>
                  </s:else>
                </s:iterator>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
      <input type="submit" value="<s:text name="general.header.generate" />" class="linkButton" />
    </form>
  </div>
</div>
<div class="contentBox">
  <h3><s:text name="gradingProperties.header.otherProperties" /></h3>
  <form action="<s:url action="saveGradingProperties" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <table class="tablesorter span2" id="propertiesTable">
      <thead></thead>
      <tbody>
        <tr>
          <td><s:text name="grading.header.mandatoryTask" />:</td>
          <td>
            <input type="checkbox" name="mandatoryTask" value="true" <s:if test="mandatoryTask">checked="checked"</s:if> />
            </td>
          </tr>
          <tr>
            <td><s:text name="grading.header.requirePeerReview" />:</td>
          <td>
            <input type="checkbox" name="requirePeerReview" value="true" <s:if test="requirePeerReview">checked="checked"</s:if> />
            </td>
          </tr>
          <tr>
            <td><s:text name="grading.header.ignoreGroups" />:</td>
          <td>
            <input type="checkbox" name="ignoreGroups" value="true" <s:if test="ignoreGroups">checked="checked"</s:if> />
            </td>
          </tr>
          <tr>
            <td><s:text name="grading.header.roundMode" />:</td>
          <td>
            <select name="roundMode">
              <s:iterator var="type" value="{'None', 'Nearest', 'Floor', 'Ceiling'}">
                <s:if test="#type == roundMode">
                  <option value="${type}" selected="selected">
                    <s:text name="grading.header.round%{type}" />
                  </option>
                </s:if>
                <s:else>
                  <option value="${type}">
                    <s:text name="grading.header.round%{type}" />
                  </option>
                </s:else>
              </s:iterator>
            </select>
          </td>
        </tr>
        <s:if test="hasSubtasks">
          <tr>
            <td><s:text name="gradingProperties.header.failedSubtasksMax" />:</td>
            <td><input type="text" name="maxFailedSubtasks" value="${maxFailedSubtasks}" size="3" /></td>
          </tr>
          <tr>
            <td><s:text name="grading.header.aggregateFunction" />:</td>
            <td>
              <select name="aggregateFunction">
                <s:iterator var="type" value="aggregateFunctionTypes">
                  <s:if test="#type.value == aggregateFunction">
                    <option value="${type.value}" selected="selected">
                      <s:text name="%{#type.property}" />
                    </option>
                  </s:if>
                  <s:else>
                    <option value="${type.value}">
                      <s:text name="%{#type.property}" />
                    </option>
                  </s:else>
                </s:iterator>
              </select>
            </td>
          </tr>
        </s:if>
        <s:else>
          <tr>
            <td><s:text name="grading.header.minScore" />:</td>
            <td><input type="text" name="minScore" value="${minScore}" size="3" pattern="-?[0-9]*\.?[0-9]*" /></td>
          </tr>
          <tr>
            <td><s:text name="grading.header.maxScore" />:</td>
            <td><input type="text" name="maxScore" value="${maxScore}" size="3" pattern="-?[0-9]*\.?[0-9]*" /></td>
          </tr>
          <tr>
            <td><s:text name="grading.header.scoreStep" />:</td>
            <td><input type="text" name="scoreStep" value="${scoreStep}" size="3" pattern="[0-9]*\.?[0-9]*" /></td>
          </tr>
          <tr>
            <td><s:text name="grading.header.calculateAverageScore" /></td>
            <td>
              <input type="checkbox" name="calculateAverage" value="true"
                     <s:if test="calculateAverage">checked="checked"</s:if> />
              </td>
            </tr>
        </s:else>
        <tr>
          <td><s:text name="grading.header.latePenalties" />:</td>
          <td>
            <textarea name="latePenalties" rows="3">${latePenalties}</textarea>
          </td>
        </tr>
      </tbody>
    </table>
    <input type="submit" value="<s:text name="gradingProperties.header.saveOtherProperties" />" class="linkButton" />
  </form>
</div>
<script>
  $(function () {
    $("#propertiesTable").tablesorter({
      widgets: ['zebra']
    });
  });
</script>
