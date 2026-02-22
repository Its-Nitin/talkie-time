import { Routes } from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {RegisterComponent} from './auth/register/register.component';
import {HomeComponent} from './pages/home/home.component';
import {authGaurd} from './auth.gaurd';
import {UsersComponent} from './pages/users/users.component';
import {ChatComponent} from './pages/chat/chat.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  {path: 'home', component: HomeComponent, canActivate: [authGaurd] },
  {path: 'users', component: UsersComponent, canActivate: [authGaurd]},
  {path: 'chat/:id/:username', component: ChatComponent, canActivate: [authGaurd]},
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login'}
];
