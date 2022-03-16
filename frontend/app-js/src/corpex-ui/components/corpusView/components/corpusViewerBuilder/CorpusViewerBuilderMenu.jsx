import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import { Paper } from '@material-ui/core';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

import CorpusViewerBuilderAddComponentMenuItem from './CorpusViewerBuilderAddComponentMenuItem';
import { componentTypes } from '../../componentData/corpusViewUserComponentTypes';

const styles = () => ({
});

class CorpusViewerBuilderMenu extends Component {
  render() {
    const {
      props: {
        sectionId,
        addComponent,
      },
    } = this;

    return (
      <div>
        <Paper square>
          <Toolbar variant="dense">
            <CorpusViewerBuilderAddComponentMenuItem
              supportedComponentTypes={Object.values(componentTypes)}
              sectionId={sectionId}
              addComponent={addComponent}
            />
            <Typography variant="h6" color="primary">
              Add Component
            </Typography>
          </Toolbar>
        </Paper>
      </div>
    );
  }
}

CorpusViewerBuilderMenu.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
};

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewerBuilderMenu));
