<%@ include file="/WEB-INF/taglibs.jsp"%>
<li>
  <s:url action="exportStudents" var="exportStudentsUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="1" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{exportStudentsUrl}">
    <s:text name="students.header.exportStudents" />
  </s:a>
</li>
<li>
  <s:url action="importStudents" var="importStudentsUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="1" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{importStudentsUrl}">
    <s:text name="students.header.importStudents" />
  </s:a>
</li>
