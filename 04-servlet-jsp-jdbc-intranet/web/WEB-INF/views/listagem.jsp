<%@ page import="java.util.List, mensagens.Chamado" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head><title>Chamados - Listagem</title></head>
<body>
<h1>Chamados Internos</h1>
<hr/>
<a href="<%= request.getContextPath() %>/chamados?acao=novo">Novo Chamado</a>
<hr/>
<%
    List<Chamado> chamados = (List<Chamado>) request.getAttribute("chamados");
    if (chamados == null || chamados.isEmpty()) {
%>
    <p>Nenhum chamado cadastrado.</p>
<%
    } else {
%>
    <table border="1" cellpadding="8">
        <tr>
            <th>ID</th>
            <th>Data</th>
            <th>Titulo</th>
            <th>Solicitante</th>
            <th>Descricao</th>
        </tr>
<%
        for (Chamado c : chamados) {
%>
        <tr>
            <td><%= c.getId() %></td>
            <td><%= c.getDataAbertura() %></td>
            <td><%= c.getTitulo() %></td>
            <td><%= c.getSolicitante() %></td>
            <td><%= c.getDescricao() %></td>
        </tr>
<%
        }
%>
    </table>
<%
    }
%>
</body>
</html>
