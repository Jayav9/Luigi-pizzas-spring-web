package be.vdab.luigi.pizzas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
public class PizzaController {
    private final PizzaService pizzaService;
    PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }
    @GetMapping("pizzas/aantal")
    long findAantal() {
        return pizzaService.findAantal();
    }

    /* @GetMapping("pizzas/{id}")
    Optional<Pizza> findById(@PathVariable long id) {
        return pizzaService.findById(id);
         }*/

  /*  @GetMapping("pizzas/{id}")
    Pizza findById(@PathVariable long id) {
        return pizzaService.findById(id)
                .orElseThrow(() -> new PizzaNietGevondenException(id));

    }*/

    @GetMapping("pizzas/{id}")
    IdNaamPrijs findById(@PathVariable long id) {
        return pizzaService.findById(id)
                .map(pizza-> new IdNaamPrijs(pizza))
    .orElseThrow(() -> new PizzaNietGevondenException(id));
    }

    private record IdNaamPrijs(long id, String naam, BigDecimal prijs) {
        IdNaamPrijs(Pizza pizza) {
            this(pizza.getId(), pizza.getNaam(), pizza.getPrijs());
        }
    }
    @GetMapping("pizzas")
    Stream<IdNaamPrijs> findAll() {
        return pizzaService.findAll()
    .stream()
    .map(pizza -> new IdNaamPrijs(pizza));
    }

}
