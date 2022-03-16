import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import corpusViewPropTypes from '../../../../corpusView.propTypes';
import { aggTypes } from '../../../../../searchBuilder/searchComponentData/enums';
import HistogramTable from '../HistogramTable';
import RangeInput from '../../../../../../../common/components/RangeInput';
import TogglableSearch from '../TogglableSearch';

export const aggId = 'facet_values';

export const getAggQuery = (kind, facetId, scoreLo, scoreHi, filterValue, take) => {
  switch (kind) {
    case 'no-score': {
      return {
        agg_type: aggTypes.FACET,
        facet_id: facetId,
        facet_values_query: filterValue,
        size: take,
      };
    }

    case 'score-filter': {
      return {
        agg_type: aggTypes.FACET,
        facet_id: facetId,
        score_lo: scoreLo,
        score_hi: scoreHi,
        facet_values_query: filterValue,
        size: take,
      };
    }

    case 'score-avg': {
      return {
        agg_type: aggTypes.FACET_CONFIDENCE,
        facet_id: facetId,
        facet_values_query: filterValue,
        size: take,
      };
    }

    default:
      return {};
  }
};

const styles = () => ({
  root: {
    width: 250,
  },
});

function FacetValuesCorpusViewerComponent({
  aggResults,
  updateData,
  currentData: {
    state,
  },
  // outerWidth,
  count,
  classes,
}) {
  const {
    facetId,
    kind,
    scoreLo,
    scoreHi,
    labelLabel,
    valueLabel,
    filterValue,
  } = state;

  const data = aggId in aggResults ? aggResults[aggId] : [];

  const updateHandler = (v) => updateData({
    state: {
      ...state,
      scoreLo: v[0],
      scoreHi: v[1],
    },
    aggs: {
      [aggId]: getAggQuery(kind, facetId, v[0], v[1], filterValue || undefined, 200),
    },
  });

  const updateFilter = (value) => {
    if (value !== (filterValue || '')) {
      updateData({
        aggs: {
          [aggId]: getAggQuery(kind, facetId, scoreLo, scoreHi, value || undefined, 200),
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
      {kind === 'score-filter' ? (
        <RangeInput
          width="100%"
          domain={[0, 1]}
          values={[scoreLo, scoreHi]}
          step={0.05}
          onChange={updateHandler}
        />
      ) : ''}
      <HistogramTable
        values={data}
        labelLabel={labelLabel}
        valueLabel={kind === 'score-avg' ? valueLabel || 'Avg. Score' : 'Doc Count'}
        displayValue={kind === 'score-avg' ? (v) => `${(v * 100).toFixed(0)}%` : undefined}
        getValue={kind === 'score-avg' ? (valObj) => valObj.score : undefined}
        count={kind === 'score-avg' ? 1 : count}
        take={15}
      />
    </div>
  );
}

FacetValuesCorpusViewerComponent.propTypes = {
  currentData: corpusViewPropTypes.componentData.isRequired,
  updateData: PropTypes.func.isRequired,
  aggs: PropTypes.shape({}).isRequired,
  state: PropTypes.shape({}).isRequired,
  aggResults: corpusViewPropTypes.aggResults.isRequired,
  facets: PropTypes.shape({}).isRequired,
  count: PropTypes.number.isRequired,
  // outerWidth: PropTypes.number.isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  facets: state.corpex.corpexRoot.facets,
});

export default connect(mapStateToProps)(withStyles(styles)(FacetValuesCorpusViewerComponent));
