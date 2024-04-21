# **Technical Documentation: Dynamic Realm-Based User Management Microservice Integrated with Keycloak**

This documentation elaborates on the setup, configuration, and operation of a Spring Boot microservice designed to manage user authentication and authorization across multiple Keycloak realms. Each realm is tailored to different organizational units or customer domains, providing granular control over access and management rights.

### **System Overview**

The microservice architecture incorporates several key components interacting with Keycloak to ensure secure and efficient user management:

- **KeycloakConfig**: Manages connection settings for Keycloak interaction.
- **SecurityConfig**: Configures Spring Security to secure application endpoints using Keycloak integration.
- **UserController**: Manages RESTful endpoints for administrative user operations.
- **UserService**: Handles the business logic for creating, updating, and disabling users in Keycloak, including role assignments.
- **UserDTO**: Data Transfer Object that encapsulates user information.
- **RealmService**: Validates if a user is authorized to perform administrative actions within a specific realm.
- **RealmMappingService**: Maintains mappings between email domains and their corresponding Keycloak realms.
- **AuditService**: Records significant user-related actions for accountability and security monitoring.
- **SelfServiceController**: Provides endpoints for users to manage their own profiles and passwords, enhancing user autonomy and reducing administrative overhead.

### **Keycloak Server Configuration**

### **Setting Up Realms and Roles**

1. **Realm Creation**: Establish individual realms for each customer or organizational unit, such as:
    - **`GoogleRealm`** for **`google.com`**
    - **`MicrosoftRealm`** for **`microsoft.com`**
2. **Define Roles**: Define roles like **`ADMIN`**, **`MANAGER`**, and **`USER`** within each realm to dictate user access levels within the microservice.
3. **SMTP Configuration**: Set up SMTP under **`Realm Settings`** -> **`Email`** in Keycloak for each realm to enable email notifications for actions like password resets.

### **Application Workflow**

### **User Registration and Management Process**

1. **User Registration Request**: Admins issue requests to **`/api/users/register`** with user data, including roles and domain-specific email addresses.
2. **Realm Determination**: **`RealmMappingService`** identifies the appropriate realm based on the email domain.
3. **Authorization Check**: **`RealmService`** confirms if the requester has **`ADMIN`** role privileges within the identified realm.
4. **User Creation**: **`UserService`** creates the user in the specified realm without setting a password initially.
5. **Role Assignment**: Roles specified in **`UserDTO`** are assigned to new users. If a role does not exist, the process stops, and an error is reported.
6. **Email Notification**: Keycloak sends an email to the new user with a link to set their password, securing the registration process.

### **Self-Service User Management**

- **Profile Update and Password Change**: **`SelfServiceController`** enables users to update their profiles and change passwords, empowering users while ensuring security via authenticated sessions.

### **Security and Role Verification**

- **Endpoint Security**: Configured through **`SecurityConfig`**, restricting access to sensitive endpoints to authenticated users with appropriate roles.
- **Role Checks**: The application verifies that the authenticated user's role aligns with the required permissions for actions, such as **`ADMIN`** for user creation.

### **Detailed Component Functionality**

### **KeycloakConfig**

Initializes the Keycloak client with necessary credentials and configuration details, ensuring secure communication with the Keycloak server.

### **SecurityConfig**

Implements Spring Security configurations that integrate with Keycloak, ensuring that only authorized users can access certain endpoints based on their roles and assigned realms.

### **UserController**

Handles HTTP requests, interacting with **`UserService`** for user creation and **`RealmService`** for authorization checks.

### **UserService**

Directly interacts with Keycloak’s API to manage user creation, role assignments, and manage user states (e.g., disabling accounts).

### **RealmService**

Verifies user permissions, ensuring that only authorized users perform administrative actions within their realms.

### **RealmMappingService**

Updates and caches mappings between email domains and Keycloak realms, supporting dynamic realm determination during user registration.

### **AuditService**

Logs significant user management actions, providing detailed records for security audits and operational monitoring.

### **SelfServiceController**

Offers endpoints for users to independently manage their profiles and passwords, reducing administrative load and enhancing user satisfaction.
