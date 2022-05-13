import React, { Component } from 'react';
import PropTypes from 'prop-types';
import AbsoluteCentered from './AbsoluteCentered';

class FullSizeCentered extends Component {
  render() {
    const {
      children,
    } = this.props;

    return (
      <div
        style={{
          position: 'relative',
          margin: 0,
          height: '100%',
          width: '100%',
        }}
      >
        <AbsoluteCentered>
          {children}
        </AbsoluteCentered>
      </div>
    );
  }
}

FullSizeCentered.propTypes = {
  children: PropTypes.node.isRequired,
};

export default FullSizeCentered;
