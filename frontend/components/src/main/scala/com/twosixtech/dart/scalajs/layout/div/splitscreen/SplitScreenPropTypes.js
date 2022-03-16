import PropTypes from 'prop-types';

export const propTypes = {
  childLeft: PropTypes.node.isRequired,
  childRight: PropTypes.node.isRequired,
  divisionType: PropTypes.oneOf(
    [
      'narrow-left',
      'left',
      'middle',
      'right',
      'narrow-right',
    ],
  ),
  independentScroll: PropTypes.bool,
  classes: PropTypes.shape({
    container: PropTypes.string,
    left: PropTypes.string,
    right: PropTypes.string,
  }),
};

export const defaultProps = {
  divisionType: 'left',
  independentScroll: false,
  classes: {},
};
