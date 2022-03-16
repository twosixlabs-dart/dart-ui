// eslint-disable-next-line no-unused-vars
import types from './searchDisplay.types';

const initState = {
  browseResults: true,
};

function corpexSearchDisplayReducer(state = initState, action) {
  switch (action.type) {
    case types.CHANGE_RESULTS_VIEW: {
      return {
        ...state,
        browseResults: action.browseResults,
      };
    }

    default: {
      return state;
    }
  }
}

export default corpexSearchDisplayReducer;
