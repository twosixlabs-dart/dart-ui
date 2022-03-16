import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';
import withStyles from '@material-ui/core/styles/withStyles';
import SearchResultsResult from './SearchResultsResult';
import SearchResultsPagination from './SearchResultsPagination';
import executeSearch from '../../searchBuilder/thunk/executeSearch.thunk';
import executeCountThunk from '../../searchBuilder/thunk/executeCount.thunk';
import { connect } from '../../../../dart-ui/context/CustomConnect';

const styles = (theme) => ({
  countOutput: {
    ...theme.typography.h6,
    marginLeft: 25,
  },
  list: {
    '& li': {
      paddingLeft: 0,
      paddingRight: 0,
    },
  },
  root: {
    height: '100%',
    overflowY: 'auto',
    paddingTop: 45,
    paddingBottom: 40,
  },
  pagination: {
    marginTop: 15,
    marginBottom: 15,
  },
  resultsWrapper: {
    maxWidth: '110ch',
    margin: 'auto',
  },
});

const getSearchQueries = (cMap, cIndex) => cMap
  .filter((v) => cIndex[v].isActive).map((v) => cIndex[v].query);

class SearchResults extends Component {
  constructor(props) {
    super(props);
    if (!props.searchExecuted) {
      const queries = getSearchQueries(props.componentMap, props.componentIndex);
      props.dispatch(executeSearch(props.xhrHandler, queries));
      props.dispatch(executeCountThunk(props.xhrHandler, queries));
    }
  }

  render() {
    const {
      tenantId,
      searchResults,
      searchError,
      errorMessage,
      dispatch,
      classes,
    } = this.props;

    const resultsArray = searchError ? (<div />)
      : searchResults.results.map((res, ind) => (
        <Grid item xs={12} key={`srch-res-row-${res.cdr.document_id}`}>
          <SearchResultsResult
            tenantId={tenantId}
            result={res}
            number={(searchResults.page * searchResults.page_size) + ind + 1}
            index={ind}
            dispatch={dispatch}
          />
        </Grid>
      ));

    const pagination = searchResults.results.length === 0 ? '' : (
      <div className={classes.pagination}>
        <Grid container direction="column" alignItems="center">
          <SearchResultsPagination />
        </Grid>
      </div>
    );

    const results = searchError ? (<div />)
      : (
        <Grid container spacing={2} direction="column" alignItems="stretch">
          {pagination}
          <Grid item xs={12}>
            <div id="search-results" className={classes.resultsWrapper}>
              <Grid container spacing={2} direction="column" alignItems="stretch">
                {resultsArray}
              </Grid>
            </div>
          </Grid>
          {pagination}
        </Grid>
      );

    const resultsElement = searchError ? (<p id="srch-err">{JSON.stringify(errorMessage)}</p>) : results;

    return (
      <div id="search-results-component" className={`search-results ${classes.root}`}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            {resultsElement}
          </Grid>
        </Grid>
      </div>
    );
  }
}

SearchResults.propTypes = {
  tenantId: PropTypes.string.isRequired,
  searchResults: PropTypes.shape({
    results: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
    page: PropTypes.number.isRequired,
    page_size: PropTypes.number,
  }).isRequired,
  searchError: PropTypes.bool.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  errorMessage: PropTypes.any,
  searchExecuted: PropTypes.bool,
  componentIndex: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  componentMap: PropTypes.arrayOf(PropTypes.string).isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
    pagination: PropTypes.string.isRequired,
    resultsWrapper: PropTypes.string.isRequired,
  }).isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

SearchResults.defaultProps = {
  searchExecuted: false,
  errorMessage: '',
};

function mapStateToProps(state, dartContext) {
  return {
    tenantId: state.dart.nav.tenantId,
    componentMap: state.corpex.searchBuilder.rootComponentMap,
    componentIndex: state.corpex.searchBuilder.componentIndex,
    searchResults: state.corpex.searchResults.searchResults,
    searchError: state.corpex.searchResults.searchError,
    errorMessage: state.corpex.searchResults.errorMessage,
    searchExecuted: state.corpex.searchResults.searchExecuted,
    xhrHandler: dartContext.xhrHandler,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(SearchResults));
