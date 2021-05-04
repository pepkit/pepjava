package org.databio.pepjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

class SampleTableTest {

    @org.junit.jupiter.api.Test
    void getSampleTableFileName() {
    }

    @org.junit.jupiter.api.Test
    void getSampleTableHeaders() {
    }

    @org.junit.jupiter.api.Test
    void getSampleTableRows() {
    }

    @org.junit.jupiter.api.Test
    void processRemove_missing_attribute() {
        Assertions.assertThrows(RemoveAttributeNotFoundException.class, () -> {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/remove_test.yaml", mapper);
            project.processAllSections();
        });
    }

    @org.junit.jupiter.api.Test
    void processRemove_confirm_attr_removed() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Project project = new Project("src/test/resources/remove_test.yaml", mapper);
        try {
            project.processAllSections();
        } catch (Exception e) {
            // we know the "missing" attribute will trigger an exception
            // but the 2nd attribute ("protocol") should have been removed
            assertEquals(project.getSampleTable().getSampleTableHeaders().size(), 4);
        }
    }

    @org.junit.jupiter.api.Test
    void processAppend() {
    }

    @org.junit.jupiter.api.Test
    void processDuplicate() {
    }

    @org.junit.jupiter.api.Test
    void processImply() {
    }

    @org.junit.jupiter.api.Test
    void processDerive_missing_attribute() {
        // test what happens when an attribute doesn't exist
        Assertions.assertThrows(DeriveNoAttrFoundException.class, () -> {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/derive_missing.yaml", mapper);
            project.processAllSections();
        });
    }

    @org.junit.jupiter.api.Test
    void processDerive_missing_closing_bracket() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/derive_missing_closing_bracket.yaml", mapper);
            project.processAllSections();
            YAMLProject yamlProject = project.getYamlProject();
            if (yamlProject.getSample_modifiers() != null && yamlProject.getSample_modifiers().getDerive() != null)
                project.getSampleTable().processDerive(yamlProject.getSample_modifiers().getDerive());
            String attr = yamlProject.getSample_modifiers().getDerive().getAttributes().get(0);
            String expected = "/data/lab/project/pig_{timeh.fastq";

            assertEquals(project.getSampleTable().getSampleTableRows().get(attr).get(1), expected);
        } catch (Exception e) {}
    }
    /*@org.junit.jupiter.api.Test
    void processAmend() {
    }*/
}