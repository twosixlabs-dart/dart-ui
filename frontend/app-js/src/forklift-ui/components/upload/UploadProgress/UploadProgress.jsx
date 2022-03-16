import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

import { size, range } from 'lodash';

import { List } from 'react-virtualized';

import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';

import UploadItem from '../UploadItem/UploadItem';
import uploadFiles from '../../../redux/thunk/uploadFiles.thunk';
import styles from './uploadProgressStyles';
import pollStatus from '../../../redux/thunk/pollStatus.thunk';
import { setPollWindow, stopPolling } from '../../../redux/actions/uploadFiles.actions';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const UploadProgress = (props) => {
  const {
    filesData,
    dispatch,
    numOfFilesSuccessUpload,
    numOfFilesFailedUpload,
    isUploading,
    polledDocuments,
    pollWindow,
    classes,
    xhrHandler,
  } = props;
  const numFilesToUpload = size(filesData);
  const isComplete = numFilesToUpload === numOfFilesFailedUpload + numOfFilesSuccessUpload;

  let docList = [];

  if (numFilesToUpload < 1) {
    docList = Object.keys(polledDocuments)
    // eslint-disable-next-line max-len
      .sort((a, b) => (polledDocuments[a].timestamp >= polledDocuments[b].timestamp ? -1 : 1))
      .map((id) => ({
        docId: id,
        status: 1,
        progress: 100,
        file: { name: polledDocuments[id].fileName },
      }));
  } else {
    docList = isComplete
      ? Object.keys(polledDocuments)
        .filter((id) => polledDocuments[id].isCurrentUpload === true)
        .sort((a, b) => (polledDocuments[a].timestamp >= polledDocuments[b].timestamp ? -1 : 1))
        .map((id) => ({
          docId: id,
          status: 1,
          progress: 100,
          file: { name: polledDocuments[id].fileName },
        })) : Object.values(filesData);
  }
  const [indexWindow, setIndexWindow] = useState([]);
  const newPollWindow = indexWindow.flatMap((i) => (
    docList[i] && docList[i].docId ? [docList[i].docId] : []
  ));

  if (newPollWindow.length !== pollWindow.length
    || newPollWindow.some((id) => !pollWindow.includes(id))) {
    dispatch(setPollWindow(newPollWindow));
  }

  useEffect(() => {
    if (isUploading) {
      dispatch(uploadFiles(xhrHandler));
    }
  }, [isUploading, numOfFilesSuccessUpload, numOfFilesFailedUpload]);

  // eslint-disable-next-line consistent-return
  useEffect(() => {
    if (isComplete) {
      dispatch(pollStatus(xhrHandler));
      return () => dispatch(stopPolling());
    }
  }, []);

  function GenPollWindowCallback() {
    return (
      {
        overscanStartIndex,
        overscanStopIndex,
        // startIndex,
        // stopIndex
      },
    ) => {
      setIndexWindow(range(overscanStartIndex, overscanStopIndex + 1));
    };
  }

  function GenUploadItemRenderer(fileDataList) {
    // eslint-disable-next-line react/prop-types
    return ({ index, key, style }) => (
      <UploadItem
        key={key}
        fileData={fileDataList[index]}
        style={style}
      />
    );
  }

  function lazyListOf(list) {
    return (
      <List
        height={580}
        width={450}
        rowCount={list.length}
        rowHeight={100}
        rowRenderer={GenUploadItemRenderer(list)}
        style={{ outline: 'none' }}
        onRowsRendered={GenPollWindowCallback(list)}
      />
    );
  }

  const lazyListElement = lazyListOf(docList);

  let uploadingHeader = (
    <Grid item xs={12}>
      {isUploading ? (
        <Typography component="h3" variant="h6" color="textPrimary" gutterBottom>
          Uploading Files
        </Typography>
      ) : (
        <Typography component="h3" variant="h6" color="textPrimary" gutterBottom>
          {`${numFilesToUpload === 1 ? '1 file' : `${numFilesToUpload} files`} selected for upload`}
        </Typography>
      )}
    </Grid>
  );

  uploadingHeader = isComplete ? (
    <Typography component="h3" variant="h6" color="textPrimary" gutterBottom>
      {`Uploaded ${numFilesToUpload}`}
    </Typography>
  ) : uploadingHeader;

  const progressElements = isComplete ? (
    <Grid item xs={12}>
      <div className={classes.wrapper}>
        {size(filesData)
          ? lazyListElement : ''}
      </div>
    </Grid>
  ) : (
    <Grid item xs={12}>
      <div className={classes.wrapper}>
        {size(filesData)
          ? lazyListElement : ''}
      </div>
    </Grid>
  );

  const uploadingElement = (
    <Grid container direction="column" alignItems="stretch" spacing={2}>
      <Grid item xs={12}>
        <Grid container direction="column" alignItems="center">
          {uploadingHeader}
          <Grid item xs={12}>
            <Typography component="h4" variant="subtitle1" color="textPrimary" gutterBottom>
              Success:
              {' '}
              {numOfFilesSuccessUpload}
              {' '}
              Failed:
              {numOfFilesFailedUpload}
            </Typography>
          </Grid>
          {isUploading ? (
            <Grid item xs={12}>
              <Typography component="h4" variant="subtitle1" color="textPrimary" gutterBottom>
                Total:
                {' '}
                {numFilesToUpload}
              </Typography>
            </Grid>
          ) : ''}
          {progressElements}
        </Grid>
      </Grid>
    </Grid>
  );

  const uploadedFilesStatuses = (
    <Grid container direction="column" alignItems="stretch" spacing={2}>
      <Grid item xs={12}>
        <Grid container direction="column" alignItems="center">
          <Typography component="h3" variant="h6" color="textPrimary" gutterBottom>
            All Uploaded Files
          </Typography>
          <Grid item xs={12}>
            <div className={classes.wrapper}>
              {lazyListElement}
            </div>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );

  return (
    <div>
      {numFilesToUpload < 1 ? (
        <Grid container direction="column" alignItems="center">
          {/* <Typography component="h3" variant="h6" color="textPrimary" gutterBottom> */}
          {/*   No files selected */}
          {/* </Typography> */}
          {uploadedFilesStatuses}
        </Grid>
      ) : uploadingElement}
    </div>
  );
};

const mapStateToProps = (state, dartContext) => ({
  filesData: state.forklift.filesData,
  numOfFilesFailedUpload: state.forklift.numOfFilesFailedUpload,
  numOfFilesSuccessUpload: state.forklift.numOfFilesSuccessUpload,
  isUploading: state.forklift.isUploading,
  polledDocuments: state.forklift.polledDocuments,
  isPolling: state.forklift.isPolling,
  pollWindow: state.forklift.pollWindow,
  xhrHandler: dartContext.xhrHandler,
});

UploadProgress.propTypes = {
  filesData: PropTypes.objectOf(PropTypes.shape({
    status: PropTypes.number,
  })).isRequired,
  dispatch: PropTypes.func.isRequired,
  numOfFilesSuccessUpload: PropTypes.number.isRequired,
  numOfFilesFailedUpload: PropTypes.number.isRequired,
  isUploading: PropTypes.bool.isRequired,
  // isPolling: PropTypes.bool.isRequired,
  polledDocuments: PropTypes.objectOf(PropTypes.shape({
    status: PropTypes.string,
    fileName: PropTypes.string,
    timestamp: PropTypes.number,
    isCurrentUpload: PropTypes.bool.isRequired,
  })).isRequired,
  pollWindow: PropTypes.arrayOf(PropTypes.number),
  classes: PropTypes.shape({
    wrapper: PropTypes.string.isRequired,
  }).isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

UploadProgress.defaultProps = {
  pollWindow: [],
};

export default connect(mapStateToProps, null)(withStyles(styles)(UploadProgress));
