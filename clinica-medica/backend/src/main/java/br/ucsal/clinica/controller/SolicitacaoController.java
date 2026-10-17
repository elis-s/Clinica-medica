package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Enums;
import br.ucsal.clinica.model.Medicacao;
import br.ucsal.clinica.model.SolicitacaoMedicacao;
import br.ucsal.clinica.repository.MedicacaoRepository;
import br.ucsal.clinica.repository.SolicitacaoMedicacaoRepository;
import br.ucsal.clinica.security.UsuarioLogado;
import br.ucsal.clinica.service.EstoqueService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoMedicacaoRepository repo;
    private final MedicacaoRepository medicacoes;
    private final EstoqueService estoque;
    private final UsuarioLogado logado;

    public SolicitacaoController(SolicitacaoMedicacaoRepository repo, MedicacaoRepository medicacoes,
                                 EstoqueService estoque, UsuarioLogado logado) {
        this.repo = repo;
        this.medicacoes = medicacoes;
        this.estoque = estoque;
        this.logado = logado;
    }

    @GetMapping
    public List<SolicitacaoMedicacao> listar() {
        if (logado.temPerfil(Enums.Perfil.ADMINISTRADOR) || logado.temPerfil(Enums.Perfil.USUARIO)) {
            return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return repo.findBySolicitanteIdOrderByIdDesc(logado.colaborador().getId());
    }

    /** O colaborador (medico) solicita a medicacao; o solicitante e capturado do login. */
    @PostMapping
    public SolicitacaoMedicacao criar(@RequestBody SolicitacaoMedicacao s) {
        exigir(s.getMedicacao() != null && s.getMedicacao().getId() != null, "Informe a medicação");
        exigir(s.getQuantidade() > 0, "Quantidade deve ser maior que zero");
        exigir(preenchido(s.getJustificativa()), "Informe a justificativa");
        Medicacao m = medicacoes.findById(s.getMedicacao().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicação não encontrada"));
        exigir(m.getStatus() == Enums.Status.ATIVO, "Medicação inativa");
        s.setId(null);
        s.setMedicacao(m);
        s.setSolicitante(logado.colaborador());
        s.setStatus(Enums.StatusSolicitacao.PENDENTE);
        return repo.save(s);
    }

    /** O usuario (atendente) entrega: baixa o estoque e registra quem entregou. */
    @PatchMapping("/{id}/entregar")
    public SolicitacaoMedicacao entregar(@PathVariable Long id) {
        return estoque.entregar(id, logado.login());
    }
}
