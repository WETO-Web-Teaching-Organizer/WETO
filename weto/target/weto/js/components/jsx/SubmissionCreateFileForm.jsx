
var SubmissionCreateFileForm = React.createClass({

    handleSubmit: function(event)
    {
        event.preventDefault();
        var submissionId = this.props.submissionId;
        var fileName = React.findDOMNode(this.refs.documentFileFileName).value.trim();
        this.props.onCreateFileSubmit(fileName, submissionId);
    },

    render: function()
    {
        return(
            <div className='submission-createFileForm form-group'>
                <h5>
                    {this.props.header}<span className='submission-createFileForm-filePatterns'> ({this.props.filePatterns})</span>
                </h5>
                <div className='input-group'>
                    <input type='text' className='form-control' placeholder='e.g. file.txt' ref='documentFileFileName'>
                        <span className='input-group-btn'>
                            <button className='btn btn-default' type='button' onClick={this.handleSubmit}>{this.props.verb}</button>
                        </span>
                    </input>
                </div>
            </div>
        );
    },
});
