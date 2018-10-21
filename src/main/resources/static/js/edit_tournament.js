$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

function updateTeam(teamID, data, cb) {
    $.ajax("/team/" + teamID, {
        type: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(data)
    }).done(cb);
}

function getTeamInfo(teamID, cb) {
    $.ajax("/team/" + teamID, {
        type: "GET",
        dataType: "json"
    }).done(function (data) {
        cb({
            name: data.data.name,
        });
    });
}

$(".changeTeamnameButton").click(function () {
    var teamID = $(this).parent().parent().attr("aria-controls");
    $("#teamModal").attr("data-teamID", teamID);
    getTeamInfo(teamID, function (teamInfo) {
        $("#teamName").text(teamInfo.name);
        $("#teamNameInput").val(teamInfo.name);
        // open modal
        $("#teamModal").modal('show');
    });
});

$("#submitTeamnameButton").click(function () {
    var teamID = $("#teamModal").attr("data-teamID");
    var teamInfo = {
        "name": $("#teamNameInput").val()
    };
    updateTeam(teamID, teamInfo, function () {
        location.reload()
    });
});
