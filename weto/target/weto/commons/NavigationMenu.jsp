<%@ include file="/WEB-INF/taglibs.jsp"%>
<!-- JSON gets injected to HTML -->
<ul id="navtree" data-json='${navigationTree}'>
  <script>
    $(function () {
      var jsonObject = jQuery("#navtree").data("json");
      var dbId = ${dbId};
      var taskId = ${taskId};
      buildTreemenuUnder("#navtree", jsonObject, dbId, taskId, "viewTask.action");
    });
  </script>
</ul>

