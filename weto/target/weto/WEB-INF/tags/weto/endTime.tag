
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ attribute name="ending" required="true"%>

<s:if test="#attr.ending != null && !#attr.ending.empty">
  ${ending}
</s:if>
<s:else>
  <s:text name="general.header.never" />
</s:else>
