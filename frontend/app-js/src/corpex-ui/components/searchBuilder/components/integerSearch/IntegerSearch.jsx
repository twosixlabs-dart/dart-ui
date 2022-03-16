import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Input from '@material-ui/core/Input';
import { connect } from 'react-redux';
import RangeInput from '../../../../../common/components/RangeInput';
import { aggTypes, queryTypes } from '../../searchComponentData/enums';
import executeAggSearchThunk from '../../thunk/executeAggSearch.thunk';

function getSearchQuery(boolType, intLo, intHi, queriedFields) {
  return {
    bool_type: boolType,
    query_type: queryTypes.INTEGER,
    queried_fields: queriedFields,
    int_hi: intHi,
    int_lo: intLo,
  };
}

function getAggQuery(queriedField) {
  return {
    agg_type: aggTypes.FIELD,
    queried_field: queriedField,
  };
}

class IntegerSearch extends Component {
  render() {
    const {
      componentId,
      boolType,
      updateCallback,
      privateAggQueries,
      getAggResults,
      componentState,
      dispatch,
    } = this.props;

    const {
      intLo,
      intHi,
      availableFields,
      queriedFields,
    } = componentState;

    const newAggQueries = {};
    // eslint-disable-next-line no-prototype-builtins
    const fieldsForUpdate = availableFields
      .filter((field) => getAggResults(
        field.field,
        // eslint-disable-next-line no-prototype-builtins
      ) === null && !privateAggQueries.hasOwnProperty(field.field));

    fieldsForUpdate.forEach((field) => {
      newAggQueries[`${componentId}_${field.field}`] = getAggQuery(field.field);
    });

    if (fieldsForUpdate.length > 0) {
      // updateCallback( null, null, null, null, null, newAggQueries, null )
      dispatch(executeAggSearchThunk([], {}, newAggQueries));
    }

    const allIntLos = queriedFields.flatMap((field) => {
      const results = getAggResults(availableFields[field].field);
      if (results) return results.map((valObj) => valObj.lo);
      return [];
    });

    const allIntHis = queriedFields.flatMap((field) => {
      const results = getAggResults(availableFields[field].field);
      if (results) return results.map((valObj) => valObj.hi);
      return [];
    });

    const limitLo = allIntLos.length > 0 ? Math.min(...allIntLos) : 0;
    const limitHi = allIntHis.length > 0 ? Math.max(...allIntHis) : 10;

    const fieldsHandler = (e) => {
      const v = e.target.value;

      const newState = { ...componentState, queriedFields: [v] };
      const isActive = intLo !== limitLo || intHi !== limitHi;
      const summary = `${availableFields[v].label}: ${intLo} to ${intHi}`;

      updateCallback(
        newState,
        isActive,
        getSearchQuery(
          boolType,
          intLo,
          intHi,
          [availableFields[v].field],
        ),
        summary,
        null,
        null,
        null,
      );
    };

    const updateValuesHandler = (v) => {
      const newState = { ...componentState, intLo: v[0], intHi: v[1] };

      updateCallback(newState);
    };

    const updateQueryHandler = (v) => {
      const newState = { ...componentState, intLo: v[0], intHi: v[1] };
      const isActive = queriedFields !== null
        && queriedFields.length > 0
        && (v[0] !== limitLo || v[1] !== limitHi);
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
      intLo === null || intLo < limitLo ? limitLo : intLo,
      intHi === null || intHi > limitHi ? limitHi : intHi,
    ];

    return (
      <div className="integer-search">
        <Grid item xs={12}>
          <Grid container direction="row" spacing={1} justifyContent="center">
            <Grid item>
              <FormControl>
                <InputLabel id="date-field-select-label">Length Field</InputLabel>
                <Select
                  labelId="date-field-select-label"
                  id="date-field-select"
                  value={queriedFields[0]}
                  onChange={fieldsHandler}
                  input={<Input />}
                >
                  {availableFields.map((intObj, index) => (
                    <MenuItem key={intObj.label} value={index}>
                      {intObj.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item>
              <RangeInput
                domain={[limitLo, limitHi]}
                values={sliderValues}
                onChange={updateValuesHandler}
                onChangeCommitted={updateQueryHandler}
                disabled={queriedFields === null || queriedFields.length === 0}
              />
            </Grid>
          </Grid>
        </Grid>

      </div>
    );
  }
}

IntegerSearch.propTypes = {
  componentId: PropTypes.string.isRequired,
  boolType: PropTypes.string.isRequired,
  updateCallback: PropTypes.func.isRequired,
  privateAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  getAggResults: PropTypes.func.isRequired,
  dispatch: PropTypes.func.isRequired,
  componentState: PropTypes.shape({
    intLo: PropTypes.number.isRequired,
    intHi: PropTypes.number.isRequired,
    queriedFields: PropTypes.arrayOf(PropTypes.string).isRequired,
    availableFields: PropTypes.arrayOf(PropTypes.shape({
      label: PropTypes.string.isRequired,
      field: PropTypes.string.isRequired,
    })),
  }).isRequired,
  classes: PropTypes.shape({
  }).isRequired,
};

const mapDispatchToProps = (dispatch) => ({
  dispatch,
});

export default connect(null, mapDispatchToProps)(IntegerSearch);
