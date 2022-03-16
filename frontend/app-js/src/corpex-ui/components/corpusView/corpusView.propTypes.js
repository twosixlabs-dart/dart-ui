import PropTypes from 'prop-types';

const aggResults = PropTypes.shape({});
const searchResults = PropTypes.shape({
  aggregations: aggResults,
});
const componentData = PropTypes.shape({
  type: PropTypes.string,
  label: PropTypes.string,
  aggs: aggResults,
  state: PropTypes.shape({}),
});
const componentIndex = PropTypes.objectOf(componentData);
const componentMap = PropTypes.arrayOf(PropTypes.string);

export default {
  searchResults,
  aggResults,
  componentData,
  componentIndex,
  componentMap,
};
