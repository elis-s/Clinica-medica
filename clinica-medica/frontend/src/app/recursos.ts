// Configuracao das telas de cadastro (uma tela generica "CrudComponent" usa estas definicoes).
export interface Campo {
  chave: string;
  rotulo: string;
  tipo?: 'text' | 'number' | 'date' | 'time' | 'password' | 'textarea' | 'enum' | 'ref' | 'multi';
  opcoes?: string[];
  ref?: { recurso: string; rotulo: string; filtro?: (o: any) => boolean };
  soCriar?: boolean;
  padrao?: any;
}
export interface Coluna { chave: string; rotulo: string; }
export interface Acao {
  rotulo: string;
  metodo: 'GET' | 'POST' | 'PATCH';
  url: (l: any) => string;
  pedir?: { chave: string; texto: string };
  mostrar?: boolean;
  mostrarSe?: (l: any) => boolean;
  perfis?: string[];
}
export interface Config {
  titulo: string;
  campos: Campo[];
  colunas: Coluna[];
  acoes?: Acao[];
  inativar?: boolean | 'motivo';
  editar?: boolean;
  editarSe?: (l: any) => boolean;
  leitura?: boolean;
  perfisEscrita?: string[];
}

const FUNCOES = ['MEDICO', 'ENFERMEIRO', 'FARMACEUTICO', 'FONOAUDIOLOGO', 'NUTRICIONISTA', 'FISIOTERAPEUTA', 'PSICOLOGO',
  'ASSISTENTE_SOCIAL', 'ADMINISTRADOR', 'ATENDENTE', 'VIGILANCIA', 'TECNICO', 'AUXILIAR_ENFERMAGEM'];
const medicos = (o: any) => o.funcao === 'MEDICO' && o.status === 'ATIVO';

export const RECURSOS: { [k: string]: Config } = {
  clinicas: {
    titulo: 'Clínicas', inativar: 'motivo',
    campos: [
      { chave: 'razaoSocial', rotulo: 'Razão social' }, { chave: 'nomeFantasia', rotulo: 'Nome fantasia' },
      { chave: 'cnpj', rotulo: 'CNPJ' }, { chave: 'rua', rotulo: 'Rua' }, { chave: 'bairro', rotulo: 'Bairro' },
      { chave: 'cidade', rotulo: 'Cidade' }, { chave: 'estado', rotulo: 'Estado' }, { chave: 'pais', rotulo: 'País' },
      { chave: 'cep', rotulo: 'CEP' }, { chave: 'email', rotulo: 'E-mail de contato' }, { chave: 'telefones', rotulo: 'Telefone(s)' },
      { chave: 'proprietarios', rotulo: 'Proprietário(s)' }, { chave: 'atividadePrincipal', rotulo: 'Atividade principal' },
      { chave: 'missao', rotulo: 'Missão', tipo: 'textarea' }, { chave: 'alvara', rotulo: 'Alvará de localização e funcionamento' },
      { chave: 'licencaSanitaria', rotulo: 'Licença sanitária' }, { chave: 'registroCrm', rotulo: 'Registro no CRM' },
      { chave: 'diretorTecnico', rotulo: 'Diretor técnico (CRM)' }
    ],
    colunas: [
      { chave: 'razaoSocial', rotulo: 'Razão social' }, { chave: 'cnpj', rotulo: 'CNPJ' }, { chave: 'cidade', rotulo: 'Cidade' },
      { chave: 'diretorTecnico', rotulo: 'Diretor técnico' }, { chave: 'dataCadastro', rotulo: 'Cadastro' },
      { chave: 'status', rotulo: 'Status' }, { chave: 'motivoInativacao', rotulo: 'Motivo inatividade' }
    ]
  },
  setores: {
    titulo: 'Setores', inativar: true,
    campos: [
      { chave: 'nome', rotulo: 'Nome' }, { chave: 'email', rotulo: 'E-mail do setor' }, { chave: 'telefone', rotulo: 'Telefone do setor' },
      { chave: 'clinica', rotulo: 'Clínica', tipo: 'ref', ref: { recurso: 'clinicas', rotulo: 'razaoSocial', filtro: o => o.status === 'ATIVO' } },
      { chave: 'responsavel', rotulo: 'Responsável (1 por setor)', tipo: 'ref', ref: { recurso: 'colaboradores', rotulo: 'nome', filtro: o => o.status === 'ATIVO' } }
    ],
    colunas: [
      { chave: 'nome', rotulo: 'Setor' }, { chave: 'clinica.razaoSocial', rotulo: 'Clínica' }, { chave: 'responsavel.nome', rotulo: 'Responsável' },
      { chave: 'email', rotulo: 'E-mail' }, { chave: 'telefone', rotulo: 'Telefone' }, { chave: 'dataCadastro', rotulo: 'Cadastro' }, { chave: 'status', rotulo: 'Status' }
    ]
  },
  convenios: {
    titulo: 'Convênios', inativar: true,
    campos: [
      { chave: 'razaoSocial', rotulo: 'Razão social' }, { chave: 'nomeFantasia', rotulo: 'Nome fantasia' }, { chave: 'registroAns', rotulo: 'Registro ANS' },
      { chave: 'cnpj', rotulo: 'CNPJ' }, { chave: 'inscricaoMunicipal', rotulo: 'Inscrição municipal' },
      { chave: 'codigoPrestador', rotulo: 'Código de prestador' }, { chave: 'email', rotulo: 'E-mail de contato' }, { chave: 'telefone', rotulo: 'Telefone' }
    ],
    colunas: [
      { chave: 'razaoSocial', rotulo: 'Razão social' }, { chave: 'nomeFantasia', rotulo: 'Fantasia' }, { chave: 'registroAns', rotulo: 'ANS' },
      { chave: 'cnpj', rotulo: 'CNPJ' }, { chave: 'codigoPrestador', rotulo: 'Cód. prestador' }, { chave: 'dataCadastro', rotulo: 'Cadastro' }, { chave: 'status', rotulo: 'Status' }
    ]
  },
  colaboradores: {
    titulo: 'Colaboradores', inativar: true,
    campos: [
      { chave: 'nome', rotulo: 'Nome completo' }, { chave: 'cpf', rotulo: 'CPF' }, { chave: 'email', rotulo: 'E-mail' }, { chave: 'telefone', rotulo: 'Telefone' },
      { chave: 'funcao', rotulo: 'Função', tipo: 'enum', opcoes: FUNCOES }, { chave: 'localTrabalho', rotulo: 'Local de trabalho na clínica' },
      { chave: 'horarioTrabalho', rotulo: 'Horário de trabalho' }, { chave: 'cargaHorariaSemanal', rotulo: 'Carga horária semanal', tipo: 'number' },
      { chave: 'especialidade', rotulo: 'Especialidade (médico)' }, { chave: 'diasTurnosAtendimento', rotulo: 'Dias e turnos de atendimento (médico)' },
      { chave: 'conselhoRegional', rotulo: 'Conselho regional (médico)' }, { chave: 'registroConselho', rotulo: 'Nº registro no conselho (médico)' }
    ],
    colunas: [
      { chave: 'nome', rotulo: 'Nome' }, { chave: 'cpf', rotulo: 'CPF' }, { chave: 'funcao', rotulo: 'Função' }, { chave: 'especialidade', rotulo: 'Especialidade' },
      { chave: 'localTrabalho', rotulo: 'Local' }, { chave: 'cargaHorariaSemanal', rotulo: 'CH semanal' }, { chave: 'dataCadastro', rotulo: 'Cadastro' }, { chave: 'status', rotulo: 'Status' }
    ]
  },
  usuarios: {
    titulo: 'Usuários e perfis de acesso', inativar: true,
    campos: [
      { chave: 'login', rotulo: 'Login' },
      { chave: 'senha', rotulo: 'Senha (em branco ao editar = mantém)', tipo: 'password' },
      { chave: 'perfis', rotulo: 'Perfis', tipo: 'multi', opcoes: ['ADMINISTRADOR', 'COLABORADOR', 'USUARIO'] },
      { chave: 'colaborador', rotulo: 'Colaborador vinculado', tipo: 'ref', ref: { recurso: 'colaboradores', rotulo: 'nome', filtro: o => o.status === 'ATIVO' } }
    ],
    colunas: [
      { chave: 'login', rotulo: 'Login' }, { chave: 'perfis', rotulo: 'Perfis' }, { chave: 'colaborador.nome', rotulo: 'Colaborador' },
      { chave: 'dataCadastro', rotulo: 'Cadastro' }, { chave: 'status', rotulo: 'Status' }
    ]
  },
  pacientes: {
    titulo: 'Pacientes (cada paciente gera seu prontuário)', inativar: true,
    campos: [
      { chave: 'nome', rotulo: 'Paciente' }, { chave: 'cpf', rotulo: 'CPF' }, { chave: 'dataNascimento', rotulo: 'Nascimento', tipo: 'date' },
      { chave: 'telefone', rotulo: 'Telefone' },
      { chave: 'medico', rotulo: 'Médico/a', tipo: 'ref', ref: { recurso: 'colaboradores', rotulo: 'nome', filtro: medicos } },
      { chave: 'especialidade', rotulo: 'Especialidade médica' },
      { chave: 'convenio', rotulo: 'Convênio (vai no prontuário)', tipo: 'ref', ref: { recurso: 'convenios', rotulo: 'nomeFantasia', filtro: o => o.status === 'ATIVO' } }
    ],
    colunas: [
      { chave: 'nome', rotulo: 'Paciente' }, { chave: 'cpf', rotulo: 'CPF' }, { chave: 'medico.nome', rotulo: 'Médico/a' },
      { chave: 'especialidade', rotulo: 'Especialidade' }, { chave: 'convenio.nomeFantasia', rotulo: 'Convênio' },
      { chave: 'prontuarioId', rotulo: 'Nº prontuário' }, { chave: 'dataCadastro', rotulo: 'Cadastro' }, { chave: 'status', rotulo: 'Status' }
    ]
  },
  agendamentos: {
    titulo: 'Agendamento de consultas', inativar: true,
    campos: [
      { chave: 'colaborador', rotulo: 'Médico/a', tipo: 'ref', ref: { recurso: 'colaboradores', rotulo: 'nome', filtro: medicos } },
      { chave: 'paciente', rotulo: 'Paciente', tipo: 'ref', ref: { recurso: 'pacientes', rotulo: 'nome', filtro: o => o.status === 'ATIVO' } },
      { chave: 'dataConsulta', rotulo: 'Data prevista', tipo: 'date' },
      { chave: 'horaConsulta', rotulo: 'Hora prevista', tipo: 'time' }
    ],
    colunas: [
      { chave: 'dataConsulta', rotulo: 'Data' }, { chave: 'horaConsulta', rotulo: 'Hora' }, { chave: 'colaborador.nome', rotulo: 'Médico/a' },
      { chave: 'paciente.nome', rotulo: 'Paciente' }, { chave: 'dataCadastro', rotulo: 'Cadastro' }, { chave: 'status', rotulo: 'Status' }
    ],
    editarSe: l => l.status === 'AGENDADO',
    acoes: [{ rotulo: 'Receber paciente', metodo: 'GET', url: l => `/agendamentos/${l.id}/recepcao`, mostrar: true, mostrarSe: l => l.status === 'AGENDADO' }]
  },
  medicacoes: {
    titulo: 'Medicações e estoque', inativar: true,
    campos: [
      { chave: 'nome', rotulo: 'Nome' }, { chave: 'descricao', rotulo: 'Descrição completa', tipo: 'textarea' }, { chave: 'fornecedor', rotulo: 'Fornecedor (fabricação)' },
      { chave: 'armazenamento', rotulo: 'Armazenamento', tipo: 'enum', opcoes: ['REFRIGERACAO', 'TEMPERATURA_AMBIENTE'] },
      { chave: 'quantidadeEstoque', rotulo: 'Quantidade inicial em estoque', tipo: 'number', soCriar: true, padrao: 0 },
      { chave: 'estoqueMinimo', rotulo: 'Estoque mínimo', tipo: 'number', padrao: 10 },
      { chave: 'dataValidade', rotulo: 'Data de validade', tipo: 'date' }, { chave: 'dataAquisicao', rotulo: 'Data de aquisição', tipo: 'date' }
    ],
    colunas: [
      { chave: 'nome', rotulo: 'Medicação' }, { chave: 'fornecedor', rotulo: 'Fornecedor' }, { chave: 'armazenamento', rotulo: 'Armazenamento' },
      { chave: 'quantidadeEstoque', rotulo: 'Saldo' }, { chave: 'estoqueMinimo', rotulo: 'Mínimo' }, { chave: 'precisaReposicao', rotulo: 'Repor?' },
      { chave: 'dataValidade', rotulo: 'Validade' }, { chave: 'status', rotulo: 'Status' }
    ],
    acoes: [
      { rotulo: 'Repor estoque', metodo: 'POST', url: l => `/medicacoes/${l.id}/entrada`, pedir: { chave: 'quantidade', texto: 'Quantidade a repor:' }, mostrarSe: l => l.status === 'ATIVO' },
      { rotulo: 'Movimentações', metodo: 'GET', url: l => `/medicacoes/${l.id}/movimentacoes`, mostrar: true }
    ]
  },
  atendimentos: {
    titulo: 'Atendimentos (prontuário)', perfisEscrita: ['COLABORADOR'],
    campos: [
      { chave: 'prontuario', rotulo: 'Prontuário / paciente', tipo: 'ref', soCriar: true, ref: { recurso: 'prontuarios', rotulo: 'paciente.nome', filtro: o => o.status === 'ATIVO' } },
      { chave: 'agendamento', rotulo: 'Agendamento (opcional)', tipo: 'ref', soCriar: true, ref: { recurso: 'agendamentos', rotulo: 'dataConsulta|paciente.nome', filtro: o => o.status === 'AGENDADO' } },
      { chave: 'tipo', rotulo: 'Tipo de atendimento', tipo: 'enum', opcoes: ['URGENCIA', 'EMERGENCIA', 'CONSULTA', 'REVISAO'] },
      { chave: 'sintomas', rotulo: 'Sintomas informados', tipo: 'textarea' }, { chave: 'diagnostico', rotulo: 'Diagnóstico', tipo: 'textarea' },
      { chave: 'tratamento', rotulo: 'Tratamento', tipo: 'textarea' }, { chave: 'medicacaoIndicada', rotulo: 'Medicação indicada' },
      { chave: 'dosagem', rotulo: 'Dosagem' }, { chave: 'orientacoesEnfermagem', rotulo: 'Orientações para a enfermagem (internação)', tipo: 'textarea' }
    ],
    colunas: [
      { chave: 'dataHoraInicio', rotulo: 'Início' }, { chave: 'prontuario.paciente.nome', rotulo: 'Paciente' }, { chave: 'tipo', rotulo: 'Tipo' },
      { chave: 'colaborador.nome', rotulo: 'Médico/a' }, { chave: 'diagnostico', rotulo: 'Diagnóstico' }, { chave: 'medicacaoIndicada', rotulo: 'Medicação' },
      { chave: 'dosagem', rotulo: 'Dosagem' }, { chave: 'dataHoraEncerramento', rotulo: 'Encerramento' }, { chave: 'status', rotulo: 'Status' }
    ],
    editarSe: l => l.status === 'EM_ANDAMENTO',
    acoes: [{ rotulo: 'Encerrar atendimento', metodo: 'PATCH', url: l => `/atendimentos/${l.id}/encerrar`, mostrarSe: l => l.status === 'EM_ANDAMENTO', perfis: ['COLABORADOR', 'ADMINISTRADOR'] }]
  },
  solicitacoes: {
    titulo: 'Solicitações de medicação', editar: false, perfisEscrita: ['COLABORADOR'],
    campos: [
      { chave: 'medicacao', rotulo: 'Medicação', tipo: 'ref', ref: { recurso: 'medicacoes', rotulo: 'nome', filtro: o => o.status === 'ATIVO' } },
      { chave: 'quantidade', rotulo: 'Quantidade', tipo: 'number' },
      { chave: 'justificativa', rotulo: 'Justificativa', tipo: 'textarea' }
    ],
    colunas: [
      { chave: 'dataCadastro', rotulo: 'Data' }, { chave: 'medicacao.nome', rotulo: 'Medicação' }, { chave: 'quantidade', rotulo: 'Qtd' },
      { chave: 'justificativa', rotulo: 'Justificativa' }, { chave: 'solicitante.nome', rotulo: 'Solicitante' }, { chave: 'status', rotulo: 'Status' },
      { chave: 'entreguePor', rotulo: 'Entregue por' }, { chave: 'dataHoraEntrega', rotulo: 'Entrega' }
    ],
    acoes: [{ rotulo: 'Entregar', metodo: 'PATCH', url: l => `/solicitacoes/${l.id}/entregar`, mostrarSe: l => l.status === 'PENDENTE', perfis: ['USUARIO', 'ADMINISTRADOR'] }]
  },
  auditoria: {
    titulo: 'Logs de auditoria', leitura: true, campos: [],
    colunas: [
      { chave: 'dataHora', rotulo: 'Data/hora' }, { chave: 'usuario', rotulo: 'Usuário' }, { chave: 'acao', rotulo: 'Ação' },
      { chave: 'caminho', rotulo: 'Caminho' }, { chave: 'statusHttp', rotulo: 'HTTP' }
    ]
  }
};
