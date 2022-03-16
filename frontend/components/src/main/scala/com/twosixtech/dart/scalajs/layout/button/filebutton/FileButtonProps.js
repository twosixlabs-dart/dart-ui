import PropTypes from 'prop-types';

export default {
  propTypes: {
    element: PropTypes.node.isRequired,
    onChange: PropTypes.func.isRequired,
    size: PropTypes.oneOf(['large', 'normal', 'small']),
    color: PropTypes.oneOf(['primary', 'secondary', 'plain']),
    style: PropTypes.oneOf(['solid', 'outlined', 'text']),
    classes: PropTypes.shape({
      root: PropTypes.string,
    }),
  },
  defaultProps: {
    size: 'normal',
    classes: {},
  },
};
