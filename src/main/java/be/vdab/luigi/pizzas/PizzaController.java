package be.vdab.luigi.pizzas;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
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

    @GetMapping(value = "pizzas", params = "naamBevat")
    Stream<IdNaamPrijs> findByNaamBevat(String naamBevat) {
        return pizzaService.findByNaamBevat(naamBevat)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @GetMapping(value = "pizzas", params = {"vanPrijs", "totPrijs"})
    Stream<IdNaamPrijs> findByPrijsTussen(BigDecimal vanPrijs,BigDecimal totPrijs){
        return pizzaService.findByPrijsTussen(vanPrijs, totPrijs)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @DeleteMapping("pizzas/{id}")
    void delete(@PathVariable long id) {
        pizzaService.delete(id);
    }

    @PostMapping("pizzas")
    long create(@RequestBody @Valid NieuwePizza nieuwePizza) {
        var id = pizzaService.create(nieuwePizza);
        return id;
    }


// How to POST multiple inputs at the same time from backend
    @PostMapping("pizzasMultiple")
    List<Long>  createMultiple(@RequestBody List<NieuwePizza> nieuwePizzas) {
   List<Long> ids = pizzaService.createAll(nieuwePizzas);
        return ids;
    }


}
