import PropTypes from 'prop-types';

export default {
  propTypes: {
    menuOpened: PropTypes.bool.isRequired,
    closeMenu: PropTypes.func.isRequired,
    openMenu: PropTypes.func.isRequired,
  },
  defaultProps: {},
};
