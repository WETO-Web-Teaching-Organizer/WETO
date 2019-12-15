<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <s:if test="publicViewingOk">
    <h2>
      <s:text name="courseRegistration.title.publicView">
        <s:param >${courseName}</s:param>
      </s:text>
    </h2>
    <p>
      <s:text name="courseRegistration.instructions.publicView" />
      <s:url action="viewTask" var="viewTaskUrl">
        <s:param name="taskId" value="%{taskId}" />
        <s:param name="tabId" value="%{mainTabId}" />
        <s:param name="dbId" value="%{dbId}" />
        <s:param name="publicView" value="true" />
      </s:url>
      <s:a href="%{viewTaskUrl}" cssClass="linkButton">
        <s:text name="courseRegistration.header.publicView" />
      </s:a>
    </p>
  </s:if>
  <h2>
    <s:text name="courseRegistration.title.register">
      <s:param >${courseName}</s:param>
    </s:text>
  </h2>
  <s:if test="groupList != null">
    <s:if test="alreadyPending">
      <p>
        <s:text name="courseRegistration.message.pending" />
      </p>
    </s:if>
    <s:else>
      <p>
        <s:text name="courseRegistration.instructions" />
      </p>
      <form action="<s:url action="commitJoinCourse" />" method="post">
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <div class="courseJoin">
          <s:if test="!groupList.isEmpty()">
            <s:text name="courseRegistration.instructions.group" />
            <div class="general-header">
              <s:text name="general.header.group" />
            </div>
            <div class="grouplist">
              <s:select list="groupList" name="groupId" value="value" />
            </div>
          </s:if>
          <input type="submit" value="<s:text name="general.header.register" />" class="linkButton" />
          <s:a action="listCourses" cssClass="linkButton">
            <s:text name="general.header.cancel" />
          </s:a>
        </div>
      </form>
    </s:else>
  </s:if>
  <s:else>
    <p>
      <s:text name="courseRegistration.error.courseClosed" />
    </p>
    <div class="main-page-link next" title = "WETO mainpage">
      <s:a action="listCourses">
        <span class ="glyphicon glyphicon-heart-empty"></span>
        <s:text name="message.header.backToMainPage"/>
      </s:a>
    </div>
  </s:else>
</div>