// Setup sidebar toggler

$(document).ready(function () {
  $("#sidebartoggle-button").click(function (event) {
    event.preventDefault();
    var wrapper = $("#wrapper");
    wrapper.toggleClass("toggled");
    var sidewrapper = $("#sidebartoggle-wrapper");
    var tabnav = $("#tabnav");
    if (wrapper.hasClass("toggled")) {
      sidewrapper.css("width", "auto");
      tabnav.css("margin-left", sidewrapper.width());
      if (tabNode.attr('isToggled') != 'true')
      {
        tabNode.next().css("margin-top", tabNode.outerHeight(true));
      } else
      {
        tabNode.next().css("margin-top", sidewrapper.outerHeight(true));
      }
      Cookies.set('showSideBar', 'false', {expires: 7});
    } else {
      tabnav.css("margin-left", 0);
      sidewrapper.css("width", $("#sidebar-nav").width());
      if (tabNode.attr('isToggled') != 'true')
      {
        tabNode.next().css("margin-top", tabNode.outerHeight(true));
      } else
      {
        tabNode.next().css("margin-top", 0);
      }
      Cookies.set('showSideBar', 'true', {expires: 7});
    }
  });
});