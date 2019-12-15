<%@ include file="/WEB-INF/taglibs.jsp"%>
<pre class="diffCell" style="word-wrap: break-word; white-space: pre-wrap; margin: 0px">${feedback}</pre>
<s:if test="!compilerFeedback">
  <script src="js/grading.js"></script>
  <script>
    $(function () {
      colorDiffFeedback($(".diffCell"));
    });
  </script>
</s:if>