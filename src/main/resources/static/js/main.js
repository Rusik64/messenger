'use strict';

const messageForm = document.querySelector('#messageForm');
const username = document.querySelector('#myUsername')?.innerHTML?.trim();
const messagesBlock = document.querySelector('#messages-block');
const messageInput = document.querySelector('#messageInput');
const messageInputBlock = document.querySelector('#message-input-block');
const sendBtn = document.querySelector('#sendBtn');
const chatBlock = document.querySelector('#chat-block');
const noFrBlock = document.querySelector('#not-friend-block');
const profileBlock = document.querySelector('#profile-block');
const usernameBlock = document.querySelector('#username');
const fullnameBlock = document.querySelector('#fullname');
const birthdayBlock = document.querySelector('#birthday');
const emailBlock = document.querySelector('#email');
const searchInput = document.querySelector('#users-search');
const searchResult = document.querySelector('.users-result');
const friendsList = document.querySelector('#friends-list');
const friendsListBlock = document.querySelector('#users-list');
const addFriendBlock = document.querySelector('.no-req-block');
const myReqBlock = document.querySelector('.waiting-my-req-block');
const reqToMeBlock = document.querySelector('.waiting-req-to-me-block');
const deleteFriendBlock = document.querySelector('.delete-friend');
const addFriendBtn = document.querySelector('.add-friend');
const acceptReqBtn = document.querySelector('.accept-req');
const deleteFriendBtn = document.querySelectorAll('.delete-req');
const backBtn = document.getElementById('backBtn');

let currentChatId = null;
let friendReqStatus = null;
let selectedUserId = null;

console.log("Username:", username);
console.log("Current user ID:", currentUserId);

let stompClient = null;
const socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.connect({}, onConnect, onError);

function onConnect() {
    stompClient.subscribe(`/user/queue/messages`, onMessageReceived);
    console.log("WebSocket connected");
}

function onError(err) {
    console.error("WebSocket error:", err);
}

async function sendReport(id) {
    try {
        await fetch(`/send-report/${id}`, { method: 'POST' });
        console.log("Report sent for message:", id);
    } catch (err) {
        console.error("Ошибка при отправке жалобы:", err);
    }
}

function displayMessage(sender, content, id) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');

    if (username === sender) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }

    const message = document.createElement('p');
    message.textContent = content;
    message.style.margin = 0;

    if (username !== sender) {
        const flexWrapper = document.createElement('div');
        flexWrapper.style.display = 'flex';
        flexWrapper.style.justifyContent = 'space-between';
        flexWrapper.style.alignItems = 'center';

        flexWrapper.appendChild(message);

        const reportBtn = document.createElement('span');
        reportBtn.textContent = '⚠️';
        reportBtn.dataset.id = id;
        reportBtn.title = 'Пожаловаться';
        reportBtn.addEventListener('click', () => sendReport(reportBtn.dataset.id));

        flexWrapper.appendChild(reportBtn);
        messageContainer.appendChild(flexWrapper);
    } else {
        messageContainer.appendChild(message);
    }

    messagesBlock.appendChild(messageContainer);
    messagesBlock.scrollTop = messagesBlock.scrollHeight;
}

async function loadMessages(userId) {
    if (!userId) return console.warn("userId отсутствует");

    messageInputBlock.classList.add('hidden');
    noFrBlock.classList.add('hidden');

    try {
        const userChatResponse = await fetch(`/chat/${userId}`);
        const userChat = await userChatResponse.json();

        chatBlock.classList.remove('hidden');
        currentChatId = userChat.id;
        console.log("Открыт чат:", currentChatId, "Статус:", userChat.status);

        // Показываем нужный блок
        if (userChat.status === 3) messageInputBlock.classList.remove('hidden');
        else noFrBlock.classList.remove('hidden');

        messagesBlock.innerHTML = '';
        userChat.messages.forEach(msg => {
            displayMessage(msg.sender.username, msg.content, msg.id);
        });
        messagesBlock.scrollTop = messagesBlock.scrollHeight;
    } catch (err) {
        console.error("Ошибка при загрузке чата:", err);
    }
}

function sendMessage(event) {
    event.preventDefault();
    const inputContent = messageInput.value.trim();
    if (!inputContent || !stompClient || friendReqStatus !== 3) return;

    const chatMessage = {
        content: inputContent,
        senderId: currentUserId,
        chatRoomId: currentChatId
    };

    stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
    displayMessage(username, inputContent);
    messageInput.value = '';
    messagesBlock.scrollTop = messagesBlock.scrollHeight;
}

async function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    if (!message) return;

    if (currentChatId && currentChatId === message.chatRoom.id) {
        displayMessage(message.sender.username, message.content, message.id);
        messagesBlock.scrollTop = messagesBlock.scrollHeight;
    }

    const notification = document.querySelector(`#${message.sender.id}`);
    if (notification && !notification.classList.contains('active')) {
        const nbrMsg = notification.querySelector('.nbr-msg');
        nbrMsg?.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

async function getProfile(userId) {
    if (!userId) return console.warn("userId отсутствует");

    [addFriendBlock, myReqBlock, reqToMeBlock, deleteFriendBlock]
        .forEach(el => el.classList.add('hidden'));

    selectedUserId = userId;

    try {
        const resp = await fetch(`/${userId}`);
        const profile = await resp.json();

        friendReqStatus = profile.status;
        profileBlock.classList.remove('hidden');

        fullnameBlock.textContent = profile.firstname + ' ' + profile.secondname;
        usernameBlock.textContent = profile.username || '';
        if (profile.birthday != null) {
            birthdayBlock.textContent = "День рождения: " + profile.birthday;
        }
        else {
        birthdayBlock.textContent = ' '};


        switch (profile.status) {
            case 0: addFriendBlock.classList.remove('hidden'); break;
            case 1: myReqBlock.classList.remove('hidden'); break;
            case 2: reqToMeBlock.classList.remove('hidden'); break;
            case 3: deleteFriendBlock.classList.remove('hidden'); break;
        }
    } catch (err) {
        console.error("Ошибка при получении профиля:", err);
    }
}

async function addFriend(userId) {
    await fetch(`/send-request/${userId}`, { method: 'POST' });
    addFriendBlock.classList.toggle('hidden');
    myReqBlock.classList.toggle('hidden');
}

async function acceptReq(userId) {
    await fetch(`/accept-req/${userId}`, { method: 'POST' });
    reqToMeBlock.classList.toggle('hidden');
    deleteFriendBlock.classList.toggle('hidden');
    noFrBlock.classList.add('hidden');
    messageInputBlock.classList.remove('hidden');
}

async function deleteReq(userId, event) {
    await fetch(`/del-req/${userId}`, { method: 'POST' });
    if (event.target.classList.contains('delete-friend')) {
        deleteFriendBlock.classList.toggle('hidden');
    }
    addFriendBlock.classList.toggle('hidden');
    noFrBlock.classList.remove('hidden');
    messageInputBlock.classList.add('hidden');
}

async function getFriends(userId, event) {
    const friendsReq = await fetch(`/${userId}/friends`);
    const friendsReqList = await friendsReq.json();
    friendsReqList.forEach(u => {
        const li = document.createElement('li');
        li.textContent = u.firstname || u.username;
        li.dataset.userId = u.id;
        li.classList.add('user', 'list-group-item');
        li.addEventListener('click', () => {
            loadMessages(u.id);
            getProfile(u.id);
        });
        friendsList.appendChild(li);
    });
}

async function getReqs(userId) {
    const friendsReqs = await fetch(`/${userId}/requests`);
    const friendsReqsList = await friendsReqs.json();
    friendsReqsList.forEach(u => {
        const li = document.createElement('li');
        li.textContent = u.firstname || u.username;
        li.dataset.userId = u.id;
        li.classList.add('user', 'list-group-item');
        li.addEventListener('click', () => {
            loadMessages(u.id);
            getProfile(u.id);
        });
        friendsList.appendChild(li);
    });
}
async function refreshFriends() {
    friendsList.innerHTML = '';
    await getFriends(currentUserId);
    await getReqs(currentUserId);
}
refreshFriends();
setInterval(refreshFriends, 30000);

messageForm.addEventListener('submit', sendMessage);
sendBtn.addEventListener('click', sendMessage);

searchInput.addEventListener('focus', () => {
    searchResult.classList.remove('hidden');
    friendsListBlock.classList.add('hidden');
    if (backBtn) backBtn.style.display = 'inline-block';
});

searchInput.addEventListener('input', async () => {
    searchResult.innerHTML = '';
    const query = searchInput.value.trim();
    if (!query) return;

    try {
        const resp = await fetch(`/users/search?query=${query}`);
        const list = await resp.json();

        list.forEach(u => {
            const li = document.createElement('li');
            li.textContent = u.firstname || u.username;
            li.dataset.userId = u.id;
            li.classList.add('user', 'list-group-item');
            li.addEventListener('click', () => {
                loadMessages(u.id);
                getProfile(u.id);
            });
            searchResult.appendChild(li);
        });
    } catch (err) {
        console.error("Ошибка при поиске:", err);
    }
});

if (backBtn) {
    backBtn.addEventListener('click', () => {
        searchResult.innerHTML = '';
        searchResult.classList.add('hidden');
        friendsListBlock.classList.remove('hidden');
        backBtn.style.display = 'none';
        searchInput.value = '';
    });
}

addFriendBtn?.addEventListener('click', () => addFriend(selectedUserId));
acceptReqBtn?.addEventListener('click', () => acceptReq(selectedUserId));
deleteFriendBtn.forEach(btn => {
    btn.addEventListener('click', e => deleteReq(selectedUserId, e));
});
