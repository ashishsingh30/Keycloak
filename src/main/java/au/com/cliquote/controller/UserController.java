package au.com.cliquote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RealmMappingService realmMappingService;

    @Autowired
    private RealmService realmService;

    @Autowired
    private AuditService auditService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO user) {
        String realmName = realmMappingService.getRealmForDomain(user.getEmail().substring(user.getEmail().indexOf('@') + 1));
        if (!realmService.isAuthorizedForRealm(realmName)) {
            auditService.logAction("Unauthorized attempt to register user", user.getUsername());
            return ResponseEntity.status(403).body("Unauthorized to manage users in this realm");
        }

        if (userService.createUser(realmName, user)) {
            auditService.logAction("User created successfully", user.getUsername());
            return ResponseEntity.ok("User created successfully with role: " + user.getRole());
        } else {
            auditService.logAction("Failed to create user", user.getUsername());
            return ResponseEntity.status(500).body("Failed to create user");
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody UserDTO user) {
        String realmName = realmMappingService.getRealmForDomain(user.getEmail().substring(user.getEmail().indexOf('@') + 1));
        if (!realmService.isAuthorizedForRealm(realmName)) {
            auditService.logAction("Unauthorized attempt to update user", user.getUsername());
            return ResponseEntity.status(403).body("Unauthorized to update user in this realm");
        }

        if (userService.updateUser(realmName, userId, user)) {
            auditService.logAction("User updated successfully", user.getUsername());
            return ResponseEntity.ok("User updated successfully");
        } else {
            auditService.logAction("Failed to update user", user.getUsername());
            return ResponseEntity.status(500).body("Failed to update user");
        }
    }

    @PostMapping("/disable/{userId}")
    public ResponseEntity<String> disableUser(@PathVariable String userId) {
        String email = userService.getUserEmail(userId);  // This method needs to be properly defined in UserService.
        String realmName = realmMappingService.getRealmForDomain(email.substring(email.indexOf('@') + 1));
        if (!realmService.isAuthorizedForRealm(realmName)) {
            auditService.logAction("Unauthorized attempt to disable user", userId);
            return ResponseEntity.status(403).body("Unauthorized to disable user in this realm");
        }

        if (userService.disableUser(realmName, userId)) {
            auditService.logAction("User disabled successfully", userId);
            return ResponseEntity.ok("User disabled successfully");
        } else {
            auditService.logAction("Failed to disable user", userId);
            return ResponseEntity.status(500).body("Failed to disable user");
        }
    }
}
