import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginForm!: FormGroup;

  constructor(private fb: FormBuilder,
              private authService: AuthService,
              private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
      if (this.loginForm.invalid) { return;}

      this.authService.login(this.loginForm.value).subscribe({
        next: (res) =>  {
          this.authService.saveToken(res.token);
          localStorage.setItem('loggedUser', JSON.stringify(res.user));
          this.router.navigate(['/home']);
        },
        error: () => alert("Login failed")
      });
  }

}
