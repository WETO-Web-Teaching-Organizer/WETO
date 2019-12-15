// TinyMCE file browser
function taskDocFileSelectCallback(field_name, url, type, win) {
  var browseUrl = (type === "file") ? "${taskDocumentsUrl}" : "${taskImagesUrl}";
  tinymce.activeEditor.windowManager.open({
    file: browseUrl,
    title: 'Task Document Selection',
    autoScroll: true,
    width: 480,
    height: 500
  }, {
    window: win,
    input: field_name
  });
  return false;
}

// Script makes sure that max points can't be less than min points
function adjustMinToThis(inputObject)
{
  var minVal = jQuery(inputObject).val();
  var maxPointsElem = jQuery(inputObject).closest("form").find("input[name='newMaxPoints']");
  maxPointsElem.attr("min", minVal);
}
function adjustMaxToThis(inputObject)
{
  var maxVal = jQuery(inputObject).val();
  var minPointsElem = jQuery(inputObject).closest("form").find("input[name='newMinPoints']");
  minPointsElem.attr("max", maxVal);
}

// Keys that store old values when form is edited. May be used to cancel edit.
var OLDNAMEKEY = "oldName";
var OLDTEXTKEY = "oldText";
var OLDMINPOINTSKEY = "oldMinPoints";
var OLDMAXPOINTSKEY = "oldMaxPoints";
var IDKEY = "id";

var savedInstructionOrder;
var newInstructionOrder;

// Creates a create form under the same div where clickedButton is located.
function prepareNewInstructionForm(clickedButton)
{
  var form = jQuery("#instruction-create-template").clone();
  var parent = jQuery(clickedButton).closest(".instruction");
  form.attr("id", "");

  parent.empty();
  parent.append(form);
  form.show(500, function () {
    // Init TinyMCE for instruction text
    var textArea = form.find("textarea");
    textArea.attr("id", "createInstructionText");
    tinymce.init({
      selector: "#createInstructionText",
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      file_browser_callback: taskDocFileSelectCallback,
      menubar: false,
      toolbar: [
        "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
        "code link image | bold italic underline superscript subscript mathslate | outdent indent bullist numlist | preview"
      ],
      style_formats_merge: true,
      style_formats: [
        {title: 'Code highlight', block: 'pre', classes: 'prettyprint'},
        {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
        {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
        {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
        {title: 'Shadow-box-week', block: 'div', classes: 'content-col'},
        {title: 'Default-Panel-element', block: 'div', classes: 'panel'},
        {title: 'Interactive-code-panel', block: 'div', classes: 'code-panel prettyprint'},
        {title: 'Peerreview-border-box', block: 'div', classes: 'peerreview-border-box'},
        {title: 'Peerreview-highlight-text', block: 'p', classes: 'peerreview-highlight'},
        {title: 'Highlight-text-background', block: 'p', classes: 'highlight-text-background'},
        {title: 'icemint', block: 'div', classes: 'icemint'},
        {title: 'red-panel-box', block: 'div', classes: 'panel-red'},
        {title: 'blue-panel-box', block: 'div', classes: 'panel-blue'},
        {title: 'latte-panel-box', block: 'div', classes: 'panel-latte'},
        {title: 'gray-panel-box', block: 'div', classes: 'panel-darker-gray'},
        {title: 'news-panel-box', block: 'div', classes: 'panel-news'},
        {title: 'news-title', block: 'h5', classes: 'news-title-style'},
        {title: 'only-border-box', block: 'div', classes: 'only-border'}
      ]
    });
  });
}

// Creates and populates a
function prepareEditInstructionForm(clickedButton)
{
  var container = jQuery(clickedButton).closest(".instruction");
  var form = jQuery("#instruction-edit-template").clone()
  form.attr("id", "");

  // Store old values
  var instructionHTML = container.find(".instructionText").html(); // Container is saved as object
  var instructionName = container.find(".instructionName").text();
  var minPoints = parseFloat(container.find(".minPoints").text());
  var maxPoints = parseFloat(container.find(".maxPoints").text());
  var id = parseInt(container.attr("data-instructionId"));

  // Populate form with old values
  jQuery(form).find(".newInstructionText").val(instructionHTML);
  jQuery(form).find(".newInstructionName").val(instructionName);
  jQuery(form).find(".newMinPoints").val(minPoints);
  jQuery(form).find(".newMaxPoints").val(maxPoints);
  jQuery(form).find("input[name='instructionId']").val(id);

  // Store old values in case of canceling this form
  form.data(OLDTEXTKEY, instructionHTML);
  form.data(OLDNAMEKEY, instructionName);
  form.data(OLDMINPOINTSKEY, minPoints);
  form.data(OLDMAXPOINTSKEY, maxPoints);
  form.data(IDKEY, id);

  $('#toggleReorderingButton').prop("disabled", true);
  $('#reorderInstructionsButton').prop("disabled", true);

  container.empty();
  container.append(form);
  form.show(500, function () {
    // Init TinyMCE for instruction text
    var textArea = form.find("textarea");
    textArea.prop("id", "newInstructionText" + id);
    tinymce.init({
      selector: "#newInstructionText" + id,
      entity_encoding: "raw",
      plugins: [
        "advlist autolink autosave link image lists charmap preview hr anchor pagebreak",
        "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
        "save table contextmenu directionality emoticons template paste textcolor mathslate"
      ],
      file_browser_callback: taskDocFileSelectCallback,
      menubar: false,
      toolbar: [
        "undo redo restoredraft | styleselect | alignleft aligncenter alignright alignjustify | forecolor backcolor emoticons | table",
        "code link image | bold italic underline superscript subscript mathslate | outdent indent bullist numlist | preview"
      ],
      style_formats_merge: true,
      style_formats: [
        {title: 'Code highlight', block: 'pre', classes: 'prettyprint'},
        {title: 'Khaki background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Khaki'}},
        {title: 'Peru background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'Peru'}},
        {title: 'RosyBrown background', block: 'div', wrapper: true, classes: 'noteBox', styles: {backgroundColor: 'RosyBrown'}},
        {title: 'Shadow-box-week', block: 'div', classes: 'content-col'},
        {title: 'Default-Panel-element', block: 'div', classes: 'panel'},
        {title: 'Interactive-code-panel', block: 'div', classes: 'code-panel prettyprint'},
        {title: 'Peerreview-border-box', block: 'div', classes: 'peerreview-border-box'},
        {title: 'Peerreview-highlight-text', block: 'p', classes: 'peerreview-highlight'},
        {title: 'Highlight-text-background', block: 'p', classes: 'highlight-text-background'},
        {title: 'icemint', block: 'div', classes: 'icemint'},
        {title: 'red-panel-box', block: 'div', classes: 'panel-red'},
        {title: 'blue-panel-box', block: 'div', classes: 'panel-blue'},
        {title: 'latte-panel-box', block: 'div', classes: 'panel-latte'},
        {title: 'gray-panel-box', block: 'div', classes: 'panel-darker-gray'},
        {title: 'news-panel-box', block: 'div', classes: 'panel-news'},
        {title: 'news-title', block: 'h5', classes: 'news-title-style'},
        {title: 'only-border-box', block: 'div', classes: 'only-border'}
      ]
    });
  });
}

function closeEditForm(clickedButton)
{
  var container = jQuery(clickedButton).closest(".instruction");
  var form = jQuery(clickedButton).closest("form");
  // Get old values
  var oldInstructionHTML = form.data(OLDTEXTKEY);
  var oldInstructionName = form.data(OLDNAMEKEY);
  var oldMinPoints = form.data(OLDMINPOINTSKEY);
  var oldMaxPoints = form.data(OLDMAXPOINTSKEY);
  var id = form.data(IDKEY);
  // Empty container and populate with the old instruction
  tinymce.get("newInstructionText" + id).destroy();
  $('#toggleReorderingButton').prop("disabled", false);
  if (savedInstructionOrder != newInstructionOrder)
  {
    $('#reorderInstructionsButton').prop("disabled", false);
  }
  var template = jQuery("#instruction-view-template").clone();
  template.attr("id", "");
  // Populate with old values
  template.find(".instructionName").text(oldInstructionName);
  template.find(".instructionText").html(oldInstructionHTML);
  template.find(".minPoints").text(oldMinPoints);
  template.find(".maxPoints").text(oldMaxPoints);
  template.find("input[name='instructionId']").val(id);
  form.hide(300, function () {
    container.empty();
    container.append(template);
    template.show(200);
  });
}

function closeCreateForm(clickedButton)
{
  var form = jQuery(clickedButton).closest("form");
  var container = jQuery(clickedButton).closest(".instruction");
  var template = jQuery("#create-button-template").clone();
  template.attr("id", "");
  tinymce.get("createInstructionText").destroy();
  form.hide(300, function () {
    container.empty();
    container.append(template);
    template.show(200);
  });
}

var xmlHttp;
if (window.XMLHttpRequest)
{// code for IE7+, Firefox, Chrome, Opera, Safari
  xmlHttp = new XMLHttpRequest();
} else
{// code for IE6, IE5
  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
}

function callback() {
  if (xmlHttp.readyState == 4 && xmlHttp.status == 204) {
    savedInstructionOrder = newInstructionOrder;
    $('#reorderInstructionsButton').prop("disabled", true);
    $('#infoBar').empty();
    $('#infoBar').append(
            '<div id="messages"><b>INFO:</b><ul class="actionMessage"><li><span><s:text name="reviewinstructions.message.success" /></span></li></ul></div>'
            );
    $('.infoBar').show();
    timeoutobject = setTimeout(function ()
    {
      jQuery(".infoBar").fadeOut(300);
    }, 7000);
  }
}

function sendAjaxRequest() {
  xmlHttp.open("GET", "reorderReviewInstructions?taskId=${taskId}&tabId=${tabId}&dbId=${dbId}&order=" + newInstructionOrder);
  xmlHttp.onreadystatechange = callback;
  xmlHttp.send(null);
}

function toggleReorderingEnabled()
{
  var instrList = $("#instructionsList");
  if (instrList.hasClass("ui-sortable"))
  {
    instrList.sortable("destroy");
    $("#toggleReorderingButton").text('<s:text name="reviewinstructions.header.enableReordering" />');
    $(".modifyInstructionButton").prop("disabled", false);
  } else
  {
    instrList.sortable({
      items: "> [data-instructionId]",
      placeholder: "ui-state-highlight",
      forcePlaceholderSize: true,
      update: function (event, ui) {
        newInstructionOrder = $(this).sortable('toArray', {attribute: 'data-instructionId'}).toString();
        if (savedInstructionOrder != newInstructionOrder)
        {
          $('#reorderInstructionsButton').prop("disabled", false);
        } else
        {
          $('#reorderInstructionsButton').prop("disabled", true);
        }
      }
    });
    if (typeof savedInstructionOrder == 'undefined')
    {
      savedInstructionOrder = instrList.sortable('toArray', {attribute: 'data-instructionId'}).toString();
    }
    instrList.disableSelection();
    $("#toggleReorderingButton").text('<s:text name="reviewinstructions.header.disableReordering" />');
    $(".modifyInstructionButton").prop("disabled", true);
  }
}