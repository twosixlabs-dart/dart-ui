import PropTypes from 'prop-types';

const alignProp = PropTypes.oneOf(['start', 'center', 'end']);

export default {
  propTypes: {
    direction: PropTypes.string,
    align: alignProp,
    items: PropTypes.arrayOf(PropTypes.shape({
      element: PropTypes.node,
      flexGrow: PropTypes.number,
      flexShrink: PropTypes.number,
      flexBasis: PropTypes.string,
      align: alignProp,
      key: PropTypes.string,
      classes: PropTypes.shape({
        root: PropTypes.string,
      }),
    })).isRequired,
    classes: PropTypes.shape({
      container: PropTypes.string,
      items: PropTypes.string,
    }),
  },
  defaultProps: {
    direction: 'row',
    align: 'start',
    classes: {},
  },
};
