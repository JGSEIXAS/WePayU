package br.ufal.ic.p2.wepayu.Services;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValidacaoException;
import br.ufal.ic.p2.wepayu.Repository.EmpregadoRepository;
import br.ufal.ic.p2.wepayu.models.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FolhaPagamentoService extends BaseService {
    private final ConsultaService consultaService;
    private final CommandHistoryService commandHistoryService;


    public FolhaPagamentoService(EmpregadoRepository repository, ConsultaService consultaService, CommandHistoryService commandHistoryService) {
        super(repository);
        this.consultaService = consultaService;
        this.commandHistoryService = commandHistoryService;
    }


    public void rodaFolha(String data, String saida) throws Exception {
        // 1. Captura o estado do repositório ANTES de qualquer modificação.
        Map.Entry<Map<String, Empregado>, Integer> estadoAnterior = repository.getState();

        // 2. Define a ação "desfazer" (undo), que restaura o estado anterior.
        Runnable undoAction = () -> repository.setState(estadoAnterior);

        // 3. Define a ação principal (o "comando") que será executada.
        Runnable commandAction = () -> {
            try {
                LocalDate dataFolha = LocalDate.parse(data, DateTimeFormatter.ofPattern("d/M/yyyy"));
                // Importante: Buscamos os empregados DENTRO da ação para usar o estado atual.
                List<Empregado> empregados = repository.findAll();

                // A lógica de gerar o arquivo de saída permanece a mesma.
                try (PrintWriter writer = new PrintWriter(new FileWriter(saida))) {
                    writer.println("FOLHA DE PAGAMENTO DO DIA " + dataFolha);
                    writer.println("====================================");
                    writer.println();

                    double totalHoristas = gerarRelatorioHoristas(writer, empregados, dataFolha);
                    double totalAssalariados = gerarRelatorioAssalariados(writer, empregados, dataFolha);
                    double totalComissionados = gerarRelatorioComissionados(writer, empregados, dataFolha);

                    double totalFolha = totalHoristas + totalAssalariados + totalComissionados;
                    writer.printf("TOTAL FOLHA: %.2f\n", totalFolha);
                }

                // A lógica de atualizar a data do último pagamento também permanece.
                // Esta é a principal modificação de estado que o "undo" reverterá.
                for (Empregado empregado : empregados) {
                    if (consultaService.isDiaDePagar(empregado, dataFolha)) {
                        double salarioBruto = consultaService.calcularSalarioBruto(empregado, dataFolha);
                        double descontos = consultaService.calcularDeducoes(empregado, dataFolha);
                        if ((salarioBruto - descontos) > 0) {
                            empregado.setDataUltimoPagamento(dataFolha);
                        }
                    }
                }
            } catch (Exception e) {
                // Envolvemos as exceções para que possam ser tratadas pelo commandHistoryService se necessário.
                throw new RuntimeException(e);
            }
        };

        // 4. Executa o comando e regista a ação de "desfazer" no histórico.
        commandHistoryService.execute(commandAction, undoAction);
    }

    private double gerarRelatorioHoristas(PrintWriter writer, List<Empregado> empregados, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        writer.println("===============================================================================================================================");
        writer.println("===================== HORISTAS ================================================================================================");
        writer.println("===============================================================================================================================");
        writer.printf("%-36s %5s %5s %13s %9s %15s %s\n", "Nome", "Horas", "Extra", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
        writer.println("==================================== ===== ===== ============= ========= =============== ======================================");

        List<Empregado> horistas = empregados.stream()
                .filter(e -> e instanceof EmpregadoHorista)
                .sorted(Comparator.comparing(Empregado::getNome))
                .collect(Collectors.toList());

        double totalBruto = 0, totalDescontos = 0, totalLiquido = 0;
        double totalHorasNormais = 0, totalHorasExtras = 0;

        for (Empregado empregado : horistas) {
            if (consultaService.isDiaDePagar(empregado, dataFolha)) {
                String dataInicialStr = empregado.getDataUltimoPagamento().plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));
                String dataFinalStr = dataFolha.plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));
                totalHorasNormais += Double.parseDouble(consultaService.getHorasNormaisTrabalhadas(empregado.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
                totalHorasExtras += Double.parseDouble(consultaService.getHorasExtrasTrabalhadas(empregado.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));

                writer.print(formatarLinhaRelatorio(empregado, dataFolha));

                double salarioBruto = consultaService.calcularSalarioBruto(empregado, dataFolha);
                double descontos = consultaService.calcularDeducoes(empregado, dataFolha);
                totalBruto += salarioBruto;
                totalDescontos += descontos;
                totalLiquido += Math.max(0, salarioBruto - descontos);
            }
        }
        writer.println();
        writer.printf("TOTAL HORISTAS  %26.0f %5.0f %13.2f %9.2f %15.2f\n\n", totalHorasNormais, totalHorasExtras, totalBruto, totalDescontos, totalLiquido);
        return totalBruto;
    }

    private double gerarRelatorioAssalariados(PrintWriter writer, List<Empregado> empregados, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        writer.println("===============================================================================================================================");
        writer.println("===================== ASSALARIADOS ============================================================================================");
        writer.println("===============================================================================================================================");
        writer.printf("%-48s %13s %9s %15s %s\n", "Nome", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
        writer.println("================================================ ============= ========= =============== ======================================");

        List<Empregado> assalariados = empregados.stream()
                .filter(e -> e instanceof EmpregadoAssalariado && !(e instanceof EmpregadoComissionado))
                .sorted(Comparator.comparing(Empregado::getNome))
                .collect(Collectors.toList());

        double totalBruto = 0, totalDescontos = 0, totalLiquido = 0;

        for (Empregado empregado : assalariados) {
            if (consultaService.isDiaDePagar(empregado, dataFolha)) {
                writer.print(formatarLinhaRelatorio(empregado, dataFolha));
                double salarioBruto = consultaService.calcularSalarioBruto(empregado, dataFolha);
                double descontos = consultaService.calcularDeducoes(empregado, dataFolha);
                totalBruto += salarioBruto;
                totalDescontos += descontos;
                totalLiquido += Math.max(0, salarioBruto - descontos);
            }
        }
        writer.println();
        writer.printf("TOTAL ASSALARIADOS %43.2f %9.2f %15.2f\n\n", totalBruto, totalDescontos, totalLiquido);
        return totalBruto;
    }

    private double gerarRelatorioComissionados(PrintWriter writer, List<Empregado> empregados, LocalDate dataFolha) throws ValidacaoException, EmpregadoNaoExisteException {
        writer.println("===============================================================================================================================");
        writer.println("===================== COMISSIONADOS ===========================================================================================");
        writer.println("===============================================================================================================================");
        writer.printf("%-17s %8s %10s %10s %13s %9s %15s %s\n", "Nome", "Fixo", "Vendas", "Comissao", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
        writer.println("===================== ======== ======== ======== ============= ========= =============== ======================================");

        List<Empregado> comissionados = empregados.stream()
                .filter(e -> e instanceof EmpregadoComissionado)
                .sorted(Comparator.comparing(Empregado::getNome))
                .collect(Collectors.toList());

        double totalBruto = 0, totalDescontos = 0, totalLiquido = 0;
        double totalFixo = 0, totalVendas = 0, totalComissao = 0;

        for (Empregado empregado : comissionados) {
            if (consultaService.isDiaDePagar(empregado, dataFolha)) {
                writer.print(formatarLinhaRelatorio(empregado, dataFolha));

                String dataInicialStr = empregado.getDataUltimoPagamento().plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));
                String dataFinalStr = dataFolha.plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));

                double salarioBruto = consultaService.calcularSalarioBruto(empregado, dataFolha);
                double descontos = consultaService.calcularDeducoes(empregado, dataFolha);
                totalBruto += salarioBruto;
                totalDescontos += descontos;
                totalLiquido += Math.max(0, salarioBruto - descontos);

                EmpregadoComissionado c = (EmpregadoComissionado) empregado;
                totalFixo += consultaService.getSalarioFixoComissionado(c);
                double vendas = Double.parseDouble(consultaService.getVendasRealizadas(c.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
                totalVendas += vendas;
                totalComissao += consultaService.getComissaoSobreVendas(c, vendas);
            }
        }
        writer.println();
        writer.printf("TOTAL COMISSIONADOS %10.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n\n", totalFixo, totalVendas, totalComissao, totalBruto, totalDescontos, totalLiquido);
        return totalBruto;
    }

    private String formatarLinhaRelatorio(Empregado e, LocalDate data) throws ValidacaoException, EmpregadoNaoExisteException {
        double salarioBruto = consultaService.calcularSalarioBruto(e, data);
        double descontos = consultaService.calcularDeducoes(e, data);
        double salarioLiquido = Math.max(0, salarioBruto - descontos);
        String metodoPagamento = consultaService.getMetodoPagamentoFormatado(e);
        String dataInicialStr = e.getDataUltimoPagamento().plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        String dataFinalStr = data.plusDays(1).format(DateTimeFormatter.ofPattern("d/M/yyyy"));

        if (e instanceof EmpregadoHorista h) {
            String horasNormais = consultaService.getHorasNormaisTrabalhadas(h.getId(), dataInicialStr, dataFinalStr);
            String horasExtras = consultaService.getHorasExtrasTrabalhadas(h.getId(), dataInicialStr, dataFinalStr);
            return String.format("%-36s %5s %5s %13.2f %9.2f %15.2f %s\n", e.getNome(), horasNormais, horasExtras, salarioBruto, descontos, salarioLiquido, metodoPagamento);
        }
        if (e instanceof EmpregadoAssalariado) {
            return String.format("%-48s %13.2f %9.2f %15.2f %s\n", e.getNome(), salarioBruto, descontos, salarioLiquido, metodoPagamento);
        }
        if (e instanceof EmpregadoComissionado c) {
            double salarioFixo = consultaService.getSalarioFixoComissionado(c);
            double vendas = Double.parseDouble(consultaService.getVendasRealizadas(c.getId(), dataInicialStr, dataFinalStr).replace(',', '.'));
            double comissao = consultaService.getComissaoSobreVendas(c, vendas);
            return String.format("%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s\n", e.getNome(), salarioFixo, vendas, comissao, salarioBruto, descontos, salarioLiquido, metodoPagamento);
        }
        return "";
    }
}