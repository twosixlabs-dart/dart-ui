import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import corpusViewPropTypes from '../../../../corpusView.propTypes';
import { aggTypes } from '../../../../../searchBuilder/searchComponentData/enums';
import HistogramTable from '../HistogramTable';
import RangeInput from '../../../../../../../common/components/RangeInput';

export const aggId = 'facet_values';

export const getAggQuery = (kind, facetId, scoreLo, scoreHi, take) => {
  switch (kind) {
    case 'no-score': {
      return {
        agg_type: aggTypes.FACET,
        facet_id: facetId,
        size: take,
      };
    }

    case 'score-filter': {
      return {
        agg_type: aggTypes.FACET,
        facet_id: facetId,
        score_lo: scoreLo,
        score_hi: scoreHi,
        size: take,
      };
    }

    case 'score-avg': {
      return {
        agg_type: aggTypes.FACET_CONFIDENCE,
        facet_id: facetId,
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

function SentimentStanceCorpusViewerComponent({
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
  } = state;

  const data = aggId in aggResults ? aggResults[aggId] : [];

  const updateHandler = (v) => updateData({
    state: {
      ...state,
      scoreLo: v[0],
      scoreHi: v[1],
    },
    aggs: {
      [aggId]: getAggQuery(kind, facetId, v[0], v[1], 15),
    },
  });

  return (
    <div className={`${classes.root} corpus-overview-component`}>
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
        labelLabel="Analytic"
        valueLabel="Measurement"
        count={count}
        take={15}
      />
    </div>
  );
}

SentimentStanceCorpusViewerComponent.propTypes = {
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

export default connect(mapStateToProps)(withStyles(styles)(SentimentStanceCorpusViewerComponent));
