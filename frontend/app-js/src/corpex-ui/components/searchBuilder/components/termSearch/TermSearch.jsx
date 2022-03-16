import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import { aggTypes, boolTypes, queryTypes } from '../../searchComponentData/enums';
import { DEFAULT_TAG_RESULTS_PAGE_SIZE } from '../../../../config/constants';
import Multivalue from '../common/Multivalue';

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

export function getAggQuery(field, queryString) {
  return {
    agg_type: aggTypes.FIELD,
    queried_field: field,
    values_query: queryString === '' ? null : queryString,
  };
}

export function getSearchQuery(boolType, queriedField, termQuery, termValues, termBoolType) {
  if (termQuery !== null && termQuery !== undefined && termQuery !== '') {
    if (termValues === null || termValues === undefined || termValues.length === 0) {
      return {
        bool_type: boolType,
        query_type: queryTypes.TEXT,
        queried_fields: [queriedField.field],
        query_string: termQuery,
      };
    }
  }

  return {
    bool_type: boolType,
    query_type: queryTypes.TERM,
    queried_field: queriedField.field,
    term_values: termValues.length === 0 ? null : termValues.map((termObj) => termObj.value),
    values_bool_type: termBoolType,
  };
}

export function getIsActive(selectedValues, query) {
  return (selectedValues !== null && selectedValues.length > 0) || query !== '';
}

export function getSummary(queriedField, selectedValues, query, termBoolType) {
  let summary = `Field${selectedValues == null || selectedValues.length === 0 ? ':' : ''} ${queriedField.label}`;
  let boolVerb = 'should match';
  if (termBoolType === boolTypes.MUST) boolVerb = 'must match';
  else if (termBoolType === boolTypes.MUST_NOT) boolVerb = 'exclude';
  else if (termBoolType === boolTypes.FILTER) boolVerb = 'filter';

  if (selectedValues !== null && selectedValues.length > 0) summary += ` ${boolVerb} values: ${selectedValues.map((v) => v.value).join(', ')}`;
  else if (query !== null && query !== undefined && query.length > 0) summary += `, Query: ${query}`;

  return summary;
}

class TermSearch extends Component {
  constructor(props) {
    super(props);

    if (props.componentState.availableFields.length === 1
      && props.componentState.queriedField === null) {
      props.updateCallback(
        { ...props.componentState, queriedField: 0 },
        false,
        null,
        getIsActive([], getSummary(props.componentState.availableFields[0])),
      );
    }
  }

  render() {
    const {
      componentId,
      boolType,
      updateCallback,
      componentState,
      privateAggQueries,
      getPrivateAggs,
      getAggResults,
      classes,
    } = this.props;

    const {
      queriedField,
      availableFields,
      query,
      page,
      termBoolType,
    } = componentState;

    // eslint-disable-next-line max-len
    const aggRes = queriedField === null || queriedField === undefined ? [] : getAggResults(availableFields[queriedField].field);
    const termValues = aggRes === null ? [] : aggRes.filter((value) => value);
    const selectedTermValues = componentState.selectedMetadataValues;

    const updateSelectedValues = (newSelectedValues) => {
      const newState = { ...componentState, selectedMetadataValues: newSelectedValues };
      const isActive = getIsActive(newSelectedValues, query);
      const summary = getSummary(
        availableFields[queriedField],
        newSelectedValues,
        query,
        termBoolType,
      );
      const newQuery = getSearchQuery(
        boolType,
        availableFields[queriedField],
        query,
        newSelectedValues,
        termBoolType,
      );

      updateCallback(newState, isActive, newQuery, summary);
    };

    const selectedTermValuesHandler = (updatedTermValues) => {
      const newSelectedValues = updatedTermValues.filter((v) => v.selected).map((v) => ({
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

    const termBoolTypeHandler = (e) => {
      const newState = { ...componentState, termBoolType: e.target.value };
      const isActive = getIsActive(selectedTermValues, query);
      const newQuery = getSearchQuery(
        boolType,
        availableFields[queriedField],
        query,
        selectedTermValues,
        e.target.value,
      );
      const summary = getSummary(
        availableFields[queriedField],
        selectedTermValues,
        query,
        e.target.value,
      );

      updateCallback(newState, isActive, newQuery, summary);
    };

    const updateHandler = (e, v) => {
      const newState = {
        ...componentState, queriedField: v, metadataValues: [], selectedMetadataValues: [], query: '',
      };
      const newAggQueries = {};
      newAggQueries[availableFields[v].field] = getAggQuery(availableFields[v].field, null);
      getPrivateAggs(newAggQueries);
      updateCallback(newState, false, null, '', null, newAggQueries, null);
    };

    const updateQueryHandler = (v) => {
      const newState = { ...componentState, query: v };

      const isActive = getIsActive(selectedTermValues, v);
      const newQuery = getSearchQuery(
        boolType,
        availableFields[queriedField],
        v,
        selectedTermValues,
        termBoolType,
      );
      const summary = getSummary(
        availableFields[queriedField],
        selectedTermValues,
        v,
        termBoolType,
      );
      const newAggQueries = { ...privateAggQueries };
      const newTermQuery = v === '' ? null : v;
      newAggQueries[availableFields[queriedField].field] = getAggQuery(
        availableFields[queriedField].field,
        newTermQuery,
      );

      updateCallback(newState, isActive, newQuery, summary, null, newAggQueries, null);
    };

    /* eslint-disable react/jsx-props-no-spreading */
    return (
      <div
        id={`term-search-${componentId}`}
        className={`term-search ${classes.root}`}
      >
        <Multivalue
          valueTypes={availableFields.map((fieldObj, index) => (
            { label: fieldObj.label, value: index }))}
          valueType={queriedField}
          onValueTypeChange={updateHandler}
          showValues={queriedField !== null && queriedField !== undefined}
          valuesQuery={query}
          onValuesQueryChange={updateQueryHandler}
          valuesPage={page}
          valuesPageSize={DEFAULT_TAG_RESULTS_PAGE_SIZE}
          onValuesPageChange={pageHandler}
          values={termValues.map((termValue) => (
            {
              value: termValue.value || undefined,
              count: termValue.num_docs,
              selected: selectedTermValues.map((v) => v.value).includes(termValue.value),
            }))}
          onValuesChange={selectedTermValuesHandler}
          selectedValues={selectedTermValues}
          onSelectedValuesMatchChange={termBoolTypeHandler}
          onSelectedValuesClear={() => selectedTermValuesHandler([])}
          onChipsChange={updatedChipsHandler}
          selectedValuesMatch={termBoolType}
          supportedMatchTypes={['SHOULD', 'MUST_NOT']}
        />
      </div>
    );
    /* eslint-enable react/jsx-props-no-spreading */
  }
}

TermSearch.propTypes = {
  componentId: PropTypes.string.isRequired,
  boolType: PropTypes.string.isRequired,
  updateCallback: PropTypes.func.isRequired,
  getPrivateAggs: PropTypes.func.isRequired,
  privateAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  componentState: PropTypes.shape({
    availableFields: PropTypes.arrayOf(PropTypes.shape({
      field: PropTypes.string.isRequired,
    })).isRequired,
    queriedField: PropTypes.number,
    query: PropTypes.string.isRequired,
    termBoolType: PropTypes.string.isRequired,
    page: PropTypes.number.isRequired,
    // eslint-disable-next-line react/forbid-prop-types
    selectedMetadataValues: PropTypes.any,
  }),
  getAggResults: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

TermSearch.defaultProps = {
  componentState: {
    selectedMetadataValues: [],
  },
};

const mapDispatchToProps = (dispatch) => ({
  dispatch,
});

export default connect(null, mapDispatchToProps)(withStyles(styles)(TermSearch));
