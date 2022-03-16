import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Paper from '@material-ui/core/Paper';
import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';
import UploadProgress from './UploadProgress/UploadProgress';
import UploadForm from './UploadForm';

const styles = () => ({
  paper: {
    padding: 10,
  },
  extraPadding: {
    paddingTop: 10,
    paddingBottom: 10,
  },
});

class UploadDisplay extends Component {
  render() {
    const {
      classes,
    } = this.props;

    return (
      <Paper classes={{ root: classes.paper }}>
        <div className={classes.extraPadding}>
          <Grid container direction="column" alignItems="stretch" spacing={2}>
            <Grid item xs={12}>
              <Grid container direction="column" alignItems="center" spacing={2}>
                <UploadForm />
              </Grid>
            </Grid>
            <Grid item xs={12}>
              <UploadProgress />
            </Grid>
          </Grid>
        </div>
      </Paper>
    );
  }
}

UploadDisplay.propTypes = {
  classes: PropTypes.shape({
    extraPadding: PropTypes.string.isRequired,
    paper: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(UploadDisplay);
