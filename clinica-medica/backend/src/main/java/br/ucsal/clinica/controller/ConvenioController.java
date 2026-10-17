package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Convenio;
import br.ucsal.clinica.repository.ConvenioRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/convenios")
public class ConvenioController extends CrudController<Convenio> {

    public ConvenioController(ConvenioRepository repo) {
        super(repo);
    }

    @Override
    protected void validar(Convenio c) {
        exigir(preenchido(c.getRazaoSocial()) && preenchido(c.getCnpj()), "Razão social e CNPJ são obrigatórios");
    }
}
