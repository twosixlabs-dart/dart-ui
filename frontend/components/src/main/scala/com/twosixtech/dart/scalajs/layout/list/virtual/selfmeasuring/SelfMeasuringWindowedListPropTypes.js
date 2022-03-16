import PropTypes from 'prop-types';

export const propTypes = {
  rowForWindow: PropTypes.func.isRequired,
  rowForMeasure: PropTypes.func,
  rowCount: PropTypes.number.isRequired,
  measureChunkSize: PropTypes.number.isRequired,
  windowWidth: PropTypes.number.isRequired,
  windowHeight: PropTypes.number.isRequired,
  overscanCount: PropTypes.number,
  onScroll: PropTypes.func,
  // eslint-disable-next-line react/no-unused-prop-types
  scrollTop: PropTypes.number,
  // eslint-disable-next-line react/no-unused-prop-types
  scrollToIndex: PropTypes.number,
  scrollToIndexCallback: PropTypes.func,
  // eslint-disable-next-line react/no-unused-prop-types
  scrollOffset: PropTypes.number,
  scrollOffsetCallback: PropTypes.func,
  key: PropTypes.string,
};

export const defaultProps = {
  rowForMeasure: undefined,
  overscanCount: 10,
  onScroll: () => {},
  scrollTop: -1,
  scrollToIndex: -1,
  scrollToIndexCallback: () => {},
  scrollOffset: -1,
  scrollOffsetCallback: () => {},
  key: 'keyless',
};
