/* eslint-disable max-classes-per-file */

import { applyMiddleware, compose, createStore } from 'redux';
import thunkMiddleware from 'redux-thunk';
import React, { Component } from 'react';
import { Provider } from 'react-redux';
import PropTypes from 'prop-types';

import { subscription, updateStateFromSection } from '../../common/utilities/localStorage';
import { rootReducer, SET_STATE_DIRECTLY } from '../../rootReducer';
import AbsoluteCentered from '../../common/components/layout/AbsoluteCentered';

const composeEnchancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

const initialState = rootReducer(undefined, { type: null });

const store = createStore(
  rootReducer,
  initialState,
  composeEnchancers(applyMiddleware(thunkMiddleware)),
);

class ReduxProvider extends Component {
  constructor(props) {
    super(props);
    const { skipInit } = props;
    this.state = { stateIsInitialized: skipInit };
  }

  componentDidMount() {
    const {
      props: {
        stateKey,
        save,
        retrieve,
        report,
        skipInit,
      },
    } = this;

    if (skipInit) return;

    retrieve(
      stateKey,
      (res) => {
        let stateSection = null;
        if (typeof (res) === 'string') {
          try {
            stateSection = JSON.parse(res);
          } catch (e) {
            report("Couldn't parse user state", `Failed to parse user state: ${e}`);
          }
        } else {
          stateSection = res;
        }

        const updatedState = updateStateFromSection(initialState, stateSection);
        store.dispatch({ type: SET_STATE_DIRECTLY, state: updatedState });
        this.setState({ stateIsInitialized: true });
      },
      (failRes) => {
        report('Could not retrieve user state', `Failed to retrieve user state: ${failRes}`);
        this.setState({ stateIsInitialized: true });
      },
      // To skip initialization
      () => this.setState({ stateIsInitialized: true }),
    );

    const stateSaver = (state) => {
      save(
        stateKey,
        state,
        () => {
        },
        (msg) => report('Unable to persist user state', `Failed to persist user state: ${msg}`),
      );
    };

    store.subscribe(subscription(store, stateSaver));
  }

  render() {
    const { children, loader } = this.props;
    const { stateIsInitialized } = this.state;

    if (stateIsInitialized) {
      return (
        <Provider store={store}>
          {children}
        </Provider>
      );
    }

    return (
      <AbsoluteCentered>
        {loader}
      </AbsoluteCentered>
    );
  }
}

ReduxProvider.propTypes = {
  stateKey: PropTypes.string,
  save: PropTypes.func,
  retrieve: PropTypes.func,
  report: PropTypes.func.isRequired,
  children: PropTypes.node.isRequired,
  skipInit: PropTypes.bool,
  loader: PropTypes.element.isRequired,
};

ReduxProvider.defaultProps = {
  stateKey: '',
  // By default, init using save/retrieve
  skipInit: false,
  // If not set, do nothing
  save: () => {},
  retrieve: () => {},
};

const ctx = React.createContext({});

class JsDartContextProvider extends Component {
  render() {
    const { dartContext, children } = this.props;

    return (
      <ctx.Provider value={dartContext}>
        {children}
      </ctx.Provider>
    );
  }
}

JsDartContextProvider.propTypes = {
  dartContext: PropTypes.shape({}).isRequired,
  children: PropTypes.node.isRequired,
};

export {
  store,
  ctx,
  ReduxProvider,
  JsDartContextProvider,
};
