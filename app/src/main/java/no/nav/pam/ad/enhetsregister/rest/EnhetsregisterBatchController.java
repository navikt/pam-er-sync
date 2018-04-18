package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.enhetsregister.batch.CsvProperties;
import no.nav.pam.ad.enhetsregister.batch.JobLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/enhetsregister")
public class EnhetsregisterBatchController {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetsregisterBatchController.class);
    private static final String HOVEDENHETER_FILENAME = "hovedenheter.csv";
    private static final String UNDERENHETER_FILENAME = "underenheter.csv";

    @Autowired
    private JobLauncherService jobLauncherService;

    @PostMapping("/sync/hovedenheter")
    public ResponseEntity syncHovedenheter() {
        LOG.debug("Start Syncing hovedenheter");

        try {
            jobLauncherService.syncFromFiles(CsvProperties.EnhetType.HOVEDENHET, HOVEDENHETER_FILENAME);
            return ResponseEntity.ok("Enhetene er importert");
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/sync/underenheter")
    public ResponseEntity syncUnderenheter() {
        LOG.debug("Start Syncing underenheter");

        try {
            JobExecution jobExecution = jobLauncherService.syncFromFiles(CsvProperties.EnhetType.UNDERENHET, UNDERENHETER_FILENAME);

            if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
                return ResponseEntity.ok("Enhetene er importert");
            } else {
                return ResponseEntity.status(500).build();
            }
        } catch (JobExecutionException e) {
            LOG.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

}
