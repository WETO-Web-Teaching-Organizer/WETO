<%@page import="fi.uta.cs.weto.db.Scoring"%>
<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:set var="nameUnspecified" value="getText('general.header.unspecified')" />
<s:set var="nameValid" value="getText('general.header.valid')" />
<s:set var="nameVoid" value="getText('general.header.void')" />
<s:set var="nameNull" value="getText('general.header.null')" />
<s:set var="nameHidden" value="getText('general.header.hidden')" />
<s:set var="nameAggregate" value="getText('general.header.aggregate')" />
<s:set var="nameChallenged" value="getText('general.header.challenged')" />
<%
  String[] statusColors =
  {
    "yellow", "green", "red", "white", "grey", "white", "violet"
  };
  String[] statusNames =
  {
    (String) pageContext.getAttribute("nameUnspecified"),
    (String) pageContext.getAttribute("nameValid"),
    (String) pageContext.getAttribute("nameVoid"),
    (String) pageContext.getAttribute("nameNull"),
    (String) pageContext.getAttribute("nameHidden"),
    (String) pageContext.getAttribute("nameAggregate"),
    (String) pageContext.getAttribute("nameChallenged")
  };
  pageContext.setAttribute("statusColors", statusColors);
  pageContext.setAttribute("statusNames", statusNames);
%>
<s:set var="challengeGradeTXT" value="getText('grading.header.challengeGrade')" />
<s:set var="discussGradeTXT" value="getText('grading.header.discussGrade')" />
<s:set var="acceptGradeTXT" value="getText('grading.header.acceptGrade')" />
<script src="js/tinymce4/tinymce.min.js"></script>
<script src="js/grading.js"></script>
<h2><s:text name="grading.header.editGrade" /></h2>
<%-- GRADE EDITOR  --%>
<form action="<s:url action="saveReviewAndGrade" />" method="post">
  <input type="hidden" name="taskId" value="${taskId}" />
  <input type="hidden" name="tabId" value="${tabId}" />
  <input type="hidden" name="dbId" value="${dbId}" />
  <input type="hidden" name="gradeId" value="${grade.id}" />
  <s:if test="submissionId != null">
    <input type="hidden" name="submissionId" value="${submissionId}" />
  </s:if>

  <!-- Receiver name and possibly link to them -->
  <div class="form-group">
    <label><s:text name="general.header.receiver" /></label>
    <br />
    <s:if test="receiver == null">
      <s:text name="general.header.receiver" />
    </s:if>
    <s:elseif test="navigator.teacher">
      <s:url action="viewStudent" var="viewStudentUrl">
        <s:param name="taskId" value="%{taskId}" />
        <s:param name="tabId" value="%{tabId}" />
        <s:param name="dbId" value="%{dbId}" />
        <s:param name="studentId" value="%{receiver.userId}" />
      </s:url>
      <s:a href="%{viewStudentUrl}" anchor="%{receiver.userId }">
        ${receiver.lastName}, ${receiver.firstName}
      </s:a>
    </s:elseif>
    <s:else>
      ${receiver.lastName}, ${receiver.firstName}
    </s:else>
  </div>

  <!-- Reviewer name and possibly a link to them -->
  <div class="form-group">
    <label><s:text name="general.header.reviewer" /></label>
    <br />
    <s:if test="reviewer == null">
      <s:text name="general.header.anonymous" />
    </s:if>
    <s:elseif test="navigator.teacher">
      <s:url action="viewStudent" var="viewStudentUrl">
        <s:param name="taskId" value="%{taskId}" />
        <s:param name="tabId" value="%{tabId}" />
        <s:param name="dbId" value="%{dbId}" />
        <s:param name="studentId" value="%{reviewer.userId}" />
      </s:url>
      <s:a href="%{viewStudentUrl}" anchor="%{reviewer.userId }">
        ${reviewer.lastName}, ${reviewer.firstName}
      </s:a>
    </s:elseif>
    <s:else>
      ${reviewer.lastName}, ${reviewer.firstName}
    </s:else>
  </div>

  <!-- Link to latest submission and list of files related to it -->
  <s:if test="submission != null">
    <div class="form-group">
      <s:url action="viewSubmission" var="viewSubmissionUrl">
        <s:param name="taskId" value="taskId" />
        <s:param name="tabId" value="submissionsTabId" />
        <s:param name="dbId" value="dbId" />
        <s:param name="submissionId" value="submission.id" />
      </s:url>
      <label>
        <s:text name="submissions.header.latestSubmission" />
      </label>
      <span class="gray-text">(<s:text name="general.header.lastModified" /> ${submission.timeStampString})</span>
      <br />
      <s:a href="%{viewSubmissionUrl}" title="View latest submission" data-toggle="tooltip" data-placement="left">
        <s:text name="general.header.viewSubmission" />
      </s:a>
    </div>
    <!-- Files -->
    <div class="form-group">
      <table class="table table-striped table-bordered table-condensed file-table">
        <caption><s:text name = "grading.title.revieweesFiles" /></caption>
        <thead>
        <th><s:text name="general.header.fileName" /></th>
        <th><s:text name="general.header.fileSize" /></th>
        </thead>
        <s:iterator var="document" value="documents">
          <tr>
            <td>
              <s:url action="downloadDocument" var="downloadDocumentURL">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="documentId" value="#document.id" />
              </s:url>
              <s:a href="%{downloadDocumentURL}" target="_blank">
                ${document.fileName}
              </s:a>
            </td>
            <td>
              ${document.contentFileSize}
            </td>
          </tr>
        </s:iterator>
      </table>
    </div>
  </s:if>
  <s:else>
    <div class="alert alert-warning">
      <s:text name="grading.header.noSubmissionsForReviewee" />
    </div>
  </s:else>
  <table class="table">
    <thead>
      <tr>
        <th>
          <label for="mark">
            <s:text name="general.header.mark" />
          </label>
        </th>
        <s:if test="navigator.teacher">
          <th>
            <label for="status"><s:text name="general.header.status" />
            </label>
          </th>
        </s:if>
        <th>
          <label>
            <s:text name="general.header.date" />
          </label>
        </th>
        <th>
          <s:text name="general.header.actions" />
        </th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>
          <s:if test="hasReviewInstructions">
            <s:set var="saveButtonText"><s:text name="grading.header.saveStatus" /></s:set>
            <s:if test="grade.mark != null">
              <input type="hidden" name="mark" value="${grade.mark}" id="markField"/>
              ${grade.mark}
            </s:if>
            <s:else>
              <input type="hidden" name="mark" value="" id="markField"/>
              <s:text name="general.header.unspecified" />
            </s:else>
          </s:if>
          <s:else>
            <s:set var="saveButtonText"><s:text name="grading.header.saveGrade" /></s:set>
            <s:if test="grade.mark != null">
              <input type="number" name="mark" step="${scoring.getProperty("scoreStep", "1")}"
                     min="${scoring.getProperty("minScore", "0")}" max="${scoring.getProperty("maxScore", "0")}"
                     value="${grade.mark}" id="markField" />
            </s:if>
            <s:else>
              <input type="number" name="mark" step="${scoring.getProperty("scoreStep", "1")}"
                     min="${scoring.getProperty("minScore", "0")}" max="${scoring.getProperty("maxScore", "0")}"
                     value="" id="markField" />
            </s:else>
          </s:else>
        </td>
        <s:if test="navigator.teacher">
          <td>
            <s:select list="statusList" name="status" value="%{grade.status}"/>
          </td>
        </s:if>
        <td>
          ${grade.timeStampString}
        </td>
        <td>
          <s:if test="!hasReviewInstructions || navigator.teacher">
            <input type="submit" value="${saveButtonText}" class="btn btn-primary" />
          </s:if>
          <s:if test="navigator.teacher">
            <s:if test="grade.mark != null">
              <input type="submit" value="<s:text name="grading.header.resetGrade" />" class="btn btn-primary" onclick="document.getElementById('markField').value = ''" />
            </s:if>
            <s:url action="deleteGrade" var="deleteGradeURL">
              <s:param name="taskId" value="%{taskId}" />
              <s:param name="tabId" value="%{tabId}" />
              <s:param name="dbId" value="%{dbId}" />
              <s:param name="gradeId" value="grade.id" />
            </s:url>
            <s:a href="%{deleteGradeURL}" cssClass="btn btn-danger">
              <s:text name="grading.header.deleteGrade" />
            </s:a>
          </s:if>
          <s:if test="(challengePeriodActive || navigator.teacher) && (grade.status == 6)">
            <s:url action="discussGrade" var="discussGradeUrl">
              <s:param name="taskId" value="%{taskId}" />
              <s:param name="tabId" value="%{tabId}" />
              <s:param name="dbId" value="%{dbId}" />
              <s:param name="gradeId" value="%{grade.id}" />
            </s:url>
            <s:a href="%{discussGradeUrl}" cssClass="btn btn-primary" cssStyle="float: right">
              ${discussGradeTXT}
            </s:a>
          </s:if>
        </td>
      </tr>
    </tbody>
  </table>
</form>
<%-- REVIEWS --%>
<div id='editgrade-reviews'>
  <s:url action="createReview" var="createReviewUrl">
    <s:param name="taskId" value="%{taskId}" />
    <s:param name="tabId" value="%{tabId}" />
    <s:param name="dbId" value="%{dbId}" />
    <s:param name="gradeId" value="%{grade.id}" />
    <s:param name="submissionId" value="submissionId" />
  </s:url>
  <form action="${createReviewUrl}" method="POST">
    <input type="submit" class="btn btn-primary" value="<s:text name='grading.header.addReview' />" />
  </form>
  <s:iterator var="review" value="CSVReviews" status="loop">
    <div class="review">
      <div class="name-and-delete">
        <!-- Reviewer's Name -->
        <h4>
          ${reviewersMap[review.id]}
        </h4>
        <!-- Review deletion button -->
        <s:if test="navigator.teacher">
          <div class="closebuttonContainer buttonConfirmContainer">
            <input type="button" class="review-delete-button btn btn-danger" onclick="toggleConfirmBox(this)"
                   value="<s:text name="grading.header.deleteReview" />" />
            <div class="confirmBox confirmBox-left text-center">
              <s:text name="grading.header.confirmDeleteReview" />
              <form action="<s:url action="deleteReview" />">
                <input type="hidden" name="taskId" value="${taskId}" />
                <input type="hidden" name="tabId" value="${tabId}" />
                <input type="hidden" name="dbId" value="${dbId}" />
                <input type="hidden" name="tagId" value="${review.id}" />
                <input type="hidden" name="gradeId" value="${grade.id}" />
                <s:if test="submissionId != null">
                  <input type="hidden" name="submissionId" value="${submissionId}" />
                </s:if>
                <input type="submit" class="btn btn-sm btn-danger" value="<s:text name="grading.header.deleteReview" />" />
                <button type="button" class="btn btn-sm btn-default" onclick="toggleConfirmBox(this)">
                  <s:text name="general.header.cancel" />
                </button>
              </form>
            </div>
          </div>
        </s:if>
      </div>
      <!-- Print multipart reviews and forms -->
      <s:if test="hasReviewInstructions">
        <!-- Review is not empty => print it in editable form -->
        <s:if test="!#review.empty">
          <form action='<s:url action="saveReviewAndGrade" />' method="POST">
            <input type="hidden" value="${grade.id}" name="gradeId"/>
            <input type="hidden" value="${taskId}" name="taskId" />
            <input type="hidden" value="${dbId}" name="dbId" />
            <input type="hidden" value="${tabId}" name="tabId" />
            <input type="hidden" value="${review.id}" name="tagId" />
            <h5><s:text name="peerReview.header.currentReview" /></h5>
            <!-- Review in parts -->
            <div class="multipart-review">
              <s:iterator var="instruction" value="reviewInstructions" status="loop">
                <div class="multipart-review-label">
                  ${instruction.name}
                </div>
                <div class="multipart-review-content">
                  <div class="panel-instruction" style="display: none">
                    ${instruction.text}
                  </div>
                  <div class="form-group left">
                    <label for='tagText'><s:text name="peerReview.header.reviewPerQuestion" /></label>
                    <input type="hidden" name="tagTexts" value="" />
                    <div class="reviewText startDisabled panel">${review.texts[loop.index]}</div>
                  </div>
                  <s:if test="#instruction.minPoints < #instruction.maxPoints">
                    <div class="form-group right">
                      <label for="mark"><s:text name="peerReview.header.points" /><span class="points-label"> (${instruction.minPoints} - ${instruction.maxPoints})</span></label>
                      <input type="number" class="form-control" name="marks" step="${scoring.getProperty("scoreStep", "1")}" disabled
                             min="${reviewInstructions[loop.index].minPoints}" max="${reviewInstructions[loop.index].maxPoints}"
                             value="${review.marks[loop.index]}" />
                    </div>
                  </s:if>
                  <s:else>
                    <input type="hidden" name="marks" value="" />
                  </s:else>
                </div>
              </s:iterator>
              <!-- Enable editing if grading period is still active-->
              <s:if test="gradingPeriodActive">
                <button type="button" class="btn btn-default edit-button" onclick="toggleFormEditable(this)">
                  <s:text name="general.header.edit" />
                </button>
                <!-- Hidden buttons to be shown when the form is being edited -->
                <button type="submit" class="btn btn-primary" style="display: none" >
                  <s:text name="grading.header.saveReviewAndGrade" />
                </button>
                <button type="button" class="btn btn-default" style="display: none" onclick="cancelFormEditable(this)">
                  <s:text name="general.header.cancel" />
                </button>
              </s:if>
            </div>
          </form>
        </s:if>
        <!-- Review is empty => print "Write new review" form -->
        <s:else>
          <!-- Form for creating new reviews -->
          <s:if test="gradingPeriodActive">
            <div class="multipart-review">
              <div class="alert alert-warning">
                <s:text name='grading.header.reviewIsEmpty' />
              </div>
              <form action="<s:url action="saveReviewAndGrade" />" method="POST">
                <h5><s:text name="peerReview.header.createNewReview" /></h5>
                <input type="hidden" value="${grade.id}" name="gradeId"/>
                <input type="hidden" value="${taskId}" name="taskId" />
                <input type="hidden" value="${dbId}" name="dbId" />
                <input type="hidden" value="${tabId}" name="tabId" />
                <input type="hidden" value="${review.id}" name="tagId" />
                <input type="hidden" value="" name="mark" id="markCopyField_${review.id}"/>
                <!-- Multipart review form with review instructions -->
                <s:iterator var="instruction" value="reviewInstructions" status="loop">
                  <label class="multipart-review-label">${instruction.name}</label>
                  <div class="multipart-review-content">
                    <div class="panel-instruction">
                      ${instruction.text}
                    </div>
                    <div class="left form-group">
                      <label><s:text name="peerReview.header.reviewPerQuestion" /></label>
                      <input type="hidden" name="tagTexts" value="" />
                      <div class="reviewText startEnabled panel"></div>
                    </div>
                    <s:if test="#instruction.minPoints < #instruction.maxPoints">
                      <div class="right form-group">
                        <label for="marks"><s:text name="peerReview.header.points" /><span class="points-label"> (${instruction.minPoints} - ${instruction.maxPoints})</span></label>
                        <input type="number" class="form-control multipart-review-points" name="marks"
                               max="${instruction.maxPoints}" min="${instruction.minPoints}" value="0" step="${scoring.getProperty("scoreStep", "1")}" />
                      </div>
                    </s:if>
                    <s:else>
                      <input type="hidden" name="marks" value="" />
                    </s:else>
                  </div>
                </s:iterator>
                <input type="submit" class="btn btn-primary" value="<s:text name="grading.header.saveReviewAndGrade" />" />
              </form>
            </div>
          </s:if>
        </s:else>
      </s:if>
      <!-- Print single part forms -->
      <s:else>
        <!-- Show previously written review -->
        <s:if test="! #review.empty">
          <div class="singlepart-review">
            <form action="<s:url action="saveReviewAndGrade" />" method="POST">
              <h5><s:text name="peerReview.header.currentReview" /></h5>
              <input type="hidden" value="${grade.id}" name="gradeId"/>
              <input type="hidden" value="${taskId}" name="taskId" />
              <input type="hidden" value="${dbId}" name="dbId" />
              <input type="hidden" value="${tabId}" name="tabId" />
              <input type="hidden" value="${review.id}" name="tagId" />
              <input type="hidden" value="" name="mark" id="markCopyField_${review.id}"/>
              <div class="form-group">
                <label for="tagText"><s:text name="general.header.text" /></label>
                <input type="hidden" name="tagText" value="" />
                <div class="reviewText startDisabled panel">${review.allTexts}</div>
              </div>
              <div class="singlepart-review-buttons">
                <!-- Enable editing if grading period is still active-->
                <s:if test="gradingPeriodActive">
                  <button type="button" class="btn btn-default edit-button" onclick="toggleFormEditable(this)">
                    <s:text name="general.header.edit" />
                  </button>
                  <!-- Hidden buttons to be shown when the form is being edited -->
                  <button type="submit" class="btn btn-primary" style="display: none" onclick="document.getElementById('markCopyField_${review.id}').value = document.getElementById('markField').value">
                    <s:text name="grading.header.saveReviewAndGrade" />
                  </button>
                  <button type="button" class="btn btn-default" style="display: none" onclick="cancelFormEditable(this)">
                    <s:text name="general.header.cancel" />
                  </button>
                </s:if>
              </div>
            </form>
          </div>
        </s:if>
        <!-- If review is not written, show reminder -->
        <s:else>
          <div class="alert alert-warning">
            <s:text name='grading.header.reviewIsEmpty' />
          </div>
          <!-- Form for creating a new review -->
          <s:if test="gradingPeriodActive">
            <h5><s:text name="peerReview.header.createNewReview" /></h5>
            <div class="singlepart-review">
              <form action="<s:url action="saveReviewAndGrade" />" method="POST">
                <input type="hidden" value="${grade.id}" name="gradeId"/>
                <input type="hidden" value="${taskId}" name="taskId" />
                <input type="hidden" value="${dbId}" name="dbId" />
                <input type="hidden" value="${tabId}" name="tabId" />
                <input type="hidden" value="${review.id}" name="tagId" />
                <input type="hidden" value="" name="mark" id="markCopyField_${review.id}"/>
                <div class="form-group">
                  <label for="tagText"><s:text name="general.header.text" /></label>
                  <input type="hidden" name="tagText" value="" />
                  <div class="reviewText startEnabled panel"></div>
                </div>
                <div class="singlepart-review-buttons">
                  <button type="submit" class="btn btn-primary" onclick="document.getElementById('markCopyField_${review.id}').value = document.getElementById('markField').value">
                    <s:text name="grading.header.saveReviewAndGrade" />
                  </button>
                </div>
              </form>
            </div>
          </s:if>
        </s:else>
      </s:else>
    </div>
  </s:iterator>
</div>
<s:url action="relogin" var="reloginUrl" escapeAmp="false" />
<div id="dummyDiv" style="display: none;"></div>
<script>
  function pollLogin()
  {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "pollLogin", false);
    xhr.send(null);
    if (xhr.status == 204) {
      return true;
    } else if (xhr.status == 403)
    {
      reloginPopup(tinymce.get('dummyDiv'));
    } else
    {
      $('#infoBar').empty();
      $('#infoBar').append(
              '<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="accessDenied.systemError" /></span></li></ul></div></div>'
              );
      $('.infoBar').show();
      timeoutobject = setTimeout(function ()
      {
        jQuery(".infoBar").fadeOut(300);
      }, 7000);
    }
    return false;
  }

  $(function () {
    // Copy text from editor to the form input field upon submit.
    // Also poll login status.
    $("form").submit(function () {
      $(this).find('.reviewText').each(function ()
      {
        var revText = tinymce.get(this.id).getContent().trim();
        $(this).prev("input").val(revText);
      });
      return pollLogin();
    });
    tinymce.init({
      selector: "div.reviewText.startDisabled",
      readonly: 1,
      inline: "true",
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      menubar: false,
      toolbar: [
        "bold italic underline superscript subscript forecolor | mathslate emoticons | outdent indent bullist numlist | link image table"
      ]
    });
    tinymce.init({
      selector: "div.reviewText.startEnabled",
      inline: "true",
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      menubar: false,
      toolbar: [
        "bold italic underline superscript subscript forecolor | mathslate emoticons | outdent indent bullist numlist | link image table"
      ]
    });
    tinymce.init({
      selector: "#dummyDiv",
      inline: "true",
      menubar: false
    });
  });
</script>