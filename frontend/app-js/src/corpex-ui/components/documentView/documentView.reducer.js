import { combineReducers } from 'redux';
import types from './documentView.types';
import corpexCdrViewReducer from './components/cdrView/cdrView.reducer';
import corpexRawViewReducer from './components/rawView/rawView.reducer';
import corpexExtractionsViewReducer from './components/cdrView/components/extractionsView/extractionsView.reducer';
import { aggs, facets } from '../../utilities/cdrReader';

const initState = {
  wordCount: null,
  aggregations: {},
  facets: {},
  error: '',
  cdr: null,
  view: 'cdr',
};

function corpexDocumentViewWrapperReducer(state = initState, action) {
  switch (action.type) {
    case types.DOC_VIEW_CLEAR_STATE: {
      return initState;
    }

    case types.DOC_VIEW_GET_CDR_FAILURE: {
      const { response } = action;
      let newError = '';
      if (response === 'Network Error') newError = 'connection';
      if (typeof response === 'object' && response !== null) {
        if (response.status === 404) newError = '404';
        else newError = 'failure';
      }

      return {
        ...state,
        error: newError,
      };
    }

    case types.COMPLETE_WORD_COUNT_QUERY: {
      if (action.wordCountResults.results[0]) {
        return {
          ...state,
          wordCount: action.wordCountResults.results[0].word_count,
        };
      }

      return state;
    }

    case types.COMPLETE_DOC_VIEW_AGGREGATIONS_QUERY: {
      return {
        ...state,
        aggregations: aggs({ aggregations: action.aggregations }),
      };
    }

    case types.COMPLETE_DOC_VIEW_FACETS_QUERY: {
      return {
        ...state,
        facets: facets(action.annotations),
      };
    }

    case types.COMPLETE_GET_CDR: {
      return {
        ...state,
        cdr: action.cdr,
      };
    }

    case types.SET_DOCUMENT_VIEW: {
      const newScrollTo = {
        ...state.scrollTo,
      };
      if (state.view !== 'cdr' && action.view === 'cdr') {
        newScrollTo.textScroll = 0;
      }

      return {
        ...state,
        view: action.view,
        scrollTo: newScrollTo,
      };
    }

    default: {
      return state;
    }
  }
}

export default combineReducers({
  root: corpexDocumentViewWrapperReducer,
  cdrView: corpexCdrViewReducer,
  rawView: corpexRawViewReducer,
  extractionsView: corpexExtractionsViewReducer,
});
