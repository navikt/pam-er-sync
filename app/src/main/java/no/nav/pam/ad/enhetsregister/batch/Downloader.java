package no.nav.pam.ad.enhetsregister.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Downloader implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Downloader.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final URL url;
    private final File temporary;

    Downloader(String x)
            throws IOException {

        this.url = new URL(x);

        temporary = File.createTempFile(Downloader.class.getSimpleName(), null);
        temporary.deleteOnExit();
        LOG.debug("Using temporary file {}", temporary.getAbsolutePath());

    }

    @Override
    public void close() {

        if (temporary.delete()) {
            LOG.debug("Deleted temporary file {}", temporary.getAbsolutePath());
        } else {
            LOG.error("Failed to delete temporary file {}", temporary.getAbsolutePath());
        }

    }

    Future<File> download()
            throws IllegalStateException {

        if (!temporary.exists()) {
            throw new IllegalStateException("Temporary file " + temporary.getAbsoluteFile() + " no longer exist; use download only once per class instance");
        }
        return executor.submit(() -> {

            try {

                try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                     FileOutputStream out = new FileOutputStream(temporary)) {
                    out.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                }
                return temporary;

            } catch (IOException e) {
                LOG.error("Failed to download file from {}", url, e);
                return null;
            }

        });

    }

}
