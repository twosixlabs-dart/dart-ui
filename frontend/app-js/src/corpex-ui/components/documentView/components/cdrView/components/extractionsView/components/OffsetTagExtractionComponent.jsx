import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import ExtractionConsole from './ExtractionConsole';
import { getTagIdFromLabel } from '../../../utilities/apiDataHelpers';
import settings from '../../../../../../../config/settings';

function OffsetTagExtractionComponent(props) {
  const {
    annotationLabel,
    extrType,
    tags,
    cdr,
    docId,
    windowDimensions,
  } = props;

  if (!cdr || !cdr.annotations) return <div />;
  if (!tags || Object.keys(tags).length === 0) return <div />;

  const tagId = getTagIdFromLabel(annotationLabel, tags);
  const supportedExtractions = {};
  settings.EXTRACTION_TYPES[tagId].forEach((extr) => {
    supportedExtractions[extr] = true;
  });
  const { annotations } = cdr;

  if (!(tagId in tags) || !tags[tagId].tagTypes || Object.keys(tags[tagId].tagTypes).length === 0) {
    return <div />;
  }

  const tagTypes = {};
  annotations
    .filter((anno) => anno.label === annotationLabel)
    .flatMap((anno) => anno.content).forEach(
      (tagObj) => {
        if (!supportedExtractions[tagObj.tag]) return;
        const tagItem = { offsets: [tagObj.offset_start, tagObj.offset_end] };
        const { description, label } = tags[tagId].tagTypes[tagObj.tag];
        if (Object.prototype.hasOwnProperty.call(tagTypes, label)) {
          tagTypes[label].tags.push(tagItem);
        } else {
          tagTypes[label] = {
            tagType: label,
            color: 'yellow',
            tags: [tagItem],
            description,
          };
        }
      },
    );

  Object.values(tagTypes).forEach((tt) => {
    // eslint-disable-next-line no-param-reassign
    tt.tags = tt.tags.sort((a, b) => (a.offsets[0] >= b.offsets[0] ? 1 : -1));
  });

  return (
    <ExtractionConsole
      extrType={extrType}
      tagTypes={tagTypes}
      docId={docId}
      windowDimensions={windowDimensions}
    />
  );
}

OffsetTagExtractionComponent.propTypes = {
  extrType: PropTypes.string.isRequired,
  cdr: PropTypes.shape({
    annotations: PropTypes.arrayOf(PropTypes.shape({})),
  }).isRequired,
  tags: PropTypes.shape({}).isRequired,
  docId: PropTypes.string.isRequired,
  annotationLabel: PropTypes.string.isRequired,
  windowDimensions: PropTypes.shape({}).isRequired,
};

const mapStateToProps = (state) => ({
  cdr: state.corpex.documentView.root.cdr,
  tags: state.corpex.corpexRoot.tags,
});

export default connect(mapStateToProps)(OffsetTagExtractionComponent);
