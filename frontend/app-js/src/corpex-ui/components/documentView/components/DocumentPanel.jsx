import React, { Component } from 'react';
import { connect } from 'react-redux';
import withStyles from '@material-ui/core/styles/withStyles';
import PropTypes from 'prop-types';
// import RawDocViewer from './RawDocViewer';
import reactLazy from '../../../../common/utilities/lazyImport';
import LazyElement from '../../../../common/components/LazyElement';

const CdrViewer = reactLazy(import(/* webpackChunkName: "cdrViewer" */'./cdrView/components/CdrViewer'));
const PdfViewer = reactLazy(import(/* webpackChunkName: "pdfViewer" */'./rawView/components/PdfViewer'));

const styles = () => ({
});

class DocumentPanel extends Component {
  render() {
    const { view, docId } = this.props;

    return view === 'raw' ? (
      <LazyElement>
        <PdfViewer docId={docId} />
      </LazyElement>
    ) : (
      <LazyElement>
        <CdrViewer docId={docId} />
      </LazyElement>
    );
  }
}

DocumentPanel.propTypes = {
  view: PropTypes.string.isRequired,
  docId: PropTypes.string.isRequired,
};

function mapStateToProps(state) {
  return {
    view: state.corpex.documentView.root.view,
    cdr: state.corpex.documentView.root.cdr,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(DocumentPanel));
