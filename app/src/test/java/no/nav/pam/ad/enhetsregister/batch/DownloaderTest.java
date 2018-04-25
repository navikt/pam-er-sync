package no.nav.pam.ad.enhetsregister.batch;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@Ignore
public class DownloaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(DownloaderTest.class);
    private static final String UNDERENHETER_URL = "http://data.brreg.no/enhetsregisteret/download/underenheter";

    @Test
    public void downloadUnderenheter()
            throws Exception {

        try (Downloader downloader = new Downloader(UNDERENHETER_URL)) {
            assertThat(downloader.download().get(), instanceOf(File.class));
        }

    }

    @Test
    public void downloadNonExistingUrl()
            throws Exception {

        try (Downloader downloader = new Downloader("http://localhost/does/not/exist.file")) {
            assertThat(downloader.download().get(), nullValue());
        }

    }

    @Test(expected = IllegalStateException.class)
    public void incorrectlyDownloadAfterClose()
            throws Exception {

        Downloader downloader = new Downloader("http://localhost/does/not/matter.file");
        assertThat(downloader.download().get(), nullValue());
        downloader.close();
        downloader.download().get();

    }

    @Test
    public void correctlyDownloadAfterAutoClosure()
            throws Exception {

        try (Downloader downloader = new Downloader(UNDERENHETER_URL)) {
            assertThat(downloader.download().get(), instanceOf(File.class));
        }
        try (Downloader downloader = new Downloader(UNDERENHETER_URL)) {
            assertThat(downloader.download().get(), instanceOf(File.class));
        }

    }

    @Test(expected = TimeoutException.class)
    public void provokeTimeout()
            throws Exception {

        try (Downloader downloader = new Downloader(UNDERENHETER_URL)) {
            downloader.download().get(1, TimeUnit.MILLISECONDS);
        }

    }

    @Test
    public void downloadAndKeepFileForManualTesting()
            throws Exception {

        try (Downloader downloader = new Downloader(UNDERENHETER_URL)) {
            File copy = File.createTempFile(DownloaderTest.class.getSimpleName(), null);
            try (FileChannel source = new FileInputStream(downloader.download().get()).getChannel();
                 FileChannel target = new FileOutputStream(copy).getChannel()
            ) {
                target.transferFrom(source, 0, source.size());
            }
            LOG.info("Downloaded content kept in file {}", copy.getAbsolutePath());
        }

    }

}