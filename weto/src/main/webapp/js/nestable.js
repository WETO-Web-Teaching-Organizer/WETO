/* Customized nestable list (drag-and-drop list items).
 * Based on Nestable by David Bushell (BSD & MIT license):
 * https://github.com/dbushell/Nestable */
function initNestable(e, t, n, r) {
  function u(t, r) {
    this.w = e(n);
    this.el = e(t);
    this.options = e.extend({}, o, r);
    this.init();
  }
  var i = "ontouchstart" in n;
  var s = function () {
    var e = n.createElement("div"), r = n.documentElement;
    if (!("pointerEvents" in e.style)) {
      return false;
    }
    e.style.pointerEvents = "auto";
    e.style.pointerEvents = "x";
    r.appendChild(e);
    var i = t.getComputedStyle && t.getComputedStyle(e, "").pointerEvents === "auto";
    r.removeChild(e);
    return!!i;
  }();
  var o = {listNodeName: "ul", itemNodeName: "li", rootClass: "dd", listClass: "dd-list",
    itemClass: "dd-item", dragClass: "dd-dragel", handleClass: "dd-handle",
    collapsedClass: "dd-collapsed", placeClass: "dd-placeholder", noDragClass: "dd-nodrag",
    emptyClass: "dd-empty",
    expandBtnHTML: '<button class ="btn-default-small" data-action="expand" type="button"><span class ="glyphicon glyphicon-plus"/></button>',
    collapseBtnHTML: '<button class ="btn-default-small" data-action="collapse" type="button"><span class ="glyphicon glyphicon-minus"></button>',
    group: 0, maxDepth: 99, threshold: 20};
  u.prototype = {init: function () {
      var n = this;
      n.reset();
      n.el.data("nestable-group", this.options.group);
      n.placeEl = e('<div class="' + n.options.placeClass + '"/>');
      e.each(this.el.find(n.options.itemNodeName), function (t, r) {
        n.setParent(e(r));
      });
      n.el.on("click", "button", function (t) {
        if (n.dragEl) {
          return;
        }
        var r = e(t.currentTarget), i = r.data("action"), s = r.parent(n.options.itemNodeName);
        if (i === "collapse") {
          n.collapseItem(s);
        }
        if (i === "expand") {
          n.expandItem(s);
        }
      });
      var r = function (t) {
        var r = e(t.target);
        if (!r.hasClass(n.options.handleClass)) {
          if (r.closest("." + n.options.noDragClass).length) {
            return;
          }
          r = r.closest("." + n.options.handleClass);
        }
        if (!r.length || n.dragEl) {
          return;
        }
        n.isTouch = /^touch/.test(t.type);
        if (n.isTouch && t.touches.length !== 1) {
          return;
        }
        t.preventDefault();
        n.dragStart(t.touches ? t.touches[0] : t);
      };
      var s = function (e) {
        if (n.dragEl) {
          e.preventDefault();
          n.dragMove(e.touches ? e.touches[0] : e);
        }
      };
      var o = function (e) {
        if (n.dragEl) {
          e.preventDefault();
          n.dragStop(e.touches ? e.touches[0] : e);
        }
      };
      if (i) {
        n.el[0].addEventListener("touchstart", r, false);
        t.addEventListener("touchmove", s, false);
        t.addEventListener("touchend", o, false);
        t.addEventListener("touchcancel", o, false);
      }
      n.el.on("mousedown", r);
      n.w.on("mousemove", s);
      n.w.on("mouseup", o);
    }, serialize: function () {
      var t, n = 0, r = this;
      step = function (t, n) {
        var i = [], s = t.children(r.options.itemNodeName);
        s.each(function () {
          var t = e(this), s = e.extend({}, t.data()), o = t.children(r.options.listNodeName);
          if (o.length) {
            s.children = step(o, n + 1);
          }
          i.push(s);
        });
        return i;
      };
      t = step(r.el.find(r.options.listNodeName).first(), n);
      return t;
    }, serialise: function () {
      return this.serialize();
    }, reset: function () {
      this.mouse = {offsetX: 0, offsetY: 0, startX: 0, startY: 0, lastX: 0,
        lastY: 0, nowX: 0, nowY: 0, distX: 0, distY: 0, dirAx: 0, dirX: 0,
        dirY: 0, lastDirX: 0, lastDirY: 0, distAxX: 0, distAxY: 0};
      this.isTouch = false;
      this.moving = false;
      this.dragEl = null;
      this.dragRootEl = null;
      this.dragDepth = 0;
      this.hasNewRoot = false;
      this.pointEl = null;
    }, expandItem: function (e) {
      e.removeClass(this.options.collapsedClass);
      e.children('[data-action="expand"]').hide();
      e.children('[data-action="collapse"]').show();
      e.children(this.options.listNodeName).show();
    }, collapseItem: function (e) {
      var t = e.children(this.options.listNodeName);
      if (t.length) {
        e.addClass(this.options.collapsedClass);
        e.children('[data-action="collapse"]').hide();
        e.children('[data-action="expand"]').show();
        e.children(this.options.listNodeName).hide();
      }
    }, expandAll: function () {
      var t = this;
      t.el.find(t.options.itemNodeName).each(function () {
        t.expandItem(e(this));
      });
    }, collapseAll: function () {
      var t = this;
      t.el.find(t.options.itemNodeName).each(function () {
        t.collapseItem(e(this));
      });
    }, setParent: function (t) {
      if (t.children(this.options.listNodeName).length) {
        t.prepend(e(this.options.expandBtnHTML));
        t.prepend(e(this.options.collapseBtnHTML));
      }
      t.children('[data-action="expand"]').hide();
    }, unsetParent: function (e) {
      e.removeClass(this.options.collapsedClass);
      e.children("[data-action]").remove();
      e.children(this.options.listNodeName).remove();
    }, dragStart: function (t) {
      var i = this.mouse, s = e(t.target), o = s.closest(this.options.itemNodeName);
      this.placeEl.css("height", o.height());
      i.offsetX = t.offsetX !== r ? t.offsetX : t.pageX - s.offset().left;
      i.offsetY = t.offsetY !== r ? t.offsetY : t.pageY - s.offset().top;
      i.startX = i.lastX = t.pageX;
      i.startY = i.lastY = t.pageY;
      this.dragRootEl = this.el;
      this.dragEl = e(n.createElement(this.options.listNodeName)).addClass(this.options.listClass + " " + this.options.dragClass);
      this.dragEl.css("width", o.width());
      if (this.dragRootEl.hasClass("readonly"))
      {
        o.clone().appendTo(this.dragEl);
        o.find(".dd-handle").addClass("chosen");
        this.placeEl[0].id = "elCopy";
      } else
      {
        this.placeEl[0].id = "elMove";
        o.after(this.placeEl);
        o[0].parentNode.removeChild(o[0]);
        o.appendTo(this.dragEl);
      }
      e(n.body).append(this.dragEl);
      this.dragEl.css({left: t.pageX - i.offsetX, top: t.pageY - i.offsetY});
      var u, a, f = this.dragEl.find(this.options.itemNodeName);
      for (u = 0; u < f.length; u++) {
        a = e(f[u]).parents(this.options.listNodeName).length;
        if (a > this.dragDepth) {
          this.dragDepth = a;
        }
      }
    }, dragStop: function (e) {
      var t = this.dragEl.children(this.options.itemNodeName).first();
      t[0].parentNode.removeChild(t[0]);
      this.placeEl.replaceWith(t);
      this.dragEl.remove();
      this.el.trigger("change");
      if (this.hasNewRoot) {
        this.dragRootEl.trigger("change");
      }
      this.reset();
      $(".dd-handle.chosen").removeClass("chosen");
    }, dragMove: function (r) {
      var i, o, u, a, f, l = this.options, c = this.mouse;
      this.dragEl.css({left: r.pageX - c.offsetX, top: r.pageY - c.offsetY});
      c.lastX = c.nowX;
      c.lastY = c.nowY;
      c.nowX = r.pageX;
      c.nowY = r.pageY;
      c.distX = c.nowX - c.lastX;
      c.distY = c.nowY - c.lastY;
      c.lastDirX = c.dirX;
      c.lastDirY = c.dirY;
      c.dirX = c.distX === 0 ? 0 : c.distX > 0 ? 1 : -1;
      c.dirY = c.distY === 0 ? 0 : c.distY > 0 ? 1 : -1;
      var h = Math.abs(c.distX) > Math.abs(c.distY) ? 1 : 0;
      if (!c.moving) {
        c.dirAx = h;
        c.moving = true;
        return;
      }
      if (c.dirAx !== h) {
        c.distAxX = 0;
        c.distAxY = 0;
      } else {
        c.distAxX += Math.abs(c.distX);
        if (c.dirX !== 0 && c.dirX !== c.lastDirX) {
          c.distAxX = 0;
        }
        c.distAxY += Math.abs(c.distY);
        if (c.dirY !== 0 && c.dirY !== c.lastDirY) {
          c.distAxY = 0;
        }
      }
      c.dirAx = h;
      if (c.dirAx && c.distAxX >= l.threshold) {
        c.distAxX = 0;
        u = this.placeEl.prev(l.itemNodeName);
        if (c.distX > 0 && u.length && !u.hasClass(l.collapsedClass)) {
          i = u.find(l.listNodeName).last();
          f = this.placeEl.parents(l.listNodeName).length;
          if (f + this.dragDepth <= l.maxDepth) {
            if (!i.length) {
              i = e("<" + l.listNodeName + "/>").addClass(l.listClass);
              i.append(this.placeEl);
              u.append(i);
              this.setParent(u);
            } else {
              i = u.children(l.listNodeName).last();
              i.append(this.placeEl);
            }
          }
        }
        if (c.distX < 0) {
          a = this.placeEl.next(l.itemNodeName);
          if (!a.length) {
            o = this.placeEl.parent();
            this.placeEl.closest(l.itemNodeName).after(this.placeEl);
            if (!o.children().length) {
              this.unsetParent(o.parent());
            }
          }
        }
      }
      var p = false;
      if (!s) {
        this.dragEl[0].style.visibility = "hidden";
      }
      this.pointEl = e(n.elementFromPoint(r.pageX - n.body.scrollLeft, r.pageY - (t.pageYOffset || n.documentElement.scrollTop)));
      if (!s) {
        this.dragEl[0].style.visibility = "visible";
      }
      if (this.pointEl.hasClass(l.handleClass)) {
        this.pointEl = this.pointEl.parent(l.itemNodeName);
      }
      if (this.pointEl.hasClass(l.emptyClass)) {
        p = true;
      } else if (!this.pointEl.length || !this.pointEl.hasClass(l.itemClass)) {
        return;
      }
      var d = this.pointEl.closest("." + l.rootClass), v = this.dragRootEl.data("nestable-id") !== d.data("nestable-id");
      if (!c.dirAx || v || p) {
        if (v && l.group !== d.data("nestable-group")) {
          return;
        }
        f = this.dragDepth - 1 + this.pointEl.parents(l.listNodeName).length;
        if (f > l.maxDepth) {
          return;
        }
        if (v) {
          this.dragRootEl = d;
          this.hasNewRoot = this.el[0] !== this.dragRootEl[0];
        }
        if (!this.dragRootEl.hasClass("readonly"))
        {
          var m = r.pageY < this.pointEl.offset().top + this.pointEl.height() / 2;
          o = this.placeEl.parent();
          if (p) {
            i = e(n.createElement(l.listNodeName)).addClass(l.listClass);
            i.append(this.placeEl);
            this.pointEl.replaceWith(i);
          } else if (m) {
            this.pointEl.before(this.placeEl);
          } else {
            this.pointEl.after(this.placeEl);
          }
          if (!o.children().length) {
            this.unsetParent(o.parent());
          }
        } else if (/^\d+_\d+:/.test(this.dragEl[0].firstElementChild.getAttribute("data-id")))
        {
          this.hasNewRoot = true;
          o = this.placeEl.parent();
          this.placeEl.remove();
          if (!o.children().length) {
            this.unsetParent(o.parent());
          }
        }
      }
    }};
  e.fn.nestable = function (t) {
    var n = this, r = this;
    n.each(function () {
      var n = e(this).data("nestable");
      if (!n) {
        e(this).data("nestable", new u(this, t));
        e(this).data("nestable-id", (new Date).getTime());
      } else {
        if (typeof t === "string" && typeof n[t] === "function") {
          r = n[t]();
        }
      }
    });
    return r || n;
  };
}
;