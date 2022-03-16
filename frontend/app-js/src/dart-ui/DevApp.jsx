import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import { JsDartContextProvider, ReduxProvider, store } from './context/contextProvider';
import UserService from './UserService';
import getTenantsFromIdToken from '../common/utilities/getTenantsFromIdToken';
import getConfig from '../common/utilities/getConfig';
import genXhrHandler from '../common/redux/thunk/xhrHandler';
import { saveToken, setTenants } from './redux/actions/dart.actions';

import DartUi from './DartiUi';

const rootElement = document.getElementById('dart-ui');

const genContext = (viewDoc, back, keycloakData) => {
  const xhrHandler = genXhrHandler(keycloakData);
  let tenants = [];
  if (keycloakData.idTokenObj) tenants = getTenantsFromIdToken(keycloakData.idTokenObj);

  return {
    xhrHandler,
    userData: {
      save: () => {},
      retrieve: () => {},
    },
    log: {
      alert: (reportMsg = 'alert report message', logMsg = 'alert log message') => alert(reportMsg, logMsg),
      report: (reportMsg = 'report report message', logMsg = 'report log message') => alert(reportMsg, logMsg),
      log: (msg = 'Console alert message') => console.log(msg),
    },
    userName: 'dev-user',
    tenants,
    router: {
      documentView: viewDoc,
      back,
    },
    token: keycloakData.token,
  };
};

class ContextProviderWrapper extends Component {
  constructor(props) {
    super(props);
    this.state = { docView: false, documentId: '', forkliftView: false };
  }

  render() {
    const { token, children } = this.props;
    const { docView, documentId, forkliftView } = this.state;
    const viewDoc = (id) => {
      this.setState((state) => ({ ...state, docView: true, documentId: id }));
    };
    const back = () => this.setState((state) => ({ ...state, docView: false, documentId: '' }));
    console.log(token);
    const dartContext = genContext(viewDoc, back, { token });

    const gotoCorpex = () => this.setState((state) => ({
      ...state,
      forkliftView: false,
      docView: false,
      documentId: '',
    }));

    const gotoForklift = () => this.setState((state) => ({
      ...state,
      forkliftView: true,
      docView: false,
      documentId: '',
    }));

    return (
      <JsDartContextProvider dartContext={dartContext}>
        {children(docView, documentId, forkliftView, gotoCorpex, gotoForklift)}
      </JsDartContextProvider>
    );
  }
}

ContextProviderWrapper.propTypes = {
  token: PropTypes.string.isRequired,
  children: PropTypes.func.isRequired,
};

const contextStateToProps = (state) => {
  console.log(state);

  return ({
    token: state.dart.nav.token,
  });
};

const ConnectedContextProviderWrapper = connect(contextStateToProps)(ContextProviderWrapper);

const renderApp = () => {
  ReactDOM.render(
    <ReduxProvider skipInit report={console.log}>
      <ConnectedContextProviderWrapper>
        {(docView, documentId, forkliftView, gotoCorpex, gotoForklift) => (
          <DartUi
            docView={docView}
            documentId={documentId}
            forkliftView={forkliftView}
            gotoCorpex={gotoCorpex}
            gotoForklift={gotoForklift}
          />
        )}
      </ConnectedContextProviderWrapper>
    </ReduxProvider>,
    rootElement,
  );
};

const renderAppWithKeycloak = (token, idTokenObj) => {
  store.dispatch(saveToken(token, idTokenObj));
  store.dispatch(setTenants(getTenantsFromIdToken(idTokenObj)));

  UserService.setTokenRefreshHandler((newToken, newIdTokenObj) => {
    store.dispatch(saveToken(newToken, newIdTokenObj));
    store.dispatch(setTenants(getTenantsFromIdToken(idTokenObj)));
  });

  renderApp();
};

if (getConfig('common').USE_DART_AUTH) {
  console.log('initializing keycloak...');
  UserService.initKeycloak(renderAppWithKeycloak);
} else {
  renderApp();
}
