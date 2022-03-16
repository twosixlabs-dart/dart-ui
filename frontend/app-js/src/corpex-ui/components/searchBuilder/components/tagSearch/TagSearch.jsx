import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { connect } from 'react-redux';
import { withStyles } from '@material-ui/core/styles';
import { aggTypes, boolTypes, queryTypes } from '../../searchComponentData/enums';
import { completeTagTypeQuery } from '../../searchBuilder.actions';
import settings from '../../../../config/settings';
import Multivalue from '../common/Multivalue';
import { DEFAULT_TAG_RESULTS_PAGE_SIZE } from '../../../../config/constants';

const styles = {
  root: {
    position: 'relative',
  },
  tagValue: {
    selected: {
      backgroundColor: 'primary',
    },
    normal: {},
  },
};

const getAggQuery = (tagId, tagType, queryString) => ({
  agg_type: aggTypes.TAG_VALUES,
  tag_id: tagId,
  tag_types: [tagType],
  tag_values_query: queryString === '' ? null : queryString,
});

function getSearchQuery(boolType, tagId, tagType, tagValues, tagBoolType, tagQuery) {
  return {
    bool_type: boolType,
    query_type: queryTypes.TAG,
    tag_id: tagId,
    tag_types: [tagType],
    tag_values: tagQuery ? undefined : tagValues.map((valObj) => valObj.value),
    values_bool_type: tagQuery ? undefined : tagBoolType,
    tag_values_query: tagQuery,
  };
}

function getSummary(selectedTagType, tagBoolType, selectedTagValues) {
  let boolTypeText = 'filtered by';
  if (tagBoolType === boolTypes.MUST) boolTypeText = 'must match';
  else if (tagBoolType === boolTypes.SHOULD) boolTypeText = 'should match';
  else if (tagBoolType === boolTypes.MUST_NOT) boolTypeText = 'must not match';

  const valuesText = selectedTagValues.map((v) => v.value).join(', ');
  return `${selectedTagType} tag ${boolTypeText} ${selectedTagValues.length} values: ${valuesText}`;
}

class TagSearch extends Component {
  constructor(props) {
    super(props);
    const { tags, componentState: { tagId } } = props;
    const savedTagTypes = tags[tagId].tagTypes;
    const numSavedTagTypes = Object.keys(savedTagTypes).length;
    if (numSavedTagTypes === 0) return;

    const supportedExtractions = {};
    settings.EXTRACTION_TYPES[tagId]
      .forEach((et) => { supportedExtractions[et] = true; });
    const tagTypes = Object.values(savedTagTypes)
      .filter((tt) => tt.tag_type in supportedExtractions);
    props.dispatch(completeTagTypeQuery(props.componentId, tagTypes));
  }

  componentDidUpdate(prevProps) {
    const { props } = this;
    const { tags, componentState: { tagId } } = props;
    const prevTags = prevProps.tags;
    const savedTagTypes = tags[tagId].tagTypes;
    const numSavedTagTypes = Object.keys(savedTagTypes).length;
    const prevSavedTagTypes = prevTags[tagId].tagTypes;
    const numPrevSavedTagTypes = Object.keys(prevSavedTagTypes).length;
    if (numPrevSavedTagTypes === numSavedTagTypes) return;

    const supportedExtractions = {};
    settings.EXTRACTION_TYPES[tagId]
      .forEach((et) => { supportedExtractions[et] = true; });
    const tagTypes = Object.values(savedTagTypes)
      .filter((tt) => tt.tag_type in supportedExtractions);
    props.dispatch(completeTagTypeQuery(props.componentId, tagTypes));
  }

  render() {
    const {
      componentId,
      boolType,
      updateCallback,
      privateAggQueries,
      getPrivateAggs,
      getAggResults,
      componentState,
      classes,
    } = this.props;

    const {
      tagId,
      tagTypes,
      selectedTagType,
      query,
      page,
      selectedTagValues,
      tagBoolType,
    } = componentState;

    // const tagTypesSet = tagTypes.map((typeObj) => typeObj.tag_type);

    const aggRes = getAggResults(selectedTagType);
    const tagValues = aggRes === null ? [] : aggRes;

    const updateHandler = (e, v) => {
      const newState = {
        ...componentState, selectedTagType: v, tagValues: [], selectedTagValues: [], query: '',
      };
      const summary = `Tag: ${v} (no values selected)`;
      const newAggQueries = {};
      newAggQueries[v] = getAggQuery(tagId, v);
      getPrivateAggs(newAggQueries);
      updateCallback(newState, false, null, summary, null, newAggQueries, null);
    };

    const updateSelectedValues = (newSelectedValues) => {
      const newState = { ...componentState, selectedTagValues: newSelectedValues };
      const isActive = newSelectedValues.length !== 0;
      const summary = getSummary(
        selectedTagType,
        tagBoolType,
        newSelectedValues,
      );
      const newQuery = getSearchQuery(
        boolType,
        tagId,
        selectedTagType,
        newSelectedValues,
        tagBoolType,
      );

      updateCallback(newState, isActive, newQuery, summary);
    };

    const selectedTagValuesHandler = (updatedTagValues) => {
      const newSelectedValues = updatedTagValues.filter((v) => v.selected).map((v) => ({
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

    const tagBoolTypeHandler = (e) => {
      const newState = { ...componentState, tagBoolType: e.target.value };
      const isActive = true;
      const newQuery = getSearchQuery(
        boolType,
        tagId,
        selectedTagType,
        selectedTagValues,
        e.target.value,
      );
      const summary = getSummary(selectedTagType, tagBoolType, selectedTagValues);

      updateCallback(newState, isActive, newQuery, summary);
    };

    const updateQueryHandler = (v) => {
      const newState = { ...componentState, query: v };
      updateCallback(newState);

      // If there are selected values, these have precedence
      if (selectedTagValues.length !== 0) {
        const newAggQueries = { ...privateAggQueries };
        const newTagQuery = v === '' ? null : v;
        newAggQueries[selectedTagType] = getAggQuery(tagId, selectedTagType, newTagQuery);

        updateCallback(null, null, null, null, null, newAggQueries, null);
        return;
      }

      const isActive = v !== '';
      const newQuery = getSearchQuery(boolType,
        tagId, selectedTagType, selectedTagValues, tagBoolType, v);
      const summary = `Tag: ${selectedTagType}, query: ${v}`;
      const newAggQueries = { ...privateAggQueries };
      const newTagQuery = v === '' ? null : v;
      newAggQueries[selectedTagType] = getAggQuery(tagId, selectedTagType, newTagQuery);

      updateCallback(newState, isActive, newQuery, summary, null, newAggQueries, null);
    };

    if (tagTypes.length === 0) return (<div />);

    return (
      <div id={`tag-search-${componentId}`} className={`tag-search ${classes.root}`}>
        <Multivalue
          valueTypes={tagTypes.map((typeObj) => (
            { label: typeObj.label, value: typeObj.tag_type, description: typeObj.description }))}
          valueType={selectedTagType}
          onValueTypeChange={updateHandler}
          showValues={selectedTagType !== null && selectedTagType !== undefined}
          valuesQuery={query}
          onValuesQueryChange={updateQueryHandler}
          valuesPage={page}
          valuesPageSize={DEFAULT_TAG_RESULTS_PAGE_SIZE}
          onValuesPageChange={pageHandler}
          values={tagValues.map((tagValue) => (
            {
              value: tagValue.value,
              count: tagValue.num_docs,
              selected: selectedTagValues.map((v) => v.value).includes(tagValue.value),
            }))}
          onValuesChange={selectedTagValuesHandler}
          selectedValues={selectedTagValues}
          onSelectedValuesMatchChange={tagBoolTypeHandler}
          onSelectedValuesClear={() => selectedTagValuesHandler([])}
          onChipsChange={updatedChipsHandler}
          selectedValuesMatch={tagBoolType}
          supportedMatchTypes={['SHOULD', 'MUST', 'MUST_NOT']}
        />
      </div>
    );
  }
}

TagSearch.propTypes = {
  tags: PropTypes.shape({}).isRequired,
  componentId: PropTypes.string.isRequired,
  boolType: PropTypes.string.isRequired,
  updateCallback: PropTypes.func.isRequired,
  privateAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  getPrivateAggs: PropTypes.func.isRequired,
  getAggResults: PropTypes.func.isRequired,
  componentState: PropTypes.shape({
    tagId: PropTypes.string.isRequired,
    tagTypes: PropTypes.arrayOf(PropTypes.shape({
      label: PropTypes.string.isRequired,
      tag_type: PropTypes.string.isRequired,
    })).isRequired,
    selectedTagType: PropTypes.string,
    query: PropTypes.string.isRequired,
    page: PropTypes.number.isRequired,
    selectedTagValues: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
    tagBoolType: PropTypes.string.isRequired,
  }).isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

const mapStateToProps = (state) => ({
  tags: state.corpex.corpexRoot.tags,
});

export default connect(mapStateToProps)(withStyles(styles)(TagSearch));
