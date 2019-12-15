var SubmissionsList = React.createClass({

	getInitialState: function()
	{
		return({submissionsMap: {}, submissionErrors: {}, submissionStates: {}, errors:[]});
	},

	getDefaultProps: function()
	{
		return({});
	},

	componentDidUpdate: function(){
		this.initTableSorters();
		this.initTooltips();
	},

	componentDidMount: function()
	{
		this.initTableSorters();
		this.initTooltips();
	},

	componentWillMount: function()
	{
		this.getJSONSubmissions();
	},

	componentWillUpdate: function()
	{
		this.removeTooltips();
	},

	initTableSorters: function(){
		var comp = React.findDOMNode(this.refs['component']);
		jQuery(comp).find(".tablesorter").tablesorter();
	},

	initTooltips: function(){
		var comp = React.findDOMNode(this.refs['component']);
		jQuery(comp).find("[data-toggle='tooltip']").tooltip();
	},

	removeTooltips: function(){
		var comp = React.findDOMNode(this.refs['component']);
		jQuery(comp).find("[data-toggle='tooltip']").tooltip('destroy');
	},

	getJSONAutoGrading: function(submissionId)
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'getJSONAutoGrading.action',
			method: 'GET',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId},
			success: function(response)
			{
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

	getSingleJSONSubmission: function(submissionId, callBack)
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'getJSONSubmission.action',
			method: 'GET',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId},
			success: function(response)
			{
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
          }.bind(this), 500);
        }

				if (callBack)
				{
					callBack();
				}
			}.bind(this)
		});
	},

	getJSONSubmissions: function()
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'getJSONSubmissions.action',
			method: 'GET',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId},
			success: function(response)
			{
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
					hasAutoGrading: response.hasAutoGrading,
				});

        var sqm = document.getElementById('submissionQuotaMessage');
        if(sqm != null)
        {
          sqm.innerHTML = response.submissionQuotaMessage;
        }

				// Fetch documents
				this.setState({submissionsMap: {}});
				var receivedSubmissions = response.submissions;
				receivedSubmissions.forEach(function(receivedSubmission)
					{
						var submissionsMap = this.state.submissionsMap;
						var submissionId = receivedSubmission.id;
            // Fetch submission in processing again after a second
						if (receivedSubmission.status == 1)
						{
              setTimeout(function(){
                this.getSingleJSONSubmission(submissionId);
              }.bind(this), 1000);
						}
						submissionsMap[submissionId] = receivedSubmission;
						this.setState({submissionsMap: submissionsMap});
						this.getJSONDocuments(submissionId);
						if ((receivedSubmission.status != 1) && (this.state.hasAutoGrading == true))
						{
							setTimeout(function () {
                this.getJSONAutoGrading(submissionId);
              }.bind(this), 500);
						}
					}.bind(this)
				);
			}.bind(this)
		});
	},

	getJSONDocuments: function(submissionId)
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'getJSONDocuments.action',
			method: 'GET',
			data: {'submissionId': submissionId, 'dbId': dbId, 'taskId': taskId, 'tabId': tabId},
			success: function(response)
			{
				this.checkForErrors(response);
				submissionsMap = this.state.submissionsMap;
				submissionsMap[submissionId].documents = response.documents;
				this.setState({submissionsMap: submissionsMap});
			}.bind(this)
		});
	},

	refreshAll: function()
	{
		this.getJSONSubmissions();
	},

    // Check if excluded files should be printed from the response.
    checkForExcludedFiles: function(response)
    {
		if (response.excludedFiles && response.excludedFiles.length > 0)
		{
			this.setState({excludedFiles: response.excludedFiles});
		}
    },

    resetExcludedFiles: function(submissionId)
    {
		this.setState({excludedFiles: []});
    },

    checkForErrors: function(response)
    {
    	if (response.actionErrors && response.actionErrors.length > 0)
    	{
    		var oldErrors = this.state.errors;
    		response.actionErrors.forEach(function(error){
    			oldErrors.unshift(error);
    		});
    		this.setState({errors: oldErrors});
    	}
    },

    resetErrors: function()
    {
    	var empty = [];
    	this.setState({errors: empty});
    },

	handleCreateFileSubmit: function(fileName, submissionId)
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'addSubmissionFile.action',
			method: 'POST',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId, 'documentFileFileName': fileName},
			success: function(response)
			{
				this.checkForErrors(response);
				// Refresh everything if the submission wasn't in "Not Submitted" state
				if (this.state.submissionsMap[submissionId].status == 2)
				{
					this.getSingleJSONSubmission(submissionId, function(){
						this.getJSONDocuments(submissionId);
					}.bind(this));
				}
				else
				{
					this.getJSONDocuments(submissionId);
				}
				this.checkForExcludedFiles(response);
			}.bind(this)
		});
	},

	handleFileChange: function(submissionId, callBack)
	{
		var status = this.state.submissionsMap[submissionId].status;
		if (status == 2 || status == 3)
		{
			this.getSingleJSONSubmission(submissionId, function(){
				this.getJSONDocuments(submissionId);
			}.bind(this));
		}
		else
		{
			this.getJSONDocuments(submissionId);
		}

		if (callBack)
		{
			callBack();
		}
	},

	handleSubmissionAdded: function()
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;
		var submitterId = this.props.submitterId;

		jQuery.ajax({
			url: 'createSubmission.action',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submitterId': submitterId},
			method: 'POST',
			success: function(response)
			{
				this.checkForErrors(response);
				this.refreshAll();
			}.bind(this)
		});
	},

	handleSubmitButtonClicked: function(submissionId)
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'completeSubmission.action',
			method: 'POST',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId},
			success: function(response)
			{
				this.checkForErrors(response);
				this.resetExcludedFiles(submissionId);
				this.getSingleJSONSubmission(submissionId);
			}.bind(this)
		});
	},

	handleTestRunButtonClicked: function(submissionId)
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;

		jQuery.ajax({
			url: 'completeSubmission.action',
			method: 'POST',
			data: {'dbId': dbId, 'taskId':taskId, 'tabId': tabId, 'submissionId': submissionId, 'isTestRun': true, 'testNumber': $('#testNumber' + submissionId).val()},
			success: function(response)
			{
				this.checkForErrors(response);
				this.getSingleJSONSubmission(submissionId);
			}.bind(this)
		});
	},

	toggleDeleteConfirmBox: function(refName)
	{
		var toggleElem = React.findDOMNode(this.refs[refName]);
		var jQEleme = jQuery(toggleElem);

	   	var state = jQEleme.css("display");

		if (state == "none")
		{
		    jQEleme.show();
		}
		else
		{
		    jQEleme.hide();
		}
	},

	confirmDeleteSubmission: function(submissionId, refName)
	{
		this.toggleDeleteConfirmBox(refName);

		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;
		jQuery.ajax({
			url: 'ajaxDeleteSubmissionConfirm.action',
			method: 'POST',
			data: {'dbId': dbId, 'taskId': taskId, 'tabId': tabId, 'submissionId': submissionId, 'submitted': true},
			success: function(response)
			{
				this.checkForErrors(response);
				this.refreshAll();
			}.bind(this)
		});
	},

	render: function()
	{
		var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;
		var updateable = this.props.submissionPeriodActive;

		// Submissions must be sorted based on timestamp, since
		// a) id doesn't necessarily represent the order
		// b) even if it did, key => value -pairs might be in any order
		var orderedIds = [];
		jQuery.each(this.state.submissionsMap, function(key, value)
		{
			var object = {timeStamp: value.timeStamp, submissionId: key};
			orderedIds.push(object);
		});
		orderedIds.sort(function(a, b){
			// Descending order: latest timestamp first
			return (b.timeStamp-a.timeStamp);
		});

		// The render loop itself
		var submissions = [];
		for (var i = 0; i < orderedIds.length && i < this.props.submissionsLimit; i++)
		{
			var renders = [];
			var submissionId = orderedIds[i].submissionId;
			var submission = this.state.submissionsMap[submissionId];
			var documents = submission.documents;
			var title = i == 0 ? this.props.current : this.props.previous+' '+i;

			// Deletebutton gets rendered always.
			// Each 'confirmBox' needs a ref of their own, since they are used in jQuery.
			var ref = 'confirmBox'+submissionId;
			if (updateable == true)
			{
				var deleteButton =  <div className='submission-deleteButtonContainer buttonConfirmContainer'>
										<button
											className='btn btn-default submission-deleteButton'
											onClick={this.toggleDeleteConfirmBox.bind(this, ref)}>
											<span className='glyphicon glyphicon-remove'></span>
										</button>

										<div className='confirmBox' ref={ref}>
											<div className='form-group'>
												<p>{this.props.confirmDeleteText}</p>
												<button
													className='btn btn-danger'
													onClick={this.confirmDeleteSubmission.bind(this, submissionId, ref)}>
													{this.props.deleteVerb}
												</button>
											</div>
											<div className='form-group'>
												<button
													className='btn btn-default'
													onClick={this.toggleDeleteConfirmBox.bind(this, ref)}>
													{this.props.cancelText}
												</button>
											</div>
										</div>
									</div>;
				renders.push(deleteButton);
			}

			// Title row and document table also get rendered always
			var submissionTitle = <SubmissionTitle
									title={title} timeStamp={submission.timeStampString}
									statusNumber={submission.status} statusText={this.state.submissionStates[submission.status]}
									errorText={this.state.submissionErrors[submission.error]} index={i} />;
			renders.push(submissionTitle);

			var documentTable =  <DocumentTable
									tabId={tabId} taskId={taskId}
									header={this.props.documentTableHeader}
									dbId={dbId} documents={documents}
									submissionId={submissionId} updateable={updateable} />;
			renders.push(documentTable);

			// Only print file manipulation and some other parts for the most recent submission
			if (updateable == true)
			{
				if (i == 0)
				{
					var dropZone = <SubmissionDropZone
										tabId={tabId} taskId={taskId}
										dbId={dbId}	submissionId={submissionId}
										allowZipping={this.state.allowZipping}
										zippingInstructions={this.props.zippingInstructions}
										header={this.props.dropzoneHeader} onFileChange={this.handleFileChange}
										checkForExcludedFiles={this.checkForExcludedFiles}
										checkForErrors={this.checkForErrors}
										filePatterns={this.state.patternDescriptions} />;
					renders.push(dropZone);
					if (this.state.allowInlineFiles == true)
          {
            var createForm = <SubmissionCreateFileForm
                      tabId={tabId} taskId={taskId}
                      dbId={dbId} submissionId={submissionId}
                        verb={this.props.createVerb} header={this.props.createHeader}
                        onCreateFileSubmit={this.handleCreateFileSubmit}
                        filePatterns={this.state.patternDescriptions} />;
            renders.push(createForm);
          }
        }
				// Auto grade total score
				if (submission.autoGradeMark)
				{
					var autoGradeMark = <div className='panel submission-autoGradeMark'>
											<h5>{this.props.autoGradeMarkHeader}</h5>
											<p>{submission.autoGradeMark}</p>
										</div>;
					renders.push(autoGradeMark);
				}
			}

			// Error field
			if (submission.error)
			{
				var error = <div className='panel submission-error'>
								<h5>{this.props.errorHeader}</h5>
					 			<p>{this.state.submissionErrors[submission.error]}</p>
					 		</div>;
				renders.push(error);
			}
			// Compiler message
			if (submission.message)
			{
				var output = [];
				if (submission.compilerResultId){
					var url = 'viewFeedback.action?taskId='+taskId+'&tabId='+tabId+'&dbId='+dbId+'&submissionId='+submissionId+'&tagId='+submission.compilerResultId;
					output = <pre className='submission-compilerOutputText' ><a href={url} target='_blank' dangerouslySetInnerHTML={{__html: submission.message}}></a></pre>;
				}
				else{
					output = <pre className='submission-compilerOutputText' dangerouslySetInnerHTML={{__html: submission.message}}></pre>
				}

				var compilerOutput = <div className='panel submission-compilerOutput'>
										<h5>{this.props.compilerOutputHeader}</h5>
										{output}
									</div>;
				renders.push(compilerOutput);
			}
			// Test scores as a table
			if (submission.testScores && submission.testScores.length > 0)
			{
				var testScoreTable = <TestScoreTable className='submission-scoreTable' taskId={taskId} tabId={tabId} dbId={dbId} submissionId={submissionId} testScores={submission.testScores}
										fullFeedbackIds={submission.fullFeedbackIds} />
				renders.push(testScoreTable);
			}

			if (updateable == true)
			{
				var submitButton = [];
				// Only offer submission completion button when it has at least one file
				// and when it is in state "Not Submitted"
				if (i == 0 && documents && documents.length > 0 && ((submission.status == 0) || (submission.status == 3)))
				{
					submitButton = 	<span className='submission-submitButtonContainer'>
											<button
												className='btn btn-primary submission-submitButton'
												onClick={this.handleSubmitButtonClicked.bind(this, submissionId)}>
												{this.props.submitButtonText}
											</button>
										</span>;
          // Button for testrun
          if (this.state.allowTestRun == true || this.state.allowTestRun == 'true')
          {
            var testRunButton = <span className='submission-testRunButtonContainer'>
                      <button
                        className='btn btn-default submissions-testRunButton'
                        onClick={this.handleTestRunButtonClicked.bind(this, submissionId)} >
                        {this.props.testRunButtonText}
                      </button>
                      <span>Test #:</span>
                      <input type="text" name="testNumber{submissionId}" size="3" style="margin: 0 10px 0 3px"/>
                    </span>;
            renders.push(testRunButton);
          }
				}
				else
				{
					submitButton = 	<div className='submission-submitButtonContainer'>
											<button disabled
												className='btn btn-default submission-submitButton'>
												{this.props.submitButtonText}
											</button>
										</div>;
				}
				renders.push(submitButton);
			}

			var submissionRender = (<div className='submission content-col row'>{renders}</div>);

			submissions.push(submissionRender);
		}

		// Create a button for new submissions if submissionPeriod is active
		var newSubmissionsButton = [];
		if (this.props.submissionPeriodActive == true || this.props.submissionPeriodActive == 'true')
		{
			var submission;
      var notIncomplete = true;
			if (orderedIds.length > 0)
			{
				submission = this.state.submissionsMap[orderedIds[0].submissionId];
        notIncomplete = ((submission.status == 3) || (submission.status == 2));
			}

			// Only enable submission creation when the latest submission is in state "accepted" or "not accepted"
			if ((orderedIds.length <= this.state.oldSubmissionLimit) && notIncomplete)
			{
				newSubmissionsButton = <button
											className='btn btn-primary submission-createSubmissionButton'
											onClick = {this.handleSubmissionAdded}>
											Create new submission
										</button>;
			}
		}
		// Print errors
		var errorRender = [];
		if (this.state.errors && this.state.errors.length > 0)
		{
			var listItems = [];
			this.state.errors.forEach(function(error){
				listItems.push(<li>{error}</li>);
			});
			errorRender = <div className='submissions-list-errors panel'
								 onClick={this.resetErrors} >
									<h5>{this.props.errorMessageHeader}</h5>
									<ul>
										{listItems}
									</ul>
								</div>;
		}

    // Possible file exclusion message
    var exclFileList = [];
    if (this.state.excludedFiles && this.state.excludedFiles.length > 0)
    {
       var exclFileListItems = [];
       this.state.excludedFiles.forEach(function(fileName){
          exclFileListItems.push(<li>{fileName}</li>);
       });

       exclFileList =   <div className='submission-list-excludedFiles panel'
                   onClick={this.resetExcludedFiles}>
                               <h5>{this.props.fileNamesNotAllowedHeader}</h5>
                               <ul className='submission-excludedFiles-list'>
                                  {exclFileListItems}
                               </ul>
                            </div>;
    }

		return(<div className='submissions-list' ref='component'>
					{newSubmissionsButton}
					<div title="Click to close" data-toggle='tooltip' data-placement='bottom'>
						{errorRender}
					</div>
					<div title="Click to close" data-toggle='tooltip' data-placement='bottom'>
						{exclFileList}
					</div>
					{submissions}
				</div>);
	}
});

