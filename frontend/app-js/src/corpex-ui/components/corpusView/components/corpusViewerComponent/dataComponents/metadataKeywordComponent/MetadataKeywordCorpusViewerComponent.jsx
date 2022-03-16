import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import corpusViewPropTypes from '../../../../corpusView.propTypes';
import { aggTypes } from '../../../../../searchBuilder/searchComponentData/enums';
import HistogramTable from '../HistogramTable';
import TogglableSearch from '../TogglableSearch';

export const aggId = 'metadata_keyword_field';

export const getAggQuery = (field, filterValue) => ({
  agg_type: aggTypes.FIELD,
  queried_field: field,
  values_query: filterValue || undefined,
});

const styles = () => ({
  root: {
    width: 250,
  },
});

function MetadataKeywordCorpusViewerComponent({
  aggResults,
  currentData: {
    state,
  },
  updateData,
  count,
  classes,
}) {
  const data = aggId in aggResults ? aggResults[aggId] : [];

  const {
    filterValue,
    field,
  } = state;

  const updateFilter = (value) => {
    if (value !== (filterValue || '')) {
      updateData({
        aggs: {
          [aggId]: getAggQuery(field, value),
        },
        state: {
          ...state,
          filterValue: value,
        },
      });
    }
  };

  return (
    <div className={`${classes.root} corpus-overview-component`}>
      <TogglableSearch
        value={filterValue}
        onChange={updateFilter}
      />
      <HistogramTable
        values={data}
        labelLabel="Field Value"
        valueLabel="Doc Count"
        count={count}
        take={15}
      />
    </div>
  );
}

MetadataKeywordCorpusViewerComponent.propTypes = {
  aggs: PropTypes.shape({}).isRequired,
  currentData: corpusViewPropTypes.componentData.isRequired,
  updateData: PropTypes.func.isRequired,
  aggResults: corpusViewPropTypes.aggResults.isRequired,
  fields: PropTypes.shape({}).isRequired,
  count: PropTypes.number.isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  fields: state.corpex.corpexRoot.fields,
});

export default connect(mapStateToProps)(withStyles(styles)(MetadataKeywordCorpusViewerComponent));
