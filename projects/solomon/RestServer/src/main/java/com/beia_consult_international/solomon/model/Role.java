package com.beia_consult_international.solomon.model;

import java.util.List;

public enum Role {
    ADMIN(List.of(Authority.READ, Authority.WRITE, Authority.DELETE)),
    COMPANY(List.of(Authority.READ, Authority.WRITE, Authority.DELETE)),
    USER(List.of(Authority.READ, Authority.WRITE));

    private List<Authority> authorities;
    Role(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
