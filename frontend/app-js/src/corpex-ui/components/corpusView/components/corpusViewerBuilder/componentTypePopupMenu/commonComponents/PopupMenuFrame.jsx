import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import Button from '@material-ui/core/Button';
import { Grid } from '@material-ui/core';

import corpusViewPropTypes from '../../../../corpusView.propTypes';

const styles = () => ({
  root: {},
});

function PopupMenuFrame({
  addComponent,
  data,
  closePopup,
  children,
  classes,
}) {
  const submitHandler = () => {
    addComponent(data);
    closePopup();
  };

  return (
    <div className={classes.root}>
      <Grid container dir="column" spacing={4}>
        <Grid item xs={12}>{children}</Grid>
        <Grid item>
          <Button
            color="primary"
            variant="contained"
            onClick={submitHandler}
          >
            Add
          </Button>
        </Grid>
      </Grid>
    </div>
  );
}

PopupMenuFrame.propTypes = {
  addComponent: PropTypes.func.isRequired,
  data: corpusViewPropTypes.componentData.isRequired,
  closePopup: PropTypes.func.isRequired,
  children: PropTypes.node.isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

const mapDispatchToProps = () => ({});

export default connect(mapDispatchToProps)(withStyles(styles)(PopupMenuFrame));
