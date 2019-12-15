<%@ include file="/WEB-INF/taglibs.jsp"%>
<div id="infoBar">
  <s:if test="hasActionErrors()">
    <div id="errors">
      <div class="error_header"><s:text name="errors.header" /></div>
      <div class="error_message"><s:actionerror /></div>
    </div>
  </s:if>
  <s:if test="hasFieldErrors()">
    <div id="errors">
      <div class="error_header"><s:text name="errors.header" /></div>
      <div class="error_message"><s:fielderror /></div>
    </div>
  </s:if>
  <s:if test="hasActionMessages()">
    <div id="messages">
      <div class="message_header"><s:text name="messages.header"/></div>
      <div class= "error_message"><s:actionmessage /></div>
    </div>
  </s:if>
</div>