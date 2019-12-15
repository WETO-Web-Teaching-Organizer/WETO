<!DOCTYPE html>
<%@ include file="/WEB-INF/init.jsp"%>
<html lang='<s:property value="#request.locale.language" />'>
  <head>
    <title><tiles:insertAttribute name="titleKey" /></title>
    <tiles:insertAttribute name="head" />
  </head>
  <body id="masterLayoutBody">
    <tiles:insertAttribute name="logout" />
    <div id="contents" class="container-fluid">
      <div class="row">
        <div class="col-lg-offset-1 col-lg-10 col-md-12 col-sm-12 col-xs-12">
          <s:if test="navigator.admin">
            <tiles:insertAttribute name="adminMenu" />
          </s:if>
          <tiles:insertAttribute name="body" />
          <div class="infoBar"><tiles:insertAttribute name="infoBar" /></div>
        </div>
      </div>
      <%-- <div id="footer"><tiles:insertAttribute name="footer" /></div>--%>
    </div>
  </body>
</html>