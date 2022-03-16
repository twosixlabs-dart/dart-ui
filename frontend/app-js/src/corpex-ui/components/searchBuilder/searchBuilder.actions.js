import types from './searchBuilder.types';

export const updateRootComponentMap = (newMap) => ({
  type: types.UPDATE_ROOT_COMPONENT_MAP,
  componentMap: newMap,
});

export const completeTagTypeQuery = (componentId, tagTypes) => ({
  type: types.COMPLETE_TAG_TYPE_QUERY,
  tagTypes,
  componentId,
});

export const completeFacetQuery = (componentId, facetObj) => ({
  type: types.COMPLETE_FACET_QUERY,
  facetObj,
  componentId,
});

export const addComponent = (id, type) => ({
  type: types.ADD_COMPONENT,
  id,
  componentType: type,
});

export const removeComponent = (address) => ({
  type: types.REMOVE_COMPONENT,
  address,
});

export const updateBoolType = (id, boolType) => ({
  type: types.UPDATE_COMPONENT_BOOL_TYPE,
  id,
  boolType,
});

export const toggleEdited = (id) => ({
  type: types.TOGGLE_COMPONENT_EDITED,
  id,
});

export const updateComponent = (
  id,
  newState,
  isActive,
  query,
  commonAggQueries,
  privateAggQueries,
  summary,
) => ({
  type: types.UPDATE_COMPONENT_STATE,
  id,
  newState,
  isActive,
  query,
  commonAggQueries,
  privateAggQueries,
  summary,
});
