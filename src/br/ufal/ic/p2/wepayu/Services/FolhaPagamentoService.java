package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.models.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class FolhaPagamentoService extends BaseService {
    private final ConsultaService consultaService;

    public FolhaPagamentoService(EmpregadoRepository repository, ConsultaService consultaService) {
        super(repository);
        this.consultaService = consultaService;
    }

    public void rodaFolha(String data, String saida) throws Exception {
        LocalDate dataFolha = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
        List<Empregado> empregados = repository.findAll();
        StringBuilder relatorio = new StringBuilder();
        double totalFolha = 0;

        // Cabeçalho e formatação CORRIGIDOS
        relatorio.append("FOLHA DE PAGAMENTO DO DIA ").append(dataFolha).append("\n");
        relatorio.append("====================================\n");
        relatorio.append(String.format("%-25s %15s %s\n", "Nome", "Salario Liquido", "Metodo"));
        relatorio.append("-----------------------------------------------------------------\n");

        for (Empregado empregado : empregados) {
            if (consultaService.isDiaDePagar(empregado, dataFolha)) {
                double salarioBruto = consultaService.calcularSalarioBruto(empregado, dataFolha);
                double deducoes = consultaService.calcularDeducoes(empregado, dataFolha);
                double pagamento = Math.max(0, salarioBruto - deducoes);

                if (pagamento > 0) {
                    totalFolha += pagamento;
                    relatorio.append(formatarLinhaRelatorio(empregado, pagamento));
                }
                // Atualiza o estado do empregado mesmo se o pagamento for zero
                empregado.setDataUltimoPagamento(dataFolha);
            }
        }

        relatorio.append("-----------------------------------------------------------------\n");
        relatorio.append("TOTAL FOLHA: ").append(String.format("%.2f", totalFolha).replace('.', ',')).append("\n");

        try (PrintWriter writer = new PrintWriter(new FileWriter(saida))) {
            writer.print(relatorio.toString());
        }
    }

    private String formatarLinhaRelatorio(Empregado e, double pagamento) {
        String metodoStr = "";
        MetodoPagamento mp = e.getMetodoPagamento();
        if (mp instanceof EmMaos) metodoStr = "em maos";
        if (mp instanceof Correios) metodoStr = "correios";
        if (mp instanceof Banco b) {
            metodoStr = "banco " + b.getBanco() + " Ag. " + b.getAgencia() + " CC " + b.getContaCorrente();
        }

        return String.format("%-25s %15s %s\n", e.getNome(), String.format("%.2f", pagamento).replace('.', ','), metodoStr);
    }
}