import React, { Component } from 'react';
import { withStyles, Typography } from '@material-ui/core';
// import PropTypes from 'prop-types';
import TextProps from './TextProps';

const styles = (theme) => ({
  clickable: {
    '&:hover': {
      cursor: 'pointer',
      color: theme.palette.primary.main,
    },
  },
  root: {},
});

class Text extends Component {
  render() {
    const {
      children,
      size,
      onClick,
      clickable,
      classes,
    } = this.props;

    let typographyVariant = 'body1';
    switch (size) {
      case 'large': {
        typographyVariant = 'h6';
        break;
      }

      case 'medium': {
        typographyVariant = 'body1';
        break;
      }

      case 'small': {
        typographyVariant = 'body2';
        break;
      }

      default: {
        break;
      }
    }

    const clickHandler = clickable ? onClick : () => {};
    const rootClass = clickable ? classes.clickable : '';
    const typographyClasses = { root: `${rootClass} ${classes.root}` };

    return (
      <Typography
        variant={typographyVariant}
        onClick={clickHandler}
        classes={typographyClasses}
      >
        {children}
      </Typography>
    );
  }
}

Text.propTypes = TextProps.propTypes;

Text.defaultProps = TextProps.defaultProps;

export default withStyles(styles)(Text);
