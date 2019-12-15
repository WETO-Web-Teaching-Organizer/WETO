<!DOCTYPE html>
<%@ include file="/WEB-INF/init.jsp"%>
<html lang='<s:property value="#request.locale.language" />'>
  <head>
    <title>${pageTitle}</title>
    <tiles:insertAttribute name="head" />
    <s:if test="#session.style != null">
      <link rel="stylesheet" href="css/<s:property value="#session.style" />.css" />
    </s:if>
  </head>
  <body>
    <tiles:insertAttribute name="body" />
    <div class="infoBar"><tiles:insertAttribute name="infoBar" /></div>
  </body>
</html>
