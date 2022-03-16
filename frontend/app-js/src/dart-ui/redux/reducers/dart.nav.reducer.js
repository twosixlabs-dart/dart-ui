import { CHOOSE_TENANT, SAVE_TOKEN } from '../actions/dart.types';

const initState = {
  tenantId: null,
  // FOR RAW-JS LOCAL DEV ENV ONLY
  token: null,
  idTokenObj: {},
};

function dartNavReducer(state = initState, action) {
  switch (action.type) {
    case CHOOSE_TENANT: {
      return {
        ...state,
        tenantId: action.tenantId,
      };
    }

    case SAVE_TOKEN: {
      const newState = {
        ...state,
        token: action.token,
        idTokenObj: action.idTokenObj,
      };
      console.log(newState);
      return newState;
    }

    default: {
      return state;
    }
  }
}

export default dartNavReducer;
