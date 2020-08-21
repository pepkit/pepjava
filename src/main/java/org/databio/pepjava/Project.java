package org.databio.pepjava;

public class Project {
    private YAMLProject yamlProject;
    private SampleTable sampleTable = null;

    public Project(YAMLProject yp) {
        this.yamlProject = yp;
        if(yp.getSample_table() != null)
            this.sampleTable = new SampleTable(yp.getSample_table());
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
