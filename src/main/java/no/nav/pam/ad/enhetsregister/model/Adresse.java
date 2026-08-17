package no.nav.pam.ad.enhetsregister.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Adresse(@Nullable String adresse,
                      @Nullable String postnummer,
                      @Nullable String poststed,
                      @Nullable String kommunenummer,
                      @Nullable String kommune,
                      @Nullable String landkode,
                      @Nullable String land) {

}
