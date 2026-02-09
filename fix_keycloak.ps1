
# Keycloak Configuration Fix Script
$ErrorActionPreference = "Stop"

$keycloakUrl = "http://localhost:9090"
$realm = "meditrack-realm"
$adminUser = "ilias"
$adminPass = "ilias2026"

Write-Host "1. Getting Admin Token..."
$tokenResponse = Invoke-RestMethod -Uri "$keycloakUrl/realms/master/protocol/openid-connect/token" -Method Post -Body @{client_id="admin-cli"; username=$adminUser; password=$adminPass; grant_type="password"}
$token = $tokenResponse.access_token
$headers = @{Authorization = "Bearer $token"}

Write-Host "2. Getting meditrack-client ID..."
$clients = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients?clientId=meditrack-client" -Method Get -Headers $headers
$meditrackClient = $clients[0]
if (-not $meditrackClient) { throw "meditrack-client not found" }
$meditrackClientId = $meditrackClient.id
Write-Host "   Found Client ID: $meditrackClientId"

Write-Host "3. Getting Service Account User ID..."
$serviceAccountUser = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients/$meditrackClientId/service-account-user" -Method Get -Headers $headers
$serviceAccountUserId = $serviceAccountUser.id
Write-Host "   Found Service Account User ID: $serviceAccountUserId"

Write-Host "4. Getting realm-management Client ID..."
$realmMgmtClients = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/clients?clientId=realm-management" -Method Get -Headers $headers
$realmMgmtClient = $realmMgmtClients[0]
if (-not $realmMgmtClient) { throw "realm-management client not found" }
$realmMgmtClientId = $realmMgmtClient.id
Write-Host "   Found realm-management Client ID: $realmMgmtClientId"

Write-Host "5. Getting Available Roles..."
$availableRoles = Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users/$serviceAccountUserId/role-mappings/clients/$realmMgmtClientId/available" -Method Get -Headers $headers

$rolesToAssign = @("manage-users", "view-users", "query-users")
$rolesPayload = @()

foreach ($roleName in $rolesToAssign) {
    $role = $availableRoles | Where-Object { $_.name -eq $roleName }
    if ($role) {
        $rolesPayload += $role
        Write-Host "   Found role to assign: $roleName"
    } else {
        Write-Host "   WARNING: Role '$roleName' not found or already assigned."
    }
}

if ($rolesPayload.Count -gt 0) {
    Write-Host "6. Assigning Roles..."
    $jsonPayload = $rolesPayload | ConvertTo-Json -Depth 10
    Invoke-RestMethod -Uri "$keycloakUrl/admin/realms/$realm/users/$serviceAccountUserId/role-mappings/clients/$realmMgmtClientId" -Method Post -Headers $headers -Body $jsonPayload -ContentType "application/json"
    Write-Host "   SUCCESS: Roles assigned!"
} else {
    Write-Host "   No new roles to assign (maybe already assigned?)."
}

Write-Host "Done."
