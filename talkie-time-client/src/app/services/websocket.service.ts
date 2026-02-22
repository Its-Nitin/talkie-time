import {Injectable} from '@angular/core';
import {Client} from '@stomp/stompjs';
import {BehaviorSubject, Subject} from 'rxjs';
import {ChatMessage} from './chat.service';

@Injectable(
  {
    providedIn: 'root'
  }
)
export class WebsocketService {
  private client!: Client;
  private messageSubject = new Subject<any>();
  private onlineUsersSubject = new BehaviorSubject<string[]>([]);
  onlineUsers$ = this.onlineUsersSubject.asObservable();
  private typingSubject = new Subject<any>();
  typing$ = this.typingSubject.asObservable();
  private msgStatusSubject = new Subject<any>();
  msgStatus$ = this.msgStatusSubject.asObservable();

  constructor() { }

  connect(username: string| null) {
    if (!username && !localStorage.getItem('token')) { return; }
    if (this.client && this.client.active) {
      return; // already connected
    }
    this.client = new Client({
      brokerURL: 'ws://localhost:8082/ws',
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      reconnectDelay: 5000,
    });
    this.client.onConnect = () => {
      this.client.subscribe(`/user/queue/messages`, (message) => {
        const msg = JSON.parse(message.body);
        this.messageSubject.next(JSON.parse(message.body));
        this.client.publish({
          destination: '/app/chat.ack',
          body: JSON.stringify({
            messageId: msg.id,
            status: 'DELIVERED'
          })
        });
      });
      // 1. Subscribe FIRST
      this.client.subscribe('/topic/presence', (msg) => {
        const users = JSON.parse(msg.body);
        this.onlineUsersSubject.next(users);
      });

      this.client.subscribe('/user/queue/presence', (msg) => {
        const users = JSON.parse(msg.body);
        this.onlineUsersSubject.next(users);
      });

      this.client.subscribe('/user/queue/typing', (msg) => {
        const event = JSON.parse(msg.body);
        this.typingSubject.next(event);
      });

      // 2. THEN request snapshot
      this.client.publish({
        destination: '/app/presence.ready',
        body: ''
      });

      this.client.subscribe('/user/queue/ack', (msg) => {
        const ack = JSON.parse(msg.body);
        this.msgStatusSubject.next(ack);
      });
    }
    this.client.activate();
  }

  sendMessage(message: any) {
    this.client.publish({destination: '/app/chat.sendMessage', body: JSON.stringify(message)});
  }
  sendTyping(receiverId: string, typing: boolean) {
    this.client.publish({destination: '/app/chat.typing', body: JSON.stringify({receiverId, typing})});
  }

  sendAck(message: ChatMessage) {
    this.client.publish({
      destination: '/app/chat.ack',
      body: JSON.stringify({
        messageId: message.id,
        status: 'SEEN'
      })
    });
  }
  getMessage() {
    return this.messageSubject.asObservable();
  }

  disconnect() {
    if (this.client && this.client.active) {
      this.client.deactivate();
    }
  }
}
