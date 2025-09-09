# **Sistema de Folha de Pagamento - WePayU**

## **📖 Sobre o Projeto**

WePayU é um sistema de informação em Java para administrar a folha de pagamento de uma empresa. O projeto foi desenvolvido como parte da disciplina de Programação 2, com foco na aplicação de boas práticas de arquitetura de software e padrões de projeto.

O sistema gerencia diferentes tipos de empregados (horistas, assalariados e comissionados), calcula salários, aplica deduções e processa pagamentos de acordo com agendas específicas, além de suportar funcionalidades avançadas como undo/redo para todas as operações que modificam o estado do sistema.

---

## **🏛️ Arquitetura e Padrões de Projeto**

O sistema foi estruturado seguindo uma arquitetura limpa em camadas para garantir a separação de responsabilidades e a manutenibilidade do código.

- **Models**: Contém as classes que representam as entidades de negócio (Empregado, CartaoDePonto, etc.).
- **Repository**: Abstrai o acesso e a persistência dos dados, que são salvos em um arquivo `empregados.xml`.
- **Services**: Orquestra a lógica de negócio da aplicação (EmpregadoService, FolhaPagamentoService, etc.).
- **Exceptions**: Pacote com todas as exceções customizadas para um tratamento de erros específico e claro.
- **Facade**: Ponto de entrada único para o sistema, simplificando a interface para o cliente.

Os principais padrões de projeto utilizados foram:

- **Facade**: Simplifica a comunicação com o sistema.
- **Command**: Permite as funcionalidades de undo e redo.
- **Factory Method**: Centraliza a criação de diferentes tipos de empregados.
- **Polimorfismo**: Utilizado para o cálculo de salários, onde cada tipo de empregado sabe como calcular seu próprio pagamento.

---

## **🚀 Como Rodar o Projeto**

Existem duas maneiras principais de compilar e executar o projeto: utilizando uma IDE (recomendado) ou via linha de comando.

### **Requisitos**

- Java JDK 17 ou superior instalado e configurado no PATH do sistema.
- O arquivo `easyaccept.jar` localizado na pasta `lib/`.

### **1. Executando com o IntelliJ IDEA (Recomendado)**

Esta é a forma mais simples e rápida.

1.  **Abra o Projeto**:
    * Abra o IntelliJ IDEA.
    * Selecione `File > Open...` e navegue até a pasta raiz do projeto WePayU.

2.  **Configure a Biblioteca**:
    * Vá em `File > Project Structure...`.
    * No menu lateral, selecione `Libraries`.
    * Clique no ícone `+` (Add) e selecione `Java`.
    * Navegue até a pasta `lib` dentro do projeto e selecione o arquivo `easyaccept.jar`. Clique em `OK` para adicioná-lo ao projeto.

3.  **Execute os Testes**:
    * Abra o arquivo `src/Main.java`.
    * Neste arquivo, você pode descomentar a linha correspondente ao script de teste que deseja executar. Por exemplo, para rodar `us1.txt`:

    ```java
    // Main.java
    public class Main {
        public static void main(String[] args) {
            String facade = "br.ufal.ic.p2.wepayu.Facade";
            EasyAccept.main(new String[]{facade, "tests/us1.txt"});
        }
    }
    ```

    * Clique na seta verde ao lado de `public static void main` e selecione `Run 'Main.main()'`.
    * O resultado dos testes será exibido no console do IntelliJ.

### **2. Executando via Linha de Comando**

Este método funciona em qualquer sistema operacional (Windows, macOS, Linux) que tenha o JDK configurado.

1.  **Abra o Terminal**:
    * Navegue pelo terminal até a pasta raiz do seu projeto WePayU.

2.  **Compile o Código-Fonte**:
    * Execute o seguinte comando para compilar todos os arquivos `.java` e colocar os arquivos `.class` na pasta `out`. O comando já inclui a referência à biblioteca `easyaccept.jar`.

    **No Windows:**

    ```bash
    javac -d out -cp ".;lib/easyaccept.jar" src/Main.java src/br/ufal/ic/p2/wepayu/**/*.java src/br/ufal/ic/p2/wepayu/models/factory/EmpregadoFactory.java
    ```

    **No macOS ou Linux:**

    ```bash
    javac -d out -cp ".:lib/easyaccept.jar" src/Main.java src/br/ufal/ic/p2/wepayu/**/*.java src/br/ufal/ic/p2/wepayu/models/factory/EmpregadoFactory.java
    ```

3.  **Execute os Testes**:
    * Agora que o projeto está compilado, você pode rodar os testes a partir da pasta `out`.
    * Para isso, primeiro modifique o arquivo `src/Main.java` para apontar para o teste que você deseja executar (como explicado no passo 3 do método com IDE). Salve e recompile se necessário.
    * Execute o comando abaixo, que adiciona a pasta `out` e a biblioteca ao classpath.

    **No Windows:**

    ```bash
    java -cp "out;lib/easyaccept.jar" Main
    ```

    **No macOS ou Linux:**

    ```bash
    java -cp "out:lib/easyaccept.jar" Main
    ```

    O resultado dos testes será exibido diretamente no seu terminal.

---

## **📂 Estrutura do Projeto**
WePayU/
├── lib/
│   └── easyaccept.jar         # Biblioteca para os testes de aceitação
├── ok/
│   └── ...                    # Arquivos de resultado esperado para os testes
├── out/
│   └── ...                    # Arquivos compilados (.class)
├── src/
│   ├── br/ufal/ic/p2/wepayu/
│   │   ├── Exception/         # Exceções customizadas
│   │   ├── models/            # Classes de domínio e a Factory
│   │   ├── Repository/        # Camada de persistência
│   │   └── Services/          # Camada de lógica de negócio
│   │   └── Facade.java        # Ponto de entrada do sistema
│   └── Main.java              # Classe para execução dos testes
├── tests/
│   └── ...                    # Scripts de teste do EasyAccept
└── README.md                  # Este arquivo

