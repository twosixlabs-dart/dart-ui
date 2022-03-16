import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';
import IconButton from '@material-ui/core/IconButton';
import { Remove } from '@material-ui/icons';

import Title from '../../../../../common/components/Title';
import CorpusViewerBuilderAddComponentMenuItem from '../corpusViewerBuilder/CorpusViewerBuilderAddComponentMenuItem';
import { componentTypes } from '../../componentData/corpusViewUserComponentTypes';
import corpusViewComponentTypes from '../../componentData/corpusViewComponentTypes';

const styles = () => ({
});

class CorpusViewerComponentHeader extends Component {
  render() {
    const {
      label,
      componentType,
      addComponent,
      removeComponent,
      dragHandleProps,
    } = this.props;

    const addElement = (
      <CorpusViewerBuilderAddComponentMenuItem
        addComponent={addComponent}
        supportedComponentTypes={Object.values(componentTypes)}
      />
    );

    const removeElement = (
      <IconButton
        edge="start"
        color="primary"
        onClick={removeComponent}
        className="dart-ui-navbar-hamburger-button"
      >
        <Remove />
      </IconButton>
    );

    return (
      <div className="corpus-overview-component-header">
        <Title small>
          <Grid container dir="row" spacing={2} alignItems="center">
            <Grid item>
              <div {...dragHandleProps}>
                {label}
              </div>
            </Grid>
            <Grid item>
              {componentType === corpusViewComponentTypes.SECTION_COMPONENT ? addElement : ''}
            </Grid>
            <Grid item>{removeElement}</Grid>
          </Grid>
        </Title>
      </div>
    );
  }
}

CorpusViewerComponentHeader.propTypes = {
  label: PropTypes.string.isRequired,
  componentType: PropTypes.string.isRequired,
  addComponent: PropTypes.func.isRequired,
  removeComponent: PropTypes.func.isRequired,
  dragHandleProps: PropTypes.shape({}),
};

CorpusViewerComponentHeader.defaultProps = {
  dragHandleProps: {},
};

const mapStateToProps = () => ({});

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewerComponentHeader));
