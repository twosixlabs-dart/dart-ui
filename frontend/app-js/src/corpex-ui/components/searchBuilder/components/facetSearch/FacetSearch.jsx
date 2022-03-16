import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import getFacet from '../../thunk/getFacet.thunk';
import { aggTypes, boolTypes, queryTypes } from '../../searchComponentData/enums';
import Multivalue from '../common/Multivalue';
import { DEFAULT_TAG_RESULTS_PAGE_SIZE } from '../../../../config/constants';
import { connect } from '../../../../../dart-ui/context/CustomConnect';

const styles = {
  root: {
    position: 'relative',
  },
};

export function getSearchQuery(
  boolType,
  facetId,
  scoreRange,
  facetQuery,
  facetValues,
  facetBoolType,
) {
  if (facetValues === null || facetValues === undefined || facetValues.length === 0) {
    return {
      bool_type: boolType,
      query_type: queryTypes.FACET,
      facet_id: facetId,
      score_lo: scoreRange === null
      || scoreRange === undefined
      || scoreRange.length !== 2
      || scoreRange[0] === 0 ? null : scoreRange[0],
      score_hi: scoreRange === null
      || scoreRange === undefined
      || scoreRange.length !== 2
      || scoreRange[1] === 1 ? null : scoreRange[1],
      facet_values_query: facetQuery === '' ? null : facetQuery,
    };
  }
  return {
    bool_type: boolType,
    query_type: queryTypes.FACET,
    facet_id: facetId,
    score_lo: scoreRange === null
    || scoreRange === undefined
    || scoreRange.length !== 2
    || scoreRange[0] === 0 ? null : scoreRange[0],
    score_hi: scoreRange === null
    || scoreRange === undefined
    || scoreRange.length !== 2
    || scoreRange[1] === 1 ? null : scoreRange[1],
    facet_values: facetValues.map((facetObj) => facetObj.value),
    values_bool_type: facetBoolType,
  };
}

export function getIsActive(selectedValues, query) {
  return (selectedValues !== null && selectedValues.length > 0) || query !== '';
}

export function getSummary(facetId, selectedValues, scoreRange, query, facetBoolType) {
  let summary = `Facet ${facetId}`;
  let boolVerb = 'should match';
  if (facetBoolType === boolTypes.MUST) boolVerb = 'must match';
  else if (facetBoolType === boolTypes.MUST_NOT) boolVerb = 'exclude';
  else if (facetBoolType === boolTypes.FILTER) boolVerb = 'filter';

  if (selectedValues !== null && selectedValues.length > 0) summary += ` ${boolVerb} values: ${selectedValues.map((v) => v.value).join(', ')}`;
  else if (query !== null && query !== undefined && query.length > 0) summary += `, Query: ${query}`;
  if (scoreRange !== null
    && scoreRange !== undefined
    && scoreRange.length === 2
    && (scoreRange[0] > 0 || scoreRange[1] < 1)) {
    summary += `, Score: ${scoreRange[0] === null ? 0 : scoreRange[0]}-${scoreRange[1] === null ? 1 : scoreRange[1]}`;
  }

  return summary;
}

export function getAggQuery(facetId, queryString, scoreLo, scoreHi) {
  return {
    agg_type: aggTypes.FACET,
    facet_id: facetId,
    facet_values_query: queryString === '' ? null : queryString,
    score_lo: scoreLo,
    score_hi: scoreHi,
  };
}

class FacetSearch extends Component {
  constructor(props) {
    super(props);

    const {
      componentId,
      updateCallback,
      dispatch,
      xhrHandler,
      componentState: { selectedFacetId, hasScore },
    } = props;

    if (selectedFacetId !== null
      && selectedFacetId !== undefined) {
      if (hasScore === null
        || hasScore === undefined) {
        const newAggQueries = {};
        newAggQueries[selectedFacetId] = getAggQuery(selectedFacetId);
        updateCallback(null, null, null, null, null, newAggQueries, null);
        dispatch(getFacet(xhrHandler, componentId, selectedFacetId));
      }
    }
  }

  render() {
    const {
      componentId,
      boolType,
      updateCallback,
      componentState,
      privateAggQueries,
      getAggResults,
      dispatch,
      classes,
      xhrHandler,
    } = this.props;

    const {
      facetIds,
      selectedFacetId,
      scoreRange,
      query,
      page,
      selectedFacetValues,
      facetBoolType,
      hasScore,
    } = componentState;

    const aggResults = getAggResults(selectedFacetId);
    const facetValues = aggResults !== null ? aggResults : [];

    const updateQueryHandler = (v) => {
      const newState = { ...componentState, query: v };

      const isActive = getIsActive(selectedFacetValues, v);
      const newQuery = getSearchQuery(
        boolType,
        selectedFacetId,
        scoreRange,
        v,
        selectedFacetValues,
        facetBoolType,
      );
      const summary = getSummary(selectedFacetId, selectedFacetValues, scoreRange, v);
      const newAggQueries = { ...privateAggQueries };
      const newFacetQuery = v === '' ? null : v;
      const aq = newAggQueries[selectedFacetId];
      const lo = aq ? aq.score_lo : null;
      const hi = aq ? aq.score_hi : null;
      newAggQueries[selectedFacetId] = getAggQuery(selectedFacetId, newFacetQuery, lo, hi);

      updateCallback(newState, isActive, newQuery, summary, null, newAggQueries, null);
    };

    const updateScoreRangeHandler = (v) => updateCallback({ ...componentState, scoreRange: v });

    const updateScoreRangeCommittedHandler = (v) => {
      const newState = { ...componentState, scoreRange: v };
      const isActive = getIsActive(selectedFacetValues, query);
      const newQuery = getSearchQuery(
        boolType,
        selectedFacetId,
        v,
        query,
        selectedFacetValues,
        facetBoolType,
      );
      const summary = getSummary(selectedFacetId, selectedFacetValues, v, query, facetBoolType);
      const newAggQueries = { ...privateAggQueries };
      const lo = v[0] > 0 ? v[0] : null;
      const hi = v[1] < 1 ? v[1] : null;
      newAggQueries[selectedFacetId] = getAggQuery(selectedFacetId, query, lo, hi);

      updateCallback(newState, isActive, newQuery, summary, null, newAggQueries, null);
    };

    const updateHandler = (e, v) => {
      const newState = {
        ...componentState, selectedFacetId: v, facetValues: [], selectedFacetValues: [], query: '',
      };
      const newAggQueries = {};
      newAggQueries[v] = getAggQuery(v);
      updateCallback(newState, false, null, '', null, newAggQueries, null);
      dispatch(getFacet(xhrHandler, componentId, v));
    };

    const updateSelectedValues = (newSelectedValues) => {
      const newState = { ...componentState, selectedFacetValues: newSelectedValues };
      const isActive = getIsActive(newSelectedValues, query);
      const summary = getSummary(
        selectedFacetId,
        newSelectedValues,
        scoreRange,
        query,
        facetBoolType,
      );
      const newQuery = getSearchQuery(
        boolType,
        selectedFacetId,
        scoreRange,
        query,
        newSelectedValues,
        facetBoolType,
      );

      updateCallback(newState, isActive, newQuery, summary, null, null, null);
    };

    const selectedFacetValuesHandler = (updatedFacetValues) => {
      const newSelectedValues = updatedFacetValues.filter((v) => v.selected).map((v) => ({
        value: v.value,
        num_docs: v.count,
      }));
      updateSelectedValues(newSelectedValues);
    };

    const updatedChipsHandler = (newChipsValues) => {
      const newSelectedValues = newChipsValues.map((v) => ({
        value: v.value,
        num_docs: v.count,
      }));
      updateSelectedValues(newSelectedValues);
    };

    const pageHandler = (e, newPage) => {
      const newState = { ...componentState, page: newPage - 1 };
      updateCallback(newState);
    };

    const facetBoolTypeHandler = (e) => {
      const newState = { ...componentState, facetBoolType: e.target.value };
      const isActive = getIsActive(selectedFacetValues, query);
      const newQuery = getSearchQuery(
        boolType,
        selectedFacetId,
        scoreRange,
        query,
        selectedFacetValues,
        e.target.value,
      );
      const summary = getSummary(
        selectedFacetId,
        selectedFacetValues,
        scoreRange,
        query,
        e.target.value,
      );

      updateCallback(newState, isActive, newQuery, summary, null, null, null);
    };

    return (
      <div
        id={`facet-search-${componentId}`}
        className={`facet-search ${classes.root}`}
      >
        <Multivalue
          valueTypes={facetIds.map((fieldObj) => (
            { label: fieldObj, value: fieldObj }))}
          valueType={selectedFacetId}
          onValueTypeChange={updateHandler}
          showValues={selectedFacetId !== null && selectedFacetId !== undefined}
          onConfidenceValuesChange={updateScoreRangeHandler}
          onConfidenceValuesChangeCommitted={updateScoreRangeCommittedHandler}
          confidenceValues={hasScore ? scoreRange : []}
          valuesQuery={query}
          onValuesQueryChange={updateQueryHandler}
          valuesPage={page}
          valuesPageSize={DEFAULT_TAG_RESULTS_PAGE_SIZE}
          onValuesPageChange={pageHandler}
          values={facetValues.map((facetValue) => (
            {
              value: facetValue.value,
              count: facetValue.num_docs,
              selected: selectedFacetValues.map((v) => v.value).includes(facetValue.value),
            }))}
          onValuesChange={selectedFacetValuesHandler}
          selectedValues={selectedFacetValues}
          onSelectedValuesMatchChange={facetBoolTypeHandler}
          onSelectedValuesClear={() => selectedFacetValuesHandler([])}
          onChipsChange={updatedChipsHandler}
          selectedValuesMatch={facetBoolType}
          supportedMatchTypes={['SHOULD', 'MUST', 'MUST_NOT']}
        />
      </div>
    );
  }
}

FacetSearch.propTypes = {
  componentId: PropTypes.string.isRequired,
  boolType: PropTypes.string.isRequired,
  updateCallback: PropTypes.func.isRequired,
  componentState: PropTypes.shape({
    selectedFacetId: PropTypes.string,
    facetIds: PropTypes.arrayOf(PropTypes.string).isRequired,
    hasScore: PropTypes.bool,
    scoreRange: PropTypes.arrayOf(PropTypes.number).isRequired,
    query: PropTypes.string.isRequired,
    selectedFacetValues: PropTypes.arrayOf(PropTypes.shape({
      value: PropTypes.string.isRequired,
    })).isRequired,
    page: PropTypes.number.isRequired,
    facetBoolType: PropTypes.string.isRequired,
  }).isRequired,
  privateAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  getAggResults: PropTypes.func.isRequired,
  dispatch: PropTypes.func.isRequired,
  xhrHandler: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

const mapDispatchToProps = (dispatch) => ({
  dispatch,
});

const mapStateToProps = (state, dartContext) => ({
  xhrHandler: dartContext.xhrHandler,
});

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(FacetSearch));
