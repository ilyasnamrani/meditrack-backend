---
description: MediTrack User Onboarding and Login Workflow
---

# User Onboarding Workflow

This workflow describes the process for staff registration and patient access within the MediTrack system.

## 1. Staff Onboarding
1. **Access**: Staff member visits the landing page.
2. **Registration**: Clicks on **"Espace Staff"** or **"S'inscrire (Staff)"** in the navbar.
3. **Form**: Fills in the registration form (Name, Email, Password, Professional Role).
4. **Keycloak**: The system automatically creates a corresponding user in Keycloak with the selected role.
5. **Login**: After registration, the staff member uses the **"Connexion"** button to log in.
6. **Dashboard**: Upon successful login, the staff is redirected to `/staff`, where they can manage patients.

## 2. Patient Onboarding
1. **Creation**: A staff member creates a new patient via the **"Nouveau Patient"** button in the Staff Dashboard.
2. **Registration ID**: The system generates a unique `Registration Number` (e.g., `PAT-123456789`).
3. **Credentials**: The patient's account is created in Keycloak using the `Registration Number` as the username and a default password (e.g., `password`).
4. **Access**: The patient visits the landing page and clicks **"Accès Patient"**.
5. **Login**: The patient enters their `Registration Number` and password in the Keycloak login screen.
6. **Interface**: After login, the patient is redirected to `/patient`, where they can view their **alerts**, **planning** (appointments), and **billing** (facturation).
