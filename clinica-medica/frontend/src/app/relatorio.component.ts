import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth';

@Component({
  selector: 'app-relatorio',
  standalone: true,
  template: `
    <h2>Relatórios</h2>
    @if (erro) { <div class="msg erro">{{ erro }}</div> }
    @if (auth.tem('ADMINISTRADOR')) { <button (click)="gerar('admin')">Relatório completo (administrador)</button> }
    @if (auth.tem('COLABORADOR')) { <button (click)="gerar('colaborador')">Meu relatório (colaborador)</button> }
    @if (dados) {
      <button class="sec" (click)="imprimir()">Imprimir</button>
      <div class="card">
        <strong>Emitido por:</strong> {{ dados.emitidoPor }} &nbsp; <strong>Em:</strong> {{ dados.dataHoraEmissao.replace('T', ' ').slice(0, 19) }}
        @if (dados.colaborador) { <br><strong>Colaborador:</strong> {{ dados.colaborador }} }
      </div>
      @for (s of secoes(); track s) {
        <h3>{{ s }} ({{ dados[s].length }})</h3>
        @if (dados[s].length) {
          <table>
            <thead><tr>@for (c of colunas(dados[s]); track c) { <th>{{ c }}</th> }</tr></thead>
            <tbody>
              @for (l of dados[s]; track $index) {
                <tr>@for (c of colunas(dados[s]); track c) { <td>{{ celula(l[c]) }}</td> }</tr>
              }
            </tbody>
          </table>
        }
      }
    }
  `
})
export class RelatorioComponent {
  dados: any = null;
  erro = '';

  constructor(private http: HttpClient, public auth: AuthService) {}

  gerar(tipo: string) {
    this.erro = '';
    this.http.get<any>('/api/relatorios/' + tipo).subscribe({
      next: d => (this.dados = d),
      error: e => (this.erro = e?.error?.message || 'Erro ao gerar relatório')
    });
  }
  imprimir() { window.print(); }
  secoes(): string[] { return Object.keys(this.dados).filter(k => Array.isArray(this.dados[k])); }
  colunas(lista: any[]): string[] { return Object.keys(lista[0]); }
  celula(v: any): string {
    if (v === null || v === undefined) return '';
    if (Array.isArray(v)) return v.join(', ');
    if (typeof v === 'object') return v.nome ?? v.razaoSocial ?? v.nomeFantasia ?? v.login ?? v.paciente?.nome ?? String(v.id);
    if (typeof v === 'string' && /^\d{4}-\d{2}-\d{2}T/.test(v)) return v.replace('T', ' ').slice(0, 16);
    return String(v);
  }
}
