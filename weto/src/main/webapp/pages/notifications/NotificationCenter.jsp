<%@ include file="/WEB-INF/taglibs.jsp"%>

<div class="notificationCenter">
    <h1><s:text name="notificationCenter.header.notificationCenter" /></h1>
    <form id="notification-center-filters" action="<s:url action="viewNotifications" />" method="post">
        <h3><s:text name="notificationCenter.header.filters" /></h3>
        <div class="input-group">
            <label for="courseId"><s:text name="notificationCenter.header.course" /></label>
            <select id="courseId" name="courseId" class="form-control">
                <s:iterator value="%{courseIdsNames}" var="courseIdName">
                    <option value="${key}" <s:if test="%{courseId == key}">selected="selected"</s:if>>${value}</option>
                </s:iterator>
            </select>
        </div>
        <div class="input-group">
            <label for="type"><s:text name="notificationCenter.header.type" /></label>
            <select id="type" name="type" class="form-control">
                <s:iterator value="%{notificationTypes}" var="notificationType">
                    <option value="${key}" <s:if test="%{key == type}">selected="selected"</s:if>><s:text name="notificationCenter.header.%{#notificationType.value}" /></option>
                </s:iterator>
            </select>
        </div>
        <div class="input-group">
            <label for="dateDesc"><s:text name="notificationCenter.header.date" /></label>
            <select id="dateDesc" name="dateDesc" class="form-control">
                <option value="true"><s:text name="notificationCenter.header.newestToOldest" /></option>
                <option value="false" <s:if test="%{!dateDesc}">selected="selected"</s:if>><s:text name="notificationCenter.header.oldestToNewest" /></option>
            </select>
        </div>
        <input type="submit" value="<s:text name="notificationCenter.form.update" />" class="linkButton" />
    </form>
    <h3 id="notifications-header"><s:text name="notificationCenter.header.notifications" /></h3>
    <s:if test="%{!notifications.isEmpty()}">
        <table class="notificationsTable table table-striped">
            <thead>
                <tr>
                    <th></th>
                    <th><s:text name="notificationCenter.header.course" /></th>
                    <th><s:text name="notificationCenter.header.type" /></th>
                    <th><s:text name="notificationCenter.header.notification" /></th>
                    <th><s:text name="notificationCenter.header.link" /></th>
                    <th><s:text name="notificationCenter.header.date" /></th>
                </tr>
            </thead>
            <tbody>
            <s:iterator value="%{notifications}" var="notification">
                <tr>
                    <td><s:if test="%{!#notification.readByUser}"><span>New!</span></s:if></td>
                    <td>
                        <s:set var="course" value="%{courseMap.get(#notification.courseId)}" />
                        <s:url action="viewTask" var="courseUrl">
                            <s:param name="taskId" value="%{#course.courseTaskId}" />
                            <s:param name="tabId" value="0" />
                            <s:param name="dbId" value="%{#course.databaseId}" />
                        </s:url>
                        <a href="${courseUrl}"><s:text name="%{courseIdsNames.get(#notification.courseId)}" /></a>
                    </td>
                    <td><s:text name="notificationCenter.header.%{#notification.type}" /></td>
                    <td>${notification.message}</td>
                    <td>
                        <s:if test="%{#notification.link != null && #notification.link.length() > 0}" >
                        <a href="${notification.link}">View</a>
                        </s:if>
                    </td>
                    <td>
                        <s:set var="timestampObject" value="%{#notification.getTimestampAsObject()}" />
                        <s:if test="%{#timestampObject != null}">
                            <s:text name="%{#timestampObject.toString()}" />
                        </s:if>
                    </td>
                </tr>
            </s:iterator>
            </tbody>
        </table>
    </s:if>
    <s:else>
        <p><s:text name="notificationCenter.header.notificationsIsEmpty" /></p>
    </s:else>
</div>
