<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src ="js/chosen.jquery.min.js"></script>
<s:if test="courseTask">
  <div class="contentBox">
    <h3>
      <s:text name="taskSettings.title.course" />
    </h3>
    <form action="<s:url action="saveCourseSettings" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <div class ="form-group">
        <div>
          <div>
            <label>
              <input type="checkbox" name="acceptAllStudents" value="true"
                     <s:if test="acceptAllStudents">checked="checked"</s:if> />
              <s:text name="taskSettings.header.acceptAllStudents" />
            </label>
          </div>
        </div>
        <div>
          <input type="submit" value="<s:text name="taskSettings.header.saveCourse" />" class="linkButton" />
        </div>
      </div>
    </form>
  </div>
  <div class="contentBox">
    <h3><s:text name="editTeachers.title" /></h3>
    <p><s:text name="editTeachers.header.teachersAlreadyOnCourse" /></p>
    <div class="tablesorter" id="teacherTable">
      <div>
        <div>
          <th><s:text name="general.header.name" /></th>
          <th><s:text name="general.header.loginName" /></th>
          <th><s:text name="general.header.email" /></th>
          <th></th>
        </div>
      </div>
      <div>
        <s:set var="deleteButton" value="%{getText('general.header.delete')}" />
        <s:iterator var="courseTeacher" value="courseTeachers">
          <div>
            <div>${courseTeacher.lastName}, ${courseTeacher.firstName}</div>
            <div>${courseTeacher.loginName}</div>
            <div><a href="mailto:${courseTeacher.email}">${courseTeacher.email}</a></div>
            <div>
              <form action="<s:url action="deleteCourseTeacher" />" method="post">
                <input type="hidden" name="taskId" value="${taskId}" />
                <input type="hidden" name="tabId" value="${tabId}" />
                <input type="hidden" name="dbId" value="${dbId}" />
                <input type="hidden" name="courseTeacherId" value="${courseTeacher.userId}" />
                <input type="submit" value="${deleteButton}" class="linkButton" />
              </form>
            </div>
          </div>
        </s:iterator>
      </div>
    </div>
    <s:if test="teachers.isEmpty()">
      <s:text name="teachers.header.noTeachersAvailable" />
    </s:if>
    <s:else>
      <h4><s:text name="editTeachers.title.add" /></h4>
      <form action="<s:url action="addCourseTeacher" />" method="post">
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <select name="masterTeacherIds" data-placeholder="<s:text name="general.header.chooseUsers" />" class="chosen-select" multiple>
          <s:iterator var="teacher" value="teachers">
            <option value="${teacher.id}">
              ${teacher.loginName}
              &#32;
              ${teacher.firstName}
              &#32;
              ${teacher.lastName}</option>
            </s:iterator>
        </select>
        <input type="submit" value="<s:text name="general.header.add" />" class="linkButton" />
      </form>
    </s:else>
  </div>
  <div class="contentBox">
    <h3><s:text name="taskSettings.title.css" /></h3>
    <form action="<s:url action="editCourseCss" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <input type="submit" value="<s:text name="taskSettings.header.editCss" />" class="linkButton" />
    </form>
  </div>
  <script>
    $(function ()
    {
      $(".chosen-select").chosen();
      $("#teacherTable").tablesorter({
        headers: {
          3: {
            sorter: false
          }
        },
        widgets: ['zebra']
      });
    });
  </script>
</s:if>
<h3>
  <s:text name="taskSettings.title.task" />
</h3>
<form action="<s:url action="saveTaskSettings" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <s:text name="editTask.header.showTheseTabs" />:
  <div class="form-group">
    <label><input type="checkbox" name="hasSubmissions" value="true"
                  <s:if test="hasSubmissions">checked="checked"</s:if> />
      <s:text name="general.header.submissions" /></label><br />
    <label><input type="checkbox" name="hasGrades" value="true"
                  <s:if test="hasGrades">checked="checked"</s:if> />
      <s:text name="general.header.grades" /></label><br />
    <label><input type="checkbox" name="hasForum" value="true"
                  <s:if test="hasForum">checked="checked"</s:if> />
      <s:text name="general.header.forum" /></label><br />
    <label><input type="checkbox" name="hasGroups" value="true"
                  <s:if test="hasGroups">checked="checked"</s:if> />
      <s:text name="general.header.groups" /></label><br />
  </div>
  <s:text name="general.header.otherOptions" />:
  <div class="form-group">
    <label><input type="checkbox" name="quiz" value="true"
                  <s:if test="quiz">checked="checked"</s:if> />
      <s:text name="general.header.quiz" /></label><br />

    <label><input type="checkbox" name="showTextInParent" value="true"
                  <s:if test="showTextInParent">checked="checked"</s:if> />
      <s:text name="editTask.header.showTextInParent" /></label><br />

    <label><input type="checkbox" name="hidden" value="true"
                  <s:if test="hidden">checked="checked"</s:if> />
      <s:text name="general.header.hidden" /></label><br />
    <label><input type="checkbox" name="publicTask" value="true"
                  <s:if test="publicTask">checked="checked"</s:if> />
      <s:text name="general.header.public" /></label><br />
  </div>
  <div class="form-group">
    <s:text name="taskSettings.header.saveSettingsFor" />:
    <label><input type="radio" name="applySettingsToSubtasks" value="false" checked="checked" />&nbsp;<s:text name="taskSettings.header.forThisTask" /></label>
    <label><input type="radio" name="applySettingsToSubtasks" value="true" />&nbsp;<s:text name="taskSettings.header.alsoToSubtasks" /></label>
    (<label><input type="checkbox" name="changesOnly" value="true" />&nbsp;<s:text name="taskSettings.header.changesOnly" /></label>)
  </div>
  <input type="submit" value="<s:text name="taskSettings.header.saveTask" />" class="linkButton" />
</form>