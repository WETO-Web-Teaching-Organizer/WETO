<%@ include file="/WEB-INF/taglibs.jsp"%>

<div id="notificationToast" role="alert" aria-live="assertive" aria-atomic="true" style="display: none;">
    <div class="toast-body">
        <span><s:text name="notificationCenter.header.newNotifications" /></span>
        <a id="showToast" href="<s:url action="viewNotifications" />"><s:text name="notificationCenter.header.showToast" /></a>
        <a href="#" onclick="hideToast();"><s:text name="notificationCenter.header.hideToast" /></a>
    </div>
</div>
<script>
    function ajax_toast() {
        $.ajax('<s:url action="getJSONNotifications" />', {
            method: "GET",
            success: function(data) {
                console.log(data);
                if(data.newNotifications) {
                    $("#notificationToast").fadeIn();
                    setTimeout(hideToast, 1000 * 15);

                    $("#notification-center-button .glyphicon").addClass('unreadNotifications');
                }
            }
        });
    }

    function hideToast() {
        $("#notificationToast").fadeOut();
    }

    $(document).ready(function() {
        var interval = 1000 * 60 * 5; // X is your minutes
        setInterval(ajax_toast, interval);
    });
</script>