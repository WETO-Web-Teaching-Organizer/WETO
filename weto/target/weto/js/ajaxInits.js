
function ajaxInits() {
  // Handle error messages
  var timeoutobject = setTimeout(function ()
  {
    jQuery(".infoBar").fadeOut(300);
  }, 7000);

  jQuery(".infoBar").click(function ()
  {
    clearTimeout(timeoutobject);
    console.log("hiding infobar");
    jQuery(this).fadeOut(300);
  });
}