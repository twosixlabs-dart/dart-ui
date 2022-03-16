import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import { boolTypes } from '../../searchComponentData/enums';

const styles = () => ({
  root: {},
  centeredButton: {
    margin: 'auto',
  },
});

const boolTypeText = (boolType) => {
  switch (boolType) {
    case boolTypes.MUST: {
      return 'All';
    }
    case boolTypes.SHOULD: {
      return 'Any';
    }
    case boolTypes.MUST_NOT: {
      return 'None';
    }
    case boolTypes.FILTER: {
      return 'All (Unscored)';
    }
    default: {
      return 'INVALID BOOLTYPE';
    }
  }
};

class MultivalueClearMatch extends Component {
  render() {
    const {
      onChange,
      value,
      onClear,
      classes,
      supportedBoolTypes,
    } = this.props;

    return (
      <Grid container spacing={4} justifyContent="center">
        <Grid item>
          <Button
            className={`clear-match-search-values-selector-clear-button ${classes.centeredButton}`}
            variant="contained"
            onClick={onClear}
          >
            CLEAR
          </Button>
        </Grid>
        <Grid item>
          <FormControl>
            <InputLabel id="list-bool-type-select-label">Match</InputLabel>
            <Select
              labelId="list-bool-type-select-label"
              className="clear-match-search-values-selector-bool-select"
              id="list-bool-type-select"
              value={value}
              onChange={onChange}
            >
              {/* eslint-disable-next-line max-len */}
              {supportedBoolTypes.map((boolType) => <MenuItem key={boolType} value={boolType}>{boolTypeText(boolType)}</MenuItem>)}
            </Select>
          </FormControl>
        </Grid>
      </Grid>
    );
  }
}

MultivalueClearMatch.propTypes = {
  supportedBoolTypes: PropTypes.arrayOf(PropTypes.string),
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  onClear: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    centeredButton: PropTypes.string.isRequired,
  }).isRequired,
};

MultivalueClearMatch.defaultProps = {
  supportedBoolTypes: [boolTypes.SHOULD, boolTypes.MUST, boolTypes.MUST_NOT],
};

export default withStyles(styles)(MultivalueClearMatch);
