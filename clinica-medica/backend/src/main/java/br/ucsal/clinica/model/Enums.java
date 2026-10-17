package br.ucsal.clinica.model;

public final class Enums {
    private Enums() {}
    public enum Status { ATIVO, INATIVO }
    public enum Perfil { ADMINISTRADOR, COLABORADOR, USUARIO }
    public enum Funcao { MEDICO, ENFERMEIRO, FARMACEUTICO, FONOAUDIOLOGO, NUTRICIONISTA, FISIOTERAPEUTA, PSICOLOGO,
        ASSISTENTE_SOCIAL, ADMINISTRADOR, ATENDENTE, VIGILANCIA, TECNICO, AUXILIAR_ENFERMAGEM }
    public enum TipoAtendimento { URGENCIA, EMERGENCIA, CONSULTA, REVISAO }
    public enum StatusAgendamento { AGENDADO, FINALIZADO, INATIVO }
    public enum StatusAtendimento { EM_ANDAMENTO, ENCERRADO }
    public enum StatusSolicitacao { PENDENTE, ENTREGUE }
    public enum Armazenamento { REFRIGERACAO, TEMPERATURA_AMBIENTE }
    public enum TipoMovimentacao { ENTRADA, SAIDA }
}
