$(document).ready(function () {
  tabNode = jQuery('#tabnav');
  tabNode.css("width", tabNode.parent().width());

  tabNode.find("span.glyphicon").click(function (event) {
    event.preventDefault();
    event.stopPropagation();
    var submenu = $(this).parent().next();
    submenu.css("display", submenu.css("display") === 'none' ? 'block' : 'none');
  });

  // Initial position of tabnav
  var stickyNavTop = tabNode.offset().top;
  var lastScrollTop = 0;

  var tabHeight = 0;

  var toggleTabsOn = function () {
    var tabNode = jQuery('#tabnav');
    tabNode.attr('isToggled', 'true');
    tabNode.hide();
    if ($("#sidebar-wrapper").width() == 0)
    {
      $("#sidebartoggle-wrapper").css("width", "auto");
      tabNode.next().css("margin-top", $("#sidebartoggle-wrapper").outerHeight(true));
    } else
    {
      tabNode.next().css("margin-top", 0);
    }
  };

  var toggleTabsOff = function () {
    var tabNode = jQuery('#tabnav');
    tabNode.attr('isToggled', 'false');
    tabNode.css("width", tabNode.parent().width());
    if ($("#sidebar-wrapper").width() == 0)
    {
      $("#sidebartoggle-wrapper").css("width", "auto");
      $("#tabnav").css("margin-left", $("#sidebartoggle-wrapper").width());
    } else
    {
      $("#tabnav").css("margin-left", 0);
    }
    tabNode.show();
    tabHeight = tabNode.outerHeight(true);
    tabNode.next().css("margin-top", tabNode.outerHeight(true));
  };

  var stickyNav = function () {
    if (tabNode.attr('isToggled') != 'true')
    {
      var scrollTop = $(window).scrollTop();
      // Tabnav gets hidden when scrolling towards bottom
      if ((scrollTop > (stickyNavTop + tabHeight)) && (lastScrollTop < scrollTop)) {
        $('#tabnav').hide();
      } else if (lastScrollTop > scrollTop) {
        $('#tabnav').show();
      }
      lastScrollTop = $(window).scrollTop();
    }
  };

  if (Cookies.get('tabnavToggled') == 'true')
  {
    toggleTabsOn();
  } else
  {
    toggleTabsOff();
    stickyNav();
  }

  $(window).scroll(function () {
    stickyNav();
  });

  $("#tabstoggle-button").click(function (event) {
    event.preventDefault();
    if (tabNode.attr('isToggled') == 'true')
    {
      toggleTabsOff();
    } else
    {
      toggleTabsOn();
    }
    Cookies.set('tabnavToggled', tabNode.attr('isToggled'), {expires: 7});
  });
});
