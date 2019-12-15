<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:if test="quiz != null">
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
  <s:url action="viewQuestionAnswers" var="viewAnswersUrl" escapeAmp="false">
    <s:param name="taskId" value="%{taskId}" />
    <s:param name="tabId" value="%{tabId}" />
    <s:param name="dbId" value="%{dbId}" />
  </s:url>
  <script src="js/ace/ace.js" type="text/javascript" charset="utf-8"></script>
  <script src="js/tinymce4/tinymce.min.js"></script>
  <script src="js/grading.js"></script>
  <s:set var="MULTICHOICE_ID">1</s:set>
  <s:set var="ESSAY_ID">2</s:set>
  <s:set var="SURVEY_ID">3</s:set>
  <s:set var="PROGRAM_ID">4</s:set>
  <s:set var="SCORETXT"><s:text name="general.header.score" /></s:set>
  <s:set var="FEEDBACKTXT"><s:text name="autograding.header.feedback" /></s:set>
  <s:set var="TESTNOTXT"><s:text name="autograding.header.testNo" /></s:set>
  <h3><s:text name="mainTab.header.editQuiz" /></h3>
  <div class="contentBox" id="newQuestionBox">
    <h4>
      <s:text name="quiz.header.addQuestion" />
    </h4>
    <div id="multiChoiceTemplate" style="display: none">
      <div class="addMultiChoice">
        <table>
          <tr>
            <td>
              <s:text name="quiz.header.questionName" />:
            </td>
            <td>
              <input type="text" name="newQuestionName" size="50"/>
            </td>
          </tr>
          <tr>
            <td>
              <s:text name="quiz.header.questionText" />:
            </td>
            <td>
              <textarea class="textEditor" name="newQuestionTexts" cols="80" rows="3"></textarea>
            </td>
          </tr>
        </table>
        <div class="contentBox">
          <s:text name="quiz.header.answerOptions" />
          <br>
          <span class="btn btn-default" onclick="addOptionTemplate(this, 'multiOptionTemplate'); addTinyMce();">
            <s:text name="quiz.header.addOption" />
          </span>
        </div>
        <hr>
        <div>
          <label>
            <input type="checkbox" name="newSingleAnswer" value="true" />
            <s:text name="quiz.header.singleAnswer" />
          </label>
          <br>

          <label><input type="checkbox" name="newShuffle" value="true"/> <s:text name="quiz.header.shuffle" /></label>
          <div>

            <h5><s:text name="quiz.header.numbering" /> </h5>
            <br>
            <label><input type="radio" name="newNumbering" value="123" checked="checked" />&nbsp;123</label>
            <br>
            <label><input type="radio" name="newNumbering" value="abc" />&nbsp;abc</label>
            <br>
            <label><input type="radio" name="newNumbering" value="ABCD" />&nbsp;ABC</label>
            <br>
            <label><input type="radio" name="newNumbering" value="none" /><s:text name="general.header.none" /></label>
            <br>
            <label><input type="radio" name="newNumbering" value="horiz" /><s:text name="quiz.header.horizontal" /></label>
            <br>
          </div>
        </div>
        &nbsp;&nbsp;&nbsp;
        <span class="btn btn-default" onclick="$(this).closest('.addMultiChoice').remove();
            document.getElementById('newQuestionBox').className = 'contentBox';
            $('#addQuestion').show()">
          <s:text name="quiz.header.cancelQuestion" />
        </span>
        <input type="hidden" name="newQuestionType" value="${MULTICHOICE_ID}" />
        <input type="submit" value="<s:text name="quiz.header.saveNewQuestion" />" class="btn btn-primary" />
      </div>
    </div>
    <div id="multiOptionTemplate" style="display: none">
      <div class="contentBox">
        <fieldset class= "form-fieldset">
          <div class="form-group">
            <label><s:text name="quiz.header.questionAnswer" /> </label>
            <textarea class="textEditor form-control" name="newAnswers" cols="80" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label><s:text name="quiz.header.questionFeedback" /></label>
            <textarea class="textEditor form-control" name="newFeedbacks" cols="80" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label><s:text name="quiz.header.questionScore" /> </label>
            <input type="number" step="0.1" name="newScores" size="4" class="form-control"/>
          </div>
          <div>
            <span class="btn-danger btn delete-choice" onclick="$(this).closest('.contentBox').remove()">
              <s:text name="quiz.header.deleteOption" />
            </span>
          </div>
        </fieldset>
      </div>
    </div>
    <div id="essayTemplate" style="display: none">
      <div class="addEssay">
        <table>
          <tr>
            <td>
              <s:text name="quiz.header.questionName" />:
            </td>
            <td>
              <input type="text" name="newQuestionName" size="50"/>
            </td>
          </tr>
          <tr>
            <td>
              <s:text name="quiz.header.questionText" />:
            </td>
            <td>
              <textarea class="textEditor" name="newQuestionTexts" cols="80" rows="3"></textarea>
            </td>
          </tr>
        </table>
        &nbsp;&nbsp;&nbsp;
        <span class="btn btn-default" onclick="$(this).closest('.addEssay').remove();
            document.getElementById('newQuestionBox').className = 'contentBox';
            $('#addQuestion').show()">
          <s:text name="quiz.header.cancelQuestion" />
        </span>
        <input type="hidden" name="newQuestionType" value="${ESSAY_ID}" />
        <input type="submit" value="<s:text name="quiz.header.saveNewQuestion" />" class="btn btn-primary" />
      </div>
    </div>
    <div id="surveyTemplate" style="display: none">
      <div class="addSurvey">
        <div class="Survey_header form-group">
          <label title="Question content"><s:text name="quiz.header.questionName" /> /Topic : &nbsp;</label>
          <input type="text" name="newQuestionName" size="50" class ="form-control">
        </div>
        <div class="contentBox" id="surveyOptions">
          <span title="In example scale 1-5">
            <s:text name="quiz.header.surveyChoices" />
          </span>
          <br>
          <span class="btn btn-default" onclick="addOptionTemplate(this, 'surveyOptionTemplate')">
            <s:text name="quiz.header.addOption" /> <%--Move under new choices--%>
          </span>
        </div>
        <hr>

        <div class="contentBox" id="surveyQuestions">
          <span title="Subquestions">
            <s:text name="quiz.header.surveyQuestions" />
          </span>
          <br>
          <span class="btn btn-default" onclick="addOptionTemplate(this, 'surveyQuestionTemplate')">
            <s:text name="quiz.header.addSurveyQuestion" />
          </span>
        </div>
        <hr>
        <div class="surveyCheckbox form-group">
          <div>
            <div class="form-inline">
              <label> <input type="checkbox" name="newSingleAnswer" value="true" />
                &nbsp;<s:text name="quiz.header.singleAnswer" />
              </label>
            </div>
          </div>
          <hr>
          <div>
            <div>
              <label><s:text name="quiz.header.detail" />: &nbsp; </label>
              <input type="text" name="newDetail" size="20" class="form-control"/>
            </div>
          </div>
        </div>
        <hr>
        <br>
        <span class="btn btn-default" onclick="$(this).closest('.addSurvey').remove();
            document.getElementById('newQuestionBox').className = 'contentBox';
            $('#addQuestion').show()">
          <s:text name="quiz.header.cancelQuestion" />
        </span>
        <input type="hidden" name="newQuestionType" value="${SURVEY_ID}" />
        <input type="submit" value="<s:text name="quiz.header.saveNewQuestion" />" class="btn btn-primary saveButton"/>
      </div>
    </div>
    <div id="surveyOptionTemplate" style="display: none">
      <div class="contentBox">
        <div>
          <div class="form-group">
            <div class="form-inline">
              <input type="text" name="newAnswers" size="40" class="form-control"/>
              <span class="btn btn-danger deleteButton" onclick="$(this).closest('.contentBox').remove()">
                <s:text name="quiz.header.deleteOption" />
              </span>
            </div>

          </div>
        </div>
      </div>
    </div>
    <div id="surveyQuestionTemplate" style="display: none">
      <div class="contentBox">
        <div class="form-group">
          <div class="form-inline">
            <input type="text" name="newQuestionTexts" size="40" class="form-control"/>
            <span class="btn-danger btn" onclick="$(this).closest('.contentBox').remove()">
              <s:text name="quiz.header.deleteQuestion" />
            </span>
          </div>
        </div>
      </div>
    </div>
    <div id="programTemplate" style="display: none">
      <div class="addProgram">
        <table>
          <tr>
            <td>
              <s:text name="quiz.header.questionName" />:
            </td>
            <td>
              <input type="text" name="newQuestionName" size="50"/>
            </td>
          </tr>
          <tr>
            <td>
              <s:text name="quiz.header.questionText" />:
            </td>
            <td>
              <textarea class="textEditor" name="newQuestionTexts" cols="80" rows="3"></textarea>
            </td>
          </tr>
          <tr>
            <td>
              <s:text name="quiz.header.language" />:
            </td>
            <td>
              <input type="text" name="newNumbering" size="50" />
            </td>
          </tr>
          <tr>
            <td>
              <s:text name="quiz.header.prologue" />:
            </td>
            <td>
              <input type="hidden" name="newQuestionTexts" class="newProgramInput" />
              <div class="newProgramEditor"></div>
            </td>
          </tr>
          <tr>
            <td>
              <s:text name="quiz.header.epilogue" />:
            </td>
            <td>
              <input type="hidden" name="newQuestionTexts" class="newProgramInput" />
              <div class="newProgramEditor"></div>
            </td>
          </tr>
          <tr>
            <td><s:text name="grading.header.newDocument" />:</td>
            <td><input type="file" name="newQuestionFile" /></td>
          </tr>
        </table>
        &nbsp;&nbsp;&nbsp;
        <span class="btn btn-default" onclick="$(this).closest('.addProgram').remove();
            document.getElementById('newQuestionBox').className = 'contentBox';
            $('#addQuestion').show()">
          <s:text name="quiz.header.cancelQuestion" />
        </span>
        <input type="hidden" name="newQuestionType" value="${PROGRAM_ID}" />
        <input type="submit" value="<s:text name="quiz.header.saveNewQuestion" />" class="btn btn-primary" />
      </div>
    </div>
    <form action="<s:url action="saveQuestion" />" method="post" enctype="multipart/form-data">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <div id="addQuestion">
        <s:text name="quiz.header.questionType" />:
        <span class="btn btn-default" onclick="$(this).parent().after($('#multiChoiceTemplate > div').first().clone());
            document.getElementById('newQuestionBox').className = 'highlightBox';
            $(this).parent().hide();
            addTinyMce();">
          <s:text name="quiz.header.multipleChoice" />
        </span>
        &nbsp;
        <span class="btn btn-default" onclick="$(this).parent().after($('#essayTemplate > div').first().clone());
            document.getElementById('newQuestionBox').className = 'highlightBox';
            $(this).parent().hide();
            addTinyMce();">
          <s:text name="quiz.header.essay" />
        </span>
        &nbsp;
        <span class="btn btn-default" onclick="$(this).parent().after($('#surveyTemplate > div').first().clone());
            document.getElementById('newQuestionBox').className = 'highlightBox';
            $(this).parent().hide();
            addTinyMce();">
          <s:text name="quiz.header.survey" />
        </span>
        &nbsp;
        <span class="btn btn-default" onclick="$(this).parent().after($('#programTemplate > div').first().clone());
            document.getElementById('newQuestionBox').className = 'highlightBox';
            $(this).parent().hide();
            addTinyMce();
            addAce(this);">
          <s:text name="quiz.header.program" />
        </span>
      </div>
    </form>
  </div>
  <div class="contentBox">
    <h4>
      <s:text name="quiz.header.organize" />
      <a href="${viewAnswersUrl}" style="float: right">[<s:text name="quiz.header.viewAllAnswers" />]</a>
    </h4>
    <div id="accordion">
      <s:iterator value="quiz.questions" var="question" status="count">
        <div class="group" data-questionId="${question.questionId}">
          <h3 class="handle">${question.questionName} &nbsp;&nbsp;(question id: ${question.questionId}) <a href="${viewAnswersUrl}&questionId=${question.questionId}" style="float: right" onclick="event.stopPropagation()">[<s:text name="quiz.header.viewAnswers" />]</a></h3>
          <div>
            <form action="<s:url action="saveQuestion" />" method="post" enctype="multipart/form-data" onsubmit="return pollLogin()">
              <input type="hidden" name="taskId" value="${taskId}" />
              <input type="hidden" name="tabId" value="${tabId}" />
              <input type="hidden" name="dbId" value="${dbId}" />
              <s:if test="#question.questionType == #MULTICHOICE_ID">
                <div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.questionName" />:
                    </div>
                    <div>
                      <input type="text" name="newQuestionName" value="${question.questionName}" size="50" />
                      <input type="hidden" name="newQuestionType" value="${question.questionType}" />
                      <input type="hidden" name="newQuestionId" value="${question.questionId}" />
                    </div>
                  </div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.questionText" />:
                    </div>
                    <div>
                      <textarea class="textEditor" name="newQuestionTexts" cols="80" rows="3"><s:property value="#question.questionTexts[0]" /></textarea>
                    </div>
                  </div>
                </div>
                <div class="contentBox">
                  <s:text name="quiz.header.answerOptions" />
                  <span class="linkButton" onclick="addOptionTemplate(this, 'multiOptionTemplate'); addTinyMce();">
                    <s:text name="quiz.header.addOption" />
                  </span>
                  <s:iterator var="answerOption" value="#question.choices" status="loop">
                    <div class="contentBox">
                      <table>
                        <tr>
                          <td class="label, tdLabel">
                            <s:text name="quiz.header.questionAnswer"/>&nbsp;${loop.count}:
                          </td>
                          <td><textarea class="textEditor" name="newAnswers" cols="80" rows="3"><s:property value="#answerOption" /></textarea></td>
                          <td rowspan="3">
                            <span class="btn-danger-small" onclick="$(this).closest('.contentBox').remove()">
                              <s:text name="quiz.header.deleteOption" />
                            </span>
                          </td>
                        </tr>
                        <tr>
                          <td class="label, tdLabel">
                            <s:text name="quiz.header.questionFeedback">
                              <s:param>${loop.count}</s:param>
                            </s:text>:
                          </td>
                          <td>
                            <textarea class="textEditor" name="newFeedbacks" cols="80" rows="3">${question.feedbacks[loop.index]}</textarea>
                          </td>
                        </tr>
                        <tr>
                          <td class="label, tdLabel">
                            <s:text name="quiz.header.questionScore">
                              <s:param>${loop.count}</s:param>
                            </s:text>:
                          </td>
                          <td>
                            <input type="text" name="newScores" cols="80" value="${question.scoreFractions[loop.index]}" />
                          </td>
                        </tr>
                      </table>
                    </div>
                  </s:iterator>
                </div>
                <table>
                  <tr>
                    <td>
                      <s:text name="quiz.header.singleAnswer" />:
                    </td>
                    <td colspan="5">
                      <input type="checkbox" name="newSingleAnswer" value="true" <s:if test="#question.singleAnswer">checked="checked"</s:if> />
                      </td>
                    </tr>
                    <tr>
                      <td>
                      <s:text name="quiz.header.shuffle" />:
                    </td>
                    <td colspan="5">
                      <input type="checkbox" name="newShuffle" value="true"
                             <s:if test="#question.shuffle">checked="checked"</s:if> />
                      </td>
                    </tr>
                    <tr>
                      <td>
                      <s:text name="quiz.header.numbering" />:
                    </td>
                    <td>
                      123<input type="radio" name="newNumbering" value="123"
                                <s:if test="#question.numbering == '123'">checked="checked"</s:if> />
                      </td>
                      <td>
                        abc<input type="radio" name="newNumbering" value="abc"
                        <s:if test="#question.numbering == 'abc'">checked="checked"</s:if> />
                      </td>
                      <td>
                        ABC<input type="radio" name="newNumbering" value="ABCD"
                        <s:if test="#question.numbering == 'ABCD'">checked="checked"</s:if> />
                      </td>
                      <td>
                      <s:text name="general.header.none" /><input type="radio" name="newNumbering" value="none"
                             <s:if test="#question.numbering == 'none'">checked="checked"</s:if> />
                      </td>
                      <td>
                      <s:text name="quiz.header.horizontal" /><input type="radio" name="newNumbering" value="horiz"
                             <s:if test="#question.numbering == 'horiz'">checked="checked"</s:if> />
                      </td>
                    </tr>
                  </table>
              </s:if>
              <s:elseif test="#question.questionType == #ESSAY_ID">
                <table>
                  <tr>
                    <td class="label, tdLabel">
                      <s:text name="quiz.header.questionName" />:
                    </td>
                    <td>
                      <input type="text" name="newQuestionName" value="${question.questionName}" size="50" />
                      <input type="hidden" name="newQuestionType" value="${question.questionType}" />
                      <input type="hidden" name="newQuestionId" value="${question.questionId}" />
                    </td>
                  </tr>
                  <tr>
                    <td class="label, tdLabel">
                      <s:text name="quiz.header.questionText" />:
                    </td>
                    <td>
                      <textarea class="textEditor" name="newQuestionTexts" cols="80" rows="3">${question.questionTexts[0]}</textarea>
                    </td>
                  </tr>
                </table>
              </s:elseif>
              <s:elseif test="#question.questionType == #SURVEY_ID">
                <table>
                  <tr>
                    <td class="label, tdLabel">
                      <s:text name="quiz.header.questionName" />:
                    </td>
                    <td>
                      <input type="text" name="newQuestionName" value="${question.questionName}" size="50" />
                      <input type="hidden" name="newQuestionType" value="${question.questionType}" />
                      <input type="hidden" name="newQuestionId" value="${question.questionId}" />
                    </td>
                  </tr>
                </table>
                <div class="contentBox">
                  <s:text name="quiz.header.surveyChoices" />
                  <span class="linkButton" onclick="addOptionTemplate(this, 'surveyOptionTemplate')">
                    <s:text name="quiz.header.addOption" />
                  </span>
                  <s:iterator var="answerOption" value="#question.choices" status="loop">
                    <div class="contentBox">
                      <table>
                        <tr>
                          <td class="label, tdLabel">
                            <s:text name="quiz.header.questionAnswer"/>&nbsp;${loop.count}:
                          </td>
                          <td><input type="text" name="newAnswers" value="${answerOption}" size="20" /></td>
                          <td>
                            <span class="btn btn-danger-small" onclick="$(this).closest('.contentBox').remove()">
                              <s:text name="quiz.header.deleteOption" />
                            </span>
                          </td>
                        </tr>
                      </table>
                    </div>
                  </s:iterator>
                </div>
                <div class="contentBox" id="surveyQuestions">
                  <s:text name="quiz.header.surveyQuestions" />
                  <span class="linkButton" onclick="addOptionTemplate(this, 'surveyQuestionTemplate')">
                    <s:text name="quiz.header.addSurveyQuestion" />
                  </span>
                  <s:iterator var="surveyQuestion" value="#question.questionTexts" status="loop">
                    <div class="contentBox">
                      <table>
                        <tr>
                          <td class="label, tdLabel">
                            <s:text name="quiz.header.questionText"/>&nbsp;${loop.count}:
                          </td>
                          <td><input type="text" name="newQuestionTexts" value="${surveyQuestion}" size="40" /></td>
                        </tr>
                      </table>
                    </div>
                  </s:iterator>
                </div>
                <table>
                  <tr>
                    <td class="label, tdLabel">
                      <s:text name="quiz.header.singleAnswer" />:
                    </td>
                    <td>
                      <input type="checkbox" name="newSingleAnswer" value="${count.index}" />
                    </td>
                  </tr>
                  <tr>
                    <td class="label, tdLabel">
                      <s:text name="quiz.header.detail" />:
                    </td>
                    <td>
                      <input type="text" name="newDetail" size="20" />
                    </td>
                  </tr>
                </table>
              </s:elseif>
              <s:elseif test="#question.questionType == #PROGRAM_ID">
                <div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.questionName" />:
                    </div>
                    <div>
                      <input type="text" name="newQuestionName" value="${question.questionName}" size="50" />
                      <input type="hidden" name="newQuestionType" value="${question.questionType}" />
                      <input type="hidden" name="newQuestionId" value="${question.questionId}" />
                    </div>
                  </div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.questionText" />:
                    </div>
                    <div>
                      <textarea class="textEditor" name="newQuestionTexts" cols="80" rows="3">${question.questionTexts[0]}</textarea>
                    </div>
                  </div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.language" />:
                    </div>
                    <div>
                      <input type="text" name="newNumbering" value="${question.numbering}" size="50" />
                    </div>
                  </div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.prologue" />:
                    </div>
                    <div>
                      <input type="hidden" name="newQuestionTexts" class="newProgramInput" />
                      <div class="newProgramEditor"><s:property value="%{#question.questionTexts[1]}" escapeHtml="true"/></div>
                    </div>
                  </div>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="quiz.header.epilogue" />:
                    </div>
                    <div>
                      <input type="hidden" name="newQuestionTexts" class="newProgramInput" />
                      <div class="newProgramEditor"><s:property value="%{#question.questionTexts[2]}" escapeHtml="true"/></div>
                    </div>
                  </div>
                  <s:if test="(#question.questionZipData != null) && (#question.questionZipName != null)">
                    <div>
                      <div class="label, tdLabel">
                        <s:text name="grading.header.currentDocument" />:&nbsp;
                        <s:url action="downloadQuestionZip" var="downloadQuestionZipURL">
                          <s:param name="taskId" value="taskId" />
                          <s:param name="tabId" value="tabId" />
                          <s:param name="dbId" value="dbId" />
                          <s:param name="questionId" value="#question.questionId" />
                        </s:url>
                        <s:a href="%{downloadQuestionZipURL}" target="_blank">
                          ${question.questionZipName}
                        </s:a>
                      </div>
                    </div>
                  </s:if>
                  <div>
                    <div class="label, tdLabel">
                      <s:text name="grading.header.newDocument" />:
                    </div>
                    <div>
                      <input type="file" name="newQuestionFile" />
                    </div>
                  </div>
                </div>
              </s:elseif>
              <input type="submit" value="<s:text name="quiz.header.saveQuestion" />" class="btn btn-primary" />
              <input type="button" class="btn-danger-small" onclick="toggleDeleteQuestionOut(this)"
                     value="<s:text name="general.header.deleteQuestion" />" />
            </form>
            <form action="<s:url action="deleteQuestion" />" method="post" class="confirmBox" style="display: none">
              <input type="hidden" name="taskId" value="${taskId}" />
              <input type="hidden" name="tabId" value="${tabId}" />
              <input type="hidden" name="dbId" value="${dbId}" />
              <input type="hidden" name="questionId" value="${question.questionId}" />
              <s:text name="quiz.header.confirmDelete" />
              <input type="submit" class="btn-danger-small" value="<s:text name="general.header.delete" />" />
              <input type="button" class="btn btn-default-small" onClick="toggleDeleteQuestionIn(this)" value="<s:text name="general.header.cancel" />" />
            </form>
          </div>
        </div>
      </s:iterator>
    </div>
    <form action="<s:url action="saveQuestionOrder" />" method="post">
      <input type="hidden" name="taskId" value="${taskId}" />
      <input type="hidden" name="tabId" value="${tabId}" />
      <input type="hidden" name="dbId" value="${dbId}" />
      <input type="hidden" name="newQuestionOrder" id="newQuestionOrder" value="" />
      <button id="reorderQuestionsButton" class="btn btn-primary" onclick="saveNewQuestionOrder()" disabled >
        <s:text name="quiz.header.saveReorder" />
      </button>
    </form>
  </div>
  <hr />
  <h3><s:text name="quiz.header.preview" /></h3>
  <div class="maincontents content-col">
    <s:iterator var="contentElem" value="quiz.questions">
      <s:if test="#contentElem.userAnswers != null">
        <s:set var="boxStyle">highlightBox</s:set>
      </s:if>
      <s:else>
        <s:set var="boxStyle">questionBox</s:set>
      </s:else>
      <div class="${boxStyle}">
        <form action="#" class="quizAnswer" method="post">
          <input type="hidden" name="taskId" value="${taskId}" />
          <input type="hidden" name="tabId" value="${tabId}" />
          <input type="hidden" name="dbId" value="${dbId}" />
          <input type="hidden" name="questionId" value="${contentElem.questionId}" />
          <input type="hidden" name="autograde" value="false" />
          <input type="hidden" name="testRun" value="false" />
          <s:if test="#contentElem.contentElementType != #SURVEY_ID">
            <div class="multipleQuiz form-group">
              <h4>
                ${contentElem.questionName}
              </h4>
              <fieldset>
                ${contentElem.questionTexts[0]}<hr>
                <s:if test="#contentElem.contentElementType == #MULTICHOICE_ID">
                  <table>
                    <tr>
                      <td>
                        <ol class="bullet_${contentElem.numbering} quiz">
                          <s:if test="#contentElem.singleAnswer">
                            <s:iterator var="choice" value="#contentElem.choices" status="loop2">
                              <li class="form-group">
                                <label>${choice}<input type="radio" name="answers[${contentElem.questionId}][0]" value="<s:property value="#choice" />" ${inputStyle}
                                                       <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[0][#loop2.index] == 'true')">checked="checked"</s:if>/></label>
                                </li>
                            </s:iterator>
                          </s:if>
                          <s:else>
                            <s:iterator var="choice" value="#contentElem.choices" status="loop2">
                              <li>
                                <label>
                                  <input type="checkbox" name="answers[${contentElem.questionId}][0]" value="<s:property value="#choice" />" ${inputStyle}
                                         <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[0][#loop2.index] == 'true')">
                                           checked="checked"
                                         </s:if> />
                                  ${choice}
                                </label>
                              </li>
                            </s:iterator>
                          </s:else>
                        </ol>
                      </td>
                      <td>&nbsp;</td>
                      <td>
                        <s:if test="quizOpen">
                          <span class="btn-default-small" onclick="$('input[name=\'answers[${contentElem.questionId}][0]\']').removeAttr('checked')">
                            <s:text name="quiz.header.clearAnswer"/>
                          </span>
                        </s:if>
                      </td>
                      <td>&nbsp;</td>
                      <td>
                        <s:if test="quizOpen">
                          <input type="button" value="<s:text name="quiz.header.saveAnswer" />"
                                 class="linkButton" onclick="submitAnswer(this)"/>
                        </s:if>
                      </td>
                    </tr>
                  </table>
                </s:if>
                <s:elseif test="#contentElem.contentElementType == #ESSAY_ID">
                  <s:if test="quizOpen">
                    <input type="hidden" name="answers[${contentElem.questionId}][0]" class="essayInput" value="" />
                    <div id="essay${contentElem.questionId}" class="essayText" contenteditable="true">
                      <s:if test="#contentElem.userAnswers != null">
                        <s:property value="%{#contentElem.userAnswers[0][0]}" escapeHtml="false"/>
                      </s:if>
                    </div>
                    <input type="button" value="<s:text name="quiz.header.saveAnswer" />"
                           class="linkButton" onclick="submitEssay(this, ${contentElem.questionId})"/>
                  </s:if>
                  <s:else>
                    <div class="contentBox">
                      <s:if test="#contentElem.userAnswers != null">
                        <s:property value="%{#contentElem.userAnswers[0][0]}" escapeHtml="false"/>
                      </s:if>
                    </div>
                  </s:else>
                </s:elseif>
                <s:elseif test="#contentElem.contentElementType == #PROGRAM_ID">
                  <s:if test="quizOpen">
                    <input type="hidden" name="answers[${contentElem.questionId}][0]" class="programInput" value="" />
                    <s:if test="(#contentElem.questionTexts[1] != null) && (#contentElem.questionTexts[1].length() > 0)">
                      <div><s:property value="%{#contentElem.questionTexts[1]}" /></div>
                    </s:if>
                    <s:else>
                      <div></div>
                    </s:else>
                    <s:if test="#contentElem.userAnswers != null">
                      <div id="program${contentElem.questionId}" class="programEditor" data-lang="${contentElem.numbering}"><s:property value="%{#contentElem.userAnswers[0][0]}" escapeHtml="true"/></div>
                    </s:if>
                    <s:else>
                      <div id="program${contentElem.questionId}" class="programEditor" data-lang="${contentElem.numbering}"></div>
                    </s:else>
                    <s:if test="(#contentElem.questionTexts[2] != null) && (#contentElem.questionTexts[2].length() > 0)">
                      <div><s:property value="%{#contentElem.questionTexts[2]}" /></div>
                    </s:if>
                    <s:else>
                      <div></div>
                    </s:else>
                    <input type="button" value="<s:text name="quiz.header.saveAnswer" />" class="linkButton" onclick="$('#feedbackDiv${contentElem.questionId}').html('').hide();
                        submitCode(this, ${contentElem.questionId})"/>
                    <strong>
                      <s:text name="quiz.header.lastSaved" />:
                      <span class="answerDate">
                        <s:if test="#contentElem.userAnswerDate != null">
                          ${contentElem.userAnswerDate}
                        </s:if>
                        <s:else>
                          <s:text name="general.header.never" />
                        </s:else>
                      </span>
                    </strong>
                    <input type="button" value="<s:text name="submissions.header.testRun" />" class="linkButton" onclick="saveAndTestCode(this, ${contentElem.questionId})"/>
                    <s:text name="autograding.header.testNo" />: <input type="text" name="testNumber" size="3">
                    <input type="button" value="<s:text name="quiz.header.saveAndSubmit" />" class="linkButton" style="float: right" onclick="saveAndSubmitCode(this, ${contentElem.questionId})"/>
                  </s:if>
                  <s:else>
                    <div class="contentBox">
                      <s:if test="#contentElem.userAnswers != null">
                        <pre class="prettyprint"><s:property value="%{#contentElem.userAnswers[0][0]}" escapeHtml="true"/></pre>
                        <strong>
                          <s:text name="quiz.header.lastSaved" />:
                          <span class="answerDate">
                            <s:if test="#contentElem.userAnswerDate != null">
                              ${contentElem.userAnswerDate}
                            </s:if>
                            <s:else>
                              <s:text name="general.header.never" />
                            </s:else>
                          </span>
                        </strong>
                      </s:if>
                    </div>
                  </s:else>
                </s:elseif>
              </fieldset>
            </div>
          </s:if>
          <s:else>
            <s:if test="(#contentElem.detail != null) && (!#contentElem.detail.isEmpty())">
              <s:set var="colSpan" value="%{#contentElem.choices.length + 3}"/>
            </s:if>
            <s:else>
              <s:set var="colSpan" value="%{#contentElem.choices.length + 2}"/>
            </s:else>
            <s:set var="rowSpan" value="%{#contentElem.questionTexts.length + 2}"/>
            <table class="survey">
              <tr>
                <th colspan="${colSpan}">
                  <h4>${contentElem.questionName}</h4>
                </th>
                <td class="RIGHT" rowspan="${rowSpan}">
                  <s:if test="quizOpen">
                    <input type="button" value="<s:text name="quiz.header.saveAnswer" />"
                           class="linkButton" onclick="submitAnswer(this)"/>
                  </s:if>
                </td>
              </tr>
              <tr>
                <td class="TH"></td>
                <s:iterator var="choice" value="#contentElem.choices">
                  <td class="TH">${choice}</td>
                </s:iterator>
                <s:if test="(#contentElem.detail != null) && (!#contentElem.detail.isEmpty())">
                  <td class="TH">
                    ${contentElem.detail}
                  </td>
                </s:if>
              </tr>
              <s:iterator var="surveyQuestion" value="#contentElem.questionTexts" status="loop2">
                <tr>
                  <td class="LEFT">${surveyQuestion}</td>
                  <s:if test="#contentElem.singleAnswer">
                    <s:iterator var="choice" value="#contentElem.choices" status="loop3">
                      <td><input type="radio" name="answers[${contentElem.questionId}][${loop2.index}]" value="<s:property value="#choice" />" ${inputStyle}
                                 <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[#loop2.index][#loop3.index] == 'true')">checked="checked"</s:if>/></td>
                      </s:iterator>
                    </s:if>
                    <s:else>
                      <s:iterator var="choice" value="#contentElem.choices" status="loop3">
                      <td><input type="checkbox" name="answers[${contentElem.questionId}][${loop2.index}]" value="<s:property value="#choice" />" ${inputStyle}
                                 <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[#loop2.index][#loop3.index] == 'true')">checked="checked"</s:if>/></td>
                      </s:iterator>
                    </s:else>
                    <s:if test="(#contentElem.detail != null) && (!#contentElem.detail.isEmpty())">
                    <td class="RIGHT"><input type="text" name="answers[${contentElem.questionId}][${loop2.index}]" size="20" ${inputStyle} /></td>
                    </s:if>
                  <td class="RIGHT">
                    <s:if test="quizOpen">
                      <span class="smallLinkButton" onclick="$('input[name=\'answers[${contentElem.questionId}][${loop2.index}]\']').filter('[type!=text]').removeAttr('checked');
                          $('input[name=\'answers[${contentElem.questionId}][${loop2.index}]\']').filter('[type=text]').val('');"><s:text name="quiz.header.clearAnswer"/></span>
                    </s:if>
                  </td>
                </tr>
              </s:iterator>
            </table>
          </s:else>
        </form>
        <hr>
        <s:if test="(#contentElem.resultMark != null) || (#contentElem.resultFeedback != null) || (#contentElem.resultError != null)">
          <div style="min-width: 50%" id="feedbackDiv${contentElem.questionId}">
            <table>
              <thead>
                <tr>
                  <th colspan="2">
                    ${SCORETXT}: ${contentElem.resultMark}
                    &nbsp;&nbsp;<span style="float: right" class="btn btn-primary-small" onclick="$(this).closest('tr').next('tr').toggle()">
                      Show/hide feedback
                    </span>
                  </th>
                </tr>
                <tr>
                  <td>
                    ${FEEDBACKTXT}
                    <s:if test="#contentElem.resultTest">
                      <br/>(${TESTNOTXT}${contentElem.resultTest})
                    </s:if>
                  </td>
                  <td>
                    <s:if test="#contentElem.resultFullFeedback != null">
                      <s:url action="viewFeedback" var="viewFeedbackURL">
                        <s:param name="taskId" value="taskId" />
                        <s:param name="tabId" value="tabId" />
                        <s:param name="dbId" value="dbId" />
                        <s:param name="tagId" value="#contentElem.resultFullFeedback" />
                      </s:url>
                      <s:a href="%{viewFeedbackURL}" target="_blank" cssClass="btn btn-default">
                        <s:text name="submissions.header.fullFeedback" />
                      </s:a>
                    </s:if>
                    <pre class="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px">${contentElem.resultFeedback} ${contentElem.resultError}</pre>
                  </td>
                </tr>
              </thead>
            </table>
          </div>
        </s:if>
        <s:else>
          <div style="display: none; min-width: 50%" id="feedbackDiv${contentElem.questionId}">
          </div>
        </s:else>
      </div>
    </s:iterator>
  </div>
  <s:url action="relogin" var="reloginUrl" escapeAmp="false" />
  <div id="dummyDiv" style = "display: none;"></div>
  <script>
    var stop = false;
    var newQuestionOrder;
    var entityMap = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#39;',
      '/': '&#x2F;',
      '`': '&#x60;',
      '=': '&#x3D;'
    };
    function escapeHtml(string) {
      return String(string).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
      });
    }

    function saveNewQuestionOrder()
    {
      document.getElementById("newQuestionOrder").value = newQuestionOrder;
    }

    function addOptionTemplate(elem, idString)
    {
      var thisElem = $(elem);
      thisElem.add(thisElem.prevAll('div')).last().before($('#' + idString + ' > div').first().clone());
    }

    function toggleDeleteQuestionOut(elem)
    {
      $(elem).parent().next('form[class*=\'confirmBox\']').toggle();
    }

    function toggleDeleteQuestionIn(elem)
    {
      $(elem).closest('form[class*=\'confirmBox\']').toggle();
    }

    function taskDocFileSelectCallback(field_name, url, type, win) {
      var browseUrl = (type === "file") ? "${taskDocumentsUrl}" : "${taskImagesUrl}";
      tinymce.activeEditor.windowManager.open({
        file: browseUrl,
        title: 'Task Document Selection',
        autoScroll: true,
        width: 480,
        height: 500
      }, {
        window: win,
        input: field_name
      });
      return false;
    }

    function addTinyMce()
    {
      tinymce.init({
        selector: "form textarea.textEditor",
        entity_encoding: "raw",
        plugins: [
          "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
          "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
          "save table contextmenu directionality emoticons template paste textcolor mathslate"
        ],
        autosave_retention: "120m",
        default_link_target: "_blank",
        file_browser_callback: taskDocFileSelectCallback,
        menubar: false,
        toolbar: [
          "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor charmap emoticons mathslate | table",
          "code link unlink image | bold italic underline strikethrough superscript subscript removeformat | outdent indent bullist numlist | preview"
        ],
        style_formats_merge: true,
        style_formats: [
          {title: 'Code highlight', block: 'pre', wrapper: true, classes: 'prettyprint'},
          {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
          {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
          {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
          {title: 'Shadow-box-week', block: 'div', wrapper: true, classes: 'content-col'},
          {title: 'Default-Panel-element', block: 'div', wrapper: true, classes: 'panel'},
          {title: 'Interactive-code-panel', block: 'div', wrapper: true, classes: 'code-panel prettyprint'},
          {title: 'Peerreview-border-box', block: 'div', wrapper: true, classes: 'peerreview-border-box'},
          {title: 'Peerreview-highlight-text', block: 'div', wrapper: true, classes: 'peerreview-highlight'},
          {title: 'Highlight-text-background', block: 'div', wrapper: true, classes: 'highlight-text-background'},
          {title: 'icemint', block: 'div', wrapper: true, classes: 'icemint'},
          {title: 'red-panel-box', block: 'div', wrapper: true, classes: 'panel-red'},
          {title: 'blue-panel-box', block: 'div', wrapper: true, classes: 'panel-blue'},
          {title: 'latte-panel-box', block: 'div', wrapper: true, classes: 'panel-latte'},
          {title: 'gray-panel-box', block: 'div', wrapper: true, classes: 'panel-darker-gray'},
          {title: 'news-panel-box', block: 'div', wrapper: true, classes: 'panel-news'},
          {title: 'news-title', block: 'h5', classes: 'news-title-style'},
          {title: 'only-border-box', block: 'div', wrapper: true, classes: 'only-border'}
        ]
      });
    }

    function addAce(btnEl)
    {
      $(btnEl).closest("form").find(".newProgramEditor").each(function (i)
      {
        var editDiv = $(this);
        var editor = ace.edit(editDiv[0]);
        editor.setOptions({
          maxLines: Infinity,
          fontSize: "11pt"
        });
        editor.getSession().setMode("ace/mode/java");
        editor.getSession().setTabSize(2);
        editor.getSession().setUseSoftTabs(true);
        editor.getSession().setUseWrapMode(true);
        editor.setTheme("ace/theme/textmate");
        // copy back to textarea on form submit...
        editDiv.closest('form').submit(function () {
          editDiv.prevAll("input.newProgramInput").first().val(editor.getSession().getValue());
        });
      });
    }

    function populateFeedback(feedbackEl, result)
    {
      var markText = '';
      var feedbackText = '';
      var errorText = '';
      var testText = '';
      var fullFeedbackId = '';
      var fullFeedbackElem = '';
      try
      {
        var resultJSON = JSON.parse(result);
        markText = resultJSON["mark"];
        feedbackText = resultJSON["feedback"];
        fullFeedbackId = resultJSON["fullFeedbackId"];
        errorText = resultJSON["error"];
        testText = resultJSON["test"];
      } catch (e)
      {
      }
      if (!markText)
      {
        markText = '0.0';
      } else if (typeof markText === 'number')
      {
        markText = "" + markText;
        if (markText.indexOf(".") < 0)
        {
          markText += ".0";
        }
      }
      if (!feedbackText)
      {
        feedbackText = '';
      } else
      {
        feedbackText = escapeHtml(feedbackText);
      }
      if (errorText && (errorText.length > 0))
      {
        errorText = "\n" + "ERROR MESSAGE\n" + escapeHtml(errorText);
      } else
      {
        errorText = '';
      }
      if (testText)
      {
        testText = " (${TESTNOTXT} " + testText + ")";
      } else
      {
        testText = '';
      }
      if (fullFeedbackId)
      {
        var url = 'viewFeedback.action?dbId=${dbId}&taskId=${taskId}&tabId=${tabId}&tagId=' + fullFeedbackId;
        fullFeedbackElem = '<a href="' + url + '" target="_blank" class="btn btn-default"><s:text name="submissions.header.fullFeedback" /></a>';
      }
      feedbackEl.html('<table><thead><tr><th colspan="2">${SCORETXT}: '
              + markText + '&nbsp;&nbsp;<span style="float: right" class="btn btn-primary-small" onclick="$(this).closest(\'tr\').next(\'tr\').toggle()">Show/hide feedback</span></th></tr><tr><td>${FEEDBACKTXT}' + '<br/>' + testText + '</td>'
              + '<td>' + fullFeedbackElem
              + '<pre class="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px">' + feedbackText + errorText
              + '</pre></td></tr></thead></table>');
      colorDiffFeedback(feedbackEl.find(".diffCell"));
    }

    function pollAutograding(feedbackEl, questionId)
    {
      $.get('getQuizScoreJSON.action?dbId=${dbId}&taskId=${taskId}&tabId=${tabId}&quizQuestionId=' + questionId).done(function (data, stat, xhr) {
        if (xhr.status == 200)
        {
          populateFeedback(feedbackEl, data);
        } else
        {
          feedbackEl.html('<img src="images/ajax-blue.gif" height="50px" /> (queue pos: ' + xhr.getResponseHeader("queuepos") + ')');
          setTimeout(function () {
            pollAutograding(feedbackEl, questionId);
          }, 1000);
        }
      }).fail(function (xhr, stat, err) {
        feedbackEl.empty();
        feedbackEl.hide();
        $('#infoBar').empty();
        $('#infoBar').append(
                '<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span>' + err + '</span></li></ul></div></div>'
                );
        $('.infoBar').show();
        timeoutobject = setTimeout(function ()
        {
          jQuery(".infoBar").fadeOut(300);
        }, 7000);
      });
    }

    function notifyUser(xhr, data, formEl)
    {
      if ((xhr.status == 200) || (xhr.status == 204)) {
        var commaPos = data.indexOf(";");
        var answerDate = "<s:text name="general.header.never"/>";
        var answerText = "";
        if (commaPos >= 0)
        {
          answerDate = data.substring(0, commaPos);
          answerText = data.substring(commaPos + 1);
        }
        if (answerText.length > 100)
        {
          answerText = answerText.substr(0, 100) + "...";
        }
        var dateSpan = formEl.find("span.answerDate");
        if (dateSpan.length > 0)
        {
          dateSpan.first().html(answerDate);
        }
        if (data.length)
        {
          formEl.closest("div").removeClass("questionBox").addClass("highlightBox");
        } else
        {
          formEl.closest("div").removeClass("highlightBox").addClass("questionBox");
        }
        $('#infoBar').empty();
        $('#infoBar').append(
                '<div id="messages"><div class="message_header"><s:text name="quiz.header.answerSuccess" /></div>'
                + escapeHtml(answerText) + '</div>'
                );
        if (typeof MathJax != "undefined")
        {
          MathJax.Hub.Queue(["Typeset", MathJax.Hub, "messages"]);
        }
        $('.infoBar').show();
        timeoutobject = setTimeout(function ()
        {
          jQuery(".infoBar").fadeOut(300);
        }, 7000);
      } else if (xhr.status == 403)
      {
        reloginPopup(tinymce.get('dummyDiv'));
      } else
      {
        $('#infoBar').empty();
        $('#infoBar').append('<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="quiz.message.answerError" /></span></li></ul></div></div>');
        $('.infoBar').show();
        timeoutobject = setTimeout(function ()
        {
          jQuery(".infoBar").fadeOut(300);
        }, 7000);
      }
    }

    function submitAnswer(btn)
    {
      var formEl = $(btn).closest("form");
      var formData = formEl.serialize();
      var url = '<s:url action="saveQuizAnswer"/>';
      $.post(url, formData).done(function (data, stat, xhr) {
        notifyUser(xhr, data, formEl);
      }).fail(function (xhr, stat, err) {
        notifyUser(xhr, "", formEl);
      });
    }

    function submitEssay(btn, essayId)
    {
      // Copy text from editor to the form input field upon submit
      var ansText = tinymce.get("essay" + essayId).getContent().trim();
      var inputJQElement = $(btn).prevAll(".essayInput").first();
      inputJQElement.val(ansText);
      var formEl = $(btn).closest("form");
      var formData = formEl.serialize();
      var url = '<s:url action="saveQuizAnswer"/>';
      $.post(url, formData).done(function (data, stat, xhr) {
        if ((xhr.status == 200) || (xhr.status == 204))
        {
          tinymce.triggerSave();
        }
        notifyUser(xhr, data, formEl);
      }).fail(function (xhr, stat, err) {
        notifyUser(xhr, "", formEl);
      });
    }

    function submitCode(btn, programId)
    {
      var inputJQElement = $(btn).prevAll("input.programInput").first();
      var ansText = ace.edit("program" + programId).getSession().getValue();
      inputJQElement.val(ansText);
      var formEl = $(btn).closest("form");
      var formData = formEl.serialize();
      var url = '<s:url action="saveQuizAnswer"/>';
      $.post(url, formData).done(function (data, stat, xhr) {
        notifyUser(xhr, data, formEl);
      }).fail(function (xhr, stat, err) {
        notifyUser(xhr, "", formEl);
      });
    }

    function saveAndSubmitCode(btn, questionId)
    {
      var formEl = $(btn).closest("form");
      formEl.find("input[name='autograde']").attr("value", "true");
      formEl.find("input[name='testRun']").attr("value", "false");
      submitCode(btn, questionId);
      var feedbackEl = $("#feedbackDiv" + questionId);
      feedbackEl.html('<img src="images/ajax-blue.gif" height="50px" style="display: block; margin: 0 auto;" />');
      feedbackEl.show();
      setTimeout(function () {
        pollAutograding(feedbackEl, questionId);
      }, 1000);
    }

    function saveAndTestCode(btn, questionId)
    {
      var formEl = $(btn).closest("form");
      formEl.find("input[name='autograde']").attr("value", "true");
      formEl.find("input[name='testRun']").attr("value", "true");
      submitCode(btn, questionId);
      var feedbackEl = $("#feedbackDiv" + questionId);
      feedbackEl.html('<img src="images/ajax-blue.gif" height="50px" style="display: block; margin: 0 auto;" />');
      feedbackEl.show();
      setTimeout(function () {
        pollAutograding(feedbackEl, questionId);
      }, 1000);
    }

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
      $("#accordion").click(function (event) {
        if (stop) {
          event.stopImmediatePropagation();
          event.preventDefault();
          stop = false;
        }
      });
      $("#accordion")
              .accordion({
                header: "> div > h3",
                collapsible: true,
                heightStyle: "content",
                active: false
              })
              .sortable({
                axis: "y",
                handle: "h3.handle",
                stop: function (event, ui) {
                  // IE doesn't register the blur when sorting
                  // so trigger focusout handlers to remove .ui-state-focus
                  ui.item.children("h3").triggerHandler("focusout");
                  stop = true;
                },
                update: function (event, ui) {
                  newQuestionOrder = $(this).sortable('toArray', {attribute: 'data-questionId'}).toString();
                  $('#reorderQuestionsButton').prop("disabled", false);
                }
              });
      addTinyMce();
      $(".newProgramEditor").each(function (i)
      {
        var editDiv = $(this);
        var editor = ace.edit(editDiv[0]);
        editor.setOptions({
          maxLines: Infinity,
          fontSize: "11pt"
        });
        editor.getSession().setMode("ace/mode/c_cpp");
        editor.getSession().setTabSize(2);
        editor.getSession().setUseSoftTabs(true);
        editor.getSession().setUseWrapMode(true);
        editor.setTheme("ace/theme/textmate");
        // copy back to textarea on form submit...
        editDiv.closest('form').submit(function () {
          editDiv.prevAll("input.newProgramInput").first().val(editor.getSession().getValue());
        });
      });
      tinymce.init({
        selector: "div.essayText",
        inline: "true",
        entity_encoding: "raw",
        plugins: [
          "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
          "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
          "save table contextmenu directionality emoticons template paste textcolor mathslate"
        ],
        autosave_retention: "120m",
        default_link_target: "_blank",
        file_browser_callback: taskDocFileSelectCallback,
        menubar: false,
        toolbar: [
          "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
          "code link unlink image | bold italic underline superscript subscript mathslate | outdent indent bullist numlist | preview"
        ],
        style_formats_merge: true,
        style_formats: [
          {title: 'Code highlight', block: 'pre', wrapper: true, classes: 'prettyprint'},
          {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
          {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
          {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
          {title: 'Shadow-box-week', block: 'div', wrapper: true, classes: 'content-col'},
          {title: 'Default-Panel-element', block: 'div', wrapper: true, classes: 'panel'},
          {title: 'Interactive-code-panel', block: 'div', wrapper: true, classes: 'code-panel prettyprint'},
          {title: 'Peerreview-border-box', block: 'div', wrapper: true, classes: 'peerreview-border-box'},
          {title: 'Peerreview-highlight-text', block: 'div', wrapper: true, classes: 'peerreview-highlight'},
          {title: 'Highlight-text-background', block: 'div', wrapper: true, classes: 'highlight-text-background'},
          {title: 'icemint', block: 'div', wrapper: true, classes: 'icemint'},
          {title: 'red-panel-box', block: 'div', wrapper: true, classes: 'panel-red'},
          {title: 'blue-panel-box', block: 'div', wrapper: true, classes: 'panel-blue'},
          {title: 'latte-panel-box', block: 'div', wrapper: true, classes: 'panel-latte'},
          {title: 'gray-panel-box', block: 'div', wrapper: true, classes: 'panel-darker-gray'},
          {title: 'news-panel-box', block: 'div', wrapper: true, classes: 'panel-news'},
          {title: 'news-title', block: 'h5', classes: 'news-title-style'},
          {title: 'only-border-box', block: 'div', wrapper: true, classes: 'only-border'}
        ]
      });
      tinymce.init({
        selector: "#dummyDiv",
        inline: "true",
        menubar: false
      });
      $(".programEditor").each(function (i)
      {
        var answerDiv = $(this);
        var prologueDiv = answerDiv.prev();
        var epilogueDiv = answerDiv.next();
        var lang = answerDiv.data("lang").trim();
        if (lang.length > 0)
        {
          lang = "ace/mode/" + lang;
        } else
        {
          lang = "ace/mode/c_cpp";
        }
        var epilogueLines = 0;
        if (prologueDiv.html().length > 0)
        {
          var prologueEditor = ace.edit(prologueDiv[0]);
          prologueEditor.setHighlightActiveLine(false);
          prologueEditor.setHighlightGutterLine(false);
          prologueEditor.setReadOnly(true);
          prologueEditor.setOptions({
            maxLines: Infinity,
            fontSize: "11pt"
          });
          prologueEditor.getSession().setMode(lang);
          prologueEditor.getSession().setTabSize(2);
          prologueEditor.getSession().setUseSoftTabs(true);
          prologueEditor.getSession().setUseWrapMode(true);
          prologueEditor.setTheme("ace/theme/solarized_light");
          prologueEditor.session.gutterRenderer = {
            getWidth: function (session, lastLineNumber, config) {
              return 3 * config.characterWidth;
            },
            getText: function (session, row) {
              return row + session.$firstLineNumber;
            }
          };
          epilogueLines = prologueEditor.getSession().getLength();
        }
        var answerEditor = ace.edit(answerDiv[0]);
        answerEditor.setOptions({
          maxLines: Infinity,
          fontSize: "11pt",
          firstLineNumber: epilogueLines + 1
        });
        answerEditor.getSession().setMode(lang);
        answerEditor.getSession().setTabSize(2);
        answerEditor.getSession().setUseSoftTabs(true);
        answerEditor.getSession().setUseWrapMode(true);
        answerEditor.setTheme("ace/theme/textmate");
        answerEditor.session.gutterRenderer = {
          getWidth: function (session, lastLineNumber, config) {
            return 3 * config.characterWidth;
          },
          getText: function (session, row) {
            return row + session.$firstLineNumber;
          }
        };
        if (epilogueDiv.html().length > 0)
        {
          var epilogueEditor = ace.edit(epilogueDiv[0]);
          epilogueEditor.setHighlightActiveLine(false);
          epilogueEditor.setHighlightGutterLine(false);
          epilogueEditor.setReadOnly(true);
          epilogueEditor.setOptions({
            maxLines: Infinity,
            fontSize: "11pt",
            firstLineNumber: epilogueLines + answerEditor.getSession().getLength() + 1
          });
          epilogueEditor.getSession().setMode(lang);
          epilogueEditor.getSession().setTabSize(2);
          epilogueEditor.getSession().setUseSoftTabs(true);
          epilogueEditor.getSession().setUseWrapMode(true);
          epilogueEditor.setTheme("ace/theme/solarized_light");
          epilogueEditor.session.gutterRenderer = {
            getWidth: function (session, lastLineNumber, config) {
              return 3 * config.characterWidth;
            },
            getText: function (session, row) {
              return row + session.$firstLineNumber;
            }
          };
          answerEditor.on("change", function (e) {
            epilogueEditor.setOptions({maxLines: Infinity, firstLineNumber: epilogueLines + answerEditor.getSession().getLength() + 1});
          });
        }
      });
      colorDiffFeedback($(".diffCell"));
    });
  </script>
</s:if>
