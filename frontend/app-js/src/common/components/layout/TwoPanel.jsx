import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core';
import Grid from '@material-ui/core/Grid';

const styles = (theme) => ({
  root: {
  },
  independentScroll: {
  },
  independentScrollRelative: {
    height: '100%',
  },
  paper: {
    padding: 20,
  },
  container: {
    margin: 'auto',
    marginTop: 24,
    height: '100%',
    overflow: 'hidden',
  },
  scrollablePanels: {
    height: 'calc(100vh - 72px)',
    overflowY: 'hidden',
    [theme.breakpoints.down('sm')]: {
      height: 'auto',
      overflowY: 'auto',
    },
  },
  scrollablePanelsRelative: {
    height: '100%',
    overflowY: 'hidden',
    [theme.breakpoints.down('sm')]: {
      height: 'auto',
      overflowY: 'auto',
    },
  },
  fixedPanels: {
  },
});

class TwoPanel extends Component {
  render() {
    const {
      classes,
      left,
      right,
      independentScroll,
      relativeHeight,
      squeezeLeft,
      squeezeRight,
      className,
    } = this.props;

    let panelsClass = independentScroll ? classes.scrollablePanels : classes.fixedPanels;
    if (independentScroll && relativeHeight) panelsClass = classes.scrollablePanelsRelative;
    let rootClass = independentScroll ? classes.independentScroll : classes.root;
    if (independentScroll && relativeHeight) rootClass = classes.independentScrollRelative;

    let lbps = squeezeLeft ? {
      md: 4,
      lg: 3,
    } : {
      md: 6,
      lg: 4,
    };

    if (squeezeRight) {
      lbps = {
        md: 7,
        lg: 8,
      };
    }

    let rbps = squeezeLeft ? {
      md: 8,
      lg: 9,
    } : {
      md: 6,
      lg: 8,
    };

    if (squeezeRight) {
      rbps = {
        md: 5,
        lg: 4,
      };
    }

    return (
      <Grid container spacing={1} classes={{ root: `${className} ${rootClass}` }}>
        <Grid item xs={12} md={lbps.md} lg={lbps.lg} classes={{ root: panelsClass }}>
          {left}
        </Grid>
        <Grid item xs={12} md={rbps.md} lg={rbps.lg} classes={{ root: panelsClass }}>
          {right}
        </Grid>
      </Grid>
    );
  }
}

TwoPanel.propTypes = {
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    fixedPanels: PropTypes.string.isRequired,
    scrollablePanels: PropTypes.string.isRequired,
    scrollablePanelsRelative: PropTypes.string.isRequired,
    independentScrollRelative: PropTypes.string.isRequired,
    independentScroll: PropTypes.string.isRequired,
  }).isRequired,
  left: PropTypes.element.isRequired,
  right: PropTypes.element.isRequired,
  independentScroll: PropTypes.bool,
  squeezeLeft: PropTypes.bool,
  squeezeRight: PropTypes.bool,
  relativeHeight: PropTypes.bool,
  className: PropTypes.string,
};

TwoPanel.defaultProps = {
  independentScroll: false,
  squeezeLeft: false,
  squeezeRight: false,
  relativeHeight: false,
  className: 'two-panel',
};

export default withStyles(styles)(TwoPanel);
