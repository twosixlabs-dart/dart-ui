import React, { Component } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';
import Chip from '@material-ui/core/Chip';
import { Select } from '@material-ui/core';
import HelpOutlineOutlinedIcon from '@material-ui/icons/HelpOutlineOutlined';

import Title from '../../../common/components/Title';
import TextQuery from '../../../common/components/TextQuery';
import { setUploadMetaData, updateLabelsText } from '../../redux/actions/uploadFiles.actions';

import { connect } from '../../../dart-ui/context/CustomConnect';
import { chooseTenant } from '../../../dart-ui/redux/actions/dart.actions';

const styles = () => ({
  paper: {
    padding: 10,
  },
  padded: {
    padding: 20,
  },
  fieldSpacing: {
    marginBottom: 15,
  },
  marginRight: {
    marginRight: 20,
  },
});

const HtmlTooltip = withStyles((theme) => ({
  tooltip: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 300,
    fontSize: theme.typography.pxToRem(12),
    border: '1px solid #dadde9',
  },
}))(Tooltip);

class MetadataForm extends Component {
  constructor(props) {
    super(props);
    this.updateMetadataField = this.updateMetadataField.bind(this);
    this.updateMetadataFieldFromEventTarget = this.updateMetadataFieldFromEventTarget.bind(this);
    this.updateLabels = this.updateLabels.bind(this);
    this.updateLabelsText = this.updateLabelsText.bind(this);
  }

  componentDidMount() {
    const {
      metaData,
      dispatchSetUploadMetaData,
      tenantId,
    } = this.props;

    dispatchSetUploadMetaData({
      ...metaData,
      tenants: [tenantId],
    });
  }

  updateMetadataField = (field) => (text) => {
    const { dispatchSetUploadMetaData, metaData } = this.props;
    dispatchSetUploadMetaData({
      ...metaData,
      [field]: text,
    });
  };

  updateMetadataFieldFromEventTarget = (field) => (e) => {
    this.updateMetadataField(field)(e.target.value);
  };

  updateLabelsText = (newText) => {
    const { dispatchUpdateLabelsText } = this.props;
    dispatchUpdateLabelsText(newText);
  };

  updateLabels = (text) => {
    const { metaData: { labels }, dispatchUpdateLabelsText } = this.props;
    this.updateMetadataField('labels')([...labels, text]);
    dispatchUpdateLabelsText('');
  };

  render() {
    const {
      metaData,
      labelsText,
      dispatchSetUploadMetaData,
      tenants,
      tenantId,
      dispatch,
      classes,
    } = this.props;

    const labelElements = metaData.labels.map((v) => (
      <Grid item key={`label-el-${v}`}>
        {v.trim().length === 0 ? '' : (
          <Chip
            size="small"
            color="primary"
            onDelete={() => {
              const newLabelsArray = metaData.labels.filter((l) => l !== v);
              this.updateMetadataField('labels')(newLabelsArray);
            }}
            label={v.trim()}
            className="metadata-form-field-labels-value"
          />
        )}
      </Grid>
    ));

    const labelsElement = (
      <Grid container direction="row" spacing={1}>
        {labelElements}
      </Grid>
    );

    return (
      <Paper
        classes={{ root: classes.paper }}
        className="metadata-form"
      >
        <Title>Metadata</Title>
        <div className={classes.padded}>
          <Grid item xs={12} classes={{ root: classes.fieldSpacing }}>
            {tenants.length > 0 ? (
              <Grid
                container
                direction="row"
                alignItems="center"
                justifyContent="center"
                spacing={2}
                className="metadata-form-field-tenants"
              >
                <Typography
                  component="h3"
                  variant="subtitle1"
                  color="textPrimary"
                  gutterBottom
                  classes={{ root: classes.marginRight }}
                >
                  Tenant:
                </Typography>
                <Select
                  native
                  value={tenantId || ''}
                  onChange={(e) => {
                    dispatch(chooseTenant(e.target.value));
                    dispatchSetUploadMetaData({
                      ...metaData,
                      tenants: [tenantId],
                    });
                  }}
                  inputProps={{
                    name: 'relevance',
                    id: 'relevance-select-input',
                  }}
                >
                  {tenants
                    .map((tenant) => (<option value={tenant}>{tenant}</option>))}
                </Select>
              </Grid>
            ) : ''}
          </Grid>
          <Grid item xs={12} classes={{ root: classes.fieldSpacing }}>
            <Grid
              container
              direction="row"
              alignItems="center"
              justifyContent="center"
              spacing={2}
              className="metadata-form-field-genre"
            >
              <Typography
                component="h3"
                variant="subtitle1"
                color="textPrimary"
                gutterBottom
                classes={{ root: classes.marginRight }}
              >
                Genre:
              </Typography>
              <Select
                native
                value={metaData.genre || 'unspecified'}
                onChange={(e) => this.updateMetadataFieldFromEventTarget('genre')({ target: { value: e.target.value } })}
                inputProps={{
                  name: 'relevance',
                  id: 'relevance-select-input',
                }}
              >
                <option value="unspecified">Unspecified</option>
                <option value="presentation">Presentation</option>
                <option value="report">Report</option>
                <option value="fact-sheet">Fact Sheets</option>
                <option value="website">Web Site</option>
                <option value="news-article">News Article</option>
                <option value="journal-article">Journal Article</option>
              </Select>
            </Grid>
          </Grid>
          <Grid item xs={12} classes={{ root: classes.fieldSpacing }}>
            <Grid
              container
              direction="row"
              alignItems="center"
              justifyContent="center"
              spacing={2}
              className="metadata-form-field-labels"
            >
              <Typography component="h3" variant="subtitle1" color="textPrimary" gutterBottom>
                Labels:
                <HtmlTooltip
                  interactive
                  title={(
                    <>
                      <Typography color="inherit">Press Enter to add new label</Typography>
                    </>
                  )}
                >
                  <HelpOutlineOutlinedIcon fontSize="small" color="primary" />
                </HtmlTooltip>
              </Typography>
              <TextQuery
                onEnter={this.updateLabels}
                onType={this.updateLabelsText}
                textValue={labelsText}
              />
            </Grid>
            {metaData.labels.length > 0 ? labelsElement : ''}
          </Grid>
        </div>
      </Paper>
    );
  }
}

const mapDispatchToProps = (dispatch) => ({
  dispatch,
  dispatchSetUploadMetaData: (metaData) => {
    dispatch(setUploadMetaData(metaData));
  },
  dispatchUpdateLabelsText: (newText) => {
    dispatch(updateLabelsText(newText));
  },
});

function mapStateToProps(state, dartContext) {
  return {
    metaData: state.forklift.metaData,
    labelsText: state.forklift.labelsText,
    tenants: dartContext.tenants,
    tenantId: state.dart.nav.tenantId,
  };
}

MetadataForm.propTypes = {
  dispatch: PropTypes.func.isRequired,
  dispatchSetUploadMetaData: PropTypes.func.isRequired,
  dispatchUpdateLabelsText: PropTypes.func.isRequired,
  labelsText: PropTypes.string.isRequired,
  metaData: PropTypes.shape({
    labels: PropTypes.arrayOf(PropTypes.string).isRequired,
    // ingestion_system: PropTypes.string,
    // relevance: PropTypes.string.isRequired,
    genre: PropTypes.string.isRequired,
  }).isRequired,
  tenants: PropTypes.arrayOf(PropTypes.string).isRequired,
  tenantId: PropTypes.string.isRequired,
  classes: PropTypes.shape({
    padded: PropTypes.string,
    paper: PropTypes.string,
    marginRight: PropTypes.string,
    fieldSpacing: PropTypes.string,
  }).isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(MetadataForm));
