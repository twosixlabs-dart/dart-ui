import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import PopupMenuFrame from './commonComponents/PopupMenuFrame';
import componentTypes from '../../../componentData/corpusViewComponentTypes';
import SelectInput from './commonComponents/SelectInput';
import { aggId, getAggQuery } from '../../corpusViewerComponent/dataComponents/metadataKeywordComponent/MetadataKeywordCorpusViewerComponent';

const styles = () => ({
  root: {},
});

function KeywordComponentPopup({
  sectionId,
  addComponent,
  closePopup,
  fields,
  classes,
}) {
  const [componentData, setComponentData] = React.useState({
    type: componentTypes.METADATA_KEYWORD_COMPONENT,
    label: 'Metadata Values: Content Type',
    layout: {
      x: 0,
      y: 0,
      w: 4,
      h: 6,
      isResizable: false,
    },
    aggs: {
      [aggId]: getAggQuery('cdr.content_type'),
    },
    state: {
      field: 'cdr.content_type',
    },
  });

  const supportedFields = Object.values(fields).filter((f) => f.data_type === 'term')
    .map((v) => [v.field_id, v.label]);

  const fieldChangeHandler = (newCdrField) => {
    setComponentData({
      ...componentData,
      label: `Metadata Values: ${fields[newCdrField].label}`,
      aggs: {
        [aggId]: getAggQuery(newCdrField),
      },
      state: {
        ...componentData.state,
        field: newCdrField,
      },
    });
  };

  const {
    state: {
      field,
    },
  } = componentData;

  return (
    <PopupMenuFrame
      data={componentData}
      closePopup={closePopup}
      sectionId={sectionId}
      addComponent={addComponent}
    >
      <div className={classes.root}>
        <SelectInput
          label="Metadata Field"
          supportedFields={supportedFields}
          onChange={fieldChangeHandler}
          value={field}
        />
      </div>
    </PopupMenuFrame>
  );
}

KeywordComponentPopup.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  addComponent: PropTypes.func.isRequired,
  closePopup: PropTypes.func.isRequired,
  fields: PropTypes.objectOf(PropTypes.shape({
    field_id: PropTypes.string,
    data_type: PropTypes.string,
    cdr_label: PropTypes.string,
    label: PropTypes.string,
  })).isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  fields: state.corpex.corpexRoot.fields,
});

export default connect(mapStateToProps)(withStyles(styles)(KeywordComponentPopup));
