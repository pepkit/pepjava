package org.databio.pepjava;

public class YAMLProject {
    // version of pep spec implemented
    private String pep_version;
    // the path to the sample table
    private String sample_table = null;
    private String output_dir = null;
    private SampleModifiers sample_modifiers;
    private ProjectModifiers project_modifiers;

    public String getPep_version() {
        return pep_version;
    }

    public void setPep_version(String pep_version) {
        this.pep_version = pep_version;
    }

    public String getOutput_dir() {
        return output_dir;
    }

    public void setOutput_dir(String output_dir) {
        this.output_dir = output_dir;
    }

    public String getSample_table() {
        return sample_table;
    }

    public void setSample_table(String sample_table) {
        this.sample_table = sample_table;
    }

    public SampleModifiers getSample_modifiers() {
        return sample_modifiers;
    }

    public void setSample_modifiers(SampleModifiers sample_modifiers) {
        this.sample_modifiers = sample_modifiers;
    }

    public ProjectModifiers getProject_modifiers() {
        return project_modifiers;
    }

    public void setProject_modifiers(ProjectModifiers project_modifiers) {
        this.project_modifiers = project_modifiers;
    }
}
