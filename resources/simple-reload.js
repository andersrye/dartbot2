function sayHello() {
    console.log("hello world!");
}
var socket;
var xmlhttp = new XMLHttpRequest();
xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState==4 && xmlhttp.status==200) {
        document.getElementById("top").innerHTML=xmlhttp.responseText;
    }
};

function connect() {
    socket = new WebSocket("ws://localhost:4440/ws");
    socket.onmessage = function(event) {
        console.log(event.data);
        xmlhttp.open("GET", "lite", true);
        xmlhttp.send();
    };
    socket.onopen = function(event) {
        console.log("websocket up!");
        console.log(event.data);
    };
}
