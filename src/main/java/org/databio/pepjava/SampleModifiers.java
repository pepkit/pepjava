package org.databio.pepjava;

import java.util.List;
import java.util.Map;

public class SampleModifiers {
    private List<String> remove;
    private Map<String,String> append;
    private Map<String,String> duplicate;
    private Derive derive;
    private List<IfThen> imply;

    public Map<String, String> getAppend() {
        return append;
    }

    public void setAppend(Map<String, String> append) {
        this.append = append;
    }

    public Map<String, String> getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Map<String, String> duplicate) {
        this.duplicate = duplicate;
    }

    public Derive getDerive() {
        return derive;
    }

    public void setDerive(Derive derive) {
        this.derive = derive;
    }

    public List<IfThen> getImply() {
        return imply;
    }

    public void setImply(List<IfThen> imply) {
        this.imply = imply;
    }

    public List<String> getRemove() {
        return remove;
    }

    public void setRemove(List<String> remove) {
        this.remove = remove;
    }


}
