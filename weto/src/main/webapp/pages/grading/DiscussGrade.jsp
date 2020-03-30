<%@ include file="/WEB-INF/init.jsp"%>
<s:url action="viewTask" var="viewGradingUrl">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{gradingTabId}" />
  <s:param name="dbId" value="%{dbId}" />
</s:url>
<s:a href="%{viewGradingUrl}" class="btn btn-primary-small"><s:text name="grading.header.returnToGrading" /></s:a>
<h2><s:text name="tag.header.gradeDiscussion" /></h2>
<s:if test="messageBeans.size() > 0">
  <s:iterator value="messageBeans" var="message">
    <div class="forumReply">
      <div class="replyUser">
        ${message.author} (${message.date}):
        <s:if test="challengePeriodActive && (courseUserId == #message.authorId)">
          <s:url action="editGradeDiscussionMessage" var="editGradeDiscussionMessageUrl">
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{tabId}" />
            <s:param name="dbId" value="%{dbId}" />
            <s:param name="gradeId" value="%{gradeId}" />
            <s:param name="messageId" value="%{#message.id}" />
          </s:url>
          <s:a href="%{editGradeDiscussionMessageUrl}" cssClass="smallLinkButton" cssStyle="float: right;">
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
<s:if test="challengePeriodActive">
  <hr>
  <form action="<s:url action="addGradeDiscussionMessage" />" method="post">
    <input type="hidden" name="taskId" value="${taskId}" />
    <input type="hidden" name="tabId" value="${tabId}" />
    <input type="hidden" name="dbId" value="${dbId}" />
    <input type="hidden" name="gradeId" value="${gradeId}" />
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