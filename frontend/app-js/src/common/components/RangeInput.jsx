import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Slider from '@material-ui/core/Slider';
import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';

const styles = (theme) => ({
  root: {
    padding: theme.spacing(2),
  },
  button: {
    height: 40,
  },
  sliderWrapper: {
    display: 'flex',
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
    flexDirection: 'column',
    height: 40,
    borderWidth: 1,
    borderStyle: 'solid',
    borderRadius: 4,
    borderColor: 'rgba(0, 0, 0, 0.23)',
    '&:hover': {
      borderColor: 'black',
    },
    '&:focus-within': {
      borderColor: theme.palette.primary.main,
      borderWidth: 2,
    },
  },
  sliderSubWrapper: {
    display: 'flex',
    alignItems: 'center',
    flexDirection: 'row',
    width: '80%',
  },
});

class RangeInput extends Component {
  render() {
    const {
      domain,
      values,
      onChange,
      onChangeCommitted,
      format,
      step,
      disabled,
      width,
      classes,
      justifyContent,
    } = this.props;

    const sliderMarks = [
      { value: domain[0], label: format(domain[0]) },
      { value: domain[1], label: format(domain[1]) },
    ];

    return (
      <div className={classes.root} style={{ width }}>
        <Grid container direction="row" justifyContent={justifyContent} spacing={2}>
          <Grid item xs={12}>
            <div className={classes.sliderWrapper}>
              <div className={classes.sliderSubWrapper}>
                <Slider
                  min={domain[0]}
                  max={domain[1]}
                  valueLabelDisplay="auto"
                  valueLabelFormat={format}
                  step={step}
                  value={values}
                  variant="outlined"
                  onChange={(e, v) => onChange(v)}
                  onChangeCommitted={(e, v) => onChangeCommitted(v)}
                  disabled={disabled}
                  marks={sliderMarks}
                />
              </div>
            </div>
          </Grid>
        </Grid>
      </div>
    );
  }
}

RangeInput.propTypes = {
  justifyContent: PropTypes.string,
  domain: PropTypes.arrayOf(PropTypes.number).isRequired,
  values: PropTypes.arrayOf(PropTypes.number).isRequired,
  onChange: PropTypes.func.isRequired,
  onChangeCommitted: PropTypes.func,
  format: PropTypes.func,
  step: PropTypes.number,
  disabled: PropTypes.bool,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    sliderSubWrapper: PropTypes.string.isRequired,
    sliderWrapper: PropTypes.string.isRequired,
  }).isRequired,
  width: PropTypes.oneOf([PropTypes.number, PropTypes.string]),
};

RangeInput.defaultProps = {
  disabled: false,
  onChangeCommitted: () => {},
  format: (v) => v,
  step: 1,
  width: 300,
  justifyContent: 'space-evenly',
};

export default withStyles(styles)(RangeInput);
