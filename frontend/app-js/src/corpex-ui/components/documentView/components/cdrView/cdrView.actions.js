import types from './cdrView.types';

export const focusTag = (window, tagType, offset, checkedTagTypes) => ({
  type: types.FOCUS_TAG,
  window,
  tagType,
  offset,
  checkedTagTypes,
});

export const unfocusTag = (tagType, offset) => ({
  type: types.UNFOCUS_TAG,
  tagType,
  offset,
});

export const hoverTag = (window, tagType, offset) => ({
  type: types.HOVER_TAG,
  window,
  tagType,
  offset,
});

export const unhoverTag = () => ({
  type: types.UNHOVER_TAG,
});

export const completeScrollToIndex = () => ({
  type: types.COMPLETE_SCROLL_TO_INDEX,
});

export const setScrollToView = (offset) => ({
  type: types.SET_SCROLL_TO_VIEW,
  offset,
});

export const completeScrollTo = (thenReturn) => ({
  type: types.COMPLETE_SCROLL_TO,
  thenReturn,
});

export const clearCdrView = () => ({
  type: types.CLEAR_CDR_VIEW,
});

export const setCdrTextArray = (textArray) => ({
  type: types.SET_CDR_TEXT_ARRAY,
  textArray,
});
