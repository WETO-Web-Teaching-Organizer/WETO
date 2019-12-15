<%@ include file="/WEB-INF/taglibs.jsp"%>
<head>
   <meta charset="UTF-8">
</head>
<body>
   <tiles:insertAttribute name = "body"/>
   <div class="infoBar"><tiles:insertAttribute name="infoBar" /></div>
   <script type="text/javascript">
      ajaxInits();
   </script>
</body>
