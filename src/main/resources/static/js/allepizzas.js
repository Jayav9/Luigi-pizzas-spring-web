"use strict";
/*import {byId, toon} from "./util.js";*/
import {byId, toon, verberg} from "./util.js";

const response = await fetch("pizzas");
if (response.ok) {
    const pizzas = await response.json();
    const pizzasBody = byId("pizzasBody");
    for (const pizza of pizzas) {
        const tr = pizzasBody.insertRow();
        tr.insertCell().textContent = pizza.id;
        tr.insertCell().textContent = pizza.naam;
        tr.insertCell().textContent = pizza.prijs;
        const td = tr.insertCell();
        const button = document.createElement("button");
        td.appendChild(button);
        button.textContent = "verwijder";
        button.onclick = async () => {
            const response = await fetch(`pizzas/${pizza.id}`, {method: "DELETE"});
            if (response.ok) {
                verberg("storing");
                tr.remove();
            } else {
                toon("storing");
            }
        };
    }
} else {
    toon("storing");
}