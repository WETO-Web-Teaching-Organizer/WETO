<%@ include file="/WEB-INF/taglibs.jsp"%>
<form action="<s:url action="commitCreateTask" />" name="taskForm" method="post">
  <!-- Hidden fields -->
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />

  <h2><s:text name="editTask.title.add" /></h2>

  <!-- Task name field -->
  <div class="form-group">
    <label for="taskName"><s:text name="general.header.name" /></label>
    <input type="text" name="taskName" placeholder="${taskName}" class="form-control" autofocus />
  </div>

  <!-- Make multiple tasks, and how many -->
  <div class="form-group">
    <label for="multipleTasks"><input type="checkbox" name="multipleTasks" value="true" onchange="enable_slider(this.checked)" />
      <s:text name="general.header.createMultiple" /></label>
    <input type="text" id="minRange" name="minRange" size="2" disabled="disabled" />
    <input type="text" id="maxRange" name="maxRange" size="2" disabled="disabled" />
    <span id="slider" hidden="hidden"></span>
  </div>

  <!-- Which tabs are visible -->
  <label><s:text name="editTask.header.showTheseTabs" /></label><br />

  <!-- Is forum visible -->
  <div class="form-group pseudo-legend">
    <label><input type="checkbox" name="hasForum" value="true" />
      <s:text name="general.header.forum" /></label>
  </div>

  <!-- Are submissions visible -->
  <fieldset class="form-fieldset">
    <legend>
      <label><input type="checkbox" name="hasSubmissions" value="true" onchange="enable_properties(this.checked)" />
        <s:text name="general.header.submissions" /></label>
    </legend>
    <div class="form-group">
      <s:text name="submissions.header.allowedFilePatterns" /><br />
      <input type="text" name="filePatterns" value="${filePatterns}" disabled="disabled" />
    </div>
    <div class="form-group">
      <s:text name="submissions.header.patternDescriptions" /><br />
      <input type="text" name="patternDescriptions" value="${patternDescriptions}" disabled="disabled" />
    </div>
    <div class="form-group">
      <label><input type="checkbox" name="allowZipping" value="true" disabled="disabled" />
        <s:text name="submissions.header.allowZipping" /></label>
    </div>
    <div class="form-group">
      <s:text name="submissionProperties.header.oldSubmissionLimit" /><br />
      <input type="text" name="oldSubmissionLimit" value="${oldSubmissionLimit}" disabled="disabled" />
    </div>
  </fieldset>

  <!-- Does the task have grades visible -->
  <fieldset class="form-fieldset">
    <legend>
      <label><input type="checkbox" name="hasGrades" value="true" onchange="enable_scores(this.checked)" />
        <s:text name="general.header.grades" /></label>
    </legend>
    <div class="form-group">
      <s:text name="grading.header.minScore" /> - <s:text name="grading.header.maxScore" /><br />
      <input type="text" name="minScore" value="${minScore}" size="2" disabled="disabled" /> -
      <input type="text" name="maxScore" value="${maxScore}" size="2" disabled="disabled" />
    </div>
  </fieldset>

  <!-- Other options -->
  <div class="form-group">
    <label><s:text name="general.header.otherOptions" /></label><br />
    <label><input type="checkbox" name="showTextInParent" value="true" /> <s:text name="editTask.header.showTextInParent" /></label><br />
    <label><input type="checkbox" name="hidden" value="true" /> <s:text name="general.header.hidden" /></label> <br/>
    <label><input type="checkbox" name="publicTask" value="true" /> <s:text name="general.header.public" /></label> <br/>
  </div>

  <!-- Submit button -->
  <div class="form-group">
    <input type="submit" value="<s:text name="general.header.create" />" class="btn btn-primary" />
  </div>
</form>

<script>
  // Functions that enable some fields based on checkbox selections
  function enable_slider(status) {
    status = !status;
    document.taskForm.minRange.disabled = status;
    document.taskForm.maxRange.disabled = status;
    document.getElementById("slider").style.display = status ? "none" : "block";
  }
  function enable_properties(status) {
    status = !status;
    document.taskForm.filePatterns.disabled = status;
    document.taskForm.patternDescriptions.disabled = status;
    document.taskForm.allowZipping.disabled = status;
    document.taskForm.oldSubmissionLimit.disabled = status;
  }
  function enable_scores(status) {
    status = !status;
    document.taskForm.minScore.disabled = status;
    document.taskForm.maxScore.disabled = status;
  }

  // Slider listener
  $(function () {
    $("#slider").slider({
      range: true,
      min: 1,
      max: 10,
      values: [1, 5],
      step: 1,
      slide: function (event, ui) {
        $("#minRange").val(ui.values[0]);
        $("#maxRange").val(ui.values[1]);
      }
    });
    $("#minRange").val($("#slider").slider("values", 0));
    $("#maxRange").val($("#slider").slider("values", 1));
  });

</script>
