package br.ucsal.clinica.config;

import br.ucsal.clinica.model.*;
import br.ucsal.clinica.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

/** Dados iniciais para testar (so executa se o banco estiver vazio). */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final ClinicaRepository clinicas;
    private final SetorRepository setores;
    private final ColaboradorRepository colaboradores;
    private final ConvenioRepository convenios;
    private final MedicacaoRepository medicacoes;
    private final PacienteRepository pacientes;
    private final PasswordEncoder encoder;

    public DataInitializer(UsuarioRepository usuarios, ClinicaRepository clinicas, SetorRepository setores,
                           ColaboradorRepository colaboradores, ConvenioRepository convenios,
                           MedicacaoRepository medicacoes, PacienteRepository pacientes, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.clinicas = clinicas;
        this.setores = setores;
        this.colaboradores = colaboradores;
        this.convenios = convenios;
        this.medicacoes = medicacoes;
        this.pacientes = pacientes;
        this.encoder = encoder;
    }

    private Usuario usuario(String login, String senha, Colaborador colab, Enums.Perfil... perfis) {
        Usuario u = new Usuario();
        u.setLogin(login);
        u.setSenha(encoder.encode(senha));
        u.setPerfis(new HashSet<>(Arrays.asList(perfis)));
        u.setColaborador(colab);
        return usuarios.save(u);
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) return;

        Clinica clinica = new Clinica();
        clinica.setRazaoSocial("Clínica Médica Acadêmica LTDA");
        clinica.setNomeFantasia("Clínica Acadêmica");
        clinica.setCnpj("12.345.678/0001-90");
        clinica.setCidade("Salvador");
        clinica.setEstado("BA");
        clinica.setPais("Brasil");
        clinica.setEmail("contato@clinica.com");
        clinica.setAtividadePrincipal("Atividade médica ambulatorial");
        clinicas.save(clinica);

        Colaborador medico = new Colaborador();
        medico.setNome("Dra. Ana Souza");
        medico.setCpf("111.111.111-11");
        medico.setFuncao(Enums.Funcao.MEDICO);
        medico.setEspecialidade("Clínica Geral");
        medico.setConselhoRegional("CRM-BA");
        medico.setRegistroConselho("12345");
        medico.setLocalTrabalho("Consultórios Médicos");
        colaboradores.save(medico);

        Colaborador atendente = new Colaborador();
        atendente.setNome("Carlos Lima");
        atendente.setCpf("222.222.222-22");
        atendente.setFuncao(Enums.Funcao.ATENDENTE);
        atendente.setLocalTrabalho("Recepção e Triagem");
        colaboradores.save(atendente);

        String[][] nomes = {{"Recepção e Triagem"}, {"Consultórios Médicos"}, {"Farmácia"}};
        for (String[] n : nomes) {
            Setor s = new Setor();
            s.setNome(n[0]);
            s.setClinica(clinica);
            s.setResponsavel(n[0].startsWith("Recep") ? atendente : medico);
            setores.save(s);
        }

        Convenio convenio = new Convenio();
        convenio.setRazaoSocial("Saúde Plena Operadora S.A.");
        convenio.setNomeFantasia("Saúde Plena");
        convenio.setCnpj("98.765.432/0001-10");
        convenio.setRegistroAns("123456");
        convenios.save(convenio);

        Medicacao dipirona = new Medicacao();
        dipirona.setNome("Dipirona 500mg");
        dipirona.setDescricao("Analgésico e antitérmico");
        dipirona.setFornecedor("Laboratório Exemplo");
        dipirona.setArmazenamento(Enums.Armazenamento.TEMPERATURA_AMBIENTE);
        dipirona.setQuantidadeEstoque(50);
        dipirona.setDataAquisicao(LocalDate.now());
        dipirona.setDataValidade(LocalDate.now().plusYears(1));
        medicacoes.save(dipirona);

        Medicacao insulina = new Medicacao();
        insulina.setNome("Insulina NPH");
        insulina.setDescricao("Controle de diabetes");
        insulina.setFornecedor("Laboratório Exemplo");
        insulina.setArmazenamento(Enums.Armazenamento.REFRIGERACAO);
        insulina.setQuantidadeEstoque(8); // abaixo do minimo (10): mostra o alerta de reposicao
        insulina.setDataAquisicao(LocalDate.now());
        insulina.setDataValidade(LocalDate.now().plusMonths(6));
        medicacoes.save(insulina);

        Paciente paciente = new Paciente();
        paciente.setNome("Maria da Silva");
        paciente.setCpf("333.333.333-33");
        paciente.setDataNascimento(LocalDate.of(1990, 5, 20));
        paciente.setMedico(medico);
        paciente.setEspecialidade("Clínica Geral");
        Prontuario pr = new Prontuario();
        pr.setPaciente(paciente);
        pr.setConvenio(convenio);
        paciente.setProntuario(pr);
        pacientes.save(paciente);

        usuario("admin", "admin123", null, Enums.Perfil.ADMINISTRADOR);
        usuario("medico", "medico123", medico, Enums.Perfil.COLABORADOR);
        usuario("atendente", "atendente123", atendente, Enums.Perfil.USUARIO);
    }
}
