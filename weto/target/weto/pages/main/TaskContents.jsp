<%@ include file="/WEB-INF/taglibs.jsp"%>
<s:set var="HTML_ID">0</s:set>
<s:set var="MULTICHOICE_ID">1</s:set>
<s:set var="ESSAY_ID">2</s:set>
<s:set var="SURVEY_ID">3</s:set>
<s:set var="PROGRAM_ID">4</s:set>
<s:set var="SCORETXT"><s:text name="general.header.score" /></s:set>
<s:set var="FEEDBACKTXT"><s:text name="autograding.header.feedback" /></s:set>
<s:set var="TESTNOTXT"><s:text name="autograding.header.testNo" /></s:set>
<s:if test="navigator.teacher && pendingStudents">
  <div class="teacherTodo">
    <s:text name="pendingstudents.message.havePending" />
  </div>
</s:if>
<script src="/wetoextra/mathjax/MathJax.js?config=TeX-AMS-MML_HTMLorMML"></script>
<script type="text/x-mathjax-config">
  MathJax.Hub.Config({
  "HTML-CSS": {
  minScaleAdjust: 80
  }
  });
</script>
<s:if test="!quizOpen">
  <s:set var="inputStyle">disabled="disabled"</s:set>
</s:if>
<s:else>
  <script src="js/ace/ace.js" type="text/javascript" charset="utf-8"></script>
  <script src="js/tinymce4/tinymce.min.js"></script>
  <script src="js/grading.js"></script>
</s:else>
<s:if test="elements.size() > 0">
  <div class="maincontents content-col">
    <s:iterator var="contentElem" value="elements">
      <!-- Print all elements in content-col (contentElem + quiz) -->
      <s:if test="#contentElem.contentElementType == #HTML_ID">
        ${contentElem.html}
      </s:if>
      <s:else>
        <s:if test="#contentElem.userAnswers != null">
          <s:set var="boxStyle">highlightBox</s:set>
        </s:if>
        <s:else>
          <s:set var="boxStyle">questionBox</s:set>
        </s:else>
        <div class="${boxStyle}">
          <form action="#" class="quizAnswer" method="post" onsubmit="return false;">
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
                  ${contentElem.questionTexts[0]}
                  <hr>
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
                                   class="linkButton" onclick="submitAnswer(this, ${contentElem.questionId})"/>
                          </s:if>
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
                      <div class="answerBox">
                        <s:if test="#contentElem.userAnswers != null">
                          <s:property value="%{#contentElem.userAnswers[0][0]}" escapeHtml="false"/>
                        </s:if>
                      </div>
                    </s:else>
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
                      <input type="button" value="<s:text name="quiz.header.saveAnswer" />" class="linkButton" onclick="$('#feedbackDiv${contentElem.questionId}').html('').hide(); submitCode(this, ${contentElem.questionId})"/>
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
                             class="linkButton" onclick="submitAnswer(this, ${contentElem.questionId})"/>
                    </s:if>
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
          <s:if test="(#contentElem.resultMark != null) || (#contentElem.resultFeedback != null) || (#contentElem.resultError != null)">
            <hr>
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
      </s:else>
    </s:iterator>
  </div>
</s:if>
<s:if test="!task.getIsQuiz()">
  <s:if test="!subtasks.isEmpty()">
    <hr>
    <s:iterator var="subtask" value="subtasks" status="stat">
      <s:url action="viewTask" var="viewTaskUrl">
        <s:param name="taskId" value="#subtask.id" />
        <s:param name="tabId" value="#mainTabId" />
        <s:param name="dbId" value="dbId" />
      </s:url>
      <s:if test="#subtask.showTextInParent && (#subtask.text != null)">
        <div class  ="content-col">
          <h2 class ="showTextInParent">
            <s:a href="%{viewTaskUrl}">
              ${subtask.name}
            </s:a>
          </h2>
          ${subtask.text}
        </div>
      </s:if>
      <s:elseif test="#subtask.isHidden">
        <h3 class="subtask-list_hiddenFromStudents">
          <s:a href="%{viewTaskUrl}" class="hiddenFromStudents">
            ${subtask.name}
          </s:a>
        </h3>
      </s:elseif>
      <s:else>
        <h3 class ="subtask-list">
          <s:a href="%{viewTaskUrl}">
            ${subtask.name}
          </s:a>
        </h3>
      </s:else>
    </s:iterator>
  </s:if>
</s:if>
<s:if test="quizOpen">
  <div id="dummyDiv" style="display: none;"></div>
  <script>
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
      $.get('getNavigationTree.action?dbId=${dbId}&taskId=${taskId}&tabId=${tabId}').done(function (data, stat, xhr) {
        if (xhr.status == 200)
        {
          var older = document.getElementById("navtree").getAttribute("data-json");
          var newer = decodeURIComponent(jQuery("<div>" + data + "</div>").text());
          if (older != newer)
          {
            $("#navtree").empty();
            buildTreemenuUnder("#navtree", $.parseJSON(data), ${dbId}, ${taskId}, "viewTask.action");
          }
        }
      });
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
        showInfoBarMessage('<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span>' + err + '</span></li></ul></div></div>');
      });
    }

    var filterResubmitMap = {};

    function notifyUser(xhr, data, formEl, questionId)
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
        if (answerText.length)
        {
          formEl.closest("div").removeClass("questionBox").addClass("highlightBox");
        } else
        {
          formEl.closest("div").removeClass("highlightBox").addClass("questionBox");
        }
        showInfoBarMessage('<div id="messages"><div class="message_header"><s:text name="quiz.header.answerSuccess" /></div>'
                + escapeHtml(answerText) + '</div>');
        if (typeof MathJax != "undefined")
        {
          MathJax.Hub.Queue(["Typeset", MathJax.Hub, "messages"]);
        }
      } else if (xhr.status == 403)
      {
        reloginPopup(tinymce.get('dummyDiv'));
      } else
      {
        showInfoBarMessage('<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="quiz.message.answerError" /></span></li></ul></div></div>');
      }
      filterResubmitMap[questionId] = false;
    }

    function filterResubmit(questionId)
    {
      if (!filterResubmitMap[questionId])
      {
        filterResubmitMap[questionId] = true;
        return true;
      } else
      {
        showInfoBarMessage('<div id="errors"><div class="error_header">ERROR!</div><div class="error_message"><ul><li><span><s:text name="quiz.message.resubmitError" /></span></li></ul></div></div>');
        return false;
      }
    }

    function submitAnswer(btn, questionId)
    {
      console.log(btn)
      console.log(questionId)
      if (filterResubmit(questionId))
      {
        var formEl = $(btn).closest("form");
        var formData = formEl.serialize();
        console.log("form data: " + formData)
        var url = '<s:url action="saveQuizAnswer"/>';
        $.post(url, formData).done(function (data, stat, xhr) {
          notifyUser(xhr, data, formEl, questionId);
        }).fail(function (xhr, stat, err) {
          notifyUser(xhr, "", formEl, questionId);
        });
      }
    }

    function submitEssay(btn, essayId)
    {
      if (filterResubmit(essayId))
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
          notifyUser(xhr, data, formEl, essayId);
        }).fail(function (xhr, stat, err) {
          notifyUser(xhr, "", formEl, essayId);
        });
      }
    }

    function submitCode(btn, programId)
    {
      if (filterResubmit(programId))
      {
        var inputJQElement = $(btn).prevAll("input.programInput").first();
        var ansText = ace.edit("program" + programId).getSession().getValue();
        inputJQElement.val(ansText);
        var formEl = $(btn).closest("form");
        var formData = formEl.serialize();
        var url = '<s:url action="saveQuizAnswer"/>';
        $.post(url, formData).done(function (data, stat, xhr) {
          notifyUser(xhr, data, formEl, programId);
        }).fail(function (xhr, stat, err) {
          notifyUser(xhr, "", formEl, programId);
        });
      }
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

    $(function () {
      tinymce.init({
        selector: "div.essayText",
        inline: "true",
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
        if (prologueDiv.html().trim().length > 0)
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
        if (epilogueDiv.html().trim().length > 0)
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
