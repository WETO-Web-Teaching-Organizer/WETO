var COLLAPSEMAPKEY = 'collapseMap';

// Initially toggleGlyphicon() can be used
var glyhiconTogglingEnabled = true;

jQuery(document).ready(function () {
  initCollapseHandlers();
});

function initCollapseHandlers()
{
  // Mark all contents collapsed (flag = false) by default
  jQuery(".collapselinkki").each(function () {
    jQuery(this).attr('title', 'expand');
    jQuery(this).data("flag", false);
  });

  // Set event listeners to collapsible elements that take care of
  // a) toggling glyphicons
  // b) saving collapsible state to cookies
  jQuery('.collapse').each(function ()
  {
    var collapsible = jQuery(this);
    var container = collapsible.closest('.tehtava');
    var collapselinkki = container.find('.collapselinkki');
    var glyph = collapselinkki.find('.glyphicon').first();

    jQuery(collapsible).on('show.bs.collapse', function () {
      collapselinkki.attr('title', 'collapse');
      glyph.removeClass("glyphicon-menu-right").addClass("glyphicon-menu-down");
      setToCollapseMap(collapselinkki, true);
    });

    jQuery(collapsible).on('hide.bs.collapse', function () {
      collapselinkki.attr('title', 'expand');
      glyph.removeClass("glyphicon-menu-down").addClass("glyphicon-menu-right");
      setToCollapseMap(collapselinkki, false);
    });
  });

  // Open previously opened collapse-items
  var collapseMap = {};
  if (Cookies.get(COLLAPSEMAPKEY))
  {
    collapseMap = JSON.parse(Cookies.get(COLLAPSEMAPKEY));
    //console.log("Initial collapse state:");
    //console.log(collapseMap);
  }

  jQuery.each(collapseMap, function (key, value) {
    glyhiconTogglingEnabled = true;

    var jqSelector = "#" + key;
    if (value == true)
    {
      var collapsibleElem = jQuery(jqSelector);
      collapsibleElem.collapse('show');
    }
  });
}

function setToCollapseMap(collapselinkki, boolean)
{
  var container = jQuery(collapselinkki).closest('.tehtava');
  var id = jQuery(container).find('.collapse').attr('id');

  var collapseMap = {};
  if (Cookies.get(COLLAPSEMAPKEY))
  {
    collapseMap = JSON.parse(Cookies.get(COLLAPSEMAPKEY));
  }
  collapseMap[id] = boolean;
  Cookies.set(COLLAPSEMAPKEY, collapseMap);
}