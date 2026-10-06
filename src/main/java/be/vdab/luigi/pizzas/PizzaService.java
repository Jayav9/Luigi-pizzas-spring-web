package be.vdab.luigi.pizzas;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PizzaService {
    private PizzaRepository pizzaRepository;
    private final PrijsRepository prijsRepository;

  /* before put action
   public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }*/

    public PizzaService(PrijsRepository prijsRepository, PizzaRepository pizzaRepository) {
        this.prijsRepository = prijsRepository;
        this.pizzaRepository = pizzaRepository;
    }

    public long findAantal(){
       return  pizzaRepository.findAantal();
    }

    Optional<Pizza> findById(long id) {
        return pizzaRepository.findById(id);
    }

    List<Pizza> findAll() {
        return pizzaRepository.findAll();
    }

    List<Pizza> findByNaamBevat(String woord) {
        return pizzaRepository.findByNaamBevat(woord);
    }
    List<Pizza> findByPrijsTussen(BigDecimal van, BigDecimal tot) {
        return pizzaRepository.findByPrijsTussen(van, tot);
    }
    @Transactional
    void delete(long id) {
        pizzaRepository.delete(id);
    }

    @Transactional
    long create(NieuwePizza nieuwePizza) {
        try {
            var winst = nieuwePizza.prijs().multiply(BigDecimal.valueOf(0.1));
            var pizza = new Pizza(0, nieuwePizza.naam(), nieuwePizza.prijs(), winst);
            // return pizzaRepository.create(pizza);
            var id = pizzaRepository.create(pizza);
            prijsRepository.create(new Prijs(id, pizza.getPrijs(), LocalDateTime.now()));
            return id;
        } catch (DuplicateKeyException ex) {
            throw new PizzaBestaatAlException(nieuwePizza.naam());
        }
    }
    // How to POST multiple inputs at the same time from backend and reuse the create method above
    @Transactional
    List<Long> createAll(List<NieuwePizza> nieuwePizzas) {
        return nieuwePizzas.stream()
                .map(this::create)
                .toList();
    }

    @Transactional
    void updatePrijs(Prijs prijs) {
        pizzaRepository.updatePrijs(prijs.getPizzaId(), prijs.getPrijs());
        prijsRepository.create(prijs);
    }
}
