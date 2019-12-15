<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h2>
    <s:if test="newUser">
      <s:text name="editUsers.header.addOne" />
      <s:set var="nextStep">commitAddUser</s:set>
    </s:if>
    <s:else>
      <s:text name="editUsers.header.edit" />
      <s:set var="nextStep">commitUpdateUser</s:set>
    </s:else>
  </h2>
  <form name="userForm" action="<s:url action="%{#nextStep}" />" method="post">
    <table>
      <tr>
        <s:if test="newUser">
          <td><s:text name="general.header.loginName" />*</td>
          <td><input type="text" name="loginName" value="${loginName}" size="30" autofocus /></td>
          </s:if>
          <s:else>
          <td><s:text name="general.header.loginName" /></td>
          <td>
            <input type="hidden" name="loginName" value="${loginName}" />
            ${loginName}
          </td>
        </s:else>
      </tr>
      <tr>
        <td><s:text name="editUsers.header.setPassword" /></td>
        <td><s:checkbox name="setPassword" value="false" onClick="showPassword(this.checked)" /></td>
      </tr>
      <tr>
        <td><s:text name="general.header.password" /></td>
        <td><s:password name="password" id="password" size="30" disabled="true"/></td>
      </tr>
      <tr>
        <td><s:text name="general.header.studentNumber" /></td>
        <td><input type="text" name="studentNumber" value="${studentNumber}" size="30" /></td>
      </tr>
      <tr>
        <td><s:text name="general.header.firstName" />*</td>
        <td><input type="text" name="firstName" value="${firstName}" size="30" /></td>
      </tr>
      <tr>
        <td><s:text name="general.header.lastName" />*</td>
        <td><input type="text" name="lastName" value="${lastName}" size="30" /></td>
      </tr>
      <tr>
        <td><s:text name="general.header.email" />*</td>
        <td><input type="text" name="email" value="${email}" size="30" /></td>
      </tr>
      <tr>
        <td colspan="2">
          <s:if test="newUser">
            <input type="submit" value="<s:text name="general.header.create" />" class="linkButton" />
          </s:if>
          <s:else>
            <input type="submit" value="<s:text name="general.header.save" />" class="linkButton" />
          </s:else>
        </td>
      </tr>
    </table>
  </form>
</div>
<s:if test="newUser">
  <div class="content-col">
    <h2><s:text name="editUsers.header.importUsers" /></h2>
    <s:text name="editUsers.instructions.import" />
    <div class="contentBox">
      <h4><s:text name="editUsers.header.formSubmit" /></h4>
      <form action="<s:url action="batchCreateUsers" />" method="post" method="post">
        <p>
          <s:textarea name="usersText" rows="20" cols="65" /><br />
          <input type="submit" value="<s:text name="general.header.submit" />" class="linkButton" />
        </p>
      </form>
    </div>
    <div class="contentBox">
      <h4><s:text name="editUsers.header.fileUpload" /></h4>
      <form action="<s:url action="batchCreateUsers" />" method="post" enctype="multipart/form-data">
        <input type="file" name="usersFile" />
        <input type="submit" value="<s:text name="general.header.upload" />" class="linkButton" />
      </form>
    </div>
  </div>
</s:if>
<script>
  function showPassword(status) {
    status = !status;
    document.userForm.password.disabled = status;
  }
</script>