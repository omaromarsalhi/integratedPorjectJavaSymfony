$(document).ready(function () {
});


function receiveMsg(messageData) {
    const {senderId, message, recipientId} = messageData;
    let reciverId = $('#currentUserInChat').attr('data-value');
    let skip = true;
    // if (reciverId == senderId && recipientId == currentUser) {
    if (recipientId == currentUser) {
        if (document.getElementById('chatContainer') && reciverId == senderId) {
            changeView()
            $('#chatContainer').append('<div class="row no-gutters ">\n' +
                '                        <div class="dynamic-resizing">\n' +
                '                            <div class="chat-bubble chat-bubble--left">\n' +
                '                                ' + message + ' ' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>')
            $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
            skip = false
        }
        if (document.getElementById('miniChatContainer')) {

            changeView()

            $('#miniChatContainer').append('<div class="message reply">\n' +
                '                        <p class="text">' + message + ' ' +
                '                        </p>' +
                '                    </div>')

            $("#miniChatContainer2").scrollTop($("#miniChatContainer2")[0].scrollHeight);
            skip = false
        }
    }
    if (senderId == currentUser) {
        if (document.getElementById('chatContainer')) {
            $('#chatContainer').append('<div class="row no-gutters ">\n' +
                '                        <div class="dynamic-resizing-reverse right">\n' +
                '                            <div class="chat-bubble chat-bubble--right">\n' +
                '                                ' + message + ' ' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>')
            $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
            $('#msgToSend').val('');
        }
        if (document.getElementById('miniChatContainer')) {
            $('#miniChatContainer').append('<div class="message">\n' +
                '                        <p class="text">' + message + ' ' +
                '                        </p>' +
                '                    </div>')
            $("#miniChatContainer2").scrollTop($("#miniChatContainer2")[0].scrollHeight);
            $('#miniMsgToSend').val('');
        }
    }
    if (skip) {
        let out = false
        let numberOfJumps = 0
        let count = 0

        const today = new Date();
        const hours = today.getHours();
        const minutes = today.getMinutes();

        const currentTime = `${hours}:${minutes}`;
        console.log(currentTime)
        while (!out) {
            let user = document.getElementById("list_user_" + count)
            if (user) {
                let userId = $('#list_user_' + count).attr('data-value').split(':')[1]
                if (userId == senderId) {
                    $('#userLastMessage_' + count).html(message)
                    $('#userLastMessage_' + count).addClass("show")
                    $('#userLastMessageTime_' + count).html(currentTime)
                    $('#userLastMessageTime_' + count).addClass("show")
                    out = true
                }
            } else if (numberOfJumps > 1)
                out = true
            else
                numberOfJumps++
            count++
        }
    }
};

function changeView() {
    $.ajax({
        url: "/chat/view",
        type: "POST",
        async: true,
        success: function (response) {
            console.log(response)
        },
    });
}

function sendMsg() {

    let reciverId
    let message
    if ($('#msgToSend').val()||$('#miniMsgToSend').val()) {
        if (document.getElementById('chatContainer')) {
            message = $('#msgToSend').val();
            $('#chatContainer').append('<div class="row no-gutters ">\n' +
                '                        <div class="dynamic-resizing-reverse right">\n' +
                '                            <div class="chat-bubble chat-bubble--right">\n' +
                '                                ' + message + ' ' +
                '                            </div>\n' +
                '                        </div>\n' +
                '                    </div>')
            $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
            $('#msgToSend').val('');
            reciverId = $('#currentUserInChat').attr('data-value');
        }
        if (document.getElementById('miniChatContainer')) {
            message = $('#miniMsgToSend').val();
            $('#miniChatContainer').append('<div class="message">\n' +
                '                        <p class="text">' + message + ' ' +
                '                        </p>' +
                '                    </div>')
            $("#miniChatContainer2").scrollTop($("#miniChatContainer2")[0].scrollHeight);
            $('#miniMsgToSend').val('');
            reciverId = $('#miniCurrentUserInChat').val();
        }


        let msg = {
            'action': 'chat',
            'message': message,
            'senderId': currentUser,
            'recipientId': reciverId
        }

        socket.send(JSON.stringify(msg));

        $.ajax({
            url: "/chat/new",
            type: "POST",
            data: {
                reciverId: reciverId,
                msg: msg,
            },
            async: true,
            success: function (response) {

            },
        });
    }

}


function loadChatInfoForMiniChat(idReciver) {
    $.ajax({
        url: "/chat/getData",
        type: "POST",
        data: {
            idReciver: idReciver,
            idSender: currentUser
        },
        async: true,
        success: function (response) {
            $('#miniChatContainer').html('')
            for (let i = 0; i < response.messages.length; i++) {
                if (response.messages[i][2]) {
                    $('#miniChatContainer').append('<div class="message">\n' +
                        '                        <p class="text">' + response.messages[i][0] + ' ' +
                        '                        </p>' +
                        '                    </div>')
                    $("#miniChatContainer2").scrollTop($("#miniChatContainer2")[0].scrollHeight);
                } else {
                    $('#miniChatContainer').append('<div class="message reply">\n' +
                        '                        <p class="text">' + response.messages[i][0] + ' ' +
                        '                        </p>' +
                        '                    </div>')
                    $("#miniChatContainer2").scrollTop($("#miniChatContainer2")[0].scrollHeight);
                }
            }

        },
    });
}


function loadChatInfo() {
    let index = document.getElementById('#list_user_0')
    let idReciver = 0
    if (index) {
        console.log('here')
        idReciver = $('#list_user_0').data('value').split(':')[1]
    } else {
        console.log('here2')
        idReciver = $('#list_user_0').data('value').split(':')[1]
    }

    $.ajax({
        url: "/chat/getData",
        type: "POST",
        data: {
            idReciver: parseInt(idReciver),
            idSender: currentUser
        },
        async: true,
        success: function (response) {
            console.log(response)
            $('#chatContainer').html('')
            for (let i = 0; i < response.messages.length; i++) {
                if (response.messages[i][2]) {
                    $('#chatContainer').append('<div class="row no-gutters ">\n' +
                        '                        <div class="dynamic-resizing-reverse right">\n' +
                        '                            <div class="chat-bubble chat-bubble--right">\n' +
                        '                                ' + response.messages[i][0] + ' ' +
                        '                            </div>\n' +
                        '                        </div>\n' +
                        '                    </div>')
                    $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
                } else {
                    $('#chatContainer').append('<div class="row no-gutters ">\n' +
                        '                        <div class="dynamic-resizing">\n' +
                        '                            <div class="chat-bubble chat-bubble--left">\n' +
                        '                                ' + response.messages[i][0] + ' ' +
                        '                            </div>\n' +
                        '                        </div>\n' +
                        '                    </div>')
                    $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
                }
            }

        },
    });
}


$('.friend-drawer--onhover').on('click', function () {
    $('.chat-bubble').hide('slow').show('slow');

    let index = $(this).data('value').split(':')[0]
    let idReciver = $(this).data('value').split(':')[1]
    let imageUser = $('#userImg_' + index).attr("src");
    let userName = $('#userFullName_' + index).html();

    $('#currentUserInChatImg').attr('src', imageUser)
    $('#currentUserInChatName').html(userName)
    $('#currentUserInChat').attr('data-value', $(this).data('value').split(':')[1]);
    $('#userLastMessageTime_' + index).removeClass("show")
    $('#userLastMessage_' + index).removeClass("show")

    $.ajax({
        url: "/chat/getData",
        type: "POST",
        data: {
            idReciver: parseInt(idReciver),
            idSender: currentUser
        },
        async: true,
        success: function (response) {
            $('#chatContainer').html('')
            for (let i = 0; i < response.messages.length; i++) {
                if (response.messages[i][2]) {
                    $('#chatContainer').append('<div class="row no-gutters ">\n' +
                        '                        <div class="dynamic-resizing-reverse right">\n' +
                        '                            <div class="chat-bubble chat-bubble--right">\n' +
                        '                                ' + response.messages[i][0] + ' ' +
                        '                            </div>\n' +
                        '                        </div>\n' +
                        '                    </div>')
                    $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
                } else {
                    $('#chatContainer').append('<div class="row no-gutters ">\n' +
                        '                        <div class="dynamic-resizing">\n' +
                        '                            <div class="chat-bubble chat-bubble--left">\n' +
                        '                                ' + response.messages[i][0] + ' ' +
                        '                            </div>\n' +
                        '                        </div>\n' +
                        '                    </div>')
                    $("#chatContainer").scrollTop($("#chatContainer")[0].scrollHeight);
                }
            }
        },
    });

});