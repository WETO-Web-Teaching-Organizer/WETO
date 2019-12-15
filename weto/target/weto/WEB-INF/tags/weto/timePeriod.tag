<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ attribute name="starting" required="true"%>
<%@ attribute name="ending" required="true"%>

<s:if test="#attr.starting != null && #attr.ending != null">
  ${starting}
  -
  ${ending}
</s:if>
<s:elseif test="#attr.starting != null">
  <s:text name="timePeriod.text.starting" />
  ${starting}
</s:elseif>
<s:elseif test="#attr.ending != null">
  <s:text name="timePeriod.text.ending" />
  ${ending}
</s:elseif>
