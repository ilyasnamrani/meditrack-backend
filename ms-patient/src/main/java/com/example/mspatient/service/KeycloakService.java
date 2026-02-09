package com.example.mspatient.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakService {

    private final Keycloak keycloak;

    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Value("${keycloak.realm}")
    private String realm;

    public String createPatientUser(String registrationNumber, String password, String firstName, String lastName,
                                    String email) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(registrationNumber); // Registration Number is the username
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailVerified(true);
        // Note: Role assignment might need to be done here or via default roles in
        // Keycloak
        // For now, we assume Keycloak doesn't need explicit role "PATIENT" if defaults
        // are set,
        // or we can add logic to assign role.

        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);

            // Set Password
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(password);

            usersResource.get(userId).resetPassword(passwordCred);

            // Assign Realm Role "PATIENT"
            try {
                // Get the RoleRepresentation from the realm
                // Note: The role "PATIENT" must exist in the Keycloak realm
                org.keycloak.representations.idm.RoleRepresentation patientRole = keycloak.realm(realm).roles()
                        .get("PATIENT").toRepresentation();

                // Assign the role to the user
                usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(patientRole));
            } catch (Exception e) {
                // Log warning or throw, but user is created.
                // Best to ensure role exists. For now, we'll propagate error if role fails as
                // it is critical for login access.
                throw new RuntimeException("User created but failed to assign PATIENT role: " + e.getMessage());
            }

            return userId;
        } else {
            throw new RuntimeException("Failed to create patient user in Keycloak. Status: " + response.getStatus());
        }
    }

    private static class CreatedResponseUtil {
        public static String getCreatedId(Response response) {
            java.net.URI location = response.getLocation();
            if (location == null) {
                return null;
            }
            String path = location.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        }
    }
}
