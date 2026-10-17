import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './auth';

const ADM = 'ADMINISTRADOR', COL = 'COLABORADOR', USU = 'USUARIO';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (auth.usuario) {
      <header>
        <strong>Clínica Médica</strong>
        @for (m of menu; track m.rota) {
          @if (visivel(m.perfis)) { <a [routerLink]="m.rota" routerLinkActive="ativo">{{ m.rotulo }}</a> }
        }
        <span class="espaco"></span>
        <span>{{ auth.usuario.nome }}</span>
        <button class="sec" (click)="auth.logout()">Sair</button>
      </header>
    }
    <main><router-outlet /></main>
  `
})
export class AppComponent {
  menu = [
    { rota: '/clinicas', rotulo: 'Clínicas', perfis: [ADM] },
    { rota: '/setores', rotulo: 'Setores', perfis: [ADM] },
    { rota: '/convenios', rotulo: 'Convênios', perfis: [ADM] },
    { rota: '/colaboradores', rotulo: 'Colaboradores', perfis: [ADM] },
    { rota: '/usuarios', rotulo: 'Usuários', perfis: [ADM] },
    { rota: '/auditoria', rotulo: 'Auditoria', perfis: [ADM] },
    { rota: '/pacientes', rotulo: 'Pacientes', perfis: [ADM, USU] },
    { rota: '/agendamentos', rotulo: 'Agendamentos', perfis: [ADM, USU] },
    { rota: '/medicacoes', rotulo: 'Medicações/Estoque', perfis: [ADM, USU] },
    { rota: '/atendimentos', rotulo: 'Atendimentos', perfis: [ADM, COL] },
    { rota: '/solicitacoes', rotulo: 'Solicitações', perfis: [ADM, COL, USU] },
    { rota: '/relatorios', rotulo: 'Relatórios', perfis: [ADM, COL] }
  ];

  constructor(public auth: AuthService) {}

  visivel(perfis: string[]): boolean {
    return this.auth.tem(...perfis);
  }
}
