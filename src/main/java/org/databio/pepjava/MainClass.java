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

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Project project = new Project("test-project.yaml", mapper);
        SampleTable sampleTable = project.getSampleTable();
        sampleTable.getSampleTableHeaders().forEach(System.out::println);
        System.out.println(sampleTable.getSampleTableRows().keySet());
        System.out.println("===========================");
        YAMLProject yamlProject = project.getYamlProject();

        if (yamlProject.getProject_modifiers().getImport() != null) {
            yamlProject.getProject_modifiers().getImport().stream().forEach(System.out::println);
        }
    }
}
