package vistoria;

import javax.microedition.rms.*;
import java.io.*;
import java.util.Vector;

public class RepositorioRMS {
    private static final String STORE_NAME = "VistoriasStore";

    public void salvar(Vistoria v) throws RecordStoreException, IOException {
        RecordStore rs = RecordStore.openRecordStore(STORE_NAME, true);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(v.getCodigoCliente());
            dos.writeUTF(v.getStatus());
            dos.writeUTF(v.getObservacao());
            dos.writeLong(v.getData());
            dos.flush();
            byte[] dados = baos.toByteArray();
            rs.addRecord(dados, 0, dados.length);
            dos.close();
            baos.close();
        } finally {
            rs.closeRecordStore();
        }
    }

    public Vector listarTodos() throws RecordStoreException, IOException {
        Vector lista = new Vector();
        RecordStore rs = RecordStore.openRecordStore(STORE_NAME, false);
        try {
            RecordEnumeration re = rs.enumerateRecords(null, null, false);
            while (re.hasNextElement()) {
                int recordId = re.nextRecordId();
                byte[] dados = rs.getRecord(recordId);
                ByteArrayInputStream bais = new ByteArrayInputStream(dados);
                DataInputStream dis = new DataInputStream(bais);
                String codigo = dis.readUTF();
                String status = dis.readUTF();
                String observacao = dis.readUTF();
                long data = dis.readLong();
                dis.close();
                bais.close();
                Vistoria v = new Vistoria(codigo, status, observacao, data);
                lista.addElement(v);
            }
            re.destroy();
        } finally {
            rs.closeRecordStore();
        }
        return lista;
    }
}
