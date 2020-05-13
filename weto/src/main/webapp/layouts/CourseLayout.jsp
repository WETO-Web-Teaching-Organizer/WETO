<!DOCTYPE html>
<%@ include file="/WEB-INF/init.jsp"%>
<html lang='<s:property value="#request.locale.language" />'>
  <head>
    <title>${pageTitle}</title>
    <tiles:insertAttribute name="head" />
    <link rel="stylesheet" href="css/${courseCssFilename}" />
  </head>
  <body>
    <tiles:insertAttribute name="logout" />
    <div id="sidebartoggle-wrapper">
      <!-- Sidebar toggler -->
      <button
        id="sidebartoggle-button" type="button" class="btn btn-default btn-sidebar"
        aria-label="Toggle sidebar/tabs" title ="Toggle sidebar/tabs" data-toggle="tooltip" data-container="body" data-placement="bottom">
        <span class="glyphicon glyphicon-menu-hamburger"></span>
      </button>
      <s:if test="navigator.masterUserId != null">
        <!-- Logout button -->
        <span class="buttonConfirmContainer" id="logoutSpan">
          <button
            id ="logout-button" onclick="toggleConfirmBox(this)" class="btn btn-default btn-sidebar"
            title="Log out" data-toggle="tooltip" data-container="body" data-placement="bottom">
            <span class="glyphicon glyphicon-log-out"></span>
          </button>
          <div class="confirmBox text-center">
            <s:text name="general.header.logoutConfirm" />
            <div>
              <button type="button" class="btn btn-sm btn-danger" onclick="$('#logoutContainer').show()"><s:text name="general.header.logout" /></button>
              <button type="button" class="btn btn-sm btn-default" onclick="toggleConfirmBox(this)">
                <s:text name="general.header.cancel" />
              </button>
            </div>
          </div>
        </span>
      </s:if>
      <!-- Notification settings button -->
      <button id="notification-center-button" class="btn btn-default btn-sidebar" type="button"
              aria-label="Notification center" title="Notification center"
              onclick="window.location.href = '<s:url action="viewNotifications" />'">
        <span class="glyphicon glyphicon-bell <s:if test="unreadNotifications > 0">unreadNotifications</s:if>"></span>
      </button>
      <!-- Buttons only shown for teacher -->
      <s:if test="navigator.reallyTeacher">
        <s:url action="switchRole" var="switchRoleUrl">
          <s:param name="taskId" value="%{taskId}" />
          <s:param name="tabId" value="%{tabId}" />
          <s:param name="dbId" value="%{dbId}" />
        </s:url>
        <span id="teacherRole-status">
          <s:if test="navigator.teacher">
            <!-- Role Teacher -->
            <button onclick="window.location.href = '${switchRoleUrl}'" class="btn btn-default btn-sidebar" title="Switch to student view">
              <img src="<s:url value='/images/roleoff.png'/>" width="30" height="30" style="border: 0" alt="Student view"/>
            </button>
          </s:if>
          <s:else>
            <!-- Specific student role -->
            <s:if test="navigator.studentRole">
              <button onclick="window.location.href = '${switchRoleUrl}'" class="btn btn-default btn-sidebar" title="Role: %{navigator.user.firstName} %{navigator.user.lastName}">
                ${navigator.user.firstName.charAt(0)} ${navigator.user.lastName.charAt(0)}
              </button>
            </s:if>
            <!-- General student view -->
            <s:else>
              <button onclick="window.location.href = '${switchRoleUrl}'" class="btn btn-default btn-sidebar" title="Switch to teacher view">
                <img class="roleactive" src="<s:url value='/images/roleon.png'/>" width="30" height="30" style="border: 0" alt=""Teacher view/>
              </button>
            </s:else>
          </s:else>
        </span>
      </s:if>
    </div>
    <div id="wrapper" style="display: none">
      <script>
        // This code snippet has to be located here to ensure
        // execution before rendering other parts of page
        var showSideBar = Cookies.get('showSideBar');
        var wrapper = jQuery("#wrapper");
        if (showSideBar == 'false')
        {
          wrapper.addClass('toggled');
        }
        wrapper.show();
      </script>
      <div id="sidebar-column">
        <div id="sidebar-wrapper">
          <nav id="sidebar-nav">
            <!-- First item is always weto-mainpage -->
            <div id = "main-page-link" class ="next"
                 title = "WETO mainpage">
              <s:a action="listCourses">
                <s:text name="weto.title" />
              </s:a>
            </div>
            <!-- Print course name -->
            <div id = "userinfo">
              <div class="user-status">
                <!-- Information about the user -->
                <div class="user-status_namefield">${navigator.realUser.firstName}&#32;&#32;${navigator.realUser.lastName}</div>
                <s:if test="navigator.masterUserId != null">
                  <div class="logoutfield center">
                    <span id = "loginButton" onclick="$('#logoutContainer').show()">
                      <s:text name="general.header.logout" />
                    </span>
                  </div>
                  <div class= "role-button">
                    <s:if test="navigator.reallyTeacher">
                      <s:url action="switchRole" var="switchRoleUrl">
                        <s:param name="taskId" value="%{taskId}" />
                        <s:param name="tabId" value="%{tabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                      </s:url>
                      <s:if test="navigator.teacher">
                        &#32;
                        <s:a href="%{switchRoleUrl}" cssClass="smallLinkButton yellow">
                          <s:text name="sidebar.header.teacherView" />
                        </s:a>
                      </s:if>
                      <s:else>
                        &#32;
                        <s:a href="%{switchRoleUrl}" cssClass="smallLinkButton yellow">
                          <s:if test="navigator.studentRole">
                            Role:
                            ${navigator.user.firstName}
                            &#32;
                            ${navigator.user.lastName}
                          </s:if>
                          <s:else>
                            <s:text name="sidebar.header.studentView" />
                          </s:else>
                        </s:a>
                      </s:else>
                    </s:if>
                    <s:elseif test="navigator.isPublicView">
                      <div class="user-status_namefield">
                        <s:text name="general.header.publicView" />
                      </div>
                    </s:elseif>
                    <s:elseif test="navigator.student && !navigator.isHopsCourse">
                      <s:url action="viewStudent" var="viewStudentUrl">
                        <s:param name="taskId" value="%{taskId}" />
                        <s:param name="tabId" value="%{tabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                        <s:param name="studentId" value="%{courseUserId}" />
                      </s:url>
                      <a href = "${viewStudentUrl}" id = "studentGrades">
                        <s:text name="sidebar.header.profile" />
                      </a>
                    </s:elseif>
                  </div>
                </s:if>
                <s:else>
                  <!-- Login link, if the user is not logged in -->
                  <div>
                    <center>
                      <s:url action="enterLogin" var="loginUrl">
                        <s:param name="taskId" value="%{taskId}" />
                        <s:param name="tabId" value="%{tabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                      </s:url>
                      <s:a id="loginButton" href="%{loginUrl}" cssClass="btn btn-primary-small">
                        <s:text name="general.header.login" />
                      </s:a>
                    </center>
                  </div>
                </s:else>
              </div>
            </div>
            <div id ="course-name">
              <s:url action="viewTask" var="taskUrl">
                <s:param name="taskId" value="%{courseTaskId}" />
                <s:param name="tabId" value="0" />
                <s:param name="dbId" value="%{dbId}" />
              </s:url>
              <s:a href="%{taskUrl}" title = "Course mainpage">${courseName}</s:a>
              <!-- Notification settings button -->
              <s:if test="navigator.masterUserId != null && courseName != null">
                <s:url action="viewNotificationSettings" var="notificationSettingsUrl">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                </s:url>
                <button id="notification-settings-button" class="btn btn-default btn-sidebar" type="button"
                        aria-label="Notification settings" title="Notification settings"
                        onclick="window.location.href = '${notificationSettingsUrl}'">
                  <span class="glyphicon glyphicon-cog"></span>
                </button>
              </s:if>
              </div>
            <s:if test="!navigationTree.isEmpty()">
              <tiles:insertAttribute name="navMenu" />
            </s:if>
          </nav>
        </div>
      </div>
      <div id="page-content-wrapper">
        <div class="container-fluid">
          <div class="row">
            <tiles:insertAttribute name="tabs" />
            <div id="courseContentPane" class="col-md-12 col-lg-10 col-sm-12 col-md-offset-0 col-lg-offset-1 col-sm-offset-0 content-tabs-actions-body">
              <div id="course-header-wrapper">
                <s:if test="editTask">
                  <form id="editTaskForm">
                    <input type="hidden" name="taskId" value="${taskId}" />
                    <input type="hidden" name="tabId" value="${tabId}" />
                    <input type="hidden" name="dbId" value="${dbId}" />
                    <div class="editable" id="taskNameDiv">
                      ${task.name}
                    </div>
                    <textarea name="taskName" id="taskName" style="display:none;"></textarea>
                    <tiles:insertAttribute name="body" />
                  </form>
                </s:if>
                <s:else>
                  <ol id="breadcrumbs" class="breadcrumb">
                    <!-- First item is always weto -->
                    <li>
                      <s:a action="listCourses">
                        <s:text name="weto.title" />
                      </s:a>
                    </li>
                    <!-- Rest of the bread crumbs -->
                    <s:iterator value="taskPath" var="taskItem" status="pathStatus">
                      <s:url action="viewTask" var="viewTaskUrl">
                        <s:param name="taskId" value="#taskItem[0]" />
                        <s:param name="tabId" value="#mainTabId" />
                        <s:param name="dbId" value="%{dbId}" />
                      </s:url>
                      <li>
                        <s:a href="%{viewTaskUrl}" >
                          ${taskItem[1]}
                        </s:a>
                      </li>
                    </s:iterator>
                  </ol>
                  <h1 class="taskHeader">
                    ${task.name}
                  </h1>
                  <div class="course-content-wrapper">
                    <tiles:insertAttribute name="body" />
                  </div>
                </s:else>
              </div>
            </div>
            <div class="infoBar" onclick="$(this).hide()"><tiles:insertAttribute name="infoBar" /></div>
          </div>
        </div>
      </div>
    </div>
    <%--<div id="footer"><tiles:insertAttribute name="footer" /></div>--%>
    <script>
      function reloginPopup(tinymceInstance)
      {
        tinymceInstance.windowManager.open({
          file: '${reloginUrl}',
          title: '<s:text name="general.header.relogin" />',
          width: window.innerWidth * 0.8,
          height: window.innerHeight * 0.8
        });
      }
    </script>
  </body>
</html>