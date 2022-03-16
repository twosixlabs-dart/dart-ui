import React, { useEffect, useRef } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import { makeStyles } from '@material-ui/core/styles';
import OutsideClickHandler from 'react-outside-click-handler/esm/OutsideClickHandler';

import getSnippet from '../utilities/getSnippet';
import {
  focusTag,
  hoverTag,
  setScrollToView,
  unfocusTag,
  unhoverTag,
} from '../../../cdrView.actions';
// import { fade } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  normal: {
    padding: 5,
    border: 'none',
  },
  hover: {
    padding: 3,
    borderColor: theme.palette.primary.light,
    borderWidth: 2,
    borderStyle: 'solid',
    cursor: 'pointer',
  },
  focus: {
    padding: 2,
    borderColor: theme.palette.primary.main,
    borderWidth: 3,
    borderStyle: 'solid',
  },
}));

function TagComponent(props) {
  const thisRef = useRef(null);

  const {
    // extrType,
    checkedTagTypes,
    tagType,
    text,
    offsets,
    measureElement,
    element,
    tagFocus,
    tagHover,
    style,
    scrollTo,
    updateRow,
    measureOnly,
    // scrollToRow,
    dispatch,
  } = props;

  const tagRef = useRef(null);

  useEffect(() => {
    const isScrolledTo = scrollTo.tagType === tagType
      && scrollTo.window === 'extr'
      && scrollTo.offset === offsets[0]
      && scrollTo.scrolledToIndex === true;

    if (isScrolledTo) {
      if (thisRef && thisRef.current) {
        const offset = thisRef.current.clientHeight / 2;
        dispatch(setScrollToView(offset));
      }
    }
  });

  const classes = useStyles();

  const isFocus = tagFocus.tagType === tagType && tagFocus.offset === offsets[0];
  const isHover = tagHover.tagType === tagType && tagHover.offset === offsets[0];

  let tagElement = measureElement !== '' ? measureElement : getSnippet(text, offsets);
  if (!measureOnly
    && element !== ''
    && element !== null
    && element !== undefined) tagElement = element(updateRow);

  const enterHandler = () => {
    if (!isHover && !isFocus) dispatch(hoverTag('extr', tagType, offsets[0]));
  };

  const leaveHandler = () => {
    if (isHover && !isFocus) dispatch(unhoverTag('extr'));
  };

  const clickHandler = (e) => {
    e.stopPropagation();
    e.preventDefault();
    if (!isFocus) dispatch(focusTag('extr', tagType, offsets[0], checkedTagTypes));
  };

  const outsideClickHandler = () => {
    if (isFocus) dispatch(unfocusTag(tagType, offsets[0]));
  };

  let className = isHover ? classes.hover : classes.normal;
  if (isFocus) className = classes.focus;

  return (
    // eslint-disable-next-line max-len
    // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
    <div
      style={style}
      ref={tagRef}
      id={`tag-component-${tagType}-${offsets[0]}`}
      className="extraction-tag-component"
      onMouseEnter={enterHandler}
      onMouseLeave={leaveHandler}
      onClick={isFocus ? () => {} : clickHandler}
      disabled={!isFocus}
    >
      <OutsideClickHandler
        display="block"
        onOutsideClick={outsideClickHandler}
      >
        <div
          className={className}
          ref={thisRef}
        >
          {tagElement}
        </div>
      </OutsideClickHandler>
    </div>
  );
}

TagComponent.propTypes = {
  text: PropTypes.string.isRequired,
  // extrType: PropTypes.string.isRequired,
  tagType: PropTypes.string.isRequired,
  offsets: PropTypes.arrayOf(PropTypes.number).isRequired,
  element: PropTypes.node,
  measureElement: PropTypes.node,
  dispatch: PropTypes.func.isRequired,
  tagFocus: PropTypes.shape({
    tagType: PropTypes.string,
    offset: PropTypes.number,
  }).isRequired,
  tagHover: PropTypes.shape({
    tagType: PropTypes.string,
    offset: PropTypes.number,
  }).isRequired,
  scrollTo: PropTypes.shape({
    textScroll: PropTypes.number,
    window: PropTypes.string,
    to: PropTypes.bool,
    offset: PropTypes.number,
    tagType: PropTypes.string,
    scrollToView: PropTypes.number,
    scrolledToIndex: PropTypes.bool,
    returnTo: PropTypes.number,
  }).isRequired,
  measureOnly: PropTypes.bool,
  updateRow: PropTypes.func.isRequired,
  checkedTagTypes: PropTypes.arrayOf(PropTypes.string).isRequired,
  // scrollToRow: PropTypes.func.isRequired,
  style: PropTypes.shape({}).isRequired,
};

TagComponent.defaultProps = {
  element: '',
  measureElement: '',
  measureOnly: false,
};

const mapStateToProps = (state) => ({
  text: state.corpex.documentView.root.cdr.extracted_text,
  tagFocus: state.corpex.documentView.cdrView.root.tagFocus,
  tagHover: state.corpex.documentView.cdrView.root.tagHover,
  scrollTo: state.corpex.documentView.cdrView.root.scrollTo,
  checkedTagTypes: state.corpex.documentView.cdrView.extractions.checkedTagTypes,
});

export default connect(mapStateToProps)(TagComponent);
