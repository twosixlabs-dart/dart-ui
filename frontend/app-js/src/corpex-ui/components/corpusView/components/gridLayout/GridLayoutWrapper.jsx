import React, { Component } from 'react';
import { connect } from 'react-redux';
import withStyles from '@material-ui/core/styles/withStyles';

const styles = () => ({
});

class GridLayoutWrapper extends Component {
  render() {
    return (
      <div className="grid-layout-wrapper" />
    );
  }
}

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(GridLayoutWrapper));
