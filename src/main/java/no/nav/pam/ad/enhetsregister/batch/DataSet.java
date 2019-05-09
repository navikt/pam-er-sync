package no.nav.pam.ad.enhetsregister.batch;

public enum DataSet {

    HOVEDENHET(
            new int[]{0, 1, 4, 11, 14, 15, 16, 17, 18, 19, 27, 28, 29, 30, 31, 32, 33},
            new String[]{
                    "organisasjonsnummer",
                    "navn",
                    "organisasjonsform",
                    "antallAnsatte",
                    "naeringskode1_kode",
                    "naeringskode1_beskrivelse",
                    "naeringskode2_kode",
                    "naeringskode2_beskrivelse",
                    "naeringskode3_kode",
                    "naeringskode3_beskrivelse",
                    "adresse",
                    "postnummer",
                    "poststed",
                    "kommunenummer",
                    "kommune",
                    "landkode",
                    "land"
            }
    ),

    UNDERENHET(
            new int[]{0, 1, 2, 3, 9, 11, 12, 13, 14, 15, 16, 24, 25, 26, 27, 28, 29, 30},
            new String[]{
                    "organisasjonsnummer",
                    "navn",
                    "overordnetEnhet",
                    "organisasjonsform",
                    "antallAnsatte",
                    "naeringskode1_kode",
                    "naeringskode1_beskrivelse",
                    "naeringskode2_kode",
                    "naeringskode2_beskrivelse",
                    "naeringskode3_kode",
                    "naeringskode3_beskrivelse",
                    "adresse",
                    "postnummer",
                    "poststed",
                    "kommunenummer",
                    "kommune",
                    "landkode",
                    "land"
            }
    );

    private final int[] includedFields;
    private final String[] fieldNames;

    DataSet(int[] includedFields, String[] fieldNames) {
        this.includedFields = includedFields;
        this.fieldNames = fieldNames;
    }

    public int[] getIncludedFields() {
        return includedFields;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

}
