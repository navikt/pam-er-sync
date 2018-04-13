package no.nav.pam.ad.enhetsregister.batch;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = {BatchJobTest.BatchTestConfig.class})
public class BatchJobTest {

//    @Configuration
//    @EnableBatchProcessing
//    @Import({BatchConfig.class, AppConfig.class})
//    static class BatchTestConfig {
//        @Bean
//        JobLauncherTestUtils jobLauncherTestUtils() {
//            return new JobLauncherTestUtils();
//        }
//    }
//
//    private static final String FILEPATH = "src/test/resources/enhetsregisteret.samples/";
//
//    @Autowired
//    private JobLauncherTestUtils jobLauncherTestUtils;
//
//    @Autowired
//    private CompanyRepository companyRepository;
//
//    @Before
//    public void before() {
//        //TODO: fix with transactions https://jira.adeo.no/browse/PAM-349
//        companyRepository.deleteAll();
//        assertEquals(0, companyRepository.count());
//    }
//
//    @Test
//    public void launchJob1() throws Exception {
//        Map<String, JobParameter> params = new HashMap<>();
//        params.put("type", new JobParameter(CsvProperties.EnhetType.HOVEDENHET.toString()));
//        params.put("filename", new JobParameter(FILEPATH + "hovedenheter.csv"));
//
//        //testing a job
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters(params));
//        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
//
//        assertEquals(6, companyRepository.count());
//
//        // Verify mapping is correct
//        Company company = companyRepository.findByOrgnr("976993888").orElse(null);
//        assertNotNull(company);
//
//        SoftAssertions softAssert = new SoftAssertions();
//        softAssert.assertThat(company.getName()).isEqualTo("FINNØY KYRKJELEGE FELLESRÅD");
//        softAssert.assertThat(company.getOrgform()).isEqualTo("KIRK");
//        softAssert.assertThat(company.getParentOrgnr()).isNull();
//        softAssert.assertThat(company.getLocation().get().getAddress()).isEqualTo("Hagatunet 4");
//        softAssert.assertThat(company.getLocation().get().getPostalCode()).isEqualTo("4160");
//        softAssert.assertThat(company.getLocation().get().getCity()).isEqualTo("FINNØY");
//        softAssert.assertThat(company.getLocation().get().getMunicipal()).isEqualTo("FINNØY");
//        softAssert.assertAll();
//    }
//
//    @Test
//    public void launchJob2() throws Exception {
//        Map<String, JobParameter> params = new HashMap<>();
//        params.put("type", new JobParameter(CsvProperties.EnhetType.UNDERENHET.toString()));
//        params.put("filename", new JobParameter(FILEPATH + "underenheter.csv"));
//
//        //testing a job
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters(params));
//        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
//
//        assertEquals(5, companyRepository.count());
//
//        // Verify mapping is correct
//        Company company = companyRepository.findByOrgnr("917297193").orElse(null);
//        assertNotNull(company);
//
//        SoftAssertions softAssert = new SoftAssertions();
//        softAssert.assertThat(company.getName()).isEqualTo("NG KIWI NORD AS AVD 169 HESSENG");
//        softAssert.assertThat(company.getOrgform()).isEqualTo("BEDR");
//        softAssert.assertThat(company.getParentOrgnr()).isEqualTo("990648492");
//        softAssert.assertThat(company.getLocation().get().getAddress()).isEqualTo("Hessengveien 2");
//        softAssert.assertThat(company.getLocation().get().getPostalCode()).isEqualTo("9912");
//        softAssert.assertThat(company.getLocation().get().getCity()).isEqualTo("HESSENG");
//        softAssert.assertThat(company.getLocation().get().getMunicipal()).isEqualTo("SØR-VARANGER");
//        softAssert.assertAll();
//    }
}



