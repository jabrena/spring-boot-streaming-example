package info.jab.ms.architecture;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

    private static final Path SOURCE_ROOT = Path.of("src/main/java/info/jab/ms");

    @Test
    void domain_should_not_depend_on_frameworks_application_or_adapters() throws IOException {
        List<String> imports = importsUnder("domain");

        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import io.quarkus.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import io.smallrye.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import com.fasterxml.jackson.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import jakarta.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import info.jab.ms.application.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import info.jab.ms.adapter.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import info.jab.ms.config.")));
    }

    @Test
    void application_should_not_depend_on_quarkus_jackson_config_or_adapters() throws IOException {
        List<String> imports = importsUnder("application");

        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import io.quarkus.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import com.fasterxml.jackson.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import info.jab.ms.adapter.")));
        assertTrue(imports.stream().noneMatch(line -> line.startsWith("import info.jab.ms.config.")));
    }

    @Test
    void driving_and_driven_adapters_should_not_depend_on_each_other() throws IOException {
        assertTrue(importsUnder("adapter/in").stream()
                .noneMatch(line -> line.startsWith("import info.jab.ms.adapter.out.")));

        assertTrue(importsUnder("adapter/out").stream()
                .noneMatch(line -> line.startsWith("import info.jab.ms.adapter.in.")));
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
