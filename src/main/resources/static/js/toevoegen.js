"use strict";
import {byId, toon, verberg} from "./util.js";
byId("toevoegen").onclick = async () => {
    verberg("naamFout", "prijsFout", "storing");
    const naamInput = byId("naam");
    if (! naamInput.checkValidity()) {
        toon("naamFout");
    }
    const prijsInput = byId("prijs");
    if (! prijsInput.checkValidity()) {
        toon("prijsFout");
    }
    const aantalFouten = document.querySelectorAll(".fout:not([hidden])").length;
    if (aantalFouten === 0) {
        // JavaScript object maken dat een pizza met naam en prijs voorstelt:
        const pizza = {
            naam: naamInput.value,
            prijs: Number(prijsInput.value)
        };
        voegToe(pizza);
    }
};

async function voegToe(pizza) {
    const response = await fetch("pizzas",
        {method: "POST",
            headers: {'Content-Type': "application/json"},
            body: JSON.stringify(pizza)
        });
    if (response.ok) {
        window.location = "allepizzas.html";
    } else {
        toon("storing");
    }
}