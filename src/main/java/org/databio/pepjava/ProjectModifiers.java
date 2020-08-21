package org.databio.pepjava;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ProjectModifiers {
    private Map<String, Map<String,String>> amend;
    private List<String> _import;

    public Map<String, Map<String, String>> getAmend() {
        return amend;
    }

    public void setAmend(Map<String, Map<String, String>> amend) {
        this.amend = amend;
    }

    @JsonProperty("import")
    public List<String> getImport() {
        return _import;
    }

    public void setImport(List<String> _import) {
        this._import = _import;
    }
}
