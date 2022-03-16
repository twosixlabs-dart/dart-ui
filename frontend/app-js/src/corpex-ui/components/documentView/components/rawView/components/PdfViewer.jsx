import React from 'react';
import PropTypes from 'prop-types';

import {
  pdfIsLoaded,
  setPdfNumPages,
  setPdfPageNumber,
  setPdfScale,
} from '../rawView.actions';
import getUrl from '../utilities/getUrl';
import PdfComponent from './PdfComponent';
import { connect } from '../../../../../../dart-ui/context/CustomConnect';

function PdfViewer(props) {
  const {
    token,
    tenantId,
    isLoaded,
    docId,
    pageNumber,
    numPages,
    dispatch,
    scale,
  } = props;

  const setNumPaqes = (num) => dispatch(setPdfNumPages(num));
  const setPageNum = (num) => dispatch(setPdfPageNumber(num));
  const setScale = (newScale) => dispatch(setPdfScale(newScale));
  const setIsLoaded = () => dispatch(pdfIsLoaded());

  const tenantQuery = tenantId ? `?tenantId=${tenantId}` : '';

  return (
    <PdfComponent
      token={token}
      isLoaded={isLoaded}
      setIsLoaded={setIsLoaded}
      url={`${getUrl(docId)}${tenantQuery}`}
      setNumPages={setNumPaqes}
      pageNumber={pageNumber}
      scale={scale}
      setScale={setScale}
      numPages={numPages}
      setPage={setPageNum}
    />
  );
}

PdfViewer.propTypes = {
  token: PropTypes.string.isRequired,
  isLoaded: PropTypes.bool.isRequired,
  docId: PropTypes.string.isRequired,
  scale: PropTypes.number.isRequired,
  pageNumber: PropTypes.number.isRequired,
  numPages: PropTypes.number.isRequired,
  dispatch: PropTypes.func.isRequired,
  tenantId: PropTypes.string.isRequired,
};

const mapStateToProps = (state, dartContext) => ({
  token: dartContext.token,
  scale: state.corpex.documentView.rawView.scale,
  pageNumber: state.corpex.documentView.rawView.pageNumber,
  numPages: state.corpex.documentView.rawView.numPages,
  isLoaded: state.corpex.documentView.rawView.isLoaded,
  tenantId: state.dart.nav.tenantId,
});

export default connect(mapStateToProps)(PdfViewer);
