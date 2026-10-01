"use strict";
import {byId, toon} from "./util.js";
const response = await fetch("pizzas");
if (response.ok) {
    const pizzas = await response.json();
    const pizzasBody = byId("pizzasBody");
    for (const pizza of pizzas) {
        const tr = pizzasBody.insertRow();
        tr.insertCell().textContent = pizza.id;
        tr.insertCell().textContent = pizza.naam;
        tr.insertCell().textContent = pizza.prijs;
    }
} else {
    toon("storing");
}