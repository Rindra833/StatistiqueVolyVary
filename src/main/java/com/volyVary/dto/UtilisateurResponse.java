package com.volyVary.dto;

public record UtilisateurResponse(
    Long id,
    String nom,
    String role,
    Long employeeId
) {
}
