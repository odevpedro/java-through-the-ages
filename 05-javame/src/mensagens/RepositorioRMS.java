package mensagens;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Repositorio de mensagens usando o RMS (Record Management System) do Java ME.
 *
 * -----------------------------------------------------------------------
 * RECORD MANAGEMENT SYSTEM (RMS)
 * -----------------------------------------------------------------------
 * O RMS e o unico mecanismo de persistencia local disponivel no MIDP 2.0.
 * Nao ha sistema de arquivos, banco de dados, nem Properties acessiveis
 * diretamente da aplicacao.
 *
 * O RMS armazena dados em um "RecordStore": um conjunto de registros,
 * onde cada registro e um array de bytes (byte[]) identificado por um
 * ID inteiro gerado automaticamente. Nao ha tipos, colunas, indices
 * ou queries — e um armazenamento de chave (int) -> valor (byte[]).
 *
 * A capacidade era tipicamente 8-64 KB por RecordStore, dependendo
 * do dispositivo. Ultrapassar o limite lancava RecordStoreFullException.
 * Com 128 KB de RAM total, cada byte era precioso.
 *
 * -----------------------------------------------------------------------
 * SERIALIZACAO MANUAL COM DATA STREAMS
 * -----------------------------------------------------------------------
 * Para converter uma Mensagem em byte[] e de volta:
 *
 *   Mensagem -> byte[]:
 *     ByteArrayOutputStream baos = new ByteArrayOutputStream();
 *     DataOutputStream dos = new DataOutputStream(baos);
 *     dos.writeUTF(autor);      // grava string com prefixo de tamanho
 *     dos.writeUTF(conteudo);
 *     dos.writeLong(timestamp);
 *     byte[] registro = baos.toByteArray();
 *
 *   byte[] -> Mensagem:
 *     ByteArrayInputStream bais = new ByteArrayInputStream(registro);
 *     DataInputStream dis = new DataInputStream(bais);
 *     String autor = dis.readUTF();    // le string com prefixo de tamanho
 *     String conteudo = dis.readUTF();
 *     long timestamp = dis.readLong();
 *
 * DataOutputStream.writeUTF() grava uma string no formato "Modified UTF-8":
 * 2 bytes de tamanho + bytes da string. A ordem de leitura deve ser
 * identica a ordem de escrita — sem esquema, sem nomes de campos.
 * Uma mudanca na ordem (ex: trocar autor <-> conteudo) corrompia
 * silenciosamente todos os registros existentes.
 *
 * -----------------------------------------------------------------------
 * ABERTURA E FECHAMENTO DO RECORDSTORE
 * -----------------------------------------------------------------------
 * RecordStore.openRecordStore(nome, criarSeNaoExistir) abre ou cria
 * o store. O nome e unico por MIDlet suite (conjunto de MIDlets no JAR).
 *
 * O RecordStore deve ser fechado explicitamente com closeRecordStore().
 * Nao fechar causava ResourceException na proxima abertura — bugs comuns
 * em dispositivos que nao tinham GC determinístico robusto.
 */
public class RepositorioRMS {

    /** Nome do RecordStore — unico por MIDlet suite, max 32 chars. */
    private static final String NOME_STORE = "MensagensStore";

    /**
     * Salva uma nova mensagem no RecordStore.
     *
     * @return o ID do registro criado (gerado pelo RMS)
     * @throws RecordStoreException se o store estiver cheio ou corrompido
     */
    public int salvar(Mensagem mensagem)
            throws RecordStoreException, IOException {

        byte[] dados = serializar(mensagem);

        RecordStore rs = null;
        try {
            /*
             * true = criar o store se nao existir.
             * O store persiste no dispositivo apos o MIDlet ser fechado,
             * ate que a aplicacao seja desinstalada ou os dados sejam
             * apagados pelo usuario ou pelo proprio MIDlet.
             */
            rs = RecordStore.openRecordStore(NOME_STORE, true);
            return rs.addRecord(dados, 0, dados.length);
        } finally {
            if (rs != null) {
                try { rs.closeRecordStore(); }
                catch (RecordStoreException e) { /* ignorado no finally */ }
            }
        }
    }

    /**
     * Carrega todas as mensagens do RecordStore.
     *
     * RecordEnumeration e o iterador do RMS — sem java.util.Iterator,
     * sem for-each. O metodo nextRecord() retorna byte[] do proximo registro.
     *
     * Usamos keepUpdated=false para performance: o enumerador nao se
     * atualiza automaticamente ao adicionar/remover registros durante a
     * iteracao. Em um dispositivo com 16 MHz de CPU, essa otimizacao
     * era relevante.
     *
     * @return Vector de Mensagem; vazio se o store nao existir ou estiver vazio
     */
    public Vector carregar() throws RecordStoreException, IOException {
        Vector resultado = new Vector();

        /*
         * Se o store nunca foi criado (primeira execucao ou apos
         * desinstalar), openRecordStore com createIfNecessary=false
         * lancaria RecordStoreNotFoundException. Capturamos e retornamos
         * vetor vazio, que e o comportamento correto para primeira execucao.
         */
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(NOME_STORE, false);
        } catch (javax.microedition.rms.RecordStoreNotFoundException e) {
            return resultado; // store nao existe ainda: zero mensagens
        }

        try {
            if (rs.getNumRecords() == 0) {
                return resultado;
            }

            /*
             * RecordEnumeration itera sobre todos os registros do store.
             * A ordem nao e garantida — dispositivos diferentes podiam
             * retornar registros em ordens diferentes. Para ordenacao,
             * era necessario implementar RecordComparator e passa-lo
             * como terceiro argumento de enumerateRecords().
             */
            RecordEnumeration enumerador = rs.enumerateRecords(
                null,   // RecordFilter: null = todos os registros
                null,   // RecordComparator: null = sem ordenacao garantida
                false   // keepUpdated: false = snapshot (mais rapido)
            );

            while (enumerador.hasNextElement()) {
                byte[] dados = enumerador.nextRecord();
                Mensagem m = desserializar(dados);
                resultado.addElement(m);
            }

            enumerador.destroy(); // libera recursos do enumerador

        } finally {
            try { rs.closeRecordStore(); }
            catch (RecordStoreException e) { /* ignorado */ }
        }

        return resultado;
    }

    /**
     * Remove todos os registros do RecordStore.
     * Usado para a funcao "limpar tudo" da UI.
     *
     * Alternativa mais economica seria deleteRecordStore() e recriar,
     * mas deleteRecordStore() deixa o store em estado inconsistente
     * se a operacao for interrompida (bateria acabando, por exemplo).
     * Apagar registro a registro e mais seguro.
     */
    public void limparTudo() throws RecordStoreException {
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(NOME_STORE, false);
            RecordEnumeration enumerador = rs.enumerateRecords(null, null, false);

            /*
             * Coletamos os IDs em um Vector antes de deletar.
             * Deletar durante a enumeracao pode causar comportamento
             * indefinido em algumas implementacoes de RMS.
             */
            Vector ids = new Vector();
            while (enumerador.hasNextElement()) {
                ids.addElement(Integer.valueOf(enumerador.nextRecordId()));
            }
            enumerador.destroy();

            for (int i = 0; i < ids.size(); i++) {
                int id = ((Integer) ids.elementAt(i)).intValue();
                rs.deleteRecord(id);
            }

        } catch (javax.microedition.rms.RecordStoreNotFoundException e) {
            // store nao existe: nada a limpar
        } finally {
            if (rs != null) {
                try { rs.closeRecordStore(); }
                catch (RecordStoreException e) { /* ignorado */ }
            }
        }
    }

    // -----------------------------------------------------------------------
    // SERIALIZAÇÃO MANUAL
    // -----------------------------------------------------------------------

    /**
     * Converte uma Mensagem em byte[] para armazenamento no RMS.
     *
     * Formato do registro (ordem obrigatoria):
     *   [UTF-8 autor] [UTF-8 conteudo] [long dataCriacaoMs]
     *
     * Qualquer mudanca na ordem ou nos tipos invalida registros existentes.
     */
    private byte[] serializar(Mensagem m) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream      dos  = new DataOutputStream(baos);

        dos.writeUTF(m.getAutor());
        dos.writeUTF(m.getConteudo());
        dos.writeLong(m.getDataCriacaoMs());
        dos.flush();

        return baos.toByteArray();
    }

    /**
     * Reconstroi uma Mensagem a partir de byte[] lido do RMS.
     * A ordem de leitura deve ser identica a ordem de escrita em serializar().
     */
    private Mensagem desserializar(byte[] dados) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream      dis  = new DataInputStream(bais);

        String autor      = dis.readUTF();
        String conteudo   = dis.readUTF();
        long   timestamp  = dis.readLong();

        return new Mensagem(autor, conteudo, timestamp);
    }
}
