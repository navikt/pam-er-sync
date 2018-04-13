package no.nav.pam.ad.enhetsregister.batch;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.batch.model.JsonEnhet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JsonDummyWriter implements ItemWriter<JsonEnhet> {

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void write(List<? extends JsonEnhet> list) throws Exception {

        for(JsonEnhet e: list ){
            System.out.println(objectMapper.writeValueAsString(e));
        }
    }
}
