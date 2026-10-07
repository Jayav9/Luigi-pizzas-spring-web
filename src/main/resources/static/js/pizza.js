"use strict";
import {byId, toon, verberg, setText} from "./util.js";
byId("zoek").onclick = async () => {
  // verberg("pizza", "zoekIdFout", "nietGevonden", "storing");
    verberg("pizza", "zoekIdFout", "nietGevonden", "storing", "nieuwePrijsFout");
    const zoekIdInput = byId("zoekId");
    if (zoekIdInput.checkValidity()) {
        findById(zoekIdInput.value);
    } else {
        toon("zoekIdFout");
        zoekIdInput.focus();
    }
};
async function findById(id) {
    const response = await fetch(`pizzas/${id}`);
    if (response.ok) {
        const pizza = await response.json();
        toon("pizza");
        setText("naam", pizza.naam);
        setText("prijs", pizza.prijs);
    } else {
        if (response.status === 404) {
            toon("nietGevonden");
        } else {
            toon("storing");
        }
    }
}
byId("bewaar").onclick = async () => {
    const nieuwePrijsInput = byId("nieuwePrijs");
    if (nieuwePrijsInput.checkValidity()) {
        verberg("nieuwePrijsFout");
        updatePrijs(Number(nieuwePrijsInput.value));
    } else {
        toon("nieuwePrijsFout");
        nieuwePrijsInput.focus();
    }
};
async function updatePrijs(nieuwePrijs) {
    const response = await fetch(`pizzas/${byId("zoekId").value}/prijs`,
        {
            method: "PUT",
            headers: {'Content-Type': "application/json"},
            body: JSON.stringify(nieuwePrijs)
        });
    if (response.ok) {
        setText("prijs", nieuwePrijs);
    } else {
        toon("storing");
    }
}
byId("prijzen").onclick = () => {
    const idEnNaam = {
        id: Number(byId("zoekId").value),
        naam: byId("naam").textContent
    };
    sessionStorage.setItem("idEnNaam", JSON.stringify(idEnNaam));
};