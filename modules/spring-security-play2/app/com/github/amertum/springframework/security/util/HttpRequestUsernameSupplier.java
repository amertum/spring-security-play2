package com.github.amertum.springframework.security.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

public class HttpRequestUsernameSupplier
        implements Supplier<String> {

    @Override
    public String get() {
        return ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse("ANONYMOUS");
    }

}
