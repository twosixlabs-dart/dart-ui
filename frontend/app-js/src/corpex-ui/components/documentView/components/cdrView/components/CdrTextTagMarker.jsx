import React, { useEffect, useRef } from 'react';
import PropTypes from 'prop-types';

import OutsideClickHandler from 'react-outside-click-handler/esm/OutsideClickHandler';

import { withStyles } from '@material-ui/core';
import Typography from '@material-ui/core/Typography';

import {
  focusTag,
  hoverTag,
  setScrollToView,
  unfocusTag,
  unhoverTag,
} from '../cdrView.actions';
import getColor from './extractionsView/utilities/getColor';

import { connect } from '../../../../../../dart-ui/context/CustomConnect';

const styles = () => ({
  tagMarker: {
    '&:hover': {
      cursor: 'pointer',
    },
  },
});

function getSpanTitle(tagTypes) {
  return tagTypes.join(', ');
}

function CdrTextTagMarker(props) {
  const {
    tagTypes,
    // extrTypes,
    checkedTagTypes,
    tagFocus,
    tagHover,
    scrollTo,
    offset,
    text,
    parent,
    dispatch,
    classes,
  } = props;

  const markerRef = useRef(null);

  const isFocus = tagTypes.includes(tagFocus.tagType)
    && offset === tagFocus.offset;
  const isHover = tagTypes.includes(tagHover.tagType)
    && offset === tagHover.offset;

  const isScrolledTo = scrollTo.window === 'text'
    && scrollTo.offset === offset
    && scrollTo.scrolledToIndex === true
    && tagTypes.includes(scrollTo.tagType);

  const tagType = tagTypes[tagTypes.length - 1];

  useEffect(() => {
    if (isScrolledTo) {
      // setTimeout(scrollFn, 50);
      // setTimeout(scrollFn, 150);
      if (markerRef && markerRef.current && parent && parent.current) {
        const thisPos = markerRef.current.getBoundingClientRect();
        const parentPos = parent.current.getBoundingClientRect();
        const verticalOffset = thisPos.top - parentPos.top;
        dispatch(setScrollToView(verticalOffset));
      }
    }
  });

  const hoverHandler = () => {
    if (!isHover) dispatch(hoverTag('text', tagType, offset));
  };

  const unhoverHandler = () => {
    if (isHover) dispatch(unhoverTag('text'));
  };

  const clickHandler = () => {
    if (!isFocus) dispatch(focusTag('text', tagType, offset, checkedTagTypes));
  };

  const clickOffHandler = (e) => {
    if (isFocus) {
      e.stopPropagation();
      dispatch(unfocusTag(tagType, offset));
    }
  };

  let outlineWidth = isHover ? 1 : 0;
  if (isFocus) outlineWidth = 2;
  const outlineStyle = 'solid';

  return (
    <OutsideClickHandler
      display="inline"
      onOutsideClick={clickOffHandler}
    >
      {/* eslint-disable-next-line max-len */}
      {/* eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions */}
      <span
        ref={markerRef}
        style={{
          backgroundColor: getColor(tagTypes, checkedTagTypes, isFocus, isHover),
          outlineStyle,
          outlineWidth,
        }}
        onMouseEnter={hoverHandler}
        onMouseLeave={unhoverHandler}
        onClick={clickHandler}
        title={getSpanTitle(tagTypes)}
        id={`tag-marker-${tagTypes.join('-')}-${offset}`}
        className={classes.tagMarker}
      >
        <Typography component="span" variant="body1">{text}</Typography>
      </span>
    </OutsideClickHandler>
  );
}

CdrTextTagMarker.propTypes = {
  text: PropTypes.string.isRequired,
  tagTypes: PropTypes.arrayOf(PropTypes.string).isRequired,
  checkedTagTypes: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.number)).isRequired,
  tagFocus: PropTypes.shape({
    tagType: PropTypes.string,
    offset: PropTypes.number,
  }).isRequired,
  tagHover: PropTypes.shape({
    tagType: PropTypes.string,
    offset: PropTypes.number,
  }).isRequired,
  scrollTo: PropTypes.shape({
    window: PropTypes.string,
    to: PropTypes.bool,
    tagType: PropTypes.string,
    offset: PropTypes.number,
    scrollToView: PropTypes.number,
    scrolledToIndex: PropTypes.bool,
  }).isRequired,
  parent: PropTypes.element.isRequired,
  offset: PropTypes.number.isRequired,
  dispatch: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    tagMarker: PropTypes.string,
  }).isRequired,
};

const mapStateToProps = (state) => ({
  tagFocus: state.corpex.documentView.cdrView.root.tagFocus,
  tagHover: state.corpex.documentView.cdrView.root.tagHover,
  scrollTo: state.corpex.documentView.cdrView.root.scrollTo,
  checkedTagTypes: state.corpex.documentView.cdrView.extractions.checkedTagTypes,
});

export default connect(mapStateToProps)(withStyles(styles)(CdrTextTagMarker));
