<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h2><s:text name="deleteCourse.title" /></h2>
  <p><s:text name="deleteCourse.text.confirmation" /></p>
  <form action="<s:url action="commitDeleteCourse" />" method="post">
    <input type="hidden" name="courseTaskId" value="${courseTaskId}" />
    <input type="submit" value="<s:text name="general.header.delete" />" class="linkButton" />
  </form>
  <br />
  <h3><s:text name="deleteCourse.header.currentContents" />:</h3>
  <div class="contentBox">
    <h2>${taskName}</h2>
    <s:if test="taskText == null || taskText == ''">
      <p style="color:red;"><s:text name="general.header.emptyPage" /></p>
    </s:if>
    <s:else>
      ${taskText}
    </s:else>
  </div>
</div>
