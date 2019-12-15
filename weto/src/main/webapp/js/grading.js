// Enables form fields and toggles its buttons
function toggleFormEditable(clickedButton)
{
  var form = jQuery(clickedButton).closest("form");

  form.find(".reviewText").each(function () {
    jQuery(this).data("oldvalue", this.innerHTML);
    var tinyed = tinymce.get(this.id);
    if (tinyed.getBody().isContentEditable)
    {
      tinyed.setMode("readonly");
    } else
    {
      tinyed.setMode("design");
    }
  });

  form.find("input[type='number']").each(function () {
    toggleEnabled(jQuery(this));
    jQuery(this).data("oldvalue", jQuery(this).val());
  });

  form.find(".togglable-instruction").toggle();
  form.find("button").toggle();
}

function toggleReviewInstructions(clickedButton)
{
  jQuery(clickedButton).closest(".review-print").find(".togglable-instruction").toggle();
}

function toggleEnabled(jQObject)
{
  var toggler = jQObject.prop("disabled");
  jQObject.prop("disabled", !toggler);
}

function cancelFormEditable(clickedButton)
{
  var form = jQuery(clickedButton).closest("form");

  // Revert to old values
  form.find(".reviewText").each(function ()
  {
    this.innerHTML = jQuery(this).data("oldvalue");
  });
  form.find("input[type='number']").each(function ()
  {
    jQuery(this).val(jQuery(this).data("oldvalue"));
  });

  toggleFormEditable(clickedButton);
}

function updateTotalMark(changedInput)
{
  var form = jQuery(changedInput).closest("form");

  var total = 0;
  form.find(".multipart-mark-points-input").each(function () {
    total = total + Number(jQuery(this).val());
  });
  form.find(".total-mark").val(Math.round(total));
}

function colorDiffFeedback(jqFeedbackEls)
{
  jqFeedbackEls.each(function () {
    var fblines = $(this).html().split('\n');
    var oklines = 0;
    for (var i = 0; i < fblines.length; i++) {
      if (/^[ +-\\]/.test(fblines[i]))
      {
        oklines += 1;
      }
    }
    if ((oklines >= 1) && (oklines >= (fblines.length - 1)))
    {
      var newfb = "";
      for (var i = 0; i < fblines.length; i++) {
        var bgcol = "#FFFFFF";
        if (fblines[i].charAt(0) == '+')
        {
          bgcol = "#98FB98";
        } else if ((fblines[i].charAt(0) == '-') || (fblines[i].charAt(0) == '\\'))
        {
          bgcol = "#FFA07A";
        }
        newfb = newfb + '<span style="background-color: ' + bgcol + ';">' + fblines[i] + '</span>';
        if ((i + 1) < fblines.length)
        {
          newfb += '\n';
        }
      }
      $(this).html(newfb);
    }
  });
}