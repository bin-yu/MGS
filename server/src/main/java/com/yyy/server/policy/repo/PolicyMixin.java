package com.yyy.server.policy.repo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PolicyMixin {
    @JsonIgnore
    public Long getId();

    @JsonIgnore
    public String getName();
}
