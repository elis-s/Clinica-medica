import { Component } from '@angular/core';
import { AuthService } from './auth';

@Component({
  selector: 'app-home',
  standalone: true,
  template: `
    <h2>Bem-vindo(a), {{ auth.usuario?.nome }}</h2>
    <p>Perfil(s): {{ auth.usuario?.perfis.join(', ') }}</p>
    <p>Use o menu acima para acessar as funcionalidades do seu perfil.</p>
  `
})
export class HomeComponent {
  constructor(public auth: AuthService) {}
}
