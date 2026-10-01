package be.vdab.luigi.pizzas;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PizzaRepository {

    private final JdbcClient jdbcClient;

    public PizzaRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    long findAantal() {
        var sql = """ 
select count(*) as aantalPizzas
from pizzas
""";
        return jdbcClient.sql(sql)
                .query(Long.class)
                .single();
    }

    Optional<Pizza> findById(long id) {
        var sql = """ 
select id, naam, prijs, winst
from pizzas
where id = ?
""";
        return jdbcClient.sql(sql)
                .param(id)
                .query(Pizza.class)
                .optional();
    }

    List<Pizza> findAll() {
        var sql = """ 
          select id, naam, prijs, winst
          from pizzas
          order by naam;
          """;
        return jdbcClient.sql(sql)
                .query(Pizza.class)
                .list();
    }

}
