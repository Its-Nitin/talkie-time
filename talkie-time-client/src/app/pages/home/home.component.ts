import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {WebsocketService} from '../../services/websocket.service';
import {ChatComponent} from '../chat/chat.component';
import {UsersComponent} from '../users/users.component';

@Component({
  selector: 'app-home',
  imports: [
    ChatComponent,
    UsersComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  username: string | null = null;
  constructor(private wsService: WebsocketService) {
    const user = localStorage.getItem('loggedUser');
    this.username = user ? JSON.parse(user).username : null;
  }
  ngOnInit(): void {
    const storedUser = localStorage.getItem('loggedUser');

    const user = storedUser ? JSON.parse(storedUser) : null;

    if (user) {
      this.wsService.connect(user.username);
    }
  }
}
