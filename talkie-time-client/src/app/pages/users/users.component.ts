import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {User, UsersService} from '../../services/users.service';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {WebsocketService} from '../../services/websocket.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    CommonModule,
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {

  @Input() activeUser: User | null = null;
  @Output() userSelected = new EventEmitter<User>();

  users: User[] = [];
  onlineUsers: string[] = [];
  currentUsername: string | null = null;

  constructor(
    private userService: UsersService,
    private router: Router,
    private websocketService: WebsocketService,
    private authService: AuthService
  ) {
    const storedUser = localStorage.getItem('loggedUser');
    this.currentUsername = storedUser ? JSON.parse(storedUser).username : null;
  }

  ngOnInit(): void {
    this.websocketService.onlineUsers$.subscribe(users => {
      this.onlineUsers = users;
    });

    this.userService.getUsers().subscribe(users => {
      // Exclude current user from the list
      this.users = users.filter(u => u.username !== this.currentUsername);
    });
  }

  openChat(user: User) {
    this.userSelected.emit(user);
  }

  isOnline(username: string): boolean {
    return this.onlineUsers.includes(username);
  }

  logout() {
    this.websocketService.disconnect();
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
