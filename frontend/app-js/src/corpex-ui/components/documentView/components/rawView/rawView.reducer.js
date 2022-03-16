import types from './rawView.types';
import docViewTypes from '../../documentView.types';

const initState = {
  pageNumber: 1,
  scale: 1,
  isLoaded: false,
  data: null,
};

function corpexRawViewReducer(state = initState, action) {
  switch (action.type) {
    case docViewTypes.DOC_VIEW_CLEAR_STATE: {
      return initState;
    }

    case docViewTypes.COMPLETE_GET_CDR: {
      return initState;
    }

    case types.COMPLETE_GET_RAW: {
      return {
        ...state,
        data: action.raw,
      };
    }

    case types.SET_PDF_PAGE_NUMBER: {
      return {
        ...state,
        pageNumber: action.pageNumber,
      };
    }

    case types.SET_PDF_NUM_PAGES: {
      return {
        ...state,
        numPages: action.numPages,
      };
    }

    case types.SET_PDF_SCALE: {
      return {
        ...state,
        scale: action.scale,
      };
    }

    case types.PDF_LOADED: {
      return {
        ...state,
        isLoaded: true,
      };
    }

    default: {
      return state;
    }
  }
}

export default corpexRawViewReducer;
