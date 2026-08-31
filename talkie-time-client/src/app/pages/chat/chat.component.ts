import {Component, ElementRef, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {ChatMessage, ChatService, MessageStatus} from '../../services/chat.service';
import {ActivatedRoute} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {WebsocketService} from '../../services/websocket.service';
import {debounceTime, Subject, Subscription, throttleTime} from 'rxjs';
import {User} from '../../services/users.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit, OnChanges, OnDestroy {
  @ViewChild('scrollMe') private chatContainer!: ElementRef;

  @Input() selectedUser: User | null = null;

  messages: ChatMessage[] = [];
  newMessage = '';

  receiverId: number | null = null;
  currentUser: any = null;
  receiverUsername: string = '';
  isTyping = false;

  private typingInput$ = new Subject<void>();
  private subscriptions: Subscription[] = [];

  constructor(
    private route: ActivatedRoute,
    private chatService: ChatService,
    private wsService: WebsocketService
  ) {}

  ngOnInit() {
    const storedUser = localStorage.getItem('loggedUser');
    this.currentUser = storedUser ? JSON.parse(storedUser) : null;

    // Check route parameters if not passed via @Input
    if (!this.selectedUser) {
      const routeId = this.route.snapshot.paramMap.get('id');
      const routeUsername = this.route.snapshot.paramMap.get('username');
      if (routeId && routeUsername) {
        this.receiverId = Number(routeId);
        this.receiverUsername = routeUsername;
        this.loadMessages();
      }
    }

    // Subscribe to incoming messages
    this.subscriptions.push(
      this.wsService.getMessage().subscribe((msg: ChatMessage) => {
        if (
          (msg.sender?.username === this.receiverUsername && msg.receiver?.username === this.currentUser?.username) ||
          (msg.sender?.username === this.currentUser?.username && msg.receiver?.username === this.receiverUsername)
        ) {
          this.messages.push(msg);
          setTimeout(() => this.scrollToBottom(), 50);

          // Acknowledge SEEN for incoming message if currently chatting with this user
          if (msg.sender?.username === this.receiverUsername) {
            this.wsService.sendAck(msg.id, 'SEEN');
          }
        }
      })
    );

    // Subscribe to typing indicator
    this.subscriptions.push(
      this.wsService.typing$.subscribe((event) => {
        if (event.senderId === this.receiverUsername) {
          this.isTyping = event.typing;
          if (this.isTyping) {
            setTimeout(() => this.scrollToBottom(), 50);
          }
        }
      })
    );

    // Subscribe to message status updates (ACKs: DELIVERED, SEEN)
    this.subscriptions.push(
      this.wsService.msgStatus$.subscribe((ack: { messageId: number; status: MessageStatus }) => {
        const msg = this.messages.find(m => m.id === ack.messageId);
        if (msg) {
          msg.status = ack.status;
        }
      })
    );

    this.typingSetup();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedUser'] && this.selectedUser) {
      this.receiverId = this.selectedUser.id;
      this.receiverUsername = this.selectedUser.username;
      this.isTyping = false;
      this.messages = [];
      this.loadMessages();
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  private typingSetup() {
    this.subscriptions.push(
      this.typingInput$.pipe(throttleTime(1000)).subscribe(() => {
        if (this.receiverUsername) {
          this.wsService.sendTyping(this.receiverUsername, true);
        }
      })
    );

    this.subscriptions.push(
      this.typingInput$.pipe(debounceTime(1200)).subscribe(() => {
        if (this.receiverUsername) {
          this.wsService.sendTyping(this.receiverUsername, false);
        }
      })
    );
  }

  loadMessages() {
    if (!this.receiverId) {
      this.messages = [];
      return;
    }

    this.chatService.getHistory(this.receiverId).subscribe({
      next: (messages) => {
        this.messages = messages;
        setTimeout(() => this.scrollToBottom(), 100);

        // Acknowledge SEEN for loaded unread messages from this sender
        this.messages.forEach(msg => {
          if (msg.sender?.id === this.receiverId && msg.status !== 'SEEN') {
            this.wsService.sendAck(msg.id, 'SEEN');
          }
        });
      },
      error: (err) => console.error("Failed to load messages:", err)
    });
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.receiverId) { return; }

    const messagePayload = {
      senderId: this.currentUser?.id,
      receiverId: this.receiverId,
      message: this.newMessage.trim(),
      status: 'SENT'
    };

    this.wsService.sendMessage(messagePayload);
    this.newMessage = '';
  }

  onTyping() {
    this.typingInput$.next();
  }

  parseTimestamp(createdAt?: string): Date | null {
    if (!createdAt) return null;
    const numeric = Number(createdAt);
    return isNaN(numeric) ? new Date(createdAt) : new Date(numeric);
  }

  scrollToBottom(): void {
    try {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
      }
    } catch (err) { }
  }
}
