package info.jab.ms.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "info.jab.ms", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_frameworks_application_or_adapters = noClasses()
            .that()
            .resideInAPackage("info.jab.ms.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..",
                    "com.fasterxml.jackson..",
                    "tools.jackson..",
                    "jakarta..",
                    "info.jab.ms.application..",
                    "info.jab.ms.adapter..",
                    "info.jab.ms.config..");

    @ArchTest
    static final ArchRule application_should_not_depend_on_spring_jackson_config_or_adapters = noClasses()
            .that()
            .resideInAPackage("info.jab.ms.application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..",
                    "com.fasterxml.jackson..",
                    "tools.jackson..",
                    "info.jab.ms.adapter..",
                    "info.jab.ms.config..");

    @ArchTest
    static final ArchRule driving_adapters_should_not_depend_on_driven_adapters = noClasses()
            .that()
            .resideInAPackage("info.jab.ms.adapter.in..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("info.jab.ms.adapter.out..");

    @ArchTest
    static final ArchRule driven_adapters_should_not_depend_on_driving_adapters = noClasses()
            .that()
            .resideInAPackage("info.jab.ms.adapter.out..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("info.jab.ms.adapter.in..");
}
