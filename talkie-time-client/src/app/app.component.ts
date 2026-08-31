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
export class AppComponent {
  title = 'talkie-time';
}
