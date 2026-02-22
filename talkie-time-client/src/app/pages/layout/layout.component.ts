import { Component } from '@angular/core';
import {UsersComponent} from '../users/users.component';
import {ChatComponent} from '../chat/chat.component';

@Component({
  selector: 'app-layout',
  imports: [
    UsersComponent,
    ChatComponent
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent {

}
