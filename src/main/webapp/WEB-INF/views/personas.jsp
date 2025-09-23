<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <title>Personas</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style>
        :root { --gap: 10px; }
        body { font-family: system-ui, Arial, sans-serif; margin: 24px; }
        h1 { margin: 0 0 16px; }
        .toolbar { display:flex; gap: var(--gap); margin-bottom: 12px; }
        button { padding: 8px 12px; cursor: pointer; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 8px; border: 1px solid #e1e1e1; }
        th { background: #f5f5f7; text-align: left; }
        tr:hover { background: #fafafa; }
        .actions { display:flex; gap: 6px; }
        .muted { color:#777; font-size: 12px; margin-left: 8px; }
        .row { display:flex; gap: var(--gap); align-items:center; }
        .right { margin-left:auto; }
        .danger { background:#ffecec; color:#b40000; border: 1px solid #ffbcbc; }
        .grid { display:grid; grid-template-columns: repeat(2, 1fr); gap: var(--gap); }
        label { display:block; font-size:12px; color:#444; margin-bottom:4px; }
        input[type=text], input[type=tel] { width:100%; padding:10px; border:1px solid #ddd; border-radius:8px; }
        .card { border:1px solid #eee; border-radius:10px; padding:16px; background:#fff; }
    </style>
</head>
<body>
<h1>Personas</h1>

<div class="toolbar">
    <form action="${pageContext.request.contextPath}/personas" method="get" class="row">
        <input type="hidden" name="action" value="new"/>
        <button type="submit">Nueva persona</button>
    </form>
    <c:if test="${not empty status}">
        <span class="muted">${status}</span>
    </c:if>
    <c:if test="${not empty error}">
        <span class="muted" style="color:#b40000">Error: ${error}</span>
    </c:if>
</div>

<table>
    <thead>
    <tr>
        <th style="width:140px">Clave</th>
        <th>Nombre</th>
        <th>Dirección</th>
        <th style="width:160px">Teléfono</th>
        <th style="width:200px">Acciones</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty personas}">
            <tr><td colspan="5"><em>Sin registros</em></td></tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="p" items="${personas}">
                <tr>
                    <td><c:out value="${p.id}"/></td>
                    <td><c:out value="${p.nombre}"/></td>
                    <td><c:out value="${p.direccion}"/></td>
                    <td><c:out value="${p.telefono}"/></td>
                    <td class="actions">
                        <form action="${pageContext.request.contextPath}/personas" method="get" style="display:inline;">
                            <input type="hidden" name="action" value="edit"/>
                            <input type="hidden" name="clave" value="${p.id}"/>
                            <button type="submit">Editar</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/personas" method="post" style="display:inline;" onsubmit="return confirm('¿Eliminar ${p.id}?');">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="clave" value="${p.id}"/>
                            <button type="submit" class="danger">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<br/>

<div class="card">
    <c:choose>
        <c:when test="${not empty persona}">
            <h3>Editar persona</h3>
            <form action="${pageContext.request.contextPath}/personas" method="post">
                <input type="hidden" name="action" value="update"/>
                <div class="grid">
                    <div>
                        <label>Clave</label>
                        <input type="text" name="clave" value="${persona.id}" readonly/>
                    </div>
                    <div>
                        <label>Nombre</label>
                        <input type="text" name="nombre" value="${persona.nombre}" required/>
                    </div>
                    <div>
                        <label>Dirección</label>
                        <input type="text" name="direccion" value="${persona.direccion}" required/>
                    </div>
                    <div>
                        <label>Teléfono</label>
                        <input type="tel" name="telefono" value="${persona.telefono}" required/>
                    </div>
                </div>
                <div class="row" style="margin-top:12px;">
                    <div class="right">
                        <button type="submit">Guardar</button>
                    </div>
                </div>
            </form>
        </c:when>
        <c:when test="${param.action == 'new'}">
            <h3>Nueva persona</h3>
            <form action="${pageContext.request.contextPath}/personas" method="post">
                <input type="hidden" name="action" value="create"/>
                <div class="grid">
                    <div>
                        <label>Clave</label>
                        <input type="text" name="clave" required/>
                    </div>
                    <div>
                        <label>Nombre</label>
                        <input type="text" name="nombre" required/>
                    </div>
                    <div>
                        <label>Dirección</label>
                        <input type="text" name="direccion" required/>
                    </div>
                    <div>
                        <label>Teléfono</label>
                        <input type="tel" name="telefono" required/>
                    </div>
                </div>
                <div class="row" style="margin-top:12px;">
                    <div class="right">
                        <button type="submit">Guardar</button>
                    </div>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <span class="muted">Selecciona “Nueva persona” o “Editar”.</span>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
