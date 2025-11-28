document.getElementById('acceptBtn').addEventListener('click', async () => {
    const resp = await fetch(`/accept-report?reportId=${reportId}`, {method: 'POST'});
    if(resp.ok) {
        alert('Пользователь заблокирован');
        window.location.href = '/admin';
    }
});

document.getElementById('deleteBtn').addEventListener('click', async () => {
    const resp = await fetch('/delete-report', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `reportId=${reportId}`
    });
    if(resp.ok) {
        alert('Жалоба удалена');
        window.location.href = '/admin';
    }
});