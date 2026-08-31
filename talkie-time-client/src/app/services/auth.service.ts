import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';

export interface RegisterUser {
  username: string;
  email: string;
  password: string;
}

export interface LoginUser {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  register(user: RegisterUser) {
    return this.http.post(`${this.apiUrl}/api/v1/auth/register`, user);
  }

  login(user: LoginUser) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/api/v1/auth/login`, user);
  }

  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  getToken() {
    return localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('loggedUser');
  }

  isLoggedIn() {
    const token = this.getToken();
    if (!token) { return false; }
    return !this.isTokenExpired(token);
  }
  isTokenExpired(token: string | null) {
    if (!token) { return true; }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    }catch (e) {
      return true;
    }
  }
}
