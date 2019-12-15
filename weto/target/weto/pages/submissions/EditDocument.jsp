<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="!navigator.teacher">
  <s:url action="viewSubmissions" var="backToSubmissionURL">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="tabId" />
    <s:param name="dbId" value="dbId" />
  </s:url>
</s:if>
<s:else>
  <s:url action="viewSubmission" var="backToSubmissionURL">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="tabId" />
    <s:param name="dbId" value="dbId" />
    <s:param name="submissionId" value="submissionId" />
  </s:url>
</s:else>
<form action="#" method="post" id="documentForm">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="documentId" value="${documentId}" />
  <div id="editDocumentHeader">
    <s:a href="%{backToSubmissionURL}" cssClass="smallLinkButton" >
      <s:text name="submissions.header.backToSubmission" />
    </s:a>
    <s:if test="updateable">
      &nbsp;
      <button type="button" name="save" class="smallLinkButton" onclick="saveDocument()">
        <s:text name="general.header.save" />
      </button>
    </s:if>
    &nbsp;
    <strong><s:text name="general.header.name" />:</strong> ${fileName}
    &nbsp;
    <strong><s:text name="general.header.size" />:</strong>
    <span class="answerSize">
      ${fileSize}
    </span>
    &nbsp;
    <strong><s:text name="quiz.header.lastSaved" />:</strong>
    <span class="answerDate">
      ${fileTimeStamp}
    </span
  </div>
  <s:textarea id="textArea" name="documentText" />
  <div id="editorDiv"></div>
</form>
<s:url action="relogin" var="reloginUrl" escapeAmp="false" />
<div id="dummyDiv" style="display: none;"></div>
<script src="js/ace/ace.js" type="text/javascript" charset="utf-8"></script>
<script src="js/tinymce4/tinymce.min.js"></script>
<script>
        var editor;
        var textArea;
        var formEl;

        var entityMap = {
          '&': '&amp;',
          '<': '&lt;',
          '>': '&gt;',
          '"': '&quot;',
          "'": '&#39;',
          '/': '&#x2F;',
          '`': '&#x60;',
          '=': '&#x3D;'
        };

        function escapeHtml(string) {
          return String(string).replace(/[&<>"'`=\/]/g, function (s) {
            return entityMap[s];
          });
        }

        function notifyUser(xhr, data, formEl)
        {
          if ((xhr.status == 200) || (xhr.status == 204)) {
            var firstComma = data.indexOf(";");
            var answerDate = "<s:text name="general.header.never"/>";
            var answerText = "";
            var answerSize = 0;
            if (firstComma >= 0)
            {
              var secondComma = data.indexOf(";", firstComma + 1);
              if (secondComma >= 0)
              {
                answerDate = data.substring(0, firstComma);
                answerSize = data.substring(firstComma + 1, secondComma);
                answerText = data.substring(secondComma + 1);
              }
            }
            if (answerText.length > 100)
            {
              answerText = answerText.substr(0, 100) + "...";
            }
            var sizeSpan = formEl.find("span.answerSize");
            if (sizeSpan.length > 0)
            {
              sizeSpan.first().html(answerSize);
            }
            var dateSpan = formEl.find("span.answerDate");
            if (dateSpan.length > 0)
            {
              dateSpan.first().html(answerDate);
            }
            showInfoBarMessage('<div id="messages"><div class="message_header"><s:text name="quiz.header.answerSuccess" /></div>'
                    + escapeHtml(answerText) + '</div>');
          } else if (xhr.status == 403)
          {
            reloginPopup(tinymce.get('dummyDiv'));
          } else
          {
            showInfoBarMessage('<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="quiz.message.answerError" /></span></li></ul></div></div>');
          }
        }

        function saveDocument()
        {
          textArea.val(editor.getSession().getValue());
          var formData = formEl.serialize();
          var url = '<s:url action="saveTextDocument" />';
          $.post(url, formData).done(function (data, stat, xhr) {
            notifyUser(xhr, data, formEl);
          }).fail(function (xhr, stat, err) {
            notifyUser(xhr, "", formEl);
          });
        }

        $(function () {
          tinymce.init({
            selector: "#dummyDiv",
            inline: "true",
            menubar: false
          });

          formEl = $("#documentForm");
          textArea = $('#textArea');
          textArea.hide();
          var editDiv = $('#editorDiv');
          editDiv.val(textArea.val());
          editor = ace.edit(editDiv[0]);
          editor.setOptions({
            maxLines: Infinity,
            fontSize: "11pt"
          });
          editor.getSession().setValue(textArea.val());
          editor.getSession().setMode("ace/mode/${textType}");
          editor.getSession().setTabSize(2);
          editor.getSession().setUseSoftTabs(true);
          editor.getSession().setUseWrapMode(true);
          editor.setTheme("ace/theme/textmate");
          editor.focus();
        });

        document.onkeydown = function (e) {
          var key = e.code;
          if (key && (e.ctrlKey || e.metaKey) && (key == 'KeyS') && !(e.altKey))
          {
            e.preventDefault();
            saveDocument();
            return false;
          }
          return true;
        };
</script>
