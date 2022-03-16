/* eslint-disable react/jsx-no-bind */
import React, { useLayoutEffect, useRef, useState } from 'react';
import PropTypes from 'prop-types';

import { Document, Page } from 'react-pdf/dist/entry.webpack';

import { makeStyles } from '@material-ui/core/styles';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

import PdfTools from './PdfTools';
import downloadRaw from '../utilities/downloadRaw';
import { USE_DART_AUTH } from '../../../../../../common/config/constants';

const useStyles = makeStyles({
  root: {
    height: '100%',
    overflowY: 'hidden',
    backgroundColor: 'darkgrey',
  },
  documentWrapper: {
    height: 'calc(100% - 50px)',
    margin: 'auto',
    overflowY: 'hidden',
  },
  document: {
    display: 'flex',
    height: '100%',
    overflow: 'auto',
    alignItems: 'center',
    justifyContent: 'center',
  },
  page: {
    margin: 'auto',
  },
  errorPaper: {
    padding: 20,
    margin: 'auto',
  },
});

function PdfViewer(props) {
  const {
    token,
    isLoaded,
    setIsLoaded,
    url,
    setNumPages,
    setPage,
    pageNumber,
    scale,
    setScale,
    numPages,
  } = props;

  const thisRef = useRef(null);
  const toolbarRef = useRef(null);

  const [state, setState] = useState({
    windowHeight: 350,
    toolbarHeight: 50,
  });

  const { windowHeight, toolbarHeight } = state;
  const documentHeight = windowHeight - toolbarHeight - 2;

  const setWindowHeight = (newHeight) => setState((oldState) => ({
    ...oldState,
    windowHeight: newHeight,
  }));

  const setToolbarHeight = (newHeight) => setState((oldState) => ({
    ...oldState,
    toolbarHeight: newHeight,
  }));

  useLayoutEffect(() => {
    if (thisRef.current.clientHeight !== windowHeight) {
      setWindowHeight(thisRef.current.clientHeight);
    }
    if (toolbarRef.current.clientHeight !== toolbarHeight) {
      setToolbarHeight(toolbarRef.current.clientHeight);
    }
  });

  const classes = useStyles();

  function onDocumentLoadSuccess(loadObj) {
    setIsLoaded();
    setNumPages(loadObj.numPages);
  }

  const httpHeaders = {};
  if (USE_DART_AUTH) httpHeaders.Authorization = `Bearer ${token}`;

  return (
    <div className={`pdf-viewer ${classes.root}`} ref={thisRef}>
      <PdfTools
        token={token}
        isLoaded={isLoaded}
        numPages={numPages}
        pageNumber={pageNumber}
        setPage={setPage}
        url={url}
        scale={scale}
        setScale={setScale}
        rootRef={toolbarRef}
      />
      <div className={classes.documentWrapper}>
        <Document
          file={{
            url,
            httpHeaders,
          }}
          options={{ withCredentials: true }}
          onLoadSuccess={onDocumentLoadSuccess}
          className={classes.document}
          loading={<div />}
          error={(
            <div className={classes.document}>
              <Paper className={classes.errorPaper}>
                <Typography>
                  <span>Raw document is not a pdf. </span>
                  <Button onClick={downloadRaw(url, token)}>Click here to download.</Button>
                </Typography>
              </Paper>
            </div>
          )}
        >
          <Page
            pageNumber={pageNumber}
            scale={scale}
            height={documentHeight}
            className={classes.page}
            loading={<div />}
          />
        </Document>
      </div>
    </div>
  );
}

PdfViewer.propTypes = {
  token: PropTypes.string.isRequired,
  isLoaded: PropTypes.bool.isRequired,
  setIsLoaded: PropTypes.func.isRequired,
  pageNumber: PropTypes.number.isRequired,
  numPages: PropTypes.number.isRequired,
  url: PropTypes.string.isRequired,
  setNumPages: PropTypes.func.isRequired,
  setPage: PropTypes.func.isRequired,
  scale: PropTypes.number.isRequired,
  setScale: PropTypes.func.isRequired,
};

export default PdfViewer;
