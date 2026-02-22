import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {HttpInterceptorFn} from '@angular/common/http';


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
    if (token) {
      req = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      })
    }
    return next(req);
}
