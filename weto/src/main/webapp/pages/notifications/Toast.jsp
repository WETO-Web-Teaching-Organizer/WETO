<%@ include file="/WEB-INF/taglibs.jsp"%>
<head>
    <meta charset="UTF-8">
</head>
<body>
    <tiles:insertAttribute name = "body"/>
    <div class="showToast" role="alert" aria-live="assertive" aria-atomic="true"><tiles:insertAttribute name="showToast"/>
        <div class="toast-header">
            <img src="..." class="rounded mr-2" alt="...">
            <strong class="mr-auto"><s:text name="notificationCenter.header.newNotifications" /></strong>
            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <button type="button" id="showToast" onClick="showToast();"><s:text name="notificationCenter.header.showToast"></s:text></button>
        <div class="toast-body">
            You have a new notification!
        </div>
    </div>
    <script>
        var ajax_toast = function() {
            $("#showToast").click(function(event) {
                event.preventDefault();
                window.open("NotificationCenter.jsp");
            });
        }
        var interval = 1000 * 60 * 5; // X is your minutes
        setInterval(ajax_toast, interval);
    </script>
</body>