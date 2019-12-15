
var TestScoreTable = React.createClass({
	consts:
	{
		headers: ['Test #', 'Score', 'Time', 'Feedback'],
    phases: {0: '(debug)', 1: '(public)', 2: '(private)', 3: '(public)', 4: '(private)'},
	},

	render: function()
	{
    var taskId = this.props.taskId;
		var dbId = this.props.dbId;
		var tabId = this.props.tabId;
		var headerCells = [];
    this.consts.headers.forEach(function(headerText){
			headerCells.push(<th className='header'>{headerText}</th>);
		});
		var headerRow = <tr>{headerCells}</tr>;

		var scoreRows = [];
		var i = 0;
		this.props.testScores.forEach(function(testScore){
      var phasetext = this.consts.phases[testScore.phase];
			var fullFeedbackCell = '';
			if (this.props.fullFeedbackIds && this.props.fullFeedbackIds[i] != null)
			{
				var url = 'viewFeedback.action?taskId='+taskId+'&dbId='+dbId+'&tabId='+tabId+'&submissionId='+this.props.submissionId+'&tagId='+this.props.fullFeedbackIds[i];
				fullFeedbackCell = <a href={url} target="_blank">Download</a>;
			}
			i++;
			scoreRows.push( <tr>
								<td>{testScore.testNo}<br/>{phasetext}</td>
								<td>{testScore.testScore}</td>
								<td>{testScore.processingTime}</td>
								<td>{fullFeedbackCell}<pre className="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px" dangerouslySetInnerHTML={{__html: testScore.feedback}}></pre></td>
							</tr>);
		}.bind(this));

		return(<table className='tablesorter table-striped table-bordered'>
					<thead>{headerRow}</thead>
					<tbody>{scoreRows}</tbody>
				</table>);
	}
});