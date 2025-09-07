'use strict'

const messageForm = document.querySelector('#messageForm');
let username = document.querySelector('#myUsername').innerHTML;
const messagesBlock = document.querySelector('#messages-block');
const messageInput = document.querySelector('#messageInput');
const sendBtn = document.querySelector('#sendBtn');
const chatBlock = document.querySelector('#chat-block');
const profileBlock = document.querySelector('#profile-block');
const usernameBlock = document.querySelector('#username');
const emailBlock = document.querySelector('#email');
const searchInput = document.querySelector('#users-search');
const searchResult = document.querySelector('.users-result');
const friendsList = document.querySelector('#users-list-block');
const addFriendBlock = document.querySelector('.no-req-block');
const myReqBlock = document.querySelector('.waiting-my-req-block');
const reqToMeBlock = document.querySelector('.waiting-req-to-me-block');
const deleteFriendBlock = document.querySelector('.delete-friend');
const addFriendBtn = document.querySelector('.add-friend');
const acceptReqBtn = document.querySelector('.accept-req');
const deleteFriendBtn = document.querySelectorAll('.delete-req');

let currentChatId = null;
let friendReqStatus = null;
let selectedUserId = null;
console.log(username);
console.log(currentUserId);

let stompClient = null;
const socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.connect({}, onConnect, onError);

//stompClient.subscribe(`/user/${username}/queue/messages`, (msg) => {
//   const notification = JSON.parse(msg.body);
//   if (notification.chatRoomId === currentChatRoomId) {
//        displayMessage(notification.senderId, notification.content);
//   }
//   });

function onConnect() {
   stompClient.subscribe(`/user/${username}/queue/messages`, onMessageReceived);
   stompClient.subscribe('/user/public', onMessageReceived);
}

function onError() {
   console.log("Какая-то ошибка.");
}

//async function loadReqs(userId) {
//    const friendsReq = await fetch(`/${userId}/friends`);
//    const frReq = await fetch(`/${userId}/requests`);
//    const friends = await friendsReq.json();
//    const reqs = await frReq.json();
//    friends.forEach(f => {
//        const userBlock = document.createElement('div');
//        const
//    })
//}

function displayMessage(sender, content) {
   const messageContainer = document.createElement('div');
   messageContainer.classList.add('message');
   if (username === sender) {
      messageContainer.classList.add('sender');
   } else {
      messageContainer.classList.add('receiver');
   }
   const message = document.createElement('p');
   message.textContent = content;
   messageContainer.appendChild(message);
   messagesBlock.appendChild(messageContainer);
}

async function loadMessages(userId) {
   console.log(userId);
   const userChatResponse = await fetch(`/chat/${userId}`);
   const userChat = await userChatResponse.json();
   if (chatBlock.classList.contains('hidden')) {
        chatBlock.classList.remove('hidden');
   }
   currentChatId = userChat.id;
   console.log(currentChatId);
   messagesBlock.innerHTML = '';
   userChat.messages.forEach(msg => {
      displayMessage(msg.sender.username, msg.content);
   });
   messagesBlock.scrollTop = messagesBlock.scrollHeight;
}

function sendMessage(event) {
   const inputContent = messageInput.value.trim();
   if (inputContent && stompClient) {
      const chatMessage = {
         content: inputContent,
         senderId: currentUserId,
         chatRoomId: currentChatId
      };
      stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
      displayMessage(currentUserId, inputContent);
   }
   messageInput.value = '';
   messagesBlock.scrollTop = messagesBlock.scrollHeight;
   event.preventDefault();
}

async function onMessageReceived(payload) {
    const message = JSON.parse(payload);
    if (currentChatId && currentChatId === message.chatRoom.id) {
        displayMessage(message.sender.username, message.content);
        messagesBlock.scrollTop = messagesBlock.scrollHeight;
    }
//    if (currentChatId) {
//    document.querySelector('')
//    }
    const notification = querySelector(`#${message.sender.id}`);
    if (notification && !notification.classList.contains('active')) {
        const nbrMsg = notification.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

async function getProfile(userId) {
    addFriendBlock.classList.add('hidden');
    myReqBlock.classList.add('hidden');
    reqToMeBlock.classList.add('hidden');
    deleteFriendBlock.classList.add('hidden');
    selectedUserId = userId;
    const resp = await fetch(`/${userId}`);
    const profile = await resp.json();
    if (profileBlock.classList.contains('hidden')) {
        profileBlock.classList.remove('hidden');
    }
    usernameBlock.textContent = '';
    emailBlock.textContent = '';
    usernameBlock.textContent = profile.username;
    emailBlock.textContent = profile.email;
    if (profile.status == 0) {
        addFriendBlock.classList.remove('hidden');
    }
    if (profile.status == 1) {
        myReqBlock.classList.remove('hidden');
    }
    if (profile.status == 2) {
        reqToMeBlock.classList.remove('hidden');
    }
    if (profile.status == 3) {
        deleteFriendBlock.classList.remove('hidden');
    }
}

async function addFriend(userId) {
    await fetch(`/send-request/${userId}`, {method: 'POST'});
    addFriendBlock.classList.toggle('hidden');
    myReqBlock.classList.toggle('hidden');
}

async function acceptReq(userId) {
    await fetch(`/accept-req/${userId}`, {method: 'POST'});
    reqToMeBlock.classList.toggle('hidden');
    deleteFriendBlock.classList.toggle('hidden');
}

async function deleteReq(userId, event) {
    await fetch(`/del-req/${userId}`, {method: 'POST'});
    if (event.target.classList == 'delete-friend') {
        deleteFriendBlock.classList.toggle('hidden');
    }
    addFriendBlock.classList.toggle('hidden');
}

messageForm.addEventListener('submit', sendMessage, true);
sendBtn.addEventListener('click', sendMessage, true);
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".user").forEach(li => {
        li.addEventListener("click", () => {
            let userId = li.dataset.userid;
            loadMessages(userId);
            getProfile(userId);
        });
    });
});
searchInput.addEventListener('click', () => {
    searchResult.classList.toggle('hidden');
    friendsList.classList.toggle('hidden');
});
searchInput.addEventListener('input', async () => {
    searchResult.innerHTML = '';
    const query = searchInput.value.trim();
    console.log(query);
    if (!query) {
    searchResult.innerHTML = '';
    return;
    }

    const resp = await fetch(`/users/search?query=${query}`);
    const list = await resp.json();

    list.forEach(u => {
    const usersListBlock = document.createElement('div');
    const usersList = document.createElement('ul');
    let usersListLi = document.createElement('li');
    usersListLi.dataset.userId = u.id;
    usersListLi.addEventListener('click', () => {
//    const userId = usersListLi.dataset.userId;
    loadMessages(usersListLi.dataset.userId);
    getProfile(usersListLi.dataset.userId);
    });
    const searchResultUsername = document.createElement('div');
    const username = document.createElement('span');
    username.textContent = u.username;
    searchResultUsername.appendChild(username);
    usersListLi.appendChild(searchResultUsername);
    usersList.appendChild(usersListLi);
    usersListBlock.appendChild(usersList);
    searchResult.appendChild(usersListBlock);
    });
});
addFriendBtn.addEventListener('click', () => addFriend(selectedUserId));
acceptReqBtn.addEventListener('click', () => acceptReq(selectedUserId));
deleteFriendBtn.forEach(btn => {
    btn.addEventListener('click', (e) => deleteReq(selectedUserId, e));
});