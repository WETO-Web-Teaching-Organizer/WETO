<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/taskdocumenticonselector.js"></script>
<h2>
  <s:text name="taskDocuments.title.deleted" />
</h2>
<div class="contentBox">
  <s:if test="documents.isEmpty()">
    <s:text name="taskDocuments.message.noDocuments" />
  </s:if>
  <s:else>
    <p>
      <s:text name="taskDocuments.text.confirmation" />
    </p>
    <form action="<s:url action="deleteTaskDocuments" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <input type="submit" value="<s:text name="taskDocuments.header.confirmDelete" />" class="linkButton" />
      <!-- Table view of the task documents -->
      <table class="tablesorter" style="margin: 0 0 5px 0">
        <thead>
          <tr>
            <th>
              <s:text name="general.header.fileName" />
            </th>
            <th>
              <s:text name="general.header.fileSize" />
            </th>
            <th>
              <s:text name="taskDocuments.header.thumbnails" />
            </th>
          </tr>
        </thead>
        <tbody>
          <s:iterator value="documents" var="document">
          <input type="hidden" name="documentIds" value="${document.id}" />
          <!-- Link used for downloading image when selected -->
          <s:url action="downloadTaskDocument" var="downloadTaskDocumentURL">
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{tabId}" />
            <s:param name="dbId" value="%{dbId}" />
            <s:param name="documentId" value="#document.id" />
          </s:url>
          <tr>
            <td>
              <s:a href="%{downloadTaskDocumentURL}" onClick="submitTaskDocument('%{downloadTaskDocumentURL}');">
                ${document.fileName}
              </s:a>
            </td>
            <s:if test="#document.contentFileSize == null">
              <td>${document.fileSize}</td>
            </s:if>
            <s:else>
              <td>${document.contentFileSize}</td>
            </s:else>
            <td>
              <s:a href="%{downloadTaskDocumentURL}" onClick="submitTaskDocument('%{downloadTaskDocumentURL}');">
                <s:if test="#document.contentMimeType.indexOf('image') == 0">
                  <!-- Use image as thumbnail, unless not an image -->
                  <img src="${downloadTaskDocumentURL}" class="thumbnail" />
                </s:if>
                <s:else>
                  <img src="images/ajax-loader.gif" onload="iconFileName(this, '${document.fileName}');" class="typeicon" />
                </s:else>
              </s:a>
            </td>
          </tr>
        </s:iterator>
        </tbody>
      </table>
    </s:else>
  </form>
</div>
