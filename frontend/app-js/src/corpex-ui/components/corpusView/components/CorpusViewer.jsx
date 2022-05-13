import React, { Component } from 'react';
import PropTypes from 'prop-types';
import withStyles from '@material-ui/core/styles/withStyles';
import CorpusViewerDashboard from './corpusViewerDashboard/CorpusViewerDashboard';
import CorpusViewerBuilder from './corpusViewerBuilder/CorpusViewerBuilder';

import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = () => ({
  fullSize: {
    height: '100%',
  },
});

class CorpusViewer extends Component {
  render() {
    const { classes } = this.props;
    return (
      <div className={`corpus-viewer ${classes.fullSize}`}>
        <CorpusViewerBuilder />
        <CorpusViewerDashboard />
      </div>
    );
  }
}

CorpusViewer.propTypes = {
  classes: PropTypes.shape({
    fullSize: PropTypes.string.isRequired,
  }).isRequired,
};

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewer));
