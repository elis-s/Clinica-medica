package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Colaborador;
import br.ucsal.clinica.repository.*;
import br.ucsal.clinica.security.UsuarioLogado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final UsuarioLogado logado;
    private final ClinicaRepository clinicas;
    private final SetorRepository setores;
    private final ConvenioRepository convenios;
    private final ColaboradorRepository colaboradores;
    private final UsuarioRepository usuarios;
    private final PacienteRepository pacientes;
    private final AgendamentoRepository agendamentos;
    private final MedicacaoRepository medicacoes;
    private final AtendimentoRepository atendimentos;
    private final SolicitacaoMedicacaoRepository solicitacoes;
    private final MovimentacaoEstoqueRepository movimentacoes;

    public RelatorioController(UsuarioLogado logado, ClinicaRepository clinicas, SetorRepository setores,
                               ConvenioRepository convenios, ColaboradorRepository colaboradores,
                               UsuarioRepository usuarios, PacienteRepository pacientes,
                               AgendamentoRepository agendamentos, MedicacaoRepository medicacoes,
                               AtendimentoRepository atendimentos, SolicitacaoMedicacaoRepository solicitacoes,
                               MovimentacaoEstoqueRepository movimentacoes) {
        this.logado = logado;
        this.clinicas = clinicas;
        this.setores = setores;
        this.convenios = convenios;
        this.colaboradores = colaboradores;
        this.usuarios = usuarios;
        this.pacientes = pacientes;
        this.agendamentos = agendamentos;
        this.medicacoes = medicacoes;
        this.atendimentos = atendimentos;
        this.solicitacoes = solicitacoes;
        this.movimentacoes = movimentacoes;
    }

    private Map<String, Object> cabecalho() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("emitidoPor", logado.login());
        r.put("dataHoraEmissao", LocalDateTime.now().toString());
        return r;
    }

    /** Administrador: relatorio completo com tudo que foi cadastrado. */
    @GetMapping("/admin")
    public Map<String, Object> admin() {
        Map<String, Object> r = cabecalho();
        r.put("clinicas", clinicas.findAll());
        r.put("setores", setores.findAll());
        r.put("convenios", convenios.findAll());
        r.put("colaboradores", colaboradores.findAll());
        r.put("usuarios", usuarios.findAll());
        r.put("pacientes", pacientes.findAll());
        r.put("agendamentos", agendamentos.findAll());
        r.put("medicacoes", medicacoes.findAll());
        r.put("movimentacoesEstoque", movimentacoes.findAll());
        r.put("atendimentos", atendimentos.findAll());
        r.put("solicitacoesMedicacao", solicitacoes.findAll());
        return r;
    }

    /** Colaborador: relatorio apenas com a sua movimentacao. */
    @GetMapping("/colaborador")
    public Map<String, Object> colaborador() {
        Colaborador c = logado.colaborador();
        Map<String, Object> r = cabecalho();
        r.put("colaborador", c.getNome());
        r.put("atendimentos", atendimentos.findByColaboradorIdOrderByIdDesc(c.getId()));
        r.put("solicitacoesMedicacao", solicitacoes.findBySolicitanteIdOrderByIdDesc(c.getId()));
        return r;
    }
}
