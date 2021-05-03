package org.databio.pepjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
    void processRemove() {
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
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/derive_missing.yaml", mapper);
            YAMLProject yamlProject = project.getYamlProject();
            if (yamlProject.getSample_modifiers() != null && yamlProject.getSample_modifiers().getDerive() != null)
                project.getSampleTable().processDerive(yamlProject.getSample_modifiers().getDerive());
        } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
            System.out.println(jpe.getMessage());
        } catch (DeriveNoAttrFoundException dnafe) {
            System.out.println(dnafe.getMessage());
        }
        // test what happens when a key within attribute does not exist
        // test what happens when attribute exists and key exists
    }

    @org.junit.jupiter.api.Test
    void processDerive_missing_closing_bracket() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            Project project = new Project("src/test/resources/derive_missing_closing_bracket.yaml", mapper);
            YAMLProject yamlProject = project.getYamlProject();
            if (yamlProject.getSample_modifiers() != null && yamlProject.getSample_modifiers().getDerive() != null)
                project.getSampleTable().processDerive(yamlProject.getSample_modifiers().getDerive());
            String attr = yamlProject.getSample_modifiers().getDerive().getAttributes().get(0);
            String expected = "/data/lab/project/pig_{timeh.fastq";
            SampleTable st = project.getSampleTable();
            assertEquals(st.getSampleTableRows().get(attr).get(1), expected);
        } catch (com.fasterxml.jackson.core.JsonProcessingException jpe) {
            System.out.println(jpe.getMessage());
        } catch (DeriveNoAttrFoundException dnafe) {
            //System.out.println(dnafe.getMessage());
        }
        // test what happens when a key within attribute does not exist
        // test what happens when attribute exists and key exists
    }
    /*@org.junit.jupiter.api.Test
    void processAmend() {
    }*/
}