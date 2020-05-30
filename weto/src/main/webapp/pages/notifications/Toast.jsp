<%@ include file="/WEB-INF/taglibs.jsp"%>

<div id="notificationToast" role="alert" aria-live="assertive" aria-atomic="true" style="display: none;">
    <div class="toast-body">
        <span><s:text name="notificationCenter.header.newNotifications" /></span>
        <a id="showToast" href="<s:url action="viewNotifications" />"><s:text name="notificationCenter.header.showToast" /></a>
        <a href="#" onclick="hideToast();"><s:text name="notificationCenter.header.hideToast" /></a>
    </div>
</div>
<script>
    var toastShown = false;
    var toastIntervalSet = false;
    var toastAjaxInterval = 1000 * 60 * 3;
    var toastInitialDelay = 1000 * 30;

    function ajax_toast() {
        if(!toastIntervalSet) {
            setInterval(ajax_toast, toastAjaxInterval);
            toastIntervalSet = true;
        }

        $.ajax('<s:url action="getJSONNotifications" />', {
            method: "GET",
            success: function(data) {
                if(data.newNotifications && !toastShown) {
                    $("#notificationToast").fadeIn();
                    $("#notification-center-button .glyphicon").addClass('unreadNotifications');
                    toastShown = true;
                } else if(!data.newNotifications) {
                    toastShown = false;
                }
            }
        });
    }

    function hideToast() {
        $("#notificationToast").fadeOut();
    }

    $(document).ready(function() {
        setTimeout(ajax_toast, toastInitialDelay);
    });
</script>