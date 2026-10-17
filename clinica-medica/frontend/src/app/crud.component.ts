import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { JsonPipe } from '@angular/common';
import { AuthService } from './auth';
import { Acao, Campo, Config, RECURSOS } from './recursos';

/** Tela generica: lista, cria, edita e inativa (nunca exclui) conforme a configuracao em recursos.ts */
@Component({
  selector: 'app-crud',
  standalone: true,
  imports: [FormsModule, JsonPipe],
  template: `
    <h2>{{ cfg.titulo }}</h2>
    @if (erro) { <div class="msg erro">{{ erro }}</div> }
    @if (ok) { <div class="msg ok">{{ ok }}</div> }

    @if (podeEscrever() && !cfg.leitura && !form) { <button (click)="novo()">+ Novo</button> }

    @if (form) {
      <form class="card" (ngSubmit)="salvar()">
        <h3>{{ editando ? 'Editar' : 'Novo cadastro' }}</h3>
        <div class="campos">
          @for (c of camposForm(); track c.chave) {
            <label>{{ c.rotulo }}
              @switch (c.tipo) {
                @case ('textarea') {
                  <textarea rows="3" [(ngModel)]="form[c.chave]" [ngModelOptions]="{standalone: true}"></textarea>
                }
                @case ('enum') {
                  <select [(ngModel)]="form[c.chave]" [ngModelOptions]="{standalone: true}">
                    <option [ngValue]="null">-</option>
                    @for (o of c.opcoes; track o) { <option [ngValue]="o">{{ o }}</option> }
                  </select>
                }
                @case ('ref') {
                  <select [(ngModel)]="form[c.chave]" [ngModelOptions]="{standalone: true}">
                    <option [ngValue]="null">-</option>
                    @for (o of opcoes[c.chave]; track o.id) { <option [ngValue]="o.id">{{ rotuloRef(o, c) }}</option> }
                  </select>
                }
                @case ('multi') {
                  <span class="multi">
                    @for (o of c.opcoes; track o) {
                      <label><input type="checkbox" [checked]="(form[c.chave] || []).includes(o)" (change)="alternar(c.chave, o)"> {{ o }}</label>
                    }
                  </span>
                }
                @default {
                  <input [type]="c.tipo || 'text'" [(ngModel)]="form[c.chave]" [ngModelOptions]="{standalone: true}">
                }
              }
            </label>
          }
        </div>
        <br>
        <button type="submit">Salvar</button>
        <button type="button" class="sec" (click)="cancelar()">Cancelar</button>
      </form>
    }

    @if (detalhe) {
      <div class="card">
        <button class="sec" (click)="detalhe = null">Fechar</button>
        <pre>{{ detalhe | json }}</pre>
      </div>
    }

    <table>
      <thead>
        <tr>
          @for (col of cfg.colunas; track col.chave) { <th>{{ col.rotulo }}</th> }
          @if (!cfg.leitura) { <th>Ações</th> }
        </tr>
      </thead>
      <tbody>
        @for (l of linhas; track $index) {
          <tr>
            @for (col of cfg.colunas; track col.chave) {
              <td [class.alerta]="col.chave === 'precisaReposicao' && l.precisaReposicao">{{ fmt(l, col.chave) }}</td>
            }
            @if (!cfg.leitura) {
              <td>
                @if (podeEditar(l)) { <button (click)="editar(l)">Editar</button> }
                @if (podeInativar(l)) { <button class="perigo" (click)="inativar(l)">Inativar</button> }
                @for (a of acoesDe(l); track a.rotulo) { <button class="sec" (click)="executar(a, l)">{{ a.rotulo }}</button> }
              </td>
            }
          </tr>
        }
      </tbody>
    </table>
    @if (linhas.length === 0) { <p>Nenhum registro.</p> }
  `
})
export class CrudComponent implements OnInit {
  cfg: Config = { titulo: '', campos: [], colunas: [] };
  recurso = '';
  linhas: any[] = [];
  form: any = null;
  editando: any = null;
  erro = '';
  ok = '';
  detalhe: any = null;
  opcoes: { [chave: string]: any[] } = {};

  constructor(private route: ActivatedRoute, private http: HttpClient, private auth: AuthService) {}

  ngOnInit() {
    this.recurso = this.route.snapshot.data['recurso'];
    this.cfg = RECURSOS[this.recurso];
    this.carregar();
    for (const c of this.cfg.campos) {
      if (c.tipo === 'ref' && c.ref) {
        const ref = c.ref;
        this.http.get<any[]>('/api/' + ref.recurso).subscribe({
          next: l => (this.opcoes[c.chave] = ref.filtro ? l.filter(ref.filtro) : l),
          error: () => (this.opcoes[c.chave] = [])
        });
      }
    }
  }

  carregar() {
    this.http.get<any[]>('/api/' + this.recurso).subscribe({
      next: l => (this.linhas = l),
      error: e => (this.erro = this.msg(e))
    });
  }

  // ---------- permissoes de tela ----------
  podeEscrever(): boolean {
    return !this.cfg.perfisEscrita || this.auth.tem(...this.cfg.perfisEscrita);
  }
  podeEditar(l: any): boolean {
    return this.podeEscrever() && this.cfg.editar !== false && l.status !== 'INATIVO' && (!this.cfg.editarSe || this.cfg.editarSe(l));
  }
  podeInativar(l: any): boolean {
    return this.podeEscrever() && !!this.cfg.inativar && l.status !== 'INATIVO';
  }
  acoesDe(l: any): Acao[] {
    return (this.cfg.acoes || []).filter(
      a => (!a.mostrarSe || a.mostrarSe(l)) && (!a.perfis || this.auth.tem(...a.perfis))
    );
  }

  // ---------- formulario ----------
  camposForm(): Campo[] {
    return this.cfg.campos.filter(c => !(this.editando && c.soCriar));
  }
  novo() {
    this.erro = ''; this.ok = '';
    this.editando = null;
    this.form = {};
    for (const c of this.cfg.campos) this.form[c.chave] = c.padrao !== undefined ? c.padrao : c.tipo === 'multi' ? [] : null;
  }
  editar(l: any) {
    this.erro = ''; this.ok = '';
    this.editando = l;
    this.form = {};
    for (const c of this.cfg.campos) {
      if (c.tipo === 'ref') this.form[c.chave] = l[c.chave] ? l[c.chave].id : null;
      else if (c.tipo === 'multi') this.form[c.chave] = [...(l[c.chave] || [])];
      else this.form[c.chave] = l[c.chave] !== undefined ? l[c.chave] : null;
    }
  }
  cancelar() { this.form = null; this.editando = null; }
  alternar(chave: string, valor: string) {
    const a: string[] = this.form[chave] || [];
    this.form[chave] = a.includes(valor) ? a.filter(x => x !== valor) : [...a, valor];
  }
  salvar() {
    this.erro = ''; this.ok = '';
    const corpo: any = this.editando ? { ...this.editando } : {};
    for (const c of this.camposForm()) {
      let v = this.form[c.chave];
      if (c.tipo === 'ref') v = v ? { id: v } : null;
      else if (c.tipo === 'number') v = v === null || v === undefined || v === '' ? null : Number(v);
      else if (v === '') v = null;
      corpo[c.chave] = v;
    }
    const req = this.editando
      ? this.http.put('/api/' + this.recurso + '/' + this.editando.id, corpo)
      : this.http.post('/api/' + this.recurso, corpo);
    req.subscribe({
      next: () => { this.ok = 'Salvo com sucesso'; this.cancelar(); this.carregar(); },
      error: e => (this.erro = this.msg(e))
    });
  }

  // ---------- acoes ----------
  inativar(l: any) {
    this.erro = ''; this.ok = '';
    let corpo: any = {};
    if (this.cfg.inativar === 'motivo') {
      const motivo = prompt('Informe o motivo da inatividade:');
      if (!motivo) return;
      corpo = { motivo };
    } else if (!confirm('Inativar este registro? (o histórico é mantido)')) {
      return;
    }
    this.http.patch('/api/' + this.recurso + '/' + l.id + '/inativar', corpo).subscribe({
      next: () => { this.ok = 'Registro inativado'; this.carregar(); },
      error: e => (this.erro = this.msg(e))
    });
  }
  executar(a: Acao, l: any) {
    this.erro = ''; this.ok = '';
    let corpo: any = null;
    if (a.pedir) {
      const v = prompt(a.pedir.texto);
      if (v === null || v === '') return;
      corpo = { [a.pedir.chave]: Number(v) };
    }
    this.http.request(a.metodo, '/api' + a.url(l), { body: corpo }).subscribe({
      next: r => {
        if (a.mostrar) this.detalhe = r; else this.ok = 'Operação realizada com sucesso';
        this.carregar();
      },
      error: e => (this.erro = this.msg(e))
    });
  }

  // ---------- exibicao ----------
  valor(obj: any, caminho: string): any {
    return caminho.split('.').reduce((o, k) => (o === null || o === undefined ? undefined : o[k]), obj);
  }
  rotuloRef(o: any, c: Campo): string {
    return c.ref!.rotulo.split('|').map(k => this.valor(o, k)).join(' - ');
  }
  fmt(l: any, caminho: string): string {
    const v = this.valor(l, caminho);
    if (v === null || v === undefined) return '';
    if (typeof v === 'boolean') return v ? 'SIM' : 'não';
    if (Array.isArray(v)) return v.join(', ');
    if (typeof v === 'string' && /^\d{4}-\d{2}-\d{2}T/.test(v)) return v.replace('T', ' ').slice(0, 16);
    return String(v);
  }
  msg(e: any): string {
    return e?.error?.message || (e?.status === 403 ? 'Sem permissão para esta operação' : 'Erro ao comunicar com o servidor (' + e?.status + ')');
  }
}
