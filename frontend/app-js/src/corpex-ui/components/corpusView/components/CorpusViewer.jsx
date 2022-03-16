import React, { Component } from 'react';
import withStyles from '@material-ui/core/styles/withStyles';
import CorpusViewerDashboard from './corpusViewerDashboard/CorpusViewerDashboard';
import CorpusViewerBuilder from './corpusViewerBuilder/CorpusViewerBuilder';

import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = () => ({
});

class CorpusViewer extends Component {
  render() {
    return (
      <div className="corpus-viewer">
        <CorpusViewerBuilder />
        <CorpusViewerDashboard />
      </div>
    );
  }
}

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewer));
