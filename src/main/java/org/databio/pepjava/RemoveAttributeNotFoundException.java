package org.databio.pepjava;

import java.util.List;

public class RemoveAttributeNotFoundException extends Exception {
    public RemoveAttributeNotFoundException(String missingAttrs) {
        super(missingAttrs);
    }
}
