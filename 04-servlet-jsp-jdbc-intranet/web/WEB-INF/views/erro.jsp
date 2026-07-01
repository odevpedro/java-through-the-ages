<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Erro - Sistema de Chamados</title>
    <style type="text/css">
        body       { font-family: Arial, sans-serif; font-size: 13px;
                     background-color: #f0f0f0; margin: 20px; color: #333; }
        h1         { color: #990000; border-bottom: 2px solid #990000;
                     padding-bottom: 6px; }
        .container { background: #fff; border: 1px solid #ccc;
                     padding: 16px; max-width: 700px; }
        .voltar    { color: #336699; }
    </style>
</head>
<body>
<div class="container">
    <h1>Erro ao processar a requisicao</h1>
    <p>O sistema nao conseguiu completar a operacao solicitada.</p>
    <p><a class="voltar" href="<%= request.getContextPath() %>/chamados">Voltar para chamados</a></p>
</div>
</body>
</html>
