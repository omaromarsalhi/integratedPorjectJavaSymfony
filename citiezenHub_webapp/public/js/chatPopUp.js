function chatInit(selector) {
    document.addEventListener('DOMContentLoaded', () => {
        if (!window.LIVE_CHAT_UI) {
            let chat = document.querySelector(selector);
            let toggle = chat.querySelector('.toggle')
            let close = chat.querySelector('.close')

            // window.setTimeout(() => {
            //     chat.classList.add('is-active')
            // }, 1000)




            close.addEventListener('click', () => {
                chat.classList.remove('is-active')
                setTimeout(function (){
                    chat.classList.remove('specialHide')
                    toggle.classList.add('d-none')
                },310)
            })

            document.onkeydown = function (evt) {
                evt = evt || window.event;
                var isEscape = false;
                if ("key" in evt) {
                    isEscape = (evt.key === "Escape" || evt.key === "Esc");
                } else {
                    isEscape = (evt.keyCode === 27);
                }
                if (isEscape) {
                    chat.classList.remove('is-active')
                    setTimeout(function (){
                        chat.classList.remove('specialHide')
                        toggle.classList.add('d-none')
                    },310)
                }
            };

            window.LIVE_CHAT_UI = true
        }
    })
}

chatInit('#chat-app')

function chatStart(userName,userId,userImg) {
    $('#userImg').prop('src','/usersImg/'+userImg)
    $('#userName').html(userName)
    let chat = document.querySelector('#chat-app');
    let toggle = chat.querySelector('.toggle')
    chat.classList.add('is-active')
    chat.classList.add('specialHide')
    toggle.classList.remove('d-none')
    $('#miniCurrentUserInChat').val(userId)
    toggle.addEventListener('click', () => {
        sendMsg()
        console.log(1)
    })
    loadChatInfoForMiniChat(userId)
}