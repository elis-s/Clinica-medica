package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Colaborador;
import br.ucsal.clinica.model.Enums;
import br.ucsal.clinica.repository.ColaboradorRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorController extends CrudController<Colaborador> {

    public ColaboradorController(ColaboradorRepository repo) {
        super(repo);
    }

    @Override
    protected void validar(Colaborador c) {
        exigir(preenchido(c.getNome()) && preenchido(c.getCpf()), "Nome e CPF são obrigatórios");
        exigir(c.getFuncao() != null, "Informe a função do colaborador");
        if (c.getFuncao() == Enums.Funcao.MEDICO) {
            exigir(preenchido(c.getEspecialidade()) && preenchido(c.getConselhoRegional()) && preenchido(c.getRegistroConselho()),
                    "Médico: informe especialidade, conselho regional e número de registro");
        }
    }
}
