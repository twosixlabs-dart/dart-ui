import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core';

const styles = (theme) => ({
  root: {
    [theme.breakpoints.up('sm')]: {
      height: '100%',
      overflow: 'hidden',
      display: 'flex',
      boxSizing: 'border-box',
      flexDirection: 'column',
    },
  },
  header: {
    [theme.breakpoints.up('sm')]: {
      flexShrink: 0,
    },
  },
  bodyContainer: {
    marginTop: theme.spacing(1),
    [theme.breakpoints.up('sm')]: {
      flexGrow: 1,
      overflowY: 'hidden',
    },
  },
  bodyFixer: {
    height: '100%',
    overflowY: 'hidden',
  },
  body: {
    height: '100%',
    overflowY: 'scroll',
  },
  bodyFixed: {
    height: '100%',
    overflowY: 'hidden',
  },
});

class StickyHeader extends Component {
  render() {
    const {
      fixedBody,
      classes,
      header,
      children,
    } = this.props;

    const bodyClass = fixedBody ? classes.bodyFixed : classes.body;

    return (
      <div className={classes.root}>
        <div className={classes.header}>
          {header}
        </div>
        <div className={classes.bodyContainer}>
          <div className={classes.bodyFixer}>
            <div className={bodyClass}>
              {children}
            </div>
          </div>
        </div>
      </div>
    );
  }
}

StickyHeader.propTypes = {
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    header: PropTypes.string.isRequired,
    bodyContainer: PropTypes.string.isRequired,
    bodyFixer: PropTypes.string.isRequired,
    bodyFixed: PropTypes.string.isRequired,
    body: PropTypes.string.isRequired,
  }).isRequired,
  header: PropTypes.node.isRequired,
  children: PropTypes.node,
  fixedBody: PropTypes.bool,
};

StickyHeader.defaultProps = {
  children: <div />,
  fixedBody: false,
};

export default withStyles(styles)(StickyHeader);
