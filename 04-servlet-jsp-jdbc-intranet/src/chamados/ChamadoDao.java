package chamados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChamadoDao {

    public void criarTabela() throws SQLException {
        try (Connection conn = ConexaoFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS chamados (" +
                "id INTEGER IDENTITY PRIMARY KEY, " +
                "titulo VARCHAR(200) NOT NULL, " +
                "descricao VARCHAR(1000) NOT NULL, " +
                "solicitante VARCHAR(100) NOT NULL, " +
                "data_abertura VARCHAR(20) NOT NULL)");
        }
    }

    public void inserir(Chamado c) throws SQLException {
        String sql = "INSERT INTO chamados (titulo, descricao, solicitante, data_abertura) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitulo());
            ps.setString(2, c.getDescricao());
            ps.setString(3, c.getSolicitante());
            ps.setString(4, c.getDataAbertura());
            ps.executeUpdate();
        }
    }

    public List<Chamado> listar() throws SQLException {
        List<Chamado> lista = new ArrayList<>();
        String sql = "SELECT * FROM chamados ORDER BY id DESC";
        try (Connection conn = ConexaoFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Chamado c = new Chamado();
                c.setId(rs.getInt("id"));
                c.setTitulo(rs.getString("titulo"));
                c.setDescricao(rs.getString("descricao"));
                c.setSolicitante(rs.getString("solicitante"));
                c.setDataAbertura(rs.getString("data_abertura"));
                lista.add(c);
            }
        }
        return lista;
    }
}
