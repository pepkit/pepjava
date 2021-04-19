package org.databio.pepjava;

/*
   since we need to be able to import other pep files and reconcile them
   this class will implement that "stacking" behavior
   the rule is that the most recent pep file

   the way this works is that the constructor for this class checks
   for an imoport section in the yaml file
   if one is found, it will instantiate the yaml file referenced there
   as the starting point for the reading of the current yaml file
   all of the content of the current yaml pep file will use the return of
   the import method (if exists) as a base for all transformations referenced
   in the current file. If an import section does not exist, then current file
   is the only set of transformations applied

   the constructor of the "referenced" yaml file will do the same for itself
*/

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URL;
import java.util.List;

public class Project {
    private YAMLProject yamlProject;
    private SampleTable sampleTable = null;

    public Project(String name, ObjectMapper mapper) throws
            com.fasterxml.jackson.core.JsonProcessingException,
            com.fasterxml.jackson.databind.JsonMappingException {
        try {
            System.out.println("Entering project " + name);
            // discern between URL and local filename
            if (name.startsWith("http")) {
                URL url = new URL(name);
                yamlProject = mapper.readValue(url, YAMLProject.class);
            } else {
                File f = new File(name);
                yamlProject = mapper.readValue(f, YAMLProject.class);
            }
            //sampleTable = new SampleTable(yamlProject.getSample_table());
            // see if there is anything to import
            if (yamlProject.getProject_modifiers() == null) {
                System.out.println("No project modifiers in project");
            } else {
                System.out.println("Processing all");
                List<String> imports = yamlProject.getProject_modifiers().getImport();
                if (!imports.isEmpty()) {
                    System.out.println("Processing imports!");
                    // go down the list of imports
                    // read a project and then reconcile it with the next in the list
                    // and finally apply this project to that reconciliation
                    Project first = new Project(imports.get(0), mapper);
                    for (int i = 1; i < imports.size(); i++) {
                        Project next = new Project(imports.get(i), mapper);
                        // reconcile projects?
                        next.processAll(first);
                        first = next;
                    }
                    processAll(first);
                } else
                    processAll(null);
            }
        } catch (java.io.IOException ioe) {
            System.out.println("Cannot find file " + name);
        }
    }

    public void processAll(Project other) {
        if (this.yamlProject.getSample_table() != null)
            this.sampleTable = new SampleTable(yamlProject.getSample_table());
        if (this.yamlProject.getSample_modifiers() == null) {
            System.out.println("No sample modifiers in this project.");
        } else {
            if (other != null) {
                // start out with an existing samples table (from other)
                // and apply all transformations from this PEP file on that
                other.processAll(null);
            } else {
                // go through the whole pep now
                if (yamlProject.getSample_modifiers().getRemove() != null) {
                    System.out.println("Removing " + yamlProject.getSample_modifiers().getRemove() + " sample attributes.");
                    if (getSampleTable().processRemove(yamlProject.getSample_modifiers().getRemove())) {
                        System.out.println("Column headers left in sample table");
                        sampleTable.getSampleTableHeaders().forEach(System.out::println);
                        System.out.println("Columns left in sample table: " + sampleTable.getSampleTableRows().keySet());
                    } else
                        System.out.println("Some attributes not removed.");
                }
                if (yamlProject.getSample_modifiers().getAppend() != null) {
                    getSampleTable().processAppend(yamlProject.getSample_modifiers().getAppend());
                    System.out.println("After APPEND:");
                    sampleTable.getSampleTableHeaders().forEach(System.out::println);
                }
                if (yamlProject.getSample_modifiers().getImply() != null) {
                    getSampleTable().processImply(yamlProject.getSample_modifiers().getImply());
                    System.out.println("After IMPLY:");
                    sampleTable.getSampleTableHeaders().forEach(System.out::println);
                }
                if (yamlProject.getSample_modifiers().getDerive() != null) {
                    getSampleTable().processDerive(yamlProject.getSample_modifiers().getDerive());
                }
            }
        }
    }

    public YAMLProject getYamlProject() {
        return yamlProject;
    }

    public void setYamlProject(YAMLProject yamlProject) {
        this.yamlProject = yamlProject;
    }

    public SampleTable getSampleTable() {
        return sampleTable;
    }

    public void setSampleTable(SampleTable sampleTable) {
        this.sampleTable = sampleTable;
    }

    public void sampleModifiersRemove() {
        sampleTable.processRemove(yamlProject.getSample_modifiers().getRemove());
    }
}
