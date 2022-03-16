import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Chip from '@material-ui/core/Chip';

const styles = () => ({
  root: {},
});

class MultivalueChips extends Component {
  render() {
    const {
      values,
      onChange,
    } = this.props;

    return (
      <Grid item xs={12}>
        <Grid container spacing={1}>
          {values.length === 0 ? (<div />)
            : values.map((valueObj) => (
              <Grid
                item
                key={`sel-chip-value-${valueObj.value}`}
                className="chip-search-values-selector-selected-value"
              >
                <Chip
                  size="small"
                  color="primary"
                  onDelete={() => {
                    const newSelectedValues = values
                      .filter((o) => o.value !== valueObj.value);
                    onChange(newSelectedValues);
                  }}
                  label={`${valueObj.value}${valueObj.count ? ` (${valueObj.count})` : ''}`}
                />
              </Grid>
            ))}
        </Grid>
      </Grid>
    );
  }
}

MultivalueChips.propTypes = {
  values: PropTypes.arrayOf(PropTypes.shape({
    value: PropTypes.string.isRequired,
    count: PropTypes.number,
  })).isRequired,
  onChange: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(MultivalueChips);
