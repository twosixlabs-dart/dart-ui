import React, { Component } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import CorpusViewerBuilderMenu from './CorpusViewerBuilderMenu';
import { addCorpusViewComponent, updateCorpusOverviewAggs } from '../../corpusView.actions';
import executeSearch from '../../../searchBuilder/thunk/executeSearch.thunk';
import propTypes from '../../corpusView.propTypes';
import uuidv4 from '../../../../../common/utilities/helpers';
import { connect } from '../../../../../dart-ui/context/CustomConnect';

const styles = () => ({
});

class CorpusViewerBuilder extends Component {
  render() {
    const {
      props: {
        sectionId,
        componentMap,
        componentIndex,
        searchQueries,
        dispatch,
        xhrHandler,
      },
    } = this;

    const addComponent = (data) => {
      const id = uuidv4();
      dispatch(addCorpusViewComponent(data, id));
      const allAggs = {};
      componentMap
        .forEach((cId) => {
          Object.keys(componentIndex[cId].aggs)
            .forEach((aggKey) => {
              allAggs[`${cId}-${aggKey}`] = componentIndex[cId].aggs[aggKey];
            });
        });
      Object.keys(data.aggs)
        .forEach((aggKey) => {
          allAggs[`${id}-${aggKey}`] = data.aggs[aggKey];
        });
      dispatch(updateCorpusOverviewAggs(allAggs));
      dispatch(executeSearch(xhrHandler, searchQueries, allAggs));
    };

    return (
      <CorpusViewerBuilderMenu
        sectionId={sectionId}
        addComponent={addComponent}
      />
    );
  }
}

CorpusViewerBuilder.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  componentMap: propTypes.componentMap.isRequired,
  componentIndex: propTypes.componentIndex.isRequired,
  searchQueries: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  dispatch: PropTypes.func.isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

function mapStateToProps(state, dartContext) {
  return {
    componentMap: state.corpex.corpusView.componentMap,
    componentIndex: state.corpex.corpusView.componentIndex,
    searchQueries: state.corpex.searchResults.searchQueries,
    xhrHandler: dartContext.xhrHandler,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CorpusViewerBuilder));
