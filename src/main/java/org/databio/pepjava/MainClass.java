package org.databio.pepjava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "pepjava", mixinStandardHelpOptions = true, version = "checksum 4.6.1",
        description = "Parses a pep project")
public class MainClass implements Callable<Integer> {
    @Parameters(index = "0", description = "PEP .yaml file to start with.")
    private String pepProjectFileName = null;

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Project project = new Project(pepProjectFileName, mapper);
        SampleTable sampleTable = project.getSampleTable();
        sampleTable.getSampleTableHeaders().forEach(System.out::println);
        System.out.println(sampleTable.getSampleTableRows().keySet());
        System.out.println("===========================");
        YAMLProject yamlProject = project.getYamlProject();

        if (yamlProject != null &&
            yamlProject.getProject_modifiers() != null &&
            yamlProject.getProject_modifiers().getImport() != null) {
            yamlProject.getProject_modifiers().getImport().stream().forEach(System.out::println);
        }
        return 0;
    }

    public static void main(String[] args) throws
            java.io.IOException,
            com.fasterxml.jackson.core.JsonProcessingException,
            com.fasterxml.jackson.databind.JsonMappingException {

        int exitCode = new CommandLine(new MainClass()).execute(args);
        System.exit(exitCode);
    }
}
