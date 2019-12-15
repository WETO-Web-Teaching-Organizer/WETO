<%@ include file="/WEB-INF/taglibs.jsp"%>
<ul id="tabnav">
  <li>
    <s:a action="listCourses">
      <span class="tabmenu-link">
        <s:text name = "weto.title"/>
      </span>
    </s:a>
  </li>
  <li >
    <s:a action="viewUsers">
      <span class="tabmenu-link">
        <s:text name="adminMenu.editUsers" />
      </span>
    </s:a>
  </li>
  <li >
    <s:a action="adminViewTeachers">
      <span class="tabmenu-link">
        <s:text name="adminMenu.editTeachers" />
      </span>
    </s:a>
  </li>
  <li>
    <s:a action="addCourse">
      <span class="tabmenu-link">
        <s:text name="adminMenu.addCourse" />
      </span>
    </s:a>
  </li>
  <li>
    <s:a action="deleteCourse">
      <span class="tabmenu-link">
        <s:text name="adminMenu.deleteCourse" />
      </span>
    </s:a>
  </li>
  <li>
    <s:a action="viewNews">
      <span class="tabmenu-link">
        <s:text name="adminMenu.editNews" />
      </span>
    </s:a>
  </li>
</ul>
