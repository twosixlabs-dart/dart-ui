import { withStyles, Button as MuiButton } from '@material-ui/core';
import React, { Component } from 'react';
// import PropTypes from 'prop-types';
import ButtonProps from './ButtonMuiProps';

// eslint-disable-next-line no-unused-vars
const styles = (theme) => ({
  root: {},
});

class Button extends Component {
  render() {
    const {
      children,
      size,
      variant,
      color,
      disabled,
      onClick,
      onMouseDown,
      classes,
    } = this.props;

    const buttonVariant = variant || 'contained';

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

      case 'medium': {
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
        variant={buttonVariant}
        classes={classes}
        onClick={onClick}
        onMouseDown={onMouseDown}
        disabled={disabled}
      >
        {children}
      </MuiButton>
    );
  }
}

Button.propTypes = ButtonProps.propTypes;

Button.defaultProps = ButtonProps.defaultProps;

export default withStyles(styles)(Button);
