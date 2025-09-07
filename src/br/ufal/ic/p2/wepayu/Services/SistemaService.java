package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;

public class SistemaService extends br.ufal.ic.p2.wepayu.Services.BaseService {

    // O construtor chama o construtor da classe pai (super)
    public SistemaService(EmpregadoRepository repository) {
        super(repository);
    }

    public void zerarSistema() {
        repository.zerarDados();
    }

    public void encerrarSistema() {
        repository.salvarDados();
    }
}