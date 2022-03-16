import types from './searchResults.types';

export const startSearch = (query) => ({
  type: types.START_SEARCH,
  payload: query,
});

export const completeSearch = (results, queries, aggs) => ({
  type: types.COMPLETE_SEARCH,
  results,
  queries,
  aggQueries: aggs,
});

export const searchError = (message) => ({
  type: types.SEARCH_ERROR,
  payload: message,
});

export const startCount = () => ({
  type: types.START_COUNT,
});

export const completeCount = (xhrResult) => ({
  type: types.COMPLETE_COUNT,
  payload: xhrResult.num_results,
});

export const toggleSearchResultExpansion = (resIndex) => ({
  type: types.TOGGLE_SEARCH_RESULT_EXPANSION,
  payload: resIndex,
});

export const completeAggregationsQuery = (resIndex, docId, res) => ({
  type: types.COMPLETE_AGGREGATIONS_QUERY,
  resIndex,
  docId,
  aggregations: res,
});

export const completeFacetsQuery = (resIndex, docId, res) => ({
  type: types.COMPLETE_FACETS_QUERY,
  resIndex,
  docId,
  annotations: res,
});

export const completeAggSearch = (res, aggs) => ({
  type: types.COMPLETE_AGG_SEARCH,
  results: res,
  aggQueries: aggs,
});

export const clearCorpusOverviewAggQueries = () => ({
  type: types.CLEAR_CORPUS_OVERVIEW_AGG_QUERIES,
});

export const addCorpusOverviewAggQueries = (aggQueries) => ({
  type: types.ADD_CORPUS_OVERVIEW_AGG_QUERIES,
  aggQueries,
});
