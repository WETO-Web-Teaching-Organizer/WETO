var TINYMCE_ENABLEDCOLOR = "#FFFFFF";
var TINYMCE_DISABLEDCOLOR = "#F0F0F0";

function taskDocFileSelectCallback(field_name, url, type, win) {
  //field_name, -> Name of windows input field that we need to fill in
  //url,        -> Existing URL link, if there is one.
  //type,       -> a string value which is either 'image', 'media' or 'file'
  //win         -> Reference to the dialog window
  var showasThumbnails = "true";
  if (type === "file")
    showasThumbnails = "false";
  // Popup the window with the taskDocument view restricted for selection
  tinymce.activeEditor.windowManager.open({
    file: "${taskDocumentsUrl}&showAsThumbnails=" + showasThumbnails,
    title: 'Task Document Selection',
    width: 640,
    height: 480,
    resizable: "yes",
    inline: "yes", // This parameter only has an effect if you use the inlinepopups plugin!
    close_previous: "no"
  }, {
    window: win,
    input: field_name
  });

  return false;
}

tinymce.init({
  selector: "div.editable",
  inline: "true",
  entity_encoding: "raw",
  plugins: [
    "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
    "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
    "save table contextmenu directionality emoticons template paste textcolor mathslate"
  ],
  file_browser_callback: taskDocFileSelectCallback,
  menubar: false,
  toolbar: [
    "undo redo | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
    "bold italic underline superscript subscript mathslate | outdent indent bullist numlist | link image | preview code"
  ],
  style_formats_merge: true,
  style_formats: [
    {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
    {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
    {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
    {title: 'Code highlight', block: 'pre', classes: 'prettyprint'}
  ]
});