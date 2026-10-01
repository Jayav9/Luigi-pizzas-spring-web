package be.vdab.luigi.pizzas;

public class PizzaService {
    private PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    public long findAantal(){
       return  pizzaRepository.findAantal();
    }
}
