import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { toPairs } from 'lodash';

import { boolTypes, componentTypes, queryTypes } from '../searchComponentData/enums';
import executeAggSearchThunk from '../thunk/executeAggSearch.thunk';
import executeCountThunk from '../thunk/executeCount.thunk';
import executeSearch from '../thunk/executeSearch.thunk';
import { updateRootComponentMap } from '../searchBuilder.actions';
import SearchBuilder from './SearchBuilder';
import { connect } from '../../../../dart-ui/context/CustomConnect';

function getAllQueriesBut(cMap, cIndex, id) {
  return cMap
    .filter((v) => v !== null
      && cIndex[v] !== null
      && cIndex[v] !== undefined)
    .filter((v) => cIndex[v].isActive
      && v !== id).map((v) => {
      if (cIndex[v].componentType === componentTypes.BOOL_SEARCH) {
        return {
          bool_type: cIndex[v].boolType,
          query_type: queryTypes.BOOL,
          queries: getAllQueriesBut(cIndex[v].componentState.componentMap, cIndex, id),
        };
      }

      return cIndex[v].query;
    });
}

class SearchBuilderRoot extends Component {
  constructor(props) {
    super(props);
    this.executePrivateAggQueries = this.executePrivateAggQueries.bind(this);
  }

  componentDidMount() {
    const { componentIndex } = this.props;
    this.executePrivateAggQueries(componentIndex);
  }

  executePrivateAggQueries(cIndex) {
    const {
      dispatch,
      rootComponentMap,
      xhrHandler,
    } = this.props;

    toPairs(cIndex).forEach(([id, cState]) => {
      const queries = getAllQueriesBut(rootComponentMap, cIndex, id);
      const aggs = {};
      toPairs(cState.privateAggQueries).forEach(([label, query]) => {
        aggs[`${id}_${label}`] = query;
      });

      if (Object.keys(aggs).length > 0) {
        dispatch(executeAggSearchThunk(
          xhrHandler, queries, null, aggs,
        ));
      }
    });
  }

  render() {
    const {
      dispatch,
      rootComponentMap,
      componentIndex,
      getResults,
      browseResults,
      corpusOverviewAggQueries,
      xhrHandler,
    } = this.props;

    const searchBuilderCallback = (boolType, queries, commonAggs, cIndex) => {
      if (queries) {
        dispatch(executeCountThunk(xhrHandler, queries));
        if (!browseResults) {
          dispatch(executeSearch(xhrHandler, queries, corpusOverviewAggQueries));
        } else if (getResults) dispatch(executeSearch(xhrHandler, queries));
        else if (commonAggs) dispatch(executeAggSearchThunk(xhrHandler, queries, commonAggs));

        this.executePrivateAggQueries(
          cIndex === null || cIndex === undefined ? componentIndex : cIndex,
        );
      }
    };

    const updateCallback = (newState) => {
      dispatch(updateRootComponentMap(newState.componentMap));
    };

    return (
      <SearchBuilder
        address={[]}
        boolType={boolTypes.MUST}
        searchBuilderCallback={searchBuilderCallback}
        executePrivateAggQueries={this.executePrivateAggQueries}
        updateCallback={updateCallback}
        componentIndex={componentIndex}
        componentState={{ componentMap: rootComponentMap }}
      />
    );
  }
}

SearchBuilderRoot.propTypes = {
  dispatch: PropTypes.func.isRequired,
  rootComponentMap: PropTypes.arrayOf(PropTypes.string).isRequired,
  componentIndex: PropTypes.objectOf(PropTypes.shape({})).isRequired,
  getResults: PropTypes.bool.isRequired,
  browseResults: PropTypes.bool.isRequired,
  corpusOverviewAggQueries: PropTypes.shape({}).isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

function mapStateToProps(state, dartContext) {
  return {
    rootComponentMap: state.corpex.searchBuilder.rootComponentMap,
    componentIndex: state.corpex.searchBuilder.componentIndex,
    browseResults: state.corpex.searchDisplay.browseResults,
    corpusOverviewAggQueries: state.corpex.corpusView.aggQueries,
    xhrHandler: dartContext.xhrHandler,
  };
}

export default connect(mapStateToProps)(SearchBuilderRoot);
