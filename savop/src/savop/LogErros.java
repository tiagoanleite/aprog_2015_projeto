package savop;

import java.util.Formatter;

/**
 * Classe que disponibiliza métodos para registo de erros
 * 
 * @author Tiago Leite (1150780)
 * @author Diogo Madureira (1150514)
 * @since 04/12/2015
 */
public class LogErros {
    
    /**
     * Regista o início do programa no ficheiro log
     * @param log Ficheiro de registo
     */
    public static void registarInicio(Formatter log) {
        log.format("[%-23s] PROGRAMA INICIADO%n", Utilitarios.timestamp());
    }
    
    /**
     * Regista um evento no ficheiro log
     * @param log Ficheiro log
     * @param mensagem Evento a registar
     */
    public static void registarMensagem(Formatter log, String mensagem){
        log.format("[%-23s] MENSAGEM: %s%n", Utilitarios.timestamp(), mensagem);
    }
    
    /**
     * Regista um erro no ficheiro log
     * @param log Ficheiro de registo
     * @param erro Mensagem a registar
     */
    public static void registarErro(Formatter log, String erro) {
        log.format("[%-23s] ERRO: %s%n", Utilitarios.timestamp(), erro);
    }
    
    /**
     * Regista um aviso no ficheiro log
     * @param log Ficheiro de registo
     * @param aviso Mensagem a registar
     */
    public static void registarAviso(Formatter log, String aviso) {
        log.format("[%-23s] AVISO: %s%n", Utilitarios.timestamp(), aviso);
    }
    
    /**
     * Regista um evento no ficheiro log
     * @param log Ficheiro de registo
     * @param evento Mensagem a registar
     */
    public static void registarEvento(Formatter log, String evento) {
        log.format("[%-23s] EVENTO: %s%n", Utilitarios.timestamp(), evento);
    }
    
    /**
     * Regista o fecho do programa no ficheiro log
     * @param log Ficheiro de registo
     */
    public static void registarFecho(Formatter log){
        log.format("[%-23s] PROGRAMA TERMINADO", Utilitarios.timestamp());
    }
 
}