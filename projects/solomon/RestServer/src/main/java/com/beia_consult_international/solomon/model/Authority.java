package com.beia_consult_international.solomon.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    READ,
    WRITE,
    DELETE;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
