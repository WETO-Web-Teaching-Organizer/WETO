
var TestScoreTable = React.createClass({displayName: "TestScoreTable",
  consts:
          {
            headers: ['Test #', 'Score', 'Time', 'Feedback'],
            phases: {0: '(debug)', 1: '(public)', 2: '(private)', 3: '(public)', 4: '(private)'},
          },
  render: function ()
  {
    var taskId = this.props.taskId;
    var dbId = this.props.dbId;
    var tabId = this.props.tabId;
    var headerCells = [];
    this.consts.headers.forEach(function (headerText) {
      headerCells.push(React.createElement("th", {className: "header"}, headerText));
    });
    var headerRow = React.createElement("tr", null, headerCells);

    var scoreRows = [];
    var i = 0;
    this.props.testScores.forEach(function (testScore) {
      var phasetext = this.consts.phases[testScore.phase];
      var fullFeedbackCell = "";
      if (this.props.fullFeedbackIds && this.props.fullFeedbackIds[i] != null)
      {
        var url = 'viewFeedback.action?taskId=' + taskId + '&dbId=' + dbId + '&tabId=' + tabId + '&submissionId=' + this.props.submissionId + '&tagId=' + this.props.fullFeedbackIds[i];
        fullFeedbackCell = React.createElement("a", {href: url, target: "_blank"}, "Download");
      }
      i++;

      scoreRows.push(React.createElement("tr", null,
              React.createElement("td", null, testScore.testNo, React.createElement("br"), phasetext),
              React.createElement("td", null, testScore.testScore),
              React.createElement("td", null, testScore.processingTime),
              React.createElement("td", null, fullFeedbackCell, React.createElement("pre", {className: "diffCell", style: {"word-wrap": "break-word;", "white-space": "pre-wrap;", "margin": "0px"}, dangerouslySetInnerHTML: {__html: testScore.feedback}}))
              ));
    }.bind(this));

    return(React.createElement("table", {className: "tablesorter table-striped table-bordered"},
            React.createElement("thead", null, headerRow),
            React.createElement("tbody", null, scoreRows)
            ));
  }
});