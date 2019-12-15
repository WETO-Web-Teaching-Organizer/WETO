<%@ include file="/WEB-INF/taglibs.jsp"%>
<li>
  <s:url action="viewGradingProperties" var="viewGradingPropertiesUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="3" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{viewGradingPropertiesUrl}">
    <s:text name="grading.header.gradingProperties" />
  </s:a>
</li>
<li>
  <s:url action="setupPeerReview" var="setupPeerReviewUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="3" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{setupPeerReviewUrl}">
    <s:text name="grading.header.peerReviewing" />
  </s:a>
</li>
<li>
  <s:url action="viewReviewInstructions" var="viewReviewInstructionsURL">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="3" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{viewReviewInstructionsURL}">
    <s:text name="general.header.reviewInstructions" />
  </s:a>
</li>
<li>
  <s:url action="generateGrades" var="generateGradesUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="3" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{generateGradesUrl}">
    <s:text name="grading.header.generateGrades" />
  </s:a>
</li>
<li>
  <s:url action="calculateGrades" var="calculateGradesUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="3" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{calculateGradesUrl}">
    <s:text name="grading.header.calculateGrades" />
  </s:a>
</li>
<li>
  <s:url action="exportGrades" var="exportGradesUrl">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="3" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <s:a href="%{exportGradesUrl}">
    <s:text name="grading.header.exportGrades" />
  </s:a>
</li>
