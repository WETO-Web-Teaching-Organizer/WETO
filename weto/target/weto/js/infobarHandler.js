
jQuery(document).ready(function ()
{
  var timeoutobject = setTimeout(function ()
  {
    jQuery(".infoBar").fadeOut(300);
  }, 7000);

  jQuery(".infoBar").click(function ()
  {
    clearTimeout(timeoutobject);
    jQuery(this).fadeOut(300);
  });
});
