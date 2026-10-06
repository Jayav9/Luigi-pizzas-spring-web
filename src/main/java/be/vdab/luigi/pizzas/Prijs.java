package be.vdab.luigi.pizzas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class Prijs {

    private final long pizzaId;
    private final BigDecimal prijs;
    private final LocalDateTime vanaf;

    Prijs(long pizzaId, BigDecimal prijs, LocalDateTime vanaf) {
        this.pizzaId = pizzaId;
        this.prijs = prijs;
        this.vanaf = vanaf;
    }

    public long getPizzaId() {
        return pizzaId;
    }

    public BigDecimal getPrijs() {
        return prijs;
    }

    public LocalDateTime getVanaf() {
        return vanaf;
    }
}
