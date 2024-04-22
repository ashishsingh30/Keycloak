package au.com.cliquote.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/self-service")
public class SelfServiceController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private RealmMappingService realmMappingService;

    // Endpoint to update user profile information
    @PatchMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody UserDTO user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(user.getUsername())) {
            auditService.logAction(currentUsername, "Unauthorized attempt to update profile");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized to update profile");
        }

        try {
            String realmName = realmMappingService.getRealmForDomain(user.getEmail().substring(user.getEmail().indexOf('@') + 1));
            boolean updated = userService.updateUser(realmName, user.getUsername(), user);
            if (updated) {
                auditService.logAction(user.getUsername(), "Updated profile successfully");
                return ResponseEntity.ok("Profile updated successfully");
            } else {
                auditService.logAction(user.getUsername(), "Failed to update profile");
                return ResponseEntity.badRequest().body("Failed to update profile");
            }
        } catch (Exception e) {
            auditService.logAction(user.getUsername(), "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating profile");
        }
    }

    // Endpoint to change user password
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Ensure operation is performed on the authenticated user

        try {
            String realmName = realmMappingService.getRealmForDomain(username.substring(username.indexOf('@') + 1));
            if (userService.changePassword(username, realmName, newPassword)) {
                auditService.logAction(username, "Password changed successfully");
                return ResponseEntity.ok("Password changed successfully");
            } else {
                auditService.logAction(username, "Password change failed");
                return ResponseEntity.badRequest().body("Password change failed");
            }
        } catch (Exception e) {
            auditService.logAction(username, "Error changing password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing password");
        }
    }
}
