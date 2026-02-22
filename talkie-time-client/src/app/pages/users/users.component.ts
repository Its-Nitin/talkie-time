import {Component, inject, OnInit} from '@angular/core';
import {User, UsersService} from '../../services/users.service';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {WebsocketService} from '../../services/websocket.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-users',
  imports: [
    CommonModule,
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit{

  users: User[] = [];
  onlineUsers: string[] = [];

  constructor(private userService: UsersService, private router: Router,private websocketService: WebsocketService) { }
  ngOnInit(): void {
    this.websocketService.onlineUsers$.subscribe(users => {
      this.onlineUsers = users;
    });

    this.userService.getUsers().subscribe(users => this.users = users);
  }
  openChat(user: User) {
    this.router.navigate(['/chat',user.id, user.username]);
  }

  isOnline(userId: string): boolean {
    return this.onlineUsers.includes(userId);
  }
  logout() {
    this.websocketService.disconnect();
    inject(AuthService).logout();
    this.router.navigate(['/login']);
  }
}
