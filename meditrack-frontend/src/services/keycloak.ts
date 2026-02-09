import Keycloak from 'keycloak-js';

// Configuration Keycloak
const keycloakConfig = {
  url: 'http://localhost:9090',
  realm: 'meditrack-realm',
  clientId: 'meditrack-client',
};

const keycloak = new Keycloak(keycloakConfig);

/**
 * Initialise Keycloak et vérifie l'authentification au démarrage
 */
export const initKeycloak = (onAuthenticatedCallback: () => void) => {
  keycloak
    .init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      pkceMethod: 'S256',
      checkLoginIframe: false,
    })
    .then((authenticated: boolean) => {
      if (authenticated) {
        console.log("User is authenticated");
      } else {
        console.log("User is not authenticated");
      }
      onAuthenticatedCallback();
    })
    .catch((error: unknown) => {
      console.error("Keycloak initialization failed", error);
      // Fallback for debugging
      if (window.confirm("Keycloak failed to initialize. Redirect to login?")) {
        keycloak.login();
      }
      onAuthenticatedCallback();
    });
};

export const doLogin = keycloak.login;
export const doLogout = keycloak.logout;
export const getToken = () => keycloak.token;
export const isLoggedIn = () => !!keycloak.token;
export const updateToken = (successCallback: () => void) =>
  keycloak.updateToken(5).then(successCallback).catch(doLogin);
export const getUsername = () => keycloak.tokenParsed?.preferred_username;
export const hasRole = (roles: string[]) => roles.some((role) => keycloak.hasResourceRole(role));

// Helper for extracting roles
export const getUserRoles = (): string[] => {
  let roles: string[] = [];

  // 1. Realm Roles (Global)
  if (keycloak.realmAccess?.roles) {
    roles = [...roles, ...keycloak.realmAccess.roles];
  }

  // 2. Client Roles (Specific to meditrack-client)
  if (keycloak.resourceAccess?.['meditrack-client']?.roles) {
    roles = [...roles, ...keycloak.resourceAccess['meditrack-client'].roles];
  }

  return [...new Set(roles)]; // Remove duplicates
};

export const isStaff = () => {
  const roles = getUserRoles();
  return roles.includes('DOCTOR') || roles.includes('NURSE') || roles.includes('ADMIN');
};

export const isPatient = () => {
  const roles = getUserRoles();
  return roles.includes('PATIENT');
};

export default keycloak;
