import PropTypes from 'prop-types';

export default {
  propTypes: {
    render: PropTypes.func.isRequired,
    setHeight: PropTypes.func,
    setWidth: PropTypes.func,
  },
  defaultProps: {
    setHeight: () => {},
    setWidth: () => {},
  },
};
