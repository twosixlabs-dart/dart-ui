import React, { Component } from 'react';
import TextField from '@material-ui/core/TextField';
import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import PropTypes from 'prop-types';

const styles = (theme) => ({
  root: {
    padding: theme.spacing(2),
  },
  wideTextInput: {
    height: 40,
    width: 300,
  },
  textInput: {
    height: 40,
  },
  button: {
    height: 40,
  },
});

class TextQuery extends Component {
  render() {
    const {
      textValue,
      onType,
      onEnter,
      justifyContent,
      disabled,
      wideInput,
      classes,
      className,
    } = this.props;

    return (
      <div className={classes.root}>
        <Grid container direction="row" spacing={2} justifyContent={justifyContent} alignItems="center">
          <Grid item>
            <TextField
              variant="outlined"
              value={textValue}
              onChange={(e) => {
                onType(e.target.value);
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  onEnter(e.target.value);
                }
              }}
              InputProps={{
                className: `${className} ${wideInput ? classes.wideTextInput : classes.textInput}`,
              }}
              disabled={disabled}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

TextQuery.propTypes = {
  textValue: PropTypes.string.isRequired,
  onType: PropTypes.func,
  onEnter: PropTypes.func,
  justifyContent: PropTypes.string,
  disabled: PropTypes.bool,
  wideInput: PropTypes.bool,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    wideTextInput: PropTypes.string.isRequired,
    textInput: PropTypes.string.isRequired,
  }).isRequired,
  className: PropTypes.string,
};

TextQuery.defaultProps = {
  onType: () => {},
  onEnter: () => {},
  disabled: false,
  wideInput: false,
  justifyContent: 'space-evenly',
  className: 'text-query-input',
};

export default withStyles(styles)(TextQuery);
