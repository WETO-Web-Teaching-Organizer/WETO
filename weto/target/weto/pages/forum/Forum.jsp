<%@ include file="/WEB-INF/init.jsp"%>
<h2><s:text name="forum.header.topics" /></h2>
<s:url action="viewForumTopic" var="viewTopicUrl">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{tabId}" />
  <s:param name="dbId" value="%{dbId}" />
</s:url>
<s:if test="topicBeans.isEmpty" >
  <p><s:text name="forum.instructions" /></p>
</s:if>
<s:else>
  <s:iterator value="topicBeans" var="topic">
    <a href="${viewTopicUrl}&topicId=${topic.id}">
      <div class="forumTopicTitle">
        <s:if test="!#topic.title.empty">
          ${topic.title}
        </s:if>
        <s:else>
          <s:text name="general.header.none" />
        </s:else>
        <div class="topicUser">
          ${topic.author}, ${topic.date}
        </div>
      </div>
    </a>
  </s:iterator>
</s:else>
<s:if test="canAddTopic">
  <hr>
  <form action="<s:url action="addForumTopic" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <h4><s:text name="forum.header.addNewTopic" /></h4>
    <s:text name="general.header.title" />: <input type="text" name="topicTitle" size="50" />
    <s:textarea name="topicText" id="topicText" cols="100" rows="10" />
    <input type="submit" value="<s:text name="general.header.submit" />" class="linkButton" />
  </form>
  <script>
    $(function () {
      tinymce.init({
        selector: "#topicText",
        entity_encoding: "raw",
        plugins: [
          "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
          "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
          "save table contextmenu directionality emoticons template paste textcolor mathslate"
        ],
        menubar: false,
        toolbar: [
          "undo redo | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
          "bold italic underline superscript subscript mathslate | outdent indent bullist numlist | link image | preview code"
        ]
      });
    });
  </script>
</s:if>
