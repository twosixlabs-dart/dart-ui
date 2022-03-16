import React, { Component } from 'react';
import { Button, withStyles } from '@material-ui/core';
import PropTypes from 'prop-types';

import Toolbar from '@material-ui/core/Toolbar';
import AppBar from '@material-ui/core/AppBar';
import CorpexUi from '../corpex-ui/CorpexUi';
import ForkliftUi from '../forklift-ui/ForkliftUi';

const styles = (thm) => ({
  root: {
  },
  appWrapper: {
    height: '100%',
    paddingTop: 48,
  },
  appBar: {
    zIndex: thm.zIndex.drawer + 1,
  },
  hidden: {
    display: 'none',
  },
});

class DartUi extends Component {
  render() {
    const {
      classes,
      docView,
      documentId,
      forkliftView,
      gotoForklift,
      gotoCorpex,
    } = this.props;

    let content = '';
    if (docView) {
      content = <CorpexUi docView={docView} documentId={documentId} />;
    } else if (forkliftView) {
      content = <ForkliftUi />;
    } else content = <CorpexUi />;

    return (
      <div className={classes.appWrapper}>
        <AppBar position="fixed" className={classes.appBar}>
          <Toolbar variant="dense">
            <Button
              onClick={gotoCorpex}
            >
              CORPEX
            </Button>
            <Button
              onClick={gotoForklift}
            >
              FORKLIFT
            </Button>
          </Toolbar>
        </AppBar>
        {content}
      </div>
    );
  }
}

DartUi.propTypes = {
  gotoCorpex: PropTypes.func.isRequired,
  gotoForklift: PropTypes.func.isRequired,
  docView: PropTypes.bool.isRequired,
  documentId: PropTypes.string.isRequired,
  forkliftView: PropTypes.bool.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    appWrapper: PropTypes.string.isRequired,
    appBar: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(DartUi);
