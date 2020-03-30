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
<div class="content-wrapper">
  <div class="header_field">
    <h2 class="grading_header-text">
      <s:text name="grading.title.grades" />
    </h2>
    <div class= "help-sign">
      <span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="right" data-original-title="Help"></span>
    </div>
  </div>
  <div class="help-field panel">
    <s:text name="grading.header.helpText"/>
  </div>
  <s:if test="!gradeTable.isEmpty()">
    <h4>
      <s:text name="grading.title.gradeTable" />
    </h4>
    <table class="dataTable">
      <tr>
        <th>
          <s:text name="general.header.score" />
        </th>
        <s:iterator var="entry" value="gradeTable">
          <td>${entry[0]}</td>
        </s:iterator>
      </tr>
      <tr>
        <th>
          <s:text name="general.header.mark" />
        </th>
        <s:iterator var="entry" value="gradeTable">
          <td>
            <s:if test="#entry[1] != null && !entry[1].isEmpty()">
              ${entry[1]}
            </s:if>
            <s:else>
              <s:text name="grading.header.failed"/>
            </s:else>
          </td>
        </s:iterator>
      </tr>
    </table>
  </s:if>
  <!-- PRINT GRADES AND REVIEWS GIVEN BY SELF AND FORMS FOR CREATING THEM -->
  <s:if test="!givenGrades.isEmpty()">
    <div class ="content-col">
      <h3>
        <s:text name="grading.title.reviewsGivenByYou" />
      </h3>
      <s:iterator var="grade" value="givenGrades">
        <s:if test="challengePeriodActive && (#grade.status == 6)">
          <s:url action="discussGrade" var="discussGradeUrl">
            <s:param name="taskId" value="%{taskId}" />
            <s:param name="tabId" value="%{tabId}" />
            <s:param name="dbId" value="%{dbId}" />
            <s:param name="gradeId" value="%{#grade.id}" />
          </s:url>
        </s:if>
        <div class="tehtava">
          <!-- The row that has aggregate information and the collapse link to one review -->
          <div class="otsikko_ja_pisteet row">
            <!-- Link that opens the collapsed element -->
            <div class='collapselinkki' data-toggle="collapse" data-target ="#own-${grade.id}">
              <div class="col-sm-4">
                <h4>
                  <span title ="Click to collapse" class="glyphicon glyphicon-menu-right col-logo"></span>
                  <s:if test="visibleMembersMap[#grade.receiverId] == null">
                    <s:text name="general.header.anonymous" />
                  </s:if>
                  <s:else>
                    ${visibleMembersMap[grade.receiverId].lastName},
                    ${visibleMembersMap[grade.receiverId].firstName}
                  </s:else>
                </h4>
              </div>
              <!-- Timestamp of given grade -->
              <div class ="grading-timestamp col-sm-4">
                <h4>
                  ${grade.timeStampString}
                </h4>
              </div>
              <!-- Show grade or 'unspecified', and the correct symbol -->
              <s:if test="#grade.mark != null">
                <div class="pisteet col-sm-4">
                  <h4 class="${statusColors[grade.status]}">
                    ${grade.mark} &nbsp;  ${statusNames[grade.status]}
                    <span class="glyphicon glyphicon-ok">
                      <s:if test="challengePeriodActive && (#grade.status == 6)">
                        <s:a href="%{discussGradeUrl}" cssClass="btn btn-danger-small" onclick="event.stopPropagation()">
                          ${discussGradeTXT}
                        </s:a>
                      </s:if>
                    </span>
                  </h4>
                </div>
              </s:if>
              <s:else>
                <h4 class="${statusColors[grade.status]} col-sm-4">
                  <s:text name="general.header.unspecified" />
                  <span class="glyphicon glyphicon-alert">
                    <s:if test="challengePeriodActive && (#grade.status == 6)">
                      <s:a href="%{discussGradeUrl}" cssClass="btn btn-danger-small" onclick="event.stopPropagation()">
                        ${discussGradeTXT}
                      </s:a>
                    </s:if>
                  </span>
                </h4>
              </s:else>
            </div>
          </div>
          <!-- Collapsing element -->
          <div id="own-${grade.id}" class="collapse review">
            <s:if test="givenReviewDocuments[#grade.id].size > 0">
              <div class="table-responsive">
                <table class="table table-striped table-bordered table-condensed
                       file-table">
                  <caption><s:text name = "grading.title.revieweesFiles" /></caption>
                  <thead>
                    <tr>
                      <th><s:text name = "general.header.files"/></th>
                      <th><s:text name = "general.header.size"/></th>
                      <th><s:text name = "general.header.actions"/></th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator var="document" value="givenReviewDocuments[#grade.id]">
                      <s:url action="downloadDocument" var="downloadDocumentURL">
                        <s:param name="taskId" value="%{taskId}" />
                        <s:param name="tabId" value="%{tabId}" />
                        <s:param name="dbId" value="%{dbId}" />
                        <s:param name="documentId" value="#document.id" />
                      </s:url>
                      <!-- Document filename, size and actions -->
                      <tr>
                        <td>
                          <s:a href="%{downloadDocumentURL}" title="Open">
                            ${document.fileName}
                          </s:a>
                        </td>
                        <td>
                          ${document.contentFileSize}
                        </td>
                        <td>
                          <s:a href="%{downloadDocumentURL}"
                               title = "Download">
                            <span class="glyphicon glyphicon-download-alt"/>
                          </s:a>
                        </td>
                      </tr>
                    </s:iterator>
                  </tbody>
                </table>
              </div>
              <hr />
            </s:if>
            <s:else>
              <div class='alert alert-warning'>
                <s:text name='peerReview.header.noDocuments' />
              </div>
            </s:else>
            <s:if test="hasReviewInstructions">
              <s:if test="givenReviewsMap[#grade.id] == null">
                <s:if test="gradingPeriodActive || (challengePeriodActive && (#grade.status == 6))">
                  <s:if test="gradingPeriodActive">
                    <div class="alert alert-info">
                      (<s:text name='peerReview.header.gradingWillClose'>
                        <s:param>${gradingPeriod[1]}</s:param>
                      </s:text>)
                    </div>
                  </s:if>
                  <s:else>
                    <div class="alert alert-info">
                      (<s:text name='peerReview.header.gradingWillClose'>
                        <s:param>${challengePeriod[1]}</s:param>
                      </s:text>)
                    </s:else>
                    <form action="<s:url action="saveReviewAndGradeReturn" method="POST" />">
                      <input type="hidden" value="${grade.id}" name="gradeId"/>
                      <input type="hidden" value="${taskId}" name="taskId" />
                      <input type="hidden" value="${dbId}" name="dbId" />
                      <input type="hidden" value="${tabId}" name="tabId" />
                      <!-- Print out review instructions -->
                      <s:iterator var="instruction" value="reviewInstructions" status="loop">
                        <div class="multipart-mark">
                          <div class="multipart-mark-label">
                            ${instruction.name}
                          </div>
                          <div class="multipart-mark-content">
                            <div class="panel-instruction">
                              ${instruction.text}
                            </div>
                          </div>
                          <s:if test="#instruction.minPoints < #instruction.maxPoints">
                            <div class="form-group multipart-mark-points">
                              <label for="mark"><s:text name="peerReview.header.points" /><span class="points-label"> (${instruction.minPoints} - ${instruction.maxPoints})</span></label>
                              <input type="number" class="multipart-mark-points-input" step="${scoring.getProperty("scoreStep", "1")}" onchange="updateTotalMark(this)"
                                     min="${instruction.minPoints}" max="${instruction.maxPoints}">
                            </div>
                          </s:if>
                          <s:else>
                            <input type="hidden" name="marks" value="" />
                          </s:else>
                        </div>
                      </s:iterator>
                      <div class="form-group total-mark">
                        <label for="mark"><s:text name="grading.header.totalMark" /><span class="points-label"> (${scoring.getProperty("minScore", "0")} - ${scoring.getProperty("maxScore", "0")})</span></label>
                        <input type="number" class="form-control total-mark" name="mark" value="${grade.mark}" disabled />
                      </div>
                      <input type='submit' class="btn btn-primary" value="<s:text name='grading.header.saveReviewAndGrade' />" />
                    </form>
                  </s:if>
                  <s:else>
                    <div class="alert alert-info">
                      <s:text name="peerReview.header.gradingPeriod"/>
                      <strong>
                        <weto:timePeriod starting="${gradingPeriod[0]}" ending="${gradingPeriod[1]}" />
                      </strong>.
                    </div>
                  </s:else>
                </s:if>
                <s:elseif test="reviewInstructions.size > 0">
                  <!-- Review exists, and it's not empty -->
                  <s:if test="!givenReviewsMap[#grade.id].empty">
                    <h4><s:text name="peerReview.header.currentReview" /></h4>
                    <form action='<s:url action="saveReviewAndGradeReturn" />' method="POST">
                      <input type="hidden" value="${grade.id}" name="gradeId"/>
                      <input type="hidden" value="${taskId}" name="taskId" />
                      <input type="hidden" value="${dbId}" name="dbId" />
                      <input type="hidden" value="${tabId}" name="tabId" />
                      <input type="hidden" value="${givenReviewsMap[grade.id].id}" name="tagId" />
                      <div class="multipart-review">
                        <s:iterator var="instruction" value="reviewInstructions" status="loop">
                          <div class="multipart-review-label">
                            ${instruction.name}
                          </div>
                          <div class="multipart-review-content">
                            <div class='togglable-instruction' style="display: none">
                              <label><s:text name='peerReview.header.reviewInstructionPerQuestion' /></label>
                              <div class="panel-instruction">
                                ${instruction.text}
                              </div>
                            </div>
                            <s:if test="#instruction.minPoints < #instruction.maxPoints">
                              <div class="form-group left">
                                <label><s:text name="peerReview.header.reviewPerQuestion" /></label>
                                <input type="hidden" name="tagTexts" value="" />
                                <div class="reviewText scored startDisabled panel">${givenReviewsMap[grade.id].texts[loop.index]}</div>
                              </div>
                              <div class="form-group right">
                                <label for="mark"><s:text name="peerReview.header.points" /><span class="points-label"> (${instruction.minPoints} - ${instruction.maxPoints})</span></label>
                                <input type="number" class="form-control" name="marks" step="${scoring.getProperty("scoreStep", "1")}" disabled required
                                       min="${reviewInstructions[loop.index].minPoints}" max="${reviewInstructions[loop.index].maxPoints}"
                                       value="${givenReviewsMap[grade.id].marks[loop.index]}" />
                              </div>
                            </s:if>
                            <s:else>
                              <div class="form-group left">
                                <label><s:text name="peerReview.header.generalFeedback" /></label>
                                <input type="hidden" name="tagTexts" value="" />
                                <div class="reviewText startDisabled panel">${givenReviewsMap[grade.id].texts[loop.index]}</div>
                              </div>
                              <input type="hidden" name="marks" value="" />
                            </s:else>
                          </div>
                        </s:iterator>
                      </div>
                      <!-- Enable editing if grading period is still active-->
                      <s:if test="gradingPeriodActive || (challengePeriodActive && (#grade.status == 6))">
                        <button type="button" class="btn btn-default edit-button" onclick="toggleFormEditable(this)">
                          <s:text name="general.header.edit" />
                        </button>
                        <!-- Hidden buttons to be shown when the form is begin edited -->
                        <button type="submit" class="btn btn-primary" style="display: none" >
                          <s:text name="grading.header.saveReviewAndGrade" />
                        </button>
                        <button type="button" class="btn btn-default" style="display: none" onclick="cancelFormEditable(this)">
                          <s:text name="general.header.cancel" />
                        </button>
                      </s:if>
                    </form>
                    <hr />
                  </s:if>
                  <!-- Review is empty: show reminder to write one -->
                  <s:else>
                    <!-- Form for creating new reviews -->
                    <s:if test="gradingPeriodActive || (challengePeriodActive && (#grade.status == 6))">
                      <s:if test="gradingPeriodActive">
                        <div class="alert alert-warning">
                          <s:text name="peerReview.header.empty" /> <strong><weto:endTime ending="${gradingPeriod[1]}" /></strong>
                        </div>
                      </s:if>
                      <s:else>
                        <div class="alert alert-warning">
                          <s:text name="peerReview.header.empty" /> <strong><weto:endTime ending="${challengePeriod[1]}" /></strong>
                        </div>
                      </s:else>
                      <h4><s:text name="peerReview.header.createNewReview" /></h4>
                      <div class="multipart-review">
                        <form action="<s:url action="saveReviewAndGradeReturn" />" method="POST">
                          <input type="hidden" value="${grade.id}" name="gradeId"/>
                          <input type="hidden" value="${taskId}" name="taskId" />
                          <input type="hidden" value="${dbId}" name="dbId" />
                          <input type="hidden" value="${tabId}" name="tabId" />
                          <input type="hidden" value="${givenReviewsMap[grade.id].id}" name="tagId" />
                          <!-- Multipart review form with review instructions -->
                          <s:iterator var="instruction" value="reviewInstructions" status="loop">
                            <label class="multipart-review-label">${instruction.name}</label>
                            <div class="multipart-review-content">
                              <label><s:text name='peerReview.header.reviewInstructionPerQuestion' /></label>
                              <div class="panel-instruction">
                                ${instruction.text}
                              </div>
                              <s:if test="#instruction.minPoints < #instruction.maxPoints">
                                <div class="left form-group">
                                  <label><s:text name="peerReview.header.reviewPerQuestion" /></label>
                                  <input type="hidden" name="tagTexts" value="" />
                                  <div class="reviewText scored startEnabled panel"></div>
                                </div>
                                <div class="right form-group">
                                  <label for="marks"><s:text name="peerReview.header.points" /><span class="points-label"> (${instruction.minPoints} - ${instruction.maxPoints})</span></label>
                                  <input type="number" class="form-control multipart-review-points" name="marks" required
                                         max="${instruction.maxPoints}" min="${instruction.minPoints}" step="${scoring.getProperty("scoreStep", "1")}" />
                                </div>
                              </s:if>
                              <s:else>
                                <div class="left form-group">
                                  <label><s:text name="peerReview.header.generalFeedback" /></label>
                                  <input type="hidden" name="tagTexts" value="" />
                                  <div class="reviewText startEnabled panel"></div>
                                </div>
                                <input type="hidden" name="marks" value="" />
                              </s:else>
                            </div>
                          </s:iterator>
                          <input type="submit" class="btn btn-primary" value="<s:text name="general.header.submit" />" />
                        </form>
                      </div>
                    </s:if>
                    <s:else>
                      <div class="alert alert-info">
                        <s:text name="peerReview.header.emptyClosed" />
                        <strong>
                          <weto:timePeriod starting="${gradingPeriod[0]}" ending="${gradingPeriod[1]}" />
                        </strong>.
                      </div>
                    </s:else>
                  </s:else>
                </s:elseif>
                <!-- Teacher has failed to give any review instructions and they are still in use -->
                <s:else>
                  <div class='alert alert-danger'>
                    <s:text name="peerReview.header.noReviewInstructions"/>
                  </div>
                </s:else>
              </s:if>
              <!-- SHOW PREVIOUSLY WRITTEN REVIEW FOR SINGLE PART FORMAT-->
              <s:else>
                <!-- Show previously written review -->
                <s:if test="givenReviewsMap[#grade.id] != null">
                  <div class="singlepart-review">
                    <form action="<s:url action="saveReviewAndGradeReturn" />" method="POST">
                      <h4><s:text name="peerReview.header.currentReview" /></h4>
                      <input type="hidden" value="${grade.id}" name="gradeId"/>
                      <input type="hidden" value="${taskId}" name="taskId" />
                      <input type="hidden" value="${dbId}" name="dbId" />
                      <input type="hidden" value="${tabId}" name="tabId" />
                      <input type="hidden" value="${givenReviewsMap[grade.id].id}" name="tagId" />
                      <div class="left form-group">
                        <label for="tagText"><s:text name="general.header.text" /></label>
                        <input type="hidden" name="tagText" value="" />
                        <div class="reviewText startDisabled panel">
                          ${givenReviewsMap[grade.id].allTexts}
                        </div>
                      </div>
                      <div class="right form-group">
                        <label for="mark"><s:text name="general.header.grade" /><span class="points-label"> (${scoring.getProperty("minScore", "0")} - ${scoring.getProperty("maxScore", "0")})</span></label>
                        <input type="number" name="mark" disabled="true" min="${scoring.getProperty("minScore", "0")}" max="${scoring.getProperty("maxScore", "0")}" class="form-control" value="${grade.mark}" />
                      </div>
                      <div class="singlepart-review-buttons">
                        <!-- Enable editing if grading period is still active-->
                        <s:if test="gradingPeriodActive || (challengePeriodActive && (#grade.status == 6))">
                          <button type="button" class="btn btn-default edit-button" onclick="toggleFormEditable(this)">
                            <s:text name="general.header.edit" />
                          </button>
                          <!-- Hidden buttons to be shown when the form is being edited -->
                          <button type="submit" class="btn btn-primary" style="display: none" >
                            <s:text name="general.header.save" />
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
                  <s:if test="gradingPeriodActive || (challengePeriodActive && (#grade.status == 6))">
                    <s:if test="gradingPeriodActive">
                      <div class="alert alert-warning">
                        <s:text name="peerReview.header.empty" /> <strong><weto:endTime ending="${gradingPeriod[1]}" /></strong>
                      </div>
                    </s:if>
                    <s:else>
                      <div class="alert alert-warning">
                        <s:text name="peerReview.header.empty" /> <strong><weto:endTime ending="${challengePeriod[1]}" /></strong>
                      </div>
                    </s:else>
                    <h4><s:text name="peerReview.header.createNewReview" /></h4>
                    <!-- Form for creating a new review -->
                    <div class="singlepart-review">
                      <form action="<s:url action="saveReviewAndGradeReturn" />" method="POST">
                        <input type="hidden" value="${grade.id}" name="gradeId"/>
                        <input type="hidden" value="${taskId}" name="taskId" />
                        <input type="hidden" value="${dbId}" name="dbId" />
                        <input type="hidden" value="${tabId}" name="tabId" />
                        <input type="hidden" value="${givenReviewsMap[grade.id].id}" name="tagId" />
                        <div class="left form-group">
                          <label for="tagText"><s:text name="general.header.text" /></label>
                          <input type="hidden" name="tagText" value="" />
                          <div class="reviewText startEnabled panel"></div>
                        </div>
                        <div class="right form-group">
                          <label for="mark"><s:text name="general.header.grade" /><span class="points-label"> (${scoring.getProperty("minScore", "0")} - ${scoring.getProperty("maxScore", "0")})</span></label>
                          <input type="number" name="mark" min="${scoring.getProperty("minScore", "0")}" max="${scoring.getProperty("maxScore", "0")}" class="form-control" />
                        </div>
                        <div class="singlepart-review-buttons">
                          <button type="submit" class="btn btn-primary" >
                            <s:text name="general.header.create" />
                          </button>
                        </div>
                      </form>
                    </div>
                  </s:if>
                  <s:else>
                    <div class="alert alert-info">
                      <s:text name="peerReview.header.emptyClosed" />
                      <strong>
                        <weto:timePeriod starting="${gradingPeriod[0]}" ending="${gradingPeriod[1]}" />
                      </strong>.
                    </div>
                  </s:else>
                </s:else>
              </s:else>
              <!-- Bottom collapse button -->
              <div class="inner-collapse">
                <span title="collapse" class= "collapse-alalinkki glyphicon glyphicon-menu-up"
                      data-toggle="collapse"
                      data-target ="#own-${grade.id}">
                </span>
              </div>
            </div>
          </div>
        </s:iterator>
      </div>
    </s:if>
    <!-- Get submission documents for self -->
    <div class ="content-col">
      <h3><s:text name = "grading.title.reviewsReceivedByYou"/></h3>
      <s:if test="mySubmittedDocuments.size > 0">
        <div class="table-responsive">
          <table class="table table-striped table-bordered table-condensed
                 file-table">
            <caption><s:text name = "general.header.yourfiles" /></caption>
            <thead>
              <tr>
                <th><s:text name = "general.header.file"/></th>
                <th><s:text name = "general.header.size"/></th>
                <th><s:text name = "general.header.actions"/></th>
              </tr>
            </thead>
            <tbody>
              <s:iterator var="document" value="mySubmittedDocuments">
                <s:url action="downloadDocument" var="downloadDocumentURL">
                  <s:param name="taskId" value="%{taskId}" />
                  <s:param name="tabId" value="%{tabId}" />
                  <s:param name="dbId" value="%{dbId}" />
                  <s:param name="documentId" value="#document.id" />
                </s:url>
                <tr>
                  <td>
                    <s:a href="%{downloadDocumentURL}" title="Open">
                      ${document.fileName}
                    </s:a>
                  </td>
                  <td>
                    ${document.contentFileSize}
                  </td>
                  <td>
                    <s:a href="%{downloadDocumentURL}"
                         title = "Download">
                      <span class="glyphicon glyphicon-download-alt"/>
                    </s:a>
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>
      </s:if>
      <%-- No files
      <s:else>
        <div class='alert alert-warning'>
          <s:text name="peerReview.header.noFiles"/>
        </div>
      </s:else> --%>

      <s:if test="!receivedGrades.isEmpty() && resultsPeriodActive">
        <!-- Received overall grade -->
        <h3>
          <s:text name = "grading.header.overall" />:
          <!-- Overall grade itself -->
          <strong>
            <s:if test="receivedGrades.get(0).mark != null">
              ${receivedGrades.get(0).mark}
            </s:if>
            <s:else>
              ${nameUnspecified}
            </s:else>
          </strong>
        </h3>
        <!-- Explanation text -->
        <p>
          <s:if test="calculateAverage">
            <s:text name="grading.header.averageScore" />
          </s:if>
          <s:else>
            <s:text name="grading.header.sumScore"/>
          </s:else>
        </p>
        <!-- Iterator, gets all the received (peer) reviews-->
        <s:iterator var="grade" value="receivedGrades" status="loop">
          <s:if test="#loop.index > 0">
            <s:if test="challengePeriodActive">
              <s:url action="challengeGrade" var="challengeGradeUrl">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="gradeId" value="%{#grade.id}" />
              </s:url>
              <s:url action="acceptGrade" var="acceptGradeUrl">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="gradeId" value="%{#grade.id}" />
              </s:url>
              <s:url action="discussGrade" var="discussGradeUrl">
                <s:param name="taskId" value="%{taskId}" />
                <s:param name="tabId" value="%{tabId}" />
                <s:param name="dbId" value="%{dbId}" />
                <s:param name="gradeId" value="%{#grade.id}" />
              </s:url>
            </s:if>
            <!--  Presenting collapse element title bar and user information about review-->
            <div class="tehtava">
              <div class="otsikko_ja_pisteet row">

                <!-- Link that opens the collapsed element -->
                <div class='collapselinkki' data-toggle="collapse" data-target ="#${grade.id}">
                  <div class="col-sm-4" >
                    <h4>
                      <span title="Click to collapse" class="glyphicon glyphicon-menu-right col-logo"></span>
                      <s:if test="visibleMembersMap[#grade.reviewerId] == null">
                        <s:text name="general.header.anonymous" />
                      </s:if>
                      <s:else>
                        ${visibleMembersMap[grade.reviewerId].lastName},
                        ${visibleMembersMap[grade.reviewerId].firstName}
                      </s:else>
                    </h4>
                  </div>

                  <!-- Timestamp of given grade -->
                  <div class ="grading-timestamp col-sm-4">
                    <h4>
                      ${grade.timeStampString}
                    </h4>
                  </div>
                  <div class="pisteet col-sm-4">
                    <s:if test="#grade.mark != null">
                      <h4 class="${statusColors[grade.status]}">
                        ${grade.mark} &nbsp; ${statusNames[grade.status]}
                        <s:if test="challengePeriodActive">
                          <s:if test="#grade.status != 6">
                            <span class="glyphicon glyphicon-ok">
                              <s:a href="%{challengeGradeUrl}" cssClass="btn btn-danger-small" onclick="event.stopPropagation()">
                                ${challengeGradeTXT}
                              </s:a>
                            </span>
                          </s:if>
                          <s:else>
                            <br />
                            <s:a href="%{discussGradeUrl}" cssClass="btn btn-danger-small" onclick="event.stopPropagation()">
                              ${discussGradeTXT}
                            </s:a>
                            <s:a href="%{acceptGradeUrl}" cssClass="btn btn-primary-small" cssStyle="float: right" onclick="event.stopPropagation()">
                              ${acceptGradeTXT}
                            </s:a>
                          </s:else>
                        </s:if>
                      </h4>
                    </s:if>
                    <s:else>
                      <h4 class="${statusColors[grade.status]}">
                        <s:text name="general.header.unspecified" />
                        <span class="glyphicon glyphicon-alert">
                          <s:if test="challengePeriodActive">
                            <s:a href="%{challengeGradeUrl}" cssClass="btn btn-danger-small">
                              <s:if test="challengePeriodActive && (#grade.status != 6)">
                                ${challengeGradeTXT}
                              </s:if>
                              <s:else>
                                ${discussGradeTXT}
                              </s:else>
                            </s:a>
                          </s:if>
                        </span>
                      </h4>
                    </s:else>
                  </div>
                </div>
              </div>

              <!-- Collapsing element get by grade id -->
              <div id="${grade.id}" class="collapse review">
                <s:if test="receivedReviewsMap[#grade.id] == null ">
                  <div class="alert alert-info">
                    <s:text name="peerReview.header.noReviewText" />
                  </div>
                </s:if>
                <s:else>
                  <div class="review-print">
                    <!-- Multipart review -->
                    <s:if test="! receivedReviewsMap[#grade.id].empty && hasReviewInstructions && reviewInstructions.size > 0">
                      <s:iterator value="receivedReviewsMap[#grade.id].texts" status="loop">
                        <!-- Print related review instruction -->
                        <s:if test="reviewInstructions.get(#loop.index) != null">
                          <div class='togglable-instruction' style='display: none'>
                            <label><s:text name='peerReview.header.reviewInstructionPerQuestion' /></label>
                            <div class="panel-instruction">
                              ${reviewInstructions.get(loop.index).text}
                            </div>
                          </div>
                        </s:if>
                        <!-- Actual review and points -->
                        <div class="left">
                          <pre class="panel panel-default pre-non-code">${receivedReviewsMap[grade.id].texts[loop.index]}</pre>
                        </div>
                        <div class="right">
                          <pre class="panel panel-default points-print">${receivedReviewsMap[grade.id].marks[loop.index]}
                            <s:if test="reviewInstructions.get(#loop.index) != null"> / ${reviewInstructions.get(loop.index).maxPoints}</s:if>
                            </pre>
                          </div>
                          <div class="clearer"></div>
                      </s:iterator>
                      <button class="btn btn-default" onclick="toggleReviewInstructions(this)">
                        <s:text name="grading.header.toggleReviewInstructions"/>
                      </button>
                    </s:if>

                    <!-- Single part review -->
                    <s:elseif test="! receivedReviewsMap[#grade.id].empty">
                      <pre class="panel panel-default pre-non-code">${receivedReviewsMap[grade.id].allTexts}</pre>
                    </s:elseif>
                  </div>

                  <!-- Print secondary texts (teacher comments etc.) if they exist -->
                  <s:if test="receivedReviewsMap[#grade.id] != null && !receivedReviewsMap[#grade.id].secondaryEmpty">
                    <div class="review-print">
                      <span class="gray-text"><s:text name="grading.header.otherUsersComment"/></span><br />
                      <s:iterator value="receivedReviewsMap[#grade.id].secondaryTexts" var="comment" status="loop">
                        <pre class="panel panel-default pre-non-code">${comment}</pre>
                      </s:iterator>
                    </div>
                  </s:if>
                </s:else>

                <!-- Bottom closer for collapsing element -->
                <div class="inner-collapse row">
                  <div class="col-sm-4"></div>
                  <div class="col-sm-4">
                    <span class="collapse-alalinkki glyphicon glyphicon-menu-up"
                          data-toggle="collapse"
                          data-target ="#${grade.id}">
                    </span>
                  </div>
                  <div class="col-sm-4"></div>
                </div>
              </div>
            </div>
          </s:if>
        </s:iterator>
      </s:if>
      <!-- No reviews yet. Tell when results period is. -->
      <s:else>
        <div class='alert alert-info'>
          <s:text name="peerReview.header.resultsPeriod"/>
          <strong>
            <weto:timePeriod starting="${resultsPeriod[0]}" ending="${resultsPeriod[1]}" />
          </strong>.
        </div>
      </s:else>
    </div>
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
      $(".help-sign").click(function (event) {
        $(this).parent().siblings(".help-field").toggle();
      });
      // Copy text from editor to the form input field upon submit.
      // Also poll login status.
      $("form").submit(function () {
        var ok = true;
        $(this).find('.reviewText').each(function ()
        {
          var revText = tinymce.get(this.id).getContent().trim();
          if ($(this).hasClass("scored") && (!revText || (revText.length < 2)))
          {
            ok = false;
          }
          $(this).prev("input").val(revText);
        });
        if (!ok)
        {
          alert("Not saved: verify that all scored review texts contain something!")
        }
        return ok && pollLogin();
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
