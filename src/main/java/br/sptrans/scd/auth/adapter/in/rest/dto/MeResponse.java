package br.sptrans.scd.auth.adapter.in.rest.dto;

import java.util.Set;

public record MeResponse(Long id, String name, Set<String> roles, Set<String> permissions, Set<String> groups) {}
