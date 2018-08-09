var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
//端口是这个application的端口，也就是看application.properties中的设置，默认就是8080, 必须配置成localhost，而不能使用127.0.0.1，否则有跨域问题
//http://localhost:8080/appRoot/websocket
    //var socket = new SockJS('/websocket');

    var sockjs_url = 'http://127.0.0.1:6604/bullet';
    //var sockjs_url = '/websocket';
    var socket = new SockJS(sockjs_url);
    stompClient = Stomp.over(socket);
    //var token = localStorage.getItem('Auth-Token') //
    //stompClient.connect({'Auth-Token': token},
    stompClient.connect({'AuthToken': 'yqbjtu'}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        ///topic/deviceIsLive/
        //stompClient.subscribe('/topic/greetings'+, function (greeting) {
        stompClient.subscribe('/topic/app01', function (greeting) {
            console.log("subscribe edbb:" + greeting);
            showGreeting(JSON.parse(greeting.body).content);
        });

    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

