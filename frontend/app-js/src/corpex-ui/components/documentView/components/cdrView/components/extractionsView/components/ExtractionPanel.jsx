import React, { useRef, useState } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import StickyHeader from '../../../../../../../../common/components/layout/StickyHeader';
import ExtractionViewer from './ExtractionViewer';
import Header from '../../../../../../../../common/components/Header';

const styles = () => ({
  scrollable: {
    height: '100%',
    overflowY: 'auto',
  },
});

function ExtractionPanel(props) {
  const extractionHeader = (
    <Header small title="Extractions" />
  );

  const windowRef = useRef(null);

  const [[width, height], setDimensions] = useState([200, 500]);

  if (windowRef && windowRef.current) {
    const newWidth = windowRef.current.clientWidth;
    const newHeight = windowRef.current.clientHeight;
    if (newWidth !== width || newHeight !== height) {
      setDimensions([newWidth, newHeight]);
    }
  }

  const {
    addTagRefExtr,
    tagRefs,
    docId,
    classes,
  } = props;

  return (
    <StickyHeader fixedBody header={extractionHeader}>
      <div
        className={`extraction-panel ${classes.scrollable}`}
        ref={windowRef}
      >
        <ExtractionViewer
          windowDimensions={{
            height,
            width,
          }}
          docId={docId}
          addTagRefExtr={addTagRefExtr}
          tagRefs={tagRefs}
        />
      </div>
    </StickyHeader>
  );
}

ExtractionPanel.propTypes = {
  addTagRefExtr: PropTypes.func.isRequired,
  tagRefs: PropTypes.shape({}).isRequired,
  docId: PropTypes.string.isRequired,
  classes: PropTypes.shape({
    scrollable: PropTypes.string,
  }).isRequired,
};

export default withStyles(styles)(ExtractionPanel);
