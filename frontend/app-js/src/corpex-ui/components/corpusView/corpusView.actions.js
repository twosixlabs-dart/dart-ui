import types from './corpusView.types';

// eslint-disable-next-line import/prefer-default-export
export const updateCorpusOverviewAggs = (aggQueries) => ({
  type: types.CORPUS_VIEW_UPDATE_ALL_AGGS,
  aggQueries,
});

export const updateComponentState = (componentId, state) => ({
  type: types.CORPUS_VIEW_UPDATE_COMPONENT_STATE,
  componentId,
  state,
});

export const updateComponent = (componentId, data) => ({
  type: types.CORPUS_VIEW_UPDATE_COMPONENT,
  componentId,
  data,
});

export const addCorpusViewComponent = (data, componentId, sectionId) => ({
  type: types.CORPUS_VIEW_ADD_COMPONENT,
  data,
  componentId,
  sectionId,
});

export const removeCorpusViewComponent = (componentId) => ({
  type: types.CORPUS_VIEW_REMOVE_COMPONENT,
  componentId,
});

export const updateComponentMap = (componentMap) => ({
  type: types.CORPUS_VIEW_UPDATE_COMPONENT_MAP,
  componentMap,
});
