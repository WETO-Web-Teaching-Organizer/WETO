<%--
  Created by IntelliJ IDEA.
  User: Jipsu
  Date: 29.3.2020
  Time: 19.59
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/WEB-INF/taglibs.jsp"%>
<h2><s:text name="Notification Center" /></h2>

<div class="filters">
    <select id = "courses">
        <option value="all">All</option>
        <option value="course2">course2</option>
        <option value="course3">course3</option>
    </select>
    <select id = "types">
        <option value="all">All</option>
        <s:iterator value="%{notificationTypes}" var="notificationType">
            <option value="<s:text name="%{notificationType}" />"><s:text name="%{notificationType}" /></option>
        </s:iterator>
    </select>
    <select id = "dates">
        <option value="newesttooldest">Newest to oldest</option>
        <option value="oldesttonewest">Oldest to newest</option>
    </select>
    <input type="submit" value="Update"/>
</div>

<hr>

<div class="notifications">
    <table>
        <thead>
            <tr>
                <td>Course</td>
                <td>Type</td>
                <td>Notification</td>
                <td>Date</td>
            </tr>
        </thead>
        <tbody>
        <s:iterator value="%{notifications}" var="notification">
            <tr>
                <td><s:text name="%{#notification.courseId}" /></td>
                <td><s:text name="%{#notification.type}" /></td>
                <td>Testi</td>
                <td>Testi</td>
                <%--<td><s:text name="%{#notification.message}" /></td>
                <td><s:text name="%{#notification.createdAt}" /></td>--%>
            </tr>
        </s:iterator>
        </tbody>
    </table>
</div>