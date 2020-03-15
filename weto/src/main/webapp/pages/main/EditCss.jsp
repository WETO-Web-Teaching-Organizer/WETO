<%@ include file="/WEB-INF/taglibs.jsp"%>
<form action="<s:url action="saveCourseCss" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <div id="header">
    <s:url action="taskSettings" var="taskSettingsURL">
      <s:param name="taskId" value="taskId" />
      <s:param name="tabId" value="tabId" />
      <s:param name="dbId" value="dbId" />
    </s:url>
    <input type="submit" value="<s:text name="general.header.save" />" class="linkButton" />
    &nbsp;
    <s:a href="%{taskSettingsURL}" cssClass="linkButton" >
      <s:text name="taskSettings.title.view" />
    </s:a>
    &nbsp;
    <strong><s:text name="general.header.name" />:</strong> ${courseCssFilename}
  </div>
  <s:textarea id="textArea" name="cssText" />
  <div id="editorDiv"></div>
</form>
<script src="js/ace/ace.js"></script>
<script>
  $(function () {
    var textarea = $('#textArea');
    textarea.hide();
    var editDiv = $('#editorDiv');
    editDiv.val(textarea.val());
    var editor = ace.edit(editDiv[0]);
    editor.setOptions({maxLines: Infinity});
    editor.getSession().setValue(textarea.val());
    editor.getSession().setMode("ace/mode/css");
    editor.getSession().setTabSize(2);
    editor.getSession().setUseSoftTabs(true);
    editor.getSession().setUseWrapMode(true);
    editor.setTheme("ace/theme/textmate");
    editor.focus();
    // copy back to textarea on form submit...
    textarea.closest('form').submit(function () {
      textarea.val(editor.getSession().getValue());
    });
  });
</script>
