package be.vdab.luigi.pizzas;

import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

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
    private final static String PRIJZEN_TABLE = "prijzen";

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
                                "naam = 'test3' and id = " + nieuweId)).isOne(),nieuweId -> assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient,
                        PRIJZEN_TABLE, "prijs = 0.01 and pizzaId = " + nieuweId)).isOne());

    }

    @Test
    void createMultipleVoegtAllePizzasToe() {
        var response = mockMvcTester.post().uri("/pizzasMultiple")
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

    @ParameterizedTest
    @ValueSource(strings = {"pizzaZonderNaam.json", "pizzaMetLegeNaam.json",
            "pizzaZonderPrijs.json", "pizzaMetNegatievePrijs.json"})
    void createMetVerkeerdeDataMislukt(String bestandsNaam) throws Exception {
        var jsonData = new ClassPathResource(bestandsNaam)
                .getContentAsString(StandardCharsets.UTF_8);
        var response = mockMvcTester.post()
                .uri("/pizzas")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(jsonData);
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);

    }

    @Test
    void putWijzigtPrijsEnVoegtPrijsToe(){
        var id = idVanTest1Pizza();
        var response = mockMvcTester.put().uri("/pizzas/{id}/prijs", id)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("7.7");
        assertThat(response).hasStatusOk();
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, PIZZAS_TABLE,
                "prijs = 7.7 and id = " + id)).isOne();
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, PRIJZEN_TABLE,
                "prijs = 7.7 and pizzaId = " + id)).isOne();
    }
    @Test
    void putVanOnbestaandePizzaMislukt(){
        var response = mockMvcTester.put().uri("/pizzas/{id}/prijs", Long.MAX_VALUE)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content("7.7");
        assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
    }
    @ParameterizedTest
    @ValueSource(strings = {"", "-7"})
    void putMetVerkeerdePrijsMislukt(String verkeerdePrijs) {
        var id = idVanTest1Pizza();
        var response = mockMvcTester.put().uri("/pizzas/{id}/prijs", id)
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(verkeerdePrijs);
        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void eenPizzaToevoegenDieAlBestaatMislukt() throws Exception {
        var jsonData = new ClassPathResource("pizzaDieAlBestaat.json")
                .getContentAsString(StandardCharsets.UTF_8);
        var response = mockMvcTester.post()
                .uri("/pizzas")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(jsonData);
        assertThat(response).hasStatus(HttpStatus.CONFLICT);
    }
}
