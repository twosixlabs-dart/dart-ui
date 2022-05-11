// eslint-disable-next-line max-classes-per-file
import React, { Component, Suspense } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import { Paper } from '@material-ui/core';

// eslint-disable-next-line import/no-extraneous-dependencies
// import { DndWrappedList } from 'dart-ui-scala13-components';

import corpusViewPropTypes from '../../corpusView.propTypes';
import CorpusViewerComponentHeader from './CorpusViewerComponentHeader';
import corpusViewComponentTypes from '../../componentData/corpusViewComponentTypes';
import TagValuesCorpusViewerComponent from './dataComponents/tagValuesComponent/TagValuesCorpusViewerComponent';
import MetadataKeywordCorpusViewerComponent from './dataComponents/metadataKeywordComponent/MetadataKeywordCorpusViewerComponent';
import {
  addCorpusViewComponent,
  removeCorpusViewComponent,
  updateComponent,
  updateComponentState,
} from '../../corpusView.actions';
import FacetValuesCorpusViewerComponent from './dataComponents/facetValuesComponent/FacetValuesCorpusViewerComponent';
import NumberComponent from './dataComponents/numberComponent/NumberComponent';
import WithDimensionsDirty from '../../../../../common/components/WithDimensionsDirty';
import uuidv4 from '../../../../../common/utilities/helpers';
import TagTypesCorpusViewerComponent from './dataComponents/tagTypesComponent/TagTypesCorpusViewerComponent';

// eslint-disable-next-line arrow-body-style
const WrappedList = React.lazy(() => {
  return import('dart-ui-scala13-components')
    .then((module) => ({ default: module.DndWrappedList }));
});

const styles = () => ({
  componentGridEle: {
    margin: 5,
  },
  componentPaper: {
    padding: 15,
  },
  sectionEle: {
    margin: 5,
  },
});

class CorpusViewerSectionComponent extends Component {
  constructor(props) {
    super(props);
    this.updateGen = this.updateGen.bind(this);
    this.addComponent = this.addComponent.bind(this);
    this.removeComponent = this.removeComponent.bind(this);
  }

  componentDidMount() {
    const {
      currentData: {
        state: {
          componentMap,
        },
      },
      componentIndex,
      updateData,
    } = this.props;

    const allAggs = {};

    componentMap.forEach((cId) => {
      Object.keys(componentIndex[cId].aggs)
        .forEach((aggKey) => {
          allAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
        });
    });
    updateData({
      aggs: allAggs,
    });
  }

  addComponent(componentId) {
    const updateData = this.updateGen(componentId);
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
        currentData: {
          state,
          aggs,
        },
        updateData,
        dispatch,
      },
    } = this;

    return () => {
      dispatch(removeCorpusViewComponent(componentId));

      const newState = { ...state };
      newState.componentMap = newState.componentMap.filter((cId) => !cId.startsWith(componentId));
      const newAggs = {};
      Object.keys(aggs).forEach((aggId) => {
        if (!aggId.startsWith(componentId)) newAggs[aggId] = aggs[aggId];
      });
      updateData({ aggs: newAggs, state: newState });
    };
  }

  updateGen(componentId) {
    return (newData) => {
      const {
        props: {
          updateData,
          currentData: {
            state: {
              componentMap,
            },
          },
          componentIndex,
          dispatch,
        },
      } = this;

      dispatch(updateComponent(componentId, newData));

      if ('aggs' in newData) {
        const newAggs = {};
        componentMap.forEach((cId) => {
          if (cId !== componentId) {
            Object.keys(componentIndex[cId].aggs).forEach((aggKey) => {
              newAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
            });
          }
        });

        Object.keys(newData.aggs).forEach((aggKey) => {
          newAggs[`${componentId}-${aggKey}`] = newData.aggs[aggKey];
        });

        updateData({ aggs: newAggs });
      }
    };
  }

  render() {
    const {
      // eslint-disable-next-line react/prop-types
      context,
      id,
      currentData,
      componentIndex,
      aggResults,
      count,
      dispatch,
      classes,
      loader,
    } = this.props;

    const { props } = this;

    const parentOuterWidth = props.outerWidth;

    const {
      state: {
        componentMap,
      },
    } = currentData;

    return (
      <div style={{ width: parentOuterWidth }}>
        <WithDimensionsDirty
          className="corpus-overview-dashboard"
        >
          {({ outerWidth }) => (
            <Suspense fallback={loader}>
              <WrappedList
                listId={id}
                listClass={id}
                context={context}
                list={componentMap}
                maxWidth={outerWidth}
                renderer={({ key, dragHandleProps }) => {
                  const cId = key;
                  if (!componentMap.includes(cId)) return <div />;

                  const aggresultsKeys = Object.keys(aggResults)
                    .filter((k) => k.startsWith(cId));
                  const subAggResults = {};
                  aggresultsKeys.forEach((k) => { subAggResults[k.replace(`${cId}-`, '')] = aggResults[k]; });
                  return (
                    <div
                      className={classes.sectionEle}
                      key={cId}
                    >
                      <Paper classes={{ root: classes.componentPaper }}>
                        <CorpusViewerComponent
                          componentIndex={componentIndex}
                          dragHandleProps={dragHandleProps}
                          addComponent={this.addComponent(cId)}
                          removeComponent={this.removeComponent(cId)}
                          aggResults={subAggResults}
                          updateData={this.updateGen(cId)}
                          context={context}
                          updateState={(newState) => dispatch(updateComponentState(cId, newState))}
                          outerWidth={outerWidth - 40}
                          id={cId}
                          count={count}
                          key={`corpus-viewer-section-component-${cId}`}
                        />
                      </Paper>
                    </div>
                  );
                }}
              />
            </Suspense>
          )}
        </WithDimensionsDirty>
      </div>
    );
  }
}

const sectionMapStateToProps = (state, dartContext) => ({
  componentIndex: state.corpex.corpusView.componentIndex,
  loader: dartContext.loader,
});

// eslint-disable-next-line max-len
const SectionComponent = connect(sectionMapStateToProps)(withStyles(styles)(CorpusViewerSectionComponent));

CorpusViewerSectionComponent.propTypes = {
  id: PropTypes.string.isRequired,
  updateData: PropTypes.func.isRequired,
  aggs: PropTypes.shape({}).isRequired,
  currentData: corpusViewPropTypes.componentData.isRequired,
  componentIndex: corpusViewPropTypes.componentIndex.isRequired,
  dispatch: PropTypes.func.isRequired,
  aggResults: PropTypes.shape({}).isRequired,
  count: PropTypes.number.isRequired,
  outerWidth: PropTypes.number.isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
  loader: PropTypes.node.isRequired,
};

class CorpusViewerComponent extends Component {
  render() {
    const {
      id,
      addComponent,
      removeComponent,
      componentIndex,
      aggResults,
      updateData,
      updateState,
      outerWidth,
      count,
      dragHandleProps,
      // eslint-disable-next-line react/prop-types
      context,
    } = this.props;

    const componentData = componentIndex[id];
    const {
      type,
      label,
    } = componentData;

    const childProps = {
      id,
      aggResults,
      updateData,
      updateState,
      count,
      outerWidth,
      currentData: componentData,
      context,
    };

    let component;

    switch (type) {
      case corpusViewComponentTypes.TAG_VALUES_COMPONENT: {
        component = (
          <TagValuesCorpusViewerComponent
            {...childProps}
          />
        );
        break;
      }

      case corpusViewComponentTypes.TAG_TYPES_COMPONENT: {
        component = (
          <TagTypesCorpusViewerComponent
            {...childProps}
          />
        );
        break;
      }

      case corpusViewComponentTypes.METADATA_KEYWORD_COMPONENT: {
        component = (
          <MetadataKeywordCorpusViewerComponent
            {...childProps}
          />
        );
        break;
      }

      case corpusViewComponentTypes.FACET_CONFIDENCE_AVG_COMPONENT:
      case corpusViewComponentTypes.FACET_CONFIDENCE_FILTER_COMPONENT:
      case corpusViewComponentTypes.FACET_COMPONENT: {
        component = (
          <FacetValuesCorpusViewerComponent
            {...childProps}
          />
        );
        break;
      }

      case corpusViewComponentTypes.METADATA_DATE_COMPONENT: {
        component = (
          <NumberComponent
            {...childProps}
          />
        );
        break;
      }

      case corpusViewComponentTypes.METADATA_NUMBER_COMPONENT: {
        component = (
          <NumberComponent
            {...childProps}
          />
        );
        break;
      }

      case corpusViewComponentTypes.SECTION_COMPONENT: {
        component = (
          <SectionComponent
            {...childProps}
          />
        );
        break;
      }

      default: {
        component = <div {...childProps} />;
      }
    }

    return (
      <div className="corpus-overview-component">
        <CorpusViewerComponentHeader
          label={label}
          addComponent={addComponent}
          removeComponent={removeComponent}
          componentType={type}
          dragHandleProps={dragHandleProps}
        />
        {component}
      </div>
    );
  }
}

CorpusViewerComponent.propTypes = {
  id: PropTypes.string.isRequired,
  addComponent: PropTypes.oneOf([undefined, PropTypes.func]).isRequired,
  removeComponent: PropTypes.func.isRequired,
  componentIndex: corpusViewPropTypes.componentIndex.isRequired,
  aggResults: corpusViewPropTypes.aggResults.isRequired,
  updateData: PropTypes.func.isRequired,
  updateState: PropTypes.func.isRequired,
  outerWidth: PropTypes.number.isRequired,
  count: PropTypes.number.isRequired,
  dragHandleProps: PropTypes.shape({}),
};

CorpusViewerComponent.defaultProps = {
  dragHandleProps: {},
};

const mapStateToProps = (state) => ({
  count: state.corpex.searchResults.count,
});

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewerComponent));
