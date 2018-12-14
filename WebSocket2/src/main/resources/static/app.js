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
    //var socket = new SockJS('/myEndPoint');

    //var sockjs_url = '/myEndPoint';
    var sockjs_url = 'http://localhost:8088/myEndPoint';

    var socket = new SockJS(sockjs_url);
    stompClient = Stomp.over(socket);
    stompClient.connect({'AuthToken': 'yqbjtu'}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/myPrefixes/topic01/705117a0cf1a487e8df6902dd5ece030', function (greeting) {
            console.log("subscribe topic01---:" + greeting);
            console.log("subscribe greeting.body---:" + greeting.body);
            console.log("subscribe json greeting.body---:" + JSON.parse(greeting.body));
            console.log("subscribe json greeting.body name---:" + JSON.parse(greeting.body).myfield1);
            showGreeting(JSON.parse(greeting.body).myfield1);
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
    stompClient.send("/myPrefixes/topic01/705117a0cf1a487e8df6902dd5ece030", {}, JSON.stringify({'myfield1': $("#name").val()}));
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

