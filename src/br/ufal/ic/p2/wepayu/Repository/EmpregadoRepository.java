package br.ufal.ic.p2.wepayu.Repository;

import br.ufal.ic.p2.wepayu.Services.CommandHistoryService;
import br.ufal.ic.p2.wepayu.models.Empregado;
import java.beans.*;
import java.io.*;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório para gerenciar a persistência de dados dos empregados.
 * Esta classe abstrai o acesso aos dados, lendo e salvando o estado
 * dos empregados em um arquivo XML ("empregados.xml").
 */
public class EmpregadoRepository {

    private Map<String, Empregado> empregados;
    private int idCont;

    /**
     * Construtor que inicializa o repositório, carregando os dados existentes
     * do arquivo de persistência.
     */
    public EmpregadoRepository() {
        carregarDados();
    }

    /**
     * Carrega os dados dos empregados e o contador de ID do arquivo "empregados.xml".
     * Se o arquivo não existir ou ocorrer um erro na leitura, inicializa um estado vazio.
     */
    private void carregarDados() {
        File arquivo = new File("empregados.xml");
        if (arquivo.exists()) {
            try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(arquivo)))) {
                this.empregados = (Map<String, Empregado>) decoder.readObject();
                this.idCont = (int) decoder.readObject();
            } catch (Exception e) {
                this.empregados = new HashMap<>();
                this.idCont = 0;
            }
        } else {
            this.empregados = new HashMap<>();
            this.idCont = 0;
        }
    }

    /**
     * Salva o estado atual do mapa de empregados e do contador de ID no arquivo "empregados.xml".
     * Utiliza um PersistenceDelegate para serializar corretamente objetos {@link LocalDate}.
     */
    public void salvarDados() {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("empregados.xml")))) {
            encoder.setPersistenceDelegate(LocalDate.class,
                    new PersistenceDelegate() {
                        @Override
                        protected Expression instantiate(Object oldInstance, Encoder out) {
                            LocalDate localDate = (LocalDate) oldInstance;
                            return new Expression(localDate, LocalDate.class, "of",
                                    new Object[]{localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()});
                        }
                    });
            encoder.writeObject(this.empregados);
            encoder.writeObject(this.idCont);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Limpa todos os dados de empregados e reseta o contador de ID.
     */
    public void zerarDados() {
        if (this.empregados != null) this.empregados.clear();
        this.idCont = 0;
    }

    /**
     * Busca um empregado pelo seu ID.
     * @param id O ID do empregado a ser buscado.
     * @return O objeto {@link Empregado} correspondente, ou null se não for encontrado.
     */
    public Empregado findById(String id) {
        return this.empregados.get(id);
    }

    /**
     * Retorna uma lista com todos os empregados cadastrados.
     * @return Uma {@link List} de objetos {@link Empregado}.
     */
    public List<Empregado> findAll() {
        return new ArrayList<>(this.empregados.values());
    }

    /**
     * Salva ou atualiza um empregado no repositório.
     * @param empregado O objeto {@link Empregado} a ser salvo.
     */
    public void save(Empregado empregado) {
        this.empregados.put(empregado.getId(), empregado);
    }

    /**
     * Remove um empregado do repositório pelo seu ID.
     * @param id O ID do empregado a ser removido.
     * @return O objeto {@link Empregado} que foi removido.
     */
    public Empregado deleteById(String id) {
        return this.empregados.remove(id);
    }

    /**
     * Gera e retorna o próximo ID sequencial para um novo empregado.
     * @return Uma string representando o próximo ID disponível.
     */
    public String getNextId() {
        return String.valueOf(++this.idCont);
    }

    /**
     * Cria e retorna um snapshot (cópia profunda) do estado atual do repositório.
     * Usado pelo {@link CommandHistoryService} para a funcionalidade de undo.
     * @return Um {@link Map.Entry} contendo a cópia do mapa de empregados e o valor do contador de ID.
     */
    public Map.Entry<Map<String, Empregado>, Integer> getState() {
        Map<String, Empregado> empregadosCopiaProfunda = new HashMap<>();
        for (Map.Entry<String, Empregado> entry : this.empregados.entrySet()) {
            empregadosCopiaProfunda.put(entry.getKey(), entry.getValue().clone());
        }
        return new AbstractMap.SimpleEntry<>(empregadosCopiaProfunda, this.idCont);
    }

    /**
     * Restaura o estado do repositório a partir de um snapshot.
     * Usado pelo {@link CommandHistoryService} para a funcionalidade de undo.
     * @param state O {@link Map.Entry} contendo o estado a ser restaurado.
     */
    public void setState(Map.Entry<Map<String, Empregado>, Integer> state) {
        Map<String, Empregado> deepCopiedMap = new HashMap<>();
        for (Map.Entry<String, Empregado> entry : state.getKey().entrySet()) {
            deepCopiedMap.put(entry.getKey(), entry.getValue().clone());
        }
        this.empregados = deepCopiedMap;
        this.idCont = state.getValue();
    }
}
