import { combineReducers } from 'redux';
import forkliftReducer from './forklift-ui/redux/reducers/uploadFiles.rootReducer';
import corpexReducer from './corpex-ui/corpex.rootReducer';
import dartReducer from './dart-ui/redux/reducers/dart.rootReducer';
// import docStatusReducer from './doc-status/redux/reducers/docStatus.rootReducer';

const appReducers = combineReducers({
  dart: dartReducer,
  corpex: corpexReducer,
  forklift: forkliftReducer,
});

export const SET_STATE_DIRECTLY = 'SET_STATE_DIRECTLY';

export const rootReducer = (state, action) => {
  switch (action.type) {
    case SET_STATE_DIRECTLY: {
      if (action.state === null || action.state === undefined) return state;
      return action.state;
    }

    default: {
      return appReducers(state, action);
    }
  }
};
