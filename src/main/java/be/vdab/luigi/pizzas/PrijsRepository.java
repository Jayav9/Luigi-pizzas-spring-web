package be.vdab.luigi.pizzas;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    List<Prijs> findByPizzaId(long pizzaId) {
        var sql = """ 
                select pizzaId, prijs, vanaf
                from prijzen
                where pizzaId = ?
                order by vanaf
                """;
        return jdbcClient.sql(sql)
                .param(pizzaId)
                .query(Prijs.class)
                .list();
    }

}
