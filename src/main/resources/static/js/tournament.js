$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

function updateMatch(matchID, data, cb) {
    $.ajax("/m/" + matchID, {
        type: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(data)
    }).done(cb);
}

function getMatchInfo(matchID, cb) {
   $.ajax("/m/" + matchID, {
       type: "GET",
       dataType: "json"
   }).done(function (data) {
        cb({
            name1: data.data.name1,
            name2: data.data.name2,
            score1: data.data.score1,
            score2: data.data.score2
        });
   });
}

$(".changeScoreButton").click(function () {
    var matchID = $(this).parent().parent().parent().attr("aria-controls");
    $("#pointsModal").attr("data-matchID", matchID);
    getMatchInfo(matchID, function (matchInfo) {
        $("#modalNameTeam1").text(matchInfo.name1);
        $("#modalNameTeam2").text(matchInfo.name2);
        $("#score1Input").val(matchInfo.score1);
        $("#score2Input").val(matchInfo.score2);
        // open modal
        $("#pointsModal").modal('show');
    });
});

$(".startGameButton").click(function () {
    var matchID = $(this).parent().parent().attr("aria-controls");
    var matchInfo = {
        "live": true,
        "score1": 0,
        "score2": 0
    };
    updateMatch(matchID, matchInfo, function () {location.reload()});
});

$("#submitScoreButton").click(function () {
    var matchID = $("#pointsModal").attr("data-matchID");
    var matchInfo = {
        "live": ($("#isLiveInput").is(":checked")),
        "score1": $("#score1Input").val(),
        "score2": $("#score2Input").val()
    };
    updateMatch(matchID, matchInfo, function () {location.reload()});
});



