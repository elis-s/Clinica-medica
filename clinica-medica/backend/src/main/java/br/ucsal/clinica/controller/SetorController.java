package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Setor;
import br.ucsal.clinica.repository.SetorRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/setores")
public class SetorController extends CrudController<Setor> {

    public SetorController(SetorRepository repo) {
        super(repo);
    }

    @Override
    protected void validar(Setor s) {
        exigir(preenchido(s.getNome()), "Nome do setor é obrigatório");
        exigir(s.getClinica() != null && s.getClinica().getId() != null, "Todo setor deve estar vinculado a uma clínica");
        exigir(s.getResponsavel() != null && s.getResponsavel().getId() != null, "Informe o responsável do setor (apenas um)");
    }
}
