<%@ include file="/WEB-INF/taglibs.jsp"%>
<div id="logoutContainer" class="logoutBoxContainer" style="display: none">
  <div class="logoutBox" onclick="$('#logoutContainer').hide()">
    <h1><s:text name="general.header.logout"/>?</h1>
    <p>
      <s:text name="general.instructions.logout"/>
    </p>
    <p style="text-align: center">
      <br/>
      <br/>
      (<s:text name="general.header.clickCloseMessage"/>)
    </p>
  </div>
</div>