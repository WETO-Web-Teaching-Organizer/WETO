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
    <script>
      function reloginPopup(tinymceInstance)
      {
        tinymceInstance.windowManager.open({
          file: '${reloginUrl}',
          title: '<s:text name="general.header.relogin" />',
          width: window.innerWidth * 0.8,
          height: window.innerHeight * 0.8
        });
      }
    </script>
  </body>
</html>
