package pedidos;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class PedidoRepository implements InitializingBean {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void afterPropertiesSet() {
        criarTabela();
    }

    public void criarTabela() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS pedidos (" +
            "id INTEGER IDENTITY PRIMARY KEY, " +
            "cliente VARCHAR(255), " +
            "produto VARCHAR(255), " +
            "quantidade INTEGER, " +
            "valor_total DECIMAL(12,2), " +
            "status VARCHAR(50))");
    }

    public int salvar(Pedido p) {
        String sql = "INSERT INTO pedidos (cliente, produto, quantidade, valor_total, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getCliente());
            ps.setString(2, p.getProduto());
            ps.setInt(3, p.getQuantidade());
            ps.setBigDecimal(4, p.getValorTotal());
            ps.setString(5, p.getStatus());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void atualizarStatus(int id, String status) {
        jdbcTemplate.update("UPDATE pedidos SET status = ? WHERE id = ?", status, id);
    }

    public List<Pedido> listar() {
        return jdbcTemplate.query("SELECT * FROM pedidos", (rs, rowNum) -> {
            Pedido p = new Pedido();
            p.setId(rs.getInt("id"));
            p.setCliente(rs.getString("cliente"));
            p.setProduto(rs.getString("produto"));
            p.setQuantidade(rs.getInt("quantidade"));
            p.setValorTotal(rs.getBigDecimal("valor_total"));
            p.setStatus(rs.getString("status"));
            return p;
        });
    }
}
