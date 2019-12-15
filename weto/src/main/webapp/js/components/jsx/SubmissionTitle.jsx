var SubmissionTitle = React.createClass({

	consts: 
	{
		/*
		Stati:
			0: Not Submitted
			1: Processing
			2: Accepted
			3: Not Accepted
			4: Quiz Submission
		*/
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

	render: function()
	{
		var glyphClass = this.consts.statusGlyphs[this.props.statusNumber];
		var statusColor = this.consts.statusColors[this.props.statusNumber];
		if (this.props.error)
		{
			glyphClass = 'glyphicon glyphicon-ban-circle';
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
		renders.push(<div className='col-sm-4'><h3>{this.props.title}</h3></div>);
		renders.push(<div className='col-sm-4'><h3>{this.props.timeStamp}</h3></div>);
		renders.push(<div className='col-sm-3'><h3>{this.props.statusText}</h3></div>);
		
		if (this.props.statusNumber == 1) // Processing
		{
			renders.push (<div className='submission-title-glyphContainer col-sm-1'><h3><img width='34' height='34' src='images/ajax-blue.gif' /></h3></div>);
		}
		else
		{
			renders.push(<div className='submission-title-glyphContainer col-sm-1'><h3 className={glyphClass}></h3></div>);
		}

		return(
			<div className={statusClass}>
				{renders}
			</div>
		);
	}
});
