package org.databio.pepjava;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class IfThen {

    private Map<String,Object> _if;
    private Map<String,String> _then;

    @JsonProperty("if")
    public Map<String, Object> getIf() {
        return _if;
    }

    public void setIf(Map<String, Object> _if) {
        this._if = _if;
    }

    @JsonProperty("then")
    public Map<String, String> getThen() {
        return _then;
    }

    public void setThen(Map<String, String> _then) {
        this._then = _then;
    }
}
