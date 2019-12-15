<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
  <h2><s:text name="news.header.currentNews" /></h2>
  <p>
    <s:url action="editNews" var="addNewsUrl" />
    <s:a href="%{addNewsUrl}" cssClass="linkButton">
      <s:text name="news.header.addNews" />
    </s:a>
  </p>
  <s:if test="news != null && news.isEmpty()">
    <p><s:text name="news.header.noNews" /></p>
  </s:if>
  <s:else>
    <table class="tablesorter" id="newsTable">
      <thead>
        <tr>
          <th><s:text name="news.header.newsTitle" /></th>
          <th><s:text name="news.header.showFrom" /></th>
          <th><s:text name="news.header.showUntil" /></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <s:iterator var="newsItem" value="news">
          <tr>
            <td>${newsItem.title}</td>
            <td>
              ${newsItem.startDateString}
            </td>
            <td>
              ${newsItem.endDateString}
            </td>
            <td>
              <s:url action="editNews" var="editNewsUrl">
                <s:param name="newsId" value="%{#newsItem.id}" />
              </s:url>
              <s:a href="%{editNewsUrl}" cssClass="linkButton">
                <s:text name="general.header.edit" />
              </s:a>
            </td>
          </tr>
        </s:iterator>
      </tbody>
    </table>
    <script>
      $(function () {
        $("#newsTable").tablesorter({widgets: ['zebra'], headers: {
            // assign the secound column (we start counting zero)
            3: {
              // disable it by setting the property sorter to false
              sorter: false
            }
          }});
      });
    </script>
  </s:else>
</div>
