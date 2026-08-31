import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

export type MessageStatus = 'SENT' | 'DELIVERED' | 'SEEN';
export interface ChatMessage {
  id?: number;
  sender: any;
  receiver: any;
  message: string;
  createdAt?: string;
  status?: MessageStatus;
}
@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getHistory(userId: number){
    return this.http.get<ChatMessage[]>(`${this.apiUrl}/chat/messages-history/${userId}`);
  }

  sendMessage(receiverId: number, message: string) {
    return this.http.post(`${this.apiUrl}/chat/send-message`, {
      receiverId,
      message
    });
  }
}
