<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/taskdocumenticonselector.js"></script>
<div id="page-content-wrapper">
  <div class="content-col">
    <div id = "taskDocumentsPopup" class ="center">
      <h3>
        <s:text name="taskDocuments.title.uploaded" />
      </h3>
      <div class="contentBox">
        <s:if test="documents.isEmpty()">
          <s:text name="taskDocuments.message.noDocuments" />
        </s:if>
        <s:else>
          <s:url action="popupTaskDocuments" var="toggleThumbnailsUrl" >
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{taskDocumentsTabId}" />
            <s:param name="dbId" value="%{dbId}" />
            <s:param name="showAsThumbnails" value="!showAsThumbnails" />
          </s:url>
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
                  <!-- Toggle button for viewing as image thumbnails or as a list of files. -->
                  <s:a href="%{toggleThumbnailsUrl}" cssClass="linkButton">
                    <s:text name="taskDocuments.header.toggleThumbnails" />
                  </s:a>
                </th>
              </tr>
            </thead>
            <tbody>
              <s:iterator value="documents" var="document">
                <!-- Link used for downloading image when selected -->
                <s:url value="downloadTaskDocument.action" var="downloadTaskDocumentURL" escapeAmp="false">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="documentId" value="#document.id" />
                </s:url>
                <tr>
                  <td>
                    <span class="smallLinkButton" onClick="submitTaskDocument('${downloadTaskDocumentURL}')">
                      ${document.fileName}
                    </span>
                  </td>
                  <s:if test="#document.contentFileSize == null">
                    <td>${document.fileSize}</td>
                  </s:if>
                  <s:else>
                    <td>${document.contentFileSize}</td>
                  </s:else>
                  <td>
                    <s:if test="showAsThumbnails">
                      <s:if test="#document.contentMimeType.indexOf('image') == 0">
                        <!-- Use image as thumbnail, unless not an image -->
                        <img src="${downloadTaskDocumentURL}" class="thumbnail" onClick="submitTaskDocument('${downloadTaskDocumentURL}')" />
                      </s:if>
                      <s:else>
                        <img src="images/ajax-loader.gif" onload="iconFileName(this, '${document.fileName}');" class="typeicon" onClick="submitTaskDocument('${downloadTaskDocumentURL}')" />
                      </s:else>
                    </s:if>
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </s:else>
      </div>
      <!-- Only show upload if allowed to do so -->
      <s:if test="allowUpload">
        <!--A form used to select a file and upload for course -->
        <form action="<s:url action="popupAddTaskDocument" />" method="post" enctype="multipart/form-data">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <input type="hidden" name="showAsThumbnails" value="${showAsThumbnails}" />
          <h3><s:text name="taskDocuments.header.addUpload" /></h3>
          <input type="file" name="documentFile" /><input type="submit" value="<s:text name="general.header.upload" />" class="linkButton" />
        </form>
      </s:if>
    </div>
  </div>
</div>
<script>
  function submitTaskDocument(URL) {
    // '&amp;' should be replaced with '&' so that the URL works with html
    URL = URL.replace(/&amp;/g, '&');
    if ("${contextPopup}" !== "none") {
      var args = top.tinymce.activeEditor.windowManager.getParams();
      win = (args.window);
      input = (args.input);
      win.document.getElementById(input).value = URL;
      top.tinymce.activeEditor.windowManager.close();
    }
  }
</script>
