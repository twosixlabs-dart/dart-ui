import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import RangeInput from '../../../../../common/components/RangeInput';

const styles = () => ({
  root: {},
});

class MultivalueSlider extends Component {
  render() {
    const {
      values,
      onChange,
      onChangeCommitted,
    } = this.props;

    return (
      <Grid
        container
        direction="row"
        spacing={2}
        justifyContent="center"
        alignItems="center"
        className="search-values-query-range"
      >
        <RangeInput
          domain={[0, 1]}
          values={values}
          onChange={onChange}
          onChangeCommitted={onChangeCommitted}
          disabled={false}
          step={0.05}
        />
      </Grid>
    );
  }
}

MultivalueSlider.propTypes = {
  values: PropTypes.arrayOf(PropTypes.number).isRequired,
  onChange: PropTypes.func.isRequired,
  onChangeCommitted: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(MultivalueSlider);
