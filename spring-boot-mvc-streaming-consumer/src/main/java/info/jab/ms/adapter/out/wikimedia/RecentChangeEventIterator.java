package info.jab.ms.adapter.out.wikimedia;

import info.jab.ms.domain.model.RecentChange;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class RecentChangeEventIterator implements Iterator<RecentChange> {

    private static final String DATA_PREFIX = "data: ";

    private final BufferedReader reader;
    private final ObjectMapper objectMapper;
    private RecentChange next;
    private boolean finished;

    RecentChangeEventIterator(BufferedReader reader, ObjectMapper objectMapper) {
        this.reader = reader;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }
        if (finished || Thread.currentThread().isInterrupted()) {
            return false;
        }

        next = readNext();
        return next != null;
    }

    @Override
    public RecentChange next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        RecentChange current = next;
        next = null;
        return current;
    }

    void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RecentChange readNext() {
        try {
            String line;
            while ((line = reader.readLine()) != null && !Thread.currentThread().isInterrupted()) {
                if (!line.startsWith(DATA_PREFIX)) {
                    continue;
                }

                return objectMapper.readValue(line.substring(DATA_PREFIX.length()), WikimediaRecentChangeEvent.class)
                        .toDomain();
            }

            finished = true;
            return null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
