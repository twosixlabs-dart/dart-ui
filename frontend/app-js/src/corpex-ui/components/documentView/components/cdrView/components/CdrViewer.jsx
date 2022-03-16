import React, { Component } from 'react';
import withStyles from '@material-ui/core/styles/withStyles';
import PropTypes from 'prop-types';
import TwoPanel from '../../../../../../common/components/layout/TwoPanel';
import ExtractionPanel from './extractionsView/components/ExtractionPanel';
import CdrTextPanel from './CdrTextPanel';

import { connect } from '../../../../../../dart-ui/context/CustomConnect';

const styles = () => ({
});

class CdrViewer extends Component {
  constructor(props) {
    super(props);
    this.addTagRefText = this.addTagRefText.bind(this);
    this.addTagRefExtr = this.addTagRefExtr.bind(this);

    this.tagRefs = {};
  }

  addTagRefText(tagType, offset, ref) {
    if (!this.tagRefs[tagType]) this.tagRefs[tagType] = { [offset]: {} };
    if (!this.tagRefs[tagType][offset]) this.tagRefs[tagType][offset] = {};
    this.tagRefs[tagType][offset].textRef = ref;
  }

  addTagRefExtr(tagType, offset, ref) {
    if (!this.tagRefs[tagType]) this.tagRefs[tagType] = { [offset]: {} };
    if (!this.tagRefs[tagType][offset]) this.tagRefs[tagType][offset] = {};
    this.tagRefs[tagType][offset].extrRef = ref;
  }

  render() {
    const { cdr, docId } = this.props;

    if (cdr === null || docId !== cdr.document_id) {
      return <div />;
    }

    return (
      <TwoPanel
        className="cdr-viewer"
        independentScroll
        relativeHeight
        squeezeRight
        left={(
          <CdrTextPanel
            docId={docId}
            addTagRefText={this.addTagRefText}
            tagRefs={this.tagRefs}
          />
        )}
        right={(
          <ExtractionPanel
            docId={docId}
            addTagRefExtr={this.addTagRefExtr}
            tagRefs={this.tagRefs}
          />
        )}
      />
    );
  }
}

CdrViewer.propTypes = {
  cdr: PropTypes.shape({
    document_id: PropTypes.string.isRequired,
  }).isRequired,
  docId: PropTypes.string.isRequired,
  match: PropTypes.shape({
    params: PropTypes.shape({
      id: PropTypes.string,
    }),
  }),
};

CdrViewer.defaultProps = {
  match: { params: {} },
};

function mapStateToProps(state) {
  return {
    cdr: state.corpex.documentView.root.cdr,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CdrViewer));
