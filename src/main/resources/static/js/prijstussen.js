"use strict";
import {byId, toon, verberg} from "./util.js";
byId("zoek").onclick = async () => {
    verberg("pizzasTable", "vanFout", "totFout", "storing");
    const vanInput = byId("van");
    if (! vanInput.checkValidity()) {
        toon("vanFout");
    }
    const totInput = byId("tot");
    if (! totInput.checkValidity()) {
        toon("totFout");
    }
    const aantalFouten = document.querySelectorAll(".fout:not([hidden])").length;
    if (aantalFouten === 0) {
        findByPrijsBetween(vanInput.value, totInput.value);
    }
};
async function findByPrijsBetween(van, tot) {
    const response = await fetch(`pizzas?vanPrijs=${van}&totPrijs=${tot}`);
    if (response.ok) {
        const pizzas = await response.json();
        toon("pizzasTable");
        const pizzasBody = byId("pizzasBody");
        pizzasBody.innerHTML = "";
        for (const pizza of pizzas) {
            const tr = pizzasBody.insertRow();
            tr.insertCell().textContent = pizza.id;
            tr.insertCell().textContent = pizza.naam
            tr.insertCell().textContent = pizza.prijs
        }
    } else {
        toon("storing");
    }
}