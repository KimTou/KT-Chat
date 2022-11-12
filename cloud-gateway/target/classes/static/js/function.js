//服务端地址
let serverUrl = 'http://localhost:8080/'

var userName = '';

//获取聊天记录
function getChatById(friendId){
    var userId = window.localStorage.getItem("userId");
    $.ajax({
        url: serverUrl + "chat/getChatById",
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        data: {
            //userId: $.cookie('userId'),
            userId: window.localStorage.getItem("userId"),
            friendId: friendId
        },
        async: true,
        success: function (data) {
            let contentList = "";
            let messageList =data.data;
            contentList +=
                "<div class='recvfrom'>" +
                "<div class='nav-top'>" +
                "<p>单聊记录</p>" +
                "</div>" +
                "<div class='news-top'>" + "<ul id='news-top-ul'>";
            $.each(messageList, function (i, rs){
                if(messageList[i].sender != userId){
                    contentList += "<li class='other'>" +
                        "<div class='avatar'><img src="+ messageList[i].avatar +"></div>";
                    contentList +=
                        "<div class='msg'>" +
                        "<p class='msg-name'>" + messageList[i].userName +"</p>" +
                        "<p class='msg-text-other'>" + messageList[i].content +"</p>" +
                        "<time>" + messageList[i].gmtCreate + "</time>" +
                        "</div>" + "</li>";
                }else{
                    contentList += "<li class='self'>" +
                        "<div class='avatar'><img src="+ messageList[i].avatar +"></div>";
                    contentList +=
                        "<div class='msg'>" +
                        "<p class='msg-name'>" + messageList[i].userName +"</p>" +
                        "<p class='msg-text-self'>" + messageList[i].content +"</p>" +
                        "<time>" + messageList[i].gmtCreate + "</time>" +
                        "</div>" + "</li>";
                }

            })
            contentList +=
                "</ul>" + "</div>" + "</div>" +
                "<div class=\"sendto\">\n" +
                "                <div class=\"but-text\">\n" +
                "                    <textarea name=\"\" id=\"message\" cols=\"110\" rows=\"6\"></textarea>\n" +
                "                    <a href=\"#\" class=\"button\" onclick=\"sendOne("+ friendId +")\">发送</a>\n" +
                "                    <a href=\"#\" class=\"button\" onclick=\"deleteChat("+ friendId +")\">删除聊天记录</a>\n" +
                "                </div>\n" +
                "            </div>"
            $("#chat-content").html(contentList);
            //$('#delete').val(friendId);
        }
    })
}

//获取群聊天记录
function getGroupChatById(groupId){
    var userId = window.localStorage.getItem("userId");
    $.ajax({
        url: serverUrl + "chat/getGroupChatById",
        method: "GET",
        data: {
            groupId: groupId
        },
        async: true,
        success: function (data) {
            let contentList = "";
            let messageList =data.data;
            contentList +=
                "<div class='recvfrom'>" +
                "<div class='nav-top'>" +
                "<p>群聊记录</p>" +
                "</div>" +
                "<div class='news-top'>" + "<ul id='news-top-ul'>";
            $.each(messageList, function (i, rs){
                if(messageList[i].sender != userId){
                    contentList += "<li class='other'>" +
                        "<div class='avatar'><img src="+ messageList[i].avatar +"></div>";
                    contentList +=
                        "<div class='msg'>" +
                        "<p class='msg-name'>" + messageList[i].userName +"</p>" +
                        "<p class='msg-text-other'>" + messageList[i].content +"</p>" +
                        "<time>" + messageList[i].gmtCreate + "</time>" +
                        "</div>" + "</li>";
                }else{
                    contentList += "<li class='self'>" +
                        "<div class='avatar'><img src="+ messageList[i].avatar +"></div>";
                    contentList +=
                        "<div class='msg'>" +
                        "<p class='msg-name'>" + messageList[i].userName +"</p>" +
                        "<p class='msg-text-self'>" + messageList[i].content +"</p>" +
                        "<time>" + messageList[i].gmtCreate + "</time>" +
                        "</div>" + "</li>";
                }

            })
            contentList +=
                "</ul>" + "</div>" + "</div>" +
                "<div class=\"sendto\">\n" +
                "                <div class=\"but-text\">\n" +
                "                    <textarea name=\"\" id=\"message\" cols=\"110\" rows=\"6\"></textarea>\n" +
                "                    <a href=\"javascript:void(0)\" class=\"button\" onclick=\"sendGroup("+ groupId +")\">发送</a>\n" +
                "                    <a href=\"javascript:void(0)\" class=\"button\" onclick=\"deleteChat("+ groupId +")\">删除聊天记录</a>\n" +
                "                </div>\n" +
                "            </div>"
            $("#chat-content").html(contentList);
        }
    })
}

//显示好友对话框
function refresh(type, boxId) {
    $.ajax({
        url: serverUrl + "chat/getAllChatBox",
        method: "GET",
        data: {
            //userId: $.cookie('userId')
            userId: window.localStorage.getItem("userId")
        },
        async: true,
        success: function (data) {
            let chatList = "";
            let boxList =data.data;
            $.each(boxList, function (i, rs){
                if(boxList[i].type){
                    chatList +=
                        "<div class='list-box' onclick='getGroupChatById("+ boxList[i].group.groupId +")'>"+
                        "<img class='chat-head' src='http://localhost:8080/img/comment-outline.png' alt=''>" +
                        "<div class='chat-rig'>" +
                        "<p class='title'>" + boxList[i].group.groupName + "</p>" +
                        "</div></div>"
                }
                else{
                    chatList +=
                        "<div class='list-box' onclick='getChatById("+ boxList[i].userVo.user.userId +")'>"+
                        "<img class='chat-head' src="+ boxList[i].userVo.user.avatar + ">" +
                        "<div class='chat-rig'>" +
                        "<p class='title'>" + boxList[i].userVo.user.userName + "</p>";
                    if(boxList[i].userVo.status){
                        chatList += "<p>（在线）</p>";
                    }else{
                        chatList += "<p>（离线）</p>";
                    }
                    chatList += "</div></div>";
                }
            })
            $("#chat-list").html(chatList);
            mine();
            if(boxId > 0){
                if(type==1){
                    getGroupChatById(boxId);
                }else{
                    getChatById(boxId)
                }
            }
        }
    })
}



var websocket = new WebSocket("ws://localhost:8080/ws");

//连接成功建立的回调方法
websocket.onopen = function () {
    //getMessage("进入聊天室");
    //var userId=$.cookie('userId');
    var userId = window.localStorage.getItem("userId");
    let data={
        action: 1,
        chatMsg: {
            senderId: userId
        }
    }
    websocket.send(JSON.stringify(data));
}

//接收到消息的回调方法
websocket.onmessage = function (event) {
    var message = eval("(" + event.data + ")");
    getMessage(message);
}

//将消息显示在网页上
function getMessage(message) {
    //let userId = $.cookie('userId');
    let userId = window.localStorage.getItem("userId");
    let userName = window.localStorage.getItem("userName");
    let avatar = window.localStorage.getItem("avatar");
    if(message.senderId === userId){
        document.getElementById('news-top-ul').innerHTML +=
            "<li class='self'>" +
            "<div class='avatar'><img src="+ avatar +"></div>" +
            "<div class='msg'>" +
            "<p class='msg-name'>" + userName +"</p>" +
            "<p class='msg-text-self'>" + message.message +"</p>" +
            //"<time>" + message.gmtCreate + "</time>" +
            "</div>" + "</li>";
    } else{
        document.getElementById('news-top-ul').innerHTML +=
            "<li class='other'>" +
            "<div class='avatar'><img src="+ message.avatar +"></div>" +
            "<div class='msg'>" +
            "<p class='msg-name'>"+ message.userName +"</p>" +
            "<p class='msg-text-other'>" + message.content +"</p>" +
            //"<time>" + message.gmtCreate + "</time>" +
            "</div>" + "</li>";
    }
}

function show(message) {
    let userName = window.localStorage.getItem("userName");
    let avatar = window.localStorage.getItem("avatar");
    document.getElementById('news-top-ul').innerHTML +=
        "<li class='self'>" +
        "<div class='avatar'><img src="+ avatar +"></div>" +
        "<div class='msg'>" +
        "<p class='msg-name'>" + userName +"</p>" +
        "<p class='msg-text-self'>" + message.message +"</p>" +
        "</div>" + "</li>";
}

//发送群消息
function sendGroup(groupId) {
    var message = document.getElementById('message').value;
    //var userId=$.cookie('userId');
    var userId = window.localStorage.getItem("userId");
    let data={
        action: 2,
        chatMsg: {
            senderId: userId,
            receiverId: groupId,
            message: message,
            type: 1
        }
    }
    if(data.message !== '') {
        websocket.send(JSON.stringify(data));
        show(data.chatMsg);
        document.getElementById('message').value = '';
        isBan(userId);
    }else{
        alert("输入框为空");
    }
}

//发送私信
function sendOne(friendId) {
    let message = document.getElementById('message').value;
    //let userId=$.cookie('userId');
    let userId=window.localStorage.getItem("userId");
    /*var map = new Map();
    map.set("sender", userId);
    map.set("receiver", friendId);
    map.set("message", message);
    map.set("type", 2);
    var map2json = Map2Json(map);*/
    let data={
        action: 2,
        chatMsg: {
            senderId: userId,
            receiverId: friendId,
            message: message,
            type: 2
        }
    }
    if(data.message != '') {
        websocket.send(JSON.stringify(data));
        getMessage(data.chatMsg);
        document.getElementById('message').value = '';
        isBan(userId);
    }else{
        alert("输入框为空");
    }
}

function isBan(userId) {
    $.ajax({
        url: serverUrl + "user-info/user/isBan",
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        data: {
            userId: userId
        },
        async: true,
        success: function (data) {
            if (data.code === 200) {

            } else {
                alert("您已被禁言，对方无法接收消息");
            }
        }
    })
}

function Map2Json(map) {
    var str = "{";
    map.forEach(function (value, key) {
        str += '"'+key+'"'+':'+ '"'+value+'",';
    })
    str = str.substring(0,str.length-1)
    str +="}";
    return str;
}

function mine(){
    $.ajax({
        url: serverUrl + "user-info/user/findUserById",
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        data: {
            //userId: $.cookie('userId')
            userId: window.localStorage.getItem("userId")
        },
        async: true,
        success: function (data) {
            userName = data.userName;
        }
    })
    $('#mine').val(userName);
}

//连接关闭的回调方法
websocket.onclose = function () {
    //setMessageInnerHTML("聊天室连接关闭");
    //alert("聊天室连接关闭")
}

//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function () {
    closeWebSocket();
}

//连接发生错误的回调方法
websocket.onerror = function () {
    //setMessageInnerHTML("聊天室连接发生错误");
    alert("聊天室连接发生错误")
}

//关闭WebSocket连接
function closeWebSocket() {
    websocket.close();
}

function deleteChat(friendId){
    $.ajax({
        url: serverUrl + "chat/deleteChat",
        method: "POST",
        data: {
            //userId: $.cookie('userId'),
            userId: window.localStorage.getItem("userId"),
            //friendId: $('#delete').val()
            friendId: friendId
        },
        async: true,
        success: function () {
            getChatById(data.friendId);
        }
    })
}



// 获取上下文路径
function getContextPath() {
    let index = document.location.pathname.substr(1).indexOf("/");
    return document.location.pathname.substr(0,index+1);
}
// 跳转到页面
function toIndex() {
    // 获取上下文路径
    let contextPath = getContextPath();
    //window.location.href = contextPath + "/stu-index.html?sId="+getQueryVariable("sId");
}
//获取参数
function getQueryVariable(variable)
{
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
}
