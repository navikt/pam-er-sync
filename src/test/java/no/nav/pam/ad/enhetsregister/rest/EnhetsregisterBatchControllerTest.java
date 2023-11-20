package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.enhetsregister.batch.TestConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.IndexClient;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestConfig.class)
public class EnhetsregisterBatchControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IndexClient client;

    @BeforeEach
    public void before() {
        assertEquals(client.getClass(), TestConfig.IndexClient.class);
    }

    @AfterEach
    public void after() {
        ((TestConfig.IndexClient) client).getStorage().clear();
    }

    @Test
    public void triggerDownloadOfHovedenheterAndGetNotFound() {

        given().port(port)
                .post("/internal/enhetsregister/sync/hovedenheter")
                .then()
                .assertThat()
                .statusCode(404); // Should be disabled in test configuration, so this is what we want.

    }

    @Test
    public void triggerDownloadOfUnderenheterAndProcessBatchJob() {

        given().port(port)
                .post("/internal/enhetsregister/sync/underenheter")
                .then()
                .assertThat()
                .statusCode(200);

        Map<String, List<Enhet>> index = ((TestConfig.IndexClient) client).getStorage();
        assertEquals(index.keySet().size(), 1);

        List<Enhet> entry = index.entrySet().iterator().next().getValue();
        assertTrue(index.keySet().iterator().next().startsWith("UNDER"));
        assertEquals(entry.size(), 6);

        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(entry.get(0).getOrganisasjonsnummer()).isEqualTo("914541662");
        softAssert.assertThat(entry.get(0).getNavn()).isEqualTo("STANETA LOGISTICS AND SERVICE STANELY OKOROAFOR");
        softAssert.assertThat(entry.get(0).getOrganisasjonsform()).isEqualTo("BEDR");
        softAssert.assertThat(entry.get(0).getAntallAnsatte()).isEqualTo(0);
        softAssert.assertThat(entry.get(0).getOverordnetEnhet()).isEqualTo("914514444");
        softAssert.assertThat(entry.get(0).getAdresse()).isNotNull();
        softAssert.assertThat(entry.get(0).getAdresse().getAdresse()).isEqualTo("Ognagata 1");
        softAssert.assertThat(entry.get(0).getAdresse().getPostnummer()).isEqualTo("4014");
        softAssert.assertThat(entry.get(0).getAdresse().getPoststed()).isEqualTo("STAVANGER");
        softAssert.assertThat(entry.get(0).getAdresse().getKommune()).isEqualTo("STAVANGER");
        softAssert.assertThat(entry.get(0).getAdresse().getLandkode()).isEqualTo("NO");
        softAssert.assertThat(entry.get(0).getAdresse().getLand()).isEqualTo("Norge");
        softAssert.assertThat(entry.get(0).getNaringskoder().size()).isEqualTo(1);
        softAssert.assertThat(entry.get(0).getNaringskoder().get(0).getKode()).isEqualTo("53.200");
        softAssert.assertThat(entry.get(0).getNaringskoder().get(0).getBeskrivelse()).isEqualTo("Andre post- og budtjenester");
        softAssert.assertAll();
    }

}
