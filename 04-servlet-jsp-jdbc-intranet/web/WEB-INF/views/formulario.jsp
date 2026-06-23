<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head><title>Novo Chamado</title></head>
<body>
<h1>Abrir Chamado</h1>
<hr/>
<a href="<%= request.getContextPath() %>/chamados">Voltar</a>
<hr/>
<%
    String erro = request.getParameter("erro");
    if (erro != null) {
%>
    <p style="color:red;"><%= erro %></p>
<%
    }
%>
<form action="<%= request.getContextPath() %>/chamados" method="post">
    <label>Titulo:<br/>
        <input type="text" name="titulo" size="50" required/>
    </label>
    <br/><br/>
    <label>Descricao:<br/>
        <textarea name="descricao" rows="5" cols="50" required></textarea>
    </label>
    <br/><br/>
    <label>Solicitante:<br/>
        <input type="text" name="solicitante" size="50" required/>
    </label>
    <br/><br/>
    <input type="submit" value="Salvar"/>
</form>
</body>
</html>
