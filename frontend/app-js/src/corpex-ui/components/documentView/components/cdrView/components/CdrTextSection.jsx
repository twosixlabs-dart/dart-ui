import React, { useRef } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import { keys } from 'lodash';

import Typography from '@material-ui/core/Typography';

import CdrTextTagMarker from './CdrTextTagMarker';
import uuidv4 from '../../../../../../common/utilities/helpers';

const hashText = (text) => {
  const len = text.length;
  if (len === 0) return uuidv4();
  if (len < 10) return text;
  return text.substring(0, 3) + text[Math.floor(len / 2)] + text.substring(len - 4, len - 1);
};

export const makeSpan = (text) => (
  <Typography component="span" variant="body1">
    {text}
  </Typography>
);

function CdrTextSection(props) {
  const thisRef = useRef(null);

  const {
    extrType,
    text,
    index,
    chunkOffset,
    tagMarkers,
    checkedTagTypes,
  } = props;

  const tags = extrType === '' ? [] : keys(checkedTagTypes)
    .flatMap((tt) => tagMarkers[`${extrType}_${tt}`][index])
    .sort((a, b) => (a.relativeOffset <= b.relativeOffset ? -1 : 1));

  let tagStack = [];

  // check for tags that were opened in prior chunk
  const closingTags = tags.filter((tag) => !tag.opening);

  closingTags
    .forEach((tag) => {
      if (!tags.some((t) => t.opening
        && t.tagType === tag.tagType
        && t.relativeOffset === tag.startingOffset)) {
        tagStack.push({ tagType: tag.tagType, startingOffset: tag.startingOffset });
      }
    });

  let relativeOffset = 0;
  const spanList = [];
  for (let i = 0; i < tags.length; i += 1) {
    const tagOffset = tags[i].relativeOffset;
    const span = text.substring(relativeOffset, tagOffset);
    if (tagStack.length === 0) {
      spanList.push(
        <span key={`${hashText(span)}-${i}`}>
          {makeSpan(span)}
        </span>,
      );
    } else {
      const startingOffset = tags[i].opening
        ? tagStack[tagStack.length - 1].startingOffset : tags[i].startingOffset;
      spanList.push(
        <CdrTextTagMarker
          tagTypes={tagStack.map((t) => t.tagType)}
          offset={chunkOffset + startingOffset} // tagMarker needs to know absolute offset
          text={span}
          key={`${hashText(span)}-${i}`}
          parent={thisRef}
        />,
      );
    }

    if (tags[i].opening) {
      tagStack.push({ tagType: tags[i].tagType, startingOffset: tags[i].offset });
    } else {
      tagStack = tagStack.filter((ele) => ele.tagType !== tags[i].tagType);
    }

    relativeOffset += span.length;
  }

  if (relativeOffset < text.length - 1) {
    const lastSpan = text.substring(relativeOffset);
    spanList.push(
      <span key={`${hashText(lastSpan)}-last`}>
        {makeSpan(lastSpan)}
      </span>,
    );
  }

  return (
    <div
      className="cdr-text-item-section"
      id={`text-chunk-${index}`}
      ref={thisRef}
    >
      {spanList}
    </div>
  );
}

CdrTextSection.propTypes = {
  text: PropTypes.string.isRequired,
  index: PropTypes.number.isRequired,
  tagMarkers: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.shape({
    tagType: PropTypes.string,
    relativeOffset: PropTypes.number,
    opening: PropTypes.bool,
    offset: PropTypes.number,
    startingOffset: PropTypes.number,
  })))).isRequired,
  chunkOffset: PropTypes.number.isRequired,
  checkedTagTypes: PropTypes.objectOf(PropTypes.string).isRequired,
  extrType: PropTypes.string,
};

CdrTextSection.defaultProps = {
  extrType: '',
};

const mapStateToProps = (state) => ({
  tagMarkers: state.corpex.documentView.cdrView.extractions.tagMarkers,
  checkedTagTypes: state.corpex.documentView.cdrView.extractions.checkedTagTypes,
  extrType: state.corpex.documentView.cdrView.extractions.extrType,
});

export default connect(mapStateToProps)(CdrTextSection);
