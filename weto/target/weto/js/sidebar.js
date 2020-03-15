// Setup sidebar/tab toggler

$(document).ready(function () {
  var stickyNavTop = 0;
  var lastScrollTop = 0;
  var tabHeight = 0;
  var wrapper = jQuery("#wrapper");
  var sideWrapper = jQuery("#sidebartoggle-wrapper");
  var sideNav = jQuery("#sidebar-nav");
  var tabNode = jQuery('#tabnav');
  var logoutBtn = jQuery('#logoutSpan');
  var roleBtn = jQuery('#teacherRole-status');

  var toggleTabsOn = function () {
    tabNode.attr('isToggled', 'true');
    tabNode.hide();
    if ($("#sidebar-wrapper").width() == 0)
    {
      $("#sidebartoggle-wrapper").css("width", "auto");
      tabNode.next().css("margin-top", sideWrapper.outerHeight(true));
    } else
    {
      tabNode.next().css("margin-top", 0);
    }
  };

  var toggleTabsOff = function () {
    tabNode.attr('isToggled', 'false');
    logoutBtn.show();
    roleBtn.show();
    tabNode.css("width", tabNode.parent().width());
    if (sideWrapper.width() == 0)
    {
      sideWrapper.css("width", "auto");
      tabNode.css("margin-left", sideWrapper.width());
    } else
    {
      tabNode.css("margin-left", 0);
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
        tabNode.hide();
      } else if (lastScrollTop > scrollTop) {
        tabNode.show();
      }
      lastScrollTop = $(window).scrollTop();
    }
  };

  if (tabNode.length)
  {
    // Initial position of tabnav
    stickyNavTop = tabNode.offset().top;
    tabNode.css("width", tabNode.parent().width());
    tabNode.find("span.glyphicon").click(function (event) {
      event.preventDefault();
      event.stopPropagation();
      var submenu = $(this).parent().next();
      submenu.css("display", submenu.css("display") === 'none' ? 'block' : 'none');
    });

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
  }

  if (wrapper.hasClass("toggled"))
  {
    if ((!tabNode.length) || (tabNode.attr('isToggled') == 'true'))
    {
      logoutBtn.hide();
      roleBtn.hide();
    }
    sideWrapper.css("width", "auto");
    tabNode.css("margin-left", sideWrapper.width());
  }

  $("#sidebartoggle-button").click(function (event) {
    event.preventDefault();
    if (wrapper.hasClass("toggled")) {
      if (tabNode.length && (tabNode.attr('isToggled') == 'true'))
      {
        toggleTabsOff();
        tabNode.css("margin-left", sideWrapper.width());
        Cookies.set('tabnavToggled', tabNode.attr('isToggled'), {expires: 7});
      } else
      {
        tabNode.css("margin-left", 0);
        wrapper.toggleClass("toggled");
        sideWrapper.css("width", sideNav.width());
        if (tabNode.attr('isToggled') != 'true')
        {
          tabNode.next().css("margin-top", tabNode.outerHeight(true));
        } else
        {
          tabNode.next().css("margin-top", 0);
        }
        Cookies.set('showSideBar', 'true', {expires: 7});
      }
    } else
    {
      if (tabNode.length && (tabNode.attr('isToggled') != 'true'))
      {
        toggleTabsOn();
        Cookies.set('tabnavToggled', tabNode.attr('isToggled'), {expires: 7});
      } else
      {
        wrapper.toggleClass("toggled");
        logoutBtn.hide();
        roleBtn.hide();
        sideWrapper.css("width", "auto");
        if (tabNode.attr('isToggled') != 'true')
        {
          tabNode.next().css("margin-top", tabNode.outerHeight(true));
        } else
        {
          tabNode.next().css("margin-top", sideWrapper.outerHeight(true));
        }
        Cookies.set('showSideBar', 'false', {expires: 7});
      }
    }
  });
});