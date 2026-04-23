function EnviarCarrito(url, valores) {
    const options = {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=utf-8"
        },
        body: JSON.stringify(valores)
    };
    
    fetch(url, options)
        .then(response => response.text())
        .then(data => {
            document.body.innerHTML = data;
        })
        .catch(error => console.error(error));
}