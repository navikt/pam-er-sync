package no.nav.pam.ad.enhetsregister.batch;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Ignored, since it actually performs downloads. Run manually if needed, and use {@link #downloadAndKeepFileForManualTesting()} if you need
 * to preserve a copy of the downloaded (gzipped) file.
 */
@Ignore
public class DownloaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(DownloaderTest.class);
    private static final String ENHETSREGISTER_UNDERENHET_URL = "https://data.brreg.no/enhetsregisteret/download/underenheter";
    private static final Proxy PROXY = Proxy.NO_PROXY; // You might need to change this if you rely on an actual proxy in your environment.

    @Test
    public void downloadUnderenheter()
            throws Exception {

        try (Downloader downloader = new Downloader(PROXY, new URL(ENHETSREGISTER_UNDERENHET_URL))) {
            assertThat(downloader.download().get(), instanceOf(File.class));
        }

    }

    @Test(expected = ExecutionException.class)
    public void downloadNonExistingUrl()
            throws Exception {

        try (Downloader downloader = new Downloader(PROXY, new URL("http://localhost/does/not/exist.file"))) {
            assertThat(downloader.download().get(), nullValue());
        }

    }

    @Test(expected = ExecutionException.class)
    public void incorrectlyDownloadAfterClose()
            throws Exception {

        Downloader downloader = new Downloader(PROXY, new URL("http://localhost/does/not/matter.file"));
        assertThat(downloader.download().get(), nullValue());
        downloader.close();
        downloader.download().get();

    }

    @Test
    public void correctlyDownloadAfterAutoClosure()
            throws Exception {

        URL url = new URL(ENHETSREGISTER_UNDERENHET_URL);
        try (Downloader downloader = new Downloader(PROXY, url)) {
            assertThat(downloader.download().get(), instanceOf(File.class));
        }
        try (Downloader downloader = new Downloader(PROXY, url)) {
            assertThat(downloader.download().get(), instanceOf(File.class));
        }

    }

    @Test(expected = TimeoutException.class)
    public void provokeTimeout()
            throws Exception {

        try (Downloader downloader = new Downloader(PROXY, new URL(ENHETSREGISTER_UNDERENHET_URL))) {
            downloader.download().get(1, TimeUnit.MILLISECONDS);
        }

    }

    @Test
    public void downloadAndKeepFileForManualTesting()
            throws Exception {

        try (Downloader downloader = new Downloader(PROXY, new URL(ENHETSREGISTER_UNDERENHET_URL))) {
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