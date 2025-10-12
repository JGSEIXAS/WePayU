import easyaccept.EasyAccept;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
/**
 * Classe principal para execução dos testes de aceitação do sistema WePayU.
 * Utiliza a biblioteca EasyAccept para rodar scripts de teste definidos em arquivos de texto.
 */
public class Main {
    /**
     * Ponto de entrada da aplicação.
     * Configura e executa uma série de scripts de teste com o EasyAccept.
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        String facade = "br.ufal.ic.p2.wepayu.Facade";
        EasyAccept.main(new String[]{facade, "tests/us1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us1_1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us2.txt"});
        EasyAccept.main(new String[]{facade, "tests/us2_1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us3.txt"});
        EasyAccept.main(new String[]{facade, "tests/us3_1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us4.txt"});
        EasyAccept.main(new String[]{facade, "tests/us4_1.txt"});

        EasyAccept.main(new String[]{facade, "tests/us5.txt"});
        EasyAccept.main(new String[]{facade, "tests/us5_1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us6.txt"});
        EasyAccept.main(new String[]{facade, "tests/us6_1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us7.txt"});
        EasyAccept.main(new String[]{facade, "tests/us8.txt"});
        EasyAccept.main(new String[]{facade, "tests/us9.txt"});
        EasyAccept.main(new String[]{facade, "tests/us9_1.txt"});
        EasyAccept.main(new String[]{facade, "tests/us10.txt"});
        EasyAccept.main(new String[]{facade, "tests/us10_1.txt"});
    }
}