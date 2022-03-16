import React, { useEffect } from 'react';
import PropTypes from 'prop-types';

import { toPairs } from 'lodash';

import { makeStyles } from '@material-ui/core/styles';

import DartAccordion from '../../../../../../../../common/components/DartAccordion';
import TagTypeCheckBox from './TagTypeCheckBox';
import {
  expandTagType,
  registerTagType,
  unExpandTagType,
} from '../extractionsView.actions';
import { unfocusTag } from '../../../cdrView.actions';
import ExtractionConsoleContents from './ExtractionConsoleContents';
import DartTooltip from '../../../../../../../../common/components/DartTooltip';

import { connect } from '../../../../../../../../dart-ui/context/CustomConnect';

const useStyles = makeStyles({
  root: {
    width: '100%',
  },
});

function ExtractionConsole(props) {
  const {
    extrType,
    tagTypes,
    tagMarkers,
    expandedTagTypes,
    windowDimensions,
    tagFocus,
    textArray,
    cdr,
    docId,
    dispatch,
  } = props;

  const tagTypePairs = toPairs(tagTypes);

  // Warning: registerTagType is expensive, since it parses all of the tags
  // into markers mapped to text chunks. Consider doing this only
  // when checked.
  useEffect(() => {
    if (textArray && textArray.length > 0 && cdr && docId === cdr.document_id) {
      tagTypePairs.forEach(([tagType, tagTypeObj]) => {
        const markerId = `${extrType}_${tagType}`;
        if (!(markerId in tagMarkers)) {
          dispatch(
            registerTagType(
              extrType,
              tagType,
              tagTypeObj.tags.map((tag) => tag.offsets),
              textArray,
            ),
          );
        }
      });
    }
  });

  const classes = useStyles();

  const tagTypeAccordionHandler = (tagType) => (e, isExpanded) => {
    e.stopPropagation();
    if (isExpanded) {
      dispatch(expandTagType(tagType));
    } else {
      dispatch(unExpandTagType(tagType));
      if (tagFocus.tagType === tagType) {
        dispatch(unfocusTag(tagType, tagFocus.offset));
      }
    }
  };

  const extractions = toPairs(tagTypes)
    .sort((a, b) => (a[1].tags.length >= b[1].tags.length ? -1 : 1))
    .map(([tagType, tagTypeObj]) => (
      <DartAccordion
        id={`extraction-panel-${tagType}`}
        title={(
          <div>
            <TagTypeCheckBox tagType={tagType} />
            {tagTypeObj.description ? (
              <DartTooltip body={tagTypeObj.description}>
                <span>{tagType}</span>
              </DartTooltip>
            ) : tagType}
          </div>
        )}
        key={`extraction-panel-${tagType}`}
        expanded={expandedTagTypes.includes(tagType) || tagFocus.tagType === tagType}
        onChange={tagTypeAccordionHandler(tagType)}
        timeout={0}
      >
        <ExtractionConsoleContents
          extrType={extrType}
          tagType={tagType}
          tags={tagTypeObj.tags}
          docId={docId}
          isExpanded={expandedTagTypes.includes(tagType)}
          typeElement={tagTypeObj.typeElement}
          windowDimensions={windowDimensions}
        />
      </DartAccordion>
    ));

  return (
    <div className={`extraction-console ${classes.root}`}>
      {extractions}
    </div>
  );
}

ExtractionConsole.propTypes = {
  extrType: PropTypes.string,
  tagTypes: PropTypes.objectOf(PropTypes.shape({
    tags: PropTypes.arrayOf(PropTypes.shape({
      offsets: PropTypes.arrayOf(PropTypes.number),
      tagElement: PropTypes.node,
    })).isRequired,
    typeElement: PropTypes.node,
  })).isRequired,
  tagMarkers: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.shape({}))))
    .isRequired,
  tagFocus: PropTypes.shape({
    offset: PropTypes.number,
    tagType: PropTypes.string,
  }).isRequired,
  textArray: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  expandedTagTypes: PropTypes.arrayOf(PropTypes.string).isRequired,
  windowDimensions: PropTypes.shape({}).isRequired,
  docId: PropTypes.string.isRequired,
  cdr: PropTypes.shape({
    document_id: PropTypes.string,
  }).isRequired,
  dispatch: PropTypes.func.isRequired,
};

ExtractionConsole.defaultProps = {
  extrType: '',
};

const mapStateToProps = (state) => ({
  expandedTagTypes: state.corpex.documentView.cdrView.extractions.expandedTagTypes,
  tagMarkers: state.corpex.documentView.cdrView.extractions.tagMarkers,
  tagFocus: state.corpex.documentView.cdrView.root.tagFocus,
  textArray: state.corpex.documentView.cdrView.root.text,
  cdr: state.corpex.documentView.root.cdr,
});

export default connect(mapStateToProps)(ExtractionConsole);
