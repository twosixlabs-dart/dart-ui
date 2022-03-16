import React, { Component } from 'react';
import PropTypes from 'prop-types';

class AbsoluteCentered extends Component {
  render() {
    const {
      children,
    } = this.props;

    return (
      <div
        style={{
          position: 'absolute',
          margin: 0,
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        {children}
      </div>
    );
  }
}

AbsoluteCentered.propTypes = {
  children: PropTypes.node.isRequired,
};

export default AbsoluteCentered;
