import types from './rawView.types';

export const completeGetRaw = (res) => ({
  type: types.COMPLETE_GET_RAW,
  raw: res,
});

export const setPdfPageNumber = (pageNumber) => ({
  type: types.SET_PDF_PAGE_NUMBER,
  pageNumber,
});

export const setPdfNumPages = (numPages) => ({
  type: types.SET_PDF_NUM_PAGES,
  numPages,
});

export const setPdfScale = (scale) => ({
  type: types.SET_PDF_SCALE,
  scale,
});

export const pdfIsLoaded = () => ({
  type: types.PDF_LOADED,
});
