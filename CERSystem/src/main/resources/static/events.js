document.addEventListener('DOMContentLoaded', () => {
    const registerButtons = document.querySelectorAll('.register-btn:not([disabled])');

    registerButtons.forEach(button => {
        button.addEventListener('click', async () => {
            const eventId = button.getAttribute('data-event-id');
            if (!eventId) return;

            button.disabled = true;
            button.textContent = 'Registering...';

            try {
                const response = await fetch(`/events/register/${eventId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    credentials: 'same-origin'  // important: sends session cookie + CSRF
                });

                const data = await response.json();
                const messageDiv = document.getElementById('message');

                if (data.success) {
                    messageDiv.innerHTML = `<p class="success">${data.message}</p>`;
                    button.textContent = 'Registered!';
                    button.disabled = true;
                    button.style.backgroundColor = '#28a745';
                } else {
                    messageDiv.innerHTML = `<p class="error">${data.message}</p>`;
                    button.textContent = 'Register';
                    button.disabled = false;
                }
            } catch (err) {
                const messageDiv = document.getElementById('message');
                messageDiv.innerHTML = `<p class="error">Network error – please try again</p>`;
                button.textContent = 'Register';
                button.disabled = false;
                console.error(err);
            }
        });
    });
});