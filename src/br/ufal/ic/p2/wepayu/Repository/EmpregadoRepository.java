package br.ufal.ic.p2.wepayu.Repository;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.beans.*;
import java.io.*;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpregadoRepository {

    private Map<String, Empregado> empregados;
    private int idCont;

    public EmpregadoRepository() {
        carregarDados();
    }

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

    public void zerarDados() {
        if (this.empregados != null) this.empregados.clear();
        this.idCont = 0;
    }

    public Empregado findById(String id) {
        return this.empregados.get(id);
    }

    public List<Empregado> findAll() {
        return new ArrayList<>(this.empregados.values());
    }

    public void save(Empregado empregado) {
        this.empregados.put(empregado.getId(), empregado);
    }

    public Empregado deleteById(String id) {
        return this.empregados.remove(id);
    }

    public String getNextId() {
        return String.valueOf(++this.idCont);
    }

    public Map.Entry<Map<String, Empregado>, Integer> getState() {
        Map<String, Empregado> empregadosCopiaProfunda = new HashMap<>();
        for (Map.Entry<String, Empregado> entry : this.empregados.entrySet()) {
            empregadosCopiaProfunda.put(entry.getKey(), entry.getValue().clone());
        }
        return new AbstractMap.SimpleEntry<>(empregadosCopiaProfunda, this.idCont);
    }

    public void setState(Map.Entry<Map<String, Empregado>, Integer> state) {
        Map<String, Empregado> deepCopiedMap = new HashMap<>();
        for (Map.Entry<String, Empregado> entry : state.getKey().entrySet()) {
            deepCopiedMap.put(entry.getKey(), entry.getValue().clone());
        }
        this.empregados = deepCopiedMap;
        this.idCont = state.getValue();
    }
}