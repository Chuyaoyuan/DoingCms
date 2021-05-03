package io.yaoyuan.doingcms;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("io.yaoyuan.doingcms");

        noClasses()
            .that()
            .resideInAnyPackage("io.yaoyuan.doingcms.service..")
            .or()
            .resideInAnyPackage("io.yaoyuan.doingcms.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..io.yaoyuan.doingcms.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
