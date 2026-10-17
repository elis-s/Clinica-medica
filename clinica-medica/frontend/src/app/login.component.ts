import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from './auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="card login">
      <h2>Clínica Médica Acadêmica</h2>
      @if (erro) { <div class="msg erro">{{ erro }}</div> }
      <form (ngSubmit)="entrar()">
        <label>Login <input name="login" [(ngModel)]="login" autofocus></label>
        <br>
        <label>Senha <input name="senha" type="password" [(ngModel)]="senha"></label>
        <br>
        <button type="submit">Entrar</button>
      </form>
    </div>
  `
})
export class LoginComponent {
  login = '';
  senha = '';
  erro = '';

  constructor(private auth: AuthService, private router: Router) {}

  entrar() {
    this.erro = '';
    this.auth.login(this.login, this.senha).subscribe({
      next: () => this.router.navigate(['/']),
      error: e => (this.erro = e.status === 401 ? 'Login ou senha inválidos' : 'Não foi possível conectar ao servidor')
    });
  }
}
