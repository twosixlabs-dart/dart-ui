import React, { Component } from 'react';
import PropTypes from 'prop-types';
import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import { setUploadFiles, startUploadFiles } from '../../redux/actions/uploadFiles.actions';

import { connect } from '../../../dart-ui/context/CustomConnect';

const styles = () => ({
  paper: {
    padding: 10,
  },
  container: {
    margin: 'auto',
    marginTop: 24,
  },
  fileInput: {
    display: 'none',
  },
  spaceOnRight: {
    marginRight: 25,
  },
  padded: {
    padding: 20,
  },
  extraPadding: {
    paddingTop: 10,
    paddingBottom: 10,
  },
});

class UploadForm extends Component {
  constructor(props) {
    super(props);
    this.fileUploadProcessor = this.fileUploadProcessor.bind(this);
    this.uploadFiles = this.uploadFiles.bind(this);
  }

    fileUploadProcessor = (event) => {
      const { dispatchSetUploadFiles } = this.props;
      dispatchSetUploadFiles(event.target.files);
      // eslint-disable-next-line no-param-reassign
      if (event.target) event.target.value = '';
    };

    uploadFiles = (event) => {
      const { dispatchStartUploadFiles } = this.props;
      event.preventDefault();
      dispatchStartUploadFiles();
      // eslint-disable-next-line no-param-reassign
      if (event.target) event.target.value = '';
    };

    render() {
      const {
        filesData,
        labelsText,
        isUploading,
        classes,
      } = this.props;

      const submitHandler = (ev) => {
        if (labelsText.length === 0) this.uploadFiles(ev);
        // eslint-disable-next-line no-alert
        else alert('It looks like you have not added a label. Press enter in the "labels" field to add the label, or delete the current text.');
      };

      const activeFiles = {};
      Object.keys(filesData).forEach((key) => {
        if (filesData[key].status !== null
          && filesData[key].status !== undefined
          && filesData[key].status !== 1) {
          activeFiles[key] = filesData[key];
        }
      });
      const numFiles = Object.keys(activeFiles).length;
      const noFiles = numFiles === 0;

      return (
        <Grid
          item
          xs={12}
          className="upload-form"
        >
          <label htmlFor="fileSelector">
            <input
              className={`upload-form-file-input ${classes.fileInput}`}
              id="fileSelector"
              name="fileSelector"
              type="file"
              multiple
              onChange={this.fileUploadProcessor}
              disabled={isUploading}
            />
            <Button
              component="span"
              variant="contained"
              className={`upload-form-file-input-button ${classes.spaceOnRight}`}
            >
              CHOOSE FILES
            </Button>
          </label>
          <Button
            onClick={submitHandler}
            variant="contained"
            disabled={isUploading || noFiles}
            className="upload-form-submit-button"
          >
            SUBMIT
          </Button>
        </Grid>
      );
    }
}

const mapDispatchToProps = (dispatch) => ({
  dispatchSetUploadFiles: (files, metaData) => {
    dispatch(setUploadFiles(files, metaData));
  },
  dispatchStartUploadFiles: () => {
    dispatch(startUploadFiles());
  },

});

function mapStateToProps(state) {
  return {
    isUploading: state.forklift.isUploading,
    filesData: state.forklift.filesData,
    labelsText: state.forklift.labelsText,
  };
}

UploadForm.propTypes = {
  dispatchSetUploadFiles: PropTypes.func.isRequired,
  labelsText: PropTypes.string.isRequired,
  isUploading: PropTypes.bool.isRequired,
  dispatchStartUploadFiles: PropTypes.func.isRequired,
  filesData: PropTypes.objectOf(PropTypes.shape({
    status: PropTypes.number,
  })).isRequired,
  classes: PropTypes.shape({
    fileInput: PropTypes.string.isRequired,
    spaceOnRight: PropTypes.string.isRequired,
  }).isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(UploadForm));
