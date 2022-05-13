import React, { Component, Suspense } from 'react';

import PropTypes from 'prop-types';

import FullSizeCentered from '../../../../common/components/layout/FullSizeCentered';
import getApiData from '../thunk/getApiData.thunk';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const SearchDisplay = React.lazy(() => import(/* webpackChunkName: "searchDisplay" */ '../../searchDisplay/components/SearchDisplay'));
const DocumentViewer = React.lazy(() => import(/* webpackChunkName: "documentViewer" */ '../../documentView/components/DocumentViewer'));

class CorpexRoot extends Component {
  componentDidMount() {
    const {
      tags,
      facets,
      fields,
      dispatch,
      xhrHandler,
    } = this.props;

    if (Object.keys(tags).length === 0
      || Object.keys(facets).length === 0
      || Object.keys(fields).length === 0) {
      dispatch(getApiData(xhrHandler));
    }
  }

  render() {
    const {
      tags,
      facets,
      fields,
      loader,
    } = this.props;

    if (Object.keys(tags).length === 0
      || Object.keys(facets).length === 0
      || Object.keys(fields).length === 0) {
      return <FullSizeCentered>{loader}</FullSizeCentered>;
    }

    const {
      docView,
      documentId,
    } = this.props;

    if (docView) {
      return (
        <Suspense fallback={<FullSizeCentered>{loader}</FullSizeCentered>}>
          <DocumentViewer documentId={documentId} />
        </Suspense>
      );
    }

    return (
      <Suspense fallback={<FullSizeCentered>{loader}</FullSizeCentered>}>
        <SearchDisplay />
      </Suspense>
    );
  }
}

CorpexRoot.propTypes = {
  docView: PropTypes.bool.isRequired,
  documentId: PropTypes.string,
  tags: PropTypes.shape({}).isRequired,
  facets: PropTypes.shape({}).isRequired,
  fields: PropTypes.shape({}).isRequired,
  dispatch: PropTypes.func.isRequired,
  xhrHandler: PropTypes.func.isRequired,
  loader: PropTypes.func.isRequired,
};

CorpexRoot.defaultProps = {
  documentId: '',
};

function mapStateToProps(state, dartContext) {
  return {
    tags: state.corpex.corpexRoot.tags,
    facets: state.corpex.corpexRoot.facets,
    fields: state.corpex.corpexRoot.fields,
    xhrHandler: dartContext.xhrHandler,
    loader: dartContext.loader,
  };
}

export default connect(mapStateToProps)(CorpexRoot);
