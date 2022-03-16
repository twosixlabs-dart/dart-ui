import { toPairs } from 'lodash';

import types from './searchResults.types';

const initState = {
  searchQueries: [],
  searchAggQueries: {},
  corpusOverviewAggQueries: {},
  searchExecuted: false,
  searchPending: false,
  aggResults: {},
  searchResults: {
    num_results: 0,
    results_per_page: 0,
    num_pages: 0,
    page: 0,
    results: [],
    aggregations: {},
  },
  searchError: false,
  errorMessage: '',
  countExecuted: false,
  countPending: false,
  count: null,
  document: null,
};

function corpexSearchResultsReducer(state = initState, action) {
  switch (action.type) {
    case types.UPDATE_SEARCH_QUERIES: {
      return {
        ...state,
        searchQueries: action.payload,
      };
    }

    case types.START_SEARCH: {
      return {
        ...state,
        searchExecuted: true,
        searchPending: true,
        searchError: false,
      };
    }

    case types.COMPLETE_SEARCH: {
      const newAggResults = { ...state.aggResults };
      let aggSearchResults = {};
      if (action.results) {
        aggSearchResults = action.results.aggregations ? action.results.aggregations : {};
      }

      toPairs(aggSearchResults).forEach(([label, res]) => { newAggResults[label] = res; });

      if (action.queries) {
        return {
          ...state,
          searchPending: false,
          searchAggQueries: action.aggQueries,
          aggResults: newAggResults,
          searchResults: action.results,
          searchQueries: action.queries,
        };
      }

      return {
        ...state,
        searchPending: false,
        aggResults: newAggResults,
        searchResults: action.results,
      };
    }

    case types.SEARCH_ERROR: {
      return {
        ...state,
        searchError: true,
        errorMessage: (action.payload === null || action.payload === '') ? 'Error' : action.payload,
        searchPending: false,
        searchResults: {
          ...state.searchResults,
          results: [],
        },
      };
    }

    case types.START_COUNT: {
      return {
        ...state,
        searchQuery: null,
        searchPending: false,
        searchError: false,
        errorMessage: '',
        countExecuted: true,
        countPending: true,
      };
    }

    case types.COMPLETE_COUNT: {
      return {
        ...state,
        countPending: false,
        count: action.payload,
      };
    }

    case types.TOGGLE_SEARCH_RESULT_EXPANSION: {
      const newResults = state.searchResults.results;
      newResults[action.payload].expanded = !newResults[action.payload].expanded;

      return {
        ...state,
        searchResults: {
          ...state.searchResults,
          results: newResults,
        },
      };
    }

    case types.COMPLETE_FACETS_QUERY: {
      const newResults = state.searchResults.results;
      const newRes = newResults[action.resIndex];
      if (newRes.cdr.document_id !== action.docId) return state;

      const facetAnnotations = action.annotations.annotations.filter((annotation) => annotation.type === 'facets');
      const facets = {};
      facetAnnotations.forEach((annotation) => { facets[annotation.label] = annotation.content; });
      newRes.facets = facets;
      newResults[action.resIndex] = newRes;

      return {
        ...state,
        searchResults: {
          ...state.searchResults,
          results: newResults,
        },
      };
    }

    case types.COMPLETE_AGGREGATIONS_QUERY: {
      const newResults = state.searchResults.results;
      const newRes = newResults[action.resIndex];
      if (newRes.cdr.document_id !== action.docId) return state;

      newRes.aggregations = action.aggregations;
      newResults[action.resIndex] = newRes;

      return {
        ...state,
        searchResults: {
          ...state.searchResults,
          results: newResults,
        },
      };
    }

    case types.COMPLETE_AGG_SEARCH: {
      const newAggResults = { ...state.aggResults };
      let aggSearchResults = {};
      if (action.results) {
        aggSearchResults = action.results.aggregations ? action.results.aggregations : {};
      }

      toPairs(aggSearchResults).forEach(([label, res]) => { newAggResults[label] = res; });

      return {
        ...state,
        searchAggQueries: action.aggQueries ? action.aggQueries : state.searchAggQueries,
        aggResults: newAggResults,
      };
    }

    case types.CLEAR_CORPUS_OVERVIEW_AGG_QUERIES: {
      return {
        ...state,
        corpusOverviewAggQueries: initState.corpusOverviewAggQueries,
      };
    }

    case types.ADD_CORPUS_OVERVIEW_AGG_QUERIES: {
      const newCorpusOverviewAggQueries = {
        ...state.corpusOverviewAggQueries,
        ...action.aggQueries,
      };

      return {
        ...state,
        corpusOverviewAggQueries: newCorpusOverviewAggQueries,
      };
    }

    default: {
      return state;
    }
  }
}

export default corpexSearchResultsReducer;
