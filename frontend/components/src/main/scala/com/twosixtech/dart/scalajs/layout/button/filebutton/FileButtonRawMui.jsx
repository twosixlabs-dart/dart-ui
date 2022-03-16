import { Button as MuiButton, withStyles } from '@material-ui/core';
import React, { Component } from 'react';
// import PropTypes from 'prop-types';
import ButtonProps from './FileButtonProps';

// eslint-disable-next-line no-unused-vars
const styles = (theme) => ({
  root: {},
});

class FileButton extends Component {
  render() {
    const {
      element,
      size,
      style,
      color,
      disabled,
      onChange,
      classes,
    } = this.props;

    let buttonVariant = 'contained';
    switch (style) {
      case 'solid': {
        buttonVariant = 'contained';
        break;
      }

      case 'outlined': {
        buttonVariant = 'outlined';
        break;
      }

      case 'text': {
        buttonVariant = 'text';
        break;
      }

      default: {
        break;
      }
    }

    let buttonColor = 'primary';
    switch (color) {
      case 'primary': {
        buttonColor = 'primary';
        break;
      }

      case 'secondary': {
        buttonColor = 'secondary';
        break;
      }

      case 'plain': {
        buttonColor = 'default';
        break;
      }

      default: {
        break;
      }
    }

    let buttonSize = 'medium';
    switch (size) {
      case 'large': {
        buttonSize = 'large';
        break;
      }

      case 'normal': {
        buttonSize = 'medium';
        break;
      }

      case 'small': {
        buttonSize = 'small';
        break;
      }

      default: {
        break;
      }
    }

    return (
      <MuiButton
        size={buttonSize}
        color={buttonColor}
        component="label"
        variant={buttonVariant}
        classes={classes}
        disabled={disabled}
      >
        {element}
        <input
          type="file"
          disabled={disabled}
          onChange={onChange}
          hidden
        />
      </MuiButton>
    );
  }
}

FileButton.propTypes = ButtonProps.propTypes;

FileButton.defaultProps = ButtonProps.defaultProps;

export default withStyles(styles)(FileButton);
