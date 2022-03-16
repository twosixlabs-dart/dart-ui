import { withStyles, IconButton as MuiIconButton } from '@material-ui/core';
import React, { Component } from 'react';
import IconButtonProps from './IconButtonProps';

// eslint-disable-next-line no-unused-vars
const styles = (theme) => ({
  root: {},
});

class IconButton extends Component {
  render() {
    const {
      icon,
      size,
      style,
      color,
      disabled,
      onClick,
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
      <MuiIconButton
        size={buttonSize}
        color={buttonColor}
        variant={buttonVariant}
        classes={classes}
        onClick={onClick}
        disabled={disabled}
      >
        {icon}
      </MuiIconButton>
    );
  }
}

IconButton.propTypes = IconButtonProps.propTypes;

IconButton.defaultProps = IconButtonProps.defaultProps;

export default withStyles(styles)(IconButton);
