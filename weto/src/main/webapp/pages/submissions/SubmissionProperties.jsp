<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2>
  <s:text name="submissionProperties.title" />
</h2>
<form action="<s:url action="saveSubmissionProperties" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <p>
    <s:text name="submissionProperties.instructions.fileNames" />
  </p>
  <p>
    <s:text name="submissions.header.allowedFilePatterns" />:
    <input type="text" name="filePatterns" value="${filePatterns}" size="50" />
  </p>
  <p>
    <s:text name="submissionProperties.instructions.patternDescriptions" />
  </p>
  <p>
    <s:text name="submissions.header.patternDescriptions" />:
    <input type="text" name="patternDescriptions" value="${patternDescriptions}" size="50" />
    (<s:text name="submissions.header.caseInsensitive" />:
    <input type="checkbox" name="caseInsensitive" value="true"
           <s:if test="caseInsensitive">checked="checked"</s:if> />)
    </p>
    <p>
    <s:text name="submissions.header.allowInlineFiles" />:
    <input type="checkbox" name="allowInlineFiles" value="true"
           <s:if test="allowInlineFiles">checked="checked"</s:if> />
    </p>
    <p>
    <s:text name="submissions.header.allowTestRun" />:
    <input type="checkbox" name="allowTestRun" value="true"
           <s:if test="allowTestRun">checked="checked"</s:if> />
    </p>
    <p>
    <s:text name="submissionProperties.instructions.zipping" />
  </p>
  <p>
    <s:text name="submissions.header.allowZipping" />:
    <input type="checkbox" name="allowZipping" value="true"
           <s:if test="allowZipping">checked="checked"</s:if> />
    </p>
    <p>
    <s:text name="submissionProperties.instructions.keptFileLimit" />
  </p>
  <p>
    <s:text name="submissionProperties.header.oldSubmissionLimit" />:
    <input type="text" name="oldSubmissionLimit" value="${oldSubmissionLimit}" size="50" />
  </p>
  <input type="submit" value="<s:text name="general.header.save" />" class="linkButton" />
</form>
