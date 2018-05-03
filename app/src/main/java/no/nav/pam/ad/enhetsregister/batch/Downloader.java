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

/**
 * A generic one-off downloader, intended to be used as an {@code AutoCloseable}.
 */
class Downloader implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Downloader.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final URL url;
    private final File file;

    Downloader(URL url)
            throws IOException {

        this.url = url;

        file = File.createTempFile(Downloader.class.getSimpleName(), null);
        file.deleteOnExit();
        LOG.debug("Using temporary file {}", file.getAbsolutePath());

    }

    @Override
    public void close() {

        if (file.delete()) {
            LOG.debug("Deleted temporary file {}", file.getAbsolutePath());
        } else {
            LOG.error("Failed to delete temporary file {}", file.getAbsolutePath());
        }

    }

    Future<File> download()
            throws IllegalStateException {

        if (!file.exists()) {
            throw new IllegalStateException("Temporary file " + file.getAbsoluteFile() + " no longer exist; use download only once per class instance");
        }
        return executor.submit(() -> {

            try {

                try (ReadableByteChannel channel = Channels.newChannel(url.openStream());
                     FileOutputStream out = new FileOutputStream(file)) {

                    long started = System.currentTimeMillis();
                    out.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                    LOG.debug("{} bytes written to file {} in {} ms", out.getChannel().size(), file.getAbsoluteFile(), System.currentTimeMillis() - started);

                }
                return file;

            } catch (IOException e) {
                LOG.error("Failed to download file from {}", url, e);
                return null;
            }

        });

    }

}
