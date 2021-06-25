package com.backbase.api.simulator.arch;

import com.backbase.api.simulator.Application;
import com.backbase.buildingblocks.archunit.ConfigurationRules;
import com.backbase.buildingblocks.archunit.test.AllControllerRules;
import com.backbase.buildingblocks.archunit.test.AllGeneralCodingRules;
import com.backbase.buildingblocks.archunit.test.AllLoggingRules;
import com.backbase.buildingblocks.archunit.test.AllNamingConventionRules;
import com.backbase.dbs.ctrl.arch.test.AllRetailAmsterdamGeneralCodingRules;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

@AnalyzeClasses(packagesOf = Application.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitRulesTest {

    @ArchTest
    ArchTests configurationRules = ArchTests.in(ConfigurationRules.class);

    @ArchTest
    ArchTests controllerRules = ArchTests.in(AllControllerRules.class);

    @ArchTest
    ArchTests generalCodingRules = ArchTests.in(AllGeneralCodingRules.class);

    @ArchTest
    ArchTests loggingRules = ArchTests.in(AllLoggingRules.class);

    @ArchTest
    ArchTests namingConventionRules = ArchTests.in(AllNamingConventionRules.class);

    @ArchTest
    ArchTests retailAmsterdamGeneralCodingRules = ArchTests.in(AllRetailAmsterdamGeneralCodingRules.class);
}
