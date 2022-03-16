import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import { LinearProgress } from '@material-ui/core';

import styles from './uploadItemStyles';

import { connect } from '../../../../dart-ui/context/CustomConnect';

const UploadItem = (props) => {
  const {
    documentView,
    fileData,
    polledDocuments,
    style,
  } = props;

  const {
    file, progress, status, docId,
  } = fileData;

  const isComplete = status === 1;

  let ingestStatus = 'Uploaded';
  if (docId && docId in polledDocuments && polledDocuments[docId].status) {
    ingestStatus = polledDocuments[docId].status;
  }

  let progressBar = '';

  if (progress === 100 && file.name.endsWith('.zip')) {
    progressBar = (
      <Grid item xs={12}>
        <Grid container dir="row" justifyContent="center" alignItems="center">
          <Grid item xs={9} className="upload-item-progress-bar">
            <LinearProgress />
            {/* eslint-disable-next-line jsx-a11y/label-has-associated-control */}
          </Grid>
          <Grid item xs={3}>
            <Typography style={{ float: 'right' }}>
              Extracting
            </Typography>
          </Grid>
        </Grid>
      </Grid>
    );
  } else if (progress !== 100) {
    progressBar = (
      <Grid item xs={12}>
        <Grid container dir="row" alignItems="center">
          <Grid item xs={11} className="upload-item-progress-bar">
            <LinearProgress variant="determinate" value={progress} />
            {/* eslint-disable-next-line jsx-a11y/label-has-associated-control */}
          </Grid>
          <Grid item xs={1}>
            <Typography style={{ float: 'right' }}>
              {progress}
              %
            </Typography>
          </Grid>
        </Grid>
      </Grid>
    );
  }

  const docName = Boolean(ingestStatus)
    && !ingestStatus.startsWith('POLL_')
    && !ingestStatus.startsWith('No')
    && !ingestStatus.startsWith('Uploaded')
    && !ingestStatus.startsWith('Stage')
    && !ingestStatus.startsWith('Bad')
    && !ingestStatus.startsWith('Dup')
    ? (
      <Typography
        component="h2"
        variant="subtitle1"
        className="upload-item-filename"
      >
        <Button
          variant="text"
          size="small"
          onClick={() => documentView(docId)}
        >
          {file.name}
        </Button>
      </Typography>
    ) : (
      <Typography
        color="black"
        variant="subtitle1"
        component="span"
        className="upload-item-filename"
      >
        {file.name}
      </Typography>
    );

  const { classes } = props;
  // const progressBarStyle = status === 2 ? classes.progressBarFailed : classes.progressBar;
  return (
    <div
      className={`upload-item ${classes.wrapper}`}
      style={style}
    >
      <Grid container direction="row">
        {progressBar}
        <Grid item>
          {docName}
        </Grid>
        {!docId ? '' : (
          <Grid item>
            <Typography
              color="black"
              variant="subtitle1"
              component="span"
              className="upload-item-doc-id"
            >
              {`Document Id: ${docId}`}
            </Typography>
          </Grid>
        )}
        {!isComplete ? '' : (
          <Grid itexm xs={12}>
            <Typography
              color="primary"
              variant="subtitle1"
              component="span"
              className="upload-item-status"
            >
              {ingestStatus}
            </Typography>
          </Grid>
        )}
      </Grid>
    </div>
  );
};

UploadItem.propTypes = {
  documentView: PropTypes.func.isRequired,
  fileData: PropTypes.shape({
    id: PropTypes.number.isRequired,
    progress: PropTypes.number.isRequired,
    status: PropTypes.number.isRequired,
    file: PropTypes.shape({
      name: PropTypes.string.isRequired,
    }).isRequired,
    docId: PropTypes.string,
  }).isRequired,
  polledDocuments: PropTypes.objectOf(PropTypes.shape({
    status: PropTypes.string,
    fileName: PropTypes.string,
  })).isRequired,
  classes: PropTypes.shape({
    percentage: PropTypes.string.isRequired,
    wrapper: PropTypes.string.isRequired,
    progressBarFailed: PropTypes.string.isRequired,
    progressBar: PropTypes.string.isRequired,
  }).isRequired,
  style: PropTypes.shape({}).isRequired,
};

const mapStateToProps = (state, dartContext) => ({
  polledDocuments: state.forklift.polledDocuments,
  documentView: dartContext.router.documentView,
});

export default connect(mapStateToProps)(withStyles(styles)(UploadItem));
