package savop;

import java.util.Formatter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Sistema de Apoio a Votações do Parlamento (SAVOP)
 * 
 * @author Tiago Leite (1150780)
 * @author Diogo Madureira (1150514)
 * @since 16/11/2015
 */
public class Savop {
    /***************************************************************************
     * CONFIGURAÇÕES
     **************************************************************************/

    private static final int MAX_DEPUTADOS = 230;// Número máximo de deputados a ser lido
    private static final String FILE_DEPUTADOS = "Deputados.txt"; // Nome do ficheiro com os dados
    private static final String FILE_LOG_ERROS = "registoERROS.txt"; // Nome do ficheiro de log
    private static final String PAGINA_HTML = "Pagina.html"; // Nome do ficheiro de saída
    private static final String FILE_RESULTADO = "Resultado_"; // Prefixo do ficheiro de resultado da funcionalidade 7
    private static final int MAX_LINHAS_PAGINA = 5; // Número máximo de linhas a mostrar no ecrã nas listagens
    private static final int LETRAS_ID_DEPUTADO = 3; // Número de letras no ID do deputado
    private static final int NUM_ID_DEPUTADO = 2; // Número de digitos no ID do deputado
    private static final int TAM_ID_DEPUTADO = LETRAS_ID_DEPUTADO + NUM_ID_DEPUTADO; // Número de caracteres do ID do deputado
    private static final int LIM_IDADE_1 = 35; private static final int LIM_IDADE_2 = 60; // Intervalo de idades para a funcionalidade 7
    
    
    /***************************************************************************
     * CORPO DA APLICAÇÃO
     **************************************************************************/

    // Entrada e saída da aplicação
    private static Scanner in = new Scanner(System.in);
    
    /**
     * @param args Argumentos da linha de comandos
     * @throws FileNotFoundException se os ficheiros não forem encontrados
     */
    public static void main(String[] args) throws FileNotFoundException {
        // Ficheiro de log
        Formatter log  = new Formatter(new File(FILE_LOG_ERROS));
        LogErros.registarInicio(log);
        // Deputados
        String[][] deputados = new String[MAX_DEPUTADOS][4];
        int nDeputados = 0;
        // Votacação
        String assuntoVotado = null;
        char[] votacao = new char[MAX_DEPUTADOS];
        int nVotos = 0;
        // Menu
        int op;
        
        do{
            op = menuPrincipal();
            switch (op) {
                case 1:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if ((existeDeputados(nDeputados) && Utilitarios.perguntaSN("Deseja remover a lista de deputados em memória? Isto irá também limpar a votação.")) || !(existeDeputados(nDeputados))) {
                        assuntoVotado = null; nVotos = 0; Utilitarios.nullVetorChars(votacao); // Limpa a votação
                        nDeputados = lerInfoFicheiro(deputados, log);
                        LogErros.registarEvento(log, "Lido o ficheiro deputados");
                    }
                    break;
                case 2:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if (!erroFaltaDeputados(nDeputados)) {
                        listagemDeputados(deputados, nDeputados);
                    }                    
                    break;
                case 3:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if (!erroFaltaDeputados(nDeputados)) {
                        System.out.println("\nInsira o ID do deputado a actualizar: ");
                        String idDeputado = in.next().trim(); in.nextLine();
                        if (validarIdDeputado(idDeputado)) {
                            actualizaDadosDeputado(idDeputado, deputados, nDeputados);  
                        } else {
                            System.out.println("ERRO: ID inválido");
                        }
                    }
                    break;
                case 4:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if ((existeVotacao(assuntoVotado) && Utilitarios.perguntaSN("Deseja remover a votacao em memória?")) || !(existeVotacao(assuntoVotado))) {
                        assuntoVotado = null; nVotos = 0; Utilitarios.nullVetorChars(votacao); // Limpa a votação
                        System.out.println("\nInsira o assunto a ler: ");
                        assuntoVotado = in.nextLine();
                        if (!erroAssuntoFalta(assuntoVotado)){
                            nVotos = lerVotacao(nDeputados, deputados, assuntoVotado, votacao, log);
                            LogErros.registarEvento(log, "Lido o assunto " + assuntoVotado);
                        }
                    }
                    break;
                case 5:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if (!erroFaltaDeputados(nDeputados) && !erroFaltaVotacao(assuntoVotado)) {
                       listarResultadosVotacao(nVotos, nDeputados, deputados, votacao); 
                    }
                    break;
                case 6:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if (!erroFaltaDeputados(nDeputados) && !erroFaltaVotacao(assuntoVotado)) {
                        resultadosPartidosCompleto(assuntoVotado, nDeputados, deputados, votacao, log);
                    }
                    break;
                case 7:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if (!erroFaltaDeputados(nDeputados) && !erroFaltaVotacao(assuntoVotado)) {
                        listarResultadosIdade(assuntoVotado, nDeputados, deputados, votacao);
                    }
                    break;
                case 8:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    if (!erroFaltaDeputados(nDeputados) && !erroFaltaVotacao(assuntoVotado)) {
                        resultadosPartidosHTML(assuntoVotado, nDeputados, deputados, votacao, log);
                    }
                    break;
                case 0:
                    LogErros.registarEvento(log, "Escolhida opcão " + op);
                    System.out.println("A sair do programa...");
                    LogErros.registarFecho(log);
                    log.close();
                    break;
                default:
                    LogErros.registarEvento(log, "Escolhida opcão incorrecta " + op);
                    System.out.println("Opção incorreta.");
                    break;
            }
        }while (op != 0);
        
    }
    
    /**
     * Escreve o menu no ecrã e lê a opção escolhida
     * @return Opção escolhida pelo utilizador
     */
    private static int menuPrincipal() { 
        String texto = ".: SAVOP - Menu principal :.\n\n"
                + "1 - Ler ficheiro de deputados\n"
                + "2 - Mostrar deputados\n"
                + "3 - Actualizar dados em memória\n"
                + "4 - Ler ficheiro de votação\n"
                + "5 - Mostrar deputados ordenados e respectivo voto\n"
                + "6 - Mostrar análise de votos por partido\n"
                + "7 - Mostrar votação por faixa etária\n"
                + "8 - Exportar análise de votos por partido\n"
                + "0 - Sair\n"
                + "\nInsira  a sua opção: ";
        System.out.printf("%n%s", texto);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }
    
    
    /***************************************************************************
     * FUNCIONALIDADE 1 e 2
     **************************************************************************/
    
    /**
    * Carrega informação dos deputados para memória a partir de ficheiro de 1
    * texto 
    * @param nomeFich Nome do ficheiro que contem info dos deputados
    * @param deputados Matriz de strings para guardar a info de deputados
    * @return O número de deputados inseridos na matriz
    * @throws FileNotFoundException
    */
    private static int lerInfoFicheiro(String[][] deputados, Formatter log) throws FileNotFoundException {
        Scanner ficheiro = new Scanner(new File(FILE_DEPUTADOS)); // Abre o ficheiro
        int nDeputados = 0;
        int nBrancas = 0;
        
        while (ficheiro.hasNext() && nDeputados < MAX_DEPUTADOS) {
            String linha = ficheiro.nextLine();
            
            if (linha.length() > 0) {
                nDeputados = guardarDadosDeputado(linha, deputados, nDeputados, log);
            } else {
                
                nBrancas++;
            }
        }
        
        if (nDeputados == MAX_DEPUTADOS) {
            //System.out.println("AVISO: Limite de memória atingido");
            LogErros.registarAviso(log, "Limite de memória atingido");
        }
        
        ficheiro.close(); // Fecha o ficheiro
        
        if (nBrancas > 0) {
            //System.out.println("AVISO: Existem " + nBrancas + " linhas em branco");
            LogErros.registarAviso(log, "Existem " + nBrancas + " linhas em branco");
        }
        
        System.out.println("\nEstão " + nDeputados + " deputados em memória");
        LogErros.registarMensagem(log, "Estão " + nDeputados + " deputados em memória");
        
        return nDeputados;
    }
    
    /**
    * Acede à informação de uma linha do ficheiro e guarda na estrutura dados deputados se a linha tiver a estrutura correta e o ID válido
    * @param linha String com o conteúdo de uma linha do ficheiro com info de um deputado
    * @param deputados Matriz de strings com a informação dos deputados
    * @param nDeputados Número de deputados existentes na matriz deputados
    * @return O novo número de deputados
    */
    private static int guardarDadosDeputado(String linha, String[][] deputados, int nDeputados, Formatter log) {
        String[] dados = linha.split(";"); // Separa os dados nas ;
        String id = dados[0].trim(); // O utilizador pode ter acrescentado espaços para facilitar a leitura/escrita
        if (dados.length == 4) {
            if (validarIdDeputado(id)) {
                if (pesquisarDeputadoPorID(id, deputados, nDeputados) == -1) {
                    if (Utilitarios.verificarStringData(dados[3].trim())) {
                        if (!Utilitarios.stringEstaVazia(dados[1])) {
                            if (!Utilitarios.stringEstaVazia(dados[2])) {
                                deputados[nDeputados][0] = id;              // id
                                deputados[nDeputados][1] = dados[1].trim(); // nome
                                deputados[nDeputados][2] = dados[2].trim(); // partido
                                deputados[nDeputados][3] = dados[3].trim(); // data nascimento
                                nDeputados++;
                            } else {
                               //System.out.println("ERRO: O deputado " + id + " não tem partido inserido"); 
                               LogErros.registarErro(log, "O deputado " + id + " não tem partido inserido");
                            }
                        } else {
                            //System.out.println("ERRO: O deputado " + id + " não tem nome inserido");
                            LogErros.registarErro(log, "O deputado " + id + " não tem nome inserido");
                        }
                    } else {
                        //System.out.println("ERRO: O deputado " + id + " não tem data de nascimento válida");
                        //LogErros.registarErro(log, "O deputado " + id + " não tem data de nascimento válida");
                    }
                } else {
                    //System.out.println("ERRO: O deputado " + id + " já existe");
                    LogErros.registarErro(log, "O deputado " + id + " já existe");
                }
            } else {
                //System.out.println("ERRO: O ID \"" + id + "\" não respeita a formatação correcta");
                LogErros.registarErro(log, "O ID \"" + id + "\" não respeita a formatação correcta");
            }
        } else {
            // System.out.println("ERRO: A linha não contém o número de campos correcto");
            LogErros.registarErro(log, "A linha não contém o número de campos correcto");
        }
        return nDeputados;
    }
    
    /**
     * Visualizar toda a informação dos deputados existente em memória paginada
     * @param deputados Matriz com a informação
     * @param nDeputados Número de elementos armazenados
     */
    private static void listagemDeputados(String[][] deputados, int nDeputados) {
        int contPaginas = 0;
        for (int i = 0; i < nDeputados; i++) {
            if (i % MAX_LINHAS_PAGINA == 0) {
                if (contPaginas > 0) {
                    Utilitarios.pausa();
                }
                contPaginas++;
                System.out.println("\nPágina nº " + contPaginas);
                System.out.printf("%-6s|| %-30s|| %-10s|| %-12s%n", "ID", "Nome", "Partido", "Nascimento");
                System.out.println("=================================================================");
            }
            System.out.printf("%-6s|| %-30s|| %-10s|| %-12s%n", deputados[i][0], deputados[i][1], deputados[i][2], Utilitarios.formatarData(deputados[i][3]));
        }
        
    }
    
    
    /***************************************************************************
     * FUNCIONALIDADE 3
     **************************************************************************/
    
    /**
     * Actualiza as informações do deputado
     * @param idDeputado ID do deputado a alterar
     * @param deputados Estrutura de dados dos deputados
     * @param nDeputados Número de deputados guardados
     * @return Se terminou com sucesso ou não
     * @todo Criar métodos para as opções do menu
     */
    private static boolean actualizaDadosDeputado(String idDeputado, String[][] deputados, int nDeputados) {
        int pos; String dados;
        pos = pesquisarDeputadoPorID(idDeputado, deputados, nDeputados);
        if (pos > -1) {
            // System.out.printf("%-6s|| %-30s|| %-10s|| %-12s%n", deputados[pos][0], deputados[pos][1], deputados[pos][2], deputados[pos][3]);
            int op;
            do {
                op = menuDadosDeputado(deputados[pos]);
                switch (op) {
                    case 1:
                        System.out.println("Novo nome:");
                        dados = in.nextLine();
                        if (!Utilitarios.stringEstaVazia(dados)) {
                            deputados[pos][1] = in.nextLine();
                        } else {
                            System.out.println("Não inseriu um nome");
                        }
                        break;
                    case 2:
                        System.out.println("Nova data:");
                        dados = in.nextLine();
                        if (Utilitarios.verificarStringData(dados)) {
                          deputados[pos][3] = dados;
                        } else {
                            System.out.println("Inseriu uma data inválida");
                        }
                        break;
                    case 3:
                        System.out.println("Novo ID:");
                        dados = in.nextLine();
                        if (pesquisarDeputadoPorID(dados, deputados, nDeputados) == -1) {
                            deputados[pos][0] = dados;
                        } else {
                            System.out.println("Já existe um deputado com esse ID");
                        }
                        break;
                    case 0:
                        // Não faz nada, apenas sai
                        break;
                    default:
                        System.out.println("Opção incorreta");
                        break;
                }
            } while (op != 0);
        } else {
            System.out.printf("ERRO: O deputado %s não foi encontrado", idDeputado);
            return false;
        }
        return true;
    }
    
    /**
     * Mostra o menu com as informações do Deputado
     * @param deputado Deputado a altrar
     * @return Opção escolhida
     */
    private static int menuDadosDeputado(String[] deputado) {
        System.out.println("\n.: Actualizar deputado :.");
        System.out.printf("%s || %s || %s || %s%n", deputado[0], deputado[1], deputado[2], Utilitarios.formatarData(deputado[3]));
        String texto = "1 - Nome\n"
                    + "2 - Data nascimento\n"
                    + "3 - ID\n"
                    + "0 - Voltar atrás\n"
                    + "\nInsira a opção: ";
        System.out.printf("%n%s%n", texto);
        int op = in.nextInt();
        in.nextLine();
        return op;
    }
    
    
    /***************************************************************************
     * FUNCIONALIDADE 4
     **************************************************************************/
    
    /**
     * Regista os votos do ficheiro
     * @param nDeputados Número de deputados
     * @param deputados Estrutura de dados dos deputados
     * @param assuntoVotado Assunto da votação
     * @param votacao Estrutura de dados da votação
     * @return Número de votos lidos
     * @throws FileNotFoundException 
     */
    private static int lerVotacao(int nDeputados, String[][] deputados, String assuntoVotado, char[] votacao, Formatter log) throws FileNotFoundException {
        assuntoVotado = assuntoVotado + ".txt";
        Scanner ficheiro = new Scanner(new File(assuntoVotado)); // Abre o ficheiro
        int nVotos = 0;
        while (ficheiro.hasNext()) {
            String linha = ficheiro.nextLine().trim();
            if(linha.length() == 6){
                String id = linha.substring(0, 5);
                char voto = linha.charAt(5);
                int pos = pesquisarDeputadoPorID(id, deputados, nDeputados);
                if (pos > -1) { // Verifica se o deputado existe e se o voto é válido
                    if (voto == 'S' || voto == 'N' || voto == 'A') {
                        votacao[pos] = voto;
                        nVotos++;  
                    } else {
                        //System.out.println("ERRO: voto inválido");
                        LogErros.registarErro(log, "Voto inválido");
                    }
                } else {
                    //System.out.println("ERRO: Deputado não encontrado");
                    LogErros.registarErro(log, "Deputado não encontrado");
                }
            } else {
                //System.out.println("ERRO: Linha com tamanho incorrecto");
                LogErros.registarErro(log, "Linha com tamanho incorrecto");
            }
        }
        
        if (nDeputados != nVotos) {
            //System.out.println("AVISO: Nem todos os deputados têm o voto registado");
            LogErros.registarAviso(log, "Nem todos os deputados têm o voto registado");
        }
        
        ficheiro.close(); // Fecha o ficheiro
        
        return nVotos;
    }
    
    
    
    /***************************************************************************
     * FUNCIONALIDADE 5
     **************************************************************************/
    
    /**
     * Mostra o resultado da votação d eforma pagianda
     * @param nVotos Nº de votos registados
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vector da votacao
     */
    private static void listarResultadosVotacao(int nVotos, int nDeputados, String[][] deputados, char[] votacao) {
        String[][] tabela = new String[nVotos][5];
        criarTabelaVotacao(nDeputados, deputados, votacao, tabela);
        Utilitarios.ordenarMatrizStrings(nVotos, tabela);
        int contPaginas = 0;
        for (int i = 0; i < nVotos; i++) {
            if (i % MAX_LINHAS_PAGINA == 0) {
                if (contPaginas > 0) {
                    Utilitarios.pausa();
                }
                contPaginas++;
                System.out.println("\nPágina nº " + contPaginas);
                System.out.printf("%-6s|| %-30s|| %-10s|| %-1s%n", "ID", "Nome", "Partido", "Voto");
                System.out.println("============================================================");
            }
            System.out.printf("%-6s|| %-30s|| %-10s|| %-1s%n", tabela[i][0], tabela[i][1], tabela[i][2], tabela[i][3]);
        }
    }
    
    /**
     * Cria a tabela com os resultados da votação
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vetor da votação
     * @param tabela Tabela a armazenar os resultados
     */
    private static void criarTabelaVotacao(int nDeputados, String[][] deputados, char[] votacao, String[][] tabela) {
        int c = 0;
        for (int i = 0; i < nDeputados; i++) {
              if (votacao[i]!='\u0000') { // Só guarda se existir voto registado
                tabela[c][0] = deputados[i][0];
                
                if (Utilitarios.contarEspacos(deputados[i][1]) > 1) {
                    tabela[c][1] = Utilitarios.nomeApelido(deputados[i][1]);
                } else {
                    tabela[c][1] = deputados[i][1];
                }
                
                tabela[c][2] = deputados[i][2];
                tabela[c][3] = ("" + votacao[i]);
                c++;
            }
        }
    }
    
    
    
    /***************************************************************************
     * FUNCIONALIDADE 6
     **************************************************************************/
    
    /**
     * Mostra no ecrã e grava para um ficheiro o resultado da votação por partido
     * @param assuntoVotado Assunto da votação
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vetor da votação
     * @throws FileNotFoundException 
     */
    private static void resultadosPartidosCompleto(String assuntoVotado, int nDeputados, String[][] deputados, char[] votacao, Formatter log) throws FileNotFoundException {
        String[] nomesPartidos = new String[nDeputados];
        int nPartidos = criarListaPartidos(nDeputados, deputados, nomesPartidos);
        
        int[] membrosPartidos = new int[nPartidos];
        criarContagemMembrosPartido(deputados, nDeputados, nomesPartidos, nPartidos, membrosPartidos);
        
        int[][] votosPartidos = new int[nPartidos][3]; // 0 = favor, 1 = contra, 2 = abstenções
        criarTabelaVotosPartido(nomesPartidos, nPartidos, votosPartidos, nDeputados, deputados, votacao);
        
        ordenarPorRepresentatividade(nPartidos, nomesPartidos, membrosPartidos, votosPartidos);
        
        Formatter ecra = new Formatter(System.out);
        System.out.println("");
        listarResultadosPartido(assuntoVotado, nPartidos, nomesPartidos, votosPartidos, ecra);
        
        String nomeFicheiro = FILE_RESULTADO + assuntoVotado + ".txt";
        Formatter ficheiro = new Formatter(new File(nomeFicheiro));
        listarResultadosPartido(assuntoVotado, nPartidos, nomesPartidos, votosPartidos, ficheiro);
        ficheiro.close();
        System.out.println("\n\nResultado guardado no ficheiro " + nomeFicheiro);
        LogErros.registarEvento(log, "Criado o ficheiro " + nomeFicheiro);
    }
    
    /**
     *  Lista o resultado da votação por partido
     * @param assuntoVotado Assunto da votação
     * @param nPartidos Nº de partidos
     * @param nomesPartidos Vetor do nome dos partidos
     * @param votosPartidos Matriz dos votos dos partidos
     * @param out Formatter para escrita
     */
    private static void listarResultadosPartido(String assuntoVotado, int nPartidos, String[] nomesPartidos, int[][] votosPartidos, Formatter out) {
        //System.out.println("\nVotação de: " + assuntoVotado);
        out.format("Votação de: %s%n", assuntoVotado);
        int favor = 0, contra = 0, abstencao = 0;
        for (int i = 0; i < nPartidos; i++) {
            //System.out.println(nomesPartidos[i] + "; Votos a favor: " + votosPartidos[i][0] + "; Votos contra: " + votosPartidos[i][1] + "; Abestenções: " + votosPartidos[i][2] + ".");
            out.format("%n%-10sVotos a favor: %3d; Votos contra: %3d; Abstenções: %3d.", nomesPartidos[i]+";", votosPartidos[i][0], votosPartidos[i][1], votosPartidos[i][2]);
            favor += votosPartidos[i][0];
            contra += votosPartidos[i][1];
            abstencao += votosPartidos[i][2];
        }
        //System.out.println("Totais Votos a favor: " + favor + "; Votos contra: " + contra + "; Abstenções: " + abstencao + ".");
        out.format("%n%n%-10sVotos a favor: %3d; Votos contra: %3d; Abstenções: %3d.", "Totais;", favor, contra, abstencao);
    }
    
    /**
     * Passa para um vetor a lista de todos os partidos existentes
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param nomePartidos Vetor dos partidos
     * @return Nº de partidos
     */
    private static int criarListaPartidos(int nDeputados, String[][] deputados, String[] nomePartidos) {
        int nPartidos = 0;
        for (int i = 0; i < nDeputados; i++) {
            if (Utilitarios.pesquisarVectorStrings(deputados[i][2], nPartidos, nomePartidos) == -1) {
                nomePartidos[nPartidos] = deputados[i][2];
                nPartidos++;
            }
        }
        return nPartidos;
    }
    
    /**
     * Conta os votos de um determinado partido
     * @param partido Partido pretendido
     * @param votosPartidos Matriz dos votos do partido
     * @param pos Linha do partido na matriz
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vetor da votação
     */
    private static void contarVotosPartido(String partido, int[][] votosPartidos, int pos, int nDeputados, String[][] deputados, char[] votacao) {
        for (int i = 0; i < nDeputados; i++) {
            if (deputados[i][2].equals(partido)) {
                switch (votacao[i]) {
                    case 'S': votosPartidos[pos][0]++; break;
                    case 'N': votosPartidos[pos][1]++; break;
                    case 'A': votosPartidos[pos][2]++; break;
                }
            }
        }
    }
    
    /**
     * Conta o número de membros de um determinado partido
     * @param partido Partido pretendido
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @return Nº de membros
     */
    private static int contarMembrosPartido(String partido, int nDeputados, String[][] deputados) {
        int nMembros = 0;
        for (int i = 0; i < nDeputados; i++) {
            if (deputados[i][2].equals(partido)) {
                nMembros++;
            }
        }
        return nMembros;
    }
    
    /**
     * Conta o número de membros de cada partido
     * @param deputados Matriz com os dados dos deputados
     * @param nDeputados Nº de deputados existentes
     * @param nomePartidos Vector com os nomes dos partidos
     * @param nPartidos Nº de partidos existentes
     * @param membrosPartidos Vector com o nº de deputados do partido
     */
    private static void criarContagemMembrosPartido(String[][] deputados, int nDeputados, String[] nomePartidos, int nPartidos, int[] membrosPartidos) {
        for (int i = 0; i < nPartidos; i++) {
            membrosPartidos[i] = contarMembrosPartido(nomePartidos[i], nDeputados, deputados);
        }
    }
   
    /**
     * Conta os votos de cada partido
     * @param nomePartidos Vector dos nomes dos partidos
     * @param nPartidos Nº de partidos
     * @param votosPartidos Nº de votos registados
     * @param nDeputados Nº de deputados existentes
     * @param deputados Matriz com os dados dos deputados
     * @param votacao Vector com os votos dos deputados
     */
    private static void criarTabelaVotosPartido(String[] nomePartidos, int nPartidos, int[][] votosPartidos, int nDeputados, String[][] deputados, char[] votacao) {
        for (int i = 0; i < nPartidos; i++) {
            contarVotosPartido(nomePartidos[i], votosPartidos, i, nDeputados, deputados, votacao);
        }
    }
    
    /**
     * Organiza a tabela de votos por partido pela representatividade de cada partido
     * @param nPartidos Nº de partidos
     * @param nomePartidos Vector dos nomes dos partidos
     * @param membrosPartidos Vector do nº de deputados do partido
     * @param votosPartidos Matriz do nº de votos do partido
     */
    private static void ordenarPorRepresentatividade(int nPartidos, String[] nomePartidos, int[] membrosPartidos, int[][] votosPartidos) {
        for (int i = 0; i < nPartidos-1; i++) {
            for (int j = i; j < nPartidos; j++) {
                if ((membrosPartidos[i] < membrosPartidos[j])) {
                    trocarTabelaVotosPartido(i, j, membrosPartidos, nomePartidos, votosPartidos);
                }else if ((membrosPartidos[i] == membrosPartidos[j])){
                    if (nomePartidos[i].compareTo(nomePartidos[j]) > 0){
                        trocarTabelaVotosPartido(i, j, membrosPartidos, nomePartidos, votosPartidos);
                    }
                }
            }
        }
    }
    
    /**
     * Troca os dados entre duas posições das estruturas de dados da tabela de votos por partido
     * @param i Posição final
     * @param j Posilão inicial
     * @param membrosPartidos Vector do nº de deputados do partido
     * @param nomePartidos Vector dos nomes dos partidos
     * @param votosPartidos Matriz do nº de votos do partido
     */
    private static void trocarTabelaVotosPartido(int i, int j, int[] membrosPartidos, String[] nomePartidos, int[][] votosPartidos) {
        int nTemp;
        String sTemp;
        
        // Ordena os votos
        for (int coluna = 0; coluna < votosPartidos[i].length; coluna++) {
            nTemp = votosPartidos[j][coluna];
            votosPartidos[j][coluna] = votosPartidos[i][coluna];
            votosPartidos[i][coluna] = nTemp;
        }

        // Ordena os nomes
        sTemp = nomePartidos[j];
        nomePartidos[j] = nomePartidos[i];
        nomePartidos[i] = sTemp;

        // Ordena os membros
        nTemp = membrosPartidos[j];
        membrosPartidos[j] = membrosPartidos[i];
        membrosPartidos[i] = nTemp;
    }
 
    
    
    /***************************************************************************
     * FUNCIONALIDADE 7
     **************************************************************************/

    /**
     * Lista os resultados da votação por idade
     * @param assuntoVotado Assunto votado
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vetor da votação
     * 
     * @todo Colocar o output num for
     * @todo Formatar o output para ficar com os espaços direitos
     */
    private static void listarResultadosIdade(String assuntoVotado, int nDeputados, String[][] deputados, char[] votacao) {
        System.out.println("\nVotação de: " + assuntoVotado);
        int[][] votosIdades = new int[3][4];
        contarVotosIdades(nDeputados, deputados, votacao, votosIdades, LIM_IDADE_1, LIM_IDADE_2);
        
        System.out.println("\nMenores de " + LIM_IDADE_1 + " anos");
        if (votosIdades[0][3] > 0) {
            System.out.println("Favor: " + Utilitarios.percentagemString(votosIdades[0][0], votosIdades[0][3]) + " Contra: " + Utilitarios.percentagemString(votosIdades[0][1], votosIdades[0][3]) + " Abstenção: " + Utilitarios.percentagemString(votosIdades[0][2], votosIdades[0][3]));
        } else {
            System.out.println("Não existem deputados desta idade");
        }

        System.out.println("\nEntre " + LIM_IDADE_1 + " e " + LIM_IDADE_2 + " anos");
        if (votosIdades[1][3] > 0) {
            System.out.println("Favor: " + Utilitarios.percentagemString(votosIdades[1][0], votosIdades[1][3]) + " Contra: " + Utilitarios.percentagemString(votosIdades[1][1], votosIdades[1][3]) + " Abstenção: " + Utilitarios.percentagemString(votosIdades[1][2], votosIdades[1][3]));            
        } else {
            System.out.println("Não existem deputados desta idade");
        }
        
        System.out.println("\nSuperiores a " + LIM_IDADE_1 + " anos");
        if (votosIdades[2][3] > 0) {
            System.out.println("Favor: " + Utilitarios.percentagemString(votosIdades[2][0], votosIdades[2][3]) + " Contra: " + Utilitarios.percentagemString(votosIdades[2][1], votosIdades[2][3]) + " Abstenção: " + Utilitarios.percentagemString(votosIdades[2][2], votosIdades[2][3]));
        } else {
            System.out.println("Não existem deputados desta idade");
        }
    }
    
    /**
     * Passa para um vetor o número de votos de cada tipo
     * @param nDeputados Nº de deputados
     * @param votacao Vetor da votação
     * @param votos Vetor do nº de votos
     */
    private static void contarVotosTotais(int nDeputados, char[] votacao, int[] votos) {
        for (int i = 0; i < nDeputados; i++) {
            switch (votacao[i]) {
                case 'S': votos[0]++; votos[3]++; break;
                case 'N': votos[1]++; votos[3]++; break;
                case 'A': votos[2]++; votos[3]++; break;
            }
        }
    }
    
    /**
     * Dadas duas idades, passa para uma matriz os votos abaixo da menor, entre as duas e acima da segunda
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vetor da votacao
     * @param votos Matriz da contagem dos votos
     * @param lim1 1ª idade
     * @param lim2 2ª idade
     */
    private static void contarVotosIdades(int nDeputados, String[][] deputados, char[] votacao, int[][] votos, int lim1, int lim2) {
        int pos, idade;
        for (int i = 0; i < nDeputados; i++) {
            idade = Utilitarios.idade(deputados[i][3]);
            if ((idade > 0) && (idade < 35)){
                pos = 0;
            }else if ((35 <= idade) && (idade <= 60)) {
                pos = 1;
            }else{
                pos = 2;
            }
            
            switch (votacao[i]) {
                case 'S': votos[pos][0]++; votos[pos][3]++; break;
                case 'N': votos[pos][1]++; votos[pos][3]++; break;
                case 'A': votos[pos][2]++; votos[pos][3]++; break;
            }
        }
    }   
    
    
    
    /***************************************************************************
     * FUNCIONALIDADE 8
     **************************************************************************/
    
    /**
     * Dado o nome de um ficheiro verifica se este existe
     * @param nome Nome do ficheiro
     * @return True or False
     * @throws FileNotFoundException 
     */
    private static boolean verificarFicheiroExiste(String nome) throws FileNotFoundException {
        File ficheiro = new File(nome);
        return ficheiro.exists();
    }
    
    /**
     * Exporta para uma página HTML os resultados da votação por partido
     * @param assuntoVotado Assunto votado
     * @param nDeputados Nº de deputados
     * @param deputados Matriz dos deputados
     * @param votacao Vetor da votação
     * @throws FileNotFoundException 
     */
    private static void resultadosPartidosHTML(String assuntoVotado, int nDeputados, String[][] deputados, char[] votacao, Formatter log) throws FileNotFoundException {
        String[] nomesPartidos = new String[nDeputados];
        int nPartidos = criarListaPartidos(nDeputados, deputados, nomesPartidos);
        
        int[] membrosPartidos = new int[nPartidos];
        criarContagemMembrosPartido(deputados, nDeputados, nomesPartidos, nPartidos, membrosPartidos);
        
        int[][] votosPartidos = new int[nPartidos][3]; // 0 = favor, 1 = contra, 2 = abstenções
        criarTabelaVotosPartido(nomesPartidos, nPartidos, votosPartidos, nDeputados, deputados, votacao);
        
        ordenarPorRepresentatividade(nPartidos, nomesPartidos, membrosPartidos, votosPartidos);
        
        int[] totaisVotos = new int[3];
        totaisVotos(votosPartidos, nDeputados, totaisVotos);
        
        Formatter pag = new Formatter(new File(PAGINA_HTML));
        PaginaHTML.iniciarPagina(pag, PAGINA_HTML);
        
        PaginaHTML.cabecalho(pag, 1, "Votação de: " + assuntoVotado);
        
        String[] titulos = {"Partido", "Favor", "Contra", "Abstenções"};

        String[][] conteudo = new String[nDeputados+1][4];
        conteudo[nPartidos][0] = "<b>Totais</b>";
        for (int i = 0; i < nPartidos; i++) {
            conteudo[i][0] = nomesPartidos[i];
            for (int j = 0; j < 3; j++) {
                conteudo[i][j+1] = Integer.toString(votosPartidos[i][j]);
            }
        }
        for (int i = 0; i < 3; i++) {
            conteudo[nPartidos][i+1] = Integer.toString(totaisVotos[i]);
        }
        
        PaginaHTML.criarTabelaComLinhaTitulos(pag, titulos, conteudo, nPartidos+1);
        PaginaHTML.fecharPagina(pag);
        pag.close();
        
        System.out.println("\n\nFicheiro criado com o nome " + PAGINA_HTML);
        LogErros.registarEvento(log, "Criado ficheiro " + PAGINA_HTML);
    }
    
    /**
     * Passa para um vetor a soma dos votos da matriz dos votos por partido
     * @param votosPartidos Matriz de votos por partido
     * @param nPartidos Nº de partidos
     * @param totais Vetor dos totais dos votos
     */
    private static void totaisVotos(int[][] votosPartidos, int nPartidos, int[] totais) {
        for (int i = 0; i < 3; i++) {
            totais[0] += votosPartidos[i][0];
            totais[1] += votosPartidos[i][1];
            totais[2] += votosPartidos[i][2];
        }
    }
    
    /***************************************************************************
     * AUXILIARES DE CONTROLO
     **************************************************************************/
    
    /**
     * Procura a linha do deputado por ID
     * @param idDeputado ID do deputado a procurar
     * @param deputados Estrutura com os dados
     * @param nDeputados Número de deputados armazenados
     * @return Posição ou -1 se não existir
     */
    private static int pesquisarDeputadoPorID(String idDeputado, String[][] deputados, int nDeputados) {
        for (int i = 0; i < nDeputados; i++) {
            if (deputados[i][0].equals(idDeputado)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Valida o ID do deputado
     * @param idDeputado ID de deputado a validar
     * @return True ou False
     */
    private static boolean validarIdDeputado(String idDeputado) {
        // Verifica se tem o tamanho correcto
        if(idDeputado.length() == TAM_ID_DEPUTADO){
            int cont = 0;
            // Verifica se tem o número certo de letras
            for (int i = 0; i < LETRAS_ID_DEPUTADO; i++) {
                if (Character.isUpperCase(idDeputado.charAt(i))) {
                    cont++;
                }
            }
            // Verifica se tem o número certo de digitos
            for (int i = LETRAS_ID_DEPUTADO; i < TAM_ID_DEPUTADO; i++) {
                if (Character.isDigit(idDeputado.charAt(i))) {
                    cont++;
                }
            }
            return cont == idDeputado.length();
        }
        return false;
    }
  
    /**
     * Mostra mensagem de erro se não existirem deputados
     * @param nDeputados Nº de deputados
     * @return True se mostrou erro ou False se não mostrou
     */
    private static boolean erroFaltaDeputados(int nDeputados) {
        if (!existeDeputados(nDeputados)) {
           System.out.println("Leia primeiro um ficheiro deputados");
           return true; 
        }
        return false;
    }
    
    /**
     * Mostra mensagem de erro se não existir assunto de votação lido
     * @param assuntoVotado Assunto votado
     * @return True se mostrou erro ou False se não mostrou
     */
    private static boolean erroFaltaVotacao(String assuntoVotado) {
       if (!existeVotacao(assuntoVotado)) {
            System.out.println("Leia primeiro uma lei");
            return true;
        }
        return false;
    }
    
    /**
     * Mostra mensagem de erro se o assunto inserido é inválido
     * @param assuntoVotado Assunto votado
     * @return True se mostrou erro ou False se não mostrou
     */
    private static boolean erroAssuntoFalta(String assuntoVotado) {
       if (!existeVotacao(assuntoVotado)) {
            System.out.println("Não inseriu o nome de um assunto");
            return true;
        }
        return false;
    }
    
    /**
     * Verifica se existem deputados
     * @param nDeputados Nº de deputados
     * @return True ou False
     */
    private static boolean  existeDeputados(int nDeputados) {
        return nDeputados != 0;
    }
    
    /**
     * Verifica se existe uma votação válida
     * @param assuntoVotado Assunto votado
     * @return True ou false
     */
    private static boolean existeVotacao (String assuntoVotado){
        return ((assuntoVotado != null) && !(assuntoVotado.trim().isEmpty()));
    }
                    
}