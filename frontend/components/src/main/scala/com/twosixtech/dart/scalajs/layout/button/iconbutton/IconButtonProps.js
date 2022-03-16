import PropTypes from 'prop-types';

export default {
  propTypes: {
    icon: PropTypes.node.isRequired,
    onClick: PropTypes.func.isRequired,
    ariaLabel: PropTypes.string,
    size: PropTypes.oneOf(['medium', 'small']),
    color: PropTypes.oneOf(['primary', 'secondary', 'plain']),
    style: PropTypes.oneOf(['solid', 'outlined', 'text']),
    classes: PropTypes.shape({
      root: PropTypes.string,
    }),
  },
  defaultProps: {
    size: 'medium',
    classes: {},
    ariaLabel: null,
  },
};
