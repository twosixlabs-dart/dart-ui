import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import componentTypes from '../../../componentData/corpusViewComponentTypes';
import PopupMenuFrame from './commonComponents/PopupMenuFrame';
import TextQuery from '../../../../../../common/components/TextQuery';

const styles = () => ({
  root: {},
});

function SectionComponentPopup({
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
      w: 12,
      h: 3,
      isResizable: false,
    },
    aggs: {},
    state: {
      componentMap: [],
    },
  });

  const updateLabelHandler = (newLabel) => setComponentData({
    ...componentData,
    type: componentTypes.SECTION_COMPONENT,
    label: newLabel,
    aggs: {},
    state: {
      componentMap: [],
    },
  });

  const {
    label,
  } = componentData;

  return (
    <PopupMenuFrame
      data={componentData}
      closePopup={closePopup}
      sectionId={sectionId}
      addComponent={addComponent}
    >
      <div className={classes.root}>
        <TextQuery
          textValue={label}
          onType={updateLabelHandler}
        />
      </div>
    </PopupMenuFrame>
  );
}

SectionComponentPopup.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
  closePopup: PropTypes.func.isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

export default withStyles(styles)(SectionComponentPopup);
