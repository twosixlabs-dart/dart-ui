import React, { Component } from 'react';

import PropTypes from 'prop-types';

import reactLazy from '../../../../common/utilities/lazyImport';
import LazyElement from '../../../../common/components/LazyElement';
import getApiData from '../thunk/getApiData.thunk';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const SearchDisplay = reactLazy(import('../../searchDisplay/components/SearchDisplay'));
const DocumentViewer = reactLazy(import('../../documentView/components/DocumentViewer'));

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
    } = this.props;

    if (Object.keys(tags).length === 0
      || Object.keys(facets).length === 0
      || Object.keys(fields).length === 0) {
      return <div>Loading</div>;
    }

    const {
      docView,
      documentId,
    } = this.props;

    if (docView) {
      return (
        <LazyElement>
          <DocumentViewer documentId={documentId} />
        </LazyElement>
      );
    }

    return (
      <LazyElement>
        <SearchDisplay />
      </LazyElement>
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
  };
}

export default connect(mapStateToProps)(CorpexRoot);
