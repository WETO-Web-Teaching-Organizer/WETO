Dropzone.autoDiscover = false;

var SubmissionDropZone = React.createClass({displayName: "SubmissionDropZone",
  initDropZone: function ()
  {
    var url = 'addSubmissionFile.action?taskId=' + this.props.taskId +
            '&dbId=' + this.props.dbId + '&tabId=' + this.props.tabId +
            '&submissionId=' + this.props.submissionId + '&overWriteExisting=true';

    var reactRef = this;
    var domNode = React.findDOMNode(this.refs.container);

    var dropZ = new Dropzone(domNode,
            {
              paramName: 'DocumentFile',
              url: url,
              method: 'POST',
              addRemoveLinks: false,
              init: function ()
              {
                var self = this;
                self.on("addedfile", function (file)
                {
                  {
                    self.emit("thumbnail", file, "images/globalFile.png");
                  }
                });

                // File uploaded
                self.on('removedfile', function (file)
                {
                  reactRef.props.onFileChange(reactRef.props.submissionId);
                });

                self.on('success', function (file, response)
                {
                  reactRef.props.onFileChange(reactRef.props.submissionId, function ()
                  {
                    reactRef.props.checkForExcludedFiles(response);
                    reactRef.props.checkForErrors(response);
                  });
                });

                // All files finished: clear dropzone thumbnails.
                self.on('queuecomplete', function (file)
                {
                  self.removeAllFiles();
                });
              },
            });

    this.dropZref = dropZ;
  },
  componentDidMount: function ()
  {
    this.initDropZone();
  },
  componentWillReceiveProps: function (nextProps)
  {
    if (nextProps.submissionId !== this.props.submissionId)
    {
      this.dropZref.removeAllFiles();
    }

    var url = 'addSubmissionFile.action?taskId=' + this.props.taskId +
            '&dbId=' + this.props.dbId + '&tabId=' + this.props.tabId +
            '&submissionId=' + nextProps.submissionId;

    this.dropZref.options.url = url;
  },
  render: function ()
  {
    if (this.props.allowZipping === false)
    {
      return(React.createElement("div", {className: "submission-dropzoneContainer"},
              React.createElement("h5", null, this.props.header),
              React.createElement("div", {ref: "container", className: "submission-dropzone dropzone"},
                      React.createElement("p", {className: "dz-submissionMessage"}, "Allowed file name patterns:", React.createElement("span", {className: "dz-filePattern"}, this.props.filePatterns))
                      )
              ));
    } else
    {
      return(React.createElement("div", {className: "submission-dropzoneContainer"},
              React.createElement("h5", null, this.props.header, React.createElement("i", null, ". ", this.props.zippingInstructions)),
              React.createElement("div", {ref: "container", className: "submission-dropzone dropzone"},
                      React.createElement("p", {className: "dz-submissionMessage"}, "Allowed file name patterns:", React.createElement("span", {className: "dz-filePattern"}, this.props.filePatterns, ", *.zip"))
                      )
              ));
    }

  }
});