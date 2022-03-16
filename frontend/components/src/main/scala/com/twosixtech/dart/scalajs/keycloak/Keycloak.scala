package com.twosixtech.dart.scalajs.keycloak

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}
import scala.scalajs.js.|

@js.native
trait KeycloakParams extends js.Object {
    var url : String = js.native
    var realm : String = js.native
    var clientId : String = js.native
}

object KeycloakParams {
    def apply(
        url : String,
        realm : String,
        clientId : String,
    ) : KeycloakParams = {
        val kp = ( new js.Object ).asInstanceOf[ KeycloakParams ]
        kp.url = url
        kp.realm = realm
        kp.clientId = clientId
        kp
    }
}

@js.native
trait KeycloakInit extends js.Object {
    var useNonce : Boolean = js.native
    var onLoad : String = js.native // - Specifies an action to do on load. Supported values are login-required or check-sso.
    var silentCheckSsoRedirectUri : String = js.native // - Set the redirect uri for silent authentication check if onLoad is set to 'check-sso'.
    var silentCheckSsoFallback : Boolean = js.native // - Enables fall back to regular check-sso when silent check-sso is not supported by the browser (default is true).
    var token : String = js.native // - Set an initial value for the token.
    var refreshToken : String = js.native // - Set an initial value for the refresh token.
    var idToken : String = js.native // - Set an initial value for the id token (only together with token or refreshToken).
    var timeSkew : Int = js.native // - Set an initial value for skew between local time and Keycloak server in seconds (only together with token or refreshToken).
    var checkLoginIframe : Boolean = js.native // - Set to enable/disable monitoring login state (default is true).
    var checkLoginIframeInterval : Int = js.native // - Set the interval to check login state (default is 5 seconds).
    var responseMode : String = js.native // - Set the OpenID Connect response mode send to Keycloak server at login request. Valid values are query or fragment. Default value is fragment, which means that after successful authentication will Keycloak redirect to JavaScript application with OpenID Connect parameters added in URL fragment. This is generally safer and recommended over query.
    var flow : String = js.native // - Set the OpenID Connect flow. Valid values are standard, implicit or hybrid.
    var enableLogging : String = js.native // - Enables logging messages from Keycloak to the console (default is false).
    var pkceMethod : String = js.native // - The method for Proof Key Code Exchange (PKCE) to use. Configuring this value enables the PKCE mechanism. Available options: "S256" - The SHA256 based PKCE method
    var messageReceiveTimeout : Int = js.native // - Set a timeout in milliseconds for waiting for message responses from the Keycloak server. This is used, for example, when waiting for a message during 3rd party cookies check. The default value is 10000.
}

object KeycloakInit {
    def apply(
        useNonce : Option[ Boolean ] = None,
        onLoad : Option[ String ] = None,
        silentCheckSsoRedirectUri : Option[ String ] = None,
        silentCheckSsoFallback : Option[ Boolean ] = None,
        token : Option[ String ] = None,
        refreshToken : Option[ String ] = None,
        idToken : Option[ String ] = None,
        timeSkew : Option[ Int ] = None,
        checkLoginIframe : Option[ Boolean ] = None,
        checkLoginIframeInterval : Option[ Int ] = None,
        responseMode : Option[ String ] = None,
        flow : Option[ String ] = None,
        enableLogging : Option[ String ] = None,
        pkceMethod : Option[ String ] = None,
        messageReceiveTimeout : Option[ Int ] = None,
    ) : KeycloakInit = {
        val ki = ( new js.Object ).asInstanceOf[ KeycloakInit ]
        useNonce.foreach( ki.useNonce = _ )
        onLoad.foreach( ki.onLoad = _ )
        silentCheckSsoRedirectUri.foreach( ki.silentCheckSsoRedirectUri = _ )
        silentCheckSsoFallback.foreach( ki.silentCheckSsoFallback = _ )
        token.foreach( ki.token = _ )
        refreshToken.foreach( ki.refreshToken = _ )
        idToken.foreach( ki.idToken = _ )
        timeSkew.foreach( ki.timeSkew = _ )
        checkLoginIframe.foreach( ki.checkLoginIframe = _ )
        checkLoginIframeInterval.foreach( ki.checkLoginIframeInterval = _ )
        responseMode.foreach( ki.responseMode = _ )
        flow.foreach( ki.flow = _ )
        enableLogging.foreach( ki.enableLogging = _ )
        pkceMethod.foreach( ki.pkceMethod = _ )
        messageReceiveTimeout.foreach( ki.messageReceiveTimeout = _ )
        ki
    }
}

@js.native
trait KeycloakLoginOptions extends js.Object {
    var redirectUri : String = js.native // - Specifies the uri to redirect to after login.
    var prompt : String = js.native // - This parameter allows to slightly customize the login flow on the Keycloak server side. For example enforce displaying the login screen in case of value login. See Parameters Forwarding Section for the details and all the possible values of the prompt parameter.
    var maxAge : Int = js.native // - Used just if user is already authenticated. Specifies maximum time since the authentication of user happened. If user is already authenticated for longer time than maxAge, the SSO is ignored and he will need to re-authenticate again.
    var loginHint : String = js.native // - Used to pre-fill the username/email field on the login form.
    var scope : String = js.native // - Used to forward the scope parameter to the Keycloak login endpoint. Use a space-delimited list of scopes. Those typically reference Client scopes defined on particular client. Note that the scope openid will be always be added to the list of scopes by the adapter. For example, if you enter the scope options address phone, then the request to Keycloak will contain the scope parameter scope=openid address phone.
    var idpHint : String = js.native // - Used to tell Keycloak to skip showing the login page and automatically redirect to the specified identity provider instead. More info in the Identity Provider documentation.
    var action : String = js.native // - If value is register then user is redirected to registration page, otherwise to login page.
    var locale : String = js.native // - Sets the 'ui_locales' query param in compliance with section 3.1.2.1 of the OIDC 1.0 specification.
    var cordovaOptions : js.Object = js.native // - Specifies the arguments that are passed to the Cordova in-app-browser (if applicable). Options hidden and location are not affected by these arguments. All available options are defined at https://cordova.apache.org/docs/en/latest/reference/cordova-plugin-inappbrowser/. Example of use: { zoom: "no", hardwareback: "yes" };
}

object KeycloakLoginOptions {
    def apply(
        redirectUri : Option[ String ] = None, // - Specifies the uri to redirect to after login.
        prompt : Option[ String ] = None, // - This parameter allows to slightly customize the login flow on the Keycloak server side. For example enforce displaying the login screen in case of value login. See Parameters Forwarding Section for the details and all the possible values of the prompt parameter.
        maxAge : Option[ Int ] = None, // - Used just if user is already authenticated. Specifies maximum time since the authentication of user happened. If user is already authenticated for longer time than maxAge, the SSO is ignored and he will need to re-authenticate again.
        loginHint : Option[ String ] = None, // - Used to pre-fill the username/email field on the login form.
        scope : Option[ String ] = None, // - Used to forward the scope parameter to the Keycloak login endpoint. Use a space-delimited list of scopes. Those typically reference Client scopes defined on particular client. Note that the scope openid will be always be added to the list of scopes by the adapter. For example, if you enter the scope options address phone, then the request to Keycloak will contain the scope parameter scope=openid address phone.
        idpHint : Option[ String ] = None, // - Used to tell Keycloak to skip showing the login page and automatically redirect to the specified identity provider instead. More info in the Identity Provider documentation.
        action : Option[ String ] = None, // - If value is register then user is redirected to registration page, otherwise to login page.
        locale : Option[ String ] = None, // - Sets the 'ui_locales' query param in compliance with section 3.1.2.1 of the OIDC 1.0 specification.
        cordovaOptions : Option[ js.Object ] = None, // - Specifies the arguments that are passed to the Cordova in-app-browser (if applicable). Options hidden and location are not affected by these arguments. All available options are defined at https://cordova.apache.org/docs/en/latest/reference/cordova-plugin-inappbrowser/. Example of use: { zoom: "no", hardwareback: "yes" };
    ) : KeycloakLoginOptions = {
        val klo = ( new js.Object ).asInstanceOf[ KeycloakLoginOptions ]
        redirectUri.foreach( klo.redirectUri = _ )
        prompt.foreach( klo.prompt = _ )
        maxAge.foreach( klo.maxAge = _ )
        loginHint.foreach( klo.loginHint = _ )
        scope.foreach( klo.scope = _ )
        idpHint.foreach( klo.idpHint = _ )
        action.foreach( klo.action = _ )
        locale.foreach( klo.locale = _ )
        cordovaOptions.foreach( klo.cordovaOptions = _ )
        klo
    }
}

@js.native
trait KeycloakLogoutOptions extends js.Object {
    var redirectUri : String = js.native
}

object KeycloakLogoutOptions {
    def apply() : KeycloakLogoutOptions =
        ( new js.Object ).asInstanceOf[ KeycloakLogoutOptions ]

    def apply( redirectUri : String ) : KeycloakLogoutOptions = {
        val klo = apply()
        klo.redirectUri = redirectUri
        klo
    }
}

@js.native
@JSImport( "keycloak-js", JSImport.Default )
class Keycloak( params : js.UndefOr[ String | KeycloakParams ] ) extends js.Object {
    // Properties (using def to reflect changeability while ensuring read-only)
    def authenticated : Boolean = js.native
    def token : String = js.native
    def tokenParsed : js.Object = js.native
    def subject : String = js.native
    def idToken : String = js.native
    def idTokenParsed : js.Object = js.native
    def realmAccess : js.Array[ String ] = js.native
    def resourceAccess : js.Array[ String ] = js.native
    def refreshToken : String = js.native
    def refreshTokenParsed : js.Object = js.native
    def timeSkew : Int = js.native
    def responseMode : String = js.native
    def flow : String = js.native
    def adapter : String = js.native
    def responseType : String = js.native

    // Mutable events
    var onReady : js.Function1[ Boolean, Unit ] = js.native
    var onAuthSuccess : js.Function0[ Unit ] = js.native
    var onAuthError : js.Function0[ Unit ] = js.native
    var onAuthRefreshSuccess : js.Function0[ Unit ] = js.native
    var onAuthRefreshError : js.Function0[ Unit ] = js.native
    var onAuthLogout : js.Function0[ Unit ] = js.native
    var onTokenExpired : js.Function0[ Unit ] = js.native

    // Methods
    def init( keycloakInit : KeycloakInit ) : js.Promise[ Boolean ] = js.native
    def login( keycloakLoginOptions : KeycloakLoginOptions ) : Unit = js.native
    def createLoginUrl( keycloakLoginOptions : js.UndefOr[ KeycloakLoginOptions ] ) : String = js.native
    def logout( keycloakLogoutOptions : KeycloakLogoutOptions ) : Unit = js.native
    def createLogoutUrl( keycloakLogoutOptions : KeycloakLogoutOptions ) : String = js.native
    def register( keycloakLoginOptions : KeycloakLoginOptions ) : Unit = js.native
    def createRegisterUrl( keycloakLoginOptions : KeycloakLoginOptions ) : String = js.native
    def accountManagement() : Unit = js.native
    def createAccountUrl( keycloakLogoutOptions : KeycloakLogoutOptions ) : String = js.native
    def hasRealmRole( role : String ) : Boolean = js.native
    def hasResourceRole( role : String, resource : js.UndefOr[ String ] ) : Boolean = js.native
    def loadUserProfile() : js.Promise[ js.Object ] = js.native
    def isTokenExpired( minValidity : js.UndefOr[ Int ] ) : Boolean = js.native
    def updateToken( minValidity : js.UndefOr[ Int ] ) : js.Promise[ Boolean ] = js.native
    def clearToken() : Unit = js.native
}
