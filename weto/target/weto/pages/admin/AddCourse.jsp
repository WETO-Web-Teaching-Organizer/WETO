<%@ include file="/WEB-INF/taglibs.jsp"%>

<div class="content-col">
  <h2><s:text name="addcourse.courseinfo.heading" /></h2>
  <form action="<s:url action="confirmCourse" />" method="post">
    <table class="tablesorter" id="acTable">
      <thead>
      </thead>
      <tbody>
        <tr>
          <td><s:text name="addCourse.courseinfo.promptCoursename" /></td>
          <td><input type="text" name="courseName" value="${courseName}" size="80" autofocus required /></td>
        </tr>
        <tr>
          <td><s:text name="addCourse.courseinfo.promptTeacher" /></td>
          <td>
            <s:if test="%{teachers.isEmpty()}">
              <s:text name="addCourse.courseinfo.promptTeacherEmptyError" />
            </s:if>
            <s:else>
              <select name="mainTeacherIdAndName">
                <s:iterator var="entry" value="%{teachers}">
                  <s:set var="entryIdAndName" value="%{#entry.id+'='+#entry.firstName+' '+#entry.lastName}" />
                  <s:if test="#entryIdAndName.equals(mainTeacherIdAndName)">
                    <option value="<s:property value="#entryIdAndName" />" selected="selected">${entry.firstName}&#32;${entry.lastName}</option>
                  </s:if>
                  <s:else>
                    <option value="<s:property value="#entryIdAndName" />">${entry.firstName}&#32;${entry.lastName}</option>
                  </s:else>
                </s:iterator>
              </select>
            </s:else>
          </td>
        </tr>
        <tr>
          <td><s:text name="addCourse.courseinfo.promptSubject" /></td>
          <td>
            <s:if test="%{subjectIds.isEmpty()}">
              <s:text name="addCourse.courseinfo.promptSubjectEmptyError" />
            </s:if>
            <s:else>
              <select name="subjectIdAndName">
                <s:iterator var="entry" value="%{subjects}">
                  <s:set var="entryIdAndName" value="%{#entry.id+'='+#entry.name}" />
                  <s:if test="#entryIdAndName.equals(subjectIdAndName)">
                    <option value="<s:property value="#entryIdAndName" />" selected="selected">${entry.name}</option>
                  </s:if>
                  <s:else>
                    <option value="<s:property value="#entryIdAndName" />">${entry.name}</option>
                  </s:else>
                </s:iterator>
              </select>
            </s:else>
          </td>
        </tr>
        <tr>
          <td><s:text name="addCourse.courseinfo.promptDatabase" /></td>
          <td>
            <s:if test="%{databaseNames.isEmpty()}">
              <s:text name="addCourse.courseinfo.promptDatabaseEmptyError" />
            </s:if>
            <s:else>
              <select name="databaseIdAndName">
                <s:iterator var="entry" value="%{databases}">
                  <s:set var="entryIdAndName" value="%{#entry.id+'='+#entry.name}" />
                  <s:if test="#entryIdAndName.equals(databaseIdAndName)">
                    <option value="<s:property value="#entryIdAndName" />" selected="selected">${entry.name}</option>
                  </s:if>
                  <s:else>
                    <option value="<s:property value="#entryIdAndName" />">${entry.name}</option>
                  </s:else>
                </s:iterator>
              </select>
            </s:else>
          </td>
        </tr>
        <tr>
          <td><s:text name="addCourse.courseinfo.promptStartDate" /></td>
          <td>
            <input type="text" name="startDate" id="startDate" value="${startDate}" size="10" maxlength="10" />
            <s:text name="addCourse.courseinfo.dateFormat" />
          </td>
        </tr>
        <tr>
          <td><s:text name="addCourse.courseinfo.promptEndDate" /></td>
          <td>
            <input type="text" name="endDate" id="endDate" value="${endDate}" size="10" maxlength="10" />
            <s:text name="addCourse.courseinfo.dateFormat" />
          </td>
        </tr>
      </tbody>
    </table>
    <table>
      <tr>
        <td><input type="submit" value="<s:text name="addCourse.courseinfo.next" />" class="linkButton" /></td>
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
    $.datepicker.setDefaults($.datepicker.regional["fi"]);
    $("#startDate").datepicker({
      onSelect: function (selectedDate) {
        $("#endDate").datepicker("option", "minDate", selectedDate);
      }});
    $("#endDate").datepicker({
      onSelect: function (selectedDate) {
        $("#startDate").datepicker("option", "maxDate", selectedDate);
      }});
  });
</script>
