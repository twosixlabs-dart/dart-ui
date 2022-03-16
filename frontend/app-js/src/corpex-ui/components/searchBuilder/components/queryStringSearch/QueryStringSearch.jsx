import React, { Component } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import Input from '@material-ui/core/Input';
import MenuItem from '@material-ui/core/MenuItem';
import TextQuery from '../../../../../common/components/TextQuery';
import { queryTypes } from '../../searchComponentData/enums';

const styles = () => ({
  root: {},
});

function getSearchQuery(boolType, query, queriedFields) {
  return {
    bool_type: boolType,
    query_type: queryTypes.TEXT,
    queried_fields: queriedFields,
    query_string: query,
  };
}

class QueryStringSearch extends Component {
  render() {
    const {
      boolType, componentState, updateCallback,
    } = this.props;
    const {
      query,
      availableFields,
      queriedFields,
    } = componentState;

    const fieldsHandler = (e) => {
      const v = e.target.value;

      const newState = { ...componentState, queriedFields: v };
      const isActive = v !== null && v.length !== 0 && query !== null && query !== '';
      const summary = `${v.map((field) => availableFields[field].label).join(', ')}: ${query}`;

      updateCallback(
        newState,
        isActive,
        getSearchQuery(boolType, query, v.map((field) => availableFields[field].field)),
        summary,
      );
    };

    const updateHandler = (v) => {
      const newState = { ...componentState, query: v };
      const isActive = queriedFields !== null && queriedFields.length !== 0 && v !== null && v !== '';
      const summary = `${queriedFields.map((field) => availableFields[field].label).join(', ')}: ${v}`;

      updateCallback(
        newState,
        isActive,
        getSearchQuery(boolType, v, queriedFields.map((field) => availableFields[field].field)),
        summary,
      );
    };

    return (
      <div className="string-search">
        <Grid container direction="row" spacing={1} justifyContent="center">
          <Grid item>
            <FormControl>
              <InputLabel id="text-field-select-label">Text Field</InputLabel>
              <Select
                labelId="text-field-select-label"
                id="text-field-select"
                multiple
                value={queriedFields}
                onChange={fieldsHandler}
                input={<Input />}
                className="string-search-select"
              >
                {availableFields.map((queryObj, index) => (
                  <MenuItem key={queryObj.label} value={index}>
                    {queryObj.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item>
            <TextQuery
              textValue={query}
              onType={updateHandler}
              wideInput
              disabled={queriedFields === null || queriedFields.length === 0}
              className="string-search-input"
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

QueryStringSearch.propTypes = {
  boolType: PropTypes.string.isRequired,
  updateCallback: PropTypes.func.isRequired,
  componentState: PropTypes.shape({
    query: PropTypes.string.isRequired,
    availableFields: PropTypes.arrayOf(PropTypes.shape({
      field: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
    })).isRequired,
    queriedFields: PropTypes.arrayOf(PropTypes.number).isRequired,
  }).isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(QueryStringSearch);
