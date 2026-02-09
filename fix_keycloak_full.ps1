
# Keycloak Full Verification and Fix Script
$ErrorActionPreference = "Stop"

$keycloakUrl = "http://localhost:9090"
$realm = "meditrack-realm"
$adminUser = "ilias"
$adminPass = "ilias2026"

Write-Host ">>> 1. Getting Admin Token..."
try {
    $tokenResponse = Invoke-RestMethod -Uri "$keycloakUrl/realms/master/protocol/openid-connect/token" -Method Post -Body @{client_id="admin-cli"; username=$adminUser; password=$adminPass; grant_type="password"}
    $token = $tokenResponse.access_token
    $headers = @{Authorization = "Bearer $token"}
    Write-Host "    [OK] Token retrieved."
} catch {
    Write-Host "    [ERROR] Failed to get token. Is Keycloak running on port 9090?"
    exit 1
}

Write-Host ">>> 2. Getting 'meditrack-client'..."
$clients = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients?clientId=meditrack-client" -Method Get -Headers $headers
$client = $clients[0]
if (-not $client) { 
    Write-Host "    [ERROR] meditrack-client not found!"
    exit 1
}
$clientId = $client.id
Write-Host "    [OK] Found Client ID: $clientId"

Write-Host ">>> 3. Verifying Client Settings..."
$needsUpdate = $false
$updatePayload = @{}

if (-not $client.serviceAccountsEnabled) {
    Write-Host "    [FIX] Enabling Service Accounts..."
    $client.serviceAccountsEnabled = $true
    $updatePayload["serviceAccountsEnabled"] = $true
    $needsUpdate = $true
} else {
    Write-Host "    [OK] Service Accounts Enabled."
}

if (-not $client.standardFlowEnabled) {
    Write-Host "    [FIX] Enabling Standard Flow..."
    $client.standardFlowEnabled = $true
    $updatePayload["standardFlowEnabled"] = $true
    $needsUpdate = $true
} else {
    Write-Host "    [OK] Standard Flow Enabled."
}

if (-not $client.directAccessGrantsEnabled) {
    Write-Host "    [FIX] Enabling Direct Access Grants..."
    $client.directAccessGrantsEnabled = $true
    $updatePayload["directAccessGrantsEnabled"] = $true
    $needsUpdate = $true
} else {
    Write-Host "    [OK] Direct Access Grants Enabled."
}

# Check Redirect URIs
if ($client.redirectUris -notcontains "http://localhost:3000/*") {
    Write-Host "    [FIX] Adding Redirect URI: http://localhost:3000/*"
    $client.redirectUris += "http://localhost:3000/*"
    $updatePayload["redirectUris"] = $client.redirectUris
    $needsUpdate = $true
} else {
    Write-Host "    [OK] Redirect URI http://localhost:3000/* exists."
}

# Check Web Origins
if ($client.webOrigins -notcontains "*") {
    Write-Host "    [FIX] Adding Web Origin *"
    $client.webOrigins += "*"
    $updatePayload["webOrigins"] = $client.webOrigins
    $needsUpdate = $true
} else {
    Write-Host "    [OK] Web Origin * exists."
}

if ($needsUpdate) {
    Write-Host ">>> 4. Updating Client Settings..."
    $json = $client | ConvertTo-Json -Depth 10
    Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients/$clientId" -Method Put -Headers $headers -Body $json -ContentType "application/json"
    Write-Host "    [SUCCESS] Client settings updated."
} else {
    Write-Host "    [OK] All client settings are correct."
}


Write-Host ">>> 5. Verifying Service Account Roles..."
$serviceAccountUser = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients/$clientId/service-account-user" -Method Get -Headers $headers
$serviceAccountUserId = $serviceAccountUser.id
Write-Host "    [OK] Service Account User ID: $serviceAccountUserId"

$realmMgmtClients = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients?clientId=realm-management" -Method Get -Headers $headers
$realmMgmtClientId = $realmMgmtClients[0].id

# Check ASSIGNED roles (Mappings)
$assignedRoles = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users/$serviceAccountUserId/role-mappings/clients/$realmMgmtClientId" -Method Get -Headers $headers
$assignedRoleNames = $assignedRoles | ForEach-Object { $_.name }

$requiredRoles = @("manage-users", "view-users", "query-users")
$rolesToAssign = @()

foreach ($role in $requiredRoles) {
    if ($assignedRoleNames -contains $role) {
        Write-Host "    [OK] Role '$role' is verified as assigned."
    } else {
        Write-Host "    [MISSING] Role '$role' is NOT assigned. Will assign."
        # Get role details to assign
        $availableRoles = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users/$serviceAccountUserId/role-mappings/clients/$realmMgmtClientId/available" -Method Get -Headers $headers
        $roleObj = $availableRoles | Where-Object { $_.name -eq $role }
        if ($roleObj) {
            $rolesToAssign += $roleObj
        } else {
            Write-Host "    [ERROR] Could not find role '$role' in available roles!"
        }
    }
}

if ($rolesToAssign.Count -gt 0) {
    Write-Host ">>> 6. Assigning Missing Roles..."
    $jsonPayload = $rolesToAssign | ConvertTo-Json -Depth 10
    Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users/$serviceAccountUserId/role-mappings/clients/$realmMgmtClientId" -Method Post -Headers $headers -Body $jsonPayload -ContentType "application/json"
    Write-Host "    [SUCCESS] Missings roles assigned."
} else {
    Write-Host "    [OK] All required roles are present."
}

Write-Host ">>> Verification Complete."
