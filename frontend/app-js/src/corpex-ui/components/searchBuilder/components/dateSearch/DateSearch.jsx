import React, { Component } from 'react';
import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Input from '@material-ui/core/Input';
import PropTypes from 'prop-types';
import executeAggSearchThunk from '../../thunk/executeAggSearch.thunk';
import RangeInput from '../../../../../common/components/RangeInput';
import { aggTypes, queryTypes } from '../../searchComponentData/enums';

import { connect } from '../../../../../dart-ui/context/CustomConnect';

function getMsFromYear(year, lower) {
  const date = new Date();
  date.setHours(0, 0, 0, 0);
  if (lower) date.setFullYear(year, 0, 1);
  else date.setFullYear(year + 1, 0, 1);
  return Math.round(date.getTime());
}

function getSearchQuery(boolType, lower, upper, queriedFields) {
  return {
    bool_type: boolType,
    query_type: queryTypes.CDR_DATE,
    queried_fields: queriedFields,
    date_hi: getMsFromYear(upper, false),
    date_lo: getMsFromYear(lower, true),
  };
}

function getAggQuery(queriedField) {
  return {
    agg_type: aggTypes.FIELD,
    queried_field: queriedField,
    bucket_size: '1y',
  };
}

class DateSearch extends Component {
  render() {
    const {
      componentId,
      boolType,
      updateCallback,
      componentState,
      getAggResults,
      xhrHandler,
      dispatch,
    } = this.props;

    const {
      lowerBound,
      upperBound,
      availableFields,
      queriedFields,
    } = componentState;

    const newAggQueries = {};
    const fieldsForUpdate = availableFields.filter((field) => getAggResults(field.field) === null);

    fieldsForUpdate.forEach((field) => {
      newAggQueries[`${componentId}_${field.field}`] = getAggQuery(field.field);
    });

    if (fieldsForUpdate.length > 0) {
      // updateCallback( null, null, null, null, null, newAggQueries, null )
      dispatch(executeAggSearchThunk(xhrHandler, [], {}, newAggQueries));
    }

    const allDateLowers = queriedFields.flatMap((field) => {
      const results = getAggResults(availableFields[field].field);
      if (results) return results.map((valObj) => new Date(valObj.lo).getFullYear());
      return [];
    });

    const allDateUppers = queriedFields.flatMap((field) => {
      const results = getAggResults(availableFields[field].field);
      if (results) return results.map((valObj) => new Date(valObj.hi).getFullYear());
      return [];
    });

    const lowerLimit = allDateLowers.length > 0 ? Math.min(...allDateLowers) : 1980;
    const upperLimit = allDateUppers.length > 0
      ? Math.max(...allDateUppers) + 1 : new Date().getFullYear();

    const fieldsHandler = (e) => {
      const v = e.target.value;

      const newState = { ...componentState, queriedFields: v };
      const isActive = lowerBound !== lowerLimit || upperBound !== upperLimit;
      const summary = `${v.map((field) => availableFields[field].label).join(', ')}: ${lowerBound} to ${upperBound}`;

      updateCallback(
        newState,
        isActive,
        getSearchQuery(
          boolType,
          lowerBound,
          upperBound,
          v.map((field) => availableFields[field].field),
        ),
        summary,
        null,
        null,
        null,
      );
    };

    const updateValuesHandler = (v) => {
      const newState = { ...componentState, lowerBound: v[0], upperBound: v[1] };

      updateCallback(newState);
    };

    const updateQueryHandler = (v) => {
      const newState = { ...componentState, lowerBound: v[0], upperBound: v[1] };
      const isActive = queriedFields !== null
        && queriedFields.length > 0
        && (v[0] !== lowerLimit || v[1] !== upperLimit);
      const summary = `${queriedFields.map((field) => availableFields[field].label).join(', ')}: ${v[0]} to ${v[1]}`;

      updateCallback(
        newState,
        isActive,
        getSearchQuery(
          boolType,
          v[0],
          v[1],
          queriedFields.map((field) => availableFields[field].field),
        ),
        summary,
        null,
        null,
        null,
      );
    };

    const sliderValues = [
      lowerBound === null || lowerBound < lowerLimit ? lowerLimit : lowerBound,
      upperBound === null || upperBound > upperLimit ? upperLimit : upperBound,
    ];

    return (
      <div className="date-search">
        <Grid item xs={12}>
          <Grid container direction="row" spacing={1} justifyContent="center">
            <Grid item>
              <FormControl>
                <InputLabel id="date-field-select-label">Date Field</InputLabel>
                <Select
                  labelId="date-field-select-label"
                  id="date-field-select"
                  multiple
                  value={queriedFields}
                  onChange={fieldsHandler}
                  input={<Input />}
                  className="date-search-field-select"
                >
                  {availableFields.map((dateObj, index) => (
                    <MenuItem key={dateObj.label} value={index}>
                      {dateObj.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item>
              <RangeInput
                domain={[lowerLimit, upperLimit]}
                values={sliderValues}
                onChange={updateValuesHandler}
                onChangeCommitted={updateQueryHandler}
                disabled={queriedFields == null || queriedFields.length === 0}
              />
            </Grid>
          </Grid>
        </Grid>

      </div>
    );
  }
}

DateSearch.propTypes = {
  componentId: PropTypes.string.isRequired,
  boolType: PropTypes.string.isRequired,
  updateCallback: PropTypes.func.isRequired,
  componentState: PropTypes.shape({
    lowerBound: PropTypes.number,
    upperBound: PropTypes.number,
    queriedFields: PropTypes.arrayOf(PropTypes.number).isRequired,
    availableFields: PropTypes.arrayOf(PropTypes.shape({
      field: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
    })).isRequired,
  }).isRequired,
  getAggResults: PropTypes.func.isRequired,
  xhrHandler: PropTypes.func.isRequired,
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = (state, dartContext) => ({
  xhrHandler: dartContext.xhrHandler,
});

export default connect(mapStateToProps)(DateSearch);
