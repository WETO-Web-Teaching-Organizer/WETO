<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/tinymce4/tinymce.min.js"></script>
<s:text name="editTask.title.edit" />
&nbsp;
<button type="button" class="btn btn-sm btn-default" onclick="submitEditTaskForm(false)">
  <s:text name="general.header.save" />
</button>
&nbsp;
<button type="button" class="btn btn-sm btn-default" onclick="submitEditTaskForm(true)">
  <s:text name="editTask.header.saveAndReturn" />
</button>
<div class="editable" id="taskTextDiv" style="min-height: 14px">
  ${task.text}
</div>
<textarea name="taskText" id="taskText" style="display:none;"></textarea>
<s:text name="editTask.title.edit" />
&nbsp;
<button type="button" class="btn btn-sm btn-default" onclick="submitEditTaskForm(false)">
  <s:text name="general.header.save" />
</button>
&nbsp;
<button type="button" class="btn btn-sm btn-default" onclick="submitEditTaskForm(true)">
  <s:text name="editTask.header.saveAndReturn" />
</button>
<s:url action="popupTaskDocuments" var="taskDocumentsUrl" escapeAmp="false">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{#mainTabId}" />
  <s:param name="dbId" value="%{dbId}" />
  <s:param name="showAsThumbnails" value="false" />
</s:url>
<s:url action="popupTaskDocuments" var="taskImagesUrl" escapeAmp="false">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{#mainTabId}" />
  <s:param name="dbId" value="%{dbId}" />
  <s:param name="showAsThumbnails" value="true" />
</s:url>
<s:url action="viewTask" var="returnToTaskUrl" escapeAmp="false" forceAddSchemeHostAndPort="true">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{#mainTabId}" />
  <s:param name="dbId" value="%{dbId}" />
</s:url>
<s:url action="relogin" var="reloginUrl" escapeAmp="false" />
<script>
  function taskDocFileSelectCallback(field_name, url, type, win) {
    var browseUrl = (type === "file") ? "${taskDocumentsUrl}" : "${taskImagesUrl}";
    tinymce.activeEditor.windowManager.open({
      file: browseUrl,
      title: 'Task Document Selection',
      autoScroll: true,
      width: 480,
      height: 500
    }, {
      window: win,
      input: field_name
    });
    return false;
  }

  function notifyUser(xhr, forward)
  {
    if (xhr.status == 204) {
      $('#infoBar').empty();
      $('#infoBar').append(
              '<div id="messages"><b>INFO:</b><ul class="actionMessage"><li><span><s:text name="editTask.message.updateSuccess" /></span></li></ul></div>'
              );
      $('.infoBar').show();
      tinymce.triggerSave();
      if (forward == true)
      {
        timeoutobject = setTimeout(function ()
        {
          window.location.href = "${returnToTaskUrl}";
        }, 1000);
      } else
      {
        timeoutobject = setTimeout(function ()
        {
          jQuery(".infoBar").fadeOut(300);
        }, 1000);
      }
    } else if (xhr.status == 403)
    {
      reloginPopup(tinymce.activeEditor);
    } else
    {
      $('#infoBar').empty();
      $('#infoBar').append(
              '<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="editTask.error.save" /></span></li></ul></div></div>'
              );
      $('.infoBar').show();
      timeoutobject = setTimeout(function ()
      {
        jQuery(".infoBar").fadeOut(300);
      }, 7000);
    }
  }

  function submitEditTaskForm(forward)
  {
    document.getElementById('taskName').value = $('#taskNameDiv').text();
    document.getElementById('taskText').value = tinymce.get('taskTextDiv').getContent();
    var formEl = $('#editTaskForm');
    var formData = formEl.serialize();
    var url = '<s:url action="commitEditTask"/>';
    $.post(url, formData).done(function (data, stat, xhr) {
      notifyUser(xhr, forward);
    }).fail(function (xhr, stat, err) {
      notifyUser(xhr, forward);
    });
    return true;
  }

  $(function () {
  <s:if test="quiz.questions.size() > 0">
    tinymce.init({
      selector: "div.editable",
      inline: "true",
      entity_encoding: "raw",
      forced_root_block: '',
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      autosave_retention: "120m",
      default_link_target: "_blank",
      file_browser_callback: taskDocFileSelectCallback,
      menubar: false,
      toolbar: [
        "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor charmap emoticons mathslate | table",
        "code link unlink image addquestion | bold italic underline strikethrough superscript subscript removeformat | outdent indent bullist numlist | preview"
      ],
      setup: function (editor) {
        editor.addButton('addquestion', {
          type: 'menubutton',
          text: 'Add question',
          icon: false,
          menu: [
    <s:iterator var="question" value="quiz.questions">
            {text: '${question.questionName}', onclick: function () {
                editor.insertContent('<div contenteditable="false" class="peerreview-border-box" data-weto="type=${question.contentElementType} taskId=${question.taskId} refId=${question.questionId}">${question.questionName}</div>');
              }},
    </s:iterator>
          ]
        });
        editor.on('PreProcess', function (e) {
          editor.$('div[data-weto]', e.node).removeAttr('contentEditable').removeAttr('class');
        });
        editor.on('SetContent', function (e) {
          editor.$('div[data-weto]', e.node).attr("contenteditable", "false").addClass("peerreview-border-box");
        });
      },
      style_formats_merge: true,
      style_formats: [
        {title: 'Code highlight', block: 'pre', wrapper: true, classes: 'prettyprint'},
        {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
        {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
        {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
        {title: 'Shadow-box-week', block: 'div', wrapper: true, classes: 'content-col'},
        {title: 'Default-Panel-element', block: 'div', wrapper: true, classes: 'panel'},
        {title: 'Interactive-code-panel', block: 'div', wrapper: true, classes: 'code-panel prettyprint'},
        {title: 'Peerreview-border-box', block: 'div', wrapper: true, classes: 'peerreview-border-box'},
        {title: 'Peerreview-highlight-text', block: 'div', wrapper: true, classes: 'peerreview-highlight'},
        {title: 'Highlight-text-background', block: 'div', wrapper: true, classes: 'highlight-text-background'},
        {title: 'icemint', block: 'div', wrapper: true, classes: 'icemint'},
        {title: 'red-panel-box', block: 'div', wrapper: true, classes: 'panel-red'},
        {title: 'blue-panel-box', block: 'div', wrapper: true, classes: 'panel-blue'},
        {title: 'latte-panel-box', block: 'div', wrapper: true, classes: 'panel-latte'},
        {title: 'gray-panel-box', block: 'div', wrapper: true, classes: 'panel-darker-gray'},
        {title: 'news-panel-box', block: 'div', wrapper: true, classes: 'panel-news'},
        {title: 'news-title', block: 'h5', classes: 'news-title-style'},
        {title: 'only-border-box', block: 'div', wrapper: true, classes: 'only-border'}
      ]
    });
  </s:if>
  <s:else>
    tinymce.init({
      selector: "div.editable",
      inline: "true",
      entity_encoding: "raw",
      forced_root_block: '',
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      autosave_retention: "120m",
      default_link_target: "_blank",
      file_browser_callback: taskDocFileSelectCallback,
      menubar: false,
      toolbar: [
        "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor charmap emoticons mathslate | table",
        "code link unlink image | bold italic underline strikethrough superscript subscript removeformat | outdent indent bullist numlist | preview"
      ],
      setup: function (editor) {
        editor.on('PreProcess', function (e) {
          editor.$('div[data-weto]', e.node).removeAttr('contentEditable').removeAttr('class');
        });
        editor.on('SetContent', function (e) {
          editor.$('div[data-weto]', e.node).attr("contenteditable", "false").addClass("peerreview-border-box");
        });
      },
      style_formats_merge: true,
      style_formats: [
        {title: 'Code highlight', block: 'pre', wrapper: true, classes: 'prettyprint'},
        {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
        {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
        {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
        {title: 'Shadow-box-week', block: 'div', wrapper: true, classes: 'content-col'},
        {title: 'Default-Panel-element', block: 'div', wrapper: true, classes: 'panel'},
        {title: 'Interactive-code-panel', block: 'div', wrapper: true, classes: 'code-panel prettyprint'},
        {title: 'Peerreview-border-box', block: 'div', wrapper: true, classes: 'peerreview-border-box'},
        {title: 'Peerreview-highlight-text', block: 'div', wrapper: true, classes: 'peerreview-highlight'},
        {title: 'Highlight-text-background', block: 'div', wrapper: true, classes: 'highlight-text-background'},
        {title: 'icemint', block: 'div', wrapper: true, classes: 'icemint'},
        {title: 'red-panel-box', block: 'div', wrapper: true, classes: 'panel-red'},
        {title: 'blue-panel-box', block: 'div', wrapper: true, classes: 'panel-blue'},
        {title: 'latte-panel-box', block: 'div', wrapper: true, classes: 'panel-latte'},
        {title: 'gray-panel-box', block: 'div', wrapper: true, classes: 'panel-darker-gray'},
        {title: 'news-panel-box', block: 'div', wrapper: true, classes: 'panel-news'},
        {title: 'news-title', block: 'h5', classes: 'news-title-style'},
        {title: 'only-border-box', block: 'div', wrapper: true, classes: 'only-border'}
      ]
    });
  </s:else>
  });
</script>
<s:if test="!subtasks.isEmpty()">
  <h3>
    <s:text name="general.header.subtasks" />
  </h3>
  <ul>
    <s:iterator var="subtask" value="subtasks" status="stat">
      <li>
        <s:url action="viewTask" var="viewTaskUrl">
          <s:param name="taskId" value="#subtask.id" />
          <s:param name="tabId" value="#mainTabId" />
          <s:param name="dbId" value="dbId" />
        </s:url>
        <s:if test="#subtask.isHidden">
          <s:a href="%{viewTaskUrl}" class="hiddenFromStudents">
            ${subtask.name}
          </s:a>
        </s:if>
        <s:else>
          <s:a href="%{viewTaskUrl}">
            ${subtask.name}
          </s:a>
        </s:else>
        <s:if test="#subtask.showTextInParent && (#subtask.text != null)">
          <br>
          <small> ${subtask.text} </small>
        </s:if>
      </li>
    </s:iterator>
  </ul>
</s:if>
