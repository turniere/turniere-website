var teamnames = [];
var element = document.getElementById("f-teamnames");
var addBtn = document.getElementById("f-addTeam");
var input = document.getElementById("f-teams-input");
var output = document.getElementById("f-teams");
var submitBtn = document.getElementById("f-submitBtn");
var groupCheckbox = document.getElementById("f-groupstage");
var groupOptions = document.getElementById("f-groupstage-options");
var errorMessage = document.getElementById("f-teamname-error");

function addTeam() {
    if (input.value !== "") {
        if (teamAlreadyExistend()) {
            showErrorMessage();
        } else {
            teamnames.push(input.value);
            var div = document.createElement("div");
            div.setAttribute("class", "h-auto alert alert-dismissible alert-success shadow-sm fade show");
            div.setAttribute("style", "display: inline-block; margin-right: 5px");
            var node = document.createTextNode(input.value);
            div.appendChild(node);

            var btn = document.createElement("button");
            btn.setAttribute("type", "button");
            btn.setAttribute("class", "close");
            btn.setAttribute("data-dismiss", "alert");
            btn.setAttribute("aria-label", "Close");

            var span = document.createElement("span");
            span.setAttribute("aria-hidden", "true");
            span.innerHTML = "&times;";

            btn.appendChild(span);

            btn.addEventListener("click", function () {
                var firstText = "";
                for (var i = 0; i < div.childNodes.length; i++) {
                    var curNode = div.childNodes[i];
                    if (curNode.nodeName === "#text") {
                        firstText = curNode.nodeValue;
                        break;
                    }
                }
                var index = teamnames.lastIndexOf(firstText);
                teamnames.splice(index, 1);
                element.removeChild(div);
            });

            div.appendChild(btn);
            element.appendChild(div);

            input.value = '';
        }
    }
}

function teamAlreadyExistend() {
    return teamnames.indexOf(input.value) !== -1;
}

addBtn.addEventListener("click", addTeam);
input.addEventListener("keyup", function (ev) {

    if (ev.keyCode === 13) {
        addTeam();
    }
});

submitBtn.addEventListener("click", function () {
    if (teamnames.length > 0) {
        output.value = teamnames.toString();
    }
});

function stopRKey(evt) {
    evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode === 13) && (node.type === "text")) {
        return false;
    }
}

function showErrorMessage() {
    errorMessage.style.display = "block";
    setTimeout(function() {errorMessage.style.display = "none"}, 3000);
}

document.onkeypress = stopRKey;

groupCheckbox.addEventListener('change', function () {
    if (groupOptions.style.display === "none") {
        groupOptions.style.display = "block";
    } else {
        groupOptions.style.display = "none";
    }
});