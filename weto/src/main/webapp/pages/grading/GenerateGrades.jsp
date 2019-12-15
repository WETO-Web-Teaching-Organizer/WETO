<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="grading.header.generateGrades" /></h2>
<p><s:text name="generateGrades.instructions" /></p>
<form action="<s:url action="generateGrades" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="submitted" value="true" />
  <p>
    <s:text name="generateGrades.instructions.autoGrading" />
  </p>
  <p>
    <input type="checkbox" name="overwriteExisting" value="true" />
    <s:text name="generateGrades.header.overwrite" />
  </p>
  <p>
    <input type="checkbox" name="gradeSubmissionsOnly" value="true" />
    <s:text name="generateGrades.header.gradeSubmissionsOnly" />
  </p>
  <p>
    <input type="checkbox" name="gradePeerReviewersOnly" value="true" />
    <s:text name="generateGrades.header.gradePeerReviewersOnly" />
  </p>
  <p>
    <s:text name="generateGrades.header.defaultGrade" />:
    <input type="text" name="defaultMark" size="5" />
  </p>
  <p>
    <input type="submit"
           value="<s:text name="general.header.generate" />" class="linkButton" />
  </p>
</form>