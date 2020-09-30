package org.databio.pepjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainClass {

    public static void main(String[] args) throws
            java.io.IOException,
            com.fasterxml.jackson.core.JsonProcessingException,
            com.fasterxml.jackson.databind.JsonMappingException {

        File yamlSource = new File("test-project.yaml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        YAMLProject yamlProject = mapper.readValue(yamlSource, YAMLProject.class);
        Project project = new Project(yamlProject);
        SampleTable sampleTable = project.getSampleTable();
        sampleTable.getSampleTableHeaders().forEach(System.out::println);
        System.out.println(sampleTable.getSampleTableRows().keySet());
        System.out.println("===========================");
        if (yamlProject.getSample_modifiers().getRemove() != null) {
            System.out.println("Removing " + yamlProject.getSample_modifiers().getRemove() + " sample attributes.");
            if (project.getSampleTable().processRemove(yamlProject.getSample_modifiers().getRemove())) {
                System.out.println("Column headers left in sample table");
                sampleTable.getSampleTableHeaders().forEach(System.out::println);
                System.out.println("Columns left in sample table: " + sampleTable.getSampleTableRows().keySet());
            } else
                System.out.println("Some attributes not removed.");
        }
        if (yamlProject.getSample_modifiers().getAppend() != null) {
            project.getSampleTable().processAppend(yamlProject.getSample_modifiers().getAppend());
            System.out.println("After APPEND:");
            sampleTable.getSampleTableHeaders().forEach(System.out::println);
        }
        if (yamlProject.getSample_modifiers().getImply() != null) {
            project.getSampleTable().processImply(yamlProject.getSample_modifiers().getImply());
            System.out.println("After IMPLY:");
            sampleTable.getSampleTableHeaders().forEach(System.out::println);
        }
    }
}
