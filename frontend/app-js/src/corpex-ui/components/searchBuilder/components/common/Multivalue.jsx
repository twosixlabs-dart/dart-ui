import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';

import Grid from '@material-ui/core/Grid';
import Divider from '@material-ui/core/Divider';
import MultivalueAggregation from './MultivalueAggregation';
import MultivalueChips from './MultivalueChips';
import MultivalueClearMatch from './MultivalueClearMatch';
import MultivalueSearch from './MultivalueSearch';
import MultivalueSelector from './MultivalueSelector';
import MultivalueSlider from './MultivalueSlider';
import { boolTypes } from '../../searchComponentData/enums';

const styles = (theme) => ({
  root: {
    padding: 15,
    paddingTop: 0,
    marginTop: theme.spacing(2),
    marginBottom: theme.spacing(1),
  },
  selectedTagValues: {
    display: 'flex',
    justifyContent: 'left',
    flexWrap: 'wrap',
    '& > *': {
      margin: theme.spacing(1),
    },
  },
  tagDivider: {
    marginTop: 20,
    marginBottom: 20,
  },
  centeredButton: {
    margin: 'auto',
  },
});

class Multivalue extends Component {
  render() {
    const {
      valueTypes,
      valueType,
      onValueTypeChange,
      showValues,
      confidenceValues,
      onConfidenceValuesChange,
      onConfidenceValuesChangeCommitted,
      useQuery,
      valuesQuery,
      onValuesQueryChange,
      valuesPage,
      valuesPageSize,
      onValuesPageChange,
      values,
      onValuesChange,
      onSelectedValuesMatchChange,
      onSelectedValuesClear,
      onChipsChange,
      selectedValuesMatch,
      supportedMatchTypes,
      classes,
    } = this.props;

    const selectedAggValues = values.filter((valueObj) => valueObj.selected);

    const valuesComponent = (
      <>
        {confidenceValues.length === 2 ? (
          <MultivalueSlider
            onChange={onConfidenceValuesChange}
            onChangeCommitted={onConfidenceValuesChangeCommitted}
            values={confidenceValues}
          />
        ) : ''}
        {useQuery ? (
          <MultivalueSearch onChange={onValuesQueryChange} query={valuesQuery} />
        ) : ''}
        <MultivalueAggregation
          page={valuesPage}
          pageSize={valuesPageSize}
          onPageChange={onValuesPageChange}
          onChange={onValuesChange}
          values={values}
        />
        {selectedAggValues.length === 0 ? '' : (<Grid className={classes.tagDivider} item xs={12}><Divider /></Grid>)}
        {selectedAggValues.length === 0 ? '' : (
          <Grid container direction="row" alignItems="center" alignContent="center" justifyContent="flex-start" spacing={2}>
            <Grid item xs={12}>
              <MultivalueClearMatch
                onChange={onSelectedValuesMatchChange}
                value={selectedValuesMatch}
                onClear={onSelectedValuesClear}
                supportedBoolTypes={supportedMatchTypes}
              />
            </Grid>
            <MultivalueChips
              values={selectedAggValues}
              onChange={onChipsChange}
            />
          </Grid>
        )}
      </>
    );

    return (
      <div
        className={`search-values-selector ${classes.root}`}
      >
        {valueTypes.length > 0 ? (
          <MultivalueSelector
            value={valueType}
            onChange={onValueTypeChange}
            possibleValues={valueTypes}
          />
        ) : ''}
        {showValues ? valuesComponent : ''}
      </div>
    );
  }
}

Multivalue.propTypes = {
  valueTypes: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    // eslint-disable-next-line react/forbid-prop-types
    value: PropTypes.any.isRequired,
    description: PropTypes.string,
  })),
  // eslint-disable-next-line react/forbid-prop-types
  valueType: PropTypes.any,
  onValueTypeChange: PropTypes.func.isRequired,
  showValues: PropTypes.bool,
  confidenceValues: PropTypes.arrayOf(PropTypes.number),
  onConfidenceValuesChange: PropTypes.func,
  onConfidenceValuesChangeCommitted: PropTypes.func,
  useQuery: PropTypes.bool,
  valuesQuery: PropTypes.string,
  onValuesQueryChange: PropTypes.func,
  valuesPage: PropTypes.number.isRequired,
  valuesPageSize: PropTypes.number.isRequired,
  onValuesPageChange: PropTypes.func.isRequired,
  // eslint-disable-next-line react/require-default-props
  values: PropTypes.arrayOf(PropTypes.shape({
    value: PropTypes.string.isRequired,
    count: PropTypes.number,
    selected: PropTypes.bool.isRequired,
  })),
  onValuesChange: PropTypes.func.isRequired,
  onSelectedValuesMatchChange: PropTypes.func.isRequired,
  onSelectedValuesClear: PropTypes.func.isRequired,
  selectedValuesMatch: PropTypes.string.isRequired,
  onChipsChange: PropTypes.func.isRequired,
  supportedMatchTypes: PropTypes.arrayOf(PropTypes.string),
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    centeredButton: PropTypes.string.isRequired,
    tagDivider: PropTypes.string.isRequired,
  }).isRequired,
};

Multivalue.defaultProps = {
  supportedMatchTypes: [boolTypes.SHOULD, boolTypes.MUST, boolTypes.MUST_NOT],
  values: [],
  showValues: true,
  confidenceValues: [],
  valueType: null,
  valueTypes: [],
  useQuery: true,
  valuesQuery: '',
  onValuesQueryChange: () => {},
  onConfidenceValuesChange: () => {},
  onConfidenceValuesChangeCommitted: () => {},
};

export default withStyles(styles)(Multivalue);
