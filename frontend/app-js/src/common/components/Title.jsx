import React from 'react';
import PropTypes from 'prop-types';
import Typography from '@material-ui/core/Typography';

export default function Title(props) {
  const { small, children } = props;

  return (
    <Typography
      component="h2"
      variant={small ? 'subtitle1' : 'h6'}
      color="primary"
      gutterBottom
    >
      {small ? <b>{children}</b> : children}
    </Typography>
  );
}

Title.propTypes = {
  children: PropTypes.node,
  small: PropTypes.bool,
};

Title.defaultProps = {
  small: false,
  children: '',
};
