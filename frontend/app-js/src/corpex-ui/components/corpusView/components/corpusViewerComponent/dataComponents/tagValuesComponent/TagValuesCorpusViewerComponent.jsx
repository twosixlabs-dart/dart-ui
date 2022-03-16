import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import corpusViewPropTypes from '../../../../corpusView.propTypes';
import { aggTypes } from '../../../../../searchBuilder/searchComponentData/enums';
import HistogramTable from '../HistogramTable';
import TogglableSearch from '../TogglableSearch';

export const aggId = 'tag_values';

export const getAggQuery = (tagId, tagType, valuesQuery) => ({
  agg_type: aggTypes.TAG_VALUES,
  tag_id: tagId,
  tag_types: [tagType],
  tag_values_query: valuesQuery || undefined,
});

const styles = () => ({
  root: {
    width: 250,
  },
});

function TagValuesCorpusViewerComponent({
  aggResults,
  currentData,
  updateData,
  count,
  classes,
}) {
  const data = aggId in aggResults ? aggResults[aggId] : [];

  const {
    state: {
      labelLabel,
      valueLabel,
      filterValue,
      tagId,
      tagType,
    },
  } = currentData;

  const updateFilter = (value) => {
    if (value !== (filterValue || '')) {
      updateData({
        aggs: {
          [aggId]: getAggQuery(tagId, tagType, value),
        },
        state: {
          ...currentData.state,
          filterValue: value,
        },
      });
    }
  };

  return (
    <div className={`${classes.root} corpus-overview-component`}>
      <TogglableSearch
        value={filterValue || ''}
        onChange={updateFilter}
      />
      <HistogramTable
        values={data}
        count={count}
        labelLabel={labelLabel}
        valueLabel={valueLabel || 'Doc Count'}
        take={15}
      />
    </div>
  );
}

TagValuesCorpusViewerComponent.propTypes = {
  currentData: corpusViewPropTypes.componentData.isRequired,
  updateData: PropTypes.func.isRequired,
  aggs: PropTypes.shape({}).isRequired,
  state: PropTypes.shape({}).isRequired,
  aggResults: corpusViewPropTypes.aggResults.isRequired,
  tags: PropTypes.shape({}).isRequired,
  count: PropTypes.number.isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  tags: state.corpex.corpexRoot.tags,
});

export default connect(mapStateToProps)(withStyles(styles)(TagValuesCorpusViewerComponent));
