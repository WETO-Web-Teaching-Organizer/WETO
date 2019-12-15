var SubmissionTitle = React.createClass({displayName: "SubmissionTitle",
  consts:
          {
            statusColors:
                    {
                      0: 'yellow',
                      1: 'white',
                      2: 'green',
                      3: 'red',
                      4: 'gray'
                    },
            statusGlyphs:
                    {
                      0: 'glyphicon glyphicon-alert',
                      1: 'ajax-loading-gif',
                      2: 'glyphicon glyphicon-ok',
                      3: 'glyphicon glyphicon-ban-circle',
                      4: ''
                    }
          },
  render: function ()
  {
    var glyphClass = this.consts.statusGlyphs[this.props.statusNumber];
    var statusColor = this.consts.statusColors[this.props.statusNumber];
    if (this.props.error)
    {
      statusColor = 'red';
    }
    // Being and 'old' submission trumps statuscolor
    if (this.props.index !== 0)
    {
      statusColor = 'gray';
    }

    var statusClass = statusColor + ' row submission-title';
    var glyphClass = this.consts.statusGlyphs[this.props.statusNumber];

    var renders = [];
    renders.push(React.createElement("div", {className: "col-sm-4"}, React.createElement("h3", null, this.props.title)));
    renders.push(React.createElement("div", {className: "col-sm-4"}, React.createElement("h3", null, this.props.timeStamp)));
    renders.push(React.createElement("div", {className: "col-sm-3"}, React.createElement("h3", null, this.props.statusText)));

    if (this.props.statusNumber == 1) // Processing
    {
      renders.push(React.createElement("div", {className: "submission-title-glyphContainer col-sm-1"}, React.createElement("h3", null, React.createElement("img", {width: "34", height: "34", src: "images/ajax-blue.gif"}))));
    } else
    {
      renders.push(React.createElement("div", {className: "submission-title-glyphContainer col-sm-1"}, React.createElement("h3", {className: glyphClass})));
    }

    return(
            React.createElement("div", {className: statusClass},
                    renders
                    )
            );
  }
});
