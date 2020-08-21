package org.databio.pepjava;

import java.util.List;
import java.util.Map;

public class Derive {
    private List<String> attributes;
    private Map<String,String> sources;

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getSources() {
        return sources;
    }

    public void setSources(Map<String, String> sources) {
        this.sources = sources;
    }
}
