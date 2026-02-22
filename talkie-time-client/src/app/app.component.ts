import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthService} from './services/auth.service';
import {WebsocketService} from './services/websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [
    RouterOutlet
  ],
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'talkie-time';
  authService = inject(AuthService);

  constructor( private router: Router, private wsService: WebsocketService) {
  }
  ngOnInit(){
    if (!this.authService.isLoggedIn()) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

}
