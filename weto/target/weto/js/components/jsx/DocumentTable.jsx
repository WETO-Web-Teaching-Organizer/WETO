var NameCell = React.createClass({
    render: function()
    {
      if(this.props.duplicate == true)
      {
        return(<td>{this.props.text} <i>(duplicate file)</i></td>)
      }
      else
      {
        return(<td>{this.props.text}</td>)
      }
  }
});

var SizeCell = React.createClass({
    render: function()
    {
        return (<td>{this.props.size} bytes</td>);
    }
});

var DateCell = React.createClass({
    render: function()
    {
        return (<td>{this.props.date}</td>);
    }
});

var ActionsCell = React.createClass({

    render: function()
    {
        var links = [];
        var actions = this.props.actions;
        var actionglyphs = this.props.actionglyphs;
        var st = {"margin-left": "5px", "margin-right": "5px"};

        this.props.actionkeys.forEach(function(key)
        {
          var glyphclass = actionglyphs[key];
          var url = actions[key];
          url = url+"?tabId="+this.props.tabId+"&taskId="+this.props.taskId+"&dbId="+this.props.dbId+"&documentId="+this.props.doc.id;
          links.push(<a href={url}><span className={glyphclass} title={key} style={st}></span></a>);
        }.bind(this));
        return (<td>{links}</td>);
    }
});

var DocumentTableRow = React.createClass({

    /* Props: actionkeys */
    render: function()
    {
      var classes= '';
      if (this.props.doc.duplicate == true)
      {
        classes= 'yellow';
      }
      return(
          <tr className = {classes}>
              <NameCell text={this.props.doc.fileName} duplicate={this.props.doc.duplicate}/>
              <SizeCell size={this.props.doc.fileSize} />
              <DateCell date={this.props.doc.fileDate} />
              <ActionsCell doc={this.props.doc} actions={this.props.actions} actionglyphs={this.props.actionglyphs}
                           actionkeys={this.props.actionkeys} dbId={this.props.dbId} tabId={this.props.tabId} taskId={this.props.taskId} />
          </tr>
      );
    }
});

var DocumentTable = React.createClass({
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
      actionglyphs: {'edit': 'glyphicon glyphicon-pencil', 'delete': 'glyphicon glyphicon-remove', 'download' : 'glyphicon glyphicon-save'}
   },

   render: function()
   {
      if(!this.props.documents)
      {
        return (<p className='submission-documentTable'><span className='documentTable-message'>Initializing Document Table...</span></p>);
      }

      if (this.props.documents.length === 0)
      {
         return (<p className='submission-documentTable'><span className='documentTable-message yellow'>No files uploaded yet.</span></p>);
      }

      var headers = [];
      this.consts.headers.forEach(function(header)
      {
         headers.push(<th className='header'>{header}</th>);
      });
      var docs = [];

      this.props.documents.forEach(function(doc)
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
        docs.push(<DocumentTableRow
          tabId={this.props.tabId} dbId={this.props.dbId}
          taskId={this.props.taskId} doc={doc}
          actionkeys={actionkeys} actions={this.consts.actions}
          actionglyphs={this.consts.actionglyphs} />);
      }.bind(this));

      return (
         <div className='submission-documentTable'>
           <h5>{this.props.header}</h5>
           <table className='table-striped table-bordered'>
              <thead>
                <tr>
                  {headers}
                </tr>
              </thead>
              <tbody>
                {docs}
              </tbody>
           </table>
         </div>
       );
    }
});