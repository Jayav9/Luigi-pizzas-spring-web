package be.vdab.luigi.pizzas;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class PrijsRepository {

    private final JdbcClient jdbcClient;
    // constructor met parameter (dependency injection)


    public PrijsRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    void create(Prijs prijs) {
        var sql = """ 
      insert into prijzen(pizzaId, prijs, vanaf)
      values (?, ?, ?);
      """;
        jdbcClient.sql(sql)
                .params(prijs.getPizzaId(), prijs.getPrijs(), prijs.getVanaf())
                .update();
    }
}
