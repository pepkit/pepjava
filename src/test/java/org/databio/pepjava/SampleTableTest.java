package org.databio.pepjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Assertions;

import java.util.List;

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
    void processAppend_correct() {
        assertDoesNotThrow(() -> {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/append_correct.yaml", mapper);
            project.processAllSections();
            YAMLProject yamlProject = project.getYamlProject();
            assertTrue(project.getSampleTable().getSampleTableHeaders().contains("read_type"));
            List<String> rowsReadType = project.getSampleTable().getSampleTableRows().get("read_type");
            assertNotNull(rowsReadType);
            assert(!rowsReadType.isEmpty());
            assert(rowsReadType.size() == project.getSampleTable().getSampleTableRows().get("protocol").size());
            assert(rowsReadType.get(0).equals("SINGLE"));
            assert(rowsReadType.get(rowsReadType.size()-1).equals("SINGLE"));
        });
    }

    @org.junit.jupiter.api.Test
    void processDuplicate() {
        assertDoesNotThrow(() -> {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/duplicate_correct.yaml", mapper);
            project.processAllSections();
            YAMLProject yamlProject = project.getYamlProject();
            assertTrue(project.getSampleTable().getSampleTableHeaders().contains("animal"));
            List<String> rowsAnimal = project.getSampleTable().getSampleTableRows().get("animal");
            assertNotNull(rowsAnimal);
            assert (!rowsAnimal.isEmpty());
            List<String> rowsOrganism = project.getSampleTable().getSampleTableRows().get("organism");
            assertNotNull(rowsOrganism);
            assert (!rowsOrganism.isEmpty());
            assert (rowsAnimal.equals(rowsOrganism));
            assert (rowsAnimal.size() == rowsOrganism.size());
        });
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
        assertDoesNotThrow(() -> {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/derive_missing_closing_bracket.yaml", mapper);
            project.processAllSections();
            YAMLProject yamlProject = project.getYamlProject();
            String attr = yamlProject.getSample_modifiers().getDerive().getAttributes().get(0);
            String expected = "/data/lab/project/pig_{timeh.fastq";

            assertEquals(project.getSampleTable().getSampleTableRows().get(attr).get(1), expected);
        });
    }
    /*@org.junit.jupiter.api.Test
    void processAmend() {
    }*/
}