import types from './documentView.types';

export const docViewClearState = () => ({
  type: types.DOC_VIEW_CLEAR_STATE,
});

export const docViewGetCdrFailure = (response) => ({
  type: types.DOC_VIEW_GET_CDR_FAILURE,
  response,
});

export const completeWordCountQuery = (wordCountResults) => ({
  type: types.COMPLETE_WORD_COUNT_QUERY,
  wordCountResults,
});
export const completeDocViewAggregationsQuery = (aggregations) => ({
  type: types.COMPLETE_DOC_VIEW_AGGREGATIONS_QUERY,
  aggregations,
});

export const completeDocViewFacetsQuery = (annotations) => ({
  type: types.COMPLETE_DOC_VIEW_FACETS_QUERY,
  annotations,
});

export const completeGetCdr = (res) => ({
  type: types.COMPLETE_GET_CDR,
  cdr: res,
});

export const setDocumentView = (view) => ({
  type: types.SET_DOCUMENT_VIEW,
  view,
});
