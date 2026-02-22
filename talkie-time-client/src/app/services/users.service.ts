import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

export interface User {
  id: number;
  username: string;
}

@Injectable(
  {
    providedIn: 'root'
  }
)
export class UsersService {
  private apiUrl = 'http://localhost:8082';
  constructor(private http: HttpClient) { }

  getUsers() {
    return this.http.get<User[]>( `${this.apiUrl}/users`);
  }

}
