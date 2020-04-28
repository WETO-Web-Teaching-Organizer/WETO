<%--
  Created by IntelliJ IDEA.
  User: Jipsu
  Date: 29.3.2020
  Time: 19.59
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/taglibs.jsp"%>
<h1><s:text name="notificationCenter.header.notificationCenter" /></h1>

<div class="filters">
    <form action="<s:url action="viewNotifications" />" method="post">
        <label for="courseId"><s:text name="notificationCenter.header.course" /></label>
        <select name="courseId">
        <s:iterator value="%{courseIdsNames}" var="courseIdName">
            <option value="${key}" <s:if test="%{courseId == key}">selected="selected"</s:if>>${value}</option>
        </s:iterator>
        </select>
        <label for="type"><s:text name="notificationCenter.header.type" /></label>
        <select name="type">
        <s:iterator value="%{notificationTypes}" var="notificationType">
            <option value="${key}" <s:if test="%{key == type}">selected="selected"</s:if>><s:text name="notificationCenter.header.%{#notificationType.value}" /></option>
        </s:iterator>
        </select>
        <label for="dateDesc"><s:text name="notificationCenter.header.date" /></label>
        <select name="dateDesc">
            <option value="false"><s:text name="notificationCenter.header.newestToOldest" /></option>
            <option value="true" <s:if test="%{dateDesc}">selected="selected"</s:if>><s:text name="notificationCenter.header.oldestToNewest" /></option>
        </select>
        <input type="submit" value="<s:text name="notificationCenter.form.update" />" class="linkButton" />
    </form>
</div>

<hr>

<div class="notifications">
<s:if test="%{!notifications.isEmpty()}">
<table class="notificationsTable">
    <thead>
        <tr>
            <td><s:text name="notificationCenter.header.course" /></td>
            <td><s:text name="notificationCenter.header.type" /></td>
            <td><s:text name="notificationCenter.header.notification" /></td>
            <td><s:text name="notificationCenter.header.date" /></td>
        </tr>
    </thead>
    <tbody>
    <s:iterator value="%{notifications}" var="notification">
        <tr <s:if test="%{!#notification.readByUser}">style="background-color:#eafeea"></s:if>>
            <td><s:text name="%{courseIdsNames.get(#notification.courseId)}" /></td>
            <td><s:text name="notificationCenter.header.%{#notification.type}" /></td>
            <td><s:text name="%{#notification.message}" /></td>
            <td><s:text name="%{#notification.timestamp}" /></td>
        </tr>
    </s:iterator>
</s:if>
<s:else>
    <p><s:text name="notificationCenter.header.notificationsIsEmpty" /></p>
</s:else>
</tbody>
</table>
</div>