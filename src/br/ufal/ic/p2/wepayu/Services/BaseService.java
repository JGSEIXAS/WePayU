package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;

/**
 * Classe base abstrata para os serviços do sistema.
 * Fornece acesso ao repositório de empregados e métodos de validação comuns
 * que são compartilhados entre os diferentes serviços.
 */
public abstract class BaseService {
    /**
     * Repositório de empregados para acesso aos dados.
     */
    protected final EmpregadoRepository repository;

    /**
     * Constrói uma instância de BaseService com o repositório de empregados.
     * @param repository O repositório para acesso aos dados dos empregados.
     */
    public BaseService(EmpregadoRepository repository) {
        this.repository = repository;
    }

    /**
     * Valida a existência de um empregado pelo ID.
     * @param id O ID do empregado a ser validado.
     * @return O objeto {@link Empregado} se encontrado.
     * @throws ValidacaoException Se o ID for nulo ou vazio.
     * @throws EmpregadoNaoExisteException Se nenhum empregado com o ID for encontrado.
     */
    protected Empregado getEmpregadoValido(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado e = repository.findById(id);
        if (e == null) throw new EmpregadoNaoExisteException();
        return e;
    }

    /**
     * Valida a existência e o tipo de um empregado.
     * @param id O ID do empregado a ser validado.
     * @param tipoEsperado A classe que representa o tipo esperado do empregado (ex: EmpregadoHorista.class).
     * @return O objeto {@link Empregado} se encontrado e do tipo correto.
     * @throws ValidacaoException Se o ID for nulo ou inválido.
     * @throws EmpregadoNaoExisteException Se o empregado não for encontrado ou não for do tipo esperado.
     */
    protected Empregado getEmpregadoValido(String id, Class<?> tipoEsperado) throws ValidacaoException, EmpregadoNaoExisteException {
        Empregado empregado = getEmpregadoValido(id);
        if (tipoEsperado == EmpregadoHorista.class && !(empregado instanceof EmpregadoHorista)) {
            throw new EmpregadoNaoHoristaException();
        }
        if (tipoEsperado == EmpregadoComissionado.class && !(empregado instanceof EmpregadoComissionado)) {
            throw new EmpregadoNaoComissionadoException();
        }
        return empregado;
    }

    /**
     * Verifica se uma string de data está em um formato válido ("d/M/yyyy") e representa uma data real.
     * @param dataStr A string da data a ser validada.
     * @return {@code true} se a data for válida, {@code false} caso contrário.
     */
    protected boolean isDataValida(String dataStr) {
        if (dataStr == null || !dataStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            return false;
        }
        String[] parts = dataStr.split("/");
        int dia = Integer.parseInt(parts[0]);
        int mes = Integer.parseInt(parts[1]);
        int ano = Integer.parseInt(parts[2]);

        if (dia < 1 || mes < 1 || mes > 12 || ano == 0) {
            return false;
        }

        int[] diasNoMes = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // Verifica se é um ano bissexto
        if (ano % 400 == 0 || (ano % 4 == 0 && ano % 100 != 0)) {
            diasNoMes[2] = 29;
        }

        return dia <= diasNoMes[mes];
    }
}