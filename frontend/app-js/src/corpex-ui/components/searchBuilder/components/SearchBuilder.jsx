import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { toPairs } from 'lodash';

import { withStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import {
  addComponent,
  removeComponent,
  toggleEdited,
  updateBoolType,
  updateComponent,
} from '../searchBuilder.actions';
import { componentTypes, queryTypes } from '../searchComponentData/enums';
import uuidv4 from '../../../../common/utilities/helpers';
import SearchBuilderAdder from './SearchBuilderAdder';
// eslint-disable-next-line import/no-cycle
import SearchComponent from './SearchComponent';

const styles = {
  root: {},
};

const getSubSearchQueries = (cMap, cIndex) => cMap
  .filter((v) => v !== null
    && cIndex[v] !== null
    && cIndex[v] !== undefined)
  .filter((v) => cIndex[v].isActive)
  .map((v) => cIndex[v].query);

const getSubAggQueries = (cMap, cIndex) => {
  const queries = {};
  cMap.filter((id) => id !== null && cIndex[id] !== null && cIndex[id] !== undefined)
    .forEach((id) => {
      toPairs(cIndex[id].commonAggQueries).forEach(([label, aggQuery]) => {
        if (cIndex[id].componentType === componentTypes.BOOL_SEARCH) queries[label] = aggQuery;
        else queries[`${id}_${label}`] = aggQuery;
      });
    });

  return queries;
};

function getSearchQuery(boolType, queries) {
  return {
    bool_type: boolType,
    query_type: queryTypes.BOOL,
    queries,
  };
}

class SearchBuilder extends Component {
  render() {
    const {
      boolType,
      dispatch,
      updateCallback,
      executePrivateAggQueries,
      searchBuilderCallback,
      componentIndex,
      componentState,
      aggregations,
    } = this.props;

    const {
      componentMap,
    } = componentState;

    const generateUpdateCallback = (id) => (
      newState,
      isActive,
      query,
      summary,
      commonAggQueries,
      privateAggQueries,
      cIndex,
    ) => {
      const tmpIndex = { ...componentIndex };
      let cIndexParam = cIndex;
      if (commonAggQueries !== null
        && commonAggQueries !== undefined) tmpIndex[id].commonAggQueries = commonAggQueries;
      if (privateAggQueries !== null && privateAggQueries !== undefined) {
        tmpIndex[id].privateAggQueries = privateAggQueries;
        cIndexParam = tmpIndex;
      }
      if (isActive !== null && isActive !== undefined) tmpIndex[id].isActive = isActive;

      if (query) {
        tmpIndex[id].query = query;

        const searchQueries = getSubSearchQueries(componentMap, tmpIndex);
        const allAggQueries = commonAggQueries !== null && commonAggQueries !== undefined
          ? getSubAggQueries(componentMap, tmpIndex) : null;
        const boolQueryIsActive = componentMap.filter((v) => tmpIndex[v].isActive).length > 0;

        searchBuilderCallback(boolType, searchQueries, allAggQueries, cIndexParam);
        updateCallback(
          componentState,
          boolQueryIsActive,
          getSearchQuery(boolType, searchQueries),
          null,
          allAggQueries,
          null,
          cIndexParam,
        );
      } else if (commonAggQueries) {
        const searchQueries = getSubSearchQueries(componentMap, tmpIndex);
        const allAggQueries = getSubAggQueries(componentMap, tmpIndex);
        searchBuilderCallback(boolType, searchQueries, allAggQueries, cIndexParam);
        updateCallback(componentState, null, null, null, allAggQueries, null, cIndexParam);
      } else {
        const searchQueries = getSubSearchQueries(componentMap, tmpIndex);
        searchBuilderCallback(boolType, searchQueries, null, cIndexParam);
        updateCallback(componentState, null, null, null, null, null, cIndexParam);
      }

      if (newState !== null && newState !== undefined) {
        dispatch(updateComponent(
          id,
          newState,
          isActive,
          query,
          commonAggQueries,
          privateAggQueries,
          summary,
        ));
      }
    };

    const generateGetPrivateAggs = (id) => (aggs) => {
      const tmpIndex = { ...componentIndex };
      if (aggs !== null && aggs !== undefined) {
        tmpIndex[id].privateAggQueries = aggs;
      }

      executePrivateAggQueries(tmpIndex);
    };

    const generateGetAggResults = (id) => (label) => {
      const key = `${id}_${label}`;
      // eslint-disable-next-line no-prototype-builtins
      if (aggregations.hasOwnProperty(key)) return aggregations[key];

      return null;
    };

    const generateRemoveComponentCallback = (id) => () => {
      const newComponentMap = [...componentState.componentMap.filter((v) => v !== id)];
      const newState = { ...componentState, componentMap: newComponentMap };
      const queries = getSubSearchQueries(newComponentMap, componentIndex);
      const searchQuery = getSearchQuery(boolType, queries);

      searchBuilderCallback(boolType, queries);
      updateCallback(newState, newComponentMap.length > 0, searchQuery);
      dispatch(removeComponent(id));
    };

    const generateAddComponentCallback = (index) => (type) => {
      const id = uuidv4();
      const newComponentMap = [...componentState.componentMap];
      newComponentMap.splice(index, 0, id);
      const newState = { ...componentState, componentMap: newComponentMap };
      dispatch(addComponent(id, type));
      updateCallback(newState);
    };

    const generateSearchBuilderCallback = (id) => (boolTypeIn, searchQueries, commonAggQueries) => {
      if (searchQueries.length > 0) {
        dispatch(updateComponent(
          id,
          null,
          true,
          getSearchQuery(boolTypeIn, searchQueries),
          commonAggQueries,
        ));
      } else dispatch(updateComponent(id, null, false));
    };

    const generateToggleEditedCallback = (id) => () => dispatch(toggleEdited(id));

    // eslint-disable-next-line arrow-body-style
    const generateBoolTypeCallback = (id) => {
      return (newBoolType) => {
        dispatch(updateBoolType(id, newBoolType));
        setTimeout(() => generateUpdateCallback(id)(
          null,
          null,
          { ...componentIndex[id].query, bool_type: newBoolType },
          null,
          null,
          null,
        ), 50);
      };
    };

    return (
      <div className="search-builder">
        <Grid container spacing={1}>
          {componentMap.map((id, i) => (
            <Grid item xs={12} key={`search-adder-wrapper-${id}`}>
              <Grid container>
                <Grid item xs={12}>
                  <SearchBuilderAdder
                    inMiddle // {i < componentMap.length - 1}
                    addComponentCallback={generateAddComponentCallback(i)}
                  />
                </Grid>
                <Grid item key={`search-component-wrapper-${id}`} xs={12}>
                  <SearchComponent
                    key={`search-component-${id}`}
                    componentId={id}
                    title={componentIndex[id].title}
                    summary={componentIndex[id].summary}
                    isEdited={componentIndex[id].isEdited}
                    isActive={componentIndex[id].isActive}
                    type={componentIndex[id].componentType}
                    boolType={componentIndex[id].boolType}
                    privateAggQueries={componentIndex[id].privateAggQueries}
                    commonAggQueries={componentIndex[id].commonAggQueries}
                    componentState={componentIndex[id].componentState}
                    componentMap={componentMap}
                    componentIndex={componentIndex}
                    getPrivateAggs={generateGetPrivateAggs(id)}
                    executePrivateAggQueries={executePrivateAggQueries}
                    getAggResults={generateGetAggResults(id)}
                    updateCallback={generateUpdateCallback(id)}
                    boolTypeCallback={generateBoolTypeCallback(id)}
                    toggleEditedCallback={generateToggleEditedCallback(id)}
                    searchBuilderCallback={generateSearchBuilderCallback(id)}
                    removeComponentCallback={generateRemoveComponentCallback(id)}
                  />
                </Grid>
              </Grid>
            </Grid>
          ))}
          <Grid item key={`search-adder-wrapper-${componentMap.length}`} xs={12}>
            <SearchBuilderAdder
              inMiddle={componentMap.length > 0} // {i < componentMap.length - 1}
              addComponentCallback={generateAddComponentCallback(componentMap.length)}
            />
          </Grid>
        </Grid>
      </div>
    );
  }
}

SearchBuilder.propTypes = {
  boolType: PropTypes.string.isRequired,
  dispatch: PropTypes.func.isRequired,
  updateCallback: PropTypes.func.isRequired,
  executePrivateAggQueries: PropTypes.func.isRequired,
  searchBuilderCallback: PropTypes.func.isRequired,
  componentIndex: PropTypes.objectOf(PropTypes.shape({
    title: PropTypes.string.isRequired,
    summary: PropTypes.string.isRequired,
    isEdited: PropTypes.bool.isRequired,
    isActive: PropTypes.bool.isRequired,
    componentType: PropTypes.string.isRequired,
    boolType: PropTypes.string.isRequired,
    query: PropTypes.shape({}).isRequired,
    privateAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
    commonAggQueries: PropTypes.objectOf(PropTypes.shape({})).isRequired,
    componentState: PropTypes.shape({}).isRequired,
  })).isRequired,
  componentState: PropTypes.shape({
    componentMap: PropTypes.arrayOf(PropTypes.string).isRequired,
  }).isRequired,
  aggregations: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.shape({}))).isRequired,
};

function mapStateToProps(state) {
  return {
    aggregations: state.corpex.searchResults.aggResults,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(SearchBuilder));
