<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src ="js/chosen.jquery.min.js"></script>
<div class="content-col">
  <h2>
    <s:if test="permissionId != null">
      <s:text name="editPermission.title.edit" />
    </s:if>
    <s:else>
      <s:text name="editPermission.title.add" />
    </s:else>
  </h2>
  <form action="<s:url action="savePermission" />" method="post" id="editPermission-form">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="allViewTaskId" value="${allViewTaskId}" />
    <input type="hidden" name="permissionId" value="${permissionId}" />
    <table id ="editPermission-Table">
      <tr>
        <td><s:text name="general.header.user" />:</td>
        <td>
          <select name="userLoginName" data-placeholder="<s:text name="general.header.chooseUsers" />" class="chosen-select" multiple>
            <s:if test="userLoginName[0].isEmpty()">
              <option value="" selected="selected">
                <s:text name="general.header.allUsers"/>
              </option>
            </s:if>
            <s:else>
              <option value="">
                <s:text name="general.header.allUsers" />
              </option>
            </s:else>
            <s:iterator var="userItr" value="users">
              <s:if test="userLoginName[0].equals(#userItr.loginName)">
                <option value="${userItr.loginName}" selected="selected">
                  ${userItr.lastName}, ${userItr.firstName} (${userItr.loginName})
                </option>
              </s:if>
              <s:else>
                <option value="${userItr.loginName}">
                  ${userItr.lastName}, ${userItr.firstName} (${userItr.loginName})
                </option>
              </s:else>
            </s:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td><s:text name="general.header.startDate" />:</td>
        <td>
          <input type="text" name="startDate" id="startDate" value="${startDate}" size="12" />
          <button type="button" onclick="this.form.elements['startDate'].value = '';">
            <s:text name="general.header.reset" />
          </button>
        </td>
      </tr>
      <tr>
        <td><s:text name="general.header.startTime" />:</td>
        <td>
          <select name="startHours">
            <s:iterator begin="0" end="23" status="status">
              <s:if test="startHours == #status.index">
                <option value="${status.index}" selected="selected">
                  <s:property value="getText('{0,number,#,##00}', {#status.index})" />
                </option>
              </s:if>
              <s:else>
                <option value="${status.index}">
                  <s:property value="getText('{0,number,#,##00}', {#status.index})" />
                </option>
              </s:else>
            </s:iterator>
          </select>
          <select name="startMinutes">
            <s:iterator begin="0" end="11" status="status">
              <s:if test="startMinutes == #status.index * 5">
                <option value="${5*status.index}" selected="selected">
                  <s:property value="getText('{0,number,#,##00}', {#status.index * 5})" />
                </option>
              </s:if>
              <s:else>
                <option value="${5*status.index}">
                  <s:property value="getText('{0,number,#,##00}', {#status.index * 5})" />
                </option>
              </s:else>
            </s:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td><s:text name="general.header.endDate" />:</td>
        <td>
          <input type="text" name="endDate" id="endDate" value="${endDate}" size="12" />
          <button type="button" onclick="this.form.elements['endDate'].value = '';">
            <s:text name="general.header.reset" />
          </button>
        </td>
      </tr>
      <tr>
        <td><s:text name="general.header.endTime" />:</td>
        <td>
          <select name="endHours">
            <s:iterator begin="0" end="23" status="status">
              <s:if test="endHours == #status.index">
                <option value="${status.index}" selected="selected">
                  <s:property value="getText('{0,number,#,##00}', {#status.index})" />
                </option>
              </s:if>
              <s:else>
                <option value="${status.index}">
                  <s:property value="getText('{0,number,#,##00}', {#status.index})" />
                </option>
              </s:else>
            </s:iterator>
          </select>
          <select name="endMinutes">
            <s:iterator begin="0" end="11" status="status">
              <s:if test="endMinutes == #status.index * 5">
                <option value="${5*status.index}" selected="selected">
                  <s:property value="getText('{0,number,#,##00}', {#status.index * 5})" />
                </option>
              </s:if>
              <s:else>
                <option value="${5*status.index}">
                  <s:property value="getText('{0,number,#,##00}', {#status.index * 5})" />
                </option>
              </s:else>
            </s:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td><s:text name="general.header.type" />:</td>
        <td>
          <select name="permissionType" data-placeholder="<s:text name="editPermissions.header.choose" />" class="chosen-select" multiple>
            <s:iterator var="type" value="permissionTypes">
              <s:if test="isCourseTask() || !#type.coursePermission">
                <s:if test="#type.value == permissionType[0]">
                  <option value="${type.value}" selected="selected">
                    <s:property value="%{getText(#type.property)}" />
                  </option>
                </s:if>
                <s:else>
                  <option value="${type.value}">
                    <s:property value="%{getText(#type.property)}" />
                  </option>
                </s:else>
              </s:if>
            </s:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="editPermissions.header.conditions" />
        </td>
        <td>
          <input type="text" name="permissionDetail" value="${permissionDetail}" />
        </td>
      </tr>
    </table>
    <input type="submit" value="<s:text name="general.header.save" />" class="btn btn-primary" id="permission-save" />
  </form>
</div>
<script>
  $(function () {
    $(".chosen-select").chosen();
    $.datepicker.setDefaults($.datepicker.regional["fi"]);
    $("#startDate").datepicker({
      onSelect: function (selectedDate) {
        $("#endDate").datepicker("option", "minDate", selectedDate);
      }});
    $("#endDate").datepicker({
      onSelect: function (selectedDate) {
        $("#startDate").datepicker("option", "maxDate", selectedDate);
      }});
  });
</script>
