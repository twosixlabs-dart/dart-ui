import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import componentTypes from '../../../componentData/corpusViewComponentTypes';
import PopupMenuFrame from './commonComponents/PopupMenuFrame';

const styles = () => ({
  root: {},
});

function SentimentStanceComponentPopup({
  sectionId,
  addComponent,
  closePopup,
  classes,
}) {
  const [componentData, setComponentData] = React.useState({
    type: componentTypes.SECTION_COMPONENT,
    label: '',
    layout: {
      x: 0,
      y: 0,
      w: 4,
      h: 6,
      isResizable: false,
    },
    aggs: {},
    state: {},
  });
  if (false) setComponentData();

  return (
    <PopupMenuFrame
      data={componentData}
      closePopup={closePopup}
      sectionId={sectionId}
      addComponent={addComponent}
    >
      <div className={classes.root} />
    </PopupMenuFrame>
  );
}

SentimentStanceComponentPopup.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
  closePopup: PropTypes.func.isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

export default withStyles(styles)(SentimentStanceComponentPopup);
