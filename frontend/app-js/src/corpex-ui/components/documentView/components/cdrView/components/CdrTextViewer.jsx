import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core';
import Paper from '@material-ui/core/Paper';

// eslint-disable-next-line import/no-unresolved,import/no-webpack-loader-syntax,import/extensions
import Worker from 'worker-loader!../workers/textProcessor.worker.js';

import CdrTextItem from './CdrTextItem';
import { completeScrollTo, completeScrollToIndex, setCdrTextArray } from '../cdrView.actions';
import SelfMeasuringWindowedListDirty from '../../../../../../common/components/SelfMeasuringWindowedListDirty';
import WithDimensionsDirty from '../../../../../../common/components/WithDimensionsDirty';
import { JsDartContextProvider, ReduxProvider } from '../../../../../../dart-ui/context/contextProvider';

import { connect } from '../../../../../../dart-ui/context/CustomConnect';

const styles = () => ({
  paper: {
    height: '100%',
    width: '100%',
    overflowY: 'hidden',
    padding: 0,
  },
  fullSize: {
    height: '100%',
    overflowY: 'hidden',
    width: '100%',
  },
});

const rowRendererGen = (textArray) => ({
  // key, // Unique key within array of rows
  // eslint-disable-next-line react/prop-types
  index, // Index of row within collection
  // isScrolling, // The List is currently being scrolled
  // style, // Style object to be applied to row (to position it)
  // parent // reference to List
}) => (
  <CdrTextItem
    // key={key}
    chunk={textArray[index]}
    index={index}
    // style={style}
  />
);

const rowMeasureRendererGen = (textArray) => ({
  // eslint-disable-next-line react/prop-types
  index, // Index of row within collection
  // isScrolling, // The List is currently being scrolled
  // style, // Style object to be applied to row (to position it)
  // parent // reference to List
}) => (
  <CdrTextItem
    // key={key}
    chunk={textArray[index]}
    index={index}
    // style={style}
    textOnly
  />
);

class CdrTextViewer extends Component {
  constructor(props) {
    super(props);
    this.scrollOffsetCallback = this.scrollOffsetCallback.bind(this);
    this.scrollToIndexCallback = this.scrollToIndexCallback.bind(this);
    this.contextInjector = this.contextInjector.bind(this);
  }

  // eslint-disable-next-line class-methods-use-this
  scrollOffsetCallback() {
    const { props: { dispatch } } = this;
    dispatch(completeScrollTo());
  }

  scrollToIndexCallback() {
    const { props: { dispatch } } = this;
    dispatch(completeScrollToIndex());
  }

  contextInjector(eles) {
    const { props: { dartContext } } = this;
    return (
      <ReduxProvider skipInit report={() => {}}>
        <JsDartContextProvider dartContext={dartContext}>
          {eles}
        </JsDartContextProvider>
      </ReduxProvider>
    );
  }

  render() {
    const {
      extrType,
      textArray,
      scrollTo: {
        // textScroll,
        window,
        scrollToView,
        offset,
        tagType,
      },
      tagIndex,
      cdr,
      docId,
      classes,
      dispatch,
    } = this.props;

    if (!!cdr && (!textArray || textArray.length < 1)) {
      const textProcessor = new Worker();
      textProcessor.onmessage = (evt) => {
        dispatch(setCdrTextArray(evt.data));
        textProcessor.terminate();
      };
      textProcessor.postMessage(cdr);
    }

    if (!textArray
      || textArray.length < 1
      || !cdr
      || docId !== cdr.document_id) return <div />;

    let scrollToIndex;
    let scrollOffset;
    if (extrType && window === 'text') {
      const tagIndexId = `${extrType}_${tagType}_${offset}`;
      if (scrollToView === null) {
        scrollToIndex = tagIndex[tagIndexId]
          ? tagIndex[tagIndexId].text || undefined : undefined;
      } else {
        scrollOffset = scrollToView;
      }
    }

    return (
      <Paper className={`cdr-text-viewer ${classes.paper}`}>
        <WithDimensionsDirty className={classes.fullSize}>
          {({ outerHeight, outerWidth }) => (
            <SelfMeasuringWindowedListDirty
              measureContextInjector={this.contextInjector}
              rowCount={textArray.length}
              rowForWindow={rowRendererGen(textArray)}
              rowForMeasure={rowMeasureRendererGen(textArray)}
              windowHeight={outerHeight}
              windowWidth={outerWidth}
              scrollToIndex={scrollToIndex}
              scrollToIndexCallback={this.scrollToIndexCallback}
              scrollOffset={scrollOffset}
              scrollOffsetCallback={this.scrollOffsetCallback}
              overscanCount={15}
              key={docId}
            />
          )}
        </WithDimensionsDirty>
      </Paper>
    );
  }
}

CdrTextViewer.propTypes = {
  dartContext: PropTypes.shape({}).isRequired,
  extrType: PropTypes.string,
  textArray: PropTypes.arrayOf(PropTypes.shape({
    paragraph: PropTypes.bool,
    text: PropTypes.string,
    offset: PropTypes.number,
    length: PropTypes.number,
  })).isRequired,
  // textScroll: PropTypes.number.isRequired,
  scrollTo: PropTypes.shape({
    textScroll: PropTypes.number,
    window: PropTypes.string,
    scrollToView: PropTypes.number,
    offset: PropTypes.number,
    tagType: PropTypes.string,
  }).isRequired,
  tagIndex: PropTypes.objectOf(PropTypes.shape({
    extr: PropTypes.number,
    text: PropTypes.number,
  })).isRequired,
  docId: PropTypes.string.isRequired,
  classes: PropTypes.shape({
    paper: PropTypes.string.isRequired,
    fullSize: PropTypes.string.isRequired,
  }).isRequired,
  cdr: PropTypes.shape({
    document_id: PropTypes.string,
  }).isRequired,
  dispatch: PropTypes.func.isRequired,
};

CdrTextViewer.defaultProps = {
  extrType: '',
};

function mapStateToProps(state, dartContext) {
  return {
    dartContext,
    textArray: state.corpex.documentView.cdrView.root.text,
    scrollTo: state.corpex.documentView.cdrView.root.scrollTo,
    tagIndex: state.corpex.documentView.cdrView.extractions.tagIndex,
    extrType: state.corpex.documentView.cdrView.extractions.extrType,
    cdr: state.corpex.documentView.root.cdr,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(CdrTextViewer));
