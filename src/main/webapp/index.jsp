<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
        .muted { color:#777; font-size: 12px; }
        .modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,.25); display:none; align-items: center; justify-content: center; padding: 16px; }
        .modal { background: #fff; width: 100%; max-width: 720px; border-radius: 10px; box-shadow: 0 10px 30px rgba(0,0,0,.25); overflow: hidden; }
        .modal header, .modal footer { padding: 12px 16px; background: #f8f8fb; }
        .modal header { font-weight: 600; }
        .modal .content { padding: 16px; }
        .grid { display:grid; grid-template-columns: repeat(2, 1fr); gap: var(--gap); }
        label { display:block; font-size: 12px; color:#444; margin-bottom: 4px; }
        input[type=text], input[type=tel] { width:100%; padding:10px; border:1px solid #ddd; border-radius: 8px; }
        .row { display:flex; gap: var(--gap); }
        .right { margin-left: auto; }
        .danger { background:#ffecec; color:#b40000; border: 1px solid #ffbcbc; }
        .hide { display:none; }
    </style>
</head>
<body>
<h1>Personas</h1>
<div class="toolbar">
    <button id="btnNew">Nueva persona</button>
    <span id="status" class="muted"></span>
</div>

<table id="tbl">
    <thead>
    <tr>
        <th style="width:140px">Clave</th>
        <th>Nombre</th>
        <th>Dirección</th>
        <th style="width:160px">Teléfono</th>
        <th style="width:160px">Acciones</th>
    </tr>
    </thead>
    <tbody id="tbody">
    <tr><td colspan="5"><em>Cargando…</em></td></tr>
    </tbody>
</table>

<!-- Modal Form -->
<div class="modal-backdrop" id="modalBackdrop" aria-hidden="true">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
        <header id="modalTitle">Persona</header>
        <div class="content">
            <form id="formPerson">
                <div class="grid">
                    <div>
                        <label>Clave</label>
                        <input type="text" id="clave" name="clave" required />
                    </div>
                    <div>
                        <label>Nombre</label>
                        <input type="text" id="nombre" name="nombre" required />
                    </div>
                    <div>
                        <label>Dirección</label>
                        <input type="text" id="direccion" name="direccion" required />
                    </div>
                    <div>
                        <label>Teléfono</label>
                        <input type="tel" id="telefono" name="telefono" required />
                    </div>
                </div>
            </form>
        </div>
        <footer class="row">
            <button id="btnCancel">Cancelar</button>
            <div class="right row">
                <button id="btnDelete" class="danger hide">Eliminar</button>
                <button id="btnSave">Guardar</button>
            </div>
        </footer>
    </div>
</div>

<script>
    // === Context-path dinámico ===
    const BASE = '<%= request.getContextPath() %>'; // ej: "", "/example_two_war_exploded", "/example_two"
    console.log("BASE =", BASE);

    const tbody = document.getElementById('tbody');
    const statusEl = document.getElementById('status');
    const modal = document.getElementById('modalBackdrop');

    const form = document.getElementById('formPerson');
    const inpClave = document.getElementById('clave');
    const inpNombre = document.getElementById('nombre');
    const inpDireccion = document.getElementById('direccion');
    const inpTelefono = document.getElementById('telefono');

    const btnNew = document.getElementById('btnNew');
    const btnSave = document.getElementById('btnSave');
    const btnDelete = document.getElementById('btnDelete');
    const btnCancel = document.getElementById('btnCancel');
    const modalTitle = document.getElementById('modalTitle');

    let editing = false;
    let originalClave = null;

    function setStatus(msg) { statusEl.textContent = msg || ''; }

    function openModal(mode, record = null) {
        editing = (mode === 'edit');
        btnDelete.classList.toggle('hide', !editing);
        modal.style.display = 'flex';
        modal.setAttribute('aria-hidden', 'false');

        if (editing && record) {
            modalTitle.textContent = 'Editar persona';
            inpClave.value = record.clave || '';
            inpNombre.value = record.nombre || '';
            inpDireccion.value = record.direccion || '';
            inpTelefono.value = record.telefono || '';
            originalClave = record.clave || '';
            inpClave.readOnly = true;
        } else {
            modalTitle.textContent = 'Nueva persona';
            form.reset();
            originalClave = null;
            inpClave.readOnly = false;
        }
        inpNombre.focus();
    }

    function closeModal() {
        modal.style.display = 'none';
        modal.setAttribute('aria-hidden', 'true');
    }

    async function loadList() {
        setStatus('Cargando…');
        try {
            const res = await fetch(`${BASE}/crud?action=list`);
            const data = await res.json();
            renderTable(Array.isArray(data) ? data : []);
            setStatus('');
        } catch (e) {
            console.error(e);
            tbody.innerHTML = `<tr><td colspan="5">Error al cargar</td></tr>`;
            setStatus('Error al cargar');
        }
    }

    function renderTable(rows) {
        if (!rows.length) {
            tbody.innerHTML = `<tr><td colspan="5"><em>Sin registros</em></td></tr>`;
            return;
        }
        tbody.innerHTML = '';
        for (const p of rows) {
            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + escapeHtml(p.clave ?? '') + '</td>' +
                '<td>' + escapeHtml(p.nombre ?? '') + '</td>' +
                '<td>' + escapeHtml(p.direccion ?? '') + '</td>' +
                '<td>' + escapeHtml(p.telefono ?? '') + '</td>' +
                '<td class="actions">' +
                '<button data-action="edit">Editar</button>' +
                '<button data-action="delete" class="danger">Eliminar</button>' +
                '</td>';

            tr.addEventListener('click', (ev) => {
                if (ev.target && ev.target.dataset && (ev.target.dataset.action === 'edit' || ev.target.dataset.action === 'delete')) {
                    return;
                }
                openModal('edit', p);
            });

            tr.querySelector('button[data-action="edit"]').addEventListener('click', (ev) => {
                ev.stopPropagation();
                openModal('edit', p);
            });

            tr.querySelector('button[data-action="delete"]').addEventListener('click', async (ev) => {
                ev.stopPropagation();
                await doDelete(p.clave);
            });

            tbody.appendChild(tr);
        }
    }

    function escapeHtml(s) {
        return String(s)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    async function doSave() {
        const payload = {
            action: editing ? "update" : "create",
            clave: document.getElementById("clave").value.trim(),
            nombre: document.getElementById("nombre").value.trim(),
            direccion: document.getElementById("direccion").value.trim(),
            telefono: document.getElementById("telefono").value.trim()
        };

        if (!payload.clave || !payload.nombre || !payload.direccion || !payload.telefono) {
            alert("Completa todos los campos.");
            return;
        }

        const body = new URLSearchParams(payload);

        console.log("POST →", `${BASE}/crud`, payload);

        const res = await fetch(`${BASE}/crud`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
            body
        });

        const data = await res.json().catch(() => null);
        console.log("HTTP", res.status, "JSON", data);

        if (!res.ok) {
            alert(`Error (${res.status}): ${data.error || "Fallo en servidor"}`);
            return;
        }

        closeModal();
        await loadList();
        setStatus(editing ? "Actualizado" : "Creado");
    }

    async function doDelete(clave) {
        if (!clave) return;
        if (!confirm(`¿Eliminar la persona con clave "${clave}"?`)) return;

        setStatus('Eliminando…');
        try {
            const body = new URLSearchParams({ action: 'delete', clave });
            const res = await fetch(`${BASE}/crud`, {
                method: 'POST',
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body
            });
            const data = await res.json().catch(() => null);
            console.log("DELETE HTTP", res.status, data);
            if (!res.ok) throw new Error('HTTP ' + res.status);
            await loadList();
            setStatus('Listo');
        } catch (e) {
            console.error(e);
            setStatus('Error al eliminar');
            alert('Ocurrió un error al eliminar.');
        }
    }

    btnNew.addEventListener('click', () => openModal('new'));
    btnCancel.addEventListener('click', () => closeModal());
    btnSave.addEventListener('click', (e) => { e.preventDefault(); doSave(); });
    btnDelete.addEventListener('click', async (e) => {
        e.preventDefault();
        if (!editing) return;
        await doDelete(originalClave);
        closeModal();
    });

    modal.addEventListener('click', (e) => { if (e.target === modal) closeModal(); });

    loadList();
</script>
</body>
</html>
