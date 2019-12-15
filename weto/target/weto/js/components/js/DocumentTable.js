var NameCell = React.createClass({displayName: "NameCell",
  render: function ()
  {
    if (this.props.duplicate == true)
    {
      return(React.createElement("td", null, this.props.text, " ", React.createElement("i", null, "(duplicate file)")))
    } else
    {
      return(React.createElement("td", null, this.props.text))
    }
  }
});

var SizeCell = React.createClass({displayName: "SizeCell",
  render: function ()
  {
    return (React.createElement("td", null, this.props.size, " bytes"));
  }
});

var DateCell = React.createClass({displayName: "DateCell",
  render: function ()
  {
    return React.createElement("td", null, this.props.date);
  }
});

var ActionsCell = React.createClass({displayName: "ActionsCell",
  render: function ()
  {
    var links = [];
    var actions = this.props.actions;
    var actionglyphs = this.props.actionglyphs;

    this.props.actionkeys.forEach(function (key)
    {
      var glyphclass = actionglyphs[key];
      var url = actions[key];
      var st = {"margin-left": "5px", "margin-right": "5px"};
      url = url + "?tabId=" + this.props.tabId + "&taskId=" + this.props.taskId + "&dbId=" + this.props.dbId + "&documentId=" + this.props.doc.id;
      links.push(React.createElement("a", {href: url}, React.createElement("span", {className: glyphclass, title: key, style: st}, " ")));
    }.bind(this));
    return (React.createElement("td", null, links));
  }
});

var DocumentTableRow = React.createClass({displayName: "DocumentTableRow",
  /* Props: actionkeys */
  render: function ()
  {
    var classes = '';
    if (this.props.doc.duplicate == true)
    {
      classes = 'yellow';
    }
    return(React.createElement("tr", {className: classes},
            React.createElement(NameCell, {text: this.props.doc.fileName, duplicate: this.props.doc.duplicate}),
            React.createElement(SizeCell, {size: this.props.doc.fileSize}),
            React.createElement(DateCell, {date: this.props.doc.fileDate}),
            React.createElement(ActionsCell, {doc: this.props.doc, actions: this.props.actions, actionglyphs: this.props.actionglyphs,
              actionkeys: this.props.actionkeys, dbId: this.props.dbId, tabId: this.props.tabId, taskId: this.props.taskId})
            )
            );
  }
});

var DocumentTable = React.createClass({displayName: "DocumentTable",
  /*
   const:
   actions: {action: url, action2: url2 etc}
   array<String> headers
   props:
   array<document> documents
   bool updateable
   int submissionId
   int taskId
   int tabId
   int dbId
   */

  consts:
          {
            headers: ['File name', 'File size', 'File date', 'Actions'],
            actions: {'download': 'downloadDocument.action', 'edit': 'editTextDocument.action', 'delete': 'deleteDocument.action'},
            actionglyphs: {'edit': 'glyphicon glyphicon-pencil', 'delete': 'glyphicon glyphicon-remove', 'download': 'glyphicon glyphicon-save'}
          },
  render: function ()
  {
    if (!this.props.documents)
    {
      return (React.createElement("p", {className: "submission-documentTable"}, React.createElement("span", {className: "documentTable-message"}, "Initializing Document Table...")));
    }

    if (this.props.documents.length === 0)
    {
      return (React.createElement("p", {className: "submission-documentTable"}, React.createElement("span", {className: "documentTable-message yellow"}, "No files uploaded yet.")));
    }

    var headers = [];
    this.consts.headers.forEach(function (header)
    {
      headers.push(React.createElement("th", {className: "header"}, header));
    });
    var docs = [];

    this.props.documents.forEach(function (doc)
    {
      var actionkeys = [];
      actionkeys.push('download');
      if (this.props.updateable == true)
      {
        actionkeys.push('delete');
        if (doc.contentMimeType)
        {
          if (doc.contentMimeType.lastIndexOf('text', 0) === 0)
          {
            actionkeys.push('edit');
          }
        }
      }
      docs.push(React.createElement(DocumentTableRow, {
        tabId: this.props.tabId, dbId: this.props.dbId,
        taskId: this.props.taskId, doc: doc,
        actionkeys: actionkeys, actions: this.consts.actions,
        actionglyphs: this.consts.actionglyphs}));
    }.bind(this));

    return (
            React.createElement("div", {className: "submission-documentTable"},
                    React.createElement("h5", null, this.props.header),
                    React.createElement("table", {className: "table-striped table-bordered"},
                            React.createElement("thead", null,
                                    React.createElement("tr", null,
                                            headers
                                            )
                                    ),
                            React.createElement("tbody", null,
                                    docs
                                    )
                            )
                    )
            );
  }
});