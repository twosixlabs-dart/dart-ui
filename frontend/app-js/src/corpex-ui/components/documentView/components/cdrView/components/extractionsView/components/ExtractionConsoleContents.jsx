import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core';
import Grid from '@material-ui/core/Grid';

import TagComponent from './TagComponent';
import DartAccordion from '../../../../../../../../common/components/DartAccordion';
import { completeScrollTo, completeScrollToIndex, unfocusTag } from '../../../cdrView.actions';
import SelfMeasuringWindowedListDirty from '../../../../../../../../common/components/SelfMeasuringWindowedListDirty';
import WithDimensionsDirty from '../../../../../../../../common/components/WithDimensionsDirty';

import { connect } from '../../../../../../../../dart-ui/context/CustomConnect';

const MAX_HEIGHT = 400;

const styles = () => ({
  tagListWindow: {
    width: '100%',
    maxHeight: MAX_HEIGHT,
  },
});

class ExtractionConsoleContents extends Component {
  constructor(props) {
    super(props);

    const {
      tags,
    } = this.props;

    // State will simply contain min height
    this.state = {
      accordionIsOpen: false,
    };

    this.sortedTags = tags
      .sort((a, b) => (a.offsets[0] < b.offsets[0] ? -1 : 1));

    this.windowRef = React.createRef();

    this.measureRenderer = this.measureRenderer.bind(this);
    this.tagRenderer = this.tagRenderer.bind(this);
    this.accordionHandler = this.accordionHandler.bind(this);
    this.scrollToIndexCallback = this.scrollToIndexCallback.bind(this);
    this.scrollToOffsetCallback = this.scrollToOffsetCallback.bind(this);
    // this.resizeHandler = this.resizeHandler.bind(this);
  }

  componentDidMount() {
    window.addEventListener('resize', this.resizeHandler);
  }

  componentDidUpdate() {
    const {
      tagType,
      tagFocus,
    } = this.props;

    const { accordionIsOpen } = this.state;

    if (!accordionIsOpen && tagFocus.tagType === tagType) {
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState((oldState) => ({
        ...oldState,
        accordionIsOpen: true,
      }));
    }
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.resizeHandler);
  }

  accordionHandler(e, isOpen) {
    const { tagType, tagFocus, dispatch } = this.props;

    this.setState((oldState) => ({
      ...oldState,
      accordionIsOpen: isOpen,
    }));

    if (!isOpen && tagType === tagFocus.tagType) {
      dispatch(unfocusTag(tagType, tagFocus.offset));
    }
  }

  // resizeHandler() {
  //   const { windowWidth } = this.state;
  //
  //   if (this.windowRef && this.windowRef.current) {
  //     const newWindowWidth = this.windowRef.current.clientWidth;
  //     if (windowWidth !== newWindowWidth) {
  //       this.setState((oldState) => ({
  //         ...oldState,
  //         windowWidth: newWindowWidth,
  //       }));
  //     }
  //   }
  // }

  scrollToIndexCallback() {
    const { props: { dispatch } } = this;
    dispatch(completeScrollToIndex());
  }

  scrollToOffsetCallback() {
    const { props: { dispatch } } = this;
    dispatch(completeScrollTo());
  }

  measureRenderer(
    {
      index, // Index of row within collection
      updateRow,
      // parent,
    },
  ) {
    const {
      tagType,
      extrType,
    } = this.props;

    const tagObj = this.sortedTags[index];

    return (
      <TagComponent
        extrType={extrType}
        tagType={tagType}
        element={tagObj.tagElement}
        measureElement={tagObj.measureElement}
        offsets={tagObj.offsets}
        measureOnly
        updateRow={updateRow}
      />
    );
  }

  tagRenderer(
    {
      index, // Index of row within collection
      updateRow,
      // parent,
    },
  ) {
    const {
      tagType,
      extrType,
    } = this.props;

    const tagObj = this.sortedTags[index];

    return (
      <TagComponent
        extrType={extrType}
        tagType={tagType}
        element={tagObj.tagElement}
        offsets={tagObj.offsets}
        updateRow={updateRow}
      />
    );
  }

  render() {
    const {
      tags,
      extrType,
      tagType,
      typeElement,
      isExpanded,
      scrollTo,
      tagIndex,
      docId,
      classes,
    } = this.props;

    const {
      accordionIsOpen,
    } = this.state;

    const isScrolledTo = scrollTo.tagType === tagType
      && scrollTo.window === 'extr';

    let scrollToIndex;
    let scrollOffset;
    const tagIndexId = `${extrType}_${scrollTo.tagType}_${scrollTo.offset}`;
    if (tagIndexId in tagIndex
      && tagIndex[tagIndexId].extr !== null) {
      if (scrollTo.scrollToView === null) {
        scrollToIndex = tagIndex[tagIndexId].extr;
      } else {
        scrollOffset = scrollTo.scrollToView;
      }
    }

    const tagList = (
      <WithDimensionsDirty className={classes.tagListWindow}>
        {({ outerWidth }) => (
          <SelfMeasuringWindowedListDirty
            key={docId}
            windowHeight={MAX_HEIGHT}
            windowWidth={outerWidth}
            rowCount={tags.length}
            rowForMeasure={this.measureRenderer}
            rowForWindow={this.tagRenderer}
            overscanCount={15}
            scrollToIndex={isScrolledTo ? scrollToIndex : undefined}
            scrollToIndexCallback={this.scrollToIndexCallback}
            scrollOffset={scrollOffset}
            scrollOffsetCallback={this.scrollToOffsetCallback}
          />
        )}
      </WithDimensionsDirty>
    );

    const contents = !typeElement || typeElement === '' ? tagList : (
      <Grid container direction="row" spacing={2}>
        <Grid item xs={12}>
          <DartAccordion
            id={`extraction-type-${tagType}-tags`}
            title={`Extracted ${tagType}`}
            expanded={accordionIsOpen}
            onChange={this.accordionHandler}
          >
            <Grid container direction="row" spacing={1}>
              <Grid item xs={12}>
                {typeElement}
              </Grid>
              <Grid item xs={12}>
                {tagList}
              </Grid>
            </Grid>
          </DartAccordion>
        </Grid>
      </Grid>
    );

    // eslint-disable-next-line no-undef
    return !isExpanded && !accordionIsOpen ? '' : (
      <div style={{ width: '100%' }} ref={this.windowRef}>
        {contents}
      </div>
    );
  }
}

ExtractionConsoleContents.propTypes = {
  tags: PropTypes.arrayOf(PropTypes.shape({
    offsets: PropTypes.arrayOf(PropTypes.number),
    tagElement: PropTypes.node,
  })).isRequired,
  extrType: PropTypes.string.isRequired,
  tagType: PropTypes.string.isRequired,
  typeElement: PropTypes.node,
  isExpanded: PropTypes.bool.isRequired,
  windowDimensions: PropTypes.shape({
    height: PropTypes.number,
    width: PropTypes.number,
  }).isRequired,
  scrollTo: PropTypes.shape({
    textScroll: PropTypes.number,
    window: PropTypes.string,
    offset: PropTypes.number,
    tagType: PropTypes.string,
    scrollToView: PropTypes.number,
  }).isRequired,
  tagIndex: PropTypes.objectOf(PropTypes.shape({
    extr: PropTypes.number,
    text: PropTypes.number,
  })).isRequired,
  tagFocus: PropTypes.shape({
    offset: PropTypes.number,
    tagType: PropTypes.string,
  }).isRequired,
  docId: PropTypes.string.isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    tagListWindow: PropTypes.string,
  }).isRequired,
};

ExtractionConsoleContents.defaultProps = {
  typeElement: '',
};

const mapStateToProps = (state) => ({
  tagIndex: state.corpex.documentView.cdrView.extractions.tagIndex,
  scrollTo: state.corpex.documentView.cdrView.root.scrollTo,
  tagFocus: state.corpex.documentView.cdrView.root.tagFocus,
});

export default connect(mapStateToProps)(withStyles(styles)(ExtractionConsoleContents));
