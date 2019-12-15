<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="navigator.teacher">
  <s:if test="!taskPath.isEmpty()">
    <form action="<s:url action="style" />" method="post">
      <s:select name="style" list="{'weto', 'weto2'}" value="#session.style" />
      <s:submit />
    </form>
  </s:if>
</s:if>
