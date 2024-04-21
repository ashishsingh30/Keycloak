package au.com.cliquote.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private Keycloak keycloak;

    // Create a new user with a specific role and trigger an action for password setup
    public boolean createUser(String realmName, UserDTO user) {
        UsersResource usersResource = keycloak.realm(realmName).users();

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(user.getUsername());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setEnabled(true);

        // No initial password is set
        newUser.setCredentials(null);

        Response response = usersResource.create(newUser);
        if (response.getStatus() != 201) {
            return false;
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        UserResource userResource = usersResource.get(userId);

        // Assign role to the user
        RoleRepresentation role = keycloak.realm(realmName).roles().get(user.getRole()).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(role));

        // Trigger required actions email (e.g., UPDATE_PASSWORD)
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));

        return true;
    }

    // Update existing user details
    public boolean updateUser(String realmName, String userId, UserDTO updatedUser) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        UserResource userResource = usersResource.get(userId);
        UserRepresentation user = userResource.toRepresentation();

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        userResource.update(user);
        return true;
    }

    // Disable a user account
    public boolean disableUser(String realmName, String userId) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        UserResource userResource = usersResource.get(userId);
        UserRepresentation user = userResource.toRepresentation();

        user.setEnabled(false);
        userResource.update(user);
        return true;
    }

    // Change user's password
    public boolean changePassword(String userId, String realmName, String newPassword) {
        try {
            UsersResource usersResource = keycloak.realm(realmName).users();
            UserResource userResource = usersResource.get(userId);
            CredentialRepresentation newCredential = new CredentialRepresentation();
            newCredential.setType(CredentialRepresentation.PASSWORD);
            newCredential.setValue(newPassword);
            newCredential.setTemporary(false); // Set to true if you want the user to update it at next login

            userResource.resetPassword(newCredential);
            return true;
        } catch (Exception e) {
            // Log the error or handle it as per your error handling policy
            return false;
        }
    }


}
