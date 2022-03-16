import React, { Component } from 'react';
import withStyles from '@material-ui/core/styles/withStyles';
import PropTypes from 'prop-types';
// import StickyHeader from '../../../../common/components/layout/StickyHeader';
import CdrTextViewer from './CdrTextViewer';
// import Header from '../../../../common/components/Header';

const styles = () => ({
  panelWrapper: {
    height: '100%',
    overflowY: 'hidden',
  },
  paper: {
    padding: 20,
  },
});

class CdrTextPanel extends Component {
  render() {
    const {
      addTagRefText,
      tagRefs,
      docId,
      classes,
    } = this.props;

    // const extractionHeader = (
    //   <Header small title="Extracted Text" />
    // );

    return (
      <div className={classes.panelWrapper}>
        <CdrTextViewer
          docId={docId}
          addTagRefText={addTagRefText}
          tagRefs={tagRefs}
        />
      </div>
    );
  }
}

CdrTextPanel.propTypes = {
  classes: PropTypes.shape({
    panelWrapper: PropTypes.string.isRequired,
  }).isRequired,
  docId: PropTypes.string.isRequired,
  addTagRefText: PropTypes.func.isRequired,
  tagRefs: PropTypes.objectOf(PropTypes.objectOf(PropTypes.shape({
    textRef: PropTypes.element,
    extrRef: PropTypes.element,
  }))).isRequired,
};

export default withStyles(styles)(CdrTextPanel);
