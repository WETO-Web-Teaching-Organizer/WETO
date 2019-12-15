<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src ="js/chosen.jquery.min.js"></script>
<div class="content-col">
  <div class="contentBox">
    <h2><s:text name="teachers.title.edit" /></h2>
    <p><s:text name="teachers.header.existingTeachers" /></p>
    <table class="tablesorter" id="teacherTable">
      <thead>
        <tr>
          <th><s:text name="general.header.name" /></th>
          <th><s:text name="general.header.loginName" /></th>
          <th><s:text name="general.header.email" /></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <s:set var="deleteButton" value="%{getText('general.header.delete')}" />
        <s:iterator var="teacher" value="teachers">
          <tr>
            <td>${teacher.lastName}, ${teacher.firstName}</td>
            <td>${teacher.loginName}</td>
            <td><a href="mailto:${teacher.email}">${teacher.email}</a></td>
            <td>
              <form action="<s:url action="adminDeleteTeacher" />" method="post">
                <input type="hidden" name="teacherUserId" value="${teacher.id}" />
                <input type="submit" value="${deleteButton}" class="linkButton" />
              </form>
            </td>
          </tr>
        </s:iterator>
      </tbody>
    </table>
    <table>
      <tr>
        <td>
          <s:if test="userList.isEmpty()">
            <s:text name="teachers.header.noTeachersAvailable" />
          </s:if>
          <s:else>
            <form action="<s:url action="adminAddTeacher" />" method="post">
              <select name="teacherUserId" data-placeholder="<s:text name="general.header.chooseUsers" />" class="chosen-select" multiple>
                <s:iterator var="user" value="userList">
                  <option value="${user.id}">${user.loginName}&#32;${user.firstName}&#32;${user.lastName}</option>
                </s:iterator>
              </select>
              <input type="submit" value="<s:text name="general.header.add" />" class="linkButton" />
            </form>
          </s:else>
        </td>
      </tr>
    </table>
  </div>
  <div class="contentBox">
    <h2><s:text name="courseTeachers.title.add" /></h2>
    <p><s:text name="courseTeachers.title.existingTeachers" /></p>
    <table class="tablesorter" id="courseTeacherTable">
      <thead>
        <tr>
          <th><s:text name="general.header.course" /></th>
          <th><s:text name="general.header.name" /></th>
          <th><s:text name="general.header.loginName" /></th>
          <th><s:text name="general.header.email" /></th>
        </tr>
      </thead>
      <tbody>
        <s:iterator value="courseTeachers" var="ct">
          <tr>
            <td>${ct[0]}</td>
            <td>${ct[1].lastName}, ${ct[1].firstName}</td>
            <td>${ct[1].loginName}</td>
            <td>${ct[1].email}</td>
          </tr>
        </s:iterator>
      </tbody>
    </table>
    <form action="<s:url action="adminViewTeachers" />" method="post">
      <table>
        <tr>
          <td>
            <s:text name="general.header.selectTeacher" />
          </td>
          <td>
            <select name="teacherUserId" data-placeholder="<s:text name="general.header.chooseUsers" />" class="chosen-select" multiple>
              <s:iterator var="user" value="teachers">
                <option value="${user.id}">${user.loginName}&#32;${user.firstName}&#32;${user.lastName }</option>
              </s:iterator>
            </select>
          </td>
        </tr>
        <tr>
          <td>
            <s:text name="general.header.selectCourse" />
          </td>
          <td>
            <select name="courseTaskId">
              <s:iterator var="course" value="courses">
                <option value="${course.masterTaskId}"> ${course.name}</option>
              </s:iterator>
            </select>
          </td>
        </tr>
        <tr>
          <td>
            <input type="submit" name="action:adminAddCourseTeacher" value="<s:text name="general.header.add" />" class="linkButton" />
          </td>
          <td>
            <input type="submit" name="action:adminDeleteCourseTeacher" value="<s:text name="general.header.delete" />" class="linkButton" />
          </td>
        </tr>
      </table>
    </form>
  </div>
</div>
<script>
  $(function () {
    $(".chosen-select").chosen();
    $("#teacherTable").tablesorter({widgets: ['zebra']});
    $("#courseTeacherTable").tablesorter({widgets: ['zebra']});
  });
</script>
