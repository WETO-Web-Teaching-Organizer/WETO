<%@ include file="/WEB-INF/taglibs.jsp"%>
<div class="content-col">
   <h2><s:text name="deleteCourse.title" /></h2>
   <p>
     <s:text name="deleteCourse.instructions" />
   </p>
   <form action="<s:url action="confirmDeleteCourse" />" method="post">
     <table>
       <tr>
         <td>
           <s:text name="general.header.selectCourse" />
         </td>
         <td>
           <select name="courseTaskId">
             <s:iterator var="course" value="courses">
               <option value="${course.masterTaskId}"> ${course.name}</option>
             </s:iterator>
           </select>
         </td>
       </tr>
       <tr>
         <td colspan="2">
           <input type="submit" value="<s:text name="general.header.delete" />" class="linkButton" />
         </td>
       </tr>
     </table>
   </form>
</div>