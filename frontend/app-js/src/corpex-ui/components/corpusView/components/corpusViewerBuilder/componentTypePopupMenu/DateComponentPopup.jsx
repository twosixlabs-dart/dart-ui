import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import PopupMenuFrame from './commonComponents/PopupMenuFrame';
import componentTypes from '../../../componentData/corpusViewComponentTypes';
import SelectInput from './commonComponents/SelectInput';
import { aggId, getAggQuery } from '../../corpusViewerComponent/dataComponents/numberComponent/NumberComponent';

const styles = () => ({
  root: {},
});

function DateComponentPopup({
  sectionId,
  addComponent,
  closePopup,
  fields,
  classes,
}) {
  const [componentData, setComponentData] = React.useState({
    type: componentTypes.METADATA_DATE_COMPONENT,
    label: 'Publication Date',
    layout: {
      x: 0,
      y: 0,
      w: 12,
      h: 3,
      isResizable: false,
    },
    aggs: {
      [aggId]: getAggQuery('cdr.extracted_metadata.CreationDate', '1M'),
    },
    state: {
      field: 'cdr.extracted_metadata.CreationDate',
      defaultBucketSize: '1M',
      bucketSize: '1M',
      kind: 'date',
    },
  });

  const supportedFields = Object.values(fields).filter((f) => f.data_type === 'date')
    .map((v) => [v.field_id, v.label]);

  const fieldChangeHandler = (newCdrField) => {
    setComponentData({
      ...componentData,
      label: fields[newCdrField].label,
      aggs: {
        [aggId]: getAggQuery(newCdrField, componentData.state.bucketSize),
      },
      state: {
        ...componentData.state,
        field: newCdrField,
        kind: newCdrField === 'cdr.timestamp' ? 'date-time' : 'date',
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
          label="Date Field"
          supportedFields={supportedFields}
          onChange={fieldChangeHandler}
          value={field}
        />
      </div>
    </PopupMenuFrame>
  );
}

DateComponentPopup.propTypes = {
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

export default connect(mapStateToProps)(withStyles(styles)(DateComponentPopup));
