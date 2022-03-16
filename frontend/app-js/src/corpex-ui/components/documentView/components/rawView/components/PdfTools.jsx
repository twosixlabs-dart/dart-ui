/* eslint-disable react/jsx-no-bind */
import React from 'react';
import PropTypes from 'prop-types';
import Grid from '@material-ui/core/Grid';
import IconButton from '@material-ui/core/IconButton';
import ArrowBackIosOutlinedIcon from '@material-ui/icons/ArrowBackIosOutlined';
import ArrowForwardIosOutlinedIcon from '@material-ui/icons/ArrowForwardIosOutlined';
import ZoomInOutlinedIcon from '@material-ui/icons/ZoomInOutlined';
import ZoomOutOutlinedIcon from '@material-ui/icons/ZoomOutOutlined';
import GetAppOutlinedIcon from '@material-ui/icons/GetAppOutlined';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import downloadRaw from '../utilities/downloadRaw';

const useStyles = makeStyles({
  root: {
    marginBottom: 2,
  },
});

function PdfTools(props) {
  const {
    token,
    isLoaded,
    setPage,
    setScale,
    pageNumber,
    numPages,
    scale,
    url,
    rootRef,
  } = props;

  const classes = useStyles();

  function changePage(offset) {
    setPage(pageNumber + offset);
  }

  function previousPage() {
    changePage(-1);
  }

  function nextPage() {
    changePage(1);
  }

  function zoom(offset) {
    let newScale = scale + offset;
    if (newScale < 0) newScale = 0;
    setScale(newScale);
  }

  function zoomOut() {
    if (scale <= 1) zoom(-0.1);
    else zoom(-0.25);
  }

  function zoomIn() {
    if (scale < 1) zoom(0.1);
    else zoom(0.25);
  }

  return (
    <div ref={rootRef} className={classes.root}>
      <Paper>
        <Grid container direction="column" alignItems="center">
          <Grid item>
            <Grid container direction="row" spacing={3} alignItems="center">
              <Grid item>
                <Grid container direction="row" alignItems="center">
                  <Grid item>
                    <IconButton
                      onClick={previousPage}
                      disabled={pageNumber === 1 || !isLoaded}
                    >
                      <ArrowBackIosOutlinedIcon />
                    </IconButton>
                  </Grid>
                  <Grid item>
                    <Typography>{isLoaded ? `${pageNumber} / ${numPages}` : '   '}</Typography>
                  </Grid>
                  <Grid item>
                    <IconButton
                      onClick={nextPage}
                      disabled={pageNumber === numPages || !isLoaded}
                    >
                      <ArrowForwardIosOutlinedIcon />
                    </IconButton>
                  </Grid>
                </Grid>
              </Grid>
              <Grid item>
                <IconButton onClick={zoomOut} disabled={!isLoaded}>
                  <ZoomOutOutlinedIcon />
                </IconButton>
                {isLoaded ? `${Math.round(scale * 100)}%` : '   '}
                <IconButton onClick={zoomIn} disabled={!isLoaded}>
                  <ZoomInOutlinedIcon />
                </IconButton>
              </Grid>
              <Grid item>
                <IconButton
                  onClick={downloadRaw(url, token)}
                >
                  <GetAppOutlinedIcon />
                </IconButton>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Paper>
    </div>
  );
}

PdfTools.propTypes = {
  token: PropTypes.string.isRequired,
  setPage: PropTypes.func.isRequired,
  numPages: PropTypes.number.isRequired,
  pageNumber: PropTypes.number.isRequired,
  isLoaded: PropTypes.bool.isRequired,
  url: PropTypes.string.isRequired,
  scale: PropTypes.number.isRequired,
  setScale: PropTypes.func.isRequired,
  rootRef: PropTypes.shape({}).isRequired,
};

export default PdfTools;
