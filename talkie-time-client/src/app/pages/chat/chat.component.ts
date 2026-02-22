import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ChatMessage, ChatService} from '../../services/chat.service';
import {ActivatedRoute} from '@angular/router';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {WebsocketService} from '../../services/websocket.service';
import {debounceTime, Subject, throttleTime} from 'rxjs';

@Component({
  selector: 'app-chat',
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  @ViewChild('scrollMe') private chatContainer!: ElementRef;

  messages: ChatMessage[] = [];
  newMessage = '';

  receiverId!: number;
  currentUser !: any;
  receiverUsername!: string;
  isTyping = false;

  private typingInput$ = new Subject<void>();


  constructor(private route: ActivatedRoute,
              private chatService: ChatService,
              private wsService: WebsocketService
  ) {
  }
  ngOnInit() {
    this.receiverId = Number(this.route.snapshot.paramMap.get('id'));
    this.receiverUsername = this.route.snapshot.paramMap.get('username') || '';
    const storedUser = localStorage.getItem('loggedUser');
    this.currentUser = storedUser ? JSON.parse(storedUser) : null;


    this.wsService.getMessage().subscribe((msg) => {
      console.log(msg);
      if (msg.sender.username === this.receiverUsername || msg.receiver.username === this.receiverUsername) {
        this.messages.push(msg);
        setTimeout(() => this.scrollToBottom(), 50);
      }
    })
    this.wsService.typing$.subscribe((event) => {
      if (event.senderId === this.receiverUsername) {
        this.scrollToBottom()
        this.isTyping = event.typing
      }
    })

    this.wsService.msgStatus$.subscribe((event) => {
      if (event.senderId === this.receiverUsername) {
        this.scrollToBottom()
        this.isTyping = event.typing
      }
    })

    this.typingSetup();
    this.loadMessages();
  }

  private typingSetup() {
    // When user starts typing → emit START once
    this.typingInput$
      .pipe(
        throttleTime(1000)   // prevents flooding
      )
      .subscribe(() => {
        this.wsService.sendTyping(this.receiverUsername, true);
      });

    // When user stops typing → emit STOP after idle
    this.typingInput$
      .pipe(
        debounceTime(1200)   // idle detection
      )
      .subscribe(() => {
        this.wsService.sendTyping(this.receiverUsername, false);
      });
  }

  loadMessages() {
    this.chatService.getHistory(this.receiverId).subscribe({
      next: (messages) =>
      {
        this.messages = messages
        setTimeout(() => this.scrollToBottom(), 500);
      },
      error: () => alert("Failed to load messages")
  });
  }

  sendMessage() {
    if (!this.newMessage.trim()) { return; }
    // this.chatService.sendMessage(this.receiverId, this.newMessage).subscribe(() => this.loadMessages());
    const message = {senderId: this.currentUser.id, receiverId: this.receiverId, message: this.newMessage, status: 'SENT'};
    this.wsService.sendMessage(message);
    this.newMessage = '';
  }

  onTyping() {
    this.typingInput$.next();
  }

  scrollToBottom(): void {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }
}
