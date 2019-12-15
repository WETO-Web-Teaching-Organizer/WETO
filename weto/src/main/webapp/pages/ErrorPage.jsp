<%@ page language="java" isErrorPage="true" pageEncoding="utf-8"
         contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML>
<html lang="en">
  <head>
    <title>An unexpected error has occurred</title>
  </head>
  <body>
    <div class="errorTitle content-col">
      <h2>
        We are truly sorry...
      </h2>
      <h3>
        An unexpected error has occurred
      </h3>
      <img src="<s:url value='/images//weto_testcard.png' />"/>
      <p>
        Please report this error to your system administrator or
        appropriate technical support personnel. Thank you for your
        cooperation.
      </p>

      <h3>Error Message</h3>
      <s:actionerror/>
      <p>
        <s:property value="%{exception}" />
      </p>
      <div class="center-block">
        <div class="main-page-link next" title = "WETO mainpage">
          <s:a action="listCourses">
            <span class ="glyphicon glyphicon-heart-empty"></span>
            <s:text name="message.header.backToMainPage"/>
          </s:a>
        </div>
      </div>
    </div>
  </body>
</html>
