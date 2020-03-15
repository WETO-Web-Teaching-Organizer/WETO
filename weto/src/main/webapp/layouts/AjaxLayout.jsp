<%@ include file="/WEB-INF/taglibs.jsp"%>
<head>
  <meta charset="UTF-8">
</head>
<body>
  <tiles:insertAttribute name = "body"/>
  <div class="infoBar"><tiles:insertAttribute name="infoBar" /></div>
  <script>
    ajaxInits();
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
