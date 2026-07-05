package com.example.mvcstreamingconsumer.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

    private static final Path SOURCE_ROOT = Path.of("src/main/java/com/example/mvcstreamingconsumer");

    @Test
    void domain_should_not_depend_on_frameworks_application_or_adapters() throws IOException {
        assertThat(importsUnder("domain"))
                .noneMatch(line -> line.startsWith("import org.springframework."))
                .noneMatch(line -> line.startsWith("import com.fasterxml.jackson."))
                .noneMatch(line -> line.startsWith("import tools.jackson."))
                .noneMatch(line -> line.startsWith("import jakarta."))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.application."))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.adapter."))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.config."));
    }

    @Test
    void application_should_not_depend_on_spring_jackson_config_or_adapters() throws IOException {
        assertThat(importsUnder("application"))
                .noneMatch(line -> line.startsWith("import org.springframework."))
                .noneMatch(line -> line.startsWith("import com.fasterxml.jackson."))
                .noneMatch(line -> line.startsWith("import tools.jackson."))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.adapter."))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.config."));
    }

    @Test
    void driving_and_driven_adapters_should_not_depend_on_each_other() throws IOException {
        assertThat(importsUnder("adapter/in"))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.adapter.out."));

        assertThat(importsUnder("adapter/out"))
                .noneMatch(line -> line.startsWith("import com.example.mvcstreamingconsumer.adapter.in."));
    }

    private static List<String> importsUnder(String packagePath) throws IOException {
        Path root = SOURCE_ROOT.resolve(packagePath);

        try (var paths = Files.walk(root)) {
            return paths.filter(path -> path.toString().endsWith(".java"))
                    .flatMap(path -> {
                        try {
                            return Files.readAllLines(path).stream();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .map(String::trim)
                    .filter(line -> line.startsWith("import "))
                    .toList();
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
}
