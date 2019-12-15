<%@ include file="/WEB-INF/taglibs.jsp"%>
<script src="js/tinymce4/tinymce.min.js"></script>
<s:url action="popupTaskDocuments" var="taskDocumentsUrl" escapeAmp="false">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{#mainTabId}" />
  <s:param name="dbId" value="%{dbId}" />
  <s:param name="showAsThumbnails" value="false" />
</s:url>
<s:url action="popupTaskDocuments" var="taskImagesUrl" escapeAmp="false">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{#mainTabId}" />
  <s:param name="dbId" value="%{dbId}" />
  <s:param name="showAsThumbnails" value="true" />
</s:url>
<!-- List previously existing review instructions -->
<div id="instructionsList">
  <s:iterator var="instruction" value="reviewInstructions">
    <div class="panel panel-default instruction" data-instructionId="${instruction.id}">
      <div class="left">
        <button class="btn btn-primary modifyInstructionButton" onclick="prepareEditInstructionForm(this)">
          <s:text name="general.header.edit" />
        </button>
      </div>
      <div class="right">
        <span class="instructionName">${instruction.name}</span><br />
        <span class="instructionText">${instruction.text}</span><br />
        <span class="points">
          <span class="minPoints">${instruction.minPoints}</span> -
          <span class="maxPoints">${instruction.maxPoints}</span>&nbsp;
          <s:text name="reviewinstructions.header.points" />
        </span>
      </div>
      <div class="closebuttonContainer buttonConfirmContainer">
        <button class="btn btn-xs btn-default modifyInstructionButton" onclick="toggleConfirmBox(this)">
          <span class="glyphicon glyphicon-remove"></span>
        </button>
        <div class="confirmBox confirmBox-left text-center">
          <s:text name="reviewinstructions.form.confirmdelete" />
          <form action="<s:url action="deleteInstruction" />">
            <input type="hidden" name="instructionId" value="${instruction.id}" />
            <input type="hidden" name="taskId" value="${taskId}" />
            <input type="hidden" name="tabId" value="${tabId}" />
            <input type="hidden" name="dbId" value="${dbId}" />
            <input type="submit" class="btn btn-sm btn-danger" value="<s:text name="general.header.delete" />" />
            <button type="button" class="btn btn-sm btn-default" onclick="toggleConfirmBox(this)">
              <s:text name="general.header.cancel" />
            </button>
          </form>
        </div>
      </div>
      <div class="clearer"></div>
    </div>
  </s:iterator>
  <s:if test="!reviewInstructions.isEmpty()">
    <p>
      <button id="toggleReorderingButton" class="btn btn-primary" onclick="toggleReorderingEnabled()">
        <s:text name="reviewinstructions.header.enableReordering" />
      </button>
      <button id="reorderInstructionsButton" class="btn btn-primary" onclick="sendAjaxRequest()" disabled >
        <s:text name="reviewinstructions.header.saveReorder" />
      </button>
    </p>
  </s:if>
  <!-- Div for adding new instruction -->
  <div class="panel panel-default instruction">
    <button class="btn btn-primary" onclick="prepareNewInstructionForm(this)">
      <s:text name="reviewinstructions.header.createnew" />
    </button>
  </div>
</div>

<!-- The template that is used for creating an instruction. -->
<form action="<s:url action="createInstruction" />" id="instruction-create-template" style="display:none" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="taskDocumentsUrl" class="taskDocumentsUrl" value="${taskDocumentsUrl}" />
  <input type="hidden" name="taskImagesUrl" class="taskImagesUrl" value="${taskImagesUrl}" />

  <div class="form-group">
    <label for="newInstructionName"><s:text name="reviewinstructions.form.newSubInstructionName" /></label>
    <input name="newInstructionName" class="form-control newInstructionName"
           placeholder= "e.g &nbsp;&quot;Excercise 1&quot; or &quot;a)&quot;"/>
  </div>

  <div class="form-group">
    <label for="newInstructionText"><s:text name="reviewinstructions.form.newSubInstructionText" /></label>
    <s:textarea name="newInstructionText" class="form-control newInstructionText"/>
  </div>

  <div class="form-group">
    <label for="newMinPoints"><s:text name="reviewinstructions.form.minPoints" /></label><br />
    <input type="number" name="newMinPoints" class="newMinPoints" step="0.25" onchange="adjustMinToThis(this)" />
  </div>

  <div class="form-group">
    <label for="newMaxPoints"><s:text name="reviewinstructions.form.maxPoints" /></label><br />
    <input type="number" name="newMaxPoints" class="newMaxPoints" step="0.25" onchange="adjustMaxToThis(this)" />
  </div>

  <input type="submit" class="btn btn-primary" value=<s:text name="general.header.create" /> />
  <button type="button" class="btn btn-default text-right" onclick="closeCreateForm(this)"><s:text name="general.header.cancel" /></button>
</form>

<!-- Template for editing an instruction. -->
<form action="<s:url action="updateInstruction" />" id="instruction-edit-template" style="display:none" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="instructionId" />
  <div class="form-group">
    <label for="newInstructionName"><s:text name="reviewinstructions.form.newSubInstructionName" /></label>
    <input name="newInstructionName" class="form-control newInstructionName" />
  </div>

  <div class="form-group">
    <label for="newInstructionText"><s:text name="reviewinstructions.form.newSubInstructionText" /></label>
    <s:textarea name="newInstructionText" class="form-control newInstructionText"  />
  </div>

  <div class="form-group">
    <label for="newMinPoints"><s:text name="reviewinstructions.form.minPoints" /></label><br />
    <input type="number" name="newMinPoints" class="newMinPoints" step="0.25" onchange="adjustMinToThis(this)" />
  </div>

  <div class="form-group">
    <label for="newMaxPoints"><s:text name="reviewinstructions.form.maxPoints" /></label><br />
    <input type="number" name="newMaxPoints" class="newMaxPoints" step="0.25" onchange="adjustMaxToThis(this)" />
  </div>

  <input type="submit" class="btn btn-primary" value=<s:text name="general.header.save" /> />
  <button type="button" class="btn btn-default text-right" onclick="closeEditForm(this)"><s:text name="general.header.cancel" /></button>
</form>

<!-- Template for viewing an instruction -->
<div id="instruction-view-template" style="display:none">
  <div class="left">
    <button class="btn btn-primary" onclick="prepareEditInstructionForm(this)">
      <s:text name="general.header.edit" />
    </button>
  </div>
  <div class="right">
    <span class="instructionName"></span><br />
    <span class="instructionText"></span><br />
    <span class="points">
      <span class="minPoints"></span> -
      <span class="maxPoints"></span>&nbsp;
      <s:text name="reviewinstructions.header.points" />
    </span>
  </div>
  <div class="closebuttonContainer buttonConfirmContainer">
    <button class="btn btn-xs btn-default" onclick="toggleConfirmBox(this)">
      <span class="glyphicon glyphicon-remove"></span>
    </button>
    <div class="confirmBox confirmBox-left text-center">
      <s:text name="reviewinstructions.form.confirmdelete" />
      <form action="<s:url action="deleteInstruction" />">
        <input type="hidden" name="instructionId"/>
        <input type="hidden" name="taskId" value="${taskId}" />
        <input type="hidden" name="tabId" value="${tabId}" />
        <input type="hidden" name="dbId" value="${dbId}" />
        <input type="submit" class="btn btn-sm btn-danger" value="<s:text name="general.header.delete" />" />
        <button type="button" class="btn btn-sm btn-default" onclick="toggleConfirmBox(this)">
          <s:text name="general.header.cancel" />
        </button>
      </form>
    </div>
  </div>
  <div class="clearer"></div>
</div>
<!-- Template for new create instruction button -->
<button class="btn btn-primary" id="create-button-template" style="display:none" onclick="prepareNewInstructionForm(this)">
  <s:text name="reviewinstructions.header.createnew" />
</button>
<script><%@ include file="/js/reviewinstructions.js"%></script>