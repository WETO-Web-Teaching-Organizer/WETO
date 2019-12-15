<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="contentBox">
  <h2><s:text name="downloadTask.title" /></h2>
  <form action="<s:url action="commitdownloadTask" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <table>
      <tr>
        <td colspan="2">
          <s:text name="downloadTask.instructions" />
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="general.header.fileName" />:
        </td>
        <td>
          <input type="text" name="fileName" value="task.zip" size="50" />
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="downloadTask.header.submitterLogins" />:
        </td>
        <td>
          <input type="text" name="submitterLogins" size="50" />
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="submissions.header.downloadLatestOrBest" />:
        </td>
        <td>
          <input type="checkbox" name="onlyLatestOrBest" value="true"
                 <s:if test="onlyLatestOrBest">checked="checked"</s:if> />
          </td>
        </tr>
        <tr>
          <td>
          <s:text name="submissions.header.doNotCompress" />:
        </td>
        <td>
          <input type="checkbox" name="doNotCompress" value="true"
                 <s:if test="doNotCompress">checked="checked"</s:if> />
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <input type="submit" value="<s:text name="general.header.download" />" class="linkButton" />
        </td>
      </tr>
    </table>
  </form>
</div>
<div class="contentBox">
  <h2><s:text name="uploadTask.title" /></h2>
  <form action="<s:url action="uploadTask" />" method="post" enctype="multipart/form-data">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <table>
      <tr>
        <td colspan="2"><s:text name="uploadTask.instructions" /></td>
      </tr>
      <tr>
        <td>
          <s:text name="general.header.fileName" />:
        </td>
        <td>
          <input type="file" name="taskFile" />
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="uploadTask.header.zipRootDir" />:
        </td>
        <td>
          <input type="text" name="zipRootDir" size="50" /><br />
          <input type="checkbox" name="autoSelectFirst" value="true"
                 <s:if test="autoSelectFirst">checked="checked"</s:if> />
          <s:text name="uploadTask.header.autoSelectFirst" />
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="uploadTask.header.studentLogins" />:
        </td>
        <td>
          <input type="text" name="studentLogins" size="50" />
        </td>
      </tr>
      <tr>
        <td>
          <s:text name="uploadTask.header.submitterLogins" />:
        </td>
        <td>
          <input type="text" name="submitterLogins" size="50" />
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <input type="submit" value="<s:text name="general.header.upload" />" class="linkButton" />
        </td>
      </tr>
    </table>
  </form>
</div>
