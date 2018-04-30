package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.TestConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.IndexClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestConfig.class)
public class EnhetsregisterBatchControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IndexClient client;

    @Before
    public void before() {
        assertThat(client, instanceOf(TestConfig.IndexClient.class));
    }

    @After
    public void after() {
        ((TestConfig.IndexClient) client).getStorage().clear();
    }

    @Test
    public void triggetDownloadOfHovedenheterAndProcessBatchJob() {

        given().port(port)
                .post("/enhetsregister/sync/hovedenheter")
                .then()
                .assertThat()
                .statusCode(200);

        Map<String, List<Enhet>> index = ((TestConfig.IndexClient) client).getStorage();
        assertThat(index.keySet(), hasSize(1));
        assertThat(index.keySet().iterator().next(), startsWith("HOVED"));

        List<Enhet> entry = index.entrySet().iterator().next().getValue();
        assertThat(entry, hasSize(6));
        assertThat(entry.get(0).getOrganisasjonsnummer(), equalTo("976993888"));
        assertThat(entry.get(0).getNavn(), equalTo("FINNØY KYRKJELEGE FELLESRÅD"));
        assertThat(entry.get(0).getOrganisasjonsform(), equalTo("KIRK"));
        assertThat(entry.get(0).getAntallAnsatte(), equalTo(68));
        assertThat(entry.get(0).getOverordnetEnhet(), nullValue());
        assertThat(entry.get(0).getAdresse(), notNullValue());
        assertThat(entry.get(0).getAdresse().getAdresse(), equalTo("Hagatunet 4"));
        assertThat(entry.get(0).getAdresse().getPostnummer(), equalTo("4160"));
        assertThat(entry.get(0).getAdresse().getPoststed(), equalTo("FINNØY"));
        assertThat(entry.get(0).getAdresse().getKommunenummer(), equalTo("1141"));
        assertThat(entry.get(0).getAdresse().getKommune(), equalTo("FINNØY"));
        assertThat(entry.get(0).getAdresse().getLandkode(), equalTo("NO"));
        assertThat(entry.get(0).getAdresse().getLand(), equalTo("Norge"));
        assertThat(entry.get(0).getPostAdresse(), nullValue());
        assertThat(entry.get(0).getNaringskoder(), hasSize(1));
        assertThat(entry.get(0).getNaringskoder().get(0).getKode(), equalTo("94.910"));
        assertThat(entry.get(0).getNaringskoder().get(0).getBeskrivelse(), equalTo("Religiøse organisasjoner"));

    }

    @Test
    public void triggerDownloadOfUnderenheterAndProcessBatchJob() {

        given().port(port)
                .post("/enhetsregister/sync/underenheter")
                .then()
                .assertThat()
                .statusCode(200);

        Map<String, List<Enhet>> index = ((TestConfig.IndexClient) client).getStorage();
        assertThat(index.keySet(), hasSize(1));

        List<Enhet> entry = index.entrySet().iterator().next().getValue();
        assertThat(index.keySet().iterator().next(), startsWith("UNDER"));
        assertThat(entry, hasSize(5));
        assertThat(entry.get(0).getOrganisasjonsnummer(), equalTo("917297193"));
        assertThat(entry.get(0).getNavn(), equalTo("NG KIWI NORD AS AVD 169 HESSENG"));
        assertThat(entry.get(0).getOrganisasjonsform(), equalTo("BEDR"));
        assertThat(entry.get(0).getAntallAnsatte(), equalTo(14));
        assertThat(entry.get(0).getOverordnetEnhet(), equalTo("990648492"));
        assertThat(entry.get(0).getAdresse(), notNullValue());
        assertThat(entry.get(0).getAdresse().getAdresse(), equalTo("Hessengveien 2"));
        assertThat(entry.get(0).getAdresse().getPostnummer(), equalTo("9912"));
        assertThat(entry.get(0).getAdresse().getPoststed(), equalTo("HESSENG"));
        assertThat(entry.get(0).getAdresse().getKommunenummer(), equalTo("2030"));
        assertThat(entry.get(0).getAdresse().getKommune(), equalTo("SØR-VARANGER"));
        assertThat(entry.get(0).getAdresse().getLandkode(), equalTo("NO"));
        assertThat(entry.get(0).getAdresse().getLand(), equalTo("Norge"));
        assertThat(entry.get(0).getPostAdresse(), nullValue());
        assertThat(entry.get(0).getNaringskoder(), hasSize(1));
        assertThat(entry.get(0).getNaringskoder().get(0).getKode(), equalTo("47.111"));
        assertThat(entry.get(0).getNaringskoder().get(0).getBeskrivelse(), equalTo("Butikkhandel med bredt vareutvalg med hovedvekt på nærings- og nytelsesmidler"));

    }

}
