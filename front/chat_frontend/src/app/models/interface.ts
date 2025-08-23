export interface Message {
  content: string;
  sentAt: string;
  senderId: number;
  receiverId: number;
}

export interface Chat {
  interlocutorId: number;
  messages: Message[];
}
