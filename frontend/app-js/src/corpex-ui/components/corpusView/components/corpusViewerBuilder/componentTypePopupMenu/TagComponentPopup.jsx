import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import componentTypes from '../../../componentData/corpusViewComponentTypes';
import PopupMenuFrame from './commonComponents/PopupMenuFrame';
import { aggId as valuesAggId, getAggQuery as valuesAggQuery } from '../../corpusViewerComponent/dataComponents/tagValuesComponent/TagValuesCorpusViewerComponent';
import { aggId as typesAggId, getAggQuery as typesAggQuery } from '../../corpusViewerComponent/dataComponents/tagTypesComponent/TagTypesCorpusViewerComponent';
import SelectInput from './commonComponents/SelectInput';

const styles = () => ({
  root: {},
});

function TagComponentPopup({
  sectionId,
  addComponent,
  closePopup,
  tagId,
  defaultTagType,
  tags,
  labelLabel,
  valueLabel,
  classes,
}) {
  const getLabel = (newType, newTagType) => {
    if (newType === componentTypes.TAG_TYPES_COMPONENT) {
      return `Tag Types: ${tags[tagId].label}`;
    }

    return (
      <div>
        <div>{`Tag Values: ${tags[tagId].label}`}</div>
        <div>{tags[tagId].tagTypes[newTagType].label}</div>
      </div>
    );
  };

  const [componentData, setComponentData] = React.useState({
    type: componentTypes.TAG_VALUES_COMPONENT,
    label: getLabel(componentTypes.TAG_VALUES_COMPONENT, defaultTagType),
    layout: {
      x: 0,
      y: 0,
      w: 4,
      h: 6,
      isResizable: false,
    },
    aggs: {
      [valuesAggId]: valuesAggQuery(tagId, defaultTagType),
    },
    state: {
      tagId,
      tagType: defaultTagType,
      labelLabel,
      valueLabel,
    },
  });

  const {
    type,
    state: {
      tagType,
    },
  } = componentData;

  const getAggQuery = (newType, newTagId, newTagType) => {
    if (newType === componentTypes.TAG_TYPES_COMPONENT) {
      return typesAggQuery(newTagId);
    }

    return valuesAggQuery(newTagId, newTagType);
  };

  const typeChangeHandler = (newType) => {
    setComponentData({
      ...componentData,
      type: newType,
      label: getLabel(newType, tagType),
      aggs: {
        [newType === componentTypes.TAG_TYPES_COMPONENT ? typesAggId : valuesAggId]:
          getAggQuery(newType, tagId),
      },
      state: {
        ...componentData.state,
        labelLabel: newType === componentTypes.TAG_TYPES_COMPONENT ? `${labelLabel} Types` : labelLabel,
      },
    });
  };

  const fieldChangeHandler = (newTagType) => {
    setComponentData({
      ...componentData,
      label: getLabel(type, newTagType),
      aggs: {
        [valuesAggId]: getAggQuery(type, tagId, newTagType),
      },
      state: {
        ...componentData.state,
        tagType: newTagType,
      },
    });
  };

  const supportedTagTypes = Object.values(tags[tagId].tagTypes).map((v) => [v.tag_type, v.label]);

  return (
    <PopupMenuFrame
      data={componentData}
      closePopup={closePopup}
      sectionId={sectionId}
      addComponent={addComponent}
    >
      <div className={classes.root}>
        <SelectInput
          label="Aggregation Type"
          supportedFields={[[componentTypes.TAG_VALUES_COMPONENT, 'Tag Values'], [componentTypes.TAG_TYPES_COMPONENT, 'Tag Types']]}
          onChange={typeChangeHandler}
          value={type}
        />
        {type === componentTypes.TAG_TYPES_COMPONENT ? '' : (
          <SelectInput
            label="Tag Type"
            supportedFields={supportedTagTypes}
            onChange={fieldChangeHandler}
            value={tagType}
          />
        )}
      </div>
    </PopupMenuFrame>
  );
}

TagComponentPopup.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
  closePopup: PropTypes.func.isRequired,
  tagId: PropTypes.string.isRequired,
  defaultTagType: PropTypes.string.isRequired,
  labelLabel: PropTypes.string.isRequired,
  valueLabel: PropTypes.oneOf([PropTypes.string, undefined]).isRequired,
  tags: PropTypes.objectOf(PropTypes.shape({
    label: PropTypes.string,
    tagTypes: PropTypes.shape({}),
  })).isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  tags: state.corpex.corpexRoot.tags,
});

export default connect(mapStateToProps)(withStyles(styles)(TagComponentPopup));
