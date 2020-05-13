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

        <div class="content-col">
            <h2><s:text name="notificationSettings.header.notificationSettings" /></h2>
            <div class="container-fluid" style="padding: 0;">
                <div class="col-xs-12 col-sm-12 col-md-8 col-lg-6" style="padding: 0;">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th class="header"><s:text name="notificationSettings.header.type"/></th>
                            <th class="header"><s:text name="notificationSettings.header.notifications"/></th>
                            <th class="header"><s:text name="notificationSettings.header.emails"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <s:iterator value="%{settings}" var="setting">
                            <tr>
                                <td>
                                    <s:text name="notificationSettings.header.%{#setting.type}" />
                                </td>
                                <td>
                                    <input type="checkbox"
                                           name="settingsMap['<s:text name="%{#setting.type}" />_notifications']"
                                           id="<s:text name="%{#setting.type}" />.notifications"
                                           <s:if test="%{#setting.notifications}">checked="checked"</s:if>
                                           value="true"
                                    />
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
                        </s:iterator>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <input type="submit" value="<s:text name="notificationSettings.form.save" />" class="linkButton" />
    </form>
</div>