var teamnames = [];
var element = document.getElementById("f-teamnames");
var addBtn = document.getElementById("f-addTeam");
var input = document.getElementById("f-teams");
var submitBtn = document.getElementById("f-submitBtn");

function addTeam() {
    if(input.value !== "") {
        teamnames.push(input.value);
        var div = document.createElement("div");
        div.setAttribute("class", "h-auto alert alert-dismissible alert-success fade show");
        div.setAttribute("style", "display: inline-block; margin-right: 5px");
        var node = document.createTextNode(input.value);
        div.appendChild(node);

        var btn = document.createElement("button");
        btn.setAttribute('type', 'button');
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

        input.value = ''
    }
}
addBtn.addEventListener("click", addTeam);


submitBtn.addEventListener("click" , function() {
   if(teamnames.length > 0)  {
       input.value = teamnames.toString();
   }
});
