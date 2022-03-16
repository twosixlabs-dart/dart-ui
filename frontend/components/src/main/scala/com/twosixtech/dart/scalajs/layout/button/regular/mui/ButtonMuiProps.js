import PropTypes from 'prop-types';

export default {
  propTypes: {
    onClick: PropTypes.func.isRequired,
    onMouseDown: PropTypes.func.isRequired,
    size: PropTypes.oneOf(['large', 'medium', 'small']),
    color: PropTypes.oneOf(['primary', 'secondary', 'plain']),
    variant: PropTypes.oneOf(['contained', 'outlined', 'text']),
    classes: PropTypes.shape({
      root: PropTypes.string,
    }),
  },
  defaultProps: {
    size: 'medium',
    classes: {},
  },
};
