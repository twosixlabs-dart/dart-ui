import React, { Component, Suspense } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import { Paper } from '@material-ui/core';

// eslint-disable-next-line import/no-extraneous-dependencies
// import { DndWrappedList, DndWrappedListContext } from 'dart-ui-scala13-components';
// import ReactGridLayout from 'react-grid-layout/lib/ReactGridLayout';

import corpusViewPropTypes from '../../corpusView.propTypes';
import CorpusViewerComponent from '../corpusViewerComponent/CorpusViewerComponent';
import {
  addCorpusViewComponent,
  removeCorpusViewComponent,
  updateComponent,
  updateComponentMap,
  updateComponentState,
  updateCorpusOverviewAggs,
} from '../../corpusView.actions';
import executeSearch from '../../../searchBuilder/thunk/executeSearch.thunk';
import WithDimensionsDirty from '../../../../../common/components/WithDimensionsDirty';
import uuidv4 from '../../../../../common/utilities/helpers';
import { connect } from '../../../../../dart-ui/context/CustomConnect';

const WrappedList = React.lazy(() => {
  import('dart-ui-scala13-components')
    .then((module) => {
      const { DndWrappedList } = module;
      return DndWrappedList;
    });
});

const WrappedListContext = React.lazy(() => {
  import('dart-ui-scala13-components')
    .then((module) => {
      const { DndWrappedListContext } = module;
      return DndWrappedListContext;
    });
});

const styles = () => ({
  componentEle: {
    margin: 5,
  },
  componentPaper: {
    padding: 15,
    width: '100%',
    height: '100%',
  },
});

class CorpusViewerDashboard extends Component {
  constructor(props) {
    super(props);
    this.addComponent = this.addComponent.bind(this);
    this.removeComponent = this.removeComponent.bind(this);
    this.updateAggs = this.updateAggs.bind(this);
    this.updateDataGen = this.updateDataGen.bind(this);
    this.updateStateGen = this.updateStateGen.bind(this);
  }

  componentDidMount() {
    const {
      searchQueries,
      componentMap,
      componentIndex,
      dispatch,
      xhrHandler,
    } = this.props;

    const allAggs = {};
    componentMap
      .forEach((cId) => {
        Object.keys(componentIndex[cId].aggs)
          .forEach((aggKey) => {
            allAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
          });
      });
    dispatch(updateCorpusOverviewAggs(allAggs));
    dispatch(executeSearch(xhrHandler, searchQueries, allAggs));
  }

  updateStateGen(id) {
    const {
      dispatch,
    } = this.props;

    return (newState) => {
      dispatch(updateComponentState(id, newState));
    };
  }

  updateAggs(id, newAggs) {
    const {
      componentMap,
      componentIndex,
      searchQueries,
      dispatch,
      xhrHandler,
    } = this.props;

    const allAggs = {};
    componentMap
      .filter((cId) => cId !== id)
      .forEach((cId) => {
        Object.keys(componentIndex[cId].aggs)
          .forEach((aggKey) => {
            allAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
          });
      });
    Object.keys(newAggs)
      .forEach((aggKey) => {
        allAggs[`${id}-${aggKey}`] = newAggs[aggKey];
      });
    dispatch(updateCorpusOverviewAggs(allAggs));
    dispatch(executeSearch(xhrHandler, searchQueries, allAggs));
  }

  updateDataGen(id) {
    const {
      dispatch,
    } = this.props;

    return (newData) => {
      dispatch(updateComponent(id, newData));
      if ('aggs' in newData) this.updateAggs(id, newData.aggs);
    };
  }

  addComponent(componentId) {
    const updateData = this.updateDataGen(componentId);
    return (data) => {
      const {
        componentIndex,
        dispatch,
      } = this.props;

      const newComponentId = uuidv4();

      dispatch(addCorpusViewComponent(data, newComponentId, componentId));
      const newAggs = {};
      componentIndex[componentId].state.componentMap.forEach((cId) => {
        Object.keys(componentIndex[cId].aggs).forEach((aggKey) => {
          newAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
        });
      });

      Object.keys(data.aggs).forEach((aggKey) => {
        newAggs[`${newComponentId}-${aggKey}`] = data.aggs[aggKey];
      });

      updateData({ aggs: newAggs });
    };
  }

  removeComponent(componentId) {
    const {
      props: {
        searchQueries,
        componentMap,
        componentIndex,
        dispatch,
        xhrHandler,
      },
    } = this;

    return () => {
      dispatch(removeCorpusViewComponent(componentId));

      const allAggs = {};
      componentMap
        .filter((cId) => cId !== componentId)
        .forEach((cId) => {
          Object.keys(componentIndex[cId].aggs)
            .forEach((aggKey) => {
              allAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
            });
        });
      dispatch(updateCorpusOverviewAggs(allAggs));
      dispatch(executeSearch(xhrHandler, searchQueries, allAggs));
    };
  }

  render() {
    const {
      dispatch,
      componentMap,
      componentIndex,
      searchResults,
      classes,
    } = this.props;

    const aggregations = 'aggregations' in searchResults ? searchResults.aggregations : {};

    return (
      <WithDimensionsDirty
        style={{ width: '100%' }}
        className="corpus-overview-dashboard"
      >
        {({ outerWidth }) => (
          <Suspense fallback={<div>Loading</div>}>
            <WrappedListContext
              onRearrange={(map) => {
                Object.keys(map).forEach((key) => {
                  if (key === 'root') dispatch(updateComponentMap(map[key]));
                  else {
                    dispatch(updateComponentState(key, {
                      ...componentIndex[key].state,
                      componentMap: map[key],
                    }));
                  }
                });
              }}
            >
              {(context) => (
                <Suspense fallback={<div>Loading</div>}>
                  <WrappedList
                    listId="root"
                    listClass="root"
                    context={context}
                    list={componentMap}
                    maxWidth={outerWidth}
                    renderer={({ key, dragHandleProps }) => {
                      const componentId = key;
                      if (!componentMap.includes(componentId)) return <div />;
                      const aggresultsKeys = Object.keys(aggregations)
                        .filter((k) => k.startsWith(componentId));
                      const aggResults = {};
                      aggresultsKeys.forEach((k) => {
                        aggResults[k.replace(`${componentId}-`, '')] = aggregations[k];
                      });
                      return (
                        <div
                          className={classes.componentEle}
                          key={componentId}
                          data-grid={componentIndex[componentId].layout}
                        >
                          <Paper classes={{ root: classes.componentPaper }}>
                            <CorpusViewerComponent
                              aggResults={aggResults}
                              context={context}
                              componentIndex={componentIndex}
                              addComponent={this.addComponent(componentId)}
                              removeComponent={this.removeComponent(componentId)}
                              updateData={this.updateDataGen(componentId)}
                              updateState={this.updateStateGen(componentId)}
                              outerWidth={outerWidth - 40}
                              id={componentId}
                              key={`corpus-viewer-component-${componentId}`}
                              dragHandleProps={dragHandleProps}
                            />
                          </Paper>
                        </div>
                      );
                    }}
                  />
                </Suspense>
              )}
            </WrappedListContext>
          </Suspense>
        )}
      </WithDimensionsDirty>
    );
  }
}

CorpusViewerDashboard.propTypes = {
  componentMap: corpusViewPropTypes.componentMap.isRequired,
  componentIndex: corpusViewPropTypes.componentIndex.isRequired,
  allAggQueries: PropTypes.shape({}).isRequired,
  searchResults: corpusViewPropTypes.searchResults.isRequired,
  searchQueries: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

function mapStateToProps(state, dartContext) {
  return {
    componentMap: state.corpex.corpusView.componentMap,
    componentIndex: state.corpex.corpusView.componentIndex,
    searchResults: state.corpex.searchResults.searchResults,
    searchQueries: state.corpex.searchResults.searchQueries,
    allAggQueries: state.corpex.corpusView.aggQueries,
    xhrHandler: dartContext.xhrHandler,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewerDashboard));
