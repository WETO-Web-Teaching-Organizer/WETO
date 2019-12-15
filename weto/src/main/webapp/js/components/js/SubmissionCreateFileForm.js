
var SubmissionCreateFileForm = React.createClass({displayName: "SubmissionCreateFileForm",

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
            React.createElement("div", {className: "submission-createFileForm form-group"}, 
                React.createElement("h5", null, 
                    this.props.header, React.createElement("span", {className: "submission-createFileForm-filePatterns"}, " (", this.props.filePatterns, ")")
                ), 
                React.createElement("div", {className: "input-group"}, 
                    React.createElement("input", {type: "text", className: "form-control", placeholder: "e.g. file.txt", ref: "documentFileFileName"}, 
                        React.createElement("span", {className: "input-group-btn"}, 
                            React.createElement("button", {className: "btn btn-default", type: "button", onClick: this.handleSubmit}, this.props.verb)
                        )
                    )
                )
            )
        );
    },
});
