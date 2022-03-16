import React, { Component } from 'react';
import PropTypes from 'prop-types';
import withStyles from '@material-ui/core/styles/withStyles';
import { Container } from '@material-ui/core';
import Grid from '@material-ui/core/Grid';
import MetadataForm from './components/metadataForm/MetadataForm';
import UploadDisplay from './components/upload/UploadDisplay';

const styles = () => ({
  container: {
    margin: 'auto',
    marginTop: 24,
  },
});

class ForkliftUi extends Component {
  render() {
    const {
      classes,
    } = this.props;

    return (
      <Container className={`forklift-ui ${classes.container}`} maxWidth="sm">
        <Grid
          container
          spacing={2}
          direction="column"
          justifyContent="center"
          alignItems="stretch"
        >
          <Grid item xs={12}>
            <MetadataForm />
          </Grid>
          <Grid item xs={12}>
            <UploadDisplay />
          </Grid>
        </Grid>
      </Container>
    );
  }
}

ForkliftUi.propTypes = {
  classes: PropTypes.shape({
    container: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(ForkliftUi);
