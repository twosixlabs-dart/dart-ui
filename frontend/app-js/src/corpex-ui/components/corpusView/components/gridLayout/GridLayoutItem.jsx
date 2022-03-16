import React, { Component } from 'react';
import { connect } from 'react-redux';
import withStyles from '@material-ui/core/styles/withStyles';

const styles = () => ({
});

class GridLayoutItem extends Component {
  render() {
    return (
      <div className="grid-layout-item" />
    );
  }
}

function mapStateToProps() {
  return {
  };
}

export default connect(mapStateToProps)(withStyles(styles)(GridLayoutItem));
