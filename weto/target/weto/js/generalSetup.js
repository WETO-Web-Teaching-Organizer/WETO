function showInfoBarMessage(message)
{
  $('#infoBar').empty();
  $('#infoBar').append(message);
  $('.infoBar').show();
  timeoutobject = setTimeout(function ()
  {
    jQuery(".infoBar").fadeOut(300);
  }, 7000);
}

function toggleConfirmBox(clickedButton)
{
  $(clickedButton).closest(".buttonConfirmContainer").find(".confirmBox").toggle();
}

jQuery(document).ready(function () {
  prettyPrint();
  jQuery('[data-toggle="tooltip"]').tooltip();
});