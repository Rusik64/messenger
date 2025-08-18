'use strict'

const messageForm = document.querySelector('#messageForm');
const username = document.querySelector('#myUsername').innerHTML;
const messagesBlock = document.querySelector('#messages-block');
const messageInput = document.querySelector('#messageInput');
const sendBtn = document.querySelector('#sendBtn');
let currentChatId = null;
console.log(username);
console.log(currentUserId);

let stompClient = null;
const socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.connect({}, onConnect, onError);

function onConnect() {
   stompClient.subscribe(`/user/${username}/queue/messages`, onMessageReceived);
   stompClient.subscribe('/user/public', onMessageReceived);
}

function onError() {
   console.log("Какая-то ошибка.");
}

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
   const userChatResponse = await fetch(`/chat/${userId}`);
   const userChat = await userChatResponse.json();
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
    const notification = querySelector(`#{message.sender.id}`);
    if (notification && !notification.classList.contains('active')) {
        const nbrMsg = notification.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

messageForm.addEventListener('submit', sendMessage, true);
sendBtn.addEventListener('click', sendMessage, true);
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("li").forEach(li => {
        li.addEventListener("click", () => {
            const userId = li.getAttribute('data-userId');
            loadMessages(userId);
        });
    });
});
