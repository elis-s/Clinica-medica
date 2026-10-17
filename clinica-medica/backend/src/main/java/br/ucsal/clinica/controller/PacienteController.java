package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Convenio;
import br.ucsal.clinica.model.Paciente;
import br.ucsal.clinica.model.Prontuario;
import br.ucsal.clinica.repository.PacienteRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.ucsal.clinica.Regra.exigir;
import static br.ucsal.clinica.Regra.preenchido;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController extends CrudController<Paciente> {

    public PacienteController(PacienteRepository repo) {
        super(repo);
    }

    @Override
    protected void validar(Paciente p) {
        exigir(preenchido(p.getNome()) && preenchido(p.getCpf()), "Nome e CPF do paciente são obrigatórios");
    }

    /** Todo paciente nasce com exatamente um prontuario (salvos juntos). */
    @Override
    protected void antesDeCriar(Paciente p) {
        Prontuario pr = new Prontuario();
        pr.setPaciente(p);
        pr.setConvenio(p.getConvenio());
        p.setProntuario(pr);
    }

    @Override
    protected void antesDeEditar(Paciente novo, Paciente atual) {
        Convenio convenio = novo.getConvenio();
        Prontuario pr = atual.getProntuario();
        pr.setConvenio(convenio);
        novo.setProntuario(pr);
    }
}
