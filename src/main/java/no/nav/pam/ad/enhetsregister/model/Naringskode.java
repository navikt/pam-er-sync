package no.nav.pam.ad.enhetsregister.model;

import org.jspecify.annotations.Nullable;

public record Naringskode(@Nullable String kode,
                          @Nullable String beskrivelse) {

}
