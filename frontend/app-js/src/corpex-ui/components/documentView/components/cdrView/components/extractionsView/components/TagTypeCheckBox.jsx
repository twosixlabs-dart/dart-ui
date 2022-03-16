import React from 'react';
import PropTypes from 'prop-types';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import { connect } from 'react-redux';
import { highlightExtractionTags, removeExtractionTagsHighlight } from '../extractionsView.actions';
import getColor from '../utilities/getColor';

function TagTypeCheckBox(props) {
  const {
    tagType,
    checkedTagTypes,
    dispatch,
  } = props;

  const isChecked = tagType in checkedTagTypes;

  const checkHandler = (e) => {
    e.stopPropagation();
    const boxIsChecked = e.target.checked;
    if (boxIsChecked) {
      dispatch(highlightExtractionTags(tagType));
    } else dispatch(removeExtractionTagsHighlight(tagType));
  };

  return (
    <FormControlLabel
      control={(
        <Checkbox
          className="extraction-tag-type-check-box"
          checked={isChecked}
          style={{
            color: isChecked ? getColor([tagType], checkedTagTypes, true, true) : undefined,
          }}
          onClick={checkHandler}
          name={`extraction-tag-type-checkbox-${tagType}`}
        />
      )}
      label=""
    />
  );
}

TagTypeCheckBox.propTypes = {
  tagType: PropTypes.string.isRequired,
  checkedTagTypes: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.number)).isRequired,
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = (state) => ({
  checkedTagTypes: state.corpex.documentView.cdrView.extractions.checkedTagTypes,
});

export default connect(mapStateToProps)(TagTypeCheckBox);
