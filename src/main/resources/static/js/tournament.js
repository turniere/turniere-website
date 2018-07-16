$(function () {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

$(".changeScoreButton").click(function () {
    var matchID = $(this).parent().parent().parent().attr("aria-controls");
    $.ajax({
        type: "GET",
        url: "/m/" + matchID,
        dataType: "json"
    })
        .done(function (json) {
            $("#modalNameTeam1").text(json.data.name1);
            $("#modalNameTeam2").text(json.data.name2);
            $("#score1Input").val(json.data.score1);
            $("#score2Input").val(json.data.score2);
        });
});

$(".startGameButton").click(function () {
    var matchID = $(this).parent().parent().children().first().text();
    var matchInfo = {
        "live": true,
        "score1": 0,
        "score2": 0
    };
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        type: "POST",
        url: "/m/" + matchID,
        data: JSON.stringify(matchInfo),
        dataType: "json"
    })
        .done(function (json) {
            location.reload();
        });
});

$("#submitScoreButton").click(function () {
    var matchID = $(this).parent().parent().parent().parent().parent().parent().parent().parent().attr("aria-controls");
    var matchInfo = {
        "live": ($("#isLiveInput").is(":checked")),
        "score1": $("#score1Input").val(),
        "score2": $("#score2Input").val()
    };
    $.ajax({
         headers: {
             'Accept': 'application/json',
             'Content-Type': 'application/json'
         },
         type: "POST",
         url: "/m/" + matchID,
         data: JSON.stringify(matchInfo),
         dataType: "json"
     })
         .done(function (json) {
           location.reload();
         })
});



