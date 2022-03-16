import PropTypes from 'prop-types';

export default {
  propTypes: {
    menuOpened: PropTypes.bool.isRequired,
    closeMenu: PropTypes.func.isRequired,
    menuItems: PropTypes.arrayOf(PropTypes.shape({
      text: PropTypes.string,
      key: PropTypes.string,
      onClick: PropTypes.func,
      isSelected: PropTypes.bool,
    })).isRequired,
    ref: PropTypes.func,
  },
  defaultProps: {},
};
