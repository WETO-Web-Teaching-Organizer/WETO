<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="contentBox">
    <h3><s:text name="notificationSettings.header.notificationSettings" /></h3>
    <form action="<s:url action="saveNotificationSettings" />" method="post">
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <s:iterator value="%{settings}" var="setting">
            <h4><s:text name="notificationSettings.header.%{#setting.type}" /></h4>
            <div class="form-group">
                <label for="<s:text name="%{#setting.type}" />.notifications">
                    <input type="checkbox"
                           name="settingsMap['<s:text name="%{#setting.type}" />_notifications']"
                           id="<s:text name="%{#setting.type}" />.notifications"
                           <s:if test="%{#setting.notifications}">checked="checked"</s:if>
                           value="true"
                    />
                    <s:text name="notificationSettings.form.notifications" />
                </label>
                <label for="<s:text name="%{#setting.type}" />.emailNotifications">
                    <input type="checkbox"
                           name="settingsMap['<s:text name="%{#setting.type}" />_emailNotifications']"
                           id="<s:text name="%{#setting.type}" />.emailNotifications"
                           <s:if test="%{#setting.emailNotifications}">checked="checked"</s:if>
                            value="true"
                    />
                    <s:text name="notificationSettings.form.emailNotifications" />
                </label>
            </div>
        </s:iterator>
        <input type="submit" value="<s:text name="notificationSettings.form.save" />" class="linkButton" />
    </form>
</div>