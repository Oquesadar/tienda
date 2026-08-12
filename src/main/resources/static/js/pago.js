// Variable global o simplemente definida aquí.
// El valor será inyectado por Thymeleaf en el HTML, no aquí.

function iniciarPagoPayPal(montoUSD) { // <-- Ahora acepta el monto como argumento    
    // 1. Usar el monto pasado como argumento    
    const endpoint = '/paypal/facturar?usdAmount=' + montoUSD;

// --- PASO CLAVE: OBTENER EL TOKEN CSRF ---
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch(endpoint, {
        method: 'POST',
        headers: {'Accept': 'application/json',
            [header]: token
        }
    })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Error HTTP: ${response.status}');
                    });
                }
                return response.json();
            })
            .then(data => {
                const approvalUrl = data.approvalLink;

                if (approvalUrl) {
                    console.log("Orden creada. Redirigiendo a PayPal.");
                    window.location.href = approvalUrl;
                } else {
                    alert("Error: No se encontró el enlace de aprobación válido.");
                }
            })
            .catch(error => {
                console.error('Fallo en el proceso de pago:', error);
                alert('Ocurrió un error al iniciar el pago: ' + error.message);
            });
}