package vistoria;

import javax.microedition.rms.*;
import java.io.*;

public class RepositorioRMS {
    private static final String STORE_NAME = "VistoriasStore";

    public void salvar(Vistoria v) throws RecordStoreException, IOException {
        RecordStore rs = RecordStore.openRecordStore(STORE_NAME, true);
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
        rs.closeRecordStore();
    }

    public void listar(RecordEnumeration re) {
        // navigation handled by MIDlet
    }

    public RecordEnumeration enumerar() throws RecordStoreException {
        RecordStore rs = RecordStore.openRecordStore(STORE_NAME, false);
        return rs.enumerateRecords(null, null, false);
    }
}
