<%@ include file="/WEB-INF/init.jsp"%>
<s:url action="viewForum" var="viewForumUrl">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{tabId}" />
  <s:param name="dbId" value="%{dbId}" />
</s:url>
<s:a href="%{viewForumUrl}" class="btn btn-primary-small"><s:text name="forum.header.backToTopics" /></s:a>
  <div class="forumTopic">
    <div class="replyUser">
    ${messageBeans[0].author} (${messageBeans[0].date}):
  </div>
  <div class="replyText">
    <h4>${topicTitle}</h4>
    <p>
      ${messageBeans[0].text}
    </p>
  </div>
</div>
<s:if test="messageBeans.size() > 1">
  <s:iterator value="messageBeans" var="message" begin="1">
    <div class="forumReply">
      <div class="replyUser">
        ${message.author} (${message.date}):
        <s:if test="courseUserId == #message.authorId">
          <s:url action="editForumMessage" var="editForumMessageUrl">
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{tabId}" />
            <s:param name="dbId" value="%{dbId}" />
            <s:param name="topicId" value="%{topicId}" />
            <s:param name="messageId" value="%{#message.id}" />
          </s:url>
          <s:a href="%{editForumMessageUrl}" cssClass="smallLinkButton" cssStyle="float: right;">
            <s:text name="general.header.edit" />
          </s:a>
        </s:if>
      </div>
      <div class="replyText">
        ${message.text}
      </div>
    </div>
  </s:iterator>
</s:if>
<s:else>
  <p><s:text name="forum.header.noReplies" /></p>
</s:else>
<%--Topic subscription for receiving notifications.--%>
<form action="<s:url action="saveTopicSubscription" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="topicId" value="${topicId}" />
  <s:if test="%{!topicSubscribed}">
    <input type="hidden" name="topicSubscription" value="true"/>
    <input type="submit" value="<s:text name="forum.header.subscribeTopic" />" class="linkButton" />
  </s:if>
  <s:else>
    <input type="hidden" name="topicSubscription" value="false"/>
    <input type="submit" value="<s:text name="forum.header.unsubscribeTopic" />" class="linkButton" />
  </s:else>
</form>
<s:if test="canAddReply">
  <hr>
  <form action="<s:url action="addForumMessage" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="topicId" value="${topicId}" />
    <h4><s:text name="forum.header.addNewMessage" /></h4>
    <s:textarea name="messageText" id="messageText" cols="80" rows="10" />
    <input type="submit" value="<s:text name="general.header.submit" />" class="btn btn-primary" />
  </form>
  <script>
    $(function () {
      tinymce.init({
        selector: "#messageText",
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