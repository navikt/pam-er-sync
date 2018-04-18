package no.nav.pam.ad.enhetsregister.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Configuration
@Profile("test")
public class WiremockConfig {

    @Bean
    public WireMockServer wireMockServer() {

        WireMockServer wireMockServer = new WireMockServer(2219);

        wireMockServer.stubFor(post("/_bulk?timeout=1m").willReturn(aResponse().withStatus(200)));

        wireMockServer.stubFor(get(urlMatching(
                ".*?/company/feed"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMockResponse("initResultat"))));

        wireMockServer.start();
        System.out.println("wiremockserver startet");
        return wireMockServer;
    }

    private String getMockResponse(String fil) {

        return "OK";

//        return new BufferedReader(
//                new InputStreamReader(WiremockConfig.class.getClassLoader()
//                        .getResourceAsStream("companyfeed.io.samples/" + fil + ".json")))
//                .lines().collect(Collectors.joining("\n"));
    }

}

