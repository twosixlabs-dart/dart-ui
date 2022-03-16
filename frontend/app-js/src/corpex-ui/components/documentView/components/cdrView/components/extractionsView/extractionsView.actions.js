import types from './extractionsView.types';

export const highlightExtractionTags = (tagType, offsets) => ({
  type: types.HIGHLIGHT_EXTRACTION_TAGS,
  tagType,
  offsets,
});

export const removeExtractionTagsHighlight = (tagType) => ({
  type: types.REMOVE_EXTRACTION_TAGS_HIGHLIGHT,
  tagType,
});

export const expandTagType = (tagType) => ({
  type: types.EXPAND_TAG_TYPE,
  tagType,
});

export const unExpandTagType = (tagType) => ({
  type: types.UN_EXPAND_TAG_TYPE,
  tagType,
});

export const expandExtractionComponent = (extrType) => ({
  type: types.EXPAND_EXTRACTION_COMPONENT,
  extrType,
});

export const unExpandExtractionComponent = (extrType) => ({
  type: types.UN_EXPAND_EXTRACTION_COMPONENT,
  extrType,
});

export const registerTagType = (extrType, tagType, offsets, textArray) => ({
  type: types.REGISTER_TAG_TYPE,
  extrType,
  tagType,
  offsets,
  textArray,
});

export const setTagState = (extrType, tagType, offset, stateUpdater) => ({
  type: types.SET_TAG_STATE,
  extrType,
  tagType,
  offset,
  stateUpdater,
});
