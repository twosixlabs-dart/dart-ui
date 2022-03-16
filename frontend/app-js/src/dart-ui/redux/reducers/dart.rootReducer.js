import { combineReducers } from 'redux';
import dartNavReducer from './dart.nav.reducer';

export default combineReducers({
  nav: dartNavReducer,
});
