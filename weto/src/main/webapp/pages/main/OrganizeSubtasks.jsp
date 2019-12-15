<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="navigationTree != null">
  <form id="JSONform" action="<s:url action="confirmOrganizeSubtasks" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="subtaskJSON" id="subtaskJSON" />
    <s:text name="editTask.title.organizeSubtasks" />
    &nbsp;
    <span class="linkButton" onclick="submitChanges()">
      <s:text name="general.header.previewChanges" />
    </span>
  </form>
  <br />
  <table id="subtask-table">
    <tr>
      <td>
        <span class="linkButton" id="expandButton" onclick="expandNestable()"><s:text name="editTask.header.expandAll" /></span>
      </td>
      <td>
        <form id="otherCourseForm">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <select name="otherCourseDbId_TaskId">
            <s:iterator var="loopName" value="courseNames" status="loop">
              <s:if test="%{courseDbId_TaskIds[#loop.index] == otherCourseDbId_TaskId}">
                <option value="${courseDbId_TaskIds[loop.index]}" selected="selected">${loopName}</option>
              </s:if>
              <s:else>
                <option value="${courseDbId_TaskIds[loop.index]}">${loopName}</option>
              </s:else>
            </s:iterator>
          </select>
          <button type="button" class="btn btn-lg btn-default" onclick="submitOtherCourseForm()">
            <s:text name="general.header.update" />
          </button>
        </form>
      </td>
    </tr>
    <tr>
      <td style="vertical-align: top">
        <div class="contentBox">
          ${task.name}
          <div class="dd" id="reordermenu" style="display: none">
            ${thisCourseTree}
          </div>
        </div>
      </td>
      <td style="vertical-align: top">
        <div class="contentBox"">
          <div class="dd readonly" id="reordermenu2" style="display: none">
            ${otherCourseTree}
          </div>
        </div>
      </td>
    </tr>
  </table>
  <div id="previewDiv" style="display:none;position:fixed;-webkit-transform:scale(0.7);-moz-transform:scale(0.7);z-index:1100;">
    <button class ="btn-default btn-lg" onClick="closePreviewPage()" type="button" style="vertical-align:top;"><span class ="glyphicon glyphicon-remove"/></button>
    <iframe width="800" height="800"></iframe>
  </div>
  <script src="js/nestable.js"></script>
  <s:url action="viewTask" var="previewTaskUrl">
  </s:url>
  <script>
      var collapsed = true;
      var previewPageAction = "${previewTaskUrl}";
      var previewDiv = document.getElementById("previewDiv");
      var iframeEl = previewDiv.lastElementChild;

      function previewPage(e, dbAndTaskIds) {
        var parts = dbAndTaskIds.split("_");
        var dbId = parts[0];
        var taskId = parts[1];
        var url = previewPageAction + '?taskId=' + taskId + '&tabId=${mainTabId}&dbId=' + dbId + '&answererId=${courseUserId}';
        iframeEl.setAttribute("src", url);
        previewDiv.style.display = '';
        previewDiv.style.left = (e.pageX - 800 * 0.15) + 'px';
        previewDiv.style.top = (e.pageY - 800 * 0.15) + 'px';
      }

      function closePreviewPage() {
        iframeEl.setAttribute("src", "");
        previewDiv.style.display = 'none';
      }

      function expandNestable() {
        if (collapsed)
        {
          $(".dd").nestable('expandAll');
          collapsed = false;
          document.getElementById("expandButton").innerHTML = '<s:text name="editTask.header.collapseAll" />';
        } else
        {
          $(".dd").nestable('collapseAll');
          collapsed = true;
          document.getElementById("expandButton").innerHTML = '<s:text name="editTask.header.expandAll" />';
        }
      }

      function submitChanges() {
        if (window.JSON) {
          $('#subtaskJSON')[0].value = window.JSON.stringify($('#reordermenu').nestable('serialize'));
          $('#JSONform')[0].submit();
        } else {
          alert.text('This feature requires JSON browser support!');
        }
      }

      function submitOtherCourseForm()
      {
        var formEl = $('#otherCourseForm');
        var formData = formEl.serialize();
        var url = '<s:url action="updateOtherCourseTree"/>';
        $.post(url, formData).done(function (data) {
          $("#reordermenu2").replaceWith('<div class="dd readonly" id="reordermenu2" style="display: none"></div>');
          $("#reordermenu2").html(data);
          $("#reordermenu2 li").each(function (i) {
            this.className = "dd-item";
            var inner = document.createElement("div");
            inner.className = "dd-handle";
            inner.innerHTML = this.firstChild.innerHTML;
            var dbAndTaskIds = this.firstChild.id;
            this.setAttribute("data-id", dbAndTaskIds + ":" + inner.innerHTML);
            this.replaceChild(inner, this.firstChild);
            var tmpdiv = document.createElement('div');
            tmpdiv.innerHTML = '<button class ="btn-default-small" onClick="previewPage(event, \x27' + dbAndTaskIds + '\x27)" type="button"><span class ="glyphicon glyphicon-globe"/></button>';
            var previewBtn = tmpdiv.firstChild;
            this.insertBefore(previewBtn, this.firstChild);
          });
          $("#reordermenu2 ul").each(function (i) {
            this.className = "dd-list";
          });
          $("#reordermenu2").nestable();
          if (collapsed)
          {
            $("#reordermenu2").nestable('collapseAll');
          }
          $("#reordermenu2").show();
        }).fail(function () {
          $('#infoBar').empty();
          $('#infoBar').append(
                  '<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="general.error.system" /></span></li></ul></div></div>'
                  );
          $('.infoBar').show();
          timeoutobject = setTimeout(function ()
          {
            jQuery(".infoBar").fadeOut(300);
          }, 7000);
        });
        return true;
      }

      $(function () {
        var root = $("#reordermenu > ul");
        if (!root.find("li").length)
        {
          var emptyChild = document.createElement("div");
          emptyChild.className = "dd-empty";
          root.after(emptyChild);
          root.remove();
        }
        $("#reordermenu li").each(function (i) {
          this.className = "dd-item";
          var inner = document.createElement("div");
          inner.className = "dd-handle";
          inner.innerHTML = this.firstChild.innerHTML;
          var parentId = ${taskId};
          var parentLI = this.parentNode.parentNode;
          if (parentLI.nodeName === "LI")
          {
            parentId = parentLI.getAttribute("data-id").split(':')[0].split('_')[2];
          }
          var rank = $(this).parent().children("li").index(this);
          var dbAndTaskIds = this.firstChild.id;
          this.setAttribute("data-id", parentId + "_" + rank + "_" +
                  dbAndTaskIds + ":" + inner.innerHTML);
          this.replaceChild(inner, this.firstChild);
          var tmpdiv = document.createElement('div');
          tmpdiv.innerHTML = '<button class ="btn-default-small" onClick="previewPage(event, \x27' + dbAndTaskIds + '\x27)" type="button"><span class ="glyphicon glyphicon-globe"/></button>';
          var previewBtn = tmpdiv.firstChild;
          this.insertBefore(previewBtn, this.firstChild);
        });
        $("#reordermenu2 li").each(function (i) {
          this.className = "dd-item";
          var inner = document.createElement("div");
          inner.className = "dd-handle";
          titleSpan = $(this).find("span[id]").first();
          inner.innerHTML = titleSpan.html();
          var dbAndTaskIds = titleSpan.attr("id");
          this.setAttribute("data-id", dbAndTaskIds + ":" + inner.innerHTML);
          titleSpan.replaceWith(inner);
          var tmpdiv = document.createElement('div');
          tmpdiv.innerHTML = '<button class ="btn-default-small" onClick="previewPage(event, \x27' + dbAndTaskIds + '\x27)" type="button"><span class ="glyphicon glyphicon-globe"/></button>';
          var previewBtn = tmpdiv.firstChild;
          this.insertBefore(previewBtn, this.firstChild);
        });
        $(".dd ul").each(function (i) {
          this.className = "dd-list";
        });
        initNestable(window.jQuery || window.Zepto, window, document);
        $(".dd").nestable();
        $(".dd").nestable('collapseAll');
        $(".dd").show();
      });
  </script>
</s:if>