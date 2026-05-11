package mensagens;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de mensagens com persistencia em arquivo via serializacao Java.
 *
 * -----------------------------------------------------------------------
 * SERIALIZACAO COMO MECANISMO DE PERSISTENCIA
 * -----------------------------------------------------------------------
 * Antes de frameworks como Hibernate (2002) e JPA (2006), e antes do
 * uso massivo de bancos de dados em aplicacoes desktop, serializar
 * objetos diretamente em arquivo era a forma mais simples de
 * persistencia em aplicacoes Java standalone.
 *
 * O arquivo gerado e binario e proprietario: so pode ser lido por
 * Java, e apenas por versoes compativeis da classe (mesmo serialVersionUID).
 * Qualquer refatoracao na classe Mensagem que alterasse seus campos
 * invalidaria todos os arquivos salvos anteriormente.
 *
 * Este era um problema real de manutencao: mudar a estrutura de uma
 * entidade exigia um "script de migracao" manual (ler com a versao antiga,
 * reescrever com a nova), algo que ORMs como Hibernate resolveram com
 * scripts SQL de schema migration.
 *
 * -----------------------------------------------------------------------
 * GENERICS (JDK 1.5 / Java SE 5, 2004)
 * -----------------------------------------------------------------------
 * Este modulo representa ~1998 (JDK 1.2+), mas o codigo usa List<Mensagem>
 * e ArrayList<Mensagem> para facilitar a leitura.
 *
 * Na pratica, em 1998, o codigo seria:
 *   List mensagens = new ArrayList();
 *   Mensagem m = (Mensagem) mensagens.get(0);   // cast obrigatorio
 *
 * Os comentarios no codigo marcam onde generics mudaram o idioma.
 *
 * -----------------------------------------------------------------------
 * IO CLASSICO: STREAMS ANINHADOS
 * -----------------------------------------------------------------------
 * O modelo de I/O do Java 1.0/1.2 e baseado em streams decorados
 * (Decorator Pattern): voce aninha streams um dentro do outro para
 * adicionar funcionalidades. Para serializar:
 *
 *   FileOutputStream  -> grava bytes em arquivo
 *   ObjectOutputStream -> converte objetos em bytes (precisa de FileOutputStream)
 *
 * Cada recurso (stream) precisa ser fechado explicitamente em finally.
 * O try-with-resources so foi introduzido no Java 7 (2011).
 */
public class RepositorioMensagens {

    private static final String ARQUIVO_DADOS = "mensagens.dat";

    /**
     * Persiste a lista completa de mensagens no arquivo binario.
     *
     * O arquivo e sobrescrito integralmente a cada salvamento.
     * Nao ha append incremental: toda a lista e regravada.
     * Em datasets grandes isso seria um problema de performance,
     * mas para um sistema de mensagens simples era aceitavel.
     */
    public void salvar(List mensagens) throws IOException {
        /*
         * Padrao classico de fechamento de recursos no Java pre-7:
         * declarar o stream fora do try para poder fechar no finally.
         * Fechar apenas o stream mais externo (ObjectOutputStream) e
         * suficiente — ele propaga o close() para os streams internos.
         */
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS));
            /*
             * writeObject() serializa o objeto e todas as suas
             * referencias transitivas (grafo de objetos) como bytes.
             * Escrevemos a List inteira de uma vez; ao ler, recuperamos
             * a List inteira de uma vez.
             */
            oos.writeObject(mensagens);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ignorado) {
                    // nao ha o que fazer se o close falhar;
                    // o arquivo ja foi gravado
                }
            }
        }
    }

    /**
     * Carrega a lista de mensagens do arquivo binario.
     *
     * Se o arquivo nao existir (primeira execucao), retorna lista vazia
     * em vez de lancar excecao — comportamento esperado para uma
     * aplicacao que inicia sem dados pre-existentes.
     *
     * O cast para List e inevitavel aqui: readObject() retorna Object.
     * Em pre-generics (JDK < 1.5) o cast seria para ArrayList diretamente;
     * com generics, o cast para List<Mensagem> gera um unchecked warning
     * do compilador porque a JVM nao pode verificar o tipo parametrico
     * em runtime (type erasure).
     */
    public List carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO_DADOS);
        if (!arquivo.exists()) {
            return new ArrayList();
        }

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(arquivo));
            return (List) ois.readObject(); // unchecked cast; seguro neste contexto
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ignorado) {
                    // idem ao salvar()
                }
            }
        }
    }
}
