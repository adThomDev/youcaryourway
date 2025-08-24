import { Component, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Message, Chat } from '../../models/interface';
import {
  CUSTOMER1,
  CUSTOMER2,
  SUPPORT,
} from '../../constants/user-roles.constants';
import { UserService, UserDTO } from '../../services/user.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnDestroy {
  selectedUser: string = '';
  selectedUserId: number | null = null;
  messages: Message[] = [];
  chats: Chat[] = [];
  interlocutorIds: number[] = [];
  selectedInterlocutor: number | null = null;
  messageContent: string = '';
  private ws: WebSocket | null = null;

  userNames: { [id: number]: string } = {};

  constructor(private userService: UserService) {}

  ngOnDestroy(): void {
    if (this.ws) {
      this.ws.close();
    }
  }

  connectToWebSocket(userId: number): void {
    if (this.ws) {
      this.ws.close();
    }

    const ws = new WebSocket(`ws://localhost:8080/chat?userId=${userId}`);
    this.ws = ws;

    ws.onopen = () => {
      console.log('WebSocket connection established for user:', userId);
    };

ws.onmessage = (event) => {
  console.log('WebSocket received:', event.data);
  const data = JSON.parse(event.data);

  // If data is an array, it's the chat history
  if (Array.isArray(data)) {
    this.selectedInterlocutor = null;
    this.messages = data;

    const chatsMap: { [key: number]: Message[] } = {};
    data.forEach((message: Message) => {
      const interlocutorId =
        message.senderId === this.selectedUserId
          ? message.receiverId
          : message.senderId;

      if (!chatsMap[interlocutorId]) {
        chatsMap[interlocutorId] = [];
      }
      chatsMap[interlocutorId].push(message);
    });

    this.chats = Object.entries(chatsMap).map(([id, msgs]) => ({
      interlocutorId: +id,
      messages: msgs,
    }));

    this.interlocutorIds = this.chats.map((c) => c.interlocutorId);
    this.interlocutorIds.forEach((id) => this.loadUserName(id));

    // if there's only one interlocutor, he's the selectedInterlocutor
    if (this.chats.length === 1) {
      this.selectedInterlocutor = this.chats[0].interlocutorId;
    }

    console.log('Chats séparés :', this.chats);
  }

  // If data is a single message, it's a new incoming message 
  // (that might be your own message you just sent)
  else {
    const message = data as Message;
    const interlocutorId =
      message.senderId === this.selectedUserId
        ? message.receiverId
        : message.senderId;

    let chat = this.chats.find((c) => c.interlocutorId === interlocutorId);
    if (!chat) {
      chat = { interlocutorId, messages: [] };
      this.chats.push(chat);
      this.interlocutorIds.push(interlocutorId);
      this.loadUserName(interlocutorId);
    }

    chat.messages.push(message);
    this.chats = [...this.chats]; //trick to reassign the same data 
    // to trigger Angular's change detection and update the UI
  }
};

    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    ws.onclose = () => {
      console.log('WebSocket connection closed for user:', userId);
    };
  }

  onUserChange(): void {
    console.log('User changed to:', this.selectedUser);
    switch (this.selectedUser) {
      case 'Customer 1':
        this.selectedUserId = CUSTOMER1;
        this.connectToWebSocket(CUSTOMER1);
        break;
      case 'Customer 2':
        this.selectedUserId = CUSTOMER2;
        this.connectToWebSocket(CUSTOMER2);
        break;
      case 'Support':
        this.selectedUserId = SUPPORT;
        this.connectToWebSocket(SUPPORT);
        break;
      default:
        console.log('No user selected');
    }
  }

  getFilteredMessages(): Message[] {
    if (!this.selectedInterlocutor && this.interlocutorIds.length === 1) {
      return this.chats[0].messages;
    }

    const chat = this.chats.find(
      (c) => c.interlocutorId === this.selectedInterlocutor
    );
    return chat ? chat.messages : [];
  }

  getSenderLabel(userId: number): string {
    if (userId === this.selectedUserId) {
      return 'you';
    }
    return this.userNames[userId] || `User ${userId}`;
  }

  private loadUserName(userId: number): void {
    if (this.userNames[userId]) return;

    this.userService.getUserById(userId).subscribe({
      next: (user: UserDTO) => {
        this.userNames[user.userId] = `${user.firstName} ${user.lastName}`;
      },
      error: (err) => {
        console.error(
          `Erreur lors du chargement de l'utilisateur ${userId}:`,
          err
        );
        this.userNames[userId] = `User ${userId}`;
      },
    });
  }

  sendMessage(): void {
    if (!this.messageContent || !this.selectedInterlocutor || !this.ws) {
      console.warn('Cannot send message: no content, no recipient, or WebSocket not connected.');
      return;
    }

    const messageToSend = {
      recipientId: this.selectedInterlocutor,
      content: this.messageContent,
    };

    this.ws.send(JSON.stringify(messageToSend));

    this.messageContent = '';
  }
}