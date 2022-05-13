import React, { Component, Suspense } from 'react';
import { connect } from 'react-redux';
import withStyles from '@material-ui/core/styles/withStyles';
import PropTypes from 'prop-types';
// import RawDocViewer from './RawDocViewer';
import FullSizeCentered from '../../../../common/components/layout/FullSizeCentered';


const CdrViewer = React.lazy(() => import(/* webpackChunkName: "cdrViewer" */'./cdrView/components/CdrViewer'));
const PdfViewer = React.lazy(() => import(/* webpackChunkName: "pdfViewer" */'./rawView/components/PdfViewer'));

const styles = () => ({
});

class DocumentPanel extends Component {
  render() {
    const { view, docId, loader } = this.props;

    return view === 'raw' ? (
      <Suspense fallback={<FullSizeCentered>{loader}</FullSizeCentered>}>
        <PdfViewer docId={docId} />
      </Suspense>
    ) : (
      <Suspense fallback={<FullSizeCentered>{loader}</FullSizeCentered>}>
        <CdrViewer docId={docId} />
      </Suspense>
    );
  }
}

DocumentPanel.propTypes = {
  view: PropTypes.string.isRequired,
  docId: PropTypes.string.isRequired,
  loader: PropTypes.node.isRequired,
};

function mapStateToProps(state, dartContext) {
  return {
    view: state.corpex.documentView.root.view,
    cdr: state.corpex.documentView.root.cdr,
    loader: dartContext.loader,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(DocumentPanel));
