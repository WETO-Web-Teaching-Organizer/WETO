<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="row">
  <div class="col-md-offset-4 col-md-4 transparent-white">
    <s:if test="inUpdateMode">
      <h1>
        <s:text name="login.message.inUpdateMode" />
      </h1>
    </s:if>
    <s:else>
      <h2 class="login_header">
        <s:text name="login.title" />
      </h2>
      <form action="<s:url action="submitLogin" />" method="post" class ="form-signin">
        <input type="hidden" name="taskId" value="${param.taskId}" />
        <input type="hidden" name="tabId" value="${param.tabId}" />
        <input type="hidden" name="dbId" value="${param.dbId}" />
        <div class="loginfield center-block" class="span4 offset4">
          <div class ="login--username">
            <s:text name="general.header.username" />
          </div>
          <input class ="form-control" type="text" name="username" value="${username}" size="12" autofocus />
          <div class ="login--passwordtext">
            <s:text name="general.header.password" />
          </div>
          <input class="form-control center-block" type="password" name="password" value="${password}" size="12" required/>
          <br>
          <input class="btn btn-lg btn-primary btn-block" id="submitlogin" type="submit" value="<s:text name="general.header.submit" />" />
        </div>
      </form>
    </s:else>
    <div class="center-block">
      <div class="main-page-link next" title = "WETO mainpage">
        <s:a action="listCourses">
          <span class ="glyphicon glyphicon-heart-empty"></span>
          <s:text name="message.header.backToMainPage"/>
        </s:a>
      </div>
    </div>
  </div>
</div>
