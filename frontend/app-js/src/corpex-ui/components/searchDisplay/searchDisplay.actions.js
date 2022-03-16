import types from './searchDisplay.types';

// eslint-disable-next-line import/prefer-default-export
export const changeResultsView = (browseResults) => ({
  type: types.CHANGE_RESULTS_VIEW,
  browseResults,
});
