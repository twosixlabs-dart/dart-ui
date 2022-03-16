import Keycloak from 'keycloak-js';

// eslint-disable-next-line no-underscore-dangle
const _kc = new Keycloak('/keycloak.json');

const doLogin = _kc.login;

const doLogout = _kc.logout;

const getToken = () => _kc.token;

const getIdObject = () => _kc.idTokenParsed;

const isLoggedIn = () => !!_kc.token;

const updateToken = (successCallback) => _kc.updateToken(30)
  .then((refresh) => {
    if (refresh) successCallback(_kc.token);
    else {
      setTimeout(() => updateToken(successCallback), 1000);
    }
  })
  .catch(() => {
    // alert('unable to refresh token');
    doLogin();
  });

const setTokenRefreshHandler = (newTokenHandler) => {
  _kc.onTokenExpired = () => {
    _kc.updateToken(30)
      .then((refresh) => {
        if (refresh) {
          newTokenHandler(_kc.token, _kc.idTokenParsed);
        } else {
          alert('Failed to refresh token!');
          doLogin();
        }
      })
      .catch(() => {
        alert('onTokenExpired invocation of updateToken failed');
        doLogin();
      });
  };
};

// eslint-disable-next-line camelcase
const getUsername = () => _kc.tokenParsed?.preferred_username;

const hasRole = (roles) => roles.some((role) => _kc.hasRealmRole(role));
const initKeycloak = (onAuthenticatedCallback) => {
  _kc.init({
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
    pkceMethod: 'S256',
  })
  // eslint-disable-next-line no-unused-vars
    .then((authenticated) => {
      if (authenticated) {
        onAuthenticatedCallback(_kc.token, _kc.idTokenParsed);
      } else {
        doLogin();
      }
    });
};

const UserService = {
  initKeycloak,
  doLogin,
  doLogout,
  isLoggedIn,
  getToken,
  getIdObject,
  updateToken,
  setTokenRefreshHandler,
  getUsername,
  hasRole,
};

export default UserService;
