package com.example.msauth.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
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

    public String createUser(String username, String email, String password, String firstName, String lastName,
            String role) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email); // Use email as username
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmailVerified(true);

        UsersResource usersResource = keycloak.realm(realm).users();

        // Security Note: Avoid printing tokens in production logs
        // System.out.println("Keycloak Access Token: " +
        // keycloak.tokenManager().getAccessToken().getToken());

        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);
            System.out.println("User created successfully in Keycloak with ID: " + userId);

            // Get a reference to the specific user resource once
            UserResource userResource = usersResource.get(userId);

            try {
                // 1. Assign Role
                /*
                 * if (role != null && !role.isEmpty()) {
                 * // This will throw a 404 Not Found if the role name doesn't exist in Keycloak
                 * RoleRepresentation realmRole =
                 * keycloak.realm(realm).roles().get(role).toRepresentation();
                 * 
                 * userResource.roles().realmLevel().add(Collections.singletonList(realmRole));
                 * System.out.println("Role " + role + " assigned to user " + userId);
                 * }
                 */

                // 2. Set Password
                if (password != null && !password.isEmpty()) {
                    CredentialRepresentation passwordCred = new CredentialRepresentation();
                    passwordCred.setTemporary(false);
                    passwordCred.setType(CredentialRepresentation.PASSWORD);
                    passwordCred.setValue(password);

                    userResource.resetPassword(passwordCred);
                    System.out.println("Password set for user " + userId);
                }

                return userId;

            } catch (Exception e) {
                // MANUALLY ROLLBACK: Delete the user if Role or Password assignment failed
                System.err.println("Error configuring user. Deleting incomplete user from Keycloak: " + userId);
                try {
                    usersResource.delete(userId);
                } catch (Exception deleteEx) {
                    System.err.println("Failed to rollback (delete) user " + userId + ": " + deleteEx.getMessage());
                }
                // Throw exception to trigger Spring DB transaction rollback in StaffService
                throw new RuntimeException("Failed to configure user (Role/Password): " + e.getMessage());
            }

        } else {
            System.err.println("Failed to create user in Keycloak. Status: " + response.getStatus());
            if (response.hasEntity()) {
                System.err.println("Error body: " + response.readEntity(String.class));
            }
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
        }
    }

    public void updateUser(String keycloakId, String email, String firstName, String lastName, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(keycloakId);

        userResource.update(user);

        if (password != null && !password.isEmpty()) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(password);
            userResource.resetPassword(passwordCred);
        }
    }

    public void deleteUser(String keycloakId) {
        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.delete(keycloakId);

        // 204 = Deleted, 404 = Already gone (acceptable), 200 = OK
        if (response.getStatus() != 204 && response.getStatus() != 200 && response.getStatus() != 404) {
            throw new RuntimeException("Failed to delete user from Keycloak. Status: " + response.getStatus());
        }
    }

    // Helper to parse ID from Location header
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