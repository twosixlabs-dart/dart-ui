import types from './corpexRoot.types';

export const completeGetFacetIds = (results) => ({
  type: types.COMPLETE_GET_FACET_IDS,
  results,
});

export const completeGetTagIds = (results) => ({
  type: types.COMPLETE_GET_TAG_IDS,
  results,
});

export const completeGetTagTypes = (tagId, results) => ({
  type: types.COMPLETE_GET_TAG_TYPES,
  tagId,
  results,
});

export const completeGetFieldIds = (results) => ({
  type: types.COMPLETE_GET_FIELD_IDS,
  results,
});
