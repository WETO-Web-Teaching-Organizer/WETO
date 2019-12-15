<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="students.header.importStudents" /></h2>
<s:text name="students.instructions.import" />
<div class="contentBox">
  <h4><s:text name="students.header.formSubmit" /></h4>
  <form action="<s:url action="importStudents" />" method="post" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <p>
      <s:textarea name="studentsText" rows="20" cols="65" /><br />
      <input type="submit" value="<s:text name="general.header.submit" />" class="linkButton" />
    </p>
  </form>
</div>
<div class="contentBox">
  <h4><s:text name="students.header.fileUpload" /></h4>
  <form action="<s:url action="importStudents" />" method="post" enctype="multipart/form-data">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="file" name="studentsFile" />
    <input type="submit" value="<s:text name="general.header.upload" />" class="linkButton" />
  </form>
</div>
