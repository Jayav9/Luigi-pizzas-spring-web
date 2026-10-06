package be.vdab.luigi.pizzas;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class PizzaBestaatAlException extends RuntimeException {
    PizzaBestaatAlException(String naam) {
        super("Een pizza bestaat al met volgende naam: " + naam);
    }
}