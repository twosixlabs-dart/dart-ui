import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Pagination from '@material-ui/lab/Pagination';
import { withStyles } from '@material-ui/core/styles';
import getSearchPage from '../thunk/getSearchPage.thunk';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = {
  root: {},
};

class SearchResultsPagination extends Component {
  render() {
    const { searchResults, dispatch, xhrHandler } = this.props;

    return (
      <div className="search-results-pagination">
        <Pagination
          count={searchResults.num_pages}
          page={searchResults.page + 1}
          onChange={(e, p) => dispatch(getSearchPage(xhrHandler, p - 1))}
          showFirstButton
          showLastButton
        />
      </div>
    );
  }
}

SearchResultsPagination.propTypes = {
  dispatch: PropTypes.func.isRequired,
  searchResults: PropTypes.shape({
    num_pages: PropTypes.number.isRequired,
    page: PropTypes.number.isRequired,
  }).isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

function mapStateToProps(state, dartContext) {
  return {
    searchResults: state.corpex.searchResults.searchResults,
    xhrHandler: dartContext.xhrHandler,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(SearchResultsPagination));
