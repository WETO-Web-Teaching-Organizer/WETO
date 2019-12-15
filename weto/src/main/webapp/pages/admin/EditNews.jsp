<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/tinymce4/tinymce.min.js"></script>
<div class="content-col">
  <form id="newsForm" action="<s:url action="updateNews" />" method="post" onsubmit="return populateNewsForm()">
    <input type ="hidden" name="newsId" value="${newsId}" />
    <input type="hidden" name="title" id="formNewsTitle" value="" />
    <input type="hidden" name="text" id="formNewsText" value="" />
    <s:if test="newsId != null && newsId != ''">
      <h2><s:text name="editNews.header.edit" /></h2>
    </s:if>
    <s:else>
      <h2><s:text name="editNews.header.add" /></h2>
    </s:else>
    <div class="contentBox">
      <table>
        <tr>
          <td><s:text name="general.header.startDate" /></td>
          <td><input type="text" name="startDate" id="startDate" value="${startDate}" size="10" maxlength="10" /></td>
          <td><s:text name="general.header.startTime" /></td>
          <td>
            <select name="startHour">
              <s:iterator begin="0" end="23" status="status">
                <s:if test="startHour == #status.index">
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
            <select name="startMinute">
              <s:iterator begin="0" end="11" status="status">
                <s:if test="startMinute == #status.index * 5">
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
          <td><s:text name="general.header.endDate" /></td>
          <td><input type="text" name="endDate" id="endDate" value="${endDate}" size="10" maxlength="10" /></td>
          <td><s:text name="general.header.endTime" /></td>
          <td>
            <select name="endHour">
              <s:iterator begin="0" end="23" status="status">
                <s:if test="endHour == #status.index">
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
            <select name="endMinute">
              <s:iterator begin="0" end="11" status="status">
                <s:if test="endMinute == #status.index * 5">
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
      </table>
    </div>
    <s:if test="newsId != null && newsId != ''">
      <div class="news">
        <div id="newsTitle" class="newsTitle" contenteditable="true">${title}</div>
        <div id="newsText" class="newsContent" contenteditable="true">
          ${text}
        </div>
      </div>
      <s:url action="deleteNews" var="deleteNewsUrl">
        <s:param name="newsId" value="%{newsId}" />
      </s:url>
      <input type="submit" value="<s:text name="general.header.save" />" class="btn btn-primary" />
      &nbsp;&nbsp;&nbsp;
      <s:a href="%{deleteNewsUrl}" class="btn btn-danger">
        <s:text name="general.header.delete" />
      </s:a>
    </s:if>
    <s:else>
      <div class="news">
        <div id="newsTitle" class="newsTitle" contenteditable="true">News title (click to edit this)</div>
        <div id="newsText" class="newsContent" contenteditable="true">
          News text (clock to edit this)
        </div>
      </div>
      <input type="submit" value="<s:text name="general.header.create" />" class="btn btn-primary" />
    </s:else>
  </form>
</div>

<script>
  function populateNewsForm()
  {
    document.getElementById('formNewsTitle').value = $('#newsTitle').text();
    document.getElementById('formNewsText').value = document.getElementById('newsText').innerHTML;
    return true;
  }

  $(function () {
    $.datepicker.setDefaults($.datepicker.regional["fi"]);
    $("#startDate").datepicker({
      onSelect: function (selectedDate) {
        $("#endDate").datepicker("option", "minDate", selectedDate);
      }});
    $("#endDate").datepicker({
      onSelect: function (selectedDate) {
        $("#startDate").datepicker("option", "maxDate", selectedDate);
      }});
    tinymce.init({
      selector: "#newsTitle",
      inline: "true",
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      menubar: false,
      toolbar: [
        "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
        "code link image | bold italic underline superscript subscript mathslate | outdent indent bullist numlist | preview"
      ],
      style_formats_merge: true,
      style_formats: [
        {title: 'Code highlight', block: 'pre', classes: 'prettyprint'},
        {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
        {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
        {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
        {title: 'Shadow-box-week', block: 'div', classes: 'content-col'},
        {title: 'Default-Panel-element', block: 'div', classes: 'panel'},
        {title: 'Interactive-code-panel', block: 'div', classes: 'code-panel prettyprint'},
        {title: 'Peerreview-border-box', block: 'div', classes: 'peerreview-border-box'},
        {title: 'Peerreview-highlight-text', block: 'p', classes: 'peerreview-highlight'},
        {title: 'Highlight-text-background', block: 'p', classes: 'highlight-text-background'},
        {title: 'icemint', block: 'div', classes: 'icemint'},
        {title: 'red-panel-box', block: 'div', classes: 'panel-red'},
        {title: 'blue-panel-box', block: 'div', classes: 'panel-blue'},
        {title: 'latte-panel-box', block: 'div', classes: 'panel-latte'},
        {title: 'gray-panel-box', block: 'div', classes: 'panel-darker-gray'},
        {title: 'news-panel-box', block: 'div', classes: 'panel-news'},
        {title: 'news-title', block: 'h5', classes: 'news-title-style'},
        {title: 'only-border-box', block: 'div', classes: 'only-border'}
      ]
    });
    tinymce.init({
      selector: "#newsText",
      inline: "true",
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      menubar: false,
      toolbar: [
        "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
        "code link image | bold italic underline superscript subscript mathslate | outdent indent bullist numlist | preview"
      ],
      style_formats_merge: true,
      style_formats: [
        {title: 'Code highlight', block: 'pre', classes: 'prettyprint'},
        {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
        {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
        {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
        {title: 'Shadow-box-week', block: 'div', classes: 'content-col'},
        {title: 'Default-Panel-element', block: 'div', classes: 'panel'},
        {title: 'Interactive-code-panel', block: 'div', classes: 'code-panel prettyprint'},
        {title: 'Peerreview-border-box', block: 'div', classes: 'peerreview-border-box'},
        {title: 'Peerreview-highlight-text', block: 'p', classes: 'peerreview-highlight'},
        {title: 'Highlight-text-background', block: 'p', classes: 'highlight-text-background'},
        {title: 'icemint', block: 'div', classes: 'icemint'},
        {title: 'red-panel-box', block: 'div', classes: 'panel-red'},
        {title: 'blue-panel-box', block: 'div', classes: 'panel-blue'},
        {title: 'latte-panel-box', block: 'div', classes: 'panel-latte'},
        {title: 'gray-panel-box', block: 'div', classes: 'panel-darker-gray'},
        {title: 'news-panel-box', block: 'div', classes: 'panel-news'},
        {title: 'news-title', block: 'h5', classes: 'news-title-style'},
        {title: 'only-border-box', block: 'div', classes: 'only-border'}
      ]
    });
  });
</script>
