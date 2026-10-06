package be.vdab.luigi.pizzas;

import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql("/pizzas.sql")
@AutoConfigureMockMvc
class PizzaControllerTest {
    private final static String PIZZAS_TABLE = "pizzas";
    private final MockMvcTester mockMvcTester;
    private final JdbcClient jdbcClient;

    PizzaControllerTest(MockMvcTester mockMvcTester, JdbcClient jdbcClient) {
        this.mockMvcTester = mockMvcTester;
        this.jdbcClient = jdbcClient;
    }

    @Test
    void findAantalVindtHetJuisteAantalPizzas() {
        var response = mockMvcTester.get()
                .uri("/pizzas/aantal");
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$")
                .isEqualTo(JdbcTestUtils.countRowsInTable(jdbcClient, PIZZAS_TABLE));
    }

    private int idVanTest1Pizza() {
        return jdbcClient.sql("select id from pizzas where naam = 'test1'")
                .query(Integer.class)
                .single();
    }

    @Test
    void findByIdMetEenBestaandeIdVindtDePizza() {
        var id = idVanTest1Pizza();
        var response = mockMvcTester.get()
                .uri("/pizzas/{id}", id);
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                //  .extractingPath("id").isEqualTo(id);
                .satisfies(
                        json -> assertThat(json).extractingPath("id").isEqualTo(id),
                        json -> assertThat(json).extractingPath("naam").isEqualTo("test1"));
    }

    @Test
    void findAllVindtAllePizzas() {
        var response = mockMvcTester.get().uri("/pizzas");
        assertThat(response).hasStatusOk()
                .bodyJson()
                .extractingPath("length()")
                .isEqualTo(JdbcTestUtils.countRowsInTable(jdbcClient, PIZZAS_TABLE));
    }

    @Test
    void findByNaamBevatVindtDeJuistePizzas() {
        var response = mockMvcTester.get().uri("/pizzas")
                .queryParam("naamBevat", "test");
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("length()")
                .isEqualTo(JdbcTestUtils.countRowsInTableWhere(
                        jdbcClient, PIZZAS_TABLE, "naam like '%test%'"));
    }

    @Test
    void findByPrijsBetweenVindtDeJuistePizzas() {
        var response = mockMvcTester.get().uri("/pizzas")
                .queryParam("vanPrijs", "10")
                .queryParam("totPrijs", "20");
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("length()")
                .isEqualTo(JdbcTestUtils.countRowsInTableWhere(
                        jdbcClient, PIZZAS_TABLE, "prijs between 10 and 20"));
    }


    @Test
    void deleteVerwijdertDePizza() {
        var id = idVanTest1Pizza();
        var response = mockMvcTester
                .delete()
                .uri("/pizzas/{id}", id);
        assertThat(response).hasStatusOk();
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, PIZZAS_TABLE,
                "id = " + id)).isZero();
    }

    @Test
    void createVoegtDePizzaToe() throws Exception {
        var jsonData = new ClassPathResource("correctPizza.json")
                .getContentAsString(StandardCharsets.UTF_8);
        var response = mockMvcTester.post().uri("/pizzas")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                /*.content("""
                        { 
                        "naam": "test3", 
                        "prijs": 3 
                        } 
                        """
                );*/
                        .content(jsonData );
        assertThat(response)
                .hasStatusOk()
                .bodyJson().extractingPath("$").satisfies(nieuweId ->
                        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, PIZZAS_TABLE,
                                "naam = 'test3' and id = " + nieuweId)).isOne());

    }

    @Test
    void createMultipleVoegtAllePizzasToe() {
        var response = mockMvcTester.post().uri("/pizzas/multiple")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("""
                        [
                          {"naam": "eerste", "prijs": 1},
                          {"naam": "tweede", "prijs": 2}
                        ]
                        """);
        assertThat(response)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("length()")
                .isEqualTo(2);
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, PIZZAS_TABLE,
                "naam in ('eerste', 'tweede')")).isEqualTo(2);
    }
}
