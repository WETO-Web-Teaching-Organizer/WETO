<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="contentBox">
    <form action="<s:url action="saveNotificationSettings" />" method="post">
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />

        <s:if test="%{saveFailed}">
            <div class="alert alert-danger" role="alert">
                <span class="glyphicon glyphicon-alert"></span>
                <s:text name="notificationSettings.header.saveFailed" />
            </div>
        </s:if>

        <h2><s:text name="notificationSettings.header.notificationSettings" /></h2>
        <s:iterator value="%{settings}" var="setting">
            <table>
                <thead>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            <label for="<s:text name="%{#setting.type}" />.notifications">
                                <s:text name="notificationSettings.header.%{#setting.type}" /> - <s:text name="notificationSettings.form.notifications" />
                            </label>
                        </td>
                        <td>
                            <input type="checkbox"
                                   name="settingsMap['<s:text name="%{#setting.type}" />_notifications']"
                                   id="<s:text name="%{#setting.type}" />.notifications"
                                   <s:if test="%{#setting.notifications}">checked="checked"</s:if>
                                   value="true"
                            />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="<s:text name="%{#setting.type}" />.emailNotifications">
                                <s:text name="notificationSettings.header.%{#setting.type}" /> - <s:text name="notificationSettings.form.emailNotifications" />
                            </label>
                        </td>
                        <td>
                            <input type="checkbox"
                                   name="settingsMap['<s:text name="%{#setting.type}" />_emailNotifications']"
                                   id="<s:text name="%{#setting.type}" />.emailNotifications"
                                   <s:if test="%{#setting.emailNotifications}">checked="checked"</s:if>
                                   value="true"
                            />
                        </td>
                    </tr>
                </tbody>
            </table>
        </s:iterator>
        <input type="submit" value="<s:text name="notificationSettings.form.save" />" class="linkButton" />
    </form>
</div>