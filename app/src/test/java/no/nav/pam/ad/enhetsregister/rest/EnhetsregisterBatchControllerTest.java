package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.TestConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.es.IndexerService;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestConfig.class)
public class EnhetsregisterBatchControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private IndexerService service;

    @Before
    public void before() {
        assertThat(service, instanceOf(TestConfig.IndexerService.class));
    }

    @Test
    public void triggerDownloadOfUnderenheterAndProcessBatchJob() {

        given().port(port)
                .post("/enhetsregister/sync/underenheter")
                .then()
                .assertThat()
                .statusCode(200);

        Map<String, List<Enhet>> index = ((TestConfig.IndexerService) service).getIndex();
        assertThat(index.keySet(), hasSize(1));
        List<Enhet> entry = index.entrySet().iterator().next().getValue();
        assertThat(entry, hasSize(5));

    }

}
