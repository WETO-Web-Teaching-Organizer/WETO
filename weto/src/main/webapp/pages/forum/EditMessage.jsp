<%@ include file="/WEB-INF/taglibs.jsp"%>
<form action="<s:url action="editForumMessage"/>" method="post" onsubmit="return pollLogin()">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="commitSave" value="true" />
  <input type="hidden" name="topicId" value="${topicId}" />
  <input type="hidden" name="messageId" value="${messageId}" />
  <h4><s:text name="forum.header.editMessage" /></h4>
  <s:textarea name="messageText" id="messageText" cols="80" rows="20" />
  <input type="submit" value="<s:text name="general.header.save" />" class="btn btn-primary" />
  <s:url action="viewForumTopic" var="viewForumTopicUrl">
    <s:param name="taskId" value="%{taskId}" />
    <s:param name="tabId" value="%{tabId}" />
    <s:param name="dbId" value="%{dbId}" />
    <s:param name="topicId" value="%{topicId}" />
  </s:url>
  <s:a href="%{viewForumTopicUrl}" cssClass="linkButton">
    <s:text name="general.header.cancel" />
  </s:a>
  <s:if test="navigator.teacher">
    <s:url action="editForumMessage" var="deleteForumMessageUrl">
      <s:param name="taskId" value="%{taskId}" />
      <s:param name="tabId" value="%{tabId}" />
      <s:param name="dbId" value="%{dbId}" />
      <s:param name="topicId" value="%{topicId}" />
      <s:param name="messageId" value="%{messageId}" />
      <s:param name="commitDelete" value="true" />
    </s:url>
    <s:a href="%{deleteForumMessageUrl}" cssClass="btn btn-danger" cssStyle="float: right">
      <s:text name="general.header.delete" />
    </s:a>
  </s:if>
</form>
<script>
  $(function () {
    tinymce.init({
      selector: "#messageText",
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      menubar: false,
      toolbar: [
        "undo redo | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
        "bold italic underline superscript subscript mathslate | outdent indent bullist numlist | link image | preview code"
      ]
    });
  });

  function pollLogin()
  {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "pollLogin", false);
    xhr.send(null);
    if (xhr.status == 204) {
      return true;
    } else if (xhr.status == 403)
    {
      reloginPopup(tinymce.get('messageText'));
    } else
    {
      $('#infoBar').empty();
      $('#infoBar').append(
              '<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="accessDenied.systemError" /></span></li></ul></div></div>'
              );
      $('.infoBar').show();
      timeoutobject = setTimeout(function ()
      {
        jQuery(".infoBar").fadeOut(300);
      }, 7000);
    }
    return false;
  }
</script>