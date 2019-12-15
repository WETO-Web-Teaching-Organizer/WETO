tinymce.init({
  selector: "textarea",
  resize: "both",
  entity_encoding: "raw",
  plugins: [
    "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
    "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
    "save table contextmenu directionality emoticons template paste textcolor mathslate"
  ],
  content_css: "css/tinyMCEcontent.css",
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