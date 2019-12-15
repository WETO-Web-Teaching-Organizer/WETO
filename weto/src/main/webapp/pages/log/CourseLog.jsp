<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2>
  <s:text name="log.title.courseLog" />
</h2>
<s:if test="showSummaryStatistics">
  <table>
    <thead>
      <tr>
        <th><s:property value="getText('log.header.summaryStatistics').toUpperCase()" /></th>
        <th><s:text name="log.header.lastDay" /></th>
        <th><s:text name="log.header.lastWeek" /></th>
        <th><s:text name="log.header.lastMonth" /></th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>
          <s:text name="log.header.uniqueUsers" />
        </td>
        <td>${userStats[0]}</td>
        <td>${userStats[1]}</td>
        <td>${userStats[2]}</td>
      </tr>
      <tr>
        <td>
          <s:text name="general.header.submissions" />
        </td>
        <td>${submissionStats[0]}</td>
        <td>${submissionStats[1]}</td>
        <td>${submissionStats[2]}</td>
      </tr>
      <tr>
        <td>
          <s:text name="log.header.uniqueSubmitters" />
        </td>
        <td>${submitterStats[0]}</td>
        <td>${submitterStats[1]}</td>
        <td>${submitterStats[2]}</td>
      </tr>
    </tbody>
  </table>
</s:if>
<s:else>
  <form action="<s:url action="viewCourseLog" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="loginName" value="${loginName}" />
    <input type="hidden" name="logTaskId" value="${logTaskId}" />
    <input type="hidden" name="eventId" value="${eventId}" />
    <input type="hidden" name="from" value="${from}" />
    <input type="hidden" name="to" value="${to}" />
    <input type="hidden" name="maxRows" value="${maxRows}" />
    <input type="hidden" name="showSummaryStatistics" value="true" />
    <input type="submit" value="<s:text name="log.header.showSummaryStatistics" />" class="linkButton" />
  </form>
</s:else>
<div class="filter">
  <form action="<s:url action="viewCourseLog" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <s:text name="log.instructions.filter" />
    <table>
      <tr>
        <td>
          <label for="loginName"><s:text name="general.header.loginName" /></label>
          <input type="text" name="loginName" value="${loginName}" />
          <label for="logTaskId"><s:text name="log.header.taskId" /></label>
          <input type="text" size="10" name="logTaskId" value="${logTaskId}" />
          <label for="eventId"><s:text name="log.header.event" /></label>
          <select name="eventId">
            <s:iterator var="eventName" value="logEventNames">
              <option value="${key}" <s:if test="%{eventId == key}">selected="selected"</s:if> >
                ${value}
              </option>
            </s:iterator>
          </select>
          <br />
          <label for="from"><s:text name="general.header.from" /></label>
          <input type="text" name="from" value="${from}" id="from" />
          <label for="to"><s:text name="general.header.to" /></label>
          <input type="text" name="to" value="${to}" id="to" />
          <label for="maxRows"><s:text name="log.header.maxRows" /></label>
          <input type="text" size="10" name="maxRows" value="${maxRows}" />
        </td>
        <td>
          <input type="submit" value="<s:text name="general.header.update" />" class="linkButton" />
        </td>
      </tr>
    </table>
  </form>
</div>
<s:if test="!logRows.empty">
  <table class="tablesorter" id="logTable">
    <thead>
      <tr>
        <th>${logHeaders[0]}</th>
        <th>${logHeaders[1]}</th>
        <th>${logHeaders[2]}</th>
        <th>${logHeaders[3]}</th>
        <th>${logHeaders[4]}</th>
      </tr>
    </thead>
    <tbody>
      <s:iterator var="row" value="logRows" status="loop">
        <tr>
          <td>
            <s:url action="viewStudent" var="viewStudentUrl">
              <s:param name="taskId" value="%{taskId}" />
              <s:param name="tabId" value="%{tabId}" />
              <s:param name="dbId" value="%{dbId}" />
              <s:param name="studentId" value="%{logEventUserIds.get(#loop.index)}" />
            </s:url>
            <s:a href="%{viewStudentUrl}">
              ${row[0]}
            </s:a>
          </td>
          <td>
            <s:url action="viewTask" var="viewTaskUrl">
              <s:param name="taskId" value="%{logEventTaskIds.get(#loop.index)}" />
              <s:param name="tabId" value="%{mainTabId}" />
              <s:param name="dbId" value="%{dbId}" />
            </s:url>
            <s:a href="%{viewTaskUrl}">
              ${row[1]}
            </s:a>
          </td>
          <td>${row[2]}</td>
          <td>${row[3]}</td>
          <td>${row[4]}</td>
        </tr>
      </s:iterator>
    </tbody>
  </table>
</s:if>
<s:else>
  <p><s:text name="log.message.isEmpty" /></p>
</s:else>
<script>
  $(function () {
    $("#logTable").tablesorter({
      widgets: ['zebra']
    });
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
  });
</script>