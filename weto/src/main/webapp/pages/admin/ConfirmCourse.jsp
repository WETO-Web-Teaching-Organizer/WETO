<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h3><s:text name="addCourse.confirm.infoTitle"/></h3>
  <form action="<s:url action="confirmCourse" />" method="post">
    <input type="hidden" name="courseName" value="<s:property value="%{courseName}" />" />
    <input type="hidden" name="mainTeacherIdAndName" value="<s:property value="%{mainTeacherIdAndName}" />" />
    <input type="hidden" name="subjectIdAndName" value="<s:property value="%{subjectIdAndName}" />" />
    <input type="hidden" name="databaseIdAndName" value="<s:property value="%{databaseIdAndName}" />" />
    <input type="hidden" name="startDate" value="${startDate}" />
    <input type="hidden" name="endDate" value="${endDate}" />
    <table class="tablesorter" id="acTable">
      <thead></thead>
      <tbody>
        <tr>
          <td><s:text name="addCourse.confirm.showCourseName"/></td>
          <td>${courseName}</td>
        </tr>
        <tr>
          <td><s:text name="addCourse.confirm.showTeacherName"/></td>
          <td>${mainTeacherIdAndName.split("=")[1]}</td>
        </tr>
        <tr>
          <td><s:text name="addCourse.confirm.showCourseSubject"/></td>
          <td>${subjectIdAndName.split("=")[1]}</td>
        </tr>
        <tr>
          <td><s:text name="addCourse.confirm.showDatabase"/></td>
          <td>${databaseIdAndName.split("=")[1]}</td>
        </tr>
        <tr>
          <td><s:text name="addCourse.confirm.showStartDate"/></td>
          <td>${startDate}</td>
        </tr>
        <tr>
          <td><s:text name="addCourse.confirm.showEndDate"/></td>
          <td>${endDate}</td>
        </tr>
      </tbody>
    </table>
    <table>
      <tr>
        <td><input type="submit" name="action:addCourse" value="<s:text name="addCourse.confirm.previous" />" class="linkButton" /></td>
        <td><input type="submit" name="action:commitCourse" value="<s:text name="general.header.confirm" />" class="linkButton" /></td>
      </tr>
    </table>
  </form>
</div>

<script>
  $(function () {
    $(acTable).tablesorter({
      headers: {
        0: {sorter: false},
        1: {sorter: false}
      },
      widgets: ['zebra']
    });
  });
</script>