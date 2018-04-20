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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/enhetsregister")
public class EnhetsregisterBatchController {

    private static final Logger LOG = LoggerFactory.getLogger(EnhetsregisterBatchController.class);

    @Autowired
    private JobLauncherService jobLauncherService;

    @PostMapping("/sync/underenheter")
    public ResponseEntity syncUnderenheter(@RequestParam(name = "filename") String filename) {
        LOG.debug("Start Syncing underenheter");

        try {
            JobExecution jobExecution = jobLauncherService.syncFromFiles(CsvProperties.EnhetType.UNDERENHET, filename);

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
