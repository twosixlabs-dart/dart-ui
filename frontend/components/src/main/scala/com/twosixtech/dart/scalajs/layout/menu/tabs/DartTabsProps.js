import PropTypes from 'prop-types';

export default {
  propTypes: {
    classes: PropTypes.shape({
      root: PropTypes.string,
    }).isRequired,
    onChange: PropTypes.func.isRequired,
    value: PropTypes.string.isRequired,
    tabs: PropTypes.arrayOf(PropTypes.shape({
      label: PropTypes.string,
      value: PropTypes.string,
    })).isRequired,
  },
  defaultProps: {},
};
