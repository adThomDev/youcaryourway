import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

// Import user role constants
import {
  CUSTOMER1,
  CUSTOMER2,
  SUPPORT,
} from '../../constants/user-roles.constants';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss'],
})
export class ChatComponent {
  selectedUser: string = '';
  messages: {
    content: string;
    sentAt: string;
    senderId: number;
    receiverId: number;
  }[] = [];

  selectedUserId: number = 0;

  connectToWebSocket(userId: number): void {
    const ws = new WebSocket(`ws://localhost:8080/chat?userId=${userId}`);

    ws.onopen = () => {
      console.log('WebSocket connection established for user:', userId);
    };

    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      this.messages = data;
      console.log('Received data:', data);
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
      case 'customer1':
        {
          this.selectedUserId = CUSTOMER1;
          this.connectToWebSocket(CUSTOMER1);
        }
        
        break;
      case 'customer2':
        this.selectedUserId = CUSTOMER2;
        this.connectToWebSocket(CUSTOMER2);
        break;
      case 'support':
        this.selectedUserId = SUPPORT;
        this.connectToWebSocket(SUPPORT);
        break;
      default:
        console.log('No user selected');
    }
  }

  getSenderLabel(senderId: number): string {
    if (senderId === this.selectedUserId) return 'you';
    else if (senderId === CUSTOMER1) return 'customer1';
    if (senderId === CUSTOMER2) return 'customer2';
    if (senderId === SUPPORT) return 'support';
    return 'unknown';
  }

}
