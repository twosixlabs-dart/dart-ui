import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Grid, withStyles } from '@material-ui/core';
import { propTypes, defaultProps } from './SplitScreenPropTypes';

const styles = (theme) => ({
  root: {
    height: '100%',
  },
  paper: {
    padding: 20,
  },
  scrollablePanels: {
    height: '100%',
    overflowY: 'hidden',
    overflowX: 'visible',
    [theme.breakpoints.down('sm')]: {
      height: 'auto',
      overflowY: 'auto',
      overflowX: 'visible',
    },
  },
  fixedPanels: {
    height: '100%',
  },
  container: {},
  left: {},
  right: {},
});

class SplitScreenRawMui extends Component {
  render() {
    const {
      classes,
      childLeft,
      childRight,
      independentScroll,
      divisionType,
    } = this.props;

    const panelsClass = independentScroll ? classes.scrollablePanels : classes.fixedPanels;

    let lbps;
    let rbps;

    switch (divisionType) {
      case 'narrow-left': {
        lbps = {
          md: 4,
          lg: 3,
        };
        rbps = {
          md: 8,
          lg: 9,
        };
        break;
      }

      case 'left': {
        lbps = {
          md: 6,
          lg: 4,
        };
        rbps = {
          md: 6,
          lg: 8,
        };
        break;
      }

      case 'middle': {
        lbps = {
          xs: 6,
        };
        rbps = lbps;
        break;
      }

      case 'right': {
        lbps = {
          md: 6,
          lg: 8,
        };
        rbps = {
          md: 6,
          lg: 4,
        };
        break;
      }

      case 'narrow-right': {
        lbps = {
          md: 8,
          lg: 9,
        };
        rbps = {
          md: 4,
          lg: 3,
        };
        break;
      }

      default: {
        lbps = { xs: 6 };
        rbps = lbps;
      }
    }

    const containerClass = classes.container || '';
    const leftClass = classes.left || '';
    const rightClass = classes.right || '';

    return (
      <Grid container spacing={1} classes={{ root: `${classes.root} ${containerClass}` }}>
        <Grid item xs={12} md={lbps.md} lg={lbps.lg} classes={{ root: `${leftClass} ${panelsClass}` }}>
          {childLeft}
        </Grid>
        <Grid item xs={12} md={rbps.md} lg={rbps.lg} classes={{ root: `${rightClass} ${panelsClass}` }}>
          {childRight}
        </Grid>
      </Grid>
    );
  }
}

SplitScreenRawMui.propTypes = {
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    paper: PropTypes.string.isRequired,
    container: PropTypes.string.isRequired,
    scrollablePanels: PropTypes.string.isRequired,
    fixedPanels: PropTypes.string.isRequired,
    left: PropTypes.string,
    right: PropTypes.string,
  }).isRequired,
  ...propTypes,
};

SplitScreenRawMui.defaultProps = defaultProps;

export default withStyles(styles)(SplitScreenRawMui);
