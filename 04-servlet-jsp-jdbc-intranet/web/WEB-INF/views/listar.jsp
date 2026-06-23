<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, mensagens.Mensagem, java.text.SimpleDateFormat" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%--
    listar.jsp — View principal do sistema de mensagens
    JSP 1.2 / Servlet 2.3 / J2EE 1.3 (~2001)

    -----------------------------------------------------------------------
    JSP: JAVA EMBUTIDO EM HTML
    -----------------------------------------------------------------------
    JSP (JavaServer Pages) permitia misturar HTML com codigo Java usando
    tags especiais:

      Scriptlet     — codigo Java executado a cada requisicao
      Expression    — valor Java convertido em String e emitido
      Declaration   — declaracao de metodo/campo na classe JSP
      Directive     — instrucoes para o compilador JSP
      Comentario JSP — comentario nao enviado ao cliente

    O container JSP compila automaticamente a pagina .jsp para uma classe
    Java (subclasse de HttpServlet) na primeira vez que e acessada. O codigo
    HTML vira chamadas a out.write(); o codigo Java e inserido diretamente.

    -----------------------------------------------------------------------
    SCRIPTLETS: O ANTI-PATTERN QUE TODOS USAVAM
    -----------------------------------------------------------------------
    Scriptlets eram a forma mais direta de usar Java em JSP.
    O problema: eles misturavam logica de negocio com apresentacao,
    dificultavam testes e criavam JSPs gigantes com Java e HTML
    entrelaçados sem separacao clara.

    A alternativa correta era JSTL (JSP Standard Tag Library), introduzida
    em J2EE 1.3 (2001), e a Expression Language (EL), padronizada em JSP 2.0
    (2003). Com JSTL:
      <c:forEach items="${mensagens}" var="m">
          ${m.autor}: ${m.conteudo}
      </c:forEach>
    Nenhuma linha de Java no HTML.

    Mas na pratica em 2001, muitas equipes ainda usavam scriptlets por
    familiaridade ou porque JSTL exigia adicionar JARs ao classpath.
    Esta JSP usa o estilo scriptlet por fidelidade historica.

    -----------------------------------------------------------------------
    SIMPLEDATEFORMAT E THREAD-SAFETY
    -----------------------------------------------------------------------
    SimpleDateFormat e instanciado por requisicao (dentro do scriptlet).
    Embora criacao de objeto por request seja ineficiente, evita o bug
    de thread-safety de um campo estatico compartilhado.
    A alternativa correta seria um ThreadLocal<SimpleDateFormat>.
--%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Sistema de Mensagens (Servlet/JSP/JDBC, ~2001)</title>
    <style type="text/css">
        body       { font-family: Arial, sans-serif; font-size: 13px;
                     background-color: #f0f0f0; margin: 20px; color: #333; }
        h1         { color: #336699; border-bottom: 2px solid #336699;
                     padding-bottom: 6px; }
        h2         { color: #555; font-size: 14px; margin-top: 20px; }
        .container { background: #fff; border: 1px solid #ccc;
                     padding: 16px; max-width: 750px; }
        .msg-table { width: 100%; border-collapse: collapse; margin-top: 8px; }
        .msg-table th { background: #336699; color: white; padding: 6px 10px;
                        text-align: left; font-weight: normal; }
        .msg-table td { padding: 6px 10px; border-bottom: 1px solid #e0e0e0;
                        vertical-align: top; }
        .msg-table tr.alt td { background-color: #f9f9f9; }
        .msg-autor  { font-weight: bold; color: #336699; white-space: nowrap; }
        .msg-data   { color: #999; font-size: 11px; white-space: nowrap; }
        .remover    { color: #cc0000; font-size: 11px; text-decoration: none; }
        .remover:hover { text-decoration: underline; }
        .form-table { border-collapse: collapse; }
        .form-table td { padding: 4px 6px; vertical-align: top; }
        .form-label { font-weight: bold; white-space: nowrap; }
        input[type=text], textarea {
            border: 1px solid #aaa; padding: 4px;
            font-family: Arial, sans-serif; font-size: 13px; }
        input[type=submit] {
            background: #336699; color: white; border: none;
            padding: 6px 18px; cursor: pointer; font-size: 13px; }
        input[type=submit]:hover { background: #2a558a; }
        .alerta-erro  { background: #ffe0e0; border: 1px solid #cc0000;
                        padding: 8px 12px; color: #990000; margin-bottom: 10px; }
        .alerta-ok    { background: #e0ffe0; border: 1px solid #009900;
                        padding: 8px 12px; color: #006600; margin-bottom: 10px; }
        .rodape       { margin-top: 14px; font-size: 11px; color: #999; }
    </style>
</head>
<body>

<div class="container">
    <h1>Sistema de Mensagens</h1>
    <p style="color:#888; font-size:11px;">
        Servlet 2.3 &middot; JSP 1.2 &middot; JDBC puro &middot; HSQLDB &mdash; (~2001)
    </p>

<%-- ======================================================================
     MENSAGENS DE FEEDBACK
     Em Servlet/JSP sem framework, feedback de operacoes era comunicado
     via request attributes (para o mesmo request) ou session attributes
     (para atravessar um redirect). Aqui usamos request attributes.
     O padrao "flash message" do Rails (2004) popularizou o uso de session
     para mensagens que sobrevivem a um redirect.
     ====================================================================== --%>
<%
    String erro    = (String) request.getAttribute("erro");
    String sucesso = (String) request.getAttribute("sucesso");
    if (erro != null) {
%>
    <div class="alerta-erro"><%= erro %></div>
<%
    }
    if (sucesso != null) {
%>
    <div class="alerta-ok"><%= sucesso %></div>
<%
    }
%>

<%-- ======================================================================
     FORMULARIO DE ADICAO
     action aponta para o URL do Servlet. method="post" garante que os
     dados nao apareçam na URL (ao contrario de method="get").

     Sem framework, nao ha binding automatico: o Servlet le cada campo
     com request.getParameter("autor") etc.
     ====================================================================== --%>
    <h2>Adicionar mensagem</h2>
    <form action="<%= request.getContextPath() %>/mensagens" method="post">
        <table class="form-table">
            <tr>
                <td class="form-label">Autor:</td>
                <td>
                    <input type="text" name="autor" size="40" maxlength="255"
                           value="<%= request.getParameter("autor") != null
                                       ? request.getParameter("autor") : "" %>">
                </td>
            </tr>
            <tr>
                <td class="form-label" style="padding-top:8px;">Conteudo:</td>
                <td>
                    <%--
                        <textarea> nao tem atributo value. O conteudo padrao
                        vai entre as tags de abertura e fechamento.
                        XSS (Cross-Site Scripting) seria um problema aqui
                        se o conteudo do parametro nao fosse escapado — mas
                        em 2001 essa preocupacao raramente era tratada em
                        codigo de demonstracao.
                    --%>
                    <textarea name="conteudo" rows="4" cols="50"></textarea>
                </td>
            </tr>
            <tr>
                <td></td>
                <td style="padding-top:6px;">
                    <input type="submit" value="Adicionar">
                </td>
            </tr>
        </table>
    </form>

<%-- ======================================================================
     LISTA DE MENSAGENS
     ====================================================================== --%>
    <h2>
        Mensagens registradas
        <%
            Integer total = (Integer) request.getAttribute("total");
            if (total != null) {
        %>
        <span style="color:#999; font-weight:normal;">
            (<%= total %> <%= total == 1 ? "mensagem" : "mensagens" %>)
        </span>
        <%
            }
        %>
    </h2>

<%
    List mensagens = (List) request.getAttribute("mensagens");
    if (mensagens == null || mensagens.isEmpty()) {
%>
    <p style="color:#999; font-style:italic;">Nenhuma mensagem cadastrada.</p>
<%
    } else {
        /*
         * SimpleDateFormat criado por request para evitar problemas de
         * thread-safety (ver comentario no cabecalho desta JSP).
         */
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
%>
    <table class="msg-table">
        <thead>
            <tr>
                <th>Autor</th>
                <th>Conteudo</th>
                <th>Data</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
<%
        for (int i = 0; i < mensagens.size(); i++) {
            Mensagem m = (Mensagem) mensagens.get(i);
            String classeAlternada = (i % 2 != 0) ? "alt" : "";
%>
            <tr class="<%= classeAlternada %>">
                <td class="msg-autor"><%= m.getAutor() %></td>
                <td><%= m.getConteudo() %></td>
                <td class="msg-data"><%= fmt.format(m.getDataCriacao()) %></td>
                <td>
                    <%--
                        Remocao via link GET (acao=remover&id=X).
                        Tecnicamente incorreto pelo REST: remocao deveria
                        ser um DELETE ou ao menos um POST. Mas em 2001
                        links para acoes era pratica comum — JavaScript
                        estava disponivel mas usar <form> para cada acao
                        era considerado excessivo para operacoes simples.
                    --%>
                    <a class="remover"
                       href="<%= request.getContextPath() %>/mensagens?acao=remover&id=<%= m.getId() %>"
                       onclick="return confirm('Remover esta mensagem?')">
                        [remover]
                    </a>
                </td>
            </tr>
<%
        }
%>
        </tbody>
    </table>
<%
    }
%>

    <p class="rodape">
        Java Through the Ages &mdash; M&oacute;dulo 04: Servlet/JSP/JDBC
    </p>
</div>

</body>
</html>
