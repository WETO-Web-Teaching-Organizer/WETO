var SubmissionsList = React.createClass({
  displayName: 'SubmissionsList',
  getInitialState: function getInitialState() {
    return {submissionsMap: {}, submissionErrors: {}, submissionStates: {}, errors: []};
  },
  getDefaultProps: function getDefaultProps() {
    return {};
  },
  componentDidUpdate: function componentDidUpdate() {
    this.initTableSorters();
    this.initTooltips();
    colorDiffFeedback($(".diffCell"));
  },
  componentDidMount: function componentDidMount() {
    this.initTableSorters();
    this.initTooltips();
    colorDiffFeedback($(".diffCell"));
  },
  componentWillMount: function componentWillMount() {
    this.getJSONSubmissions();
  },
  componentWillUpdate: function componentWillUpdate() {
    this.removeTooltips();
  },
  initTableSorters: function initTableSorters() {
    var comp = React.findDOMNode(this.refs['component']);
    jQuery(comp).find(".tablesorter").tablesorter();
  },
  initTooltips: function initTooltips() {
    var comp = React.findDOMNode(this.refs['component']);
    jQuery(comp).find("[data-toggle='tooltip']").tooltip();
  },
  removeTooltips: function removeTooltips() {
    var comp = React.findDOMNode(this.refs['component']);
    jQuery(comp).find("[data-toggle='tooltip']").tooltip('destroy');
  },
  getJSONAutoGrading: function getJSONAutoGrading(submissionId) {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'getJSONAutoGrading.action',
      method: 'GET',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId},
      success: function (response) {
        this.checkForErrors(response);
        var submissionsMap = this.state.submissionsMap;
        var targetSubmission = submissionsMap[submissionId];

        targetSubmission.compilerResultId = response.compilerResultId;
        targetSubmission.fullFeedbackIds = response.fullFeedbackIds;
        targetSubmission.testScores = response.testScores;

        submissionsMap[submissionId] = targetSubmission;
        this.setState({submissionsMap: submissionsMap});
      }.bind(this)
    });
  },
  getSingleJSONSubmission: function getSingleJSONSubmission(submissionId, callBack) {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'getJSONSubmission.action',
      method: 'GET',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId},
      success: function (response) {
        this.checkForErrors(response);

        var updatedSubmission = response.submission;
        var submissionsMap = this.state.submissionsMap;
        var oldDocuments = submissionsMap[submissionId].documents;
        var oldExcludedFiles = submissionsMap[submissionId].excludedFiles;

        updatedSubmission.excludedFiles = oldExcludedFiles;
        updatedSubmission.documents = oldDocuments;
        submissionsMap[submissionId] = updatedSubmission;
        this.setState({submissionsMap: submissionsMap});

        // If state is "processing", start listening for updates
        if (updatedSubmission.status == 1) {
          setTimeout(function () {
            this.getSingleJSONSubmission(submissionId);
          }.bind(this), 1000);
        } else {
          setTimeout(function () {
            this.getJSONAutoGrading(submissionId);
            $.get('getNavigationTree.action?taskId=' + taskId + '&tabId=' + tabId + '&dbId=' + dbId).done(function (data, stat, xhr) {
              if (xhr.status == 200)
              {
                var older = document.getElementById("navtree").getAttribute("data-json");
                var newer = decodeURIComponent(jQuery("<div>" + data + "</div>").text());
                if (older != newer)
                {
                  $("#navtree").empty();
                  buildTreemenuUnder("#navtree", $.parseJSON(data), dbId, taskId, "viewTask.action");
                }
              }
            });
          }.bind(this), 500);
        }

        if (callBack) {
          callBack();
        }
      }.bind(this)
    });
  },
  getJSONSubmissions: function getJSONSubmissions() {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'getJSONSubmissions.action',
      method: 'GET',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId},
      success: function (response) {
        this.checkForErrors(response);

        // Set options
        this.setState({
          submissionErrors: response.submissionErrors,
          submissionStates: response.submissionStates,
          allowInlineFiles: response.allowInlineFiles,
          allowTestRun: response.allowTestRun,
          oldSubmissionLimit: response.oldSubmissionLimit,
          submissionQuotaMessage: response.submissionQuotaMessage,
          allowZipping: response.allowZipping,
          filePatterns: response.filePatterns,
          patternDescriptions: response.patternDescriptions,
          hasAutoGrading: response.hasAutoGrading
        });

        var sqm = document.getElementById('submissionQuotaMessage');
        if (sqm != null)
        {
          sqm.innerHTML = response.submissionQuotaMessage;
        }

        // Fetch documents
        this.setState({submissionsMap: {}});
        var receivedSubmissions = response.submissions;
        receivedSubmissions.forEach(function (receivedSubmission) {
          var submissionsMap = this.state.submissionsMap;
          var submissionId = receivedSubmission.id;
          // Fetch submission in processing again after a second
          if (receivedSubmission.status == 1) {
            setTimeout(function () {
              this.getSingleJSONSubmission(submissionId);
            }.bind(this), 1000);
          }
          submissionsMap[submissionId] = receivedSubmission;
          this.setState({submissionsMap: submissionsMap});
          this.getJSONDocuments(submissionId);
          if (receivedSubmission.status != 1 && this.state.hasAutoGrading == true) {
            setTimeout(function () {
              this.getJSONAutoGrading(submissionId);
            }.bind(this), 500);
          }
        }.bind(this));
      }.bind(this)
    });
  },
  getJSONDocuments: function getJSONDocuments(submissionId) {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'getJSONDocuments.action',
      method: 'GET',
      data: {'submissionId': submissionId, 'dbId': dbId, 'taskId': taskId, 'tabId': tabId},
      success: function (response) {
        this.checkForErrors(response);
        submissionsMap = this.state.submissionsMap;
        submissionsMap[submissionId].documents = response.documents;
        this.setState({submissionsMap: submissionsMap});
      }.bind(this)
    });
  },
  refreshAll: function refreshAll() {
    this.getJSONSubmissions();
  },
  // Check if excluded files should be printed from the response.
  checkForExcludedFiles: function checkForExcludedFiles(response) {
    if (response.excludedFiles && response.excludedFiles.length > 0) {
      this.setState({excludedFiles: response.excludedFiles});
    }
  },
  resetExcludedFiles: function resetExcludedFiles(submissionId) {
    this.setState({excludedFiles: []});
  },
  checkForErrors: function checkForErrors(response) {
    if (response.actionErrors && response.actionErrors.length > 0) {
      var oldErrors = this.state.errors;
      response.actionErrors.forEach(function (error) {
        oldErrors.unshift(error);
      });
      this.setState({errors: oldErrors});
    }
  },
  resetErrors: function resetErrors() {
    var empty = [];
    this.setState({errors: empty});
  },
  handleCreateFileSubmit: function handleCreateFileSubmit(fileName, submissionId) {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'addSubmissionFile.action',
      method: 'POST',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId, 'documentFileFileName': fileName},
      success: function (response) {
        this.checkForErrors(response);
        // Refresh everything if the submission wasn't in "Not Submitted" state
        if (this.state.submissionsMap[submissionId].status == 2) {
          this.getSingleJSONSubmission(submissionId, function () {
            this.getJSONDocuments(submissionId);
          }.bind(this));
        } else {
          this.getJSONDocuments(submissionId);
        }
        this.checkForExcludedFiles(response);
      }.bind(this)
    });
  },
  handleFileChange: function handleFileChange(submissionId, callBack) {
    var status = this.state.submissionsMap[submissionId].status;
    if (status == 2 || status == 3) {
      this.getSingleJSONSubmission(submissionId, function () {
        this.getJSONDocuments(submissionId);
      }.bind(this));
    } else {
      this.getJSONDocuments(submissionId);
    }

    if (callBack) {
      callBack();
    }
  },
  handleSubmissionAdded: function handleSubmissionAdded() {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;
    var submitterId = this.props.submitterId;

    jQuery.ajax({
      url: 'createSubmission.action',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submitterId': submitterId},
      method: 'POST',
      success: function (response) {
        this.checkForErrors(response);
        this.refreshAll();
      }.bind(this)
    });
  },
  handleSubmitButtonClicked: function handleSubmitButtonClicked(submissionId) {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'completeSubmission.action',
      method: 'POST',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId},
      success: function (response) {
        this.checkForErrors(response);
        this.resetExcludedFiles(submissionId);
        this.getSingleJSONSubmission(submissionId);
      }.bind(this)
    });
  },
  handleTestRunButtonClicked: function handleTestRunButtonClicked(submissionId) {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;

    jQuery.ajax({
      url: 'completeSubmission.action',
      method: 'POST',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId, 'isTestRun': true, 'testNumber': $('#testNumber' + submissionId).val()},
      success: function (response) {
        this.checkForErrors(response);
        this.getSingleJSONSubmission(submissionId);
      }.bind(this)
    });
  },
  toggleDeleteConfirmBox: function toggleDeleteConfirmBox(refName) {
    var toggleElem = React.findDOMNode(this.refs[refName]);
    var jQEleme = jQuery(toggleElem);

    var state = jQEleme.css("display");

    if (state == "none") {
      jQEleme.show();
    } else {
      jQEleme.hide();
    }
  },
  confirmDeleteSubmission: function confirmDeleteSubmission(submissionId, refName) {
    this.toggleDeleteConfirmBox(refName);

    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;
    jQuery.ajax({
      url: 'ajaxDeleteSubmissionConfirm.action',
      method: 'POST',
      data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId, 'submitted': true},
      success: function (response) {
        this.checkForErrors(response);
        this.refreshAll();
      }.bind(this)
    });
  },
  render: function render() {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;
    var updateable = this.props.submissionPeriodActive;

    // Submissions must be sorted based on timestamp, since
    // a) id doesn't necessarily represent the order
    // b) even if it did, key => value -pairs might be in any order
    var orderedIds = [];
    jQuery.each(this.state.submissionsMap, function (key, value) {
      var object = {timeStamp: value.timeStamp, submissionId: key};
      orderedIds.push(object);
    });
    orderedIds.sort(function (a, b) {
      // Descending order: latest timestamp first
      return b.timeStamp - a.timeStamp;
    });

    // The render loop itself
    var submissions = [];
    for (var i = 0; i < orderedIds.length && i < this.props.submissionsLimit; i++) {
      var renders = [];
      var submissionId = orderedIds[i].submissionId;
      var submission = this.state.submissionsMap[submissionId];
      var documents = submission.documents;
      var title = (i == 0) ? this.props.current : this.props.previous + ' ' + i;

      // Deletebutton gets rendered always.
      // Each 'confirmBox' needs a ref of their own, since they are used in jQuery.
      var ref = 'confirmBox' + submissionId;
      if (updateable == true) {
        var deleteButton = React.createElement(
                'div',
                {className: 'submission-deleteButtonContainer buttonConfirmContainer'},
                React.createElement(
                        'button',
                        {
                          className: 'btn btn-default submission-deleteButton',
                          onClick: this.toggleDeleteConfirmBox.bind(this, ref)},
                        React.createElement('span', {className: 'glyphicon glyphicon-remove'})
                        ),
                React.createElement(
                        'div',
                        {className: 'confirmBox', ref: ref},
                        React.createElement(
                                'div',
                                {className: 'form-group'},
                                React.createElement(
                                        'p',
                                        null,
                                        this.props.confirmDeleteText
                                        ),
                                React.createElement(
                                        'button',
                                        {
                                          className: 'btn btn-danger',
                                          onClick: this.confirmDeleteSubmission.bind(this, submissionId, ref)},
                                        this.props.deleteVerb
                                        )
                                ),
                        React.createElement(
                                'div',
                                {className: 'form-group'},
                                React.createElement(
                                        'button',
                                        {
                                          className: 'btn btn-default',
                                          onClick: this.toggleDeleteConfirmBox.bind(this, ref)},
                                        this.props.cancelText
                                        )
                                )
                        )
                );
        renders.push(deleteButton);
      }

      var statusText = (submission.status == 1) ? this.state.submissionStates[submission.status] + '(queue pos: ' + submission.queuePos + ')' : this.state.submissionStates[submission.status];
      // Title row and document table also get rendered always
      var submissionTitle = React.createElement(SubmissionTitle, {
        title: title, timeStamp: submission.timeStampString,
        statusNumber: submission.status, statusText: statusText,
        errorText: this.state.submissionErrors[submission.error], index: i});
      renders.push(submissionTitle);

      var documentTable = React.createElement(DocumentTable, {
        tabId: tabId, taskId: taskId,
        header: this.props.documentTableHeader,
        dbId: dbId, documents: documents,
        submissionId: submissionId, updateable: updateable});
      renders.push(documentTable);

      // Only print file manipulation and some other parts for the most recent submission
      if (updateable == true) {
        if (i == 0) {
          var dropZone = React.createElement(SubmissionDropZone, {
            tabId: tabId, taskId: taskId,
            dbId: dbId, submissionId: submissionId,
            allowZipping: this.state.allowZipping,
            zippingInstructions: this.props.zippingInstructions,
            header: this.props.dropzoneHeader, onFileChange: this.handleFileChange,
            checkForExcludedFiles: this.checkForExcludedFiles,
            checkForErrors: this.checkForErrors,
            filePatterns: this.state.patternDescriptions});
          renders.push(dropZone);
          if (this.state.allowInlineFiles == true) {
            var createForm = React.createElement(SubmissionCreateFileForm, {
              tabId: tabId, taskId: taskId,
              dbId: dbId, submissionId: submissionId,
              verb: this.props.createVerb, header: this.props.createHeader,
              onCreateFileSubmit: this.handleCreateFileSubmit,
              filePatterns: this.state.patternDescriptions});
            renders.push(createForm);
          }
        }
        // Auto grade total score
        if (submission.autoGradeMark) {
          var autoGradeMark = React.createElement(
                  'div',
                  {className: 'panel submission-autoGradeMark'},
                  React.createElement(
                          'h5',
                          null,
                          this.props.autoGradeMarkHeader
                          ),
                  React.createElement(
                          'p',
                          null,
                          submission.autoGradeMark
                          )
                  );
          renders.push(autoGradeMark);
        }
      }

      // Error field
      if (submission.error) {
        var error = React.createElement(
                'div',
                {className: 'panel submission-error'},
                React.createElement(
                        'h5',
                        null,
                        this.props.errorHeader
                        ),
                React.createElement(
                        'p',
                        null,
                        this.state.submissionErrors[submission.error]
                        )
                );
        renders.push(error);
      }
      // Compiler message
      if (submission.message) {
        var output = [];
        if (submission.compilerResultId) {
          var url = 'viewFeedback.action?taskId=' + taskId + '&tabId=' + tabId + '&dbId=' + dbId + '&submissionId=' + submissionId + '&tagId=' + submission.compilerResultId;
          output = React.createElement(
                  'pre',
                  {className: 'submission-compilerOutputText'},
                  React.createElement('a', {href: url, target: "_blank", dangerouslySetInnerHTML: {__html: submission.message}})
                  );
        } else {
          output = React.createElement('pre', {className: 'submission-compilerOutputText', dangerouslySetInnerHTML: {__html: submission.message}});
        }

        var compilerOutput = React.createElement(
                'div',
                {className: 'panel submission-compilerOutput'},
                React.createElement(
                        'h5',
                        null,
                        this.props.compilerOutputHeader
                        ),
                output
                );
        renders.push(compilerOutput);
      }
      // Test scores as a table
      if (submission.testScores && submission.testScores.length > 0) {
        var testScoreTable = React.createElement(TestScoreTable, {className: 'submission-scoreTable', taskId: taskId, tabId: tabId, dbId: dbId, submissionId: submissionId, testScores: submission.testScores,
          fullFeedbackIds: submission.fullFeedbackIds});
        renders.push(testScoreTable);
      }

      if (updateable == true) {
        var submitButton = [];
        // Only offer submission completion button when it has at least one file
        // and when it is in state "Not Submitted" or "Not accepted"
        if (i == 0 && documents && documents.length > 0 && ((submission.status == 0) || (submission.status == 3))) {
          submitButton = React.createElement(
                  'span',
                  {className: 'submission-submitButtonContainer'},
                  React.createElement(
                          'button',
                          {
                            className: 'btn btn-primary submission-submitButton',
                            onClick: this.handleSubmitButtonClicked.bind(this, submissionId)
                          },
                          this.props.submitButtonText
                          )
                  );
          // Button for testrun
          if (this.state.allowTestRun == true || this.state.allowTestRun == 'true') {
            var testRunButton = React.createElement(
                    'span',
                    {className: 'submission-testRunButtonContainer'},
                    React.createElement(
                            'button',
                            {
                              className: 'btn btn-default submissions-testRunButton',
                              onClick: this.handleTestRunButtonClicked.bind(this, submissionId)
                            },
                            this.props.testRunButtonText
                            ),
                    React.createElement('span', null, 'Test #:'),
                    React.createElement(
                            'input',
                            {
                              type: 'text',
                              style: {'margin': '0 10px 0 3px'},
                              id: 'testNumber' + submissionId,
                              size: '3'
                            })
                    );
            renders.push(testRunButton);
          }
        } else {
          submitButton = React.createElement(
                  'div',
                  {className: 'submission-submitButtonContainer'},
                  React.createElement(
                          'button',
                          {disabled: true,
                            className: 'btn btn-default submission-submitButton'},
                          this.props.submitButtonText
                          )
                  );
        }
        renders.push(submitButton);
      }

      var submissionRender = React.createElement(
              'div',
              {className: 'submission content-col row'},
              renders
              );

      submissions.push(submissionRender);
    }

    // Create a button for new submissions if submissionPeriod is active
    var newSubmissionsButton = [];
    if (this.props.submissionPeriodActive == true || this.props.submissionPeriodActive == 'true') {
      var submission;
      var notIncomplete = true;
      if (orderedIds.length > 0) {
        submission = this.state.submissionsMap[orderedIds[0].submissionId];
        notIncomplete = ((submission.status == 3) || (submission.status == 2));
      }

      // Only enable submission creation when the latest submission is in state "accepted" or "not accepted"
      if ((orderedIds.length <= this.state.oldSubmissionLimit) && notIncomplete) {
        newSubmissionsButton = React.createElement(
                'button',
                {
                  className: 'btn btn-primary submission-createSubmissionButton',
                  onClick: this.handleSubmissionAdded},
                'Create new submission'
                );
      }
    }
    // Print errors
    var errorRender = [];
    if (this.state.errors && this.state.errors.length > 0) {
      var listItems = [];
      this.state.errors.forEach(function (error) {
        listItems.push(React.createElement(
                'li',
                null,
                error
                ));
      });
      errorRender = React.createElement(
              'div',
              {className: 'submissions-list-errors panel',
                onClick: this.resetErrors},
              React.createElement(
                      'h5',
                      null,
                      this.props.errorMessageHeader
                      ),
              React.createElement(
                      'ul',
                      null,
                      listItems
                      )
              );
    }

    // Possible file exclusion message
    var exclFileList = [];
    if (this.state.excludedFiles && this.state.excludedFiles.length > 0) {
      var exclFileListItems = [];
      this.state.excludedFiles.forEach(function (fileName) {
        exclFileListItems.push(React.createElement(
                'li',
                null,
                fileName
                ));
      });

      exclFileList = React.createElement(
              'div',
              {className: 'submission-list-excludedFiles panel',
                onClick: this.resetExcludedFiles},
              React.createElement(
                      'h5',
                      null,
                      this.props.fileNamesNotAllowedHeader
                      ),
              React.createElement(
                      'ul',
                      {className: 'submission-excludedFiles-list'},
                      exclFileListItems
                      )
              );
    }

    return React.createElement(
            'div',
            {className: 'submissions-list', ref: 'component'},
            newSubmissionsButton,
            React.createElement(
                    'div',
                    {title: 'Click to close', 'data-toggle': 'tooltip', 'data-placement': 'bottom'},
                    errorRender
                    ),
            React.createElement(
                    'div',
                    {title: 'Click to close', 'data-toggle': 'tooltip', 'data-placement': 'bottom'},
                    exclFileList
                    ),
            submissions
            );
  }
});