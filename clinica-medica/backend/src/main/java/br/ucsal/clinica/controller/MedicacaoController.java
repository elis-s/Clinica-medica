package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Medicacao;
import br.ucsal.clinica.model.MovimentacaoEstoque;
import br.ucsal.clinica.repository.MedicacaoRepository;
import br.ucsal.clinica.repository.MovimentacaoEstoqueRepository;
import br.ucsal.clinica.security.UsuarioLogado;
import br.ucsal.clinica.service.EstoqueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/medicacoes")
public class MedicacaoController extends CrudController<Medicacao> {

    private final EstoqueService estoque;
    private final MovimentacaoEstoqueRepository movimentacoes;
    private final UsuarioLogado logado;

    public MedicacaoController(MedicacaoRepository repo, EstoqueService estoque,
                               MovimentacaoEstoqueRepository movimentacoes, UsuarioLogado logado) {
        super(repo);
        this.estoque = estoque;
        this.movimentacoes = movimentacoes;
        this.logado = logado;
    }

    @Override
    protected void validar(Medicacao m) {
        exigir(preenchido(m.getNome()), "Nome da medicação é obrigatório");
        exigir(m.getQuantidadeEstoque() >= 0, "Quantidade inválida");
    }

    @Override
    protected void depoisDeCriar(Medicacao salvo) {
        estoque.registrarEntradaInicial(salvo, logado.login());
    }

    /** O saldo nunca e alterado na edicao: so por movimentacoes. */
    @Override
    protected void antesDeEditar(Medicacao novo, Medicacao atual) {
        novo.setQuantidadeEstoque(atual.getQuantidadeEstoque());
    }

    @PostMapping("/{id}/entrada")
    public Medicacao repor(@PathVariable Long id, @RequestBody Map<String, Integer> corpo) {
        Integer qtd = corpo.get("quantidade");
        exigir(qtd != null, "Informe a quantidade");
        return estoque.entrada(id, qtd, logado.login());
    }

    @GetMapping("/{id}/movimentacoes")
    public List<MovimentacaoEstoque> movimentacoes(@PathVariable Long id) {
        return movimentacoes.findByMedicacaoIdOrderByDataHoraDesc(id);
    }
}
