<%@ include file="/WEB-INF/taglibs.jsp"%>

<div class="notificationCenter">
    <h1><s:text name="notificationCenter.header.notificationCenter" /></h1>
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
            <option value="true"><s:text name="notificationCenter.header.newestToOldest" /></option>
            <option value="false" <s:if test="%{!dateDesc}">selected="selected"</s:if>><s:text name="notificationCenter.header.oldestToNewest" /></option>
        </select>
        <input type="submit" value="<s:text name="notificationCenter.form.update" />" class="linkButton" />
    </form>
    <s:if test="%{!notifications.isEmpty()}">
        <table class="notificationsTable">
            <thead>
                <tr>
                    <th></th>
                    <th><s:text name="notificationCenter.header.course" /></th>
                    <th><s:text name="notificationCenter.header.type" /></th>
                    <th><s:text name="notificationCenter.header.notification" /></th>
                    <th></th>
                    <th><s:text name="notificationCenter.header.date" /></th>
                </tr>
            </thead>
            <tbody>
            <s:iterator value="%{notifications}" var="notification">
                <tr>
                    <td><s:if test="%{!#notification.readByUser}"><span>New</span></s:if></td>
                    <td><s:text name="%{courseIdsNames.get(#notification.courseId)}" /></td>
                    <td><s:text name="notificationCenter.header.%{#notification.type}" /></td>
                    <td><s:text name="%{#notification.message}" /></td>
                    <td>
                        <s:if test="%{#notification.link != null && #notification.link.length() > 0}" >
                        <a href="${notification.link}">Link</a>
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
