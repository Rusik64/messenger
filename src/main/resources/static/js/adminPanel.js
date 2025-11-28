async function loadReports() {
        const resp = await fetch('/reports', {method: 'POST'});
        const reports = await resp.json();
        const container = document.getElementById('reports-list');
        container.innerHTML = '';

        if (reports.length === 0) {
            container.innerHTML = '<p class="text-muted">Нет новых жалоб</p>';
            return;
        }

        reports.forEach(r => {
            const a = document.createElement('a');
            a.href = `/report/${r.id}`;
            a.classList.add('list-group-item', 'list-group-item-action');
            a.textContent = `Жалоба на пользователя ${r.reportedUserName}: "${r.messageContent}"`;
            container.appendChild(a);
        });
    }

    document.addEventListener('DOMContentLoaded', loadReports);