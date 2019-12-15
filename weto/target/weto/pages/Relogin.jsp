<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="row">
  <div class="col-md-offset-4 col-md-4 transparent-white">
    <s:if test="inUpdateMode">
      <h1>
        <s:text name="login.message.inUpdateMode" />
      </h1>
    </s:if>
    <s:else>
      <h2 class="login_header">
        <s:text name="login.title" />
      </h2>
      <div class="panel-red">
        <h4>
          <s:text name="general.message.loggedOut" />
        </h4>
      </div>
      <form id="reloginForm" class="form-signin">
        <div class="loginfield center-block" class="span4 offset4">
          <div class ="login--username">
            <s:text name="general.header.username" />
          </div>
          <input class ="form-control" type="text" name="username" size="12" autofocus />
          <div class ="login--passwordtext">
            <s:text name="general.header.password" />
          </div>
          <input class="form-control center-block" type="password" name="password" size="12" required/>
          <br>
          <button type="button" class="btn btn-lg btn-primary btn-block" onclick="submitReloginForm()">
            <s:text name="general.header.submit" />
          </button>
        </div>
      </form>
    </s:else>
  </div>
</div>
<script>
  function notifyUser(xhr)
  {
    if (xhr.status == 204) {
      $('#infoBar').empty();
      $('#infoBar').append(
              '<div id="messages"><b>INFO:</b><ul class="actionMessage"><li><span><s:text name="general.header.loginSuccess" /></span></li></ul></div>'
              );
      $('.infoBar').show();
      timeoutobject = setTimeout(function ()
      {
        jQuery(".infoBar").fadeOut(300);
        top.tinymce.activeEditor.windowManager.close();
      }, 1000);
    } else
    {
      $('#infoBar').empty();
      $('#infoBar').append(
              '<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="login.error.reloginFailed" /></span></li></ul></div></div>'
              );
      $('.infoBar').show();
      timeoutobject = setTimeout(function ()
      {
        jQuery(".infoBar").fadeOut(300);
      }, 7000);
    }
  }

  function submitReloginForm()
  {
    var formEl = $('#reloginForm');
    var formData = formEl.serialize();
    var url = '<s:url action="submitRelogin"/>';
    $.post(url, formData).done(function (data, stat, xhr) {
      notifyUser(xhr);
    }).fail(function (xhr, stat, err) {
      notifyUser(xhr);
    });
    return true;
  }
</script>
