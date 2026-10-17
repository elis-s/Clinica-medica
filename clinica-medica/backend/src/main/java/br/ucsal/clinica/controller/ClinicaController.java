package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Clinica;
import br.ucsal.clinica.repository.ClinicaRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/clinicas")
public class ClinicaController extends CrudController<Clinica> {

    public ClinicaController(ClinicaRepository repo) {
        super(repo);
    }

    @Override
    protected void validar(Clinica c) {
        exigir(preenchido(c.getRazaoSocial()) && preenchido(c.getCnpj()), "Razão social e CNPJ são obrigatórios");
    }

    @Override
    protected void antesDeInativar(Clinica c, String motivo) {
        exigir(preenchido(motivo), "Informe o motivo da inatividade da clínica");
    }
}
