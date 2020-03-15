<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:set var="HTML_ID">0</s:set>
<s:set var="MULTICHOICE_ID">1</s:set>
<s:set var="ESSAY_ID">2</s:set>
<s:set var="SURVEY_ID">3</s:set>
<s:set var="PROGRAM_ID">4</s:set>
<s:set var="SCORETXT"><s:text name="general.header.score" /></s:set>
<s:set var="FEEDBACKTXT"><s:text name="autograding.header.feedback" /></s:set>
<s:set var="TESTTXT"><s:text name="general.header.test" /></s:set>
<s:set var="CHOICETXT"><s:text name="quiz.header.questionAnswer" /></s:set>
<s:set var="inputStyle">disabled="disabled"</s:set>
  <script src="/wetoextra/mathjax/MathJax.js?config=TeX-AMS-MML_HTMLorMML"></script>
  <script type="text/x-mathjax-config">
    MathJax.Hub.Config({
    "HTML-CSS": {
    minScaleAdjust: 80
    }
    });
  </script>
<s:url action="exportQuestionAnswers" var="exportAnswersUrl" escapeAmp="false">
  <s:param name="taskId" value="%{taskId}" />
  <s:param name="tabId" value="%{tabId}" />
  <s:param name="dbId" value="%{dbId}" />
  <s:param name="questionId" value="%{questionId}" />
</s:url>
<s:if test="answerers.size() > 0">
  <div class="maincontents content-col">
    <table id="answerTable">
      <thead>
      <th>
        <a href="${exportAnswersUrl}" style="color: white">[<s:text name="quiz.header.exportAnswers" />]</a>
        <span style="color: white" onclick="selectElementContents()">[<s:text name="quiz.header.selectAll" />]</span>
      </th>
      <s:iterator var="contentElem" value="questions">
        <td>
          <s:if test="#contentElem.contentElementType != #SURVEY_ID">
            <div class="multipleQuiz form-group">
              <h4>
                ${contentElem.questionName}
              </h4>
              <fieldset>
                ${contentElem.questionTexts[0]}
              </fieldset>
              <s:if test="#contentElem.contentElementType == #MULTICHOICE_ID">
                <table>
                  <tr>
                    <td>
                      <ol class="bullet_${contentElem.numbering} quiz" style="padding-left: 1.2em;">
                        <s:if test="#contentElem.singleAnswer">
                          <s:iterator var="choice" value="#contentElem.choices" status="loop2">
                            <li class="form-group">
                              <label>${choice}<input type="radio" name="answers[${contentElem.questionId}][0]" value="<s:property value="#choice" />" ${inputStyle} /></label>
                            </li>
                          </s:iterator>
                        </s:if>
                        <s:else>
                          <s:iterator var="choice" value="#contentElem.choices" status="loop2">
                            <li>
                              <label>
                                <input type="checkbox" name="answers[${contentElem.questionId}][0]" value="<s:property value="#choice" />" ${inputStyle} />
                                ${choice}
                              </label>
                            </li>
                          </s:iterator>
                        </s:else>
                      </ol>
                    </td>
                  </tr>
                </table>
              </s:if>
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
                </td>
              </tr>
            </table>
          </s:else>
        </td>
      </s:iterator>
      </thead>
      <tbody>
        <s:iterator var="answerer" value="answerers" status="loop">
          <tr>
            <td>${answerer.lastName}, ${answerer.firstName}</td>
            <s:iterator var="contentElem" value="%{answers[#loop.index]}">
              <td>
                <s:if test="#contentElem == null">
                  <s:set var="boxStyle">highlightBox</s:set>
                </s:if>
                <s:else>
                  <s:if test="#contentElem.userAnswers != null">
                    <s:set var="boxStyle">highlightBox</s:set>
                  </s:if>
                  <s:else>
                    <s:set var="boxStyle">questionBox</s:set>
                  </s:else>
                  <div class="${boxStyle}">
                    <s:if test="#contentElem.contentElementType != #SURVEY_ID">
                      <div class="multipleQuiz form-group">
                        <fieldset>
                          <s:if test="#contentElem.contentElementType == #MULTICHOICE_ID">
                            <table>
                              <tr>
                                <td>
                                  <ul style="padding-left: 1.2em;">
                                    <s:iterator var="choice" value="#contentElem.choices" status="loop2">
                                      <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[0][#loop2.index] == 'true')">
                                        <li>${choice}</li>
                                        </s:if>
                                      </s:iterator>
                                  </ul>
                                </td>
                              </tr>
                            </table>
                          </s:if>
                          <s:elseif test="#contentElem.contentElementType == #ESSAY_ID">
                            <div class="contentBox">
                              <s:if test="#contentElem.userAnswers != null">
                                <s:property value="%{#contentElem.userAnswers[0][0]}" escapeHtml="false"/>
                              </s:if>
                            </div>
                          </s:elseif>
                          <s:elseif test="#contentElem.contentElementType == #PROGRAM_ID">
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
                                <td><input type="radio" name="${answerer.id}answers[${contentElem.questionId}][${loop2.index}]" value="<s:property value="#choice" />" ${inputStyle}
                                           <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[#loop2.index][#loop3.index] == 'true')">checked="checked"</s:if>/></td>
                                </s:iterator>
                              </s:if>
                              <s:else>
                                <s:iterator var="choice" value="#contentElem.choices" status="loop3">
                                <td><input type="checkbox" name="${answerer.id}answers[${contentElem.questionId}][${loop2.index}]" value="<s:property value="#choice" />" ${inputStyle}
                                           <s:if test="(#contentElem.userAnswers != null) && (#contentElem.userAnswers[#loop2.index][#loop3.index] == 'true')">checked="checked"</s:if>/></td>
                                </s:iterator>
                              </s:else>
                              <s:if test="(#contentElem.detail != null) && (!#contentElem.detail.isEmpty())">
                              <td class="RIGHT"><input type="text" name="answers[${contentElem.questionId}][${loop2.index}]" size="20" ${inputStyle} /></td>
                              </s:if>
                            <td class="RIGHT">
                            </td>
                          </tr>
                        </s:iterator>
                      </table>
                    </s:else>
                    <s:if test="(#contentElem.resultMark != null) || ((#contentElem.resultFeedbacks != null) && (#contentElem.resultFeedbacks.length > 0)) || (#contentElem.resultError != null)">
                      <div style="min-width: 50%" id="fbDiv${answerer.id}_${contentElem.questionId}">
                        <table style="width: 100%">
                          <thead>
                            <tr>
                              <th colspan="2">
                                ${SCORETXT}: ${contentElem.resultMark}
                                <s:if test="((#contentElem.resultFeedbacks != null) && (#contentElem.resultFeedbacks.length > 0)) || (#contentElem.resultError != null)">
                                  &nbsp;&nbsp;<span style="float: right" class="btn btn-primary-small"
                                                    onclick="$(this).closest('tr').next('tr').toggle()">
                                    Show/hide feedback
                                  </span>
                                </s:if>
                              </th>
                            </tr>
                            <s:if test="((#contentElem.resultFeedbacks != null) && (#contentElem.resultFeedbacks.length > 0)) || (#contentElem.resultError != null)">
                              <tr>
                                <td style="vertical-align:top" class="testCaseButtons">
                                  <s:if test="#contentElem.resultError != null">
                                    <button onclick="viewResTab(this, '#msg${answerer.id}_${contentElem.questionId}')" style="display: block; white-space: nowrap" class="linkButton">Message</button>
                                  </s:if>
                                  <s:if test="#contentElem.contentElementType == #PROGRAM_ID">
                                    <s:set var="RESBTNTXT">${TESTTXT}</s:set>
                                  </s:if>
                                  <s:else>
                                    <s:set var="RESBTNTXT">${CHOICETXT}</s:set>
                                  </s:else>
                                  <s:iterator var="fb" value="#contentElem.resultFeedbacks" status="idx">
                                    <s:if test="#contentElem.resultTestNos[#idx.index] != null">
                                      <s:if test="#contentElem.resultScores[#idx.index] > 0">
                                        <button onclick="viewResTab(this, '#res_${answerer.id}_${contentElem.questionId}_${idx.count}')" style="display: block; white-space: nowrap" class="linkButton">${RESBTNTXT}&nbsp;#${contentElem.resultTestNos[idx.index]}&nbsp;<span class="glyphicon glyphicon-ok-sign" style="color: green"></span></button>
                                        </s:if>
                                        <s:else>
                                        <button onclick="viewResTab(this, '#res_${answerer.id}_${contentElem.questionId}_${idx.count}')" style="display: block; white-space: nowrap" class="linkButton">${RESBTNTXT}&nbsp;#${contentElem.resultTestNos[idx.index]}&nbsp;<span class="glyphicon glyphicon-remove-sign" style="color: red"></span></button>
                                        </s:else>
                                      </s:if>
                                      <s:else>
                                        <s:if test="#contentElem.resultScores[#idx.index] > 0">
                                        <button onclick="viewResTab(this, '#res_${answerer.id}_${contentElem.questionId}_${idx.count}')" style="display: block; white-space: nowrap" class="linkButton">${RESBTNTXT}&nbsp;#${idx.count}&nbsp;<span class="glyphicon glyphicon-ok-sign" style="color: green"></span></button>
                                        </s:if>
                                        <s:else>
                                        <button onclick="viewResTab(this, '#res_${answerer.id}_${contentElem.questionId}_${idx.count}')" style="display: block; white-space: nowrap" class="linkButton">${RESBTNTXT}&nbsp;#${idx.count}&nbsp;<span class="glyphicon glyphicon-remove-sign" style="color: red"></span></button>
                                        </s:else>
                                      </s:else>
                                    </s:iterator>
                                </td>
                                <td style="width: 100%">
                                  <s:if test="#contentElem.resultError != null">
                                    <div id="msg${answerer.id}_${contentElem.questionId}" style="display: none">
                                      <s:if test="#contentElem.resultFullError != null">
                                        <s:url action="viewFeedback" var="viewErrorURL">
                                          <s:param name="taskId" value="taskId" />
                                          <s:param name="tabId" value="tabId" />
                                          <s:param name="dbId" value="dbId" />
                                          <s:param name="tagId" value="#contentElem.resultFullError" />
                                        </s:url>
                                        <s:a href="%{viewErrorURL}" target="_blank" cssClass="btn btn-default">
                                          <s:text name="submissions.header.fullFeedback" />
                                        </s:a>
                                      </s:if>
                                      ${contentElem.resultError}
                                    </div>
                                  </s:if>
                                  <s:iterator var="fb" value="#contentElem.resultFeedbacks" status="idx">
                                    <div id="res_${answerer.id}_${contentElem.questionId}_${idx.count}" style="display: none">
                                      <s:if test="#contentElem.resultFullFeedbacks[#idx.index] != null">
                                        <s:url action="viewFeedback" var="viewFeedbackURL">
                                          <s:param name="taskId" value="taskId" />
                                          <s:param name="tabId" value="tabId" />
                                          <s:param name="dbId" value="dbId" />
                                          <s:param name="tagId" value="#contentElem.resultFullFeedbacks[#idx.index]" />
                                        </s:url>
                                        <s:a href="%{viewFeedbackURL}" target="_blank" cssClass="btn btn-default">
                                          <s:text name="submissions.header.fullFeedback" />
                                        </s:a>
                                      </s:if>
                                      <pre class="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px">${fb}</pre>
                                    </div>
                                  </s:iterator>
                                </td>
                              </tr>
                            </s:if>
                          </thead>
                        </table>
                      </div>
                    </s:if>
                    <s:else>
                      <div style="display: none; min-width: 50%" id="fbDiv${answerer.id}_${contentElem.questionId}">
                      </div>
                    </s:else>
                  </div>
                  <s:if test="#contentElem.contentElementType == #ESSAY_ID && scoreStep != null">
                    <s:text name="general.header.grade" />:
                    <s:if test="#contentElem.resultMark != null">
                      <input type="number" name="newMarks" data-questionId="${contentElem.questionId}" data-answererId="${answerer.id}" step="${scoreStep}"
                             min="${minScore}" max="${maxScore}" value="${contentElem.resultMark}" />
                    </s:if>
                    <s:else>
                      <input type="number" name="newMarks" data-questionId="${contentElem.questionId}" data-answererId="${answerer.id}" step="${scoreStep}"
                             min="${minScore}" max="${maxScore}" value=""/>
                    </s:else>
                    <button type="button" class="btn btn-primary" onclick="saveQuizGrades()">
                      <s:text name="grading.header.saveAllGrades" />
                    </button>
                  </s:if>
                </s:else>
              </td>
            </s:iterator>
          </tr>
        </s:iterator>
      </tbody>
    </table>
  </div>
  <s:url action="relogin" var="reloginUrl" escapeAmp="false" />
  <s:url action="saveQuizGrades" var="saveQuizGradesUrl" escapeAmp="false">
    <s:param name="taskId" value="taskId" />
    <s:param name="tabId" value="tabId" />
    <s:param name="dbId" value="dbId" />
  </s:url>
  <div id="dummyDiv" style="display: none;"></div>
  <script>
    tinymce.init({
      selector: "#dummyDiv",
      inline: "true",
      menubar: false
    });

    function viewResTab(btn, idStr)
    {
      $(btn).siblings().each(function (i)
      {
        this.style.fontWeight = "normal";
      });
      btn.style.fontWeight = "bold";
      var showDiv = $(idStr);
      showDiv.siblings().hide();
      showDiv.show();
    }

    function selectElementContents() {
      var el = document.getElementById('answerTable');
      var body = document.body, range, sel;
      if (document.createRange && window.getSelection) {
        range = document.createRange();
        sel = window.getSelection();
        sel.removeAllRanges();
        try {
          range.selectNodeContents(el);
          sel.addRange(range);
        } catch (e) {
          range.selectNode(el);
          sel.addRange(range);
        }
      } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(el);
        range.select();
      }
    }

    function notifyUser(xhr)
    {
      if ((xhr.status == 200) || (xhr.status == 204)) {
        showInfoBarMessage('<div id="messages"><div class="message_header"><s:text name="grading.message.allGradesSaved" /></div></div>');
      } else if (xhr.status == 403)
      {
        reloginPopup(tinymce.get('dummyDiv'));
      } else
      {
        showInfoBarMessage('<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="general.error.system" /></span></li></ul></div></div>');
      }
    }

    var jqForm = null;
    var inputElMap = {};
    var gradeEls = null;

    function saveQuizGrades()
    {
      if (gradeEls == null)
      {
        gradeEls = document.getElementsByName("newMarks");
      }
      if (jqForm == null)
      {
        formEl = document.createElement("form");
        formEl.method = "POST";
        formEl.action = "";
        for (var i = 0; i < gradeEls.length; i++)
        {
          var markEl = gradeEls[i];
          var questionId = markEl.getAttribute("data-questionId");
          var answererId = markEl.getAttribute("data-answererId");
          var inputEl = document.createElement("input");
          inputEl.name = "questionMarks";
          inputEl.value = questionId + "_" + answererId + "_" + markEl.value;
          inputElMap[questionId + "_" + answererId] = inputEl;
          formEl.appendChild(inputEl);
        }
        document.body.appendChild(formEl);
        jqForm = $(formEl);
      } else
      {
        for (var i = 0; i < gradeEls.length; i++)
        {
          var markEl = gradeEls[i];
          var questionId = markEl.getAttribute("data-questionId");
          var answererId = markEl.getAttribute("data-answererId");
          var inputEl = inputElMap[questionId + "_" + answererId];
          inputEl.value = questionId + "_" + answererId + "_" + markEl.value;
        }
      }
      var formData = jqForm.serialize();
      var url = '${saveQuizGradesUrl}';
      $.post(url, formData).done(function (data, stat, xhr) {
        notifyUser(xhr);
      }).fail(function (xhr, stat, err) {
        notifyUser(xhr);
      });
    }

    colorDiffFeedback($(".diffCell"));
    $(function () {
      $(".testCaseButtons").each(function (i)
      {
        $(this).children().last().click();
      });
    });
  </script>
</s:if>