<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/taskdocumenticonselector.js"></script>
<!-- Only show upload if allowed to do so -->
<s:if test="allowUpload">
  <h2>
    <s:text name="taskDocuments.title.addFiles" />
  </h2>
  <s:url action="addTaskDocument" var="addTaskDocumentUrl" />
  <div class="form-group">
    <label><input type="checkbox" id="overWriteBox" name="overWriteExisting" value="true" <s:if test="overWriteExisting">checked="checked"</s:if> />
      <s:text name="taskDocuments.header.overWrite" /></label>
  </div>
  <script>
    Dropzone.options.taskDocumentsDropzone =
            {
              paramName: 'documentFile',
              url: '${addTaskDocumentUrl}?taskId=${taskId}&tabId=${tabId}&dbId=${dbId}&showAsThumbnails=false&overWriteExisting=true',
              method: 'POST',
              addRemoveLinks: false,
              init: function ()
              {
                this.on("addedfile", function (file)
                {
                  this.emit("thumbnail", file, "images/globalFile.png");
                });
                this.on("processing", function (file) {
                  if (document.getElementById('overWriteBox').checked)
                  {
                    this.options.url = '${addTaskDocumentUrl}?taskId=${taskId}&tabId=${tabId}&dbId=${dbId}&showAsThumbnails=false&overWriteExisting=true';
                  } else
                  {
                    this.options.url = '${addTaskDocumentUrl}?taskId=${taskId}&tabId=${tabId}&dbId=${dbId}&showAsThumbnails=false&overWriteExisting=false';
                  }
                });
                this.on('queuecomplete', function (file)
                {
                  if (document.getElementById('overWriteBox').checked)
                  {
                    window.location.search = 'taskId=${taskId}&tabId=${tabId}&dbId=${dbId}&showAsThumbnails=false&overWriteExisting=true';
                  } else
                  {
                    window.location.search = 'taskId=${taskId}&tabId=${tabId}&dbId=${dbId}&showAsThumbnails=false&overWriteExisting=false';
                  }
                });
              }
            };
  </script>
  <form action="<s:url action="addTaskDocument" />" method="post" enctype="multipart/form-data" class="dropzone" id="taskDocumentsDropzone">
    <div class="fallback">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <input type="hidden" name="showAsThumbnails" value="${showAsThumbnails}" />
      <input type="hidden" name="overWriteExisting" value="${overWriteExisting}" />
      <s:text name="taskDocuments.header.addUpload" />: <input type="file" name="documentFile" />
      <input type="submit" value="<s:text name="general.header.upload" />" class="linkButton" />
    </div>
  </form>
</s:if>
<div class="contentBox">
  <s:if test="documents.isEmpty()">
    <h2>
      <s:text name="taskDocuments.message.noDocuments" />
    </h2>
  </s:if>
  <s:else>
    <h2>
      <s:text name="taskDocuments.title.uploaded" />
    </h2>
    <form action="<s:url action="downloadTaskDocuments" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <!-- Table view of the task documents -->
      <table>
        <tr>
          <td style="vertical-align: top">
            <!-- Toggle button for viewing as image thumbnails or as a list of files. -->
            <s:url action="viewTaskDocuments" var="toggleThumbnailsUrl" >
              <s:param name="taskId" value="%{taskId}" />
              <s:param name="tabId" value="%{taskDocumentsTabId}" />
              <s:param name="dbId" value="%{dbId}" />
              <s:param name="showAsThumbnails" value="!showAsThumbnails" />
            </s:url>
            <s:a href="%{toggleThumbnailsUrl}" cssClass="linkButton">
              <s:text name="taskDocuments.header.toggleThumbnails" />
            </s:a>
            <br /><br /><input type="submit" value="<s:text name="taskDocuments.header.downloadSelected" />" class="btn btn-primary" />
            <s:if test="allowDelete == true">
              <br /><br /><br /><input type="submit" name="action:previewDeleteTaskDocuments" value="<s:text name="general.header.deleteSelected" />" class="btn btn-danger" />
            </s:if>
          </td>
          <td>
            <table class="tablesorter" style="margin: 0 0 5px 0">
              <thead>
                <tr>
                  <th>
                    <input type="checkbox" onclick="selectAll(this);" />
                  </th>
                  <th>
                    <s:text name="general.header.fileName" />
                  </th>
                  <th>
                    <s:text name="general.header.fileSize" />
                  </th>
                  <s:if test="showAsThumbnails">
                    <th>
                      <s:text name="taskDocuments.header.thumbnails" />
                    </th>
                  </s:if>
                </tr>
              </thead>
              <tbody>
                <s:iterator value="documents" var="document">
                  <!-- Link used for downloading image when selected -->
                  <s:url action="downloadTaskDocument" var="downloadTaskDocumentURL">
                    <s:param name="taskId" value="%{taskId}" />
                    <s:param name="tabId" value="%{tabId}" />
                    <s:param name="dbId" value="%{dbId}" />
                    <s:param name="documentId" value="#document.id" />
                  </s:url>
                  <tr>
                    <td>
                      <input type ="checkbox" name="documentIds" value="${document.id}">
                    </td>
                    <td>
                      <s:a href="%{downloadTaskDocumentURL}">
                        ${document.fileName}
                      </s:a>
                    </td>
                    <s:if test="#document.contentFileSize == null">
                      <td>${document.fileSize}</td>
                    </s:if>
                    <s:else>
                      <td>${document.contentFileSize}</td>
                    </s:else>
                    <s:if test="showAsThumbnails">
                      <td>
                        <s:a href="%{downloadTaskDocumentURL}">
                          <s:if test="#document.contentMimeType.indexOf('image') == 0">
                            <!-- Use image as thumbnail, unless not an image -->
                            <img src="${downloadTaskDocumentURL}" alt="${document.fileName}" class="thumbnail" />
                          </s:if>
                          <s:else>
                            <img src="images/ajax-loader.gif" alt="" onload="iconFileName(this, '${document.fileName}');" class="typeicon" />
                          </s:else>
                        </s:a>
                      </td>
                    </s:if>
                  </tr>
                </s:iterator>
              </tbody>
            </table>
          </td>
        </tr>
      </table>
    </form>
  </s:else>
</div>
<script>
  function selectAll(cb) {
    var checked = cb.checked;
    // Make all checkboxes the same
    var boxes = document.getElementsByName('documentIds');
    for (var i = 0, box; box = boxes[i]; i++) {
      box.checked = checked;
    }
  }
</script>
