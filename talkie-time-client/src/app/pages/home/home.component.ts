import {Component, OnInit} from '@angular/core';
import {WebsocketService} from '../../services/websocket.service';
import {ChatComponent} from '../chat/chat.component';
import {UsersComponent} from '../users/users.component';
import {User} from '../../services/users.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    ChatComponent,
    UsersComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  username: string | null = null;
  selectedUser: User | null = null;

  constructor(private wsService: WebsocketService) {
    const user = localStorage.getItem('loggedUser');
    this.username = user ? JSON.parse(user).username : null;
  }

  ngOnInit(): void {
    if (this.username) {
      this.wsService.connect(this.username);
    }
  }

  onUserSelected(user: User) {
    this.selectedUser = user;
  }
}
