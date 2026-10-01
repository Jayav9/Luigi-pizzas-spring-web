"use strict";
import {byId, toon, verberg} from "./util.js";
byId("zoek").onclick = async () => {
    verberg("pizzasTable", "woordFout", "storing");
    const woordInput = byId("woord");
    if (woordInput.checkValidity()) {
        findByWoord(woordInput.value);
    } else {
        toon("woordFout");
        woordInput.focus();
    }
};
async function findByWoord(woord) {
    const response = await fetch(`pizzas?naamBevat=${woord}`);
    if (response.ok) {
        const pizzas = await response.json();
        toon("pizzasTable");
        const pizzasBody = byId("pizzasBody");
        pizzasBody.innerHTML = "";
        for (const pizza of pizzas) {
            const tr = pizzasBody.insertRow();
            tr.insertCell().textContent = pizza.id;
            tr.insertCell().textContent = pizza.naam;
            tr.insertCell().textContent = pizza.prijs;
        }
    } else {
        toon("storing");
    }
}