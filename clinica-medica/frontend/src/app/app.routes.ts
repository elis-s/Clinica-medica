import { Routes } from '@angular/router';
import { guard } from './auth';
import { LoginComponent } from './login.component';
import { HomeComponent } from './home.component';
import { CrudComponent } from './crud.component';
import { RelatorioComponent } from './relatorio.component';

const ADM = 'ADMINISTRADOR', COL = 'COLABORADOR', USU = 'USUARIO';
const crud = (recurso: string, perfis: string[]) => ({
  path: recurso, component: CrudComponent, canActivate: [guard], data: { recurso, perfis }
});

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', component: HomeComponent, canActivate: [guard] },
  crud('clinicas', [ADM]),
  crud('setores', [ADM]),
  crud('convenios', [ADM]),
  crud('colaboradores', [ADM]),
  crud('usuarios', [ADM]),
  crud('auditoria', [ADM]),
  crud('pacientes', [ADM, USU]),
  crud('agendamentos', [ADM, USU]),
  crud('medicacoes', [ADM, USU]),
  crud('atendimentos', [ADM, COL]),
  crud('solicitacoes', [ADM, COL, USU]),
  { path: 'relatorios', component: RelatorioComponent, canActivate: [guard], data: { perfis: [ADM, COL] } },
  { path: '**', redirectTo: '' }
];
