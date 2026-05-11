package mensagens;

/**
 * Entidade de dominio adaptada ao ambiente extremamente restrito do Java ME.
 *
 * -----------------------------------------------------------------------
 * RESTRICOES DO CLDC 1.1
 * -----------------------------------------------------------------------
 * CLDC (Connected Limited Device Configuration) define o subconjunto
 * minimo do Java disponivel em dispositivos moveis: celulares com
 * 128-512 KB de RAM e processadores de 16-32 MHz.
 *
 * O que NAO existe no CLDC 1.1 (mas existe no J2SE):
 *   - java.util.Date     -> ausente. Usamos long (milissegundos desde epoch).
 *   - java.util.List     -> ausente. Usamos Vector.
 *   - java.util.ArrayList -> ausente.
 *   - java.lang.Math.* (quase tudo) -> apenas abs, min, max.
 *   - Reflection         -> ausente.
 *   - Finalizadores      -> ausente.
 *   - Thread groups      -> ausente.
 *   - java.io.File       -> ausente (sem sistema de arquivos acessivel).
 *   - Generics           -> ausente (compilador de nivel Java 1.3).
 *   - float/double       -> ausente no CLDC 1.0; presente no CLDC 1.1.
 *
 * O que EXISTE no CLDC 1.1:
 *   - java.lang.*  (String, StringBuffer, Thread, System, Math limitado)
 *   - java.util.Vector, Hashtable, Enumeration, Random, Calendar, TimeZone
 *   - java.io.DataInputStream, DataOutputStream, ByteArrayInputStream/OutputStream
 *   - javax.microedition.lcdui.* (UI)
 *   - javax.microedition.rms.* (RecordStore, persistencia local)
 *   - javax.microedition.midlet.MIDlet (classe base)
 *
 * -----------------------------------------------------------------------
 * DATA COMO LONG
 * -----------------------------------------------------------------------
 * Sem java.util.Date, a data de criacao e armazenada como long:
 * milissegundos desde 1970-01-01T00:00:00Z (Unix timestamp).
 *
 * Para exibicao, usamos java.util.Calendar (disponivel no CLDC 1.1)
 * para decompor o long em campos legiveis (dia, mes, ano, hora, minuto).
 * E verboso, mas era a unica opcao.
 *
 * -----------------------------------------------------------------------
 * SERIALIZAÇÃO MANUAL
 * -----------------------------------------------------------------------
 * Para persistir no RecordStore (RMS), a Mensagem precisa ser convertida
 * em byte[] e recuperada de byte[]. O mecanismo e DataOutputStream/
 * DataInputStream sobre ByteArray streams — sem Serializable (indisponivel).
 * O RepositorioRMS faz essa conversao.
 */
public class Mensagem {

    private String autor;
    private String conteudo;
    private long   dataCriacaoMs; // milissegundos desde epoch

    /**
     * Construtor para novas mensagens.
     * System.currentTimeMillis() e o unico mecanismo de timestamp
     * disponivel no CLDC 1.1 sem java.util.Date.
     */
    public Mensagem(String autor, String conteudo) {
        this.autor         = autor;
        this.conteudo      = conteudo;
        this.dataCriacaoMs = System.currentTimeMillis();
    }

    /**
     * Construtor para mensagens carregadas do RecordStore.
     * O timestamp e restaurado do registro gravado.
     */
    public Mensagem(String autor, String conteudo, long dataCriacaoMs) {
        this.autor         = autor;
        this.conteudo      = conteudo;
        this.dataCriacaoMs = dataCriacaoMs;
    }

    public String getAutor() {
        return autor;
    }

    public String getConteudo() {
        return conteudo;
    }

    public long getDataCriacaoMs() {
        return dataCriacaoMs;
    }

    /**
     * Formata a data de criacao em uma string legivel usando Calendar.
     *
     * Calendar e a unica alternativa a java.util.Date no CLDC 1.1.
     * setTime(ms) inicializa o Calendar a partir do timestamp em milissegundos.
     *
     * O resultado e formatado manualmente com StringBuffer e zeros a esquerda.
     * SimpleDateFormat nao existe no CLDC 1.1 — cada campo de data/hora
     * era formatado manualmente por todo desenvolvedor Java ME.
     */
    public String getDataFormatada() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new java.util.Date(dataCriacaoMs));

        int dia  = cal.get(java.util.Calendar.DAY_OF_MONTH);
        int mes  = cal.get(java.util.Calendar.MONTH) + 1; // Calendar.MONTH e 0-based
        int ano  = cal.get(java.util.Calendar.YEAR);
        int hora = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int min  = cal.get(java.util.Calendar.MINUTE);

        /*
         * StringBuffer para construcao de strings — sem StringBuilder
         * no CLDC 1.1. Zero a esquerda inserido manualmente.
         * Era um utilitario que todo desenvolvedor Java ME reimplementava
         * em cada projeto.
         */
        StringBuffer sb = new StringBuffer();
        if (dia < 10) sb.append('0');
        sb.append(dia).append('/');
        if (mes < 10) sb.append('0');
        sb.append(mes).append('/');
        sb.append(ano).append(' ');
        if (hora < 10) sb.append('0');
        sb.append(hora).append(':');
        if (min < 10) sb.append('0');
        sb.append(min);

        return sb.toString();
    }

    public String toString() {
        return autor + " [" + getDataFormatada() + "]";
    }
}
