<%@ include file="/WEB-INF/taglibs.jsp"%>

<div class="row main-row-page">
  <div class="main-header col-md-6 col-sm-12 col-xs-12">
    <h1>
      <s:text name="weto.title"/>
    </h1>
    <h4><s:text name="weto.subtitle"/></h4>
  </div>
  <div class="col-md-6 content-row col-sm-12 col-xs-12">

    <div class="user-status">
      <!-- Login -->
      <s:if test="navigator != null && navigator.masterUserId != null">
        <div class="logoutbutton center" onclick="$('#logoutContainer').show()">
          <s:text name="general.header.logout"/>
        </div>
        <div class="logfield-username">
          <h4>
            &#32;
            ${navigator.realUser.firstName}
            &#32;
            ${navigator.realUser.lastName}
          </h4>
        </div>
      </s:if>
      <s:else>
        <s:if test="inUpdateMode">
          <h1>
            <s:text name="login.message.inUpdateMode" />
          </h1>
        </s:if>
        <s:else>
          <s:if test="navigator == null || navigator.masterUserId == null">
            <div id = "loginForm">
              <h2 class="login_header">
                <s:text name="login.title" />
              </h2>
              <form action="<s:url action="submitLogin" />" method="post" class ="form-signin">
                <div class="loginfield center-block" class="span4 offset4">
                  <div class ="login--username">
                    <s:text name="general.header.username" />
                  </div>
                  <input class ="form-control" type="text" name="username" value="${username}" size="12" autofocus />
                  <div class ="login--passwordtext">
                    <s:text name="general.header.password" />
                  </div>
                  <input class="form-control center-block" type="password" name="password" value="${password}" size="12" required/>
                  <input class="btn btn-lg btn-primary btn-block" id="submitloginbutton" type="submit" value="<s:text name="general.header.submit" />" />
                </div>
              </form>
            </div>
          </s:if>
        </s:else>
      </s:else>
    </div>

    <div class="courselist">
      <!--[if IE]>
        If you want fully functional WETO, we recommend to you to download
        newest <a href="https://www.mozilla.org/fi/firefox/new/>Mozilla Firefox</a>, Google Chrome or Safari browser.
      <![endif]-->
      <s:if test="navigator == null">
        <s:text name="courses.instructions.login" />
      </s:if>
      <s:else>
        <s:text name="courses.instructions.loggedIn" />
      </s:else>
      <s:if test="subjects.isEmpty()">
        <s:text name="courses.message.noCourses" />
      </s:if>
      <s:else>
        <s:iterator value="subjects" var="subject">
          <h2><s:property value="#subject.name" /></h2>
          <s:if test="registeredCourses[#subject.id] != null
                && !registeredCourses[#subject.id].isEmpty()">
            <h3><s:text name="courses.header.yourCourses" /></h3>
            <ul>
              <s:iterator var="course" value="registeredCourses[#subject.id]">
                <li>
                  <s:url action="viewTask" var="viewCourseUrl">
                    <s:param name="taskId" value="#course.courseTaskId" />
                    <s:param name="tabId" value="#mainTabId" />
                    <s:param name="dbId" value="#course.databaseId" />
                  </s:url>
                  <s:a href="%{viewCourseUrl}">
                    ${course.name}
                  </s:a>
                  <s:if test="viewPeriods[#course.masterTaskId][0] != null ||
                        viewPeriods[#course.masterTaskId][1] != null">
                    (<weto:timePeriod
                      starting="${viewPeriods[course.masterTaskId][0]}"
                      ending="${viewPeriods[course.masterTaskId][1]}" />)
                  </s:if>
                </li>
              </s:iterator>
            </ul>
          </s:if>
          <s:if test="courses[#subject.id] != null
                && !courses[#subject.id].isEmpty()">
            <h3><s:text name="courses.header.availableCourses" /></h3>
            <ul>
              <s:iterator var="course" value="courses[#subject.id]">
                <li>
                  <s:url action="viewTask" var="viewCourseUrl">
                    <s:param name="taskId" value="#course.courseTaskId" />
                    <s:param name="tabId" value="#mainTabId" />
                    <s:param name="dbId" value="#course.databaseId" />
                  </s:url>
                  <s:a href="%{viewCourseUrl}">
                    ${course.name}
                  </s:a>
                  (<s:text name="courses.header.registrationDeadline" />: ${registerPeriods[course.masterTaskId][1]} )
                </li>
              </s:iterator>
            </ul>
          </s:if>
        </s:iterator>
        <s:if test="inactiveCourses != null && !inactiveCourses.isEmpty()">
          <div id="showDetailsLinkDiv">
            <div class="leftAlign">
              <span style="cursor:pointer" onclick="showDetails()">[<s:text name="courses.header.showInactiveCourses" />]</span>
            </div>
          </div>
          <div id="hideDetailsLinkDiv">
            <div class="leftAlign">
              <span style="cursor:pointer" onclick="hideDetails()">[<s:text name="courses.header.hideInactiveCourses" />]</span>
            </div>
          </div>
          <div id="stackTraceDiv" style="display: none">
            <h3><s:text name="courses.header.inactiveCourses" /></h3>
            <ul>
              <s:iterator var="course" value="inactiveCourses">
                <li>
                  <s:url action="viewTask" var="viewCourseUrl">
                    <s:param name="taskId" value="#course.courseTaskId" />
                    <s:param name="tabId" value="#mainTabId" />
                    <s:param name="dbId" value="#course.databaseId" />
                  </s:url>
                  <s:a href="%{viewCourseUrl}">
                    ${course.name}
                  </s:a>
                  <s:if test="viewPeriods[#course.masterTaskId][0] != null ||
                        viewPeriods[#course.masterTaskId][1] != null">
                    (<weto:timePeriod
                      starting="${viewPeriods[course.masterTaskId][0]}"
                      ending="${viewPeriods[course.masterTaskId][1]}" />)
                  </s:if>
                </li>
              </s:iterator>
            </ul>
          </div>
          <script>
            function showDetails() {
              document.getElementById("showDetailsLinkDiv").style.display = "none";
              document.getElementById("hideDetailsLinkDiv").style.display = "inline";
              document.getElementById("stackTraceDiv").style.display = "inline";
            }
            function hideDetails() {
              document.getElementById("showDetailsLinkDiv").style.display = "inline";
              document.getElementById("hideDetailsLinkDiv").style.display = "none";
              document.getElementById("stackTraceDiv").style.display = "none";
            }
          </script>
        </s:if>
      </s:else>
    </div>

    <s:if test="!news.isEmpty()">
      <div id="news-container" data-mcs-theme="dark">
        <h4 class ="news-header"><s:text name="news.title"/></h4>
        <s:iterator value="news" var="newsItem">
          <div class="news">
            <div class="newsTitle">${newsItem.title}</div>
            <div class="newsContent">
              ${newsItem.text}
            </div>
            <div class="newsTimestamp">${newsItem.startDateString}</div>
          </div>
        </s:iterator>
      </div>
    </s:if>
  </div>
</div>

<script>
  $(function () {
    $("#news-container").mCustomScrollbar({
      axis: "y",
      setHeight: 180,
      setWidth: "100%",
      scrollInertia: 0
    });
  });
</script>
