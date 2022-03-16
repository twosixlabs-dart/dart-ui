import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import corpusViewPropTypes from '../../../../corpusView.propTypes';
import { aggTypes } from '../../../../../searchBuilder/searchComponentData/enums';
import HistogramTable from '../HistogramTable';

export const aggId = 'tag_types';

export const getAggQuery = (tagId) => ({
  agg_type: aggTypes.TAG_TYPES,
  tag_id: tagId,
});

const styles = () => ({
  root: {
    width: 250,
  },
});

function TagTypesCorpusViewerComponent({
  aggResults,
  count,
  tags,
  currentData: {
    state: {
      tagId,
      labelLabel,
      valueLabel,
    },
  },
  classes,
}) {
  const data = aggId in aggResults ? aggResults[aggId] : [];

  return (
    <div className={`${classes.root} corpus-overview-component`}>
      <HistogramTable
        values={
          // eslint-disable-next-line max-len,camelcase
          data.filter((({ value }) => value in tags[tagId].tagTypes)).map(({ value, num_docs }) => ({ value: tags[tagId].tagTypes[value].label, num_docs }))
        }
        labelLabel={labelLabel}
        valueLabel={valueLabel || 'Doc Count'}
        count={count}
        take={15}
      />
    </div>
  );
}

TagTypesCorpusViewerComponent.propTypes = {
  aggs: PropTypes.shape({}).isRequired,
  state: PropTypes.shape({}).isRequired,
  aggResults: corpusViewPropTypes.aggResults.isRequired,
  tags: PropTypes.shape({}).isRequired,
  count: PropTypes.number.isRequired,
  currentData: corpusViewPropTypes.componentData.isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  tags: state.corpex.corpexRoot.tags,
});

export default connect(mapStateToProps)(withStyles(styles)(TagTypesCorpusViewerComponent));
