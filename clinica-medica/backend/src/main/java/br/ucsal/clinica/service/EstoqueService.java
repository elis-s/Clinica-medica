package br.ucsal.clinica.service;

import br.ucsal.clinica.model.*;
import br.ucsal.clinica.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static br.ucsal.clinica.Regra.exigir;

/** Controle de estoque: todo saldo so muda atraves de uma movimentacao registrada. */
@Service
public class EstoqueService {

    private final MedicacaoRepository medicacoes;
    private final MovimentacaoEstoqueRepository movimentacoes;
    private final SolicitacaoMedicacaoRepository solicitacoes;

    public EstoqueService(MedicacaoRepository medicacoes, MovimentacaoEstoqueRepository movimentacoes,
                          SolicitacaoMedicacaoRepository solicitacoes) {
        this.medicacoes = medicacoes;
        this.movimentacoes = movimentacoes;
        this.solicitacoes = solicitacoes;
    }

    private MovimentacaoEstoque registrar(Medicacao m, Enums.TipoMovimentacao tipo, int qtd, Colaborador solicitante, String login) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setMedicacao(m);
        mov.setTipo(tipo);
        mov.setQuantidade(qtd);
        mov.setDataHora(LocalDateTime.now());
        mov.setColaboradorSolicitante(solicitante);
        mov.setResponsavel(login);
        return movimentacoes.save(mov);
    }

    /** Estoque inicial informado no cadastro da medicacao. */
    @Transactional
    public void registrarEntradaInicial(Medicacao m, String login) {
        if (m.getQuantidadeEstoque() > 0) registrar(m, Enums.TipoMovimentacao.ENTRADA, m.getQuantidadeEstoque(), null, login);
    }

    /** Reposicao (compra) de medicacao. */
    @Transactional
    public Medicacao entrada(Long medicacaoId, int quantidade, String login) {
        exigir(quantidade > 0, "Quantidade deve ser maior que zero");
        Medicacao m = medicacoes.findById(medicacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicação não encontrada"));
        exigir(m.getStatus() == Enums.Status.ATIVO, "Medicação inativa");
        m.setQuantidadeEstoque(m.getQuantidadeEstoque() + quantidade);
        registrar(m, Enums.TipoMovimentacao.ENTRADA, quantidade, null, login);
        return medicacoes.save(m);
    }

    /** Entrega da medicacao solicitada: baixa o estoque e registra solicitante + quem entregou. */
    @Transactional
    public SolicitacaoMedicacao entregar(Long solicitacaoId, String login) {
        SolicitacaoMedicacao s = solicitacoes.findById(solicitacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada"));
        exigir(s.getStatus() == Enums.StatusSolicitacao.PENDENTE, "Solicitação já foi entregue");
        Medicacao m = s.getMedicacao();
        exigir(m.getQuantidadeEstoque() >= s.getQuantidade(), "Estoque insuficiente (saldo: " + m.getQuantidadeEstoque() + ")");
        m.setQuantidadeEstoque(m.getQuantidadeEstoque() - s.getQuantidade());
        medicacoes.save(m);
        registrar(m, Enums.TipoMovimentacao.SAIDA, s.getQuantidade(), s.getSolicitante(), login);
        s.setStatus(Enums.StatusSolicitacao.ENTREGUE);
        s.setDataHoraEntrega(LocalDateTime.now());
        s.setEntreguePor(login);
        return solicitacoes.save(s);
    }
}
