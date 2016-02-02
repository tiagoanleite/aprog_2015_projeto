package savop;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Scanner;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Classe de métodos utilitários utilizados no SAVOP
 * 
 * @author Tiago Leite (1150780)
 * @author Diogo Madureira (1150780)
 * @since 16/11/2015
 */
public class Utilitarios{
    private static Scanner in = new Scanner(System.in); // Os métodos devem receber isto por referência
    
    /**
     * Conta o número de espaços existentes numa string
     * @param texto Texto a analisar
     * @return Número de espaços existentes
     */
    public static int contarEspacos(String texto) {
        int c = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == ' ') {
                c++;
            }
        }
        return c;
    }
    
    /**
    * Reduz um nome ao primeiro e último
    * @param nome Nome completo
    * @return Nome reduzido
    */
    public static String nomeApelido(String nome) {
        nome = nome.trim();
        int firstSpace = nome.indexOf(" ");
        int lastSpace = nome.lastIndexOf(" ");
        String res = nome.substring(0, firstSpace) + nome.substring(lastSpace);
        return res;
    }
    
    /**
     * Escreve a data atual no ecrã
     */
    public static void mostrarDataActual() {
        Calendar hoje = Calendar.getInstance();
        int diaH = hoje.get(Calendar.DAY_OF_MONTH);
        int mesH = hoje.get(Calendar.MONTH)+1; // O mês começa no 0
        int anoH = hoje.get(Calendar.YEAR);
        System.out.println("Data de hoje: " + diaH + "/" + mesH + "/" + anoH);
    }
    
    /**
     * Calcula a idade inserida a data de nascimento
     * @param dataNasc Data de nascimento em AAAAMMDD
     * @return Idade
     */
    public static int idade(String dataNasc) {
        int ano = Integer.parseInt(dataNasc.substring(0,4));
        int mes = Integer.parseInt(dataNasc.substring(4,6));
        int dia = Integer.parseInt(dataNasc.substring(6));
        
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        int mesAtual = Calendar.getInstance().get(Calendar.MONTH)+1;
        int diaAtual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        
        int idade = anoAtual - ano;
        if (mes == mesAtual) {
            if (dia < diaAtual) {
                idade--;
            }
        }
        return idade;
    }
    
    /**
     * Formata a data para um formato legível
     * @param desformatada Data em AAAAMMDD
     * @return Data em DD/MM/AAAA
     */
    public static String formatarData(String desformatada) {
        String ano = desformatada.substring(0, 4);
        String mes = desformatada.substring(4, 6);
        String dia = desformatada.substring(6);
        
        return (dia + "/" + mes + "/" + ano);
    }
    
    /**
     * Verifica se a string pode contém uma data válida dp tipo AAAAMMDD
     * @param aVerificar String a verificar
     * @return True ou False
     */
    public static boolean verificarStringData(String aVerificar) {
        if (eNumero(aVerificar)){
            int ano = Integer.parseInt(aVerificar.substring(0, 4));
            int mes = Integer.parseInt(aVerificar.substring(4, 6));
            int dia = Integer.parseInt(aVerificar.substring(6));

            int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
            int mesAtual = Calendar.getInstance().get(Calendar.MONTH)+1;
            int diaAtual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            if ((ano < anoAtual) || ((ano == anoAtual) && (mes < mesAtual)) || ((ano == anoAtual) && (mes == mesAtual) && (dia <= diaAtual))) {
                if ((mes >= 1) && (mes <=12)){
                    switch (mes) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            if ((dia >= 1) && (dia <= 31)) {
                                return true;
                            }
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            if ((dia >= 1) && (dia <= 310)) {
                                return true;
                            }
                            break;
                        case 2:
                            if ((dia >= 1) && (dia <=29) && !(verificarAnoBissexto(ano))) {
                                return true;
                            } else {
                                if ((dia >= 1) && (dia <=29) && !(verificarAnoBissexto(ano))) {
                                    return true;
                                }
                            }
                            break;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Verificar se determinado ano é bissexto
     * @param ano Ano a verificar
     * @return True ou False
     */
    public static boolean verificarAnoBissexto(int ano) {
        return (ano % 400 == 0) || ((ano % 4 == 0) && (ano % 100 != 0));
    }
    
    /**
     * Permite pausar o programa até utilizador primir o ENTER
     */
    public static void pausa() {
        System.out.println("\n\nPrima ENTER para continuar\n");
        in.nextLine();
    }
    
    /**
     * Ordena uma matriz de strings através do primeiro campo
     * @param elementos Número de elementos ocupados
     * @param tabela Matriz a organizar
     */
    public static void ordenarMatrizStrings(int elementos, String[][] tabela) {
        String temp;
        for (int i = 0; i < elementos-1; i++) {
            for (int j = i; j < elementos; j++) {
                if ((tabela[i][0].compareTo(tabela[j][0])) > 0) {
                    for (int coluna = 0; coluna < tabela[i].length; coluna++) {
                        temp = tabela[j][coluna];
                        tabela[j][coluna] = tabela[i][coluna];
                        tabela[i][coluna] = temp;
                    }
                }
            }
            
        }
    }
    
    /**
     * Pesquisa um vector de strings por um determinado valor
     * @param valor Valor a encontrar
     * @param elementos Número de elementos armazenados
     * @param vector Vector a procurar
     * @return Posição ou -1 se não existir
     */
    public static int pesquisarVectorStrings(String valor, int elementos, String[] vector) {
        for (int i = 0; i < elementos; i++) {
            if (vector[i].equals(valor)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Dados dois inteiros retorna a percengem com uma casa decimal em string
     * @param favoraveis Casos favoraveis
     * @param possiveis Casos possiveis
     * @return Percentagem formatada
     */
    public static String percentagemString(int favoraveis, int possiveis) {
        double res = percentagemFloat(favoraveis, possiveis);
        if (favoraveis * possiveis < 0) {
            return "Erro";
        } else if (possiveis == 0) {
            return "Inexistente";
        } else if (favoraveis == 0) {
            return "0%";
        }
        return (String.format("%.1f", res) + "%");
    }
    
    /**
     * Dados dois inteiros retorna a percentagem de casos em float
     * @param favoraveis Casos favoraveis
     * @param possiveis Casos possíveis
     * @return Percentagem
     */
    public static float percentagemFloat(int favoraveis, int possiveis) {
        if (possiveis == 0) {
            return -1;
        }
        return (favoraveis/(float)possiveis)*100;
    }
    
    /**
     * Verifica se um ficheiro com um determinado nome existe
     * @param nome nome do ficheiro
     * @return True ou False
     * @throws FileNotFoundException 
     */
    public static boolean ficheiroExiste(String nome) throws FileNotFoundException {
        File ficheiro = new File(nome);
        return ficheiro.exists();
    }
    
    /**
     * Permite-me apagar todas as posições de um array de chars
     * @param vetor Vetor de chars a apagar
     */
    public static void nullVetorChars(char[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            vetor[i] = '\u0000';
        }
    }
    
    /**
     * Faz ao utilizador uma pergunta de sim ou não
     * @param texto Texto da pergunta
     * @return True ou False
     */
    public static boolean perguntaSN(String texto) {
        System.out.println(texto + " (\"Sim\" ou \"Nao\")");
        String resposta;
        do {
            resposta = in.nextLine();
        } while (!(resposta.trim().equalsIgnoreCase("S")) && !(resposta.trim().equalsIgnoreCase("N")) && !(resposta.trim().equalsIgnoreCase("Sim")) && !(resposta.trim().equalsIgnoreCase("Não")) && !(resposta.trim().equalsIgnoreCase("Nao")));
        return (resposta.trim().equalsIgnoreCase("S")) || (resposta.trim().equalsIgnoreCase("Sim"));
    }
    
    /**
     * Recebe uma string e verifica se esta é um numero inteiro positivo
     * @param string String a verificar
     * @return True se todos os caracteres forem digitos ou false
     */
    public static boolean eNumero(String string)
    {
        for (char c : string.toCharArray())
        {
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Verifica se a string não é nula e se não está vazia
     * @param string String a verificar
     * @return True ou False
     */
    public static boolean stringEstaVazia(String string) {
        return ((string == null) || (string.isEmpty()));
    }
    
    /**
     * Retorna o tempo e dada actual em string
     * @return Timestamp em string
     */
    public static String timestamp() {
        Date data= new Date();
        long tempo = data.getTime();
        Timestamp ts = new Timestamp(tempo);
        return ts.toString();
    }
   
}