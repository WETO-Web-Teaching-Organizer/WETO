
COLLAPSEDICONNAME = "glyphicon-plus";
EXPANDEDICONNAME = "glyphicon-minus";

// Build tree menu
function buildTreemenuUnder(containerSelector, JSONData, dbId, clickedTaskId, actionName)
{
  // Build the list
  var containerDOM = jQuery(containerSelector);
  var parent = JSONData;
  recurseTree(parent, containerDOM, dbId, clickedTaskId, actionName);

  // Add togglability
  makeTogglable(containerDOM);
  // Set intial state of the tree, which is defined in JSONData
  setCollapseState(containerDOM);
}

// Constructs the list tree
function recurseTree(current, containerDOM, dbId, clickedTaskId, actionName)
{
  // Skip root level and add its children as first level
  if (current.isRoot)
  {
    for (var i = 0; i < current.children.length; i++)
    {
      var child = current.children[i];
      recurseTree(child, containerDOM, dbId, clickedTaskId, actionName);
    }
    return;
  }

  // A new list item acts as a container for a navigation link and submenu(s)
  var newItem = jQuery("<li></li>");
  newItem.data("taskId", current.id);
  var linkRow = jQuery("<div></div>");
  linkRow.addClass("glyph-link-empty");
  newItem.append(linkRow);

  // One of the elements was clicked at previous request.
  if (current.id == clickedTaskId)
  {
    newItem.addClass("expanded-task");
  }

  // Create the link
  var linkURL = actionName + "?" + "taskId=" + current.id + "&tabId=" + 0 + "&dbId=" + dbId;
  var name = decodeURIComponent(jQuery("<div>" + current.name + "</div>").text());
  if (current.grade)
  {
    name = name + '<span class="glyphicon glyphicon-ok-sign"></span>';
  }
  var newLink = jQuery("<a>" + (current.isHidden ? "<del>" + name + "</del>" : name) + "</a>");
  newLink.attr("href", linkURL);
  linkRow.append(newLink);
  // Only non-root <li>'s are expandable
  newItem.addClass("expandable-li");

  containerDOM.append(newItem);

  if (current.children.length)
  {
    //Create empty container to make rest of row also clickable
    var secondaryToggler = jQuery("<div>" + "&nbsp;" + "</div>");
    secondaryToggler.addClass('emptyContainer');
    secondaryToggler.addClass('nav-toggler-secondary');
    linkRow.append(secondaryToggler);

    // Add  submenu toggler to current
    var toggler = jQuery("<span></span>");
    toggler.addClass("nav-toggler");
    toggler.addClass(COLLAPSEDICONNAME).addClass("glyphicon");
    linkRow.prepend(toggler);

    // Create a new list inside the above created dom item and recursively fill it.
    var newSublist = jQuery("<ul></ul>");
    newItem.append(newSublist);

    for (var i = 0; i < current.children.length; i++)
    {
      var child = current.children[i];
      recurseTree(child, newSublist, dbId, clickedTaskId, actionName);
    }
  }
}

function makeTogglable(containerDOM)
{
  // Clicking on a nav-toggler shows its children i.e. the sub-items it has.
  jQuery(containerDOM).find(".nav-toggler, .nav-toggler-secondary").click(function (event)
  {
    var liObject = jQuery(this).closest('li');
    var ulObject = liObject.children("ul");
    var glyphObject;

    if (jQuery(this).hasClass('emptyContainer')) {
      glyphObject = jQuery(this).siblings('.glyphicon');
    } else {
      glyphObject = jQuery(this);
    }
    expandOrCollapse(ulObject, glyphObject);
    event.stopPropagation();
  });
}

// Setups the navigation initial tree on page load
function setCollapseState(containerDOM)
{
  // At the beginning collapse everything except first level
  jQuery(containerDOM).find(".nav-toggler").each(function () {
    var liParent = jQuery(this).closest(".expandable-li");
    var ulObject = liParent.children("ul");
    collapse(ulObject, jQuery(this));
  });

  // The rest: find element which has class "expanded-task", expand it and all its ancestors
  jQuery(containerDOM).find(".expanded-task").filter(".expandable-li").each(function () {
    var activeEl = $(this);
    // Expand clicked item unless it is root
    var firstUlObject = activeEl.children("ul").first();
    var firstGlyph = activeEl.find(".glyphicon").first();
    expand(firstUlObject, firstGlyph);

    // Expand all <li> ancestors of clicked item
    activeEl.parentsUntil(containerDOM).filter(".expandable-li").each(function ()
    {
      var ulObject = jQuery(this).children("ul").first();
      var glyph = jQuery(this).find(".glyphicon").first();
      expand(ulObject, glyph);
    });
    var links = activeEl.find("a");
    links.last().focus();
    links.first().focus();
  });
}

// Takes the parent <li> and ".nav-toggler" as arguments
function expandOrCollapse(sublist, navToggler)
{
  // Toggle all <ul> children
  sublist.slideToggle(300);
  // Toggle the icon
  if (navToggler.hasClass(COLLAPSEDICONNAME))
  {
    navToggler.removeClass(COLLAPSEDICONNAME);
    navToggler.addClass(EXPANDEDICONNAME);
  } else if (navToggler.hasClass(EXPANDEDICONNAME))
  {
    navToggler.addClass(COLLAPSEDICONNAME);
    navToggler.removeClass(EXPANDEDICONNAME);
  }

}
function expand(sublist, navToggler)
{
  sublist.toggle(true);
  if (navToggler.hasClass(COLLAPSEDICONNAME))
  {
    navToggler.removeClass(COLLAPSEDICONNAME);
    navToggler.addClass(EXPANDEDICONNAME);
  }
}
function collapse(sublist, navToggler)
{
  sublist.toggle(false);
  if (navToggler.hasClass(EXPANDEDICONNAME))
  {
    navToggler.removeClass(EXPANDEDICONNAME);
    navToggler.addClass(COLLAPSEDICONNAME);
  }
}