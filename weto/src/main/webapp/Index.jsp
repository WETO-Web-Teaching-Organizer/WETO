<%@ include file="/WEB-INF/taglibs.jsp"%>
<%
	response.setStatus(301);
	response.setHeader("Location", "listCourses.action");
	response.setHeader("Connection", "close");
%>