package be.vdab.luigi.pizzas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("pizzas")
public class PizzaController {
    private final PizzaService pizzaService;
    PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping("aantal")
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

    @GetMapping("{id}")
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

    @GetMapping
    Stream<IdNaamPrijs> findAll() {
        return pizzaService.findAll()
    .stream()
    .map(pizza -> new IdNaamPrijs(pizza));
    }

    @GetMapping(params = "naamBevat")
    Stream<IdNaamPrijs> findByNaamBevat(String naamBevat) {
        return pizzaService.findByNaamBevat(naamBevat)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @GetMapping( params = {"vanPrijs", "totPrijs"})
    Stream<IdNaamPrijs> findByPrijsTussen(BigDecimal vanPrijs,BigDecimal totPrijs){
        return pizzaService.findByPrijsTussen(vanPrijs, totPrijs)
                .stream()
                .map(pizza -> new IdNaamPrijs(pizza));
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        pizzaService.delete(id);
    }

    @PostMapping
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

    @PutMapping("{id}/prijs")
    void updatePrijs(@PathVariable long id,
                     @RequestBody @NotNull @PositiveOrZero BigDecimal nieuwePrijs) {
        var prijs = new Prijs(id, nieuwePrijs, LocalDateTime.now());
        pizzaService.updatePrijs(prijs);
    }
    private record PrijsVanaf(BigDecimal prijs, LocalDateTime vanaf) {
        PrijsVanaf(Prijs prijs) {
            this(prijs.getPrijs(), prijs.getVanaf());
        }
    }

    @GetMapping("{id}/prijzen")
    Stream<PrijsVanaf> findPrijzen(@PathVariable long id) {
        return pizzaService.findPrijzen(id)
                .stream()
                .map(prijs -> new PrijsVanaf(prijs));
    }


}
