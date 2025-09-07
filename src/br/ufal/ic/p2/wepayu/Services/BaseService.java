package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;

// Classe base abstrata para compartilhar o repositório e validações comuns
public abstract class BaseService {
    protected final EmpregadoRepository repository;

    public BaseService(EmpregadoRepository repository) {
        this.repository = repository;
    }

    protected Empregado getEmpregadoValido(String id) throws ValidacaoException, EmpregadoNaoExisteException {
        if (id == null || id.isEmpty()) throw new IdentificacaoNulaException();
        Empregado e = repository.findById(id);
        if (e == null) throw new EmpregadoNaoExisteException("Empregado nao existe.");
        return e;
    }

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

        if (ano % 400 == 0 || (ano % 4 == 0 && ano % 100 != 0)) {
            diasNoMes[2] = 29;
        }

        return dia <= diasNoMes[mes];
    }
}